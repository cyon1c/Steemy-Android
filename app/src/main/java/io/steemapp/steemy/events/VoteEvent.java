package io.steemapp.steemy.events;

/**
 * Created by John on 9/7/2016.
 */
public class VoteEvent {
    public boolean success;
    public boolean upvote;

    public VoteEvent(boolean success, boolean upvote){
        this.success = success;
        this.upvote = upvote;
    }
}
