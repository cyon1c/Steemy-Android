package io.steemapp.steemy.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import io.steemapp.steemy.R;
import io.steemapp.steemy.SteemyGlobals;
import io.steemapp.steemy.adapter.FollowerListAdapter;
import io.steemapp.steemy.listeners.OnFollowerInteractionListener;
import io.steemapp.steemy.models.Follower;
import io.steemapp.steemy.views.SteemyButton;
import io.steemapp.steemy.views.SteemyTextView;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ManageAccountFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ManageAccountFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ManageAccountFragment extends Fragment implements OnFollowerInteractionListener {

    private OnFragmentInteractionListener mListener;

    protected SteemyTextView mPostingKey, mActiveKey;
    protected SteemyButton mActiveReveal, mPostingReveal, mAddRemovePosting, mAddRemoveActive;

    protected RecyclerView mIgnoredRecycler;
    protected ArrayList<Follower> mIgnoredData;
    protected FollowerListAdapter mIgnoredAdapter;

    protected View mRootView;

    protected boolean mAPrivateShowing = false;
    protected boolean mPPrivateShowing = false;

    public ManageAccountFragment() {
        // Required empty public constructor
        mIgnoredAdapter = new FollowerListAdapter(new ArrayList<Follower>(), this, false);
    }

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_ACTIVE = "active_public";
    private static final String ARG_POSTING = "posting_public";
    private static final String ARG_ACTIVE_PRIVATE = "active_private";
    private static final String ARG_POSTING_PRIVATE = "posting_private";

    // TODO: Rename and change types of parameters
    private Pair<String, String> mActive;
    private Pair<String, String> mPosting;

    public static ManageAccountFragment newInstance(Pair<String, String> active, Pair<String, String> posting) {
        ManageAccountFragment fragment = new ManageAccountFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ACTIVE, active.first);
        args.putString(ARG_POSTING, posting.first);
        args.putString(ARG_ACTIVE_PRIVATE, active.second);
        args.putString(ARG_POSTING_PRIVATE, posting.second);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mActive = new Pair<>(getArguments().getString(ARG_ACTIVE), getArguments().getString(ARG_ACTIVE_PRIVATE));
            mPosting = new Pair<>(getArguments().getString(ARG_POSTING), getArguments().getString(ARG_POSTING_PRIVATE));
        }
        mIgnoredAdapter.setContext(this.getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_manage_account, container, false);

        initViews();

        return mRootView;
    }

    protected void initViews() {
        mPostingKey = (SteemyTextView) mRootView.findViewById(R.id.posting_key_text);
        mActiveKey = (SteemyTextView) mRootView.findViewById(R.id.active_key_text);
        mActiveReveal = (SteemyButton) mRootView.findViewById(R.id.show_active);
        mPostingReveal = (SteemyButton) mRootView.findViewById(R.id.show_posting);
        mAddRemoveActive = (SteemyButton) mRootView.findViewById(R.id.add_remove_active);
        mAddRemovePosting = (SteemyButton) mRootView.findViewById(R.id.add_remove_posting);
        mIgnoredRecycler = (RecyclerView) mRootView.findViewById(R.id.ignored_users_recycler);

        mActiveKey.setText(mActive.first);
        if (mActive.second == null) {
            mAddRemoveActive.setText("Import Key");
        } else
            mAddRemoveActive.setText("Remove Key");
        mActiveReveal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mAPrivateShowing) {
                    mActiveReveal.setText("Show Public");
                    if (mActive.second != null)
                        mActiveKey.setText(mActive.second);
                    else
                        mActiveKey.setText("No Private Key Imported");
                    mAPrivateShowing = true;
                } else {
                    mActiveReveal.setText("Show Private");
                    mActiveKey.setText(mActive.first);
                    mAPrivateShowing = false;
                }
            }
        });
        mAddRemoveActive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mActive.second == null) {
                    mListener.importKey(SteemyGlobals.KEY_TYPE.ACTIVE);
                } else {
                    mListener.removeActive();
                    mActive = new Pair<String, String>(mActive.first, null);
                    mAddRemoveActive.setText("Import Key");
                }

            }
        });


        mPostingKey.setText(mPosting.first);
        if (mPosting.second == null) {
            mAddRemovePosting.setText("Import Key");
        } else
            mAddRemovePosting.setText("Remove Key");
        mPostingReveal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mPPrivateShowing) {
                    mPostingReveal.setText("Show Public");
                    if (mPosting.second != null)
                        mPostingKey.setText(mPosting.second);
                    else
                        mPostingKey.setText("No Private Key Imported");
                    mPPrivateShowing = true;
                } else {
                    mPostingReveal.setText("Show Private");
                    mPostingKey.setText(mPosting.first);
                    mPPrivateShowing = false;
                }
            }
        });
        mAddRemovePosting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPosting.second == null) {
                    mListener.importKey(SteemyGlobals.KEY_TYPE.POSTING);
                } else {
                    mListener.removePosting();
                    mPosting = new Pair<String, String>(mPosting.first, null);
                    mAddRemovePosting.setText("Import Key");
                }
            }
        });

        mIgnoredRecycler.setAdapter(mIgnoredAdapter);
        mIgnoredRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        mListener.ignoredRequested();
    }

    public void setIgnoredList(ArrayList<Follower> data) {
        mIgnoredData = data;
        mIgnoredAdapter.setData(mIgnoredData);
        mIgnoredAdapter.notifyDataSetChanged();
    }

    public void updateImported(Pair<String, String> active, Pair<String, String> posting){
        mActive = active;
        mPosting = posting;
        if (mActive.second == null) {
            mAddRemoveActive.setText("Import Key");
        } else
            mAddRemoveActive.setText("Remove Key");
        if (mPosting.second == null) {
            mAddRemovePosting.setText("Import Key");
        } else
            mAddRemovePosting.setText("Remove Key");
    }

    @Override
    public void onFollowsRequested() {

    }

    @Override
    public void onFollowerClicked(String accountName) {
        mListener.ignoredUserClicked(accountName);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
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

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void removeActive();

        void removePosting();

        void importKey(SteemyGlobals.KEY_TYPE key_type);

        void ignoredUserClicked(String account);

        void ignoredRequested();
    }
}
