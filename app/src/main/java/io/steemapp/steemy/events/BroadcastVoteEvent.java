package io.steemapp.steemy.events;

import org.json.JSONObject;

import io.steemapp.steemy.transactions.Transaction;

/**
 * Created by John on 9/7/2016.
 */
public class BroadcastVoteEvent {
    public JSONObject vote;
    public Transaction voteTx;

    public BroadcastVoteEvent(JSONObject vote){
        this.vote = vote;
    }

    public BroadcastVoteEvent(Transaction tx){
        voteTx = tx;
    }
}
