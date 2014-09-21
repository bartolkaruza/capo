package com.philips.lighting.quickstart;

import android.animation.Animator;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.http.GameRESTfulService;
import com.http.data.DeviceAddress;
import com.http.data.Game;
import com.http.data.GameValues;
import com.philips.lighting.data.AccessPointListAdapter;
import com.philips.lighting.data.HueSharedPreferences;
import com.philips.lighting.hue.sdk.PHAccessPoint;
import com.philips.lighting.hue.sdk.PHBridgeSearchManager;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.hue.sdk.PHMessageType;
import com.philips.lighting.hue.sdk.PHSDKListener;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHHueError;
import com.philips.lighting.model.PHHueParsingError;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import io.blueapps.lightspace.R;
import io.blueapps.lightspace.bleutooth.MyBluetoothDevice;
import io.blueapps.lightspace.socket.MeasurementPair;
import io.blueapps.lightspace.socket.MeasurementSender;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * PHHomeActivity - The starting point in your own Hue App.
 * 
 * For first time use, a Bridge search (UPNP) is performed and a list of all available bridges is displayed (and clicking one of them shows the PushLink dialog allowing authentication). The last connected Bridge IP Address and
 * Username are stored in SharedPreferences.
 * 
 * For subsequent usage the app automatically connects to the last connected bridge. When connected the MyApplicationActivity Activity is started. This is where you should start implementing your Hue App! Have fun!
 * 
 * For explanation on key concepts visit: https://github.com/PhilipsHue/PhilipsHueSDK-Java-MultiPlatform-Android
 * 
 *
 */
public class GameActivity extends Activity implements OnItemClickListener, Callback<Game>, MeasurementSender.GameSocketCallback {

    public static final String KEY_MODE = "mode";
    public static final String KEY_ADRESS = "adress";
    public static final String KEY_GAME_ID = "gameid";

    public static final int MODE_JOIN = 0;
    public static final int MODE_HOST = 1;

    private static final long SCAN_PERIOD = 100000;
    private static final int REQUEST_ENABLE_BT = 1;

    private int mode = MODE_JOIN;
    private String deviceAdress;
    private String gameID;

    private PHHueSDK phHueSDK;
    public static final String TAG = "QuickStart";
    private HueSharedPreferences prefs;
    private AccessPointListAdapter adapter;

    private MeasurementSender rssiSender;
    private BluetoothAdapter mBluetoothAdapter;

    private Handler mHandler;
    private boolean mScanning = false;

    private boolean lastSearchWasIPScan = false;

    @InjectView(R.id.gameView)
    protected View gameView;

    @InjectView(R.id.bridge_list)
    protected ListView bridgeList;

    @InjectView(R.id.game_status_text)
    protected TextView titleText;

    GameRESTfulService gameService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActivityBackgroundColor(Color.WHITE);
        setContentView(R.layout.bridgelistlinear);
        ButterKnife.inject(this);

        if (getIntent().getExtras() != null) {
            this.mode = getIntent().getIntExtra(KEY_MODE, MODE_JOIN);
            this.deviceAdress = getIntent().getStringExtra(KEY_ADRESS);
            this.gameID = getIntent().getStringExtra(KEY_GAME_ID);
        }

        gameService = GameRESTfulService.getInstance(new DeviceAddress(deviceAdress));
        gameService.getGame(gameID, this);

        mHandler = new Handler();
        initBLE();
        initSockets();

        if (this.mode == MODE_HOST) {
            initHUEAPI();
        }
    }

    public void setActivityBackgroundColor(int color) {
        View view = this.getWindow().getDecorView();
        view.setBackgroundColor(color);
    }

    private void initSockets() {
        Log.d("API", "initSockets");
        rssiSender = new MeasurementSender();
        rssiSender.setSocketCallback(this);
        rssiSender.init();
    }

    private boolean initBLE() {

        adapter = new AccessPointListAdapter(getApplicationContext(), new ArrayList<PHAccessPoint>());
        bridgeList.setOnItemClickListener(this);
        bridgeList.setAdapter(adapter);

        Log.d("API", "initBLE");
        // Initializes a Bluetooth adapter. For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            Crouton.makeText(this, R.string.error_bluetooth_not_supported, Style.ALERT).show();
            finish();
            return false;
        }
        return true;
    }

    private void initHUEAPI() {

        Log.d("API", "initHUE");
        // Gets an instance of the Hue SDK.
        phHueSDK = PHHueSDK.create();
        // Set the Device Name (name of your app). This will be stored in your bridge whitelist entry.
        phHueSDK.setDeviceName("QuickStartApp");
        // Register the PHSDKListener to receive callbacks from the bridge.
        phHueSDK.getNotificationManager().registerSDKListener(listener);

        // Try to automatically connect to the last known bridge. For first time use this will be empty so a bridge search is automatically started.
        prefs = HueSharedPreferences.getInstance(getApplicationContext());
        String lastIpAddress = prefs.getLastConnectedIPAddress();
        String lastUsername = prefs.getUsername();

        // Automatically try to connect to the last connected IP Address. For multiple bridge support a different implementation is required.
        if (lastIpAddress != null && !lastIpAddress.equals("")) {
            PHAccessPoint lastAccessPoint = new PHAccessPoint();
            lastAccessPoint.setIpAddress(lastIpAddress);
            lastAccessPoint.setUsername(lastUsername);

            if (!phHueSDK.isAccessPointConnected(lastAccessPoint)) {
                Crouton.makeText(this, R.string.connecting, Style.INFO).show();
                // PHWizardAlertDialog.getInstance().showProgressDialog(R.string.connecting, this);
                phHueSDK.connect(lastAccessPoint);
            }
        }
        else { // First time use, so perform a bridge search.
            doBridgeSearch();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (rssiSender != null)
            rssiSender.stop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        scanLeDevice(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Ensures Bluetooth is enabled on the device. If Bluetooth is not currently enabled,
        // fire an intent to display a dialog asking the user to grant permission to enable it.
        if (!mBluetoothAdapter.isEnabled()) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }

        scanLeDevice(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.w(TAG, "Inflating home menu");
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    // Local SDK Listener
    private PHSDKListener listener = new PHSDKListener() {

        @Override
        public void onAccessPointsFound(List<PHAccessPoint> accessPoint) {
            Log.w(TAG, "Access Points Found. " + accessPoint.size());

            PHWizardAlertDialog.getInstance().closeProgressDialog();
            if (accessPoint != null && accessPoint.size() > 0) {
                phHueSDK.getAccessPointsFound().clear();
                phHueSDK.getAccessPointsFound().addAll(accessPoint);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.updateData(phHueSDK.getAccessPointsFound());
                    }
                });
            }
        }

        @Override
        public void onCacheUpdated(List<Integer> arg0, PHBridge bridge) {
            Log.w(TAG, "On CacheUpdated");

        }

        @Override
        public void onBridgeConnected(PHBridge b) {
            phHueSDK.setSelectedBridge(b);
            phHueSDK.enableHeartbeat(b, PHHueSDK.HB_INTERVAL);
            phHueSDK.getLastHeartbeat().put(b.getResourceCache().getBridgeConfiguration().getIpAddress(), System.currentTimeMillis());
            prefs.setLastConnectedIPAddress(b.getResourceCache().getBridgeConfiguration().getIpAddress());
            prefs.setUsername(prefs.getUsername());
            PHWizardAlertDialog.getInstance().closeProgressDialog();
        }

        @Override
        public void onAuthenticationRequired(PHAccessPoint accessPoint) {
            Log.w(TAG, "Authentication Required.");
            phHueSDK.startPushlinkAuthentication(accessPoint);
            startActivity(new Intent(GameActivity.this, PHPushlinkActivity.class));

        }

        @Override
        public void onConnectionResumed(PHBridge bridge) {
            if (GameActivity.this.isFinishing())
                return;

            Log.v(TAG, "onConnectionResumed" + bridge.getResourceCache().getBridgeConfiguration().getIpAddress());
            phHueSDK.getLastHeartbeat().put(bridge.getResourceCache().getBridgeConfiguration().getIpAddress(), System.currentTimeMillis());
            for (int i = 0; i < phHueSDK.getDisconnectedAccessPoint().size(); i++) {

                if (phHueSDK.getDisconnectedAccessPoint().get(i).getIpAddress().equals(bridge.getResourceCache().getBridgeConfiguration().getIpAddress())) {
                    phHueSDK.getDisconnectedAccessPoint().remove(i);
                }
            }

        }

        @Override
        public void onConnectionLost(PHAccessPoint accessPoint) {
            Log.v(TAG, "onConnectionLost : " + accessPoint.getIpAddress());
            if (!phHueSDK.getDisconnectedAccessPoint().contains(accessPoint)) {
                phHueSDK.getDisconnectedAccessPoint().add(accessPoint);
            }
        }

        @Override
        public void onError(int code, final String message) {
            Log.e(TAG, "on Error Called : " + code + ":" + message);

            if (code == PHHueError.NO_CONNECTION) {
                Log.w(TAG, "On No Connection");
            }
            else if (code == PHHueError.AUTHENTICATION_FAILED || code == 1158) {
                PHWizardAlertDialog.getInstance().closeProgressDialog();
            }
            else if (code == PHHueError.BRIDGE_NOT_RESPONDING) {
                Log.w(TAG, "Bridge Not Responding . . . ");
                PHWizardAlertDialog.getInstance().closeProgressDialog();
                Crouton.makeText(GameActivity.this, message, Style.ALERT);

            }
            else if (code == PHMessageType.BRIDGE_NOT_FOUND) {

                if (!lastSearchWasIPScan) { // Perform an IP Scan (backup mechanism) if UPNP and Portal Search fails.
                    phHueSDK = PHHueSDK.getInstance();
                    PHBridgeSearchManager sm = (PHBridgeSearchManager) phHueSDK.getSDKService(PHHueSDK.SEARCH_BRIDGE);
                    sm.search(false, false, true);
                    lastSearchWasIPScan = true;
                }
                else {
                    PHWizardAlertDialog.getInstance().closeProgressDialog();
                    Crouton.makeText(GameActivity.this, message, Style.ALERT);
                }

            }
        }

        @Override
        public void onParsingErrors(List<PHHueParsingError> parsingErrorsList) {
            for (PHHueParsingError parsingError : parsingErrorsList) {
                Log.e(TAG, "ParsingError : " + parsingError.getMessage());
            }
        }
    };

    /**
     * Called when option is selected.
     * 
     * @param item the MenuItem object.
     * @return boolean Return false to allow normal menu processing to proceed, true to consume it here.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.find_new_bridge:
                doBridgeSearch();
                break;
        }
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mode == MODE_HOST) {
            if (listener != null) {
                phHueSDK.getNotificationManager().unregisterSDKListener(listener);
            }
            phHueSDK.disableAllHeartbeat();
        }
        Crouton.cancelAllCroutons();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if (mode == MODE_HOST) {
            HueSharedPreferences prefs = HueSharedPreferences.getInstance(getApplicationContext());
            PHAccessPoint accessPoint = (PHAccessPoint) adapter.getItem(position);
            accessPoint.setUsername(prefs.getUsername());

            PHBridge connectedBridge = phHueSDK.getSelectedBridge();

            if (connectedBridge != null) {
                String connectedIP = connectedBridge.getResourceCache().getBridgeConfiguration().getIpAddress();
                if (connectedIP != null) { // We are already connected here:-
                    phHueSDK.disableHeartbeat(connectedBridge);
                    phHueSDK.disconnect(connectedBridge);
                }
            }
            PHWizardAlertDialog.getInstance().showProgressDialog(R.string.connecting, GameActivity.this);
            phHueSDK.connect(accessPoint);
        }
    }

    public void doBridgeSearch() {
        PHWizardAlertDialog.getInstance().showProgressDialog(R.string.search_progress, GameActivity.this);
        PHBridgeSearchManager sm = (PHBridgeSearchManager) phHueSDK.getSDKService(PHHueSDK.SEARCH_BRIDGE);
        // Start the UPNP Searching of local bridges.
        sm.search(true, true);
    }

    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, byte[] scanRecord) {

            GameRESTfulService.getInstance().getGame("game01", new Callback<Game>() {
                @Override
                public void success(Game game, Response response) {
                    GameValues[] values = game.getValues();

                    MyBluetoothDevice bluetoothDevice = new MyBluetoothDevice(device, rssi);
                    List<MeasurementPair> pairs = new ArrayList<MeasurementPair>();
                    MeasurementPair pair = new MeasurementPair();
                    String deviceAddress = "";

                    if (bluetoothDevice != null && bluetoothDevice.getDevice() != null) {
                        deviceAddress = bluetoothDevice.getDevice().getAddress();
                        pair.setDeviceAddress(deviceAddress);
                        pair.setRssi(bluetoothDevice.getRssi());
                    }

                    for (GameValues gv : values) {
                        if (deviceAddress.equalsIgnoreCase(gv.getAddress())) {
                            pairs.add(pair);
                            rssiSender.updateMeasurement(pairs);
                        }
                    }
                }

                @Override
                public void failure(RetrofitError error) {

                }
            });

        }
    };

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    invalidateOptionsMenu();
                }
            }, SCAN_PERIOD);

            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        }
        else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
        invalidateOptionsMenu();
    }

    /**
     * Returns the complimentary (opposite) color.
     * @param color int RGB color to return the compliment of
     * @return int RGB of compliment color
     */
    public static int getComplimentColor(int color) {
        // get existing colors
        int alpha = Color.alpha(color);
        int red = Color.red(color);
        int blue = Color.blue(color);
        int green = Color.green(color);

        // find compliments
        red = (~red) & 0xff;
        blue = (~blue) & 0xff;
        green = (~green) & 0xff;

        return Color.argb(alpha, red, green, blue);
    }


    @Override
    public void success(Game game, Response response) {
        if (game != null) {

            Log.d("GAME","ready");
            titleText.setText("This is your target color! GO!");



            titleText.postDelayed(new Runnable() {
                @Override
                public void run() {
                    titleText.animate().alpha(0).setDuration(200).setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animator) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animator) {
                            titleText.setVisibility(View.GONE);
                        }

                        @Override
                        public void onAnimationCancel(Animator animator) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animator) {

                        }
                    }).start();
                }
            },5000);

            this.gameView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    gameView.getViewTreeObserver().removeOnPreDrawListener(this);
                    gameView.setAlpha(0);
                    gameView.animate().alpha(1).setDuration(1000).start();

                    titleText.setAlpha(0);
                    titleText.setVisibility(View.VISIBLE);
                    titleText.animate().alpha(1).setDuration(200).start();

                    return true;
                }
            });
            int color = Color.parseColor(game.getTargetColor());
            this.gameView.setBackgroundColor(color);
            this.titleText.setTextColor(getComplimentColor(color));
        }
    }

    @Override
    public void failure(RetrofitError error) {
        Crouton.makeText(this, "there was a problem loading the game, please try again", Style.ALERT).show();
        finish();
    }

    @Override
    public void onConnected() {
        Crouton.makeText(this, "Connection established.", Style.CONFIRM).show();
    }

    @Override
    public void onSocketError(String message) {
        try {
            Crouton.makeText(this, message, Style.ALERT).show();
        } catch (Exception e)
        {
            
        }
    }

    @Override
    public void onHueChanged(String value) {

    }

    @Override
    public void onGameOver() {
        Crouton.makeText(this, "Yay! you won.", Style.CONFIRM).show();
        finish();
    }
}
