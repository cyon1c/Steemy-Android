package io.steemapp.steemy.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;

import com.squareup.otto.Subscribe;

import net.hockeyapp.android.metrics.MetricsManager;

import io.steemapp.steemy.R;
import io.steemapp.steemy.SteemyGlobals;
import io.steemapp.steemy.dialogs.VoteDialogFragment;
import io.steemapp.steemy.events.CategoriesEvent;
import io.steemapp.steemy.events.DiscussionsEvent;
import io.steemapp.steemy.events.NoNetworkEvent;
import io.steemapp.steemy.events.NotLoggedInEvent;
import io.steemapp.steemy.events.RequestAccountEvent;
import io.steemapp.steemy.events.RequestGlobalStateEvent;
import io.steemapp.steemy.events.NetworkErrorEvent;
import io.steemapp.steemy.events.VoteEvent;
import io.steemapp.steemy.fragments.DiscussionListFragment;
import io.steemapp.steemy.listeners.OnDiscussionListInteractionListener;
import io.steemapp.steemy.listeners.VoteDialogListener;
import io.steemapp.steemy.models.Discussion;
import io.steemapp.steemy.models.DiscussionList;

public class HomeListActivity extends AbstractActivity implements
        OnDiscussionListInteractionListener,
        VoteDialogListener{

    private FloatingActionButton mFAB;

    private final static String FRAGMENTTAG = "HomeListFragment";
    private final static int PHOTO_REQUEST_CODE = 3000;
    private final static String FIRST_RUN_KEY = "firstrun";

    protected DiscussionListFragment mFragment;

    @Override
    public int getContentView() {
        return R.layout.activity_home_list;
    }

    @Override
    public void setActivity() {
        activity = this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MetricsManager.register(this, getApplication());
        mEventBus.register(this);
        launchServices();
        checkFirstRun();

        mFAB = (FloatingActionButton) findViewById(R.id.fab);
        mFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(ComposePostActivity.intent(false, HomeListActivity.this, null, null, null));
            }
        });

        mFragment = DiscussionListFragment.newInstance();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, mFragment, FRAGMENTTAG)
                .commit();

    }

    @Subscribe
    public void onEvent(DiscussionsEvent event){
        if(mCategoryIsDirty)
            mFragment.setNewList(event.discussion);
        else
            mFragment.updateListData(event.discussion);

        mFragment.finishRefreshing();
        mCategoryIsDirty = false;
    }

    @Subscribe
    public void onEvent(CategoriesEvent event){
        SteemyGlobals.setCategories(event.mList);
        mCategoryData = SteemyGlobals.steemyCategories;
        mCategoryAdapter.updateItemList(mCategoryData.getCategoryList());
        mCategoryAdapter.notifyDataSetChanged();
    }

    @Subscribe
    public void onEvent(NetworkErrorEvent event){
        showErrorDialog("Network Error", event.error);
    }

    @Subscribe
    public void onEvent(NoNetworkEvent event){
        showErrorDialog("No Network", null);
    }

    @Subscribe
    public void onEvent(RequestAccountEvent event){
        if(mService != null){
            mService.getAccount(event.name);
        }
    }

    @Subscribe
    public void onEvent(VoteEvent event){
        if(event.success){
            Log.i("Home List", "Successful vote");
        }else
            Log.i("Home List", "Failed vote");
    }

    private void launchServices(){
        mEventBus.post(new RequestGlobalStateEvent(true));
        if(mCategoryData != null && mCategoryData.getCurrent() != null)
            mService.getDiscussions(mSortMethod.toString(), mCategoryData.getCurrent(), "none", "none", 11);
        else
            mService.getDiscussions(mSortMethod.toString(), "none", "none", "none", 11);
    }

    @Override
    public void onLoadMore(DiscussionList list) {
        if(mCategoryData.getCurrent() != null)
            mService.getDiscussions(mSortMethod.toString(), mCategoryData.getCurrent(), list.getNextAuthor(), list.getNextTitle(), 11);
    }

    @Override
    public void vote(Discussion discussion, short vote_weight) {
        mTransactor.vote(discussion.getAuthor(), discussion.getPermlink(), vote_weight);
    }

    @Override
    public void customVoteRequested(Discussion discussion, boolean upvote) {
        VoteDialogFragment voteDialogFragment = VoteDialogFragment.newInstance(discussion, upvote);
        voteDialogFragment.show(getSupportFragmentManager(), "dialog");
    }

    @Override
    public void onCustomVoteSubmitted(String author, String permlink, short voteValue) {
        mTransactor.vote(author, permlink, voteValue);
    }

    @Override
    public void onDiscussionClicked(Discussion discussion){
        Intent newDiscussionIntent = DiscussionActivity.intent(discussion.getCategory(), discussion.getAuthor(), discussion.getPermlink(), this);
        startActivity(newDiscussionIntent);
    }

    @Override
    public void onRefresh() {
        mCategoryIsDirty = true;
        mService.getDiscussions(mSortMethod.toString(), mCategoryData.getCurrent(), "none", "none", 11);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == LOGIN_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

            }
        }
    }

    public static Intent intent(Activity activity){
        return new Intent(activity, HomeListActivity.class);
    }

    protected void checkFirstRun(){
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if(prefs.contains(FIRST_RUN_KEY)){
            return;
        }else{
            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setTitle("Welcome to Steemy!")
                    .setMessage("We are so excited for you to try out Steemy. Please keep in mind first and foremost throughout your testing of Steemy that it is currently in Alpha, and all features are subject to change. We take great care in building reliable, quality apps, and have done our best to provide that to you now. If you run into any issues, please feel free to reach out at contact@steemapp.io. Remember - All functionality is in test mode and shouldn't be trusted with any critical operations.  All data and image hosting might be temporary. Use at your own risk.")
                    .setPositiveButton("Start using Steemy", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });

            builder.create().show();
            prefs.edit().putBoolean(FIRST_RUN_KEY, true).apply();
        }
    }

    @Subscribe
    public void onEvent(NotLoggedInEvent event){
        showErrorDialog("Error", "You must be logged in to do that.");
    }

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
