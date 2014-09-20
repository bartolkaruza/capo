package com.http;

import com.http.data.CreateGame;
import com.http.data.DeviceAddress;
import com.http.data.Game;

import java.util.List;

import retrofit.RestAdapter;

/**
 * Created by Sunil Shetty on 9/20/2014. sunil.shetty@klm.com
 */
public class GameRESTfulService {
    private static GameRESTfulService instance;
    private GameRESTful service;

    private GameRESTfulService() {
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(GameRESTful.END_POINT).build();

        service = restAdapter.create(GameRESTful.class);
    }

    private void da() {
        new Thread() {
            @Override
            public void run() {

                Game game01 = service.createGame(new CreateGame("01-AA-01-AA", "game01"));

                List<Game> games = service.getGames();

                Game game011 = service.getGame("game01");

                Game game012 = service.joinGame("game01", new DeviceAddress("01-AA-01-AA"));

            }
        }.start();
    }

    public static GameRESTfulService newInstance() {
        if (instance == null) {
            instance = new GameRESTfulService();
        }
        return newInstance();
    }

}
