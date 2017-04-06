package io.steemapp.steemy.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ActiveVote {

    @SerializedName("voter")
    @Expose
    private String voter;
    @SerializedName("weight")
    @Expose
    private String weight;
    @SerializedName("rshares")
    @Expose
    private Long rshares;
    @SerializedName("percent")
    @Expose
    private Integer percent;
    @SerializedName("time")
    @Expose
    private String time;

    /**
     *
     * @return
     * The voter
     */
    public String getVoter() {
        return voter;
    }

    /**
     *
     * @param voter
     * The voter
     */
    public void setVoter(String voter) {
        this.voter = voter;
    }

    /**
     *
     * @return
     * The weight
     */
    public String getWeight() {
        return weight;
    }

    /**
     *
     * @param weight
     * The weight
     */
    public void setWeight(String weight) {
        this.weight = weight;
    }

    /**
     *
     * @return
     * The rshares
     */
    public Long getRshares() {
        return rshares;
    }

    /**
     *
     * @param rshares
     * The rshares
     */
    public void setRshares(Long rshares) {
        this.rshares = rshares;
    }

    /**
     *
     * @return
     * The percent
     */
    public Integer getPercent() {
        return percent;
    }

    /**
     *
     * @param percent
     * The percent
     */
    public void setPercent(Integer percent) {
        this.percent = percent;
    }

    /**
     *
     * @return
     * The time
     */
    public String getTime() {
        return time;
    }

    /**
     *
     * @param time
     * The time
     */
    public void setTime(String time) {
        this.time = time;
    }

}
