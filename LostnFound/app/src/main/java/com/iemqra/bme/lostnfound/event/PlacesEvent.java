package com.iemqra.bme.lostnfound.event;

import com.iemqra.bme.lostnfound.model.Place;

import java.util.List;

public class PlacesEvent extends BaseEvent {
    private List<Place> places;

    public PlacesEvent() {
    }

    public PlacesEvent(List<Place> places) {
        this.places = places;
    }

    public List<Place> getPlaces() {
        return places;
    }

    public void setPlaces(List<Place> places) {
        this.places = places;
    }
}
