package com.iemqra.bme.lostnfound.event;

import com.iemqra.bme.lostnfound.model.Item;

import java.util.List;

/**
 * Created by Balazs on 2017. 03. 24..
 */

public class ItemsEvent extends BaseEvent {
    private List<Item> items;

    public ItemsEvent() {
    }

    public ItemsEvent(List<Item> items) {
        this.items = items;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }
}
