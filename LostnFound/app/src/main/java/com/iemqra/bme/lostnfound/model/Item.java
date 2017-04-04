package com.iemqra.bme.lostnfound.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Balazs on 2017. 03. 24..
 */

public class Item {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("place_uid")
    @Expose
    private String placeUid;
    @SerializedName("image_id")
    @Expose
    private Integer imageId;
    @SerializedName("image_path")
    @Expose
    private String imagePath;
    @SerializedName("user_uid")
    @Expose
    private String userUid;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("created_at")
    @Expose
    private String createdAt;

    public Item() {
    }

    public Item(Integer id, String placeUid, Integer imageId, String imagePath, String userUid, String title, String description, String createdAt) {
        this.id = id;
        this.placeUid = placeUid;
        this.imageId = imageId;
        this.imagePath = imagePath;
        this.userUid = userUid;
        this.title = title;
        this.description = description;
        this.createdAt = createdAt;
    }

    public Item(String title, String description, String userId, Integer imageId, String placeId) {
        this.title = title;
        this.description = description;
        this.userUid = userId;
        this.imageId = imageId;
        this.placeUid = placeId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPlaceUid() {
        return placeUid;
    }

    public void setPlaceUid(String placeUid) {
        this.placeUid = placeUid;
    }

    public Integer getImageId() {
        return imageId;
    }

    public void setImageId(Integer imageId) {
        this.imageId = imageId;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
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

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
