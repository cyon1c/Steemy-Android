package io.steemapp.steemy;
import android.app.Application;
import android.support.multidex.MultiDexApplication;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import io.steemapp.steemy.events.AuthManagerFailureEvent;
import io.steemapp.steemy.events.NetworkErrorEvent;


/**
 * Created by John on 7/27/2016.
 */
public class SteemyApplication extends MultiDexApplication {

    public Bus mGlobalBus;

    @Override
    public void onCreate() {
        super.onCreate();
        mGlobalBus = new Bus();
        mGlobalBus.register(this);
    }

    @Subscribe
    public void onEvent(NetworkErrorEvent event){

    }

    @Subscribe
    public void onEvent(AuthManagerFailureEvent event){

    }
}
