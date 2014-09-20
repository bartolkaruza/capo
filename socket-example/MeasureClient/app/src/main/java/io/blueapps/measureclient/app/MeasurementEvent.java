package io.blueapps.measureclient.app;

import android.os.Build;

/**
 * Created by bartolkaruza on 20/05/14.
 */
public class MeasurementEvent {

    private String agent;
    private double latitude;
    private double longitude;
    private double altitude;
    private String time;

    public MeasurementEvent(double latitude, double longitude, double altitude, String time) {
        agent = Build.BRAND;
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
        this.time = time;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
