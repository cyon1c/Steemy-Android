package io.steemapp.steemy.transactions;

import android.util.Pair;

import com.squareup.otto.Bus;

import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import io.steemapp.steemy.SteemyGlobals;
import io.steemapp.steemy.events.BroadcastCommentEvent;
import io.steemapp.steemy.events.BroadcastFollowEvent;
import io.steemapp.steemy.events.BroadcastTransferEvent;
import io.steemapp.steemy.events.BroadcastVoteEvent;
import io.steemapp.steemy.models.AccountManager;
import io.steemapp.steemy.models.Discussion;

import static io.steemapp.steemy.SteemyGlobals.hexStringToByteArray;

/**
 * Created by John on 10/15/2016.
 */

public class TransactionManager {

    private AccountManager mAccountManager;
    protected Bus mEventBus;
    protected Transaction currentTransaction;
    protected static TransactionManager instance;

    private TransactionManager(AccountManager TransactionManager, Bus bus){
        mEventBus = bus;
        mAccountManager = TransactionManager;
    }

    public static TransactionManager instance(AccountManager m, Bus b){
        if(instance == null)
            instance = new TransactionManager(m, b);

        return instance;
    }

    public TransactionManager newTx(){
        currentTransaction = new Transaction();
        currentTransaction.setHeaders(
                getCurrentRefBlock(),
                getCurrentRefBlockPrefix(),
                getNewExpiration());

        return this;
    }

    public TransactionManager addVoteOp(String author, String voter, String permlink, short weight){
        if(currentTransaction.mOperation == null) {
            currentTransaction.addVoteOperation(author, voter, permlink, weight);
        }

        return this;
    }

    public TransactionManager addFollowOp(String follower, String following, String followType){
        if(currentTransaction.mOperation == null){
            currentTransaction.addFollowOperation(follower, following, followType);
        }

        return this;
    }

    public TransactionManager addCommentOp(Discussion discussion){
        if(currentTransaction.mOperation == null){
            currentTransaction.addCommentOperation(discussion);
        }

        return this;
    }

    public TransactionManager addTransferOp(String sender, String receiver, String amount, String assetID, String memo){
        if(currentTransaction.mOperation == null){
            currentTransaction.addTransferOperation(sender, receiver, amount, assetID, memo);
        }

        return this;
    }

    public TransactionManager prepareTx(){
        if(currentTransaction.mOperation instanceof TransferOperation)
            currentTransaction.serializeAndSign(mAccountManager.getActiveKey());
        else {
            Pair<String, byte[]> key = mAccountManager.getPostingKey();
            currentTransaction.serializeAndSign(key);
        }

        return this;
    }

    public void broadcastTx(){
        if(currentTransaction.mOperation instanceof TransferOperation){
            mEventBus.post(new BroadcastTransferEvent(currentTransaction));
        }
        else if(currentTransaction.mOperation instanceof CommentOperation){
            mEventBus.post(new BroadcastCommentEvent(currentTransaction));
        }
        else if(currentTransaction.mOperation instanceof VoteOperation){
            mEventBus.post(new BroadcastVoteEvent(currentTransaction));
        }
        else if(currentTransaction.mOperation instanceof FollowOperation){
            mEventBus.post(new BroadcastFollowEvent(currentTransaction));
        }
    }

    private int getCurrentRefBlock(){
        return (SteemyGlobals.BLOCKCHAIN_GLOBALS.getHeadBlockNumber() & 0xFFFF);
    }

    private long getCurrentRefBlockPrefix(){
        byte[] headBlock = hexStringToByteArray(SteemyGlobals.BLOCKCHAIN_GLOBALS.getHeadBlockId());

        byte[] headBlockBytes = Arrays.copyOfRange(headBlock, 4, 8);

        ByteBuffer ref_block_prefix_bytes = ByteBuffer.allocate(4);

        int count = 3;
        while(count > -1){
            ref_block_prefix_bytes.put(headBlockBytes[count]);
            count--;
        }
        return (ref_block_prefix_bytes.getInt(0) & 0x00000000FFFFFFFFL);
    }

    private Pair<Long, String> getNewExpiration(){
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
