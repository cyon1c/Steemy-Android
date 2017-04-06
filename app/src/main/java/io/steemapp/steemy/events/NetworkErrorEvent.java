package io.steemapp.steemy.events;


/**
 * Created by John on 7/28/2016.
 */
public class NetworkErrorEvent {
    public String error;

    public NetworkErrorEvent(String error){
        this.error = error;
    }
}
