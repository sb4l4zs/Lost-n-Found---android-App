package com.iemqra.bme.lostnfound.event;


import com.iemqra.bme.lostnfound.model.Place;

public class PlaceEvent extends BaseEvent {
    private Place place;

    public PlaceEvent() {
    }

    public PlaceEvent(Place place) {
        this.place = place;
    }

    public Place getPlace() {
        return place;
    }

    public void setPlace(Place place) {
        this.place = place;
    }
}
