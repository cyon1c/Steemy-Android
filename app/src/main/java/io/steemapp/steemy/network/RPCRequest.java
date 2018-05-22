package io.steemapp.steemy.network;

import com.google.gson.annotations.SerializedName;

import java.util.Random;

public class RPCRequest {

    private static Random idGenerator = new Random();
    private static Integer idIncremental = idGenerator.nextInt();

    @SerializedName("jsonrpc")
    private final static String jsonrpc = "2.0";

    @SerializedName("id")
    private Integer id;

    @SerializedName("params")
    private Object[] params;

    @SerializedName("method")
    private String method;

    public RPCRequest(Integer id) {
        this.id = id;
    }

    public static RPCRequest emptySequentialRequest(){
        return new RPCRequest(idIncremental++);
    }

    public static RPCRequest emptyRequest(){
        return new RPCRequest(idGenerator.nextInt());
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Object[] getParams() {
        return params;
    }

    public void setParams(Object[] params) {
        this.params = params;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }
}
