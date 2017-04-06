package io.steemapp.steemy.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Pair;

import com.squareup.otto.Subscribe;

import net.hockeyapp.android.LoginActivity;

import io.steemapp.steemy.R;
import io.steemapp.steemy.SteemyGlobals;
import io.steemapp.steemy.events.AccountsEvent;
import io.steemapp.steemy.events.AddedFollowersEvent;
import io.steemapp.steemy.events.NetworkErrorEvent;
import io.steemapp.steemy.events.NoNetworkEvent;
import io.steemapp.steemy.events.NotLoggedInEvent;
import io.steemapp.steemy.fragments.ManageAccountFragment;

import static io.steemapp.steemy.SteemyGlobals.byteArrayToHexString;

public class ManageAccountActivity extends AbstractActivity implements ManageAccountFragment.OnFragmentInteractionListener {

    private ManageAccountFragment mFragment;

    @Override
    public int getContentView() {
        return R.layout.activity_manage_account;
    }

    @Override
    public void setActivity() {
        activity = this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mEventBus.register(this);

        if(SteemyGlobals.areCategoriesInSync()){
            mCategoryAdapter.updateItemList(mCategoryData.getCategoryList());
            mCategoryAdapter.notifyDataSetChanged();
        }

        mService.getFollowersList(mAccountManager.getCurrentAccount(), SteemyGlobals.FOLLOWER_TYPE.IGNORED);

        String activePrivate = null;
        if(mAccountManager.getActiveKey() != null)
            activePrivate = byteArrayToHexString(mAccountManager.getActiveKey().second);

        String postingPrivate = null;
        if(mAccountManager.getPostingKey() != null)
            postingPrivate = byteArrayToHexString(mAccountManager.getPostingKey().second);

        Pair<String, String> active = new Pair<>(
                mAccountManager.getCurrentAccount().getActiveKeyAuth().getKeyAuths().get(0).get(0),
                activePrivate);

        Pair<String, String> posting = new Pair<>(
                mAccountManager.getCurrentAccount().getPostingKeyAuth().getKeyAuths().get(0).get(0),
                postingPrivate);

        mFragment = ManageAccountFragment.newInstance(active, posting);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.manage_container, mFragment)
                .commit();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == LOGIN_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                String activePrivate = null;
                if(mAccountManager.getActiveKey() != null)
                    activePrivate = byteArrayToHexString(mAccountManager.getActiveKey().second);

                String postingPrivate = null;
                if(mAccountManager.getPostingKey() != null)
                    postingPrivate = byteArrayToHexString(mAccountManager.getPostingKey().second);

                Pair<String, String> active = new Pair<>(
                        mAccountManager.getCurrentAccount().getActiveKeyAuth().getKeyAuths().get(0).get(0),
                        activePrivate);

                Pair<String, String> posting = new Pair<>(
                        mAccountManager.getCurrentAccount().getPostingKeyAuth().getKeyAuths().get(0).get(0),
                        postingPrivate);

                mFragment.updateImported(active, posting);
            }
        }
    }

    public static Intent intent(Activity activity){
        Intent i = new Intent(activity, ManageAccountActivity.class);

        return i;
    }

    @Override
    public void ignoredUserClicked(String accountName) {
        mService.getAccount(accountName);
    }

    @Override
    public void ignoredRequested() {
        mService.getFollowersList(mAccountManager.getCurrentAccount(), SteemyGlobals.FOLLOWER_TYPE.IGNORED);
    }

    @Override
    public void importKey(SteemyGlobals.KEY_TYPE key_type) {
        startActivityForResult(LogInActivity.loginIntent(activity), LOGIN_REQUEST_CODE);
    }

    @Override
    public void removePosting() {
        mAccountManager.removeKey(SteemyGlobals.KEY_TYPE.POSTING);
    }

    @Override
    public void removeActive() {
        mAccountManager.removeKey(SteemyGlobals.KEY_TYPE.ACTIVE);
    }

    @Subscribe
    public void onEvent(AddedFollowersEvent event){
        mFragment.setIgnoredList(event.account.getmIgnored());
    }

    @Subscribe
    public void onEvent(AccountsEvent event){
        startActivity(ProfileActivity.intent(event.account.getAccounts().get(0), this, false));
    }

    //ERROR HANDLING HERE
    @Subscribe
    public void onEvent(NetworkErrorEvent event){
        showErrorDialog("Network Error", event.error);
    }

    @Subscribe
    public void onEvent(NotLoggedInEvent event){
        showErrorDialog("Error", "You must be logged in to do that.");
    }

    @Subscribe
    public void onEvent(NoNetworkEvent event){
        showErrorDialog("No Network", null);
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
