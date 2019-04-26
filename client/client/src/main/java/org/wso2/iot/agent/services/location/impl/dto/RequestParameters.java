package org.wso2.iot.agent.services.location.impl.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ulrichh1 on 1/25/17.
 */

public class RequestParameters {
    private String considerIp = "true";
    private List<WifiAccessPoint> wifiAccessPoints = new ArrayList<WifiAccessPoint>();

    public boolean getConsiderIp() {
        return Boolean.valueOf(considerIp);
    }

    public void setConsiderIp(boolean considerIp) {
        this.considerIp = Boolean.toString(considerIp);
    }

    public List<WifiAccessPoint> getWifiAccessPoints() {
        return wifiAccessPoints;
    }

    public void setWifiAccessPoints(List<WifiAccessPoint> wifiAccessPoints) {
        this.wifiAccessPoints = wifiAccessPoints;
    }

    public void addWifiAccessPoint(WifiAccessPoint wifiAccessPoint){
        wifiAccessPoints.add(wifiAccessPoint);
    }
}
