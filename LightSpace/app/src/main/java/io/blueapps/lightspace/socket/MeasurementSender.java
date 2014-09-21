package io.blueapps.lightspace.socket;

import android.util.Log;

import com.google.gson.Gson;
import com.http.GameRESTful;
import com.http.data.ColorUpdate;
import com.http.data.GameColor;

import org.json.JSONObject;

import java.net.MalformedURLException;
import java.text.DateFormat;
import java.util.Date;

import io.socket.IOAcknowledge;
import io.socket.IOCallback;
import io.socket.SocketIO;
import io.socket.SocketIOException;
import ui.activities.CapoSplashActivity;

/**
 * Created by bartolkaruza on 20/05/14.
 */
public class MeasurementSender {

    private SocketIO socket;

    private GameSocketCallback mCallback;

    public MeasurementSender() {
        socket = null;
    }

    public static interface GameSocketCallback {
        public void onConnected();

        public void onSocketError(String message);

        public void onHueChanged(GameColor value);

        public void onGameOver();
    }

    public void setSocketCallback(GameSocketCallback callback) {
        this.mCallback = callback;
    }

    public void init() {
        try {
            // socket = new SocketIO("http://192.168.1.88:3000/");
            // socket = new SocketIO("http://bartolkaruza-measure-app.nodejitsu.com/");
            socket = new SocketIO(GameRESTful.END_POINT);
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
            if (mCallback != null)
                mCallback.onSocketError(e.getMessage());
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
                Log.d("STRING",s);

                if (s.equals("update")) {

                    try {
                        Gson gson = new Gson();
                        ColorUpdate color = gson.fromJson(((JSONObject) (objects[0])).toString(), ColorUpdate.class);
                        if (mCallback != null)
                            mCallback.onHueChanged(color.getCurrentColor());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                Log.d("on", s);
            }

            @Override
            public void onError(SocketIOException socketIOException) {
                Log.e("onError", socketIOException.getMessage());
                Throwable cause = socketIOException.getCause();
                if (cause != null) {
                    Log.e("onError", "cause: " + cause.getMessage());
                }
                if (mCallback != null && socketIOException != null)

                    mCallback.onSocketError(socketIOException.getMessage());
            }

            @Override
            public void onDisconnect() {
                Log.d("onDisconnect", "Connection terminated.");
                if (mCallback != null)
                    mCallback.onSocketError("Connection terminated.");
            }

            @Override
            public void onConnect() {
                if (mCallback != null)
                    mCallback.onConnected();
            }

        });

    }

    public void stop() {
        socket.disconnect();
    }

    public void updateMeasurement(MeasurementPair measurements, String gameId) {
        String time = DateFormat.getDateTimeInstance().format(new Date(System.currentTimeMillis()));
        if (socket != null && socket.isConnected()) {
            Gson gson = new Gson();
            String s = gson.toJson(new MeasureEvent(gameId, CapoSplashActivity.address.getDeviceAddress(), measurements));
            Log.d("sender", "sending measurement with: " + time + s);
            socket.emit("measurement", s);
        }
        else {
            Log.e("ERROR", "socket not connected");
        }
    }
}
