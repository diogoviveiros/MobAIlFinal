package com.example.mobailfinal;

import java.io.Serializable;
import java.time.LocalDateTime;

public class DeviceItem implements Serializable {

    private String deviceName;
    private String address;
    private boolean aFalse;
    private int signalStrength;
    private LocalDateTime time;

    public String getDeviceName() {
        return deviceName;
    }

    public boolean getConnected() {
        return aFalse;
    }

    public String getAddress() {
        return address;
    }

    public int signalStrength(){
        return signalStrength;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public void setTime(LocalDateTime time){ this.time = time;}

    public String toString(){

        return "Name: " + deviceName + " (MAC Address: " + address + ")" + " Strength: " + signalStrength + "dBm" + " (Time: " + time.toString() + ")";
    }

    public DeviceItem(String name, String address, String aFalse, int signalStrength, LocalDateTime time) {
        this.deviceName = name;
        this.address = address;
        if (aFalse == "true") {
            this.aFalse = true;
        }
        else {
            this.aFalse = false;
        }
        this.signalStrength = signalStrength;
        this.time = time;
    }
}
