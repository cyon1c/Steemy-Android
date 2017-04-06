package io.steemapp.steemy.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import io.steemapp.steemy.R;
import io.steemapp.steemy.listeners.VoteDialogListener;
import io.steemapp.steemy.models.Discussion;
import io.steemapp.steemy.views.SteemyTextView;

/**
 * Created by John on 10/28/2016.
 */

public class VoteDialogFragment extends DialogFragment {

    public static VoteDialogFragment newInstance(Discussion discussion, boolean upvote){
        VoteDialogFragment f = new VoteDialogFragment();
        Bundle b = new Bundle();
        b.putString("title", discussion.getTitle());
        b.putString("author", discussion.getAuthor());
        b.putString("permlink", discussion.getPermlink());
        b.putBoolean("vote", upvote);

        f.setArguments(b);

        return f;
    }

    public VoteDialogFragment() {
        super();
    }

    protected VoteDialogListener mListener;
    protected boolean upvote;
    protected String title;
    protected String author;
    protected String permlink;
    protected View mRootView;
    protected short mVoteValue;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            upvote = getArguments().getBoolean("vote");
            title = getArguments().getString("title");
            permlink = getArguments().getString("permlink");
            author = getArguments().getString("author");
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof VoteDialogListener) {
            mListener = (VoteDialogListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement VoteDialogListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.widget_vote, container, false);

        getDialog().setTitle((upvote ? "Upvote: " : "Downvote: ") + title);
        SeekBar voteBar = (SeekBar)mRootView.findViewById(R.id.vote_seek_bar);
        voteBar.setMax(100);
        final SteemyTextView currentVoteValue = ((SteemyTextView) mRootView.findViewById(R.id.vote_seek_current));
        voteBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mVoteValue = (short)i;
                currentVoteValue.setText(mVoteValue);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        ((SteemyTextView) mRootView.findViewById(R.id.seek_bar_max)).setText(upvote? "+100%" : "-100%");
        mRootView.findViewById(R.id.vote_cancel_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VoteDialogFragment.this.dismiss();
            }
        });

        mRootView.findViewById(R.id.vote_positive_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VoteDialogFragment.this.dismiss();
                mListener.onCustomVoteSubmitted(author, permlink, (short)(upvote ? mVoteValue : -1*mVoteValue));
            }
        });

        return mRootView;
    }
}
