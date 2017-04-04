package com.iemqra.bme.lostnfound.model.APIHelper;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ItemUploadData {
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("user_uid")
    @Expose
    private String userUid;
    @SerializedName("image_id")
    @Expose
    private Integer imageId;
    @SerializedName("place_uid")
    @Expose
    private String placeUid;

    public ItemUploadData() {
    }

    public ItemUploadData(String title, String description, String userUid, Integer imageId, String placeUid) {
        this.title = title;
        this.description = description;
        this.userUid = userUid;
        this.imageId = imageId;
        this.placeUid = placeUid;
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

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    public Integer getImageId() {
        return imageId;
    }

    public void setImageId(Integer imageId) {
        this.imageId = imageId;
    }

    public String getPlaceUid() {
        return placeUid;
    }

    public void setPlaceUid(String placeUid) {
        this.placeUid = placeUid;
    }
}
