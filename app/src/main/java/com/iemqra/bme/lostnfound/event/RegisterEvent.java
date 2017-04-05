package com.iemqra.bme.lostnfound.event;

import com.iemqra.bme.lostnfound.model.User;

/**
 * Created by Balazs on 2017. 03. 21..
 */

public class RegisterEvent extends BaseEvent {
    private User user;

    public RegisterEvent() {
    }

    public RegisterEvent(int code, String message, User user, Throwable throwable) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
