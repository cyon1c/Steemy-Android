package io.steemapp.steemy.events;

import io.steemapp.steemy.transactions.Transaction;

/**
 * Created by John on 10/15/2016.
 */

public class BroadcastFollowEvent {
    public Transaction followTx;

    public BroadcastFollowEvent(Transaction tx){
        followTx = tx;
    }
}
