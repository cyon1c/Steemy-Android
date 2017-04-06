package io.steemapp.steemy.models;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Follower {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("follower")
    @Expose
    private String follower;
    @SerializedName("following")
    @Expose
    private String following;
    @SerializedName("what")
    @Expose
    private List<String> what = new ArrayList<String>();

    /**
     *
     * @return
     * The id
     */
    public String getId() {
        return id;
    }

    /**
     *
     * @param id
     * The id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     *
     * @return
     * The follower
     */
    public String getFollower() {
        return follower;
    }

    /**
     *
     * @param follower
     * The follower
     */
    public void setFollower(String follower) {
        this.follower = follower;
    }

    /**
     *
     * @return
     * The following
     */
    public String getFollowing() {
        return following;
    }

    /**
     *
     * @param following
     * The following
     */
    public void setFollowing(String following) {
        this.following = following;
    }

    /**
     *
     * @return
     * The what
     */
    public List<String> getWhat() {
        return what;
    }

    /**
     *
     * @param what
     * The what
     */
    public void setWhat(List<String> what) {
        this.what = what;
    }

}
