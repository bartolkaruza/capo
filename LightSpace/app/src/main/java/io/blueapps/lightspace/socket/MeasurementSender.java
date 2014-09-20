package io.blueapps.lightspace.socket;

import android.util.Log;
import android.util.Pair;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import org.json.JSONObject;

import java.net.MalformedURLException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.blueapps.lightspace.bleutooth.BluetoothLeService;
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
//            socket = new SocketIO("http://bartolkaruza-measure-app.nodejitsu.com/");
            socket = new SocketIO("http://10.12.1.74:3000");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        socket.connect(new IOCallback() {
            @Override
            public void onMessage(String data, IOAcknowledge ack) {
                Log.d("onMessage", data);
            }

            @Override
            public void onMessage(JSONObject jsonObject, IOAcknowledge ioAcknowledge) {
                Log.d("onMessage", jsonObject.toString());
            }

            @Override
            public void on(String s, IOAcknowledge ioAcknowledge, Object... objects) {
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
                updateMeasurement(new ArrayList<Pair<String, Integer>>());
                Log.d("onConnect", "Connection established");
            }

        });
    }

    public void updateMeasurement(List<Pair<String, Integer>> measurements) {
        String time = DateFormat.getDateTimeInstance().format(new Date(System.currentTimeMillis()));
        if(socket != null) {
            Log.d("sender", "sending measurement with: " + time);
            Gson gson = new Gson();
            socket.emit("measurement", gson.toJson(new MeasureEvent(BluetoothLeService.MY_UUID, measurements)));
        }
    }
}
