package io.steemapp.steemy.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import io.steemapp.steemy.R;
import io.steemapp.steemy.views.SteemyTextView;

/**
 * Created by john.white on 8/31/16.
 */
public class TagListAdapter extends RecyclerView.Adapter<TagListAdapter.TagViewHolder> {

    private ArrayList<String> mTags = new ArrayList<>(0);

    public TagListAdapter() {
        super();
    }

    @Override
    public void onBindViewHolder(final TagViewHolder holder, final int position) {
        holder.tagName.setText(mTags.get(position));
        holder.tag = mTags.get(position);
        holder.xButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTags.remove(holder.tag);
                notifyItemRemoved(position);
            }
        });
    }

    @Override
    public TagViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tag_view, parent, false);
        return new TagViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return mTags.size();
    }

    public void addTag(String tag){
        mTags.add(tag);
        notifyItemInserted(mTags.size()-1);
    }

    public ArrayList<String> getData(){return mTags;}

    public class TagViewHolder extends RecyclerView.ViewHolder{
        public SteemyTextView tagName;
        public ImageView xButton;
        public String tag;

        public TagViewHolder(View itemView) {
            super(itemView);
            tagName = (SteemyTextView)itemView.findViewById(R.id.tag_name);
            xButton = (ImageView)itemView.findViewById(R.id.remove_tag_button);
        }
    }
}
