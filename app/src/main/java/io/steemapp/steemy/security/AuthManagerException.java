package io.steemapp.steemy.security;

/**
 * Created by maxke on 25.07.2016.
 * Wrapper exception. If that is thrown, auth is fubar.
 */

public class AuthManagerException extends Exception {
    public AuthManagerException(Exception cause) {
        initCause(cause);
    }
}