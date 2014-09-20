package com.http;

import com.http.data.CreateGame;
import com.http.data.DeviceAddress;
import com.http.data.Game;

import java.util.List;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;

/**
 * Created by Sunil Shetty on 9/20/2014. sunil.shetty@klm.com
 */
public interface GameRESTful {

    public static String END_POINT = "http://bartolkaruza-measure-app.nodejitsu.com/";

    @PUT("/game")
    void createGame(@Body CreateGame gameRequest, Callback<Game> cb);

    @GET("/game")
    void getGames(Callback<List<Game>> cb);

    @GET("/game/{gameId}")
    void getGame(@Path("gameId") String gameId, Callback<Game> cb);

    @POST("/game/{gameId}/join")
    void joinGame(@Path("gameId") String gameId, @Body DeviceAddress deviceAddress, Callback<Game> cb);
}
