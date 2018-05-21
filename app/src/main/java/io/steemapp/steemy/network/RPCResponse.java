package io.steemapp.steemy.network;

import com.google.gson.annotations.SerializedName;

public class RPCResponse {

    @SerializedName("result")
    private Object result;

    @SerializedName("error")
    private String error;

    @SerializedName("id")
    private Integer id;

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
