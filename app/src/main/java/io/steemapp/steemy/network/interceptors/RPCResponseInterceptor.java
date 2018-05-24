package io.steemapp.steemy.network.interceptors;

import com.google.gson.JsonSyntaxException;

import java.io.IOException;

import io.steemapp.steemy.network.rpc.RPCResponse;
import io.steemapp.steemy.network.rpc.RPCResponseError;
import io.steemapp.steemy.network.rpc.RPCUtil;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import static io.steemapp.steemy.network.rpc.RPCResponseError.DB_LOCK_ERROR;
import static io.steemapp.steemy.network.rpc.RPCResponseError.JUSSI_ERROR;
import static io.steemapp.steemy.network.rpc.RPCResponseError.JUSSI_ERROR_CODE;
import static io.steemapp.steemy.network.rpc.RPCResponseError.STEEMD_UNKNOWN_ERROR;

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
                    handleRPCError(rpcResponse.getError());
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

    private void handleRPCError(RPCResponseError error){
        if(error.getCode() != null){
            //TODO: Handle RPC Errors
            switch(error.getCode()){
                default:

            }
        }

        boolean retry = isRetriable(error);
    }

    private boolean isRetriable(RPCResponseError error){
        return (error.getErrorMessage().contains(DB_LOCK_ERROR)
                || error.getErrorMessage().contains(STEEMD_UNKNOWN_ERROR)
                || (error.getErrorMessage().contains(JUSSI_ERROR)
                    && error.getCode().equals(JUSSI_ERROR_CODE)));
    }
}
