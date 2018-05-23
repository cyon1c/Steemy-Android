package io.steemapp.steemy.network.rpc;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

public class RPCResponse {

    @SerializedName("jsonrpc")
    private String jsonrpc;

    @SerializedName("result")
    protected JsonObject result;

    @SerializedName("error")
    protected RPCResponseError error;

    @SerializedName("id")
    protected Integer id;

    protected boolean success = false;

    protected RPCResponse() {

    }

    public boolean isSuccessful(){
        return success;
    }

    public JsonObject getResult() {
        return result;
    }

    public void setResult(JsonObject result) {
        this.result = result;
    }

    public RPCResponseError getError() {
        return error;
    }

    public void setError(RPCResponseError error) {
        this.error = error;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getErrorCode(){
        return error != null ? error.code : null;
    }

    public String getErrorMessage(){
        return error != null ? error.errorMessage : null;
    }

    public JsonObject getErrorData(){
        return error != null ? error.errorData : null;
    }
}
