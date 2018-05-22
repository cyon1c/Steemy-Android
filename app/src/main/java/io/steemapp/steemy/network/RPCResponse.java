package io.steemapp.steemy.network;

import com.google.gson.annotations.SerializedName;

public class RPCResponse {

    @SerializedName("jsonrpc")
    private String jsonrpc;

    @SerializedName("result")
    protected Object result;

    @SerializedName("error")
    protected String error;

    @SerializedName("id")
    protected Integer id;

    public RPCResponse(Object result, String error, Integer id) {
        this.result = result;
        this.error = error;
        this.id = id;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
