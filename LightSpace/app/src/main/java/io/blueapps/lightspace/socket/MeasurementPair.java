package io.blueapps.lightspace.socket;

/**
 * Created by bartolkaruza on 20/09/14.
 */
public class MeasurementPair {

    private String deviceAddress;
    private String rssi;

    public String getDeviceAddress() {
        return deviceAddress;
    }

    public void setDeviceAddress(String deviceAddress) {
        this.deviceAddress = deviceAddress;
    }

    public String getRssi() {
        return rssi;
    }

    public void setRssi(String rssi) {
        this.rssi = rssi;
    }
}
