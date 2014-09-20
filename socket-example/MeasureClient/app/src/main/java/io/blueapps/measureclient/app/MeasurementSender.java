package io.blueapps.measureclient.app;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.net.MalformedURLException;
import java.text.DateFormat;
import java.util.Date;

import io.socket.IOAcknowledge;
import io.socket.IOCallback;
import io.socket.SocketIO;
import io.socket.SocketIOException;

/**
 * Created by bartolkaruza on 20/05/14.
 */
public class MeasurementSender {

    private SocketIO socket;

    public MeasurementSender() {
        socket = null;
        try {
//            socket = new SocketIO("http://192.168.1.88:3000/");
            socket = new SocketIO("http://bartolkaruza-measure-app.nodejitsu.com/");
//            socket = new SocketIO("http://10.0.2.2:3000");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        socket.connect(new IOCallback() {
            @Override
            public void onMessage(String data, IOAcknowledge ack) {
                Log.d("onMessage", data);
            }

            @Override
            public void onMessage(JsonElement jsonElement, IOAcknowledge ioAcknowledge) {
                Log.d("onMessage", jsonElement.toString());
            }

            @Override
            public void on(String s, IOAcknowledge ioAcknowledge, JsonElement... jsonElements) {
                Log.d("on", s);
            }

            @Override
            public void onError(SocketIOException socketIOException) {
                Log.e("onError", socketIOException.getMessage());
            }

            @Override
            public void onDisconnect() {
                Log.d("onDisconnect", "Connection terminated.");
            }

            @Override
            public void onConnect() {
                Log.d("onConnect", "Connection established");
            }

        });
    }

    public void updateMeasurement(final double latitude, final double longitude, final double altitude) {
        String time = DateFormat.getDateTimeInstance().format(new Date(System.currentTimeMillis()));
        if(socket != null) {
            Log.d("sender", "sending measurement with: " + time);
            Gson gson = new Gson();
            socket.emit("measurement", gson.toJson(new MeasurementEvent(latitude, longitude, altitude, time)));
        }
    }
}
