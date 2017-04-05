package com.iemqra.bme.lostnfound.helper;

import android.os.Parcel;
import android.os.Parcelable;

public class PlaceFollowHandler implements Parcelable {

    public static final Creator<PlaceFollowHandler> CREATOR = new Creator<PlaceFollowHandler>() {
        public PlaceFollowHandler createFromParcel(Parcel in) {
            PlaceFollowHandler place = new PlaceFollowHandler();
            //item.id = in.readInt();
            place.place_uid = in.readString();
            place.name = in.readString();
            place.address = in.readString();
            place.phone = in.readString();
            return place;
        }

        public PlaceFollowHandler[] newArray(int size) {
            return new PlaceFollowHandler[size];
        }
    };
    public String place_uid;
    public String name;
    public String address;
    public String phone;

    public PlaceFollowHandler() {
    }

    public PlaceFollowHandler(String place_uid, String name, String address, String phone) {
        this.place_uid = place_uid;
        this.name = name;
        this.address = address;
        this.phone = phone;
    }

    public String getPlace_uid() {
        return place_uid;
    }

    public void setPlace_uid(String place_uid) {
        this.place_uid = place_uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public boolean equals(Object obj) {
        return (this.place_uid.equals(((PlaceFollowHandler) obj).place_uid)
                && this.name.equals(((PlaceFollowHandler) obj).name)
                && this.address.equals(((PlaceFollowHandler) obj).address)
                && this.phone.equals(((PlaceFollowHandler) obj).phone));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        //dest.writeString(Integer.toString(id));
        dest.writeString(place_uid);
        dest.writeString(name);
        dest.writeString(address);
        dest.writeString(phone);
    }
}
