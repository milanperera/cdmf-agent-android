package org.wso2.iot.agent.services.location.impl.dto;

/**
 * Created by ulrichh1 on 1/25/17.
 */

public class WifiAccessPoint {
    private String macAddress;
    private Integer signalStrength;
    private Integer age;
    private Integer channel;
    private Integer signalToNoiseRatio;

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public Integer getSignalStrength() {
        return signalStrength;
    }

    public void setSignalStrength(Integer signalStrength) {
        this.signalStrength = signalStrength;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Integer getChannel() {
        return channel;
    }

    public void setChannel(Integer channel) {
        this.channel = channel;
    }

    public Integer getSignalToNoiseRatio() {
        return signalToNoiseRatio;
    }

    public void setSignalToNoiseRatio(Integer signalToNoiseRatio) {
        this.signalToNoiseRatio = signalToNoiseRatio;
    }
}
