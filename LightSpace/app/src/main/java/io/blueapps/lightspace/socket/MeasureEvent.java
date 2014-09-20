package io.blueapps.lightspace.socket;

import android.util.Pair;

import java.util.List;

/**
 * Created by bartolkaruza on 20/05/14.
 */
public class MeasureEvent {

    private String deviceId;
    private List<Pair<String, Integer>> measurements;

    public MeasureEvent(String deviceId, List<Pair<String, Integer>> measurements) {
        this.deviceId = deviceId;
        this.measurements = measurements;
    }

}
