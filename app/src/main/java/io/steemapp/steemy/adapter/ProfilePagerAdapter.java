package io.steemapp.steemy.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import io.steemapp.steemy.fragments.DiscussionListFragment;
import io.steemapp.steemy.fragments.FollowersFragment;
import io.steemapp.steemy.fragments.WalletFragment;

/**
 * Created by John on 8/19/2016.
 */
public class ProfilePagerAdapter extends FragmentPagerAdapter {

    WalletFragment mWalletF;
    DiscussionListFragment mMyPostsF;
    DiscussionListFragment mRepliesF;
    FollowersFragment mFollowersF;

    private String mWalletTitle = "Wallet";
    private String mMyPostsTitle = "Blog Posts";
    private String mRepliesTitle = "Replies";
    private String mFollowTitle = "Followers";

    private int currentPostion;

    public ProfilePagerAdapter(FragmentManager fm, WalletFragment wf, DiscussionListFragment mpf, DiscussionListFragment rf, FollowersFragment ff) {
        super(fm);
        this.
        mWalletF = wf;
        mMyPostsF = mpf;
        mRepliesF = rf;
        mFollowersF = ff;
    }

    @Override
    public Fragment getItem(int position) {
        currentPostion = position;
        switch(position){
            case 0:
                return mWalletF;
            case 1:
                return mMyPostsF;
            case 2:
                return mRepliesF;
            case 3:
                return mFollowersF;
        }
        return null;
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch(position){
            case 0:
                return mWalletTitle;
            case 1:
                return mMyPostsTitle;
            case 2:
                return mRepliesTitle;
            case 3:
                return mFollowTitle;
        }
        return null;
    }

    public String getCurrentFragment(){
        return getPageTitle(currentPostion).toString();
    }

    public void swapFollowerTitle(boolean followers){
        if(followers)
            mFollowTitle = "Followers";
        else
            mFollowTitle = "Following";

        notifyDataSetChanged();
    }
}
