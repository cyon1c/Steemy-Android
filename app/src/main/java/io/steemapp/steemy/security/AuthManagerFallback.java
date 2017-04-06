package io.steemapp.steemy.security;

import android.content.Context;
import android.util.Base64;

import java.security.KeyStore;

/**
 * Created by maxke on 25.07.2016.
 * Provides a simple fallback that at least slightly obfuscates stuff, should probably never be reached.
 */
public class AuthManagerFallback extends AuthManager {

    public AuthManagerFallback(Context ctx) {
        super(ctx);
    }

    @Override
    protected void maybeCreateKey(KeyStore ks) throws AuthManagerException {
        // Stub
    }

    @Override
    protected String encrypt(String input) throws AuthManagerException {
        return Base64.encodeToString(input.getBytes(), Base64.NO_WRAP);
    }

    @Override
    protected String decrypt(String base64) throws AuthManagerException {
        return new String(Base64.decode(base64, Base64.NO_WRAP));
    }
}
