package io.steemapp.steemy.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by John on 8/4/2016.
 */
public class AccountResult{
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("result")
    @Expose
    private List<Account> account = new ArrayList<Account>();

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
     * The result
     */
    public List<Account> getAccounts() {
        return account;
    }

    /**
     *
     * @param account
     * The result
     */
    public void setAccount(List<Account> account) {
        this.account = account;
    }

}