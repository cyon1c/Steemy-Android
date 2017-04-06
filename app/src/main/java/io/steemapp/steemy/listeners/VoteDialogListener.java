package io.steemapp.steemy.listeners;

/**
 * Created by John on 10/28/2016.
 */

public interface VoteDialogListener {

    void onCustomVoteSubmitted(String author, String permlink, short voteValue);
}
