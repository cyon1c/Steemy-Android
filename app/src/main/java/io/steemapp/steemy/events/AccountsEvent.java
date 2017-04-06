package io.steemapp.steemy.events;

import io.steemapp.steemy.models.AccountResult;

/**
 * Created by John on 8/4/2016.
 */
public class AccountsEvent {
    public AccountResult account;

    public AccountsEvent(AccountResult result){
        account = result;
    }
}
