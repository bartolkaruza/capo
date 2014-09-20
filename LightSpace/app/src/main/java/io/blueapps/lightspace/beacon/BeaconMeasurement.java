package io.blueapps.lightspace.beacon;

import com.estimote.sdk.Beacon;

import java.util.ArrayList;
import java.util.List;

public class BeaconMeasurement {
    private List<RSSI> measurements = new ArrayList<RSSI>();
    private boolean calculating;

    private Integer beaconId;

    public BeaconMeasurement(Integer beaconId) {
        this.beaconId = beaconId;
    }

    public void addBeacon(Beacon beacon) {
        if (!calculating) {
            addMeasurement(new RSSI(beacon.getRssi()));
        }
    }

    public int getAverageRSSI() {
        calculating = Boolean.TRUE;

        int totalAmountOfRSSIValues = getMeasurements().size();
        int totalRSSI = 0;

        for (RSSI rssi : getMeasurements()) {
            totalRSSI += rssi.getValue();
        }

        int averageRSSI = totalRSSI / totalAmountOfRSSIValues;

        int quality = 2 * (averageRSSI + 100);
        calculating = Boolean.FALSE;

        return averageRSSI;
    }

    private synchronized List<RSSI> getMeasurements() {
        return measurements;
    }

    private void addMeasurement(RSSI measurement) {
        if (!calculating) {
            if (getMeasurements().size() > 10) {
                getMeasurements().remove(0);
            }
            getMeasurements().add(measurement);
        }
    }

    public Integer getBeaconId() {
        return beaconId;
    }
}