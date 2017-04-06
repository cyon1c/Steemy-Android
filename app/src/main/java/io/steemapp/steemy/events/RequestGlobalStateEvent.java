package io.steemapp.steemy.events;

/**
 * Created by John on 8/23/2016.
 */
public class RequestGlobalStateEvent {
    public boolean force = false;

    public RequestGlobalStateEvent(boolean force){
        this.force = force;
    }
}
