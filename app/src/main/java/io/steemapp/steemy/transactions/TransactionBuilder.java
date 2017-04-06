package io.steemapp.steemy.transactions;

import android.util.Pair;

import com.squareup.otto.Bus;

import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.core.VarInt;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import io.steemapp.steemy.SteemyGlobals;
import io.steemapp.steemy.events.BroadcastCommentEvent;
import io.steemapp.steemy.events.BroadcastVoteEvent;
import io.steemapp.steemy.events.NotLoggedInEvent;
import io.steemapp.steemy.events.SigningFailureEvent;
import io.steemapp.steemy.models.AccountManager;
import io.steemapp.steemy.models.Discussion;
import io.steemapp.steemy.security.SteemECKey;

import static io.steemapp.steemy.SteemyGlobals.byteArrayToHexString;
import static io.steemapp.steemy.SteemyGlobals.hexStringToByteArray;

/**
 * Created by John on 7/27/2016.
 */
public class TransactionBuilder {

    protected Bus mEventBus;
    protected AccountManager mAccountManager;
    protected static TransactionBuilder instance;

    public static TransactionBuilder getInstance(Bus eventBus, AccountManager manager) {
        if (instance == null)
            instance = new TransactionBuilder(eventBus, manager);

        return instance;
    }

    private TransactionBuilder(Bus eventBus, AccountManager manager) {
        mEventBus = eventBus;
        mAccountManager = manager;
    }

    public void comment(Discussion discussion) {
        if (mAccountManager.isLoggedIn()) {
            int ref_block_num = getCurrentRefBlock();
            long ref_block_prefix = getCurrentRefBlockPrefix();
            Pair<Long, String> expiration = getNewExpiration();
            byte[] serializedComment = generateSerializedCommentMessage(ref_block_num, ref_block_prefix, expiration.first, discussion);
            byte[] signature = signTransaction(serializedComment);

            JSONObject ops = new JSONObject();
            JSONObject tx = new JSONObject();
            try {
                ops.put("author", discussion.getAuthor());
                ops.put("permlink", discussion.getPermlink());
                ops.put("parent_author", discussion.getParentAuthor());
                ops.put("parent_permlink", discussion.getParentPermlink());
                ops.put("title", discussion.getTitle());
                ops.put("body", discussion.getBody());
                ops.put("category", discussion.getCategory());
                ops.put("json_metadata", discussion.getJsonMetadata());
                tx.put("ref_block_num", ref_block_num);
                tx.put("ref_block_prefix", ref_block_prefix);
                tx.put("expiration", expiration.second);
                tx.put("extensions", new JSONArray());
                JSONArray commentOp = new JSONArray();
                commentOp.put(SteemyGlobals.Tx.comment.toString());
                commentOp.put(ops);
                JSONArray opsArray = new JSONArray();
                opsArray.put(0, commentOp);
                tx.put("operations", opsArray);
                JSONArray sigArray = new JSONArray();
                sigArray.put(byteArrayToHexString(signature));
                tx.put("signatures", sigArray);

                broadcastComment(tx);
            } catch (JSONException e) {
                mEventBus.post(new SigningFailureEvent("Unable to generate transaction JSON object."));
            }
        } else {
            mEventBus.post(new NotLoggedInEvent());
        }
    }

    public void vote(String author, String permlink, short weight) {
        if (mAccountManager.isLoggedIn()) {
            int ref_block_num = getCurrentRefBlock();
            long ref_block_prefix = getCurrentRefBlockPrefix();
            Pair<Long, String> expiration = getNewExpiration();
            byte[] serializedVote = generateSerializedVoteMessage(ref_block_num, ref_block_prefix, expiration.first, mAccountManager.getAccountName(), author, permlink, weight);
            byte[] signature = signTransaction(serializedVote);

            JSONObject ops = new JSONObject();
            JSONObject tx = new JSONObject();
            try {
                ops.put("author", author);
                ops.put("permlink", permlink);
                ops.put("voter", mAccountManager.getAccountName());
                ops.put("weight", weight);
                tx.put("ref_block_num", ref_block_num);
                tx.put("ref_block_prefix", ref_block_prefix);
                tx.put("expiration", expiration.second);
                tx.put("extensions", new JSONArray());
                JSONArray voteOp = new JSONArray();
                voteOp.put(SteemyGlobals.Tx.vote.toString());
                voteOp.put(ops);
                JSONArray opsArray = new JSONArray();
                opsArray.put(0, voteOp);
                tx.put("operations", opsArray);
                JSONArray sigArray = new JSONArray();
                sigArray.put(byteArrayToHexString(signature));
                tx.put("signatures", sigArray);

                broadcastVote(tx);
            } catch (JSONException e) {
                mEventBus.post(new SigningFailureEvent("Unable to generate transaction JSON object."));
            }
        } else {
            mEventBus.post(new NotLoggedInEvent());
        }
    }

    public void follow(String follower, String following, String[] what) {

    }

    private byte[] signTransaction(byte[] message) {

        Pair<String, byte[]> postingKey = mAccountManager.getPostingKey();
        byte[] privateKey = postingKey.second;

        SteemECKey ecKey = new SteemECKey(privateKey, ECKey.publicKeyFromPrivate(new BigInteger(1, privateKey), true));

        ECKey.ECDSASignature signature = ecKey.sign(Sha256Hash.of(message));
        if (signature != null) {
            int id = -1;
            ECKey recKey;
            while (id == -1) {
                for (int i = 0; i < 4; i++) {
                    recKey = ECKey.recoverFromSignature(i, signature, Sha256Hash.of(message), true);
                    if (recKey != null) {
                        if (ecKey.getPublicKeyAsHex().equalsIgnoreCase(recKey.getPublicKeyAsHex())) {
                            id = i;
                            break;
                        }
                    }
                }
            }

            id += 4;
            id += 27;

            ByteBuffer finalSig = ByteBuffer.allocate(signature.encodeToDER().length + 1);
            finalSig.put((byte) id);
            finalSig.put(signature.r.toByteArray());
            finalSig.put(signature.s.toByteArray());

            return finalSig.array();
        }
        mEventBus.post(new SigningFailureEvent("Failed to sign transaction."));
        return null;
    }

    private void broadcastVote(JSONObject vote) {
        mEventBus.post(new BroadcastVoteEvent(vote));
    }

    private void broadcastComment(JSONObject comment) {
        mEventBus.post(new BroadcastCommentEvent(comment));
    }

    private byte[] generateSerializedVoteMessage(int ref_block_num,
                                                 long ref_block_prefix,
                                                 long expiration,
                                                 String voter,
                                                 String author,
                                                 String permlink,
                                                 short vote) {
        String chainid = "0000000000000000000000000000000000000000000000000000000000000000";
        byte[] chainBytes = hexStringToByteArray(chainid);
        int bufferSize = chainBytes.length + 2 + 4 + 4 + 2 + 1 + voter.length() + 1 + author.length() + 1 + permlink.length() + 2 + 1;
        ByteBuffer message = ByteBuffer.allocate(bufferSize);

        message.order(ByteOrder.LITTLE_ENDIAN);
        message.put(chainBytes);
        message.putShort((short) ref_block_num);

        message.putInt((int) ref_block_prefix);

        message.putInt((int) expiration);
        VarInt opLength = new VarInt(1);
        VarInt opType = new VarInt(SteemyGlobals.Tx.vote.getValue());
        message.put(opLength.encode());
        message.put(opType.encode());
        VarInt propLength = new VarInt(voter.length());
        message.put(propLength.encode());
        message.put(voter.getBytes());
        propLength = new VarInt(author.length());
        message.put(propLength.encode());
        message.put(author.getBytes());
        propLength = new VarInt(permlink.length());
        message.put(propLength.encode());
        message.put(permlink.getBytes());
        message.putShort(vote);
        byte extensions = 0;
        message.put(extensions);

        return message.array();
    }

    private byte[] generateSerializedCommentMessage(int ref_block_num,
                                                    long ref_block_prefix,
                                                    long expiration,
                                                    Discussion discussion) {

        String chainid = "0000000000000000000000000000000000000000000000000000000000000000";
        byte[] chainBytes = hexStringToByteArray(chainid);

        VarInt parentAuthorLength = new VarInt(discussion.getParentAuthor().length());
        VarInt parentPermlinkLength = new VarInt(discussion.getParentPermlink().length());
        VarInt authorLength = new VarInt(discussion.getAuthor().length());
        VarInt permlinkLength = new VarInt(discussion.getPermlink().length());
        VarInt titleLength = new VarInt(discussion.getTitle().length());
        VarInt bodyLength = new VarInt(discussion.getBody().length());
        VarInt jsonLength = new VarInt(discussion.getJsonMetadata().length());

        int bufferSize = chainBytes.length
                + 2 + 4 + 4 + 2
                + parentAuthorLength.encode().length + discussion.getParentAuthor().length()
                + parentPermlinkLength.encode().length + discussion.getParentPermlink().length()
                + authorLength.encode().length + discussion.getAuthor().length()
                + permlinkLength.encode().length + discussion.getPermlink().length()
                + titleLength.encode().length + discussion.getTitle().length()
                + bodyLength.encode().length + discussion.getBody().length()
                + jsonLength.encode().length + discussion.getJsonMetadata().length() + 1;

        ByteBuffer message = ByteBuffer.allocate(bufferSize);

        message.order(ByteOrder.LITTLE_ENDIAN);

        message.put(chainBytes);
        message.putShort((short) ref_block_num);
        message.putInt((int) ref_block_prefix);
        message.putInt((int) expiration);

        VarInt opLength = new VarInt(1);
        VarInt opType = new VarInt(SteemyGlobals.Tx.comment.getValue());

        message.put(opLength.encode());
        message.put(opType.encode());

        message.put(parentAuthorLength.encode());
        message.put(discussion.getParentAuthor().getBytes());

        message.put(parentPermlinkLength.encode());
        message.put(discussion.getParentPermlink().getBytes());

        message.put(authorLength.encode());
        message.put(discussion.getAuthor().getBytes());

        message.put(permlinkLength.encode());
        message.put(discussion.getPermlink().getBytes());

        message.put(titleLength.encode());
        message.put(discussion.getTitle().getBytes());

        message.put(bodyLength.encode());
        message.put(discussion.getBody().getBytes());

        message.put(jsonLength.encode());
        message.put(discussion.getJsonMetadata().getBytes());

        byte extensions = 0;
        message.put(extensions);

        return message.array();
    }

    private int getCurrentRefBlock() {
        return (SteemyGlobals.BLOCKCHAIN_GLOBALS.getHeadBlockNumber() & 0xFFFF);
    }

    private long getCurrentRefBlockPrefix() {
        byte[] headBlock = hexStringToByteArray(SteemyGlobals.BLOCKCHAIN_GLOBALS.getHeadBlockId());

        byte[] headBlockBytes = Arrays.copyOfRange(headBlock, 4, 8);

        ByteBuffer ref_block_prefix_bytes = ByteBuffer.allocate(4);

        int count = 3;
        while (count > -1) {
            ref_block_prefix_bytes.put(headBlockBytes[count]);
            count--;
        }
        return (ref_block_prefix_bytes.getInt(0) & 0x00000000FFFFFFFFL);
    }

    private Pair<Long, String> getNewExpiration() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

        calendar.add(Calendar.SECOND, 30);
        Date expireDate = calendar.getTime();
        String date = sdf.format(expireDate);
        long secondsSinceEpoch = calendar.getTimeInMillis() / 1000;
        return new Pair<>(secondsSinceEpoch, date);
    }
}
