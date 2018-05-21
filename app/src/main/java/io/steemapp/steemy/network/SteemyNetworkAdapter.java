package io.steemapp.steemy.network;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by John on 7/28/2016.
 */
public class SteemyNetworkAdapter {

    private final static String BASEURL = "https://api.steemit.com/";

    protected static SteemyRetrofitService getNewService() {
        return new Retrofit.Builder()
                .baseUrl(BASEURL)
                .client(newJsonRPCClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(SteemyRetrofitService.class);
    }

    private static OkHttpClient newJsonRPCClient(){
//        OkHttpClient clientBuilder = new OkHttpClient.Builder()
//                .addInterceptor(new LoggingInterceptor())
//                .cache(buildCache());

        return new OkHttpClient();
    }

//    private static Cache buildCache(){
//
//    }


}
