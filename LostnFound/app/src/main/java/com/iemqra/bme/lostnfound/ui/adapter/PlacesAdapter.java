package com.iemqra.bme.lostnfound.ui.adapter;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.iemqra.bme.lostnfound.R;
import com.iemqra.bme.lostnfound.api.ApiClient;
import com.iemqra.bme.lostnfound.manager.DataManager;
import com.iemqra.bme.lostnfound.model.APIHelper.RemovePlaceFollowData;
import com.iemqra.bme.lostnfound.model.Place;
import com.iemqra.bme.lostnfound.ui.activity.MainActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PlacesAdapter extends RecyclerView.Adapter<PlacesAdapter.ViewHolder> {

    private Context context;
    private List<Place> placesList;

    public PlacesAdapter(Context context, List<Place> placesList) {
        this.context = context;
        this.placesList = placesList;
    }

    public PlacesAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_following, viewGroup, false);
        context = viewGroup.getContext();
        return new PlacesAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PlacesAdapter.ViewHolder holder, int position) {
        final Place place = placesList.get(position);
        holder.tvName.setText(place.getName());
        holder.btCallPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                context.startActivity(
                        new Intent(Intent.ACTION_DIAL).setData(Uri.parse("tel:" + place.getPhoneNumber())));
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                deleteFollowedPlace(place.getUniqueId());
                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return placesList.size();
    }

    private void deleteFollowedPlace(final String placeId) {
        new AlertDialog.Builder(context)
                .setTitle("Unfollow place")
                .setMessage("Are you sure you want to remove this followed place?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (((MainActivity) context).checkInternetConnection()) {
                            ApiClient.removePlaceFollow(new RemovePlaceFollowData(DataManager.INSTANCE.getUser().get("uid"), placeId));
                        } else {
                            DataManager.INSTANCE.getPlaceFollowingsToDelete().add(new RemovePlaceFollowData(DataManager.INSTANCE.getUser().get("uid"), placeId));
                        }
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.following_name)
        TextView tvName;
        @BindView(R.id.following_call_place)
        ImageButton btCallPlace;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
