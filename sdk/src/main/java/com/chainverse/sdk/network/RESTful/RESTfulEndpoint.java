package com.chainverse.sdk.network.RESTful;

import com.chainverse.sdk.network.RESTful.raw.TestRaw;
import com.google.gson.JsonElement;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RESTfulEndpoint {
    @GET("/v1/user/{user_address}/game/{game_address}/items")
    Observable<JsonElement> getItems(@Path("user_address") String userAddress, @Path("game_address") String gameAddress);

    @GET("/v1/market/items")
    Observable<JsonElement> getItemOnMarket(
            @Query("game_address") String gameAddress,
            @Query("page") int page,
            @Query("page_size") int pageSize,
            @Query("name") String name
    );

    @GET("/v1/user/asset/tokens")
    Observable<JsonElement> getMyAsset();

    @POST("/v1/user/nonce")
    Observable<JsonElement> getNonce();

    @POST("/v1/item")
    Observable<JsonElement> testBuy(@Body TestRaw body);

}
