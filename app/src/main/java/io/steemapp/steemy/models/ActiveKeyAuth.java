package io.steemapp.steemy.models;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by John on 8/4/2016.
 */
public class ActiveKeyAuth {

    @SerializedName("weight_threshold")
    @Expose
    private Integer weightThreshold;
    @SerializedName("account_auths")
    @Expose
    private List<Object> accountAuths = new ArrayList<Object>();
    @SerializedName("key_auths")
    @Expose
    private List<List<String>> keyAuths = new ArrayList<List<String>>();

    /**
     *
     * @return
     * The weightThreshold
     */
    public Integer getWeightThreshold() {
        return weightThreshold;
    }

    /**
     *
     * @param weightThreshold
     * The weight_threshold
     */
    public void setWeightThreshold(Integer weightThreshold) {
        this.weightThreshold = weightThreshold;
    }

    /**
     *
     * @return
     * The accountAuths
     */
    public List<Object> getAccountAuths() {
        return accountAuths;
    }

    /**
     *
     * @param accountAuths
     * The account_auths
     */
    public void setAccountAuths(List<Object> accountAuths) {
        this.accountAuths = accountAuths;
    }

    /**
     *
     * @return
     * The keyAuths
     */
    public List<List<String>> getKeyAuths() {
        return keyAuths;
    }

    /**
     *
     * @param keyAuths
     * The key_auths
     */
    public void setKeyAuths(List<List<String>> keyAuths) {
        this.keyAuths = keyAuths;
    }

}