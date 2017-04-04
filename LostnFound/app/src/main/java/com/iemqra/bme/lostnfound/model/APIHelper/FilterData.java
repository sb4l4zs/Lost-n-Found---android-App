package com.iemqra.bme.lostnfound.model.APIHelper;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Balazs on 2017. 03. 24..
 */

public class FilterData {
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("place")
    @Expose
    private String place;
    @SerializedName("user")
    @Expose
    private String user;
    @SerializedName("startIndex")
    @Expose
    private Integer startIndex;
    @SerializedName("queryAmount")
    @Expose
    private Integer queryAmount;

    public FilterData() {
    }

    public FilterData(String title, String description, String place, String user, Integer startIndex, Integer queryAmount) {
        this.title = title;
        this.description = description;
        this.place = place;
        this.user = user;
        this.startIndex = startIndex;
        this.queryAmount = queryAmount;
    }

    public FilterData(int startIndex, int queryAmount, String user_uid) {
        this.user = user_uid;
        this.startIndex = startIndex;
        this.queryAmount = queryAmount;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Integer getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(Integer startIndex) {
        this.startIndex = startIndex;
    }

    public Integer getQueryAmount() {
        return queryAmount;
    }

    public void setQueryAmount(Integer queryAmount) {
        this.queryAmount = queryAmount;
    }
}
