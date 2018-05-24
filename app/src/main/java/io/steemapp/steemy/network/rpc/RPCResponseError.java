package io.steemapp.steemy.network.rpc;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

public class RPCResponseError{

    public static final String STEEMD_UNKNOWN_ERROR = "Unknown exception";
    public static final String DB_LOCK_ERROR = "Unable to acquire database lock";
    public static final String JUSSI_ERROR = "Internal Error";

    private static final String UNSPECIFIED = "unspecified";

    public static final Integer JUSSI_ERROR_CODE = -32603;

    @SerializedName("code")
    protected Integer code;

    @SerializedName("message")
    protected String errorMessage;

    @SerializedName("data")
    protected JsonObject errorData;

    public JsonObject getErrorData() {
        return errorData;
    }

    public void setErrorData(JsonObject errorData) {
        this.errorData = errorData;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public static RPCResponseError unspecifiedError(){
        RPCResponseError error = new RPCResponseError();
        error.errorMessage = UNSPECIFIED;
        error.code = -9999;

        return error;
    }
}