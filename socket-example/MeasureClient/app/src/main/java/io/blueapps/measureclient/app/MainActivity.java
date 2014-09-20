package io.blueapps.measureclient.app;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationRequest;

import retrofit.RetrofitError;


public class MainActivity extends Activity {

    private PowerManager.WakeLock wakeLock;

    private MeasurementSender measurementSender = new MeasurementSender();

    private LocationClient locationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyWakelockTag");
        wakeLock.acquire();
        setupLocationClient();
        locationClient.connect();

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                measurementSender = new MeasurementSender();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        wakeLock.release();
    }

    private void setupLocationClient() {
        this.locationClient = new LocationClient(this, new GooglePlayServicesClient.ConnectionCallbacks() {
            @Override
            public void onConnected(Bundle bundle) {
                Log.d("GooglePlayServices", "Connected");
                LocationRequest locationRequest = LocationRequest.create();
                locationRequest.setFastestInterval(50);
                locationRequest.setInterval(50);
                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                locationClient.requestLocationUpdates(locationRequest, new com.google.android.gms.location.LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        Log.d("gps", "sending location: " + location.getLatitude() + " and long: " + location.getLongitude());
                        measurementSender.updateMeasurement(location.getLatitude(), location.getLongitude(), location.getAltitude());
                    }
                });
            }

            @Override
            public void onDisconnected() {
                Log.d("GooglePlayServices", "Disconnected");
            }
        }, new GooglePlayServicesClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(ConnectionResult connectionResult) {
                Log.d("GooglePlayServices", "OnConnectionFailed");
            }
        });
    }
}
