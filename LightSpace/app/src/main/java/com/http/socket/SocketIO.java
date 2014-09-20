package com.http.socket;

import com.http.GameRESTful;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.socketio.Acknowledge;
import com.koushikdutta.async.http.socketio.ConnectCallback;
import com.koushikdutta.async.http.socketio.EventCallback;
import com.koushikdutta.async.http.socketio.JSONCallback;
import com.koushikdutta.async.http.socketio.SocketIOClient;
import com.koushikdutta.async.http.socketio.StringCallback;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Sunil Shetty on 9/20/2014.
 * sunil.shetty@klm.com
 */
public class SocketIO {


    public SocketIO() {
        SocketIOClient.connect(AsyncHttpClient.getDefaultInstance(), GameRESTful.END_POINT, new ConnectCallback() {
            @Override
            public void onConnectCompleted(Exception ex, SocketIOClient client) {
                if (ex != null) {
                    ex.printStackTrace();
                    return;
                }
                client.setStringCallback(new StringCallback() {
                    @Override
                    public void onString(String string, Acknowledge acknowledge) {
                        System.out.println(string);
                    }
                });
                client.on("foo", new EventCallback() {
                    @Override
                    public void onEvent(JSONArray argument, Acknowledge acknowledge) {
                        System.out.println("args: " + argument.toString());
                    }
                });
                client.setJSONCallback(new JSONCallback() {
                    @Override
                    public void onJSON(JSONObject json, Acknowledge acknowledge) {
                        System.out.println("json: " + json.toString());
                    }
                });
                client.emit("foo");
            }
        });
    }
}
