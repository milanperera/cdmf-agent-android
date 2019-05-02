package org.wso2.emm.system.service;

import android.app.Application;

import com.verifone.utilities.Log;

public class SystemServiceApplication extends Application {
    private final static String TAG = "SystemServiceApp";
    @Override
    public void onCreate() {
        super.onCreate();

        if (!Log.isInitialized()) {
            android.util.Log.d(TAG, "starting Logging");
            Log.initialize(this, 20);
        }
    }
}
