package com.http.test;

import com.http.data.CreateGame;
import com.http.data.DeviceAddress;
import com.http.data.Game;
import com.http.GameRESTful;

import java.util.List;

import retrofit.RestAdapter;

/**
 * Created by Sunil Shetty on 9/20/2014.
 * sunil.shetty@klm.com
 */
public class GameRESTfulTest {

    private GameRESTful service;

    public static void test(){
        new GameRESTfulTest();
    }

    private GameRESTfulTest() {
        initGameRESTfulService();

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

    private void initGameRESTfulService() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(GameRESTful.END_POINT)
                .build();

        service = restAdapter.create(GameRESTful.class);
    }
}
