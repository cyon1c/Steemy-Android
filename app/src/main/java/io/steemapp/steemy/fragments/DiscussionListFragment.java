package io.steemapp.steemy.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import io.steemapp.steemy.R;
import io.steemapp.steemy.adapter.DiscussionListAdapter;
import io.steemapp.steemy.listeners.EndlessRecyclerViewScrollListener;
import io.steemapp.steemy.listeners.OnDiscussionListInteractionListener;
import io.steemapp.steemy.models.Discussion;
import io.steemapp.steemy.models.DiscussionList;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnDiscussionListInteractionListener}
 * interface.
 */
public class DiscussionListFragment extends Fragment {

    private OnDiscussionListInteractionListener mListener;


    private View mRootView;

    private SwipeRefreshLayout mRefreshLayout;
    private RecyclerView mRecyclerView;
    private DiscussionListAdapter mAdapter;
    private DiscussionList mDiscussionList;

    public DiscussionListFragment() {
        mAdapter = new DiscussionListAdapter(new ArrayList<Discussion>(), mListener);
    }

    @SuppressWarnings("unused")
    public static DiscussionListFragment newInstance() {
        DiscussionListFragment fragment = new DiscussionListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter.setContext(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_home_list, container, false);

        mRefreshLayout = (SwipeRefreshLayout)mRootView.findViewById(R.id.swipe_refresh_list);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mListener.onRefresh();
            }
        });
//        mRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark);
        mRecyclerView = (RecyclerView)mRootView.findViewById(R.id.discussion_list_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItems) {
                mListener.onLoadMore(mDiscussionList);
            }
        });

        mRecyclerView.setAdapter(mAdapter);

        return mRootView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnDiscussionListInteractionListener) {
            mListener = (OnDiscussionListInteractionListener) context;
            mAdapter.setListener(mListener);
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */

    public void setNewList(DiscussionList list){
        mDiscussionList = list;
        mAdapter.setData(mDiscussionList.getDiscussionList());
        mAdapter.notifyDataSetChanged();
    }

    public void updateListData(DiscussionList list){
        mDiscussionList = list;
        mAdapter.updateData(mDiscussionList.getDiscussionList());
        mAdapter.notifyDataSetChanged();
    }

    public void statelessUpdate(DiscussionList list){
        if(mDiscussionList == null){
            mDiscussionList = list;
            mAdapter.setData(mDiscussionList.getDiscussionList());
            mAdapter.notifyDataSetChanged();
        }else{
            mDiscussionList = list;
            mAdapter.updateData(mDiscussionList.getDiscussionList());
            mAdapter.notifyDataSetChanged();
        }
    }

    public void finishRefreshing(){
        if(mRefreshLayout != null)
            mRefreshLayout.setRefreshing(false);
    }
}
