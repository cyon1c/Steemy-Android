package io.steemapp.steemy.fragments;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.math.RoundingMode;
import java.text.DecimalFormat;

import io.steemapp.steemy.R;
import io.steemapp.steemy.views.SteemyTextView;

/**
 * A placeholder fragment containing a simple view.
 */
public class ProfileFragment extends Fragment {

    private OnProfileFragmentInteraction mListener;

    private String mProfileName;
    private String mSteemBalance;
    private int mFollowersNum;
    private int mFollowingNum;
    private int mPostNum;
    private int mAccountRep;

    private static String ACCOUNT_NAME = "name";
    private static String ACCOUNT_BALANCE = "balance";
    private static String ACCOUNT_FOLLOWERS = "followers";
    private static String ACCOUNT_FOLLOWING = "following";
    private static String ACCOUNT_POSTS = "my_posts";
    private static String ACCOUNT_REP = "reputation";


    private View mRootView;
    private SteemyTextView mProfileView;
    private LinearLayout mBalanceView;
    private LinearLayout mPostsView;
    private LinearLayout mFollowersView;
    private LinearLayout mFollowingView;

    public ProfileFragment() {

    }

    public static ProfileFragment getNewInstance(Bundle b){
        ProfileFragment fragment = new ProfileFragment();
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mProfileName = getArguments().getString(ACCOUNT_NAME);
            mSteemBalance = getArguments().getString(ACCOUNT_BALANCE);
            mFollowersNum = getArguments().getInt(ACCOUNT_FOLLOWERS);
            mFollowingNum = getArguments().getInt(ACCOUNT_FOLLOWING);
            mPostNum = getArguments().getInt(ACCOUNT_POSTS);
            mAccountRep = getArguments().getInt(ACCOUNT_REP);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_profile, container, false);

        initViews();

        return mRootView;
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        try {
            mListener = (OnProfileFragmentInteraction) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnProfileFragmentInteraction");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void initViews(){
        mProfileView = (SteemyTextView)mRootView.findViewById(R.id.profile_name);
        mProfileView.setText(getformattedProfileName());

        mBalanceView = (LinearLayout) mRootView.findViewById(R.id.profile_steem);
        ((SteemyTextView)mBalanceView.getChildAt(0)).setText(mSteemBalance);
        ((SteemyTextView)mBalanceView.getChildAt(1)).setText("Steem\nDollars");
        mBalanceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.balanceClicked();
            }
        });

        mFollowersView = (LinearLayout) mRootView.findViewById(R.id.profile_followers);
        ((SteemyTextView)mFollowersView.getChildAt(0)).setText(Integer.toString(mFollowersNum));
        ((SteemyTextView)mFollowersView.getChildAt(1)).setText("Followers");
        mFollowersView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.followersClicked();
            }
        });
        mFollowingView = (LinearLayout) mRootView.findViewById(R.id.profile_following);
        ((SteemyTextView)mFollowingView.getChildAt(0)).setText(Integer.toString(mFollowingNum));
        ((SteemyTextView)mFollowingView.getChildAt(1)).setText("Following");
        mFollowingView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.followingClicked();
            }
        });
        mPostsView = (LinearLayout) mRootView.findViewById(R.id.profile_posts);
        ((SteemyTextView)mPostsView.getChildAt(0)).setText(Integer.toString(mPostNum));
        ((SteemyTextView)mPostsView.getChildAt(1)).setText("Posts");
        mPostsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.postsClicked();
            }
        });
    }

    private String getformattedProfileName(){
        return "@" + mProfileName + " (" + mAccountRep + ")";
    }

    public static Bundle buildBundle(String account, String balance, int followers, int following, int posts, int rep){
        Bundle b = new Bundle();
        b.putString(ACCOUNT_NAME, account);
        double value = Double.parseDouble(balance);
        DecimalFormat df = new DecimalFormat("#.00");
        df.setRoundingMode(RoundingMode.CEILING);
        balance = df.format(value);
        b.putString(ACCOUNT_BALANCE, balance);
        b.putInt(ACCOUNT_FOLLOWERS, followers);
        b.putInt(ACCOUNT_FOLLOWING, following);
        b.putInt(ACCOUNT_POSTS, posts);
        b.putInt(ACCOUNT_REP, rep);

        return b;
    }

    public interface OnProfileFragmentInteraction{
        public void postsClicked();
        public void followersClicked();
        public void followingClicked();
        public void balanceClicked();
    }

    public void updateFollowers(int followers){
        mFollowersNum = followers;
        ((SteemyTextView)mFollowersView.getChildAt(0)).setText(Integer.toString(mFollowersNum));
    }

    public void updateFollowing(int following){
        mFollowingNum = following;
        ((SteemyTextView)mFollowingView.getChildAt(0)).setText(Integer.toString(mFollowingNum));
    }
}
