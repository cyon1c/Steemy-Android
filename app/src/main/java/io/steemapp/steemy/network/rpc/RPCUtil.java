package io.steemapp.steemy.network.rpc;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;

import io.steemapp.steemy.transactions.Transaction;
import okhttp3.Response;
import okio.BufferedSource;

public class RPCUtil {

    private static Gson gson = new Gson();

    public static RPCResponse fromNetworkResponse(Response response) throws JsonSyntaxException, NullPointerException, IOException {

        final BufferedSource responseSource = response.body().source();
        responseSource.request(Integer.MAX_VALUE);

        RPCResponse rpcResponse = gson.fromJson(responseSource.buffer().snapshot().utf8(),
                RPCResponse.class);

        //There is a result object, nothing more needed.
        if(rpcResponse.getResult() != null){
            rpcResponse.success = true;
            return rpcResponse;
        }

        //There wasn't a result object. FAILURE ;_;
        rpcResponse.success = false;

        //Result AND Error are missing - something is horribly broken
        if(rpcResponse.getError() == null){
            rpcResponse.setError(RPCResponseError.unspecifiedError());
        }

        return rpcResponse;
    }

    public static RPCRequest wrapTransaction(Transaction tx){

        return RPCRequest.emptyRequest();
    }
}
