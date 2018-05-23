package io.steemapp.steemy.network.rpc;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RPCRequest {

    private static Random idGenerator = new Random();
    private static Integer idIncremental = idGenerator.nextInt();

    @SerializedName("jsonrpc")
    private final static String jsonrpc = "2.0";

    @Expose
    @SerializedName("id")
    private Integer id;

    @Expose
    @SerializedName("params")
    private List<Object> params = new ArrayList<>();

    @Expose
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

    public static RPCRequest simpleRequest(String api, String method){
        RPCRequest simple = emptyRequest();
        simple.method = String.format("%s.%s", api, method);

        return simple;
    }

    public static RPCRequest simpleSequentialRequest(String api, String method){
        RPCRequest simple = emptySequentialRequest();
        simple.method = String.format("%s.%s", api, method);

        return simple;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<Object> getParams() {
        return params;
    }

    public void setParams(List<Object> params) {
        this.params = params;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }
}
