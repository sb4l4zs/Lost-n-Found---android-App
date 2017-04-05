package com.iemqra.bme.lostnfound.api;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import com.iemqra.bme.lostnfound.helper.SQLiteHandler;
import com.iemqra.bme.lostnfound.model.Item;
import com.iemqra.bme.lostnfound.model.Place;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SQLiteApi {
    private static final String TAG = SQLiteApi.class.getSimpleName();
    private static SQLiteHandler db;
    private Context context;

    public SQLiteApi(Context context) {
        this.context = context;
        db = new SQLiteHandler(context);
    }

    public static Bitmap imageStringToBitmap(String id) {
        HashMap<String, String> ImageQuery = db.getImageById(id);
        if (ImageQuery.get("image") != null) {
            String image = ImageQuery.get("image").toString();
            Log.e(TAG, "image: " + image);
            if (image != null) {
                Log.d(TAG, "image not = 0" + image);
                byte[] decodedByte = Base64.decode(image, 0);
                return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
            } else
                return null;
        } else
            return null;
    }

    public static String getPlaceNameByUid(String uid) {
        HashMap<String, String> PlaceNameQuery = db.getPlaceName(uid);
        Log.e(TAG, "name: " + PlaceNameQuery.get("name"));
        if (PlaceNameQuery.get("name") != null) {
            String name = PlaceNameQuery.get("name").toString();
            Log.e(TAG, "name: " + name);

            return name;
        } else
            return null;
    }

    public static List<Item> getItemsByUserFromSQLite(final String user_uid) {
        ArrayList<HashMap<String, String>> itemsFromSQLite = db.getItemsByUser(user_uid);
        List<Item> itemList = null;
        if (itemsFromSQLite != null) {
            itemList = new ArrayList<>();
            for (HashMap<String, String> item : itemsFromSQLite) {
                String place_uid = item.get("place_uid");
                String image_id = item.get("image_id");
                String title = item.get("title");
                String description = item.get("description");
                String created_at = item.get("created_at");
                Item m = new Item(null, place_uid, Integer.valueOf(image_id), "", user_uid, title, description, created_at);
                itemList.add(m);
            }
        }
        return itemList;
    }

    public List<Item> searchItemsFromSQLite(String searchFor) {
        ArrayList<HashMap<String, String>> itemsFromSQLite = db.getItemsByTitle(searchFor);
        List<Item> itemList = null;
        if (itemsFromSQLite != null) {
            itemList = new ArrayList<>();
            for (HashMap<String, String> item : itemsFromSQLite) {
                String place_uid = item.get("place_uid");
                String image_id = item.get("image_id");
                String user_uid = item.get("user_uid");
                String title = item.get("title");
                String description = item.get("description");
                String created_at = item.get("created_at");
                Item m = new Item(null, place_uid, Integer.valueOf(image_id), "", user_uid, title, description, created_at);
                itemList.add(m);
            }
        }
        return itemList;
    }

    public List<Place> getFollowedPlacesFromSQLite() {
        ArrayList<HashMap<String, String>> followsFromSQLite = db.getFollowedPlaces();
        List<Place> placeList = null;
        if (followsFromSQLite != null) {
            placeList = new ArrayList<>();
            for (HashMap<String, String> place : followsFromSQLite) {
                String place_uid = place.get("uid");
                String place_name = place.get("name");
                String place_address = place.get("address");
                String place_phone = place.get("phone_number");
                com.iemqra.bme.lostnfound.model.Place m = new com.iemqra.bme.lostnfound.model.Place(null, place_uid, place_name, place_address, place_phone);
                placeList.add(m);
            }
        }
        return placeList;
    }
}
