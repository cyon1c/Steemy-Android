package io.steemapp.steemy.models;

import android.content.Context;
import android.os.Handler;
import android.util.Pair;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import io.steemapp.steemy.SteemyGlobals;
import io.steemapp.steemy.events.AccountsEvent;
import io.steemapp.steemy.events.AuthManagerFailureEvent;
import io.steemapp.steemy.events.LogoutEvent;
import io.steemapp.steemy.events.RequestAccountEvent;
import io.steemapp.steemy.events.SuccessfulLoginEvent;
import io.steemapp.steemy.security.AuthManager;
import io.steemapp.steemy.security.AuthManagerException;
import io.steemapp.steemy.security.AuthManagerFactory;

/**
 * Created by john.white on 8/17/16.
 */
public class AccountManager {

    private Context mContext;
    private static AccountManager instance;
    private Account mAccount;
    private AuthManager mAuthManager;
    private Bus mEventBus;
    private boolean initialized = false;

    private Handler mHandler = new Handler();
    private Runnable mInvalidator = new Runnable() {
        @Override
        public void run() {
            mUserPass = null;
            mLoginKeyType = null;
        }
    };

    private Pair<String, String> mUserPass;
    private SteemyGlobals.KEY_TYPE mLoginKeyType;

    private AccountManager(Context ctx, Bus eventBus){
        mContext = ctx;
        mAuthManager = AuthManagerFactory.get(mContext);
        mEventBus = eventBus;
    }

    public static AccountManager get(Context appContext, Bus bus){
        if(instance == null)
            instance = new AccountManager(appContext, bus);
            instance.init();
        return instance;
    }

    protected void init(){
        if(!initialized) {
            String userJSON = mAuthManager.getAuthUser();
            if (userJSON != null) {
                mAccount = Account.fromJson(userJSON);
                mEventBus.post(new RequestAccountEvent(mAccount.getName()));
            }
            initialized = true;
        }
    }

    @Subscribe
    public void onEvent(AccountsEvent accountsEvent){
        Account account = accountsEvent.account.getAccounts().get(0);
        if(account.getName().equals(mAccount.getName())){
            mAccount = account;
            mAuthManager.updateAccount(mAccount);
        }
    }

    public void setUserAndPass(String user, String pass, SteemyGlobals.KEY_TYPE keyType){
        mUserPass = new Pair<>(user, pass);
        mLoginKeyType = keyType;
        mHandler.postDelayed(mInvalidator, 1500);
    }

    public void login(Account account){
        if(mUserPass != null) {
            mHandler.removeCallbacksAndMessages(null);
            try {
                if (mAuthManager.authenticate(account, mUserPass.second, mLoginKeyType)) {
                    mAccount = account;
                    mEventBus.post(new SuccessfulLoginEvent());
                } else {
                    mEventBus.post(new AuthManagerFailureEvent("There was a problem logging in with the provided username and password"));
                }
            } catch (AuthManagerException e) {
                mEventBus.post(new AuthManagerFailureEvent(e.getMessage()));
            }
        }else{
            mEventBus.post(new AuthManagerFailureEvent("Login attempt timed out, login credentials invalidated."));
        }
    }

    public String getAccountName(){
        if(isLoggedIn())
            return mAccount.getName();
        else
            return null;
    }

    public boolean isLoggedIn(){return (mAccount != null);}

    public void logout(){
        mAccount = null;
        mAuthManager.logout();
        mEventBus.post(new LogoutEvent());
        initialized = false;
    }

    public void removeKey(SteemyGlobals.KEY_TYPE keytpe){
        mAuthManager.removeKey(keytpe);
    }

    public Account getCurrentAccount(){
        return mAccount;
    }

    public Pair<String, byte[]> getPostingKey(){
        try {
            return mAuthManager.getUserKey(SteemyGlobals.KEY_TYPE.POSTING);
        }catch (AuthManagerException e){
            return null;
        }
    }

    public Pair<String, byte[]> getActiveKey(){
        try {
            return mAuthManager.getUserKey(SteemyGlobals.KEY_TYPE.ACTIVE);
        }catch (AuthManagerException e){
            return null;
        }
    }

}
