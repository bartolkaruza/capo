package com.http;


import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Part;
import retrofit.http.Path;

/**
 * Created by Sunil Shetty on 9/20/2014.
 * sunil.shetty@klm.com
 */
public interface GameRESTful {

    @PUT("/game/create")
    String createGame(@Part("name") String name, @Part("deviceAddress") String deviceAddress);

    @GET("/game")
    String getGameId();

    @GET("/game/{gameId}")
    Game getGame(@Path("gameId") long gameId);

    @PUT("game/{gameId}/join")
    String joinGame(@Path("gameId") String gameId, @Part(("deviceAddress")) String deviceAddress);

    @POST("game/{gameId}/update")
    GameUpdateResponse getUpdate(@Path("gameId") String gameId, @Body GameUpdateRequest gameUpdate);

}
