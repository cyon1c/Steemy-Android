package io.steemapp.steemy.network.interceptors;

import com.google.gson.JsonSyntaxException;

import java.io.IOException;

import io.steemapp.steemy.network.rpc.RPCResponse;
import io.steemapp.steemy.network.rpc.RPCUtil;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okio.BufferedSource;

public class RPCResponseInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        boolean retry = false;

        Request request = chain.request();
        Response response = chain.proceed(request);

        //Response was successful, but we still need to validate
        //the RPC response.
        if(response.isSuccessful()) {
            try {
                RPCResponse rpcResponse = RPCUtil.fromNetworkResponse(response);
                if(!rpcResponse.isSuccessful()){
                    retry = rpcResponse.getError().isRetriable();
                }

            } catch (Exception e) {
                if (e instanceof NullPointerException) {
                    //Network response body is null
                } else if (e instanceof IOException) {
                    //Couldn't read response body.
                } else if (e instanceof JsonSyntaxException) {
                    //Response body is not a JSON RPC Response.
                } else {
                    //Something seriously broke.
                }
            }
        }

        //TODO: RETRY AND CONNECTION CYCLING SEMANTICS

        return response;
    }
}
