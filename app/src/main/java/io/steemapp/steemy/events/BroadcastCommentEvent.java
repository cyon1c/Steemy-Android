package io.steemapp.steemy.events;

import org.json.JSONObject;

import io.steemapp.steemy.transactions.Transaction;

/**
 * Created by John on 9/7/2016.
 */
public class BroadcastCommentEvent {
    public JSONObject comment;
    public Transaction commentTx;

    public BroadcastCommentEvent(JSONObject comment){
        this.comment = comment;
    }

    public BroadcastCommentEvent(Transaction tx){
        commentTx = tx;
    }
}
