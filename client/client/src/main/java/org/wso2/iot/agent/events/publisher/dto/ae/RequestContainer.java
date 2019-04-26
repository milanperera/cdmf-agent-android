package org.wso2.iot.agent.events.publisher.dto.ae;

import java.util.List;

/**
 * Created by ulrichh1 on 10/27/16.
 */

public class RequestContainer {
    private List<Data> data;

    public List<Data> getData() {
        return data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }

    public RequestContainer(List<Data> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "RequestContainer{" +
                "data=" + data +
                '}';
    }
}

