package org.wso2.iot.agent.services.location.impl.dto;

/**
 * Created by ulrichh1 on 1/25/17.
 */

public class GeolocationResponse {
    private GeolocationData data;
    private String status;

    public GeolocationData getData() {
        return data;
    }

    public void setData(GeolocationData data) {
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "GeolocationResponse{" +
                "data=" + data +
                ", status='" + status + '\'' +
                '}';
    }
}
