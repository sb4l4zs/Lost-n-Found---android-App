package com.iemqra.bme.lostnfound.api;

import com.iemqra.bme.lostnfound.model.APIHelper.FilterData;
import com.iemqra.bme.lostnfound.model.APIHelper.GcmData;
import com.iemqra.bme.lostnfound.model.APIHelper.ItemUploadData;
import com.iemqra.bme.lostnfound.model.APIHelper.LoginData;
import com.iemqra.bme.lostnfound.model.APIHelper.RemoveItemData;
import com.iemqra.bme.lostnfound.model.APIHelper.RemovePlaceFollowData;
import com.iemqra.bme.lostnfound.model.APIHelper.UserData;
import com.iemqra.bme.lostnfound.model.Item;
import com.iemqra.bme.lostnfound.model.Place;
import com.iemqra.bme.lostnfound.model.User;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface ApiInterface {

    @POST("/api/Registration")
    Call<User> createUser(@Body User user);

    @POST("/api/Login")
    Call<User> loginUser(@Body LoginData loginData);

    @POST("/api/FacebookLogin")
    Call<User> facebookLogin(@Body LoginData loginData);

    @POST("/api/Gcm")
    Call<Void> addGcmToken(@Body GcmData gcmData);

    @POST("/api/Items")
    Call<List<Item>> getItems(@Body FilterData filterData);

    @HTTP(method = "DELETE", path = "/api/Items", hasBody = true)
    Call<Void> removeItem(@Body RemoveItemData removeItemData);

    @GET("/api/PlaceFollowing")
    Call<List<Place>> getFollowedPlaces(@Query("userId") String userId);

    @POST("/api/PlaceFollowing")
    Call<Void> addPlaceFollow(@Body RemovePlaceFollowData removePlaceFollowData);

    @HTTP(method = "DELETE", path = "/api/PlaceFollowing", hasBody = true)
    Call<Void> removePlaceFollow(@Body RemovePlaceFollowData removePlaceFollowData);

    @GET("/api/Place")
    Call<Place> getPlaceData(@Query("placeId") String placeId);

    @GET("/api/User")
    Call<UserData> getUserData(@Query("userId") String userId);

    @GET("/api/Image")
    Call<String> getImagePathById(@Query("imageId") String imageId);

    @Multipart
    @POST("/api/UploadImage")
    Call<String> uploadImage(@Part MultipartBody.Part image);

    @POST("/api/UploadItem")
    Call<Void> uploadItem(@Body ItemUploadData item);

    @GET("/api/User")
    Call<String> GetEmpDetails();

}