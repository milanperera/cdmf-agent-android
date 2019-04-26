package org.wso2.iot.agent.events.publisher.dto.ae;

/**
 * Created by ulrichh1 on 10/27/16.
 */

public class Data {
    private String createTime;
    private String environment;
    private String host;
    private String message;
    private MessageData messageData;
    private String messageType;
    private int severity;
    private String system;

    public Data(String createTime, String environment, String host, String message, String messageData, String messageType, int severity, String system) {
        this.createTime = createTime;
        this.environment = environment;
        this.host = host;
        this.message = message;
        this.messageData = new MessageData(messageData);
        this.messageType = messageType;
        this.severity = severity;
        this.system = system;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public MessageData getMessageData() {
        return messageData;
    }

    public void setMessageData(MessageData messageData) {
        this.messageData = messageData;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public int getSeverity() {
        return severity;
    }

    public void setSeverity(int severity) {
        this.severity = severity;
    }

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    @Override
    public String toString() {
        return "Data{" +
                "createTime='" + createTime + '\'' +
                ", environment='" + environment + '\'' +
                ", host='" + host + '\'' +
                ", message='" + message + '\'' +
                ", messageData=" + messageData +
                ", messageType='" + messageType + '\'' +
                ", severity='" + severity + '\'' +
                ", system='" + system + '\'' +
                '}';
    }
}