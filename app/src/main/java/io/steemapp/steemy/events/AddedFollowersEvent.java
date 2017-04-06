package io.steemapp.steemy.events;

import io.steemapp.steemy.SteemyGlobals;
import io.steemapp.steemy.models.Account;

/**
 * Created by John on 8/19/2016.
 */
public class AddedFollowersEvent {
    public SteemyGlobals.FOLLOWER_TYPE followerType;
    public Account account;

    public AddedFollowersEvent(Account account, SteemyGlobals.FOLLOWER_TYPE followerType){
        this.account = account;
        this.followerType = followerType;
    }
}
