package com.http;

import com.http.data.CreateGame;
import com.http.data.DeviceAddress;
import com.http.data.Game;

import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.http.Path;

/**
 * Created by Sunil Shetty on 9/20/2014. sunil.shetty@klm.com
 */
public class GameRESTfulService {
    private static GameRESTfulService instance;
    private GameRESTful service;
    private DeviceAddress localAddress;

    private GameRESTfulService(DeviceAddress address) {
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(GameRESTful.END_POINT).build();

        service = restAdapter.create(GameRESTful.class);
        this.localAddress = address;
    }

    public static GameRESTfulService getInstance() {
        if (instance == null) {
            throw new RuntimeException("initialize with address");
        }

        return instance;
    }

    public static GameRESTfulService getInstance(DeviceAddress address) {
        if (instance == null) {
            instance = new GameRESTfulService(address);
        }

        return instance;
    }

    public void createGame(String game, Callback<Game> callback) {
        CreateGame gameRequest = new CreateGame(localAddress.getDeviceAddress(), game);
        service.createGame(gameRequest, callback);
    }

    public void getGames(Callback<List<Game>> callback) {
        service.getGames(callback);
    }

    public void getGame(@Path("gameId") String gameId, Callback<Game> callback) {
        service.getGame(gameId, callback);
    }

    public void joinGame(String game, Callback<Game> callback) {
        service.joinGame(game, localAddress, callback);
    }

}
