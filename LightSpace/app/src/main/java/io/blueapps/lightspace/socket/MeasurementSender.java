package io.blueapps.lightspace.socket;

import android.util.Log;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;

/**
 * Created by bartolkaruza on 20/05/14.
 */
public class MeasurementSender {

    Socket socket;

    public MeasurementSender() {
        try {
            socket = IO.socket("http://bartolkaruza-measure-app.nodejitsu.com");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {

            @Override
            public void call(Object... args) {
                socket.emit("foo", "hi");
                socket.disconnect();
            }

        }).on("event", new Emitter.Listener() {

            @Override
            public void call(Object... args) {
                for(Object message : args) {
                    Log.d("test", message.toString());
                }
            }

        }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {

            @Override
            public void call(Object... args) {
                for(Object message : args) {
                    Log.d("disconnect", message.toString());
                }
            }

        });
        socket.connect();
    }

    public void send(String message) {
        socket.send("measurement", message);
    }
}
