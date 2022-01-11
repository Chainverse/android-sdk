package com.chainverse.sdk.network.RESTful;

import com.chainverse.sdk.network.RESTful.raw.TestRaw;
import com.google.gson.JsonElement;

import java.math.BigInteger;
import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

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

    @GET("/v1/sdk/user/{nft}/items")
    Observable<JsonElement> getMyAsset(@Path("nft") String gameAddress);

    @GET("/v1/sdk/market/{nft}/items")
    Observable<JsonElement> getListItemOnMarket(
            @Path("nft") String gameAddress,
            @QueryMap Map<String, String> options
    );

    @GET("/v1/sdk/market/item/{nft}/{item_id}")
    Observable<JsonElement> getDetailNFT(
            @Path("nft") String nft,
            @Path("item_id") BigInteger tokenId
    );

    @GET("/v1/sdk/game/{game_address}")
    Observable<JsonElement> getServiceByGame(@Path("game_address") String gameAddress);

    @POST("/v1/user/nonce")
    Observable<JsonElement> getNonce();

    @POST("/v1/item")
    Observable<JsonElement> testBuy(@Body TestRaw body);

    @PUT("/v1/user/item/publish/{token_contract}/{token_id}")
    Observable<JsonElement> publish(@Path("token_contract") String tokenContract, @Path("token_id") BigInteger tokenId);
}
