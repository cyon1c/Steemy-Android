package io.steemapp.steemy.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import io.steemapp.steemy.R;
import io.steemapp.steemy.listeners.OnDiscussionListInteractionListener;
import io.steemapp.steemy.models.AccountManager;
import io.steemapp.steemy.models.Discussion;
import io.steemapp.steemy.views.SteemyTextView;

import java.util.List;

@SuppressWarnings("deprecation")
public class DiscussionListAdapter extends RecyclerView.Adapter<DiscussionListAdapter.BasePostViewHolder> {

    private List<Discussion> mList;
    private OnDiscussionListInteractionListener mListener;
    private Context mContext;
    private String currentAccount;

    public DiscussionListAdapter(List<Discussion> items, OnDiscussionListInteractionListener listener) {
        super();
        mList = items;
        mListener = listener;
        currentAccount = AccountManager.get(null, null).getAccountName();
    }

    public void setContext(Context context){
        mContext = context;
    }

    @Override
    public int getItemViewType(int position) {
        if(!mList.get(position).getJsonMetadata().contains("\"image\":")){
            return 0;
        }else{
            return 1;
        }
    }

    @Override
    public BasePostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == 1) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.image_post, parent, false);
            return new ImagePostViewHolder(view);
        }
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.base_post, parent, false);
        return new BasePostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final BasePostViewHolder holder, int position) {
        holder.mItem = mList.get(position);
        holder.mTitleView.setText(holder.mItem.getTitle());
        String body = Html.fromHtml(holder.mItem.getBody()).toString().replace('\n', (char) 32)
                .replace((char) 160, (char) 32).replace((char) 65532, (char) 32).trim();
        holder.mTeaserView.setText(body);
        holder.mPayoutView.setText(holder.mItem.getHumanReadablePayout());
        holder.mAuthorView.setText(holder.mItem.getAuthor());
        holder.mCategoryView.setText(holder.mItem.getCategory());
        holder.mTimeView.setText(holder.mItem.getTimeAgo());
        holder.mVotesView.setText(Integer.toString(holder.mItem.getActiveVotes().size()) + " Votes");
        holder.mCommentsView.setText(holder.mItem.getChildren().toString() + " Comments");
        if(holder instanceof ImagePostViewHolder) {
            ImagePostViewHolder iHolder = (ImagePostViewHolder)holder;
//            mPicasso
//                    .load(holder.mItem.getTeaserImage())
//                    .into(iHolder.mImageView);
            Glide.with(mContext)
                    .load(holder.mItem.getTeaserImage())
                    .crossFade()
                    .into(iHolder.mImageView);
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onDiscussionClicked(holder.mItem);
                }
            }
        });

        if(currentAccount != null && holder.mItem.hasVoted(currentAccount))
            holder.mUpvote.setSelected(true);
        else
            holder.mUpvote.setSelected(false);

        holder.mUpvote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mListener != null){
                    if(!holder.mUpvote.isSelected()) {
                        mListener.vote(holder.mItem, (short)10000);
                        holder.mUpvote.setSelected(true);
                    }else{
                        mListener.vote(holder.mItem, (short)0);
                        holder.mUpvote.setSelected(false);
                    }
                }
            }
        });

        holder.mUpvote.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mListener.customVoteRequested(holder.mItem, true);
                return true;
            }
        });
    }

    public void setData(List<Discussion> list){
        mList = list;
    }

    public void updateData(List<Discussion> list){
        mList.addAll(list);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class BasePostViewHolder extends RecyclerView.ViewHolder {
        public View mView;
        public SteemyTextView mTitleView;
        public SteemyTextView mTeaserView;
        public SteemyTextView mPayoutView;
        public SteemyTextView mTimeView;
        public SteemyTextView mAuthorView;
        public SteemyTextView mCategoryView;
        public SteemyTextView mVotesView;
        public SteemyTextView mCommentsView;
        public ImageView mUpvote;
        public Discussion mItem;

        public BasePostViewHolder(View view) {
            super(view);
            mView = view;
            mUpvote = (ImageView) view.findViewById(R.id.upvote_icon);
            mTitleView = (SteemyTextView) view.findViewById(R.id.discussion_title);
            mTeaserView = (SteemyTextView) view.findViewById(R.id.discussion_teaser);
            mPayoutView = (SteemyTextView) view.findViewById(R.id.discussion_payout);
            mTimeView = (SteemyTextView) view.findViewById(R.id.discussion_time_stamp);
            mAuthorView = (SteemyTextView) view.findViewById(R.id.discussion_author);
            mCategoryView = (SteemyTextView)view.findViewById(R.id.discussion_category);
            mVotesView = (SteemyTextView)view.findViewById(R.id.discussion_votes);
            mCommentsView = (SteemyTextView) view.findViewById(R.id.discussion_comments);

        }
    }

    public class ImagePostViewHolder extends BasePostViewHolder {
        public ImageView mImageView;


        public ImagePostViewHolder(View view) {
            super(view);
            mImageView = (ImageView) view.findViewById(R.id.discussion_image);


        }
    }

    public void setListener(OnDiscussionListInteractionListener ls){
        mListener = ls;
    }
}
