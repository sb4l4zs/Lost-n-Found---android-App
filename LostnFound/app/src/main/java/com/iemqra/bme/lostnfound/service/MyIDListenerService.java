package com.iemqra.bme.lostnfound.service;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.google.android.gms.iid.InstanceIDListenerService;
import com.iemqra.bme.lostnfound.R;
import com.iemqra.bme.lostnfound.api.ApiClient;
import com.iemqra.bme.lostnfound.manager.DataManager;

import java.io.IOException;

public class MyIDListenerService extends InstanceIDListenerService {
    @Override
    public void onTokenRefresh() {
        String token = null;
        InstanceID instanceID = InstanceID.getInstance(MyIDListenerService.this);
        try {
            token = instanceID.getToken(MyIDListenerService.this.getString(R.string.gcm_defaultSenderId), GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //send token to app server
        ApiClient.addGcmToken(DataManager.INSTANCE.getUser().get("uid"), token);
    }
}