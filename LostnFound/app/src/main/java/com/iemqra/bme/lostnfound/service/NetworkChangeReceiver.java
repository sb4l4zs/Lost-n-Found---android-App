package com.iemqra.bme.lostnfound.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.iemqra.bme.lostnfound.api.ApiClient;
import com.iemqra.bme.lostnfound.manager.DataManager;
import com.iemqra.bme.lostnfound.model.APIHelper.RemoveItemData;
import com.iemqra.bme.lostnfound.model.APIHelper.RemovePlaceFollowData;

import java.util.ArrayList;
import java.util.List;

public class NetworkChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        if (isConnected) {
            if (!DataManager.INSTANCE.getItemsToDelete().isEmpty()) {
                List<RemoveItemData> itemsToRemove = DataManager.INSTANCE.getItemsToDelete();
                for (RemoveItemData itemToRemove : itemsToRemove) {
                    ApiClient.removeItem(itemToRemove);
                }
                DataManager.INSTANCE.setItemsToDelete(new ArrayList<RemoveItemData>());
            }
            if (!DataManager.INSTANCE.getPlaceFollowingsToDelete().isEmpty()) {
                List<RemovePlaceFollowData> placesToRemove = DataManager.INSTANCE.getPlaceFollowingsToDelete();
                for (RemovePlaceFollowData placeToRemove : placesToRemove) {
                    ApiClient.removePlaceFollow(placeToRemove);
                }
                DataManager.INSTANCE.setPlaceFollowingsToDelete(new ArrayList<RemovePlaceFollowData>());
            }
        }
    }
}
