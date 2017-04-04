package com.iemqra.bme.lostnfound.helper;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.iemqra.bme.lostnfound.R;

import java.util.List;


public class PlaceFollowAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<PlaceFollowHandler> placeList;

    public PlaceFollowAdapter(Activity activity, List<PlaceFollowHandler> itemList) {
        this.activity = activity;
        this.placeList = itemList;
    }

    @Override
    public int getCount() {
        return placeList.size();
    }

    @Override
    public Object getItem(int location) {
        return placeList.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.item_following, null);

        TextView placeName = (TextView) convertView.findViewById(R.id.following_name);

        placeName.setText(placeList.get(position).name);

        return convertView;
    }
}
