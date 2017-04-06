package io.steemapp.steemy.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class GlobalResults {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("result")
    @Expose
    private BlockchainGlobals blockchainGlobals;

    /**
     *
     * @return
     * The id
     */
    public Integer getId() {
        return id;
    }

    /**
     *
     * @param id
     * The id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     *
     * @return
     * The blockchainGlobals
     */
    public BlockchainGlobals getBlockchainGlobals() {
        return blockchainGlobals;
    }

    /**
     *
     * @param blockchainGlobals
     * The blockchainGlobals
     */
    public void setBlockchainGlobals(BlockchainGlobals blockchainGlobals) {
        this.blockchainGlobals = blockchainGlobals;
    }
}

