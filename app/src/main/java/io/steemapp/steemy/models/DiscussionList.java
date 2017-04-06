package io.steemapp.steemy.models;

import android.content.Context;
import android.util.Log;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class DiscussionList {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("result")
    @Expose
    private List<Discussion> mDiscussionList = new ArrayList<Discussion>();

    private String mNextAuthor, mNextTitle;

    public String getNextAuthor() {
        return mNextAuthor;
    }

    public String getNextTitle() {
        return mNextTitle;
    }

    /**
     * @return The id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id The id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return The result
     */
    public List<Discussion> getDiscussionList() {
        return mDiscussionList;
    }

    /**
     * @param mDiscussionList The result
     */
    public void setDiscussionList(List<Discussion> mDiscussionList) {
        this.mDiscussionList = mDiscussionList;
    }

    public void processList(int request_size) {
        for (Discussion d : mDiscussionList) {
            d.formatDiscussion();
        }
        if (mDiscussionList.size() >= request_size) {
            Discussion last = mDiscussionList.get(mDiscussionList.size() - 1);
            mNextAuthor = last.getAuthor();
            mNextTitle = last.getPermlink();
            mDiscussionList.remove(mDiscussionList.size() - 1);
        }
    }

}