package io.steemapp.steemy.events;

/**
 * Created by John on 8/21/2016.
 */
public class RequestAccountEvent {
    public String name;

    public RequestAccountEvent(String account){
        name = account;
    }
}
