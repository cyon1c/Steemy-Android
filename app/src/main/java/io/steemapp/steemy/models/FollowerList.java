package io.steemapp.steemy.models;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FollowerList {

    @SerializedName("num_followers")
    @Expose
    private Integer numFollowers;
    @SerializedName("followers")
    @Expose
    private List<Follower> followers = new ArrayList<Follower>();

    /**
     *
     * @return
     * The id
     */
    public Integer getNumFollowers() {
        return numFollowers;
    }

    /**
     *
     * @param numFollowers
     * The id
     */
    public void setNumFollowers(Integer numFollowers) {
        this.numFollowers = numFollowers;
    }

    /**
     *
     * @return
     * The followers
     */
    public List<Follower> getFollowers() {
        return followers;
    }

    /**
     *
     * @param followers
     * The followers
     */
    public void setFollowers(List<Follower> followers) {
        this.followers = followers;
    }

}