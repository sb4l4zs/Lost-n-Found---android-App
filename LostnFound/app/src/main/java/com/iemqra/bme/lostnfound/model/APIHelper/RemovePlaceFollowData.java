package com.iemqra.bme.lostnfound.model.APIHelper;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RemovePlaceFollowData {
    @SerializedName("userId")
    @Expose
    private String userId;
    @SerializedName("placeId")
    @Expose
    private String placeId;

    public RemovePlaceFollowData() {
    }

    public RemovePlaceFollowData(String userId, String placeId) {
        this.userId = userId;
        this.placeId = placeId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }
}
