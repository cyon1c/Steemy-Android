package io.steemapp.steemy.transactions;

import org.bitcoinj.core.VarInt;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import io.steemapp.steemy.SteemyGlobals;
import io.steemapp.steemy.models.Discussion;

/**
 * Created by John on 10/13/2016.
 */

public class CommentOperation extends Operation {

    Discussion mDiscussion;
    VarInt parentAuthorLength;
    VarInt parentPermlinkLength;
    VarInt authorLength;
    VarInt permlinkLength;
    VarInt titleLength;
    VarInt bodyLength;
    VarInt jsonLength;


    @Override
    public byte[] serializeMessage(int ref_block_num, long ref_block_prefix, long expiration) {
        parentAuthorLength = new VarInt(mDiscussion.getParentAuthor().length());
        parentPermlinkLength = new VarInt(mDiscussion.getParentPermlink().length());
        authorLength = new VarInt(mDiscussion.getAuthor().length());
        permlinkLength = new VarInt(mDiscussion.getPermlink().length());
        titleLength = new VarInt(mDiscussion.getTitle().length());
        bodyLength = new VarInt(mDiscussion.getBody().length());
        jsonLength = new VarInt(mDiscussion.getJsonMetadata().length());

        ByteBuffer message = ByteBuffer.allocate(calculateBufferSize());

        message.order(ByteOrder.LITTLE_ENDIAN);

        message.put(chainID);
        message.putShort((short)ref_block_num);
        message.putInt((int)ref_block_prefix);
        message.putInt((int)expiration);

        VarInt opLength = new VarInt(1);
        VarInt opType = new VarInt(SteemyGlobals.Tx.comment.getValue());

        message.put(opLength.encode());
        message.put(opType.encode());

        message.put(parentAuthorLength.encode());
        message.put(mDiscussion.getParentAuthor().getBytes());

        message.put(parentPermlinkLength.encode());
        message.put(mDiscussion.getParentPermlink().getBytes());

        message.put(authorLength.encode());
        message.put(mDiscussion.getAuthor().getBytes());

        message.put(permlinkLength.encode());
        message.put(mDiscussion.getPermlink().getBytes());

        message.put(titleLength.encode());
        message.put(mDiscussion.getTitle().getBytes());

        message.put(bodyLength.encode());
        message.put(mDiscussion.getBody().getBytes());

        message.put(jsonLength.encode());
        message.put(mDiscussion.getJsonMetadata().getBytes());

        byte extensions = 0;
        message.put(extensions);

        return message.array();
    }

    @Override
    public int calculateBufferSize() {
        return chainID.length
                +2+4+4+2
                +parentAuthorLength.encode().length+mDiscussion.getParentAuthor().length()
                +parentPermlinkLength.encode().length+mDiscussion.getParentPermlink().length()
                +authorLength.encode().length+mDiscussion.getAuthor().length()
                +permlinkLength.encode().length+mDiscussion.getPermlink().length()
                +titleLength.encode().length+mDiscussion.getTitle().length()
                +bodyLength.encode().length+mDiscussion.getBody().length()
                +jsonLength.encode().length+mDiscussion.getJsonMetadata().length()+1;
    }
}
