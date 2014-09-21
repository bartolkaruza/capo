package io.blueapps.lightspace.socket;

/**
 * Created by bartolkaruza on 20/05/14.
 */
public class MeasureEvent {
    private String gameId;
    private String deviceId;
    private MeasurementPair valuePair;

    public MeasureEvent(String gameId, String deviceId, MeasurementPair measurements) {
        this.deviceId = deviceId;
        this.gameId = gameId;
        this.valuePair = measurements;
    }


    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public MeasurementPair getValuePair() {
        return valuePair;
    }

    public void setValuePair(MeasurementPair valuePair) {
        this.valuePair = valuePair;
    }
}
