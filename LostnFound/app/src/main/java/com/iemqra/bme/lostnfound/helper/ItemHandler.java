package com.iemqra.bme.lostnfound.helper;

import android.os.Parcel;
import android.os.Parcelable;

import com.iemqra.bme.lostnfound.model.Item;

public class ItemHandler implements Parcelable {
    public static final Parcelable.Creator<ItemHandler> CREATOR = new Parcelable.Creator<ItemHandler>() {
        public ItemHandler createFromParcel(Parcel in) {
            ItemHandler item = new ItemHandler();
            //item.id = in.readInt();
            item.place_uid = in.readString();
            item.image_id = in.readString();
            item.image_path = in.readString();
            item.user_uid = in.readString();
            item.title = in.readString();
            item.description = in.readString();
            item.created_at = in.readString();
            return item;
        }

        public ItemHandler[] newArray(int size) {
            return new ItemHandler[size];
        }
    };
    private int id;
    private String place_uid;
    private String image_id;
    private String image_path;
    private String user_uid;
    private String title;
    private String description;
    private String created_at;

    public ItemHandler() {
    }

    public ItemHandler(Item item) {
        this.place_uid = item.getPlaceUid();
        this.image_id = item.getImageId().toString();
        this.image_path = item.getImagePath();
        this.user_uid = item.getUserUid();
        this.title = item.getTitle();
        this.description = item.getDescription();
        this.created_at = item.getCreatedAt();
    }

    public ItemHandler(String place_uid, String image_id, String image_path, String user_uid, String title, String description, String created_at) {
        this.place_uid = place_uid;
        this.image_id = image_id;
        this.image_path = image_path;
        this.user_uid = user_uid;
        this.title = title;
        this.description = description;
        this.created_at = created_at;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPlace_uid() {
        return place_uid;
    }

    public void setPlace_uid(String place_uid) {
        this.place_uid = place_uid;
    }

    public String getImage_id() {
        return image_id;
    }

    public void setImage_id(String image_id) {
        this.image_id = image_id;
    }

    public String getImage_path() {
        return image_path;
    }

    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }

    public String getUser_uid() {
        return user_uid;
    }

    public void setUser_uid(String user_uid) {
        this.user_uid = user_uid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    @Override
    public boolean equals(Object obj) {
        return (this.place_uid.equals(((ItemHandler) obj).place_uid)
                && this.image_id.equals(((ItemHandler) obj).image_id)
                && this.image_path.equals(((ItemHandler) obj).image_path)
                && this.user_uid.equals(((ItemHandler) obj).user_uid)
                && this.title.equals(((ItemHandler) obj).title)
                && this.description.equals(((ItemHandler) obj).description)
                && this.created_at.equals(((ItemHandler) obj).created_at));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        //dest.writeString(Integer.toString(id));
        dest.writeString(place_uid);
        dest.writeString(image_id);
        dest.writeString(image_path);
        dest.writeString(user_uid);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(created_at);
    }
}
