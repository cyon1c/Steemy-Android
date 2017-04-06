package io.steemapp.steemy.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.View;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import io.steemapp.steemy.R;
import io.steemapp.steemy.SteemyApplication;
import io.steemapp.steemy.SteemyGlobals;
import io.steemapp.steemy.events.CategoriesEvent;
import io.steemapp.steemy.events.LogoutEvent;
import io.steemapp.steemy.events.NoNetworkEvent;
import io.steemapp.steemy.events.NetworkErrorEvent;
import io.steemapp.steemy.events.NotLoggedInEvent;
import io.steemapp.steemy.fragments.SettingsFragment;
import io.steemapp.steemy.models.AccountManager;
import io.steemapp.steemy.views.SteemyTextView;

public class SettingsActivity extends AbstractActivity implements SettingsFragment.SettingsFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mEventBus.register(this);

        if(SteemyGlobals.areCategoriesInSync()){
            mCategoryAdapter.updateItemList(mCategoryData.getCategoryList());
            mCategoryAdapter.notifyDataSetChanged();
        }
    }

    public static Intent intent(Activity activity){
        Intent i = new Intent(activity, SettingsActivity.class);

        return i;
    }

    @Override
    public void onOSSClicked() {
        View ossView = getLayoutInflater().inflate(R.layout.oss_license, null);
        ((SteemyTextView)ossView).setMovementMethod(new ScrollingMovementMethod());
        AlertDialog oss = new AlertDialog.Builder(this)
                .setView(ossView)
                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).create();

        oss.show();
    }

    @Override
    public void onPrivacyClicked() {
        String url = getString(R.string.privacy_link);
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    @Override
    public void onTOSClicked() {
        String url = getString(R.string.tos_link);
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    @Override
    public void onLogoutClicked() {
        mAccountManager.logout();
    }

    @Override
    public void onManageAuthClicked() {
        startActivity(ManageAccountActivity.intent(this));
    }

    @Subscribe
    public void onEvent(LogoutEvent event){
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public int getContentView() {
        return R.layout.activity_settings;
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
