package io.steemapp.steemy.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by John on 8/20/2016.
 */
public class ChainProperties {

    @Expose
    @SerializedName("account_creation_fee")
    private String mAccountCreationFee;
    @Expose
    @SerializedName("maximum_block_size")
    private int mMaxBlockSize;
    @Expose
    @SerializedName("account_creation_fee")
    private int sbdInterestRate;

    public String getmAccountCreationFee() {
        return mAccountCreationFee;
    }

    public void setmAccountCreationFee(String mAccountCreationFee) {
        this.mAccountCreationFee = mAccountCreationFee;
    }

    public int getmMaxBlockSize() {
        return mMaxBlockSize;
    }

    public void setmMaxBlockSize(int mMaxBlockSize) {
        this.mMaxBlockSize = mMaxBlockSize;
    }

    public int getSbdInterestRate() {
        return sbdInterestRate;
    }

    public void setSbdInterestRate(int sbdInterestRate) {
        this.sbdInterestRate = sbdInterestRate;
    }
}
