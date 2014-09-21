package io.blueapps.lightspace.socket;

import java.util.List;

/**
 * Created by bartolkaruza on 20/05/14.
 */
public class MeasureEvent {
    private String gameId;
    private String deviceId;
    private List<MeasurementPair> measurements;

    public MeasureEvent(String gameId, String deviceId, List<MeasurementPair> measurements) {
        this.deviceId = deviceId;
        this.gameId = gameId;
        this.measurements = measurements;
    }

}
