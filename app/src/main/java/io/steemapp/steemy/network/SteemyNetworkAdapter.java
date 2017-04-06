package io.steemapp.steemy.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by John on 7/28/2016.
 */
public class SteemyNetworkAdapter {

    private final static String BASEURL = "http://www.steemapp.io/";

    protected static SteemyRetrofitService getNewService() {
        return new Retrofit.Builder()
                .baseUrl(BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(SteemyRetrofitService.class);
    }
}
