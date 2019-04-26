/*
 * Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.iot.agent.services.location.impl;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.v4.content.ContextCompat;

import com.google.gson.Gson;
import com.verifone.swordfish.usermanagement.UserManagement;
import com.verifone.utilities.Log;

import org.wso2.iot.agent.R;
import org.wso2.iot.agent.proxy.interfaces.APIResultCallBack;
import org.wso2.iot.agent.services.location.impl.dto.GeolocationResponse;
import org.wso2.iot.agent.services.location.impl.dto.RequestParameters;
import org.wso2.iot.agent.services.location.impl.dto.WifiAccessPoint;
import org.wso2.iot.agent.utils.CommonUtils;
import org.wso2.iot.agent.utils.Constants;
import org.wso2.iot.agent.utils.Preference;
import org.wso2.iot.agent.utils.VResources;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * This class holds the function implementations of the location service.
 */
public class GeoLocationServiceImpl extends Service implements LocationListener, UserManagement.OnUserManagementListener {

    private Location location;
    private LocationManager locationManager;
    private static GeoLocationServiceImpl serviceInstance;
    private AlarmManager alarmManager;
    private Context context;
    private BroadcastReceiver broadcastReceiver;
    private final static Random random = new Random();
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60;
//    private UserManagement userManagement;
    public static final String ACTION_NAME = "org.wso2.iot.agent.services.location.SET_LOCATION";
    private static final String TAG = GeoLocationServiceImpl.class.getSimpleName();

    private GeoLocationServiceImpl() {}

    private GeoLocationServiceImpl(Context context) {
        this.context = context;
        locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

        IntentFilter filter = new IntentFilter(ACTION_NAME);
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                setLocation();
            }
        };
        context.registerReceiver(broadcastReceiver, filter);
//        userManagement = UserManagement.userManagement(context);
//        userManagement.setOnUserManagementListener(this);


        class LooperThread extends Thread {
            public Handler mHandler;
            public void run() {
                if (Looper.myLooper() == null) {
                    Looper.prepare();
                }
                GeoLocationServiceImpl.this.setLocation();
                mHandler = new Handler() {
                    public void handleMessage(Message msg) {
                        Log.e(TAG, "Couldn't get location: " + msg);
                    }
                };
            }
        }
        new LooperThread().run();
    }

    public static GeoLocationServiceImpl getInstance(Context context) {
        if (serviceInstance == null) {
            synchronized (GeoLocationServiceImpl.class) {
                if (serviceInstance == null) {
                    serviceInstance = new GeoLocationServiceImpl(context);
                }
            }
        }
        return serviceInstance;
    }

    /**
     * In this method, it gets the latest location updates from gps/ network.
     */
    private void setLocation() {

        if (Constants.DEBUG_MODE_ENABLED)
            Log.d(TAG, "Getting current location");

        if (setLocationViaGPS())
            return;

        setLocationViaGeolocationService(); // will call setLocationViaVenueAddress() if MLS fails

        triggerAlarm();
    }

    private void triggerAlarm(){

        long interval = 4 * 60 * 60 * 1000 - random.nextInt(15 * 60 * 1000); // 4 hours minus up to 15 minutes jitter

//        if (BuildConfig.BUILD_TYPE != null &&
//                (BuildConfig.BUILD_TYPE.equals("development") || BuildConfig.BUILD_TYPE.equals("qa")))
//            interval = 5 * 60 * 1000 - random.nextInt(30 * 1000); // on QA, set to 4min30s to 5min

        Intent intent = new Intent(ACTION_NAME);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        alarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + interval, pendingIntent);
        if (Constants.DEBUG_MODE_ENABLED)
            Log.d(TAG, "Will update position again in " + interval + " ms");
    }

    private boolean setLocationViaGPS(){
        if (Constants.DEBUG_MODE_ENABLED)
            Log.d(TAG, "Trying to set location via GPS");

        try {
            if (locationManager != null) {
                if (Build.VERSION.SDK_INT < 23 ||
                        (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                                ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
                    boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);


                    if (isGpsEnabled) {
                        if (location == null) {
                            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                                    MIN_TIME_BW_UPDATES,
                                    MIN_DISTANCE_CHANGE_FOR_UPDATES,
                                    this);
                            if (locationManager != null) {

                                location = locationManager.getLastKnownLocation(
                                        LocationManager.GPS_PROVIDER);
                                if (location != null) {
                                    Preference.putString(context, context.getResources().getString(R.string.shared_pref_location),
                                            new Gson().toJson(location));
                                    if (Constants.DEBUG_MODE_ENABLED)
                                        Log.d(TAG, "Setting location via GPS was successful");

                                    return true;
                                }
                            }
                        }
                    }

                }
            }
        } catch (RuntimeException e) {
            Log.e(TAG, "Unable to get device location via GPS", e);
        }

        return false;
    }


    private void setLocationViaGeolocationService(){
        if (Constants.DEBUG_MODE_ENABLED)
            Log.d(TAG, "Trying to set location via geolocation service");

        try {
            RequestParameters requestParams = new RequestParameters();
            WifiManager wifiManager = (WifiManager) this.context.getSystemService(Context.WIFI_SERVICE);
            List<ScanResult> apList = wifiManager.getScanResults();

            if (Constants.DEBUG_MODE_ENABLED)
                Log.d(TAG, "Got Wifi list: " + apList.toString());

            for (ScanResult ap : apList){
                WifiAccessPoint wifiAccessPoint = new WifiAccessPoint();
                wifiAccessPoint.setChannel(getChannelFromFrequency(ap.frequency));
                wifiAccessPoint.setMacAddress(ap.BSSID);
                wifiAccessPoint.setSignalStrength(ap.level);
                requestParams.addWifiAccessPoint(wifiAccessPoint);
            }

            APIResultCallBack apiResultCallback = new APIResultCallBack() {
                @Override
                public void onReceiveAPIResult(Map<String, String> result, int requestCode) {
                    if (Constants.DEBUG_MODE_ENABLED)
                        Log.d(TAG, "got response: " + requestCode + "; " + result.toString());

                    if (result == null){
                        Log.e(TAG, "no response body");
                        return;
                    }

                    String statusCode = result.get("status");

                    if (Integer.valueOf(statusCode) == HttpURLConnection.HTTP_OK){
                        String response = result.get("response");
                        GeolocationResponse geolocationResponse = new Gson().fromJson(response, GeolocationResponse.class);

                        if (Constants.DEBUG_MODE_ENABLED)
                            Log.d(TAG, "Parsed response object: " + geolocationResponse.toString());

                        location = new Location("");
                        location.setLatitude(geolocationResponse.getData().getLocation().getLat());
                        location.setLongitude(geolocationResponse.getData().getLocation().getLng());

                        Preference.putString(context, context.getResources().getString(R.string.shared_pref_location),
                                new Gson().toJson(location));

                        return;


                    } else {
                        String errorMsg = "Error retrieving device location from geolocation service. status code" + requestCode;
                        if (result != null){
                            errorMsg += "; response: " + result.toString();
                        }
                        Log.e(TAG, errorMsg);
                    }


                }
            };
            String json =new Gson().toJson(requestParams);
            if (Constants.DEBUG_MODE_ENABLED)
                Log.d(TAG, "Sending JSON: " + json);

            CommonUtils.callSecuredAPI(context, VResources.getInstance(context).getString(R.string.geolocation), org.wso2.iot.agent.proxy.utils.Constants.HTTP_METHODS.POST, json, apiResultCallback, 0);

        } catch (RuntimeException e) {
            Log.e(TAG, "Unable to get device location via MLS", e);
        }

//        setLocationViaVenueAddress();
    }

//    private boolean setLocationViaVenueAddress(){
//        if (Constants.DEBUG_MODE_ENABLED)
//            Log.d(TAG, "Trying to set location via venue address");
//
//        List<UserLocation> venues = userManagement.getLocations();
//        if (venues != null && !venues.isEmpty()){
//            UserLocation venue = venues.get(0); // TODO: we need a server API to get the current venue of the device; for now we assume that there is only one venue
//            Address address = venue.getAddress();
//
//            location = new Location("");
//            location.setLatitude(address.getLatitude()); TODO
//            location.setLongitude(address.getLongitude());
//
//        }
//
//        return false;
//    }


    public Location getLastKnownLocation() {
        return location;
    }

    public Location getLocation() {
        if (location == null) {
            location = new Gson().fromJson(Preference.getString(context, context.getResources().getString(
                    R.string.shared_pref_location)), Location.class);
        }
        return location;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        if (broadcastReceiver != null) {
            context.unregisterReceiver(broadcastReceiver);
            broadcastReceiver = null;
        }
        super.onDestroy();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            Preference.putString(context, context.getResources().getString(R.string.shared_pref_location),
                                 new Gson().toJson(location));
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @SuppressWarnings("boxing")
    private final static ArrayList<Integer> channelsFrequency = new ArrayList<Integer>(
            Arrays.asList(0, 2412, 2417, 2422, 2427, 2432, 2437, 2442, 2447,
                    2452, 2457, 2462, 2467, 2472, 2484));

    public static Integer getFrequencyFromChannel(int channel) {
        return channelsFrequency.get(channel);
    }

    public static int getChannelFromFrequency(int frequency) {
        return channelsFrequency.indexOf(Integer.valueOf(frequency));
    }

    @Override
    public void onUserManagementServiceConnected() {

    }

    @Override
    public void onUserManagementServiceDisconnected() {

    }
}
