package com.iemqra.bme.lostnfound.api;

import android.util.Log;

import com.iemqra.bme.lostnfound.event.BaseEvent;
import com.iemqra.bme.lostnfound.event.ItemsEvent;
import com.iemqra.bme.lostnfound.event.PlaceEvent;
import com.iemqra.bme.lostnfound.event.PlacesEvent;
import com.iemqra.bme.lostnfound.event.RegisterEvent;
import com.iemqra.bme.lostnfound.event.UserEvent;
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

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    public static String API_URL = "http://192.168.1.80:8080";
    private static String TAG = "ApiClient";
    private static Retrofit retrofit = null;

    public static Retrofit getClient() {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(1000, TimeUnit.MILLISECONDS).addInterceptor(interceptor).build();

        retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        return retrofit;
    }

    public static void registerUser(final String first_name, final String last_name, final String email, final String salt, final String password) {
        ApiInterface api = ApiClient.getClient().create(ApiInterface.class);

        Call<User> registerQuery = api.createUser(new User(first_name, last_name, email, salt, password));
        registerQuery.enqueue(new Callback<User>() {
            RegisterEvent event = new RegisterEvent();

            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                event.setCode(response.code());
                event.setMessage(response.message());
                event.setUser(response.body());
                EventBus.getDefault().post(event);
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e(TAG, "Registration Error: " + t.getMessage());
                event.setMessage(t.getMessage());
                event.setThrowable(t);
                EventBus.getDefault().post(event);
            }
        });
    }

    public static void loginUser(final String email, final String password) {
        ApiInterface api = ApiClient.getClient().create(ApiInterface.class);

        Call<User> registerQuery = api.loginUser(new LoginData(email, password));
        registerQuery.enqueue(new Callback<User>() {
            RegisterEvent event = new RegisterEvent();

            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                event.setCode(response.code());
                event.setMessage(response.message());
                event.setUser(response.body());
                EventBus.getDefault().post(event);
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e(TAG, "Login Error: " + t.getMessage());
                event.setMessage(t.getMessage());
                event.setThrowable(t);
                EventBus.getDefault().post(event);
            }
        });
    }

    public static void facebookLogin(final String firstName, final String lastName, final String facebookId) {
        ApiInterface api = ApiClient.getClient().create(ApiInterface.class);

        Call<User> registerQuery = api.facebookLogin(new LoginData(firstName, lastName, facebookId));
        registerQuery.enqueue(new Callback<User>() {
            RegisterEvent event = new RegisterEvent();

            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                event.setCode(response.code());
                event.setMessage(response.message());
                event.setUser(response.body());
                EventBus.getDefault().post(event);
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e(TAG, "Facebook login Error: " + t.getMessage());
                event.setMessage(t.getMessage());
                event.setThrowable(t);
                EventBus.getDefault().post(event);
            }
        });
    }

    public static void addGcmToken(String email, String token) {
        ApiInterface api = ApiClient.getClient().create(ApiInterface.class);

        Call<Void> registerQuery = api.addGcmToken(new GcmData(email, token));
        registerQuery.enqueue(new Callback<Void>() {
            RegisterEvent event = new RegisterEvent();

            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                event.setCode(response.code());
                event.setMessage(response.message());
                EventBus.getDefault().post(event);
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "GCM reg Error: " + t.getMessage());
                event.setMessage(t.getMessage());
                event.setThrowable(t);
                EventBus.getDefault().post(event);
            }
        });
    }

    public static void getItems(final FilterData filterData) {
        ApiInterface api = ApiClient.getClient().create(ApiInterface.class);

        Call<List<Item>> registerQuery = api.getItems(filterData);
        registerQuery.enqueue(new Callback<List<Item>>() {
            ItemsEvent event = new ItemsEvent();

            @Override
            public void onResponse(Call<List<Item>> call, Response<List<Item>> response) {
                event.setCode(response.code());
                if (filterData.getUser().isEmpty() && filterData.getStartIndex() == 0)
                    event.setMessage("search");
                else if (filterData.getUser().isEmpty() && filterData.getStartIndex() != 0)
                    event.setMessage("searchMore");
                else if (filterData.getStartIndex() == 0)
                    event.setMessage("myItems");
                else
                    event.setMessage("myItemsMore");
                event.setItems(response.body());
                EventBus.getDefault().post(event);
            }

            @Override
            public void onFailure(Call<List<Item>> call, Throwable t) {
                Log.e(TAG, "Registration Error: " + t.getMessage());
                event.setMessage(t.getMessage());
                event.setThrowable(t);
                EventBus.getDefault().post(event);
            }
        });
    }

    public static void removeItem(RemoveItemData removeItemData) {
        ApiInterface api = ApiClient.getClient().create(ApiInterface.class);

        Call<Void> registerQuery = api.removeItem(removeItemData);
        registerQuery.enqueue(new Callback<Void>() {
            BaseEvent event = new BaseEvent();

            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                event.setCode(response.code());
                event.setMessage("removeItem");
                EventBus.getDefault().post(event);
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "GCM reg Error: " + t.getMessage());
                event.setMessage(t.getMessage());
                event.setThrowable(t);
                EventBus.getDefault().post(event);
            }
        });
    }

    public static void getFollowedPlaces(final String userId) {
        ApiInterface api = ApiClient.getClient().create(ApiInterface.class);

        Call<List<Place>> registerQuery = api.getFollowedPlaces(userId);
        registerQuery.enqueue(new Callback<List<Place>>() {
            PlacesEvent event = new PlacesEvent();

            @Override
            public void onResponse(Call<List<Place>> call, Response<List<Place>> response) {
                event.setCode(response.code());
                event.setMessage("places");
                event.setPlaces(response.body());
                EventBus.getDefault().post(event);
            }

            @Override
            public void onFailure(Call<List<Place>> call, Throwable t) {
                Log.e(TAG, "Registration Error: " + t.getMessage());
                event.setMessage(t.getMessage());
                event.setThrowable(t);
                EventBus.getDefault().post(event);
            }
        });
    }

    public static void addPlaceFollow(RemovePlaceFollowData removePlaceFollowData) {
        ApiInterface api = ApiClient.getClient().create(ApiInterface.class);

        Call<Void> registerQuery = api.addPlaceFollow(removePlaceFollowData);
        registerQuery.enqueue(new Callback<Void>() {
            BaseEvent event = new BaseEvent();

            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                event.setCode(response.code());
                event.setMessage("placeFollow");
                EventBus.getDefault().post(event);
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "GCM reg Error: " + t.getMessage());
                event.setMessage(t.getMessage());
                event.setThrowable(t);
                EventBus.getDefault().post(event);
            }
        });
    }

    public static void removePlaceFollow(RemovePlaceFollowData removePlaceFollowData) {
        ApiInterface api = ApiClient.getClient().create(ApiInterface.class);

        Call<Void> registerQuery = api.removePlaceFollow(removePlaceFollowData);
        registerQuery.enqueue(new Callback<Void>() {
            BaseEvent event = new BaseEvent();

            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                event.setCode(response.code());
                event.setMessage("removePlace");
                EventBus.getDefault().post(event);
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "GCM reg Error: " + t.getMessage());
                event.setMessage(t.getMessage());
                event.setThrowable(t);
                EventBus.getDefault().post(event);
            }
        });
    }

    public static void getPlaceData(final String placeId) {
        ApiInterface api = ApiClient.getClient().create(ApiInterface.class);

        Call<Place> registerQuery = api.getPlaceData(placeId);
        registerQuery.enqueue(new Callback<Place>() {
            PlaceEvent event = new PlaceEvent();

            @Override
            public void onResponse(Call<Place> call, Response<Place> response) {
                event.setCode(response.code());
                event.setMessage("placeData");
                event.setPlace(response.body());
                EventBus.getDefault().post(event);
            }

            @Override
            public void onFailure(Call<Place> call, Throwable t) {
                Log.e(TAG, "Registration Error: " + t.getMessage());
                event.setMessage(t.getMessage());
                event.setThrowable(t);
                EventBus.getDefault().post(event);
            }
        });
    }

    public static void getUserData(final String userId) {
        ApiInterface api = ApiClient.getClient().create(ApiInterface.class);

        Call<UserData> registerQuery = api.getUserData(userId);
        registerQuery.enqueue(new Callback<UserData>() {
            UserEvent event = new UserEvent();

            @Override
            public void onResponse(Call<UserData> call, Response<UserData> response) {
                event.setCode(response.code());
                event.setMessage("userData");
                event.setUserData(response.body());
                EventBus.getDefault().post(event);
            }

            @Override
            public void onFailure(Call<UserData> call, Throwable t) {
                Log.e(TAG, "Registration Error: " + t.getMessage());
                event.setMessage(t.getMessage());
                event.setThrowable(t);
                EventBus.getDefault().post(event);
            }
        });
    }

    public static void getImagePathById(final String imageId) {
        ApiInterface api = ApiClient.getClient().create(ApiInterface.class);

        Call<String> registerQuery = api.getImagePathById(imageId);
        registerQuery.enqueue(new Callback<String>() {
            BaseEvent event = new BaseEvent();

            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                event.setCode(response.code());
                event.setMessage("imagePath");
                event.setData(response.body());
                EventBus.getDefault().post(event);
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e(TAG, "Registration Error: " + t.getMessage());
                event.setMessage(t.getMessage());
                event.setThrowable(t);
                EventBus.getDefault().post(event);
            }
        });
    }

    public static void uploadImage(MultipartBody.Part image) {
        ApiInterface api = ApiClient.getClient().create(ApiInterface.class);

        Call<String> registerQuery = api.uploadImage(image);
        registerQuery.enqueue(new Callback<String>() {
            BaseEvent event = new BaseEvent();

            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                event.setCode(response.code());
                event.setMessage("imageId");
                event.setData(response.body());
                EventBus.getDefault().post(event);
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e(TAG, "Registration Error: " + t.getMessage());
                event.setMessage(t.getMessage());
                event.setThrowable(t);
                EventBus.getDefault().post(event);
            }
        });
    }

    public static void uploadItem(ItemUploadData item) {
        ApiInterface api = ApiClient.getClient().create(ApiInterface.class);

        Call<Void> registerQuery = api.uploadItem(item);
        registerQuery.enqueue(new Callback<Void>() {
            BaseEvent event = new BaseEvent();

            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                event.setCode(response.code());
                event.setMessage("itemUploaded");
                EventBus.getDefault().post(event);
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Registration Error: " + t.getMessage());
                event.setMessage(t.getMessage());
                event.setThrowable(t);
                EventBus.getDefault().post(event);
            }
        });
    }

}