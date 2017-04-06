package io.steemapp.steemy.transactions;

import static io.steemapp.steemy.SteemyGlobals.hexStringToByteArray;

/**
 * Created by John on 10/13/2016.
 */

public abstract class Operation {

    protected static final byte[] chainID = hexStringToByteArray("0000000000000000000000000000000000000000000000000000000000000000");

    abstract byte[] serializeMessage(int ref_block_num, long ref_block_prefix, long expiration);
    abstract int calculateBufferSize();
}
