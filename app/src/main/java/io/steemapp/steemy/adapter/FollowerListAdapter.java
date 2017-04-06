package io.steemapp.steemy.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import io.steemapp.steemy.R;
import io.steemapp.steemy.fragments.FollowersFragment;
import io.steemapp.steemy.listeners.OnFollowerInteractionListener;
import io.steemapp.steemy.models.Category;
import io.steemapp.steemy.models.Follower;
import io.steemapp.steemy.views.SteemyTextView;

/**
 * Created by John on 7/15/2016.
 */
public class FollowerListAdapter extends RecyclerView.Adapter<FollowerListAdapter.FollowerViewHolder>{

    private Context mContext;
    private List<Follower> mItemData;
    private OnFollowerInteractionListener mListener;
    private boolean isFollowers;

    public FollowerListAdapter(List<Follower> data, OnFollowerInteractionListener listener, boolean isFollowers) {
        super();
        mItemData = data;
        mListener = listener;
        this.isFollowers = isFollowers;
    }

    public void setContext(Context context){
        mContext = context;
    }

    @Override
    public FollowerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.follower_list_item, parent, false);
        return new FollowerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final FollowerViewHolder holder, int position) {
        holder.item = mItemData.get(position);
        if (isFollowers){
            holder.mFollowerView.setText(holder.item.getFollower());
            holder.mParentView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onFollowerClicked(holder.item.getFollower());
                }
            });
        }
        else {
            holder.mFollowerView.setText(holder.item.getFollowing());
            holder.mParentView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onFollowerClicked(holder.item.getFollowing());
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return mItemData.size();
    }

    public void setData(List<Follower> list){
        mItemData = list;
    }

    public class FollowerViewHolder extends RecyclerView.ViewHolder{
        public Follower item;
        public final View mParentView;
        public final SteemyTextView mFollowerView;

        public FollowerViewHolder(View itemView) {
            super(itemView);
            mParentView = itemView;
            mFollowerView = (SteemyTextView)itemView.findViewById(R.id.follower_name);
        }
    }
}
