package io.blueapps.lightspace;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.larswerkman.holocolorpicker.ColorPicker;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.hue.sdk.utilities.PHUtilities;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import io.blueapps.lightspace.bleutooth.MyBluetoothDevice;

public class ColorActivity extends Activity implements ColorPicker.OnColorSelectedListener {

    private PHHueSDK phHueSDK;

    private LeDeviceListAdapter mLeDeviceListAdapter;
    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning;
    private Handler mHandler;

    private static final int REQUEST_ENABLE_BT = 1;
    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 100000;
    private ColorPicker picker;

    // private SVBar svBar;
    // private OpacityBar opacityBar;
    // private Button button;
    // private TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color);
        phHueSDK = PHHueSDK.create();

        picker = (ColorPicker) findViewById(R.id.picker);
        // svBar = (SVBar) findViewById(R.id.svbar);
        // opacityBar = (OpacityBar) findViewById(R.id.opacitybar);
        // picker.addSVBar(svBar);
        // picker.addOpacityBar(opacityBar);
        // button = (Button) findViewById(R.id.button1);
        // text = (TextView) findViewById(R.id.textView1);
        //
        // button.setOnClickListener(new View.OnClickListener() {
        //
        // @Override
        // public void onClick(View v) {
        // text.setTextColor(picker.getColor());
        // picker.setOldCenterColor(picker.getColor());
        // }
        // });

        picker.setOnColorSelectedListener(this);

        mHandler = new Handler();

        // Use this check to determine whether BLE is supported on the device. Then you can
        // selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }

        // Initializes a Bluetooth adapter. For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
    }

    public void onColorSelected(int color) {
        setHueColor(color);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        if (!mScanning) {
            menu.findItem(R.id.menu_stop).setVisible(false);
            menu.findItem(R.id.menu_scan).setVisible(true);
            menu.findItem(R.id.menu_refresh).setActionView(null);
        }
        else {
            menu.findItem(R.id.menu_stop).setVisible(true);
            menu.findItem(R.id.menu_scan).setVisible(false);
            menu.findItem(R.id.menu_refresh).setActionView(R.layout.actionbar_indeterminate_progress);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_scan:
                mLeDeviceListAdapter.clear();
                scanLeDevice(true);
                break;
            case R.id.menu_stop:
                scanLeDevice(false);
                break;
        }
        return true;
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

        // Initializes list view adapter.
        mLeDeviceListAdapter = new LeDeviceListAdapter();
        scanLeDevice(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        super.onPause();
        scanLeDevice(false);
        mLeDeviceListAdapter.clear();
    }

    // @Override
    // protected void onListItemClick(ListView l, View v, int position, long id) {
    // final BluetoothDevice device = mLeDeviceListAdapter.getDevice(position).getDevice();
    // if (device == null)
    // return;
    // final Intent intent = new Intent(this, DeviceControlActivity.class);
    // intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_NAME, device.getName());
    // intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_ADDRESS, device.getAddress());
    // if (mScanning) {
    // mBluetoothAdapter.stopLeScan(mLeScanCallback);
    // mScanning = false;
    // }
    // startActivity(intent);
    // }

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

    // Adapter for holding devices found through scanning.
    private class LeDeviceListAdapter extends BaseAdapter {
        private List<MyBluetoothDevice> mLeDevices;
        private LayoutInflater mInflator;

        public LeDeviceListAdapter() {
            super();
            mLeDevices = new ArrayList<MyBluetoothDevice>();
            mInflator = ColorActivity.this.getLayoutInflater();
        }

        public void addDevice(MyBluetoothDevice device) {
            if (!mLeDevices.contains(device)) {
                mLeDevices.add(device);
            }
            else {
                for (MyBluetoothDevice dev : mLeDevices) {
                    if (dev.equals(device)) {
                        dev.setRssi(device.getRssi());
                    }
                }
            }
        }

        public MyBluetoothDevice getDevice(int position) {
            return mLeDevices.get(position);
        }

        public void clear() {
            mLeDevices.clear();
        }

        @Override
        public int getCount() {
            return mLeDevices.size();
        }

        @Override
        public Object getItem(int i) {
            return mLeDevices.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            // General ListView optimization code.
            if (view == null) {
                view = mInflator.inflate(R.layout.listitem_device, null);
                viewHolder = new ViewHolder();
                viewHolder.deviceAddress = (TextView) view.findViewById(R.id.device_address);
                viewHolder.deviceName = (TextView) view.findViewById(R.id.device_name);
                view.setTag(viewHolder);
            }
            else {
                viewHolder = (ViewHolder) view.getTag();
            }

            MyBluetoothDevice myBluetoothDevice = mLeDevices.get(i);
            BluetoothDevice device = myBluetoothDevice.getDevice();
            String deviceName = device.getName();

            if (deviceName == null || deviceName.length() <= 0) {
                deviceName = "unkown";
            }

            final String finalDeviceName = myBluetoothDevice.getRssi() + ": " + deviceName;

            viewHolder.deviceName.setText(finalDeviceName);
            viewHolder.deviceAddress.setText(device.getAddress());

            return view;
        }
    }

    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, byte[] scanRecord) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mLeDeviceListAdapter.addDevice(new MyBluetoothDevice(device, rssi));
                    // mLeDeviceListAdapter.notifyDataSetChanged();
                    boolean isPlayer = true;

                    if (isPlayer) {
                        Random rand = new Random();

                        int i = 255 + rssi;
                        int red = i - rand.nextInt(i);
                        int green = i - rand.nextInt(i);
                        int blue = i - rand.nextInt(i);

                        int color = -Color.argb(0, red, green, blue);
                        Log.d("color changes", "set color: " + color);
                        Log.d("color changes", "color= " + color + ", r=" + red + " g=" + green + " b= " + blue);
                        picker.setColor(color);
                        picker.setShowOldCenterColor(false);

                        setHueColor(color);
                    }
                }
            });
        }
    };

    static class ViewHolder {
        TextView deviceName;
        TextView deviceAddress;
    }

    public void setHueColor(int color) {
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = (color >> 0) & 0xFF;

        Log.d("colorpicker", "color= " + color + ", r=" + r + " g=" + g + " b= " + b);
        setHueColor(r, g, b);
    }

    public void setHueColor(int r, int g, int b) {
        if (r != 0 && g != 0 && b != 0) {
            PHBridge bridge = phHueSDK.getSelectedBridge();

            if (bridge != null) {
                List<PHLight> allLights = bridge.getResourceCache().getAllLights();

                for (PHLight light : allLights) {
                    float xy[] = PHUtilities.calculateXYFromRGB(r, g, b, light.getModelNumber());
                    PHLightState lightState = new PHLightState();
                    lightState.setX(xy[0]);
                    lightState.setY(xy[1]);

                    // lightState.setHue(rand.nextInt(MAX_HUE));
                    // To validate your lightstate is valid (before sending to the bridge) you can use:
                    // String validState = lightState.validateState();
                    // bridge.updateLightState(light, lightState, listener);
                    bridge.updateLightState(light, lightState); // If no bridge response is required then use this simpler form.
                }
            }
        }
        else {
            Log.d("color changes", "bulb can not display black");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        phHueSDK.disableAllHeartbeat();
    }
}
