package io.steemapp.steemy.network.rpc;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RPCRequest {

    private static Random idGenerator = new Random();
    private static Integer idIncremental = idGenerator.nextInt();

    @Expose
    @SerializedName("jsonrpc")
    private final String jsonrpc = "2.0";

    @Expose
    @SerializedName("id")
    private Integer id;

    @Expose
    @SerializedName("params")
    private List<JsonObject> params;

    @Expose
    @SerializedName("method")
    private String method;

    public RPCRequest(Integer id) {
        this.id = id;
        params = new ArrayList<>();
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

    public List<JsonObject> getParams() {
        return params;
    }

    public void setParams(List<JsonObject> params) {
        this.params = params;
    }

    public void addParam(Object param){
        Gson gson = new Gson();
        addJsonParam((JsonObject)gson.toJsonTree(param));
    }

    public void addJsonParam(JsonObject json){
        if(params == null)
            params = new ArrayList<>();

        params.add(json);
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }
}
