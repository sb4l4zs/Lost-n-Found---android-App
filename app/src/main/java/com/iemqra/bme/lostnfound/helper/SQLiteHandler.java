package com.iemqra.bme.lostnfound.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

public class SQLiteHandler extends SQLiteOpenHelper {

    private static final String TAG = SQLiteHandler.class.getSimpleName();

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "1978122_admin";
    private static final String TABLE_USER = "user";
    private static final String TABLE_ITEMS = "items";
    private static final String TABLE_PLACES = "places";
    private static final String TABLE_IMAGES = "images";
    private static final String TABLE_FOLLOWS = "follows";

    // User table columns
    private static final String KEY_USER_ID = "id";
    private static final String KEY_USER_FIRST_NAME = "first_name";
    private static final String KEY_USER_LAST_NAME = "last_name";
    private static final String KEY_USER_EMAIL = "email";
    private static final String KEY_USER_UID = "uid";
    private static final String KEY_USER_CREATED_AT = "created_at";

    // Place table columns
    private static final String KEY_PLACE_ID = "id";
    private static final String KEY_PLACE_UID = "uid";
    private static final String KEY_PLACE_NAME = "name";
    private static final String KEY_PLACE_ADDRESS = "address";
    private static final String KEY_PLACE_PHONE_NUMBER = "phone_number";

    // Items table columns
    private static final String KEY_ITEM_ID = "uid";
    private static final String KEY_ITEM_PLACE_UID = "place_uid";
    private static final String KEY_ITEM_IMAGE_ID = "image_id";
    private static final String KEY_ITEM_USER_UID = "user_uid";
    private static final String KEY_ITEM_TITLE = "title";
    private static final String KEY_ITEM_DESCRIPTION = "description";
    private static final String KEY_ITEM_CREATED_AT = "created_at";

    // Images table columns
    private static final String KEY_IMAGE_ID = "id";
    private static final String KEY_IMAGE_DBID = "dbid";
    private static final String KEY_IMAGE_IMAGE = "image";

    // Follows table columns
    private static final String KEY_FOLLOW_PLACE = "place_uid";

    public SQLiteHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_USER + "("
                + KEY_USER_ID + " INTEGER PRIMARY KEY," + KEY_USER_FIRST_NAME + " TEXT," + KEY_USER_LAST_NAME + " TEXT,"
                + KEY_USER_EMAIL + " TEXT UNIQUE," + KEY_USER_UID + " TEXT,"
                + KEY_USER_CREATED_AT + " TEXT" + ")";
        db.execSQL(CREATE_LOGIN_TABLE);

        String CREATE_IMAGES_TABLE = "CREATE TABLE " + TABLE_IMAGES + "("
                + KEY_IMAGE_ID + " INTEGER PRIMARY KEY," + KEY_IMAGE_DBID + " TEXT," + KEY_IMAGE_IMAGE + " TEXT" + ")";
        db.execSQL(CREATE_IMAGES_TABLE);

        String CREATE_PLACES_TABLE = "CREATE TABLE " + TABLE_PLACES + "("
                + KEY_PLACE_ID + " INTEGER PRIMARY KEY," + KEY_PLACE_UID + " TEXT UNIQUE," + KEY_PLACE_NAME + " TEXT,"
                + KEY_PLACE_ADDRESS + " TEXT," + KEY_PLACE_PHONE_NUMBER + " TEXT" + ")";
        db.execSQL(CREATE_PLACES_TABLE);

        String CREATE_ITEMS_TABLE = "CREATE TABLE " + TABLE_ITEMS + "("
                + KEY_ITEM_ID + " INTEGER PRIMARY KEY," + KEY_ITEM_PLACE_UID + " TEXT," + KEY_ITEM_IMAGE_ID + " TEXT,"
                + KEY_ITEM_USER_UID + " TEXT," + KEY_ITEM_TITLE + " TEXT," + KEY_ITEM_DESCRIPTION + " TEXT,"
                + KEY_ITEM_CREATED_AT + " TEXT, " + "FOREIGN KEY(" + KEY_ITEM_PLACE_UID + ") REFERENCES " + TABLE_PLACES + "(" + KEY_PLACE_ID +
                "), FOREIGN KEY(" + KEY_ITEM_USER_UID + ") REFERENCES " + TABLE_USER + "(" + KEY_USER_UID +
                "), FOREIGN KEY(" + KEY_ITEM_IMAGE_ID + ") REFERENCES " + TABLE_IMAGES + "(" + KEY_IMAGE_ID + ")" + ")";
        db.execSQL(CREATE_ITEMS_TABLE);

        String CREATE_FOLLOWS_TABLE = "CREATE TABLE " + TABLE_FOLLOWS + "("
                + KEY_FOLLOW_PLACE + " TEXT UNIQUE )";
        db.execSQL(CREATE_FOLLOWS_TABLE);

        Log.d(TAG, "user and places and items and images DB tables created");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLACES);
        onCreate(db);
    }

    /**
     * Storing user details in database
     */
    public void addUser(String first_name, String last_name, String email, String uid, String created_at) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_USER_FIRST_NAME, first_name);
        values.put(KEY_USER_LAST_NAME, last_name);
        values.put(KEY_USER_EMAIL, email);
        values.put(KEY_USER_UID, uid);
        values.put(KEY_USER_CREATED_AT, created_at);

        long id = db.insert(TABLE_USER, null, values);
        db.close();

        Log.d(TAG, "New user inserted into sqlite: " + id);
    }

    /**
     * Storing item details in database
     */
    public void addItem(String place_uid, String image_id, String user_uid, String title, String description, String created_at) {
        if (!ifItemInDB(place_uid, image_id, user_uid, title, description, created_at)) {
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(KEY_ITEM_PLACE_UID, place_uid);
            values.put(KEY_ITEM_IMAGE_ID, image_id);
            values.put(KEY_ITEM_USER_UID, user_uid);
            values.put(KEY_ITEM_TITLE, title);
            values.put(KEY_ITEM_DESCRIPTION, description);
            values.put(KEY_ITEM_CREATED_AT, created_at);

            Log.d(TAG, "Insert values: " + values);
            long id = db.insert(TABLE_ITEMS, null, values);
            db.close();

            Log.d(TAG, "New item inserted into sqlite: " + id);
        } else {
            Log.d(TAG, "Item is already stored: " + title);
        }
    }

    /**
     * Storing image details in database
     */
    public void addImage(String db_id, String image) {
        if (!ifImageInDB(db_id, image)) {
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(KEY_IMAGE_DBID, db_id);
            values.put(KEY_IMAGE_IMAGE, image);

            Log.d(TAG, "Insert values: " + values);
            long id = db.insert(TABLE_IMAGES, null, values);
            db.close();

            Log.d(TAG, "New image inserted into sqlite: " + id);
        } else {
            Log.d(TAG, "Image is already stored: " + db_id);
        }
    }

    /**
     * Storing image details in database
     */
    public void addPlace(String uid, String name, String address, String phone) {
        if (!ifPlaceInDB(uid)) {
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(KEY_PLACE_UID, uid);
            values.put(KEY_PLACE_NAME, name);
            values.put(KEY_PLACE_ADDRESS, address);
            values.put(KEY_PLACE_PHONE_NUMBER, phone);

            Log.d(TAG, "Insert values: " + values);
            long id = db.insert(TABLE_PLACES, null, values);
            db.close();

            Log.d(TAG, "New place inserted into sqlite: " + id);
        } else {
            Log.d(TAG, "Place is already stored: " + uid);
        }
    }

    /**
     * Storing image details in database
     */
    public void addFollowedPlace(String place_uid) {
        if (!ifFollowedPlaceInDB(place_uid)) {
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(KEY_FOLLOW_PLACE, place_uid);

            Log.d(TAG, "Insert values: " + values);
            long id = db.insert(TABLE_FOLLOWS, null, values);
            db.close();

            Log.d(TAG, "New followed place inserted into sqlite: " + id);
        } else {
            Log.d(TAG, "Place is already followed: " + place_uid);
        }
    }

    public boolean ifItemInDB(String place_uid, String image_id, String user_uid, String title, String description, String created_at) throws SQLException {
        SQLiteDatabase db = this.getWritableDatabase();
        int count = -1;
        Cursor c = null;
        try {
            String query = "SELECT * FROM "
                    + TABLE_ITEMS + " WHERE " + KEY_ITEM_PLACE_UID + " = ? AND " + KEY_ITEM_IMAGE_ID + " = ? AND " + KEY_ITEM_USER_UID + " = ? AND " + KEY_ITEM_TITLE + " = ? AND " + KEY_ITEM_DESCRIPTION + " = ? AND " + KEY_ITEM_CREATED_AT + " = ? ";
            c = db.rawQuery(query, new String[]{place_uid, image_id, user_uid, title, description, created_at});
            if (c.moveToFirst()) {
                count = c.getInt(0);
            }
            Log.d(TAG, "query result: " + count);
            return count > 0;
        } finally {
            if (c != null) {
                c.close();
            }
        }
    }


    public boolean ifImageInDB(String id, String image) throws SQLException {
        SQLiteDatabase db = this.getWritableDatabase();
        int count = -1;
        Cursor c = null;
        try {
            String query = "SELECT * FROM "
                    + TABLE_IMAGES + " WHERE " + KEY_IMAGE_DBID + " = ? AND " + KEY_IMAGE_IMAGE + " = ?";
            c = db.rawQuery(query, new String[]{id, image});
            if (c.moveToFirst()) {
                count = c.getInt(0);
            }
            Log.d(TAG, "query result: " + count);
            return count > 0;
        } finally {
            if (c != null) {
                c.close();
            }
        }
    }

    public boolean ifPlaceInDB(String uid) throws SQLException {
        SQLiteDatabase db = this.getWritableDatabase();
        int count = -1;
        Cursor c = null;
        try {
            String query = "SELECT * FROM "
                    + TABLE_PLACES + " WHERE " + KEY_PLACE_UID + " = ?";
            c = db.rawQuery(query, new String[]{uid});
            if (c.moveToFirst()) {
                count = c.getInt(0);
            }
            Log.d(TAG, "query result: " + count);
            return count > 0;
        } finally {
            if (c != null) {
                c.close();
            }
        }
    }

    public boolean ifFollowedPlaceInDB(String uid) throws SQLException {
        SQLiteDatabase db = this.getWritableDatabase();
        int count = -1;
        Cursor c = null;
        try {
            String query = "SELECT * FROM "
                    + TABLE_FOLLOWS + " WHERE " + KEY_FOLLOW_PLACE + " = ?";
            c = db.rawQuery(query, new String[]{uid});
            if (c.moveToFirst()) {
                count = c.getInt(0);
            }
            Log.d(TAG, "query result: " + count);
            return count > 0;
        } finally {
            if (c != null) {
                c.close();
            }
        }
    }

    /**
     * Getting user data from database
     */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        String selectQuery = "SELECT  * FROM " + TABLE_USER;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            user.put("first_name", cursor.getString(1));
            user.put("last_name", cursor.getString(2));
            user.put("email", cursor.getString(3));
            user.put("uid", cursor.getString(4));
            user.put("created_at", cursor.getString(5));
        }
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching user from Sqlite: " + user.toString());

        return user;
    }

    /**
     * Getting place name by id
     */
    public HashMap<String, String> getPlaceName(String place_uid) {
        HashMap<String, String> name = new HashMap<String, String>();
        String selectQuery = "SELECT " + KEY_PLACE_NAME + " FROM " + TABLE_PLACES + " WHERE " + KEY_PLACE_UID + " = ?";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{place_uid});
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            name.put("name", cursor.getString(0));
        }
        cursor.close();
        db.close();
        // return name
        Log.d(TAG, "Fetching place name from Sqlite: " + place_uid + name.toString());

        return name;
    }

    /**
     * Getting place name by id
     */
    public HashMap<String, String> getPlaceInfo(String place_uid) {
        HashMap<String, String> name = new HashMap<String, String>();
        String selectQuery = "SELECT * FROM " + TABLE_PLACES + " WHERE " + KEY_PLACE_UID + " = ?";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{place_uid});
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            name.put("name", cursor.getString(2));
            name.put("address", cursor.getString(3));
            name.put("phone_number", cursor.getString(4));
        }
        cursor.close();
        db.close();
        // return name
        Log.d(TAG, "Fetching place name from Sqlite: " + place_uid + name.toString());

        return name;
    }


    /**
     * Getting max db_id from images
     */
    public HashMap<String, String> countImagesInSQLite() {
        HashMap<String, String> id = new HashMap<String, String>();
        String selectQuery = "SELECT MAX(" + KEY_IMAGE_DBID + ") FROM " + TABLE_IMAGES;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            if (cursor.getString(0) == null)
                id.put("max", "0");
            else
                id.put("max", cursor.getString(0));
        }
        cursor.close();
        db.close();

        Log.d(TAG, "Fetching max db_id from Sqlite: " + id.toString());

        return id;
    }

    /**
     * Getting max uid from places
     */
    public HashMap<String, String> countPlacesInSQLite() {
        HashMap<String, String> id = new HashMap<String, String>();
        String selectQuery = "SELECT MAX(" + KEY_PLACE_ID + ") FROM " + TABLE_PLACES;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            if (cursor.getString(0) == null)
                id.put("max", "0");
            else
                id.put("max", cursor.getString(0));
        }
        cursor.close();
        db.close();

        Log.d(TAG, "Fetching max db_id from Sqlite: " + id.toString());

        return id;
    }

    /**
     * Getting user data from database
     */
    public HashMap<String, String> getImageById(String db_id) {
        HashMap<String, String> image = new HashMap<String, String>();
        String selectQuery = "SELECT " + KEY_IMAGE_IMAGE + " FROM " + TABLE_IMAGES + " WHERE " + KEY_IMAGE_DBID + " = ?";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{db_id});
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            image.put("image", cursor.getString(0));
        }
        cursor.close();
        db.close();
        // return user
        //Log.d(TAG, "Fetching image from Sqlite: " + image.toString());

        return image;
    }

    /**
     * Getting items data from database
     */
    public ArrayList<HashMap<String, String>> getItems() {
        ArrayList<HashMap<String, String>> items = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> item = new HashMap<String, String>();
        String selectQuery = "SELECT  * FROM " + TABLE_ITEMS;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        if (cursor.moveToFirst()) {
            while (cursor.isAfterLast() == false) {
                item.put("place_uid", cursor.getString(1));
                item.put("user_uid", cursor.getString(2));
                item.put("title", cursor.getString(3));
                item.put("description", cursor.getString(4));
                item.put("created_at", cursor.getString(5));

                items.add(item);
                item = null;
                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching user from Sqlite: " + items.toString());

        return items;
    }

    /**
     * Getting items data from database
     */
    public ArrayList<HashMap<String, String>> getItemsByUser(String user_uid) {
        ArrayList<HashMap<String, String>> items = new ArrayList<HashMap<String, String>>();
        String selectQuery = "SELECT  * FROM " + TABLE_ITEMS + " WHERE " + KEY_ITEM_USER_UID + "='" + user_uid + "'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        Log.d(TAG, "Cursor count: " + cursor.getCount());
        // Move to first row
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> item = new HashMap<String, String>();
                item.put(KEY_ITEM_PLACE_UID, cursor.getString(cursor.getColumnIndex(KEY_ITEM_PLACE_UID)));
                item.put(KEY_ITEM_IMAGE_ID, cursor.getString(cursor.getColumnIndex(KEY_ITEM_IMAGE_ID)));
                item.put(KEY_ITEM_USER_UID, cursor.getString(cursor.getColumnIndex(KEY_ITEM_USER_UID)));
                item.put(KEY_ITEM_TITLE, cursor.getString(cursor.getColumnIndex(KEY_ITEM_TITLE)));
                item.put(KEY_ITEM_DESCRIPTION, cursor.getString(cursor.getColumnIndex(KEY_ITEM_DESCRIPTION)));
                item.put(KEY_ITEM_CREATED_AT, cursor.getString(cursor.getColumnIndex(KEY_ITEM_CREATED_AT)));

                Log.d(TAG, "Current item fetch from Sqlite: " + item.toString());

                items.add(item);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        // return items
        Log.d(TAG, "Fetching items from Sqlite: " + items.toString());

        return items;
    }

    /**
     * Getting items data from database
     */
    public ArrayList<HashMap<String, String>> getItemsByTitle(String title) {
        ArrayList<HashMap<String, String>> items = new ArrayList<HashMap<String, String>>();
        String selectQuery = "SELECT  * FROM " + TABLE_ITEMS + " WHERE " + KEY_ITEM_TITLE + " LIKE '%" + title + "%'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        Log.d(TAG, "Cursor count: " + cursor.getCount());
        // Move to first row
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> item = new HashMap<String, String>();
                item.put(KEY_ITEM_PLACE_UID, cursor.getString(cursor.getColumnIndex(KEY_ITEM_PLACE_UID)));
                item.put(KEY_ITEM_IMAGE_ID, cursor.getString(cursor.getColumnIndex(KEY_ITEM_IMAGE_ID)));
                item.put(KEY_ITEM_USER_UID, cursor.getString(cursor.getColumnIndex(KEY_ITEM_USER_UID)));
                item.put(KEY_ITEM_TITLE, cursor.getString(cursor.getColumnIndex(KEY_ITEM_TITLE)));
                item.put(KEY_ITEM_DESCRIPTION, cursor.getString(cursor.getColumnIndex(KEY_ITEM_DESCRIPTION)));
                item.put(KEY_ITEM_CREATED_AT, cursor.getString(cursor.getColumnIndex(KEY_ITEM_CREATED_AT)));

                Log.d(TAG, "Current item fetch from Sqlite: " + item.toString());

                items.add(item);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        // return items
        Log.d(TAG, "Fetching items from Sqlite: " + items.toString());

        return items;
    }

    /**
     * Getting items data from database
     */
    public ArrayList<HashMap<String, String>> getFollowedPlaces() {
        ArrayList<HashMap<String, String>> places = new ArrayList<HashMap<String, String>>();
        String selectQuery = "SELECT  " + TABLE_PLACES + ".* FROM " + TABLE_PLACES + " LEFT JOIN " + TABLE_FOLLOWS + " ON " +
                TABLE_FOLLOWS + "." + KEY_FOLLOW_PLACE + "=" + TABLE_PLACES + "." + KEY_PLACE_UID + " WHERE " +
                TABLE_FOLLOWS + "." + KEY_FOLLOW_PLACE + "=" + TABLE_PLACES + "." + KEY_PLACE_UID;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        Log.d(TAG, "Cursor count: " + cursor.getCount());
        // Move to first row
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> place = new HashMap<String, String>();
                place.put(KEY_PLACE_UID, cursor.getString(cursor.getColumnIndex(KEY_PLACE_UID)));
                place.put(KEY_PLACE_NAME, cursor.getString(cursor.getColumnIndex(KEY_PLACE_NAME)));
                place.put(KEY_PLACE_ADDRESS, cursor.getString(cursor.getColumnIndex(KEY_PLACE_ADDRESS)));
                place.put(KEY_PLACE_PHONE_NUMBER, cursor.getString(cursor.getColumnIndex(KEY_PLACE_PHONE_NUMBER)));

                Log.d(TAG, "Current item fetch from Sqlite: " + place.toString());

                places.add(place);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        // return items
        Log.d(TAG, "Fetching items from Sqlite: " + places.toString());

        return places;
    }

    /**
     * Remove followed place
     */
    public boolean removeFollowedPlace(String place_uid) {
        String selectQuery = "DELETE FROM " + TABLE_FOLLOWS + " WHERE " + KEY_FOLLOW_PLACE + "='" + place_uid + "'";

        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL(selectQuery);

        if (!ifFollowedPlaceInDB(place_uid)) {
            Log.d(TAG, "Removed followed place from Sqlite: " + place_uid);
            return true;
        } else {
            Log.d(TAG, "Something went wrong when deleting followed place from Sqlite: " + place_uid);
            return false;
        }
    }

    /**
     * Re crate database Delete all tables and create them again
     */
    public void deleteTables() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_USER, null, null);
        db.delete(TABLE_ITEMS, null, null);
        db.delete(TABLE_PLACES, null, null);
        db.delete(TABLE_IMAGES, null, null);
        db.delete(TABLE_FOLLOWS, null, null);
        db.close();

        Log.d(TAG, "Deleted all user info from sqlite");
    }

}