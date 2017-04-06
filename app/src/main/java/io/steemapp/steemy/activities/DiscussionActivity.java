package io.steemapp.steemy.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import com.squareup.otto.Subscribe;

import io.steemapp.steemy.R;
import io.steemapp.steemy.SteemyGlobals;
import io.steemapp.steemy.dialogs.VoteDialogFragment;
import io.steemapp.steemy.events.AccountsEvent;
import io.steemapp.steemy.events.CategoriesEvent;
import io.steemapp.steemy.events.CommentsEvent;
import io.steemapp.steemy.events.NoNetworkEvent;
import io.steemapp.steemy.events.NetworkErrorEvent;
import io.steemapp.steemy.events.NotLoggedInEvent;
import io.steemapp.steemy.fragments.DiscussionFragment;
import io.steemapp.steemy.listeners.VoteDialogListener;
import io.steemapp.steemy.models.CommentTree;
import io.steemapp.steemy.models.Discussion;

public class DiscussionActivity extends AbstractActivity implements
        DiscussionFragment.DiscussionListener,
        VoteDialogListener{

    private DiscussionFragment mDiscussionFragment;
    private Bundle mDiscussionDetails;

    private CommentTree mCommentTree;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mEventBus.register(this);

        mDiscussionDetails = getIntent().getExtras();

        getSupportActionBar().setTitle(mDiscussionDetails.getString("category"));

        mDiscussionFragment = DiscussionFragment.instance();

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, mDiscussionFragment)
                .commit();

        if(SteemyGlobals.areCategoriesInSync()){
            mCategoryAdapter.updateItemList(mCategoryData.getCategoryList());
            mCategoryAdapter.notifyDataSetChanged();
        }
    }

    @Subscribe
    public void onEvent(CommentsEvent event){
        mCommentTree = event.tree;
        mDiscussionFragment.updateComments(mCommentTree);
    }

    public static Intent intent(String category, String author, String permlink, Activity activity){
        Intent i = new Intent(activity, DiscussionActivity.class);

        Bundle b = new Bundle();

        b.putString("category", category);
        b.putString("author", author);
        b.putString("permlink", permlink);

        i.putExtras(b);

        return i;
    }

    @Override
    protected void onResume() {
        super.onResume();

        mService.getComments(mDiscussionDetails.getString("category"), mDiscussionDetails.getString("author"), mDiscussionDetails.getString("permlink"));
    }

    @Override
    public void update() {

    }

    @Override
    public void vote(Discussion discussion, int voteWeight) {
        mTransactor.vote(discussion.getAuthor(), discussion.getPermlink(), (short)voteWeight);
    }

    @Override
    public void onCustomVoteSubmitted(String author, String permlink, short voteValue) {
        mTransactor.vote(author, permlink, voteValue);
    }

    @Override
    public void customVoteRequested(Discussion discussion, boolean upvote) {
        VoteDialogFragment voteDialogFragment = VoteDialogFragment.newInstance(discussion, upvote);
        voteDialogFragment.show(getSupportFragmentManager(), "dialog");
    }

    @Override
    public void replyClicked(String author, String permlink) {
        startActivity(ComposePostActivity.intent(true, this, mCommentTree.getRoot().getCategory(), author, permlink));
    }

    @Override
    public void votesClicked(String author, String permlink) {

    }

    @Override
    public void payoutClicked(String author, String permlink) {

    }

    @Override
    public void profileClicked(String name) {
        mService.getAccount(name);
    }

    @Override
    public int getContentView() {
        return R.layout.activity_discussion;
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
    public void onEvent(NoNetworkEvent event){
        showErrorDialog("No Network", null);
    }

    @Subscribe
    public void onEvent(NotLoggedInEvent event){
        showErrorDialog("Error", "You must be logged in to do that.");
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
