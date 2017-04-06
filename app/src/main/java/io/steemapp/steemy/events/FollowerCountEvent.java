package io.steemapp.steemy.events;

/**
 * Created by John on 8/24/2016.
 */
public class FollowerCountEvent {
    public int numFollowers;
    public boolean isFollowers;

    public  FollowerCountEvent(int numFollowers, boolean isFollowers){
        this.numFollowers = numFollowers;
        this.isFollowers = isFollowers;
    }
}
