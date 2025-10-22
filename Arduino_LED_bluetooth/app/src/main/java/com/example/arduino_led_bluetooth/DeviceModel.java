package com.example.arduino_led_bluetooth;

public class DeviceModel {

    private String deviceName, deviceAddress;

    public DeviceModel(){}

    public DeviceModel(String deviceName, String deviceAddress){
        this.deviceName = deviceName;
        this.deviceAddress = deviceAddress;
    }

    public String getDeviceName(){return deviceName;}

    public String getDeviceAddress(){return deviceAddress;}
}
