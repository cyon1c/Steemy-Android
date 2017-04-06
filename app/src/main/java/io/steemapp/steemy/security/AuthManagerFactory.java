package io.steemapp.steemy.security;

import android.content.Context;
import android.os.Build;

/**
 * Created by maxke on 25.07.2016.
 * Provides a best effort approach to saving user credentials
 */

public class AuthManagerFactory {

    public static AuthManager get(Context ctx) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                AuthManager m = new AuthManager23(ctx);
                m.init();
                return m;
            } else {
                AuthManager m = new AuthManager18(ctx);
                m.init();
                return m;
            }
        } catch (AuthManagerException e) {
            return new AuthManagerFallback(ctx);
        }
    }

}
