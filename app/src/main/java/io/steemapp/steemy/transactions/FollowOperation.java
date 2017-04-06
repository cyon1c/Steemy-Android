package io.steemapp.steemy.transactions;

import org.bitcoinj.core.VarInt;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import io.steemapp.steemy.SteemyGlobals;

/**
 * Created by John on 10/13/2016.
 */

public class FollowOperation extends Operation{

    public String mFollower;
    public String mFollowing;
    public String mFollowType;
    VarInt jsonLength;
    VarInt mFollowerLength;
    static final String follow = "follow";
    static final VarInt followLength = new VarInt(follow.length());
    public String json;

    @Override
    public byte[] serializeMessage(int ref_block_num, long ref_block_prefix, long expiration) {

        json = "{\"follower\":\""+mFollower+"\",\"following\":\""+mFollowing+"\",\"what\":[" + mFollowType + "]}";
        jsonLength = new VarInt(json.length());
        mFollowerLength = new VarInt(mFollower.length());

        ByteBuffer message = ByteBuffer.allocate(calculateBufferSize());

        message.order(ByteOrder.LITTLE_ENDIAN);
        message.put(chainID);
        message.putShort((short)ref_block_num);

        message.putInt((int)ref_block_prefix);

        message.putInt((int)expiration);
        VarInt opLength = new VarInt(1);
        VarInt opType = new VarInt(SteemyGlobals.Tx.follow.getValue());
        message.put(opLength.encode());
        message.put(opType.encode());
        byte b = 0;
        message.put(b);
        b = 1;
        message.put(b);

        message.put(mFollowerLength.encode());
        message.put(mFollower.getBytes());

        message.put(followLength.encode());
        message.put(follow.getBytes());

        message.put(jsonLength.encode());
        message.put(json.getBytes());

        byte extensions = 0;
        message.put(extensions);

        return message.array();
    }

    @Override
    public int calculateBufferSize() {
        return chainID.length
                +2+4+4+2+2
                +mFollower.length()+mFollowerLength.encode().length
                +follow.length()+followLength.encode().length
                +json.length()+jsonLength.encode().length+1;
    }
}
