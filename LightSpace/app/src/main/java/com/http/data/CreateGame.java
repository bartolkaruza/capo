package com.http.data;

/**
 * Created by Sunil Shetty on 9/20/2014.
 * sunil.shetty@klm.com
 */
public class CreateGame {
    private String name;

    private String deviceAddress;

    public CreateGame(String deviceAddress, String name) {
        this.deviceAddress = deviceAddress;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDeviceAddress() {
        return deviceAddress;
    }

    public void setDeviceAddress(String deviceAddress) {
        this.deviceAddress = deviceAddress;
    }
}

