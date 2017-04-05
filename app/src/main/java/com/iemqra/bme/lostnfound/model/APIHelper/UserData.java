package com.iemqra.bme.lostnfound.model.APIHelper;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Balazs on 2017. 03. 26..
 */

public class UserData {
    @SerializedName("firstName")
    @Expose
    private String firstName;
    @SerializedName("lastName")
    @Expose
    private String lastName;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("facebookId")
    @Expose
    private String facebookId;

    public UserData() {
    }

    public UserData(String firstName, String lastName, String email, String facebookId) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.facebookId = facebookId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }
}
