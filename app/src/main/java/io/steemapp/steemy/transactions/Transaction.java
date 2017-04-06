package io.steemapp.steemy.transactions;

import android.util.Pair;

import com.squareup.otto.Bus;

import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.Sha256Hash;

import java.math.BigInteger;
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
import io.steemapp.steemy.security.SteemECKey;

import static io.steemapp.steemy.SteemyGlobals.hexStringToByteArray;

/**
 * Created by John on 10/13/2016.
 */

public class Transaction {

    protected int mRefBlockNum;
    protected long mRefBlockPrefix;
    protected Pair<Long, String> mExp;
    protected byte[] mSerializedMessage;
    protected byte[] mSignature;
    protected Operation mOperation;

    protected Transaction(){

    }

    protected void setHeaders(int refblocknum, long refblockprefix, Pair<Long, String> exp){
        mRefBlockNum = refblocknum;
        mRefBlockPrefix = refblockprefix;
        mExp = exp;
    }

    protected void addVoteOperation(String author, String voter, String permlink, short weight){
        VoteOperation vop = (VoteOperation)OperationFactory.createOperation(SteemyGlobals.Tx.vote);
        vop.mAuthor = author;
        vop.mVoter = voter;
        vop.mPermlink = permlink;
        vop.mWeight = weight;
        mOperation = vop;

    }

    protected void addCommentOperation(Discussion discussion){
        CommentOperation cop = (CommentOperation)OperationFactory.createOperation(SteemyGlobals.Tx.comment);
        cop.mDiscussion = discussion;
        mOperation = cop;
    }

    protected void addTransferOperation(String sender, String receiver, String amount, String assetID, String memo){
        TransferOperation top = (TransferOperation)OperationFactory.createOperation(SteemyGlobals.Tx.transfer);
        top.mSender = sender;
        top.mReceiver = receiver;
        top.mAmount = amount;
        top.mAssetID = assetID;
        top.mMemo = memo;
        mOperation = top;
    }

    protected void addFollowOperation(String follower, String following, String followType){
        FollowOperation fop = (FollowOperation)OperationFactory.createOperation(SteemyGlobals.Tx.follow);
        fop.mFollower = follower;
        fop.mFollowing = following;
        fop.mFollowType = followType;
        mOperation = fop;
    }

    protected boolean serializeAndSign(Pair<String, byte[]> key){
        mSerializedMessage = mOperation.serializeMessage(mRefBlockNum, mRefBlockPrefix, mExp.first);
        mSignature = sign(key);
        if(mSignature == null)
            return false;
        else
            return true;
    }

    protected byte[] sign(Pair<String, byte[]> privateKey){

        byte[] key = privateKey.second;
        SteemECKey ecKey = new SteemECKey(key, ECKey.publicKeyFromPrivate(new BigInteger(1, key), true));

        ECKey.ECDSASignature signature = ecKey.sign(Sha256Hash.of(mSerializedMessage));
        if(signature != null) {
            int id = -1;
            ECKey recKey;
            while (id == -1) {
                for (int i = 0; i < 4; i++) {
                    recKey = ECKey.recoverFromSignature(i, signature, Sha256Hash.of(mSerializedMessage), true);
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
        return null;
    }

    public int getRefBlockNum() {
        return mRefBlockNum;
    }

    public long getRefBlockPrefix() {
        return mRefBlockPrefix;
    }

    public Pair<Long, String> getExp() {
        return mExp;
    }

    public byte[] getSerializedMessage() {
        return mSerializedMessage;
    }

    public byte[] getSignature() {
        return mSignature;
    }

    public Operation getOperation(){
        return mOperation;
    }
}
