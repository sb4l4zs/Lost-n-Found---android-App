package com.iemqra.bme.lostnfound.event;

import com.iemqra.bme.lostnfound.model.APIHelper.UserData;

public class UserEvent extends BaseEvent {
    private UserData userData;

    public UserEvent() {
    }

    public UserEvent(UserData userData) {
        this.userData = userData;
    }

    public UserData getUserData() {
        return userData;
    }

    public void setUserData(UserData userData) {
        this.userData = userData;
    }
}
