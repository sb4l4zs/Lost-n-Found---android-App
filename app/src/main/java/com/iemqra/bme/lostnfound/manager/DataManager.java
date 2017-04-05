package com.iemqra.bme.lostnfound.manager;

import com.iemqra.bme.lostnfound.model.APIHelper.RemoveItemData;
import com.iemqra.bme.lostnfound.model.APIHelper.RemovePlaceFollowData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Balazs on 2017. 04. 03..
 */

public enum DataManager {
    INSTANCE;

    HashMap<String, String> user;
    private List<RemovePlaceFollowData> placeFollowingsToDelete = new ArrayList<>();
    private List<RemoveItemData> itemsToDelete = new ArrayList<>();

    public List<RemovePlaceFollowData> getPlaceFollowingsToDelete() {
        return placeFollowingsToDelete;
    }

    public void setPlaceFollowingsToDelete(List<RemovePlaceFollowData> placeFollowingsToDelete) {
        this.placeFollowingsToDelete = placeFollowingsToDelete;
    }

    public List<RemoveItemData> getItemsToDelete() {
        return itemsToDelete;
    }

    public void setItemsToDelete(List<RemoveItemData> itemsToDelete) {
        this.itemsToDelete = itemsToDelete;
    }

    public HashMap<String, String> getUser() {
        return user;
    }

    public void setUser(HashMap<String, String> user) {
        this.user = user;
    }
}
