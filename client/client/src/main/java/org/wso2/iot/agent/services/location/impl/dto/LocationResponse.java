package org.wso2.iot.agent.services.location.impl.dto;

/**
 * Created by ulrichh1 on 1/25/17.
 */

public class LocationResponse {
    private Double lat;
    private Double lng;

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    @Override
    public String toString() {
        return "LocationResponse{" +
                "lat=" + lat +
                ", lng=" + lng +
                '}';
    }
}
