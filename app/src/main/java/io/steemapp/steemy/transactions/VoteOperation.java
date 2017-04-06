package io.steemapp.steemy.transactions;

import org.bitcoinj.core.VarInt;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import io.steemapp.steemy.SteemyGlobals;

/**
 * Created by John on 10/13/2016.
 */

public class VoteOperation extends Operation {

    String mAuthor;
    String mVoter;
    String mPermlink;
    short mWeight;

    @Override
    public byte[] serializeMessage(int ref_block_num, long ref_block_prefix, long expiration) {
        ByteBuffer message = ByteBuffer.allocate(calculateBufferSize());

        message.order(ByteOrder.LITTLE_ENDIAN);
        message.put(chainID);
        message.putShort((short)ref_block_num);

        message.putInt((int)ref_block_prefix);

        message.putInt((int)expiration);
        VarInt opLength = new VarInt(1);
        VarInt opType = new VarInt(SteemyGlobals.Tx.vote.getValue());
        message.put(opLength.encode());
        message.put(opType.encode());
        VarInt propLength = new VarInt(mVoter.length());
        message.put(propLength.encode());
        message.put(mVoter.getBytes());
        propLength = new VarInt(mAuthor.length());
        message.put(propLength.encode());
        message.put(mAuthor.getBytes());
        propLength = new VarInt(mPermlink.length());
        message.put(propLength.encode());
        message.put(mPermlink.getBytes());
        message.putShort(mWeight);
        byte extensions = 0;
        message.put(extensions);

        return message.array();
    }

    @Override
    public int calculateBufferSize() {
        return chainID.length+2+4+4+2+1+mVoter.length()+1+mAuthor.length()+1+mPermlink.length()+2+1;
    }
}
