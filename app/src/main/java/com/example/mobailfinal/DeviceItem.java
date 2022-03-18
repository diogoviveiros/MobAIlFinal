package com.example.mobailfinal;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Date;

public class DeviceItem implements Serializable{

    private String deviceName;
    private String hash;
    private boolean aFalse;
    private int signalStrength;
    private Date time;

    public String getDeviceName() {
        return deviceName;
    }

    public boolean getConnected() {
        return aFalse;
    }

    public String getHash() {
        return hash;
    }

    public String getSignalStrength(){ return "" + signalStrength;}

    public Date getTime(){ return this.time;}

    public int signalStrength(){
        return signalStrength;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public void setTime(Date time){ this.time = time;}

    public String toString(){

        return "Name: " + deviceName + " (Hash: " + hash + ")" + " Strength: " + signalStrength + "dBm" + " (Time: " + time.toString() + ")";
    }

    public DeviceItem(String name, String hash, int signalStrength, Date time) {
        this.deviceName = name;
        this.hash = hash;

        this.signalStrength = signalStrength;
        this.time = time;
    }


}
