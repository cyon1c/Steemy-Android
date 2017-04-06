package io.steemapp.steemy.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import io.steemapp.steemy.R;
import io.steemapp.steemy.models.Discussion;
import io.steemapp.steemy.views.SteemyTextView;

/**
 * Created by John on 8/9/2016.
 */
public class CommentsListAdapter extends RecyclerView.Adapter<CommentsListAdapter.CommentViewHolder>{

    protected ArrayList<Discussion> mComments;

    public CommentsListAdapter(ArrayList<Discussion> list){
        mComments = list;
    }

    @Override
    public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CommentViewHolder holder, int position) {
        holder.mItem = mComments.get(position);
        holder.author.setText(holder.mItem.getAuthor());
        holder.timestamp.setText(holder.mItem.getTimeAgo());
        holder.webView.setWebViewClient(new WebViewClient());
        holder.webView.setWebChromeClient(new WebChromeClient());


        holder.webView.loadData(holder.mItem.getBody(), "text/html; charset=UTF-8", null);

        holder.webView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.metadataParent.setVisibility(View.VISIBLE);
            }
        });

        holder.upvoteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: Add voting capability here.
            }
        });

        holder.payout.setText(holder.mItem.getHumanReadablePayout());
        holder.votes.setText(holder.mItem.getActiveVotes().size());
    }

    @Override
    public int getItemCount() {
        return mComments.size();
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder{

        public Discussion mItem;
        public View parentView;
        public SteemyTextView author;
        public SteemyTextView timestamp;
        public WebView webView;
        public RelativeLayout metadataParent;
        public ImageView upvoteIcon;
        public SteemyTextView payout;
        public SteemyTextView votes;


        public CommentViewHolder(View parentView){
            super(parentView);
            this.parentView = parentView;
//            author = (SteemyTextView)parentView.findViewById(R.id.comment_author);
//            timestamp = (SteemyTextView)parentView.findViewById(R.id.comment_time_stamp);
            webView = (WebView)parentView.findViewById(R.id.comment_webview);
            metadataParent = (RelativeLayout)parentView.findViewById(R.id.comment_metadata);
            upvoteIcon = (ImageView)parentView.findViewById(R.id.upvote_icon);
            payout = (SteemyTextView)parentView.findViewById(R.id.comment_payout);
            votes = (SteemyTextView)parentView.findViewById(R.id.comment_votes);
        }
    }
}
