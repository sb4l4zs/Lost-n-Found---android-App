package com.iemqra.bme.lostnfound.model.APIHelper;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GcmData {
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("token")
    @Expose
    private String token;

    public GcmData() {
    }

    public GcmData(String email, String token) {
        this.email = email;
        this.token = token;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
