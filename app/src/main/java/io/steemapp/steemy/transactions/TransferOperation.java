package io.steemapp.steemy.transactions;

import android.util.Pair;

import org.bitcoinj.core.VarInt;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import io.steemapp.steemy.SteemyGlobals;

/**
 * Created by John on 10/13/2016.
 */

public class TransferOperation extends Operation {

    public String mSender;
    public String mReceiver;
    public String mAmount;
    public String mAssetID;
    public String mMemo;

    VarInt mSenderLength;
    VarInt mReceiverLength;
    VarInt mMemoLength;

    @Override
    public byte[] serializeMessage(int ref_block_num, long ref_block_prefix, long expiration) {
        mSenderLength = new VarInt(mSender.length());
        mReceiverLength = new VarInt(mReceiver.length());
        mMemoLength = new VarInt(mMemo.length());

        int dotIndex = mAmount.indexOf(".");
        byte precision = (byte)(dotIndex == -1 ? 0 : (mAmount.length()-1 - dotIndex));


//        dollarsAndCents = deconstructAmount(mAmount);

        ByteBuffer message = ByteBuffer.allocate(calculateBufferSize());

        message.order(ByteOrder.LITTLE_ENDIAN);
        message.put(chainID);
        message.putShort((short)ref_block_num);

        message.putInt((int)ref_block_prefix);

        message.putInt((int)expiration);
        VarInt opLength = new VarInt(1);
        VarInt opType = new VarInt(SteemyGlobals.Tx.transfer.getValue());
        message.put(opLength.encode());
        message.put(opType.encode());

        message.put(mSenderLength.encode());
        message.put(mSender.getBytes());

        message.put(mReceiverLength.encode());
        message.put(mReceiver.getBytes());

        int amount = Integer.parseInt(mAmount.replace(".", ""));
        message.putInt(amount);

        message.put(precision);
        message.put(mAssetID.getBytes());
        byte empty = 0;
        for(int i = 0; i < 7-mAssetID.length(); i++)
            message.put(empty);

        message.put(mMemoLength.encode());
        message.put(mMemo.getBytes());

        //extensions byte
        message.put(empty);

        return message.array();
    }

//    private Pair<Integer, Integer> deconstructAmount(String amount){
//        String dollars = amount.substring(0, amount.indexOf(".")-1);
//        String cents = amount.substring(amount.indexOf("."), amount.length()-1);
//
//        if(cents.length() > 3) {
//            cents = cents.substring(0, 3);
//        }
//    }

    @Override
    public int calculateBufferSize() {
        return chainID.length
                +2+4+4+2
                +mSender.length()+mSenderLength.encode().length
                +mReceiver.length()+mReceiverLength.encode().length
                +4+1+7
                +mMemo.length()+mMemoLength.encode().length+1;
    }
}
