package io.steemapp.steemy.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;


import net.hockeyapp.android.LoginActivity;

import io.steemapp.steemy.R;
import io.steemapp.steemy.SteemyApplication;
import io.steemapp.steemy.SteemyGlobals;
import io.steemapp.steemy.events.AccountsEvent;
import io.steemapp.steemy.events.AuthManagerFailureEvent;
import io.steemapp.steemy.events.CategoriesEvent;
import io.steemapp.steemy.events.NoNetworkEvent;
import io.steemapp.steemy.events.NetworkErrorEvent;
import io.steemapp.steemy.events.SuccessfulLoginEvent;
import io.steemapp.steemy.fragments.LogInFragment;
import io.steemapp.steemy.models.Account;
import io.steemapp.steemy.models.AccountManager;
import io.steemapp.steemy.models.AccountResult;
import io.steemapp.steemy.network.SteemyAPIService;


/**
 * Created by John on 10/3/2015.
 */
public class LogInActivity extends AbstractActivity
        implements LogInFragment.OnFragmentInteractionListener{

    LogInFragment mLoginFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLoginFragment = LogInFragment.newInstance();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.login_container,  mLoginFragment, null)
                .commit();

        mEventBus.register(this);
        getSupportActionBar().setTitle("");

        if(SteemyGlobals.areCategoriesInSync()){
            mCategoryAdapter.updateItemList(mCategoryData.getCategoryList());
            mCategoryAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(getIntent().getExtras() != null && mAccountManager.isLoggedIn()){
            mLoginFragment.setUsername(mAccountManager.getAccountName());
            mLoginFragment.setImport(getIntent().getExtras().getInt("key_type"));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mEventBus.unregister(this);
    }

    @Subscribe
    public void onEvent(AccountsEvent accountsEvent){
        AccountResult result = accountsEvent.account;
        mAccountManager.login(result.getAccounts().get(0));
    }

    @Subscribe
    public void onEvent(SuccessfulLoginEvent event){
        successfulLogin(mAccountManager.getCurrentAccount());
    }

    @Override
    public void onLoginClicked(String user, String pass, int keyType) {
        if(user.isEmpty()) {
            mLoginFragment.emptyUserField();
            return;
        } else if(pass.isEmpty()) {
            mLoginFragment.emptyPasswordField();
            return;
        } else{
            if(keyType == 0)
                mAccountManager.setUserAndPass(user, pass, SteemyGlobals.KEY_TYPE.POSTING);
            else if(keyType == 1)
                mAccountManager.setUserAndPass(user, pass, SteemyGlobals.KEY_TYPE.ACTIVE);
            else if(keyType == 2)
                mAccountManager.setUserAndPass(user, pass, SteemyGlobals.KEY_TYPE.MASTER);
            
            mService.getAccount(user);
        }

//        mPublicKeys = LoginUtil.getPublicKeys(user, pass);

    }

    public static Intent loginIntent(Activity activity){
        Intent i = new Intent(activity, LogInActivity.class);
        i.setFlags(i.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
        return i;
    }

    public static Intent importKeyIntent(Activity activity, SteemyGlobals.KEY_TYPE kt){
        Intent i = new Intent(activity, LogInActivity.class);
        i.setFlags(i.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
        Bundle b = new Bundle();
        b.putInt("key_type", kt.getValue());
        i.putExtras(b);
        return i;
    }

    public void successfulLogin(Account account){
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public int getContentView() {
        return R.layout.activity_login;
    }

    @Override
    public void setActivity() {
        activity = this;
    }

    @Subscribe
    public void onEvent(CategoriesEvent event){
        SteemyGlobals.setCategories(event.mList);
        mCategoryData = SteemyGlobals.steemyCategories;
        mCategoryAdapter.updateItemList(mCategoryData.getCategoryList());
        mCategoryAdapter.notifyDataSetChanged();
    }

    //ERROR HANDLING HERE
    @Subscribe
    public void onEvent(NetworkErrorEvent event){
        showErrorDialog("Network Error", event.error);
    }

    @Subscribe
    public void onEvent(NoNetworkEvent event){
        showErrorDialog("No Network", null);
    }

    @Subscribe
    public void onEvent(AuthManagerFailureEvent errorEvent){
        showErrorDialog("Login Failure", errorEvent.e);
    }

    private boolean mIsDialogShowing = false;
    private void showErrorDialog(String title, String message){
        if(!mIsDialogShowing) {
            AlertDialog dialog = SteemyGlobals.buildSteemyErrorDialog(this, title, message);

            dialog.setButton(DialogInterface.BUTTON_POSITIVE, "Okay >", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    mIsDialogShowing = false;
                    dialogInterface.dismiss();
                }
            });
            dialog.show();
        }
    }
}
