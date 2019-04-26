package org.wso2.iot.agent.services.location.impl.dto;

/**
 * Created by ulrichh1 on 1/25/17.
 */

public class GeolocationData {
    private LocationResponse location;
    private Double accuracy;

    public LocationResponse getLocation() {
        return location;
    }

    public void setLocation(LocationResponse location) {
        this.location = location;
    }

    public Double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(Double accuracy) {
        this.accuracy = accuracy;
    }

    @Override
    public String toString() {
        return "GeolocationResponse{" +
                "location=" + location +
                ", accuracy=" + accuracy +
                '}';
    }
}
