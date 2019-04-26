/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.iot.agent.events.publisher;

import com.verifone.utilities.Log;

import org.json.JSONObject;
import org.wso2.iot.agent.AndroidAgentException;
import org.wso2.iot.agent.BuildConfig;
import org.wso2.iot.agent.R;
import org.wso2.iot.agent.api.DeviceInfo;
import org.wso2.iot.agent.events.EventRegistry;
import org.wso2.iot.agent.events.beans.EventPayload;
import org.wso2.iot.agent.events.publisher.dto.ae.Data;
import org.wso2.iot.agent.events.publisher.dto.ae.RequestContainer;
import org.wso2.iot.agent.proxy.interfaces.APIResultCallBack;
import org.wso2.iot.agent.utils.CommonUtils;
import org.wso2.iot.agent.utils.Constants;
import org.wso2.iot.agent.utils.VResources;

import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class AndroidEndpointPublisher implements APIResultCallBack, DataPublisher {
    private static String deviceIdentifier;
    private static final String TAG = AndroidEndpointPublisher.class.getName();

    private static final TimeZone timezone = TimeZone.getTimeZone("UTC");
    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");

    static {
        DeviceInfo deviceInfo = new DeviceInfo(EventRegistry.context);
        deviceIdentifier = deviceInfo.getDeviceId();
        dateFormat.setTimeZone(timezone);

    }

    public void publish(EventPayload eventPayload) {
        if (EventRegistry.context != null) {
            Log.i(TAG, "uploading the logs to Android Endpoint");


            String nowAsISO = dateFormat.format(new Date());

            String cleanedUpPayload =  new String(eventPayload.getPayload().getBytes(StandardCharsets.US_ASCII)); // just to be sure: convert to ASCII
            String logs = JSONObject.quote(cleanedUpPayload);
            Data data = new Data(nowAsISO, BuildConfig.BUILD_TYPE, this.deviceIdentifier, "LOGCAT", logs, "ANDROID_SYS_LOGS", 7, "Android Endpoint");
            List<Data> dataList = new ArrayList<>(1);
            dataList.add(data);
            RequestContainer request = new RequestContainer(dataList);

            try {
                String requestPayload = CommonUtils.toJSON(request);
//                Log.i(TAG, "Uploading JSON: " + requestPayload);

//                try {
//                    PrintWriter out = new PrintWriter("/sdcard/logcat.txt");
//
//                    out.println(requestPayload);
//                    out.close();
//                } catch (Exception ex){
//                    ex.printStackTrace();
//                }

                Log.d(TAG, "length: " + eventPayload.getPayload().length());

                CommonUtils.callSecuredAPI(EventRegistry.context,
                        VResources.getInstance(EventRegistry.context).getString(R.string.logs_endpoint), org.wso2.iot.agent.proxy.utils.
                                Constants.HTTP_METHODS.POST,
                        requestPayload, AndroidEndpointPublisher.this,
                        Constants.EVENT_REQUEST_CODE); // TODO: since empty response, om.android.volley.ParseError: org.json.JSONException is thrown (but it doesn't matter)
            } catch (AndroidAgentException e) {
                Log.e(TAG, "Cannot convert event data to JSON");
            }
        }

    }

    @Override
    public void onReceiveAPIResult(Map<String, String> result, int requestCode) {
        if (Constants.DEBUG_MODE_ENABLED) {
            String status = result.get(Constants.STATUS);
            Log.d(TAG, "Result for request: " + requestCode + " is " + status);
        }
    }
}
