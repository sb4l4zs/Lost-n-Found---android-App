package com.iemqra.bme.lostnfound.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("unique_id")
    @Expose
    private String uniqueId;
    @SerializedName("first_name")
    @Expose
    private String firstName;
    @SerializedName("last_name")
    @Expose
    private String lastName;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("encrypted_password")
    @Expose
    private String encryptedPassword;
    @SerializedName("salt")
    @Expose
    private String salt;
    @SerializedName("fb_id")
    @Expose
    private String fbId;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("gcm_regid")
    @Expose
    private String gcmRegid;

    public User() {
    }

    public User(String firstName, String lastName, String email, String salt, String encryptedPassword) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.salt = salt;
        this.encryptedPassword = encryptedPassword;
    }

    public User(Integer id, String uniqueId, String firstName, String lastName, String email, String encryptedPassword, String salt, String fbId, String createdAt, String updatedAt, String gcmRegid) {
        this.id = id;
        this.uniqueId = uniqueId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.encryptedPassword = encryptedPassword;
        this.salt = salt;
        this.fbId = fbId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.gcmRegid = gcmRegid;
    }

    public User(String email, String password) {
        this.email = email;
        this.encryptedPassword = password;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
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

    public String getEncryptedPassword() {
        return encryptedPassword;
    }

    public void setEncryptedPassword(String encryptedPassword) {
        this.encryptedPassword = encryptedPassword;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getFbId() {
        return fbId;
    }

    public void setFbId(String fbId) {
        this.fbId = fbId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getGcmRegid() {
        return gcmRegid;
    }

    public void setGcmRegid(String gcmRegid) {
        this.gcmRegid = gcmRegid;
    }

}