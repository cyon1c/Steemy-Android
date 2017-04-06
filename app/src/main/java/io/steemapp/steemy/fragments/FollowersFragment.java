package io.steemapp.steemy.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ViewFlipper;

import java.util.ArrayList;

import io.steemapp.steemy.R;
import io.steemapp.steemy.adapter.FollowerListAdapter;
import io.steemapp.steemy.listeners.OnFollowerInteractionListener;
import io.steemapp.steemy.models.Follower;

public class FollowersFragment extends Fragment {

    private OnFollowerInteractionListener mListener;

    private View mRootView;

    private RecyclerView mFollowersRecycler;
    private FollowerListAdapter mFollowersAdapter;
    private ArrayList<Follower> mFollowersList;

    private RecyclerView mFollowingRecycler;
    private FollowerListAdapter mFollowingAdapter;
    private ArrayList<Follower> mFollowingList;

    private ViewFlipper mFollowFlipper;

    public FollowersFragment() {
        // Required empty public constructor
        mFollowersAdapter = new FollowerListAdapter(new ArrayList<Follower>(), mListener, true);
        mFollowingAdapter = new FollowerListAdapter(new ArrayList<Follower>(), mListener, false);
    }

    public static FollowersFragment newInstance() {
        FollowersFragment fragment = new FollowersFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
        mFollowersAdapter.setContext(getActivity());
        mFollowingAdapter.setContext(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mRootView = inflater.inflate(R.layout.fragment_followers, container, false);

        mFollowersRecycler = (RecyclerView)mRootView.findViewById(R.id.followers_recycler);
        mFollowersRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        mFollowersRecycler.setAdapter(mFollowersAdapter);


        mFollowingRecycler = (RecyclerView)mRootView.findViewById(R.id.following_recycler);
        mFollowingRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        mFollowingRecycler.setAdapter(mFollowingAdapter);

        mFollowFlipper = (ViewFlipper)mRootView.findViewById(R.id.follower_flipper);

        return mRootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFollowerInteractionListener) {
            mListener = (OnFollowerInteractionListener) context;
            if(mFollowersList == null || mFollowingList == null){
                mListener.onFollowsRequested();
            }
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void switchViewFlipper(boolean followers){
        if(followers){
            mFollowFlipper.setDisplayedChild(0);
        }else{
            mFollowFlipper.setDisplayedChild(1);
        }
    }

    public void setNewFollowerList(ArrayList<Follower> list){
        mFollowersList = list;
        mFollowersAdapter.setData(mFollowersList);
        mFollowersAdapter.notifyDataSetChanged();
    }

    public void setNewFollowingList(ArrayList<Follower> list){
        mFollowingList = list;
        mFollowingAdapter.setData(mFollowingList);
        mFollowingAdapter.notifyDataSetChanged();
    }
}
