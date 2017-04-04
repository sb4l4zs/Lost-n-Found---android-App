package com.iemqra.bme.lostnfound.model.APIHelper;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Balazs on 2017. 04. 03..
 */

public class RemoveItemData {
    @SerializedName("userId")
    @Expose
    private String userId;
    @SerializedName("itemId")
    @Expose
    private Integer itemId;

    public RemoveItemData() {
    }

    public RemoveItemData(String userId, Integer itemId) {
        this.userId = userId;
        this.itemId = itemId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }
}
