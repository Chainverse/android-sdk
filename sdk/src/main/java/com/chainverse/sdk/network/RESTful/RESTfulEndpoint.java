package com.chainverse.sdk.network.RESTful;

import com.chainverse.sdk.network.RESTful.raw.TestRaw;
import com.google.gson.JsonElement;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface RESTfulEndpoint {
    @GET("/v1/user/{user_address}/game/{game_address}/items")
    Observable<JsonElement> getItems(@Path("user_address") String  userAddress, @Path("game_address") String  gameAddress);


    @POST("/game-signer-1/v1/item")
    Observable<JsonElement> testBuy(@Body TestRaw body);

}
