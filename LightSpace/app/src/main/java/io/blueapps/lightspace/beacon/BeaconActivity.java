package io.blueapps.lightspace.beacon;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.blueapps.lightspace.R;

public class BeaconActivity extends Activity {
    private static final String ESTIMOTE_PROXIMITY_UUID = "B9407F30-F5F8-466E-AFF9-25556B57FE6D";
    private static final Region ALL_ESTIMOTE_BEACONS = new Region("regionId", ESTIMOTE_PROXIMITY_UUID, null, null);
    private static final String TAG = "BeaconActivity";

    private BeaconManager beaconManager;
    public Map<Integer, BeaconMeasurement> measurements = new HashMap<Integer, BeaconMeasurement>();

    private Handler locationBroadcastHandler = new Handler();


    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
      /* do what you need to do */

      /* and here comes the "trick" */
            locationBroadcastHandler.postDelayed(this, 10000);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locationBroadcastHandler.postDelayed(runnable, 10000);
        com.estimote.sdk.utils.L.enableDebugLogging(Boolean.FALSE);
        setContentView(R.layout.activity_beacon);

        // Should be invoked in #onCreate.
        beaconManager = new BeaconManager(this);
        beaconManager.setRangingListener(new BeaconManager.RangingListener() {

            @Override
            public void onBeaconsDiscovered(Region region, List<com.estimote.sdk.Beacon> beacons) {
                for (Beacon beacon : beacons) {
                    BeaconMeasurement bm = measurements.get(beacon.getMajor());

                    if (bm == null) {
                        bm = new BeaconMeasurement(beacon.getMajor());
                        measurements.put(beacon.getMajor(), bm);
                    }

                    bm.addBeacon(beacon);

                    // Log.i(TAG, "beacon " + beacon.getProximityUUID() + " " + beacon.getMajor() + ": " + beacon.getRssi() + " - average: " + bm.getAverageRSSI());
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        // Should be invoked in #onStart.
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                try {
                    beaconManager.startRanging(ALL_ESTIMOTE_BEACONS);
                } catch (RemoteException e) {
                    Log.e(TAG, "Cannot start ranging", e);
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Should be invoked in #onStop.
        try {
            beaconManager.stopRanging(ALL_ESTIMOTE_BEACONS);
        } catch (RemoteException e) {
            Log.e(TAG, "Cannot stop but it does not matter now", e);
        }

        // When no longer needed. Should be invoked in #onDestroy.
        beaconManager.disconnect();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.beacon, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
