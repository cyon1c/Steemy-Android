package io.steemapp.steemy.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by John on 8/20/2016.
 */
public class ChainResult {

    @Expose
    @SerializedName("id")
    private int id;
    @Expose
    @SerializedName("result")
    private ChainProperties mProperties;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ChainProperties getmProperties() {
        return mProperties;
    }

    public void setmProperties(ChainProperties mProperties) {
        this.mProperties = mProperties;
    }
}
