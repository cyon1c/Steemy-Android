package io.steemapp.steemy.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import java.text.SimpleDateFormat;
import java.util.List;

import io.steemapp.steemy.R;
import io.steemapp.steemy.models.Category;
import io.steemapp.steemy.views.SteemyTextView;

/**
 * Created by John on 7/15/2016.
 */
public class CategoryListAdapter extends RecyclerView.Adapter<CategoryListAdapter.CategoryViewHolder>{

    private Context mContext;
    private List<Category> mItemData;
    private CategorySelectedListener mListener;

    public CategoryListAdapter(Context context, List<Category> data, CategorySelectedListener listener) {
        super();
        mContext = context;
        mItemData = data;
        mListener = listener;
    }

    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.category_list_item, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CategoryViewHolder holder, int position) {
        holder.item = mItemData.get(position);
        holder.mCategoryView.setText(holder.item.getName());
        holder.mCategoryDiscussions.setText(holder.item.getDiscussions().toString() + " Discussions");
        holder.mCategoryTimeStamp.setText(holder.item.getTime());
        holder.mParentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.itemClicked(holder.item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mItemData.size();
    }

    public void updateItemList(List<Category> items){
        mItemData = items;
    }

    public interface CategorySelectedListener{
        public void itemClicked(Category item);
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder{
        public Category item;
        public final View mParentView;
        public final SteemyTextView mCategoryView;
        public final SteemyTextView mCategoryDiscussions;
        public final SteemyTextView mCategoryTimeStamp;

        public CategoryViewHolder(View itemView) {
            super(itemView);
            mParentView = itemView;
            mCategoryView = (SteemyTextView)itemView.findViewById(R.id.category_title);
            mCategoryDiscussions = (SteemyTextView)itemView.findViewById(R.id.category_discussions);
            mCategoryTimeStamp = (SteemyTextView)itemView.findViewById(R.id.category_time_stamp);
        }
    }
}
