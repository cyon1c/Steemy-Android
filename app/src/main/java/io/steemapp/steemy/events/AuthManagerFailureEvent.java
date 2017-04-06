package io.steemapp.steemy.events;

import io.steemapp.steemy.security.AuthManagerException;

/**
 * Created by John on 8/21/2016.
 */
public class AuthManagerFailureEvent {
    public String e;

    public AuthManagerFailureEvent(String e){
        this.e = e;
    }
}
