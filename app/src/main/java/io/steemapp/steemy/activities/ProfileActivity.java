package io.steemapp.steemy.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.gson.Gson;
import com.squareup.otto.Subscribe;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;

import io.steemapp.steemy.R;
import io.steemapp.steemy.SteemyGlobals;
import io.steemapp.steemy.adapter.ProfilePagerAdapter;
import io.steemapp.steemy.dialogs.VoteDialogFragment;
import io.steemapp.steemy.events.AccountsEvent;
import io.steemapp.steemy.events.AddedFollowersEvent;
import io.steemapp.steemy.events.CategoriesEvent;
import io.steemapp.steemy.events.FollowEvent;
import io.steemapp.steemy.events.MyPostsEvent;
import io.steemapp.steemy.events.NoNetworkEvent;
import io.steemapp.steemy.events.NotLoggedInEvent;
import io.steemapp.steemy.events.RepliesEvent;
import io.steemapp.steemy.events.NetworkErrorEvent;
import io.steemapp.steemy.fragments.DiscussionListFragment;
import io.steemapp.steemy.fragments.FollowersFragment;
import io.steemapp.steemy.fragments.ProfileFragment;
import io.steemapp.steemy.fragments.WalletFragment;
import io.steemapp.steemy.listeners.OnDiscussionListInteractionListener;
import io.steemapp.steemy.listeners.OnFollowerInteractionListener;
import io.steemapp.steemy.listeners.VoteDialogListener;
import io.steemapp.steemy.models.Account;
import io.steemapp.steemy.models.Discussion;
import io.steemapp.steemy.models.DiscussionList;
import io.steemapp.steemy.models.Follower;

public class ProfileActivity extends AbstractActivity implements OnDiscussionListInteractionListener,
        ProfileFragment.OnProfileFragmentInteraction,
        OnFollowerInteractionListener,
        VoteDialogListener {

    private final static String ACCOUNT_ARG = "account";
    private final static String MY_ARG = "thisIsMyAccount";

    private ProfileFragment mProfileFragment;
    private WalletFragment mWalletFragment;
    private DiscussionListFragment mPostsFragment;
    private FollowersFragment mFollowersFragment;
    private DiscussionListFragment mRepliesFragment;

    private ViewPager mProfilePager;
    private ProfilePagerAdapter mProfileAdapter;
    private PagerTabStrip mProfileTabStrip;

    private Account mCurrentAccount;
    private boolean thisIsMyAccount = false;

    MenuItem mFollowItem;
    MenuItem mUnfollowItem;
    MenuItem mMuteItem;
    MenuItem mUnmuteItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mEventBus.register(this);

        initAccount(getIntent());

        mProfilePager = (ViewPager)findViewById(R.id.profile_pager);
        mProfileTabStrip = (PagerTabStrip)findViewById(R.id.profile_pager_tabs);
        styleProfileTabStrip();

        launchFragments();

        mService.getMyPosts(mCurrentAccount.getName());
        mService.getReplies(mCurrentAccount.getName());

        if(SteemyGlobals.areCategoriesInSync()){
            mCategoryAdapter.updateItemList(mCategoryData.getCategoryList());
            mCategoryAdapter.notifyDataSetChanged();
        }

        onFollowsRequested();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        if(!thisIsMyAccount && mAccountManager.isLoggedIn()) {
            inflater.inflate(R.menu.menu_profile, menu);
            mFollowItem = menu.findItem(R.id.nav_follow);
            mUnfollowItem = menu.findItem(R.id.nav_unfollow);
            mMuteItem = menu.findItem(R.id.nav_mute);
            mUnmuteItem = menu.findItem(R.id.nav_unmute);

            if(mAccountManager.getCurrentAccount().checkIfFollowing(mCurrentAccount.getName())) {
                mFollowItem.setVisible(false);
                mUnfollowItem.setVisible(true);
            }else{
                mFollowItem.setVisible(true);
                mUnfollowItem.setVisible(false);
            }
            if(mAccountManager.getCurrentAccount().checkIfMuted(mCurrentAccount.getName())) {
                mMuteItem.setVisible(false);
                mUnmuteItem.setVisible(true);
            }else{
                mMuteItem.setVisible(true);
                mUnmuteItem.setVisible(false);
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String follow = "";
        switch(item.getItemId()) {
            case R.id.nav_follow:
                follow = "\"blog\"";
                break;
            case R.id.nav_unfollow:
                break;
            case R.id.nav_mute:
                follow = "\"ignore\"";
                break;
            case R.id.nav_unmute:
                break;
        }

        mTransactionManager
                .newTx()
                .addFollowOp(mAccountManager.getAccountName(), mCurrentAccount.getName(), follow)
                .prepareTx()
                .broadcastTx();

        return true;
    }

    public void styleProfileTabStrip(){
        mProfileTabStrip.setDrawFullUnderline(false);
        mProfileTabStrip.setTabIndicatorColor(getResources().getColor(R.color.colorPrimary));
        mProfileTabStrip.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
    }

    private void launchFragments(){
        mProfileFragment = initProfileFragment();

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, mProfileFragment)
                .commit();

        mWalletFragment = initWalletFragment();
        mRepliesFragment = DiscussionListFragment.newInstance();
        mPostsFragment = DiscussionListFragment.newInstance();
        mFollowersFragment = FollowersFragment.newInstance();

        mProfileAdapter = new ProfilePagerAdapter(getSupportFragmentManager(),
                mWalletFragment,
                mPostsFragment,
                mRepliesFragment,
                mFollowersFragment);

        mProfilePager.setAdapter(mProfileAdapter);

    }

    public void initAccount(Intent i){
        Bundle extras = i.getExtras();
        thisIsMyAccount = extras.getBoolean(MY_ARG);
        if(thisIsMyAccount){
            mCurrentAccount = mAccountManager.getCurrentAccount();
        }else
            mCurrentAccount = new Gson().fromJson(extras.getString(ACCOUNT_ARG), Account.class);
        getSupportActionBar().setTitle("@" + mCurrentAccount.getName());
    }

    @Subscribe
    public void onEvent(AddedFollowersEvent event){
        mCurrentAccount = event.account;
        if(event.followerType == SteemyGlobals.FOLLOWER_TYPE.FOLLOWERS){
            initFollowersLists(mCurrentAccount.getFollowers(), true);
            mProfileFragment.updateFollowers(mCurrentAccount.getFollowers().size());
        }else if(event.followerType == SteemyGlobals.FOLLOWER_TYPE.FOLLOWING){
            initFollowersLists(mCurrentAccount.getFollowing(), false);
            mProfileFragment.updateFollowing(mCurrentAccount.getFollowing().size());
        }
    }

    @Subscribe
    public void onEvent(FollowEvent event){
        if(event.followState == SteemyGlobals.FOLLOW_STATE.FOLLOWED) {
            mService.getFollowersList(mCurrentAccount, SteemyGlobals.FOLLOWER_TYPE.FOLLOWERS);
            mFollowItem.setVisible(false);
            mUnfollowItem.setVisible(true);
            mMuteItem.setVisible(true);
            mUnmuteItem.setVisible(false);
        }else if(event.followState == SteemyGlobals.FOLLOW_STATE.IGNORED){
            mFollowItem.setVisible(true);
            mUnfollowItem.setVisible(false);
            mMuteItem.setVisible(false);
            mUnmuteItem.setVisible(true);
        }else if(event.followState == SteemyGlobals.FOLLOW_STATE.NONE){
            mFollowItem.setVisible(true);
            mUnfollowItem.setVisible(false);
            mMuteItem.setVisible(true);
            mUnmuteItem.setVisible(false);
        }

    }

    @Subscribe
    public void onEvent(AccountsEvent event){
        startActivity(ProfileActivity.intent(event.account.getAccounts().get(0), this, false));
    }

    @Subscribe
    public void onEvent(MyPostsEvent event) {
        mPostsFragment.statelessUpdate(event.discussion);
        mPostsFragment.finishRefreshing();
    }

    @Subscribe
    public void onEvent(RepliesEvent event){
        mRepliesFragment.statelessUpdate(event.discussion);
        mRepliesFragment.finishRefreshing();
    }

    private ProfileFragment initProfileFragment(){
        Bundle b = ProfileFragment.buildBundle(
                mCurrentAccount.getName(),
                mCurrentAccount.getSbdBalance().substring(0, mCurrentAccount.getSbdBalance().length()-4),
                mCurrentAccount.getFollowers().size(),
                mCurrentAccount.getFollowing().size(),
                mCurrentAccount.getPostCount(),
                mCurrentAccount.getReputation());

        return ProfileFragment.getNewInstance(b);
    }

    private WalletFragment initWalletFragment(){
        double valueOfOneVest = (SteemyGlobals.BLOCKCHAIN_GLOBALS.getSteemtoVestsRate()/1000000);
        double accountVests = Double.parseDouble(mCurrentAccount.getVestingShares().substring(0, mCurrentAccount.getVestingShares().length()-6));

        DecimalFormat df = new DecimalFormat("#.00");
        df.setRoundingMode(RoundingMode.CEILING);
        String steemPower = df.format(accountVests*valueOfOneVest)+" STEEM";

        return WalletFragment.getNewInstance(
                mCurrentAccount.getBalance(),
                steemPower,
                "$"+mCurrentAccount.getSbdBalance().substring(0, mCurrentAccount.getSbdBalance().length()-4));
    }

    private void initFollowersLists(ArrayList<Follower> followList, boolean isFollowers){
        if(isFollowers) {
            mFollowersFragment.setNewFollowerList(followList);
            mProfileFragment.updateFollowers(followList.size());
        } else {
            mFollowersFragment.setNewFollowingList(followList);
            mProfileFragment.updateFollowing(followList.size());
        }
    }

    public static Intent intent(Account account, Activity activity, boolean isThisMyAccount){
        Intent i = new Intent(activity, ProfileActivity.class);
        Bundle b = new Bundle();
        b.putString(ACCOUNT_ARG, account.toJson());
        b.putBoolean(MY_ARG, isThisMyAccount);
        i.setFlags(i.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
        i.putExtras(b);

        return i;
    }

    @Override
    public void balanceClicked() {
        mProfilePager.setCurrentItem(0, true);
    }

    @Override
    public void followingClicked() {
        mProfileAdapter.swapFollowerTitle(false);
        mProfilePager.setCurrentItem(3);
        mFollowersFragment.switchViewFlipper(false);
    }

    @Override
    public void followersClicked() {
        mProfileAdapter.swapFollowerTitle(true);
        mProfilePager.setCurrentItem(3);
        mFollowersFragment.switchViewFlipper(true);
    }

    @Override
    public void postsClicked() {
        mProfilePager.setCurrentItem(1);
    }

    @Override
    public void onLoadMore(DiscussionList list) {
        if(list.getNextAuthor() != null && list.getNextTitle() != null)
            mService.getDiscussions(SteemyGlobals.getNew(), "none", list.getNextAuthor(), list.getNextTitle(), 11);
    }

    @Override
    public void vote(Discussion discussion, short vote_weight) {

    }

    @Override
    public void onRefresh() {
        if(mProfileAdapter.getCurrentFragment().equalsIgnoreCase("Replies"))
            mService.getReplies(mCurrentAccount.getName());
        else
            mService.getMyPosts(mCurrentAccount.getName());
    }

    @Override
    public void onDiscussionClicked(Discussion discussion){
        Intent newDiscussionIntent = DiscussionActivity.intent(discussion.getCategory(), discussion.getAuthor(), discussion.getPermlink(), this);
        startActivity(newDiscussionIntent);
    }

    @Override
    public void onFollowerClicked(String accountName) {
        mService.getAccount(accountName);
    }

    @Override
    public void onFollowsRequested() {
        mService.getFollowersList(mCurrentAccount, SteemyGlobals.FOLLOWER_TYPE.FOLLOWERS);
        mService.getFollowersList(mCurrentAccount, SteemyGlobals.FOLLOWER_TYPE.FOLLOWING);
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
    public int getContentView() {
        return R.layout.activity_profile;
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
