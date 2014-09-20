package io.blueapps.lightspace.socket;

import java.util.List;

/**
 * Created by bartolkaruza on 20/05/14.
 */
public class MeasureEvent {

    private String deviceId;
    private List<MeasurementPair> measurements;

    public MeasureEvent(String deviceId, List<MeasurementPair> measurements) {
        this.deviceId = deviceId;
        this.measurements = measurements;
    }

}
