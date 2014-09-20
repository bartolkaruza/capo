package io.blueapps.lightspace.bleutooth;

import android.bluetooth.BluetoothDevice;

/**
 * Created by klm37586 on 9/20/2014.
 */
public class MyBluetoothDevice {
    private BluetoothDevice device;
    private int rssi;

    public MyBluetoothDevice(BluetoothDevice device, int rssi) {
        this.device = device;
        this.rssi = rssi;
    }

    public BluetoothDevice getDevice() {
        return device;
    }

    public void setDevice(BluetoothDevice device) {
        this.device = device;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        MyBluetoothDevice that = (MyBluetoothDevice) o;

        if (device != null ? !device.equals(that.device) : that.device != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = device != null ? device.hashCode() : 0;
        result = 31 * result + rssi;
        return result;
    }
}
