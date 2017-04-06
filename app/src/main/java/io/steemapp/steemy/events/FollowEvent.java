package io.steemapp.steemy.events;

import io.steemapp.steemy.SteemyGlobals;
import io.steemapp.steemy.models.Follower;

/**
 * Created by John on 10/16/2016.
 */

public class FollowEvent {
    public SteemyGlobals.FOLLOW_STATE followState;

    public FollowEvent(SteemyGlobals.FOLLOW_STATE follow_state){
        followState = follow_state;
    }
}
