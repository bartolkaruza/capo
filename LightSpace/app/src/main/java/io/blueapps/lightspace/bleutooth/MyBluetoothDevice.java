package io.blueapps.lightspace.bleutooth;

import android.bluetooth.BluetoothDevice;
import android.graphics.Color;

import java.util.Random;

/**
 * Created by klm37586 on 9/20/2014.
 */
public class MyBluetoothDevice {
    private BluetoothDevice device;
    private int color;
    private int rssi;

    public MyBluetoothDevice(BluetoothDevice device, int rssi) {
        this.device = device;
        this.rssi = rssi;

        Random rand = new Random();

        int i = 255 + rssi;
        int red = i - rand.nextInt(i);
        int green = i - rand.nextInt(i);
        int blue = i - rand.nextInt(i);

        this.color = -Color.argb(255, red, green, blue);
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

    public int getColor() {
        return color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        MyBluetoothDevice that = (MyBluetoothDevice) o;

        if (device != null ? !device.getAddress().equals(that.device.getAddress()) : that.device != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = device != null ? device.hashCode() : 0;
        return result;
    }
}
