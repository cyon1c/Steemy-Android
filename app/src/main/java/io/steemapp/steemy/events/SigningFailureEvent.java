package io.steemapp.steemy.events;

import java.security.SignatureException;

/**
 * Created by John on 9/7/2016.
 */
public class SigningFailureEvent {
    public String message;

    public SigningFailureEvent(String message){
        this.message = message;
    }
}
