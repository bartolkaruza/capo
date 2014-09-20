package io.blueapps.lightspace.socket;

/**
 * Created by bartolkaruza on 20/09/14.
 */
public class MeasurementPair {

    private String deviceAddress;
    private int rssi;

    public String getDeviceAddress() {
        return deviceAddress;
    }

    public void setDeviceAddress(String deviceAddress) {
        this.deviceAddress = deviceAddress;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }
}
