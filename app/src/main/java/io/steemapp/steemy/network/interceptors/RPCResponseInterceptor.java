package io.steemapp.steemy.network.interceptors;

import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;

import io.steemapp.steemy.network.RPCResponse;
import okhttp3.Interceptor;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.BufferedSource;

public class RPCResponseInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response response = chain.proceed(chain.request());

        try {
            final BufferedSource responseSource = response.body().source();
            responseSource.request(Integer.MAX_VALUE);
//            RPCResponse rpcResponse = new Gson().fromJson(responseSource.buffer().snapshot().utf8(), RPCResponse.class);

        }catch (NullPointerException e){

        }

        return response;
    }


    private void validateRPCResponse(String responseBody){
        //TODO: Mutate network response based on RPC Response.
    }
}
