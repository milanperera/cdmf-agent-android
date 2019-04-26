package org.wso2.iot.agent.events.publisher.dto.ae;

/**
 * Created by ulrichh1 on 10/27/16.
 */

public class MessageData {
    private String rawLogs;

    public MessageData(String rawLogs) {
        this.rawLogs = rawLogs;
    }

    public String getRawLogs() {
        return rawLogs;
    }

    public void setRawLogs(String rawLogs) {
        this.rawLogs = rawLogs;
    }

    @Override
    public String toString() {
        return "MessageData{" +
                "rawLogs='" + rawLogs + '\'' +
                '}';
    }
}