package com.iemqra.bme.lostnfound.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.iemqra.bme.lostnfound.R;
import com.iemqra.bme.lostnfound.api.ApiClient;
import com.iemqra.bme.lostnfound.api.SQLiteApi;
import com.iemqra.bme.lostnfound.event.BaseEvent;
import com.iemqra.bme.lostnfound.event.PlacesEvent;
import com.iemqra.bme.lostnfound.helper.LocationHandler;
import com.iemqra.bme.lostnfound.helper.PlaceHandler;
import com.iemqra.bme.lostnfound.helper.SQLiteHandler;
import com.iemqra.bme.lostnfound.manager.DataManager;
import com.iemqra.bme.lostnfound.model.APIHelper.RemovePlaceFollowData;
import com.iemqra.bme.lostnfound.ui.activity.MainActivity;
import com.iemqra.bme.lostnfound.ui.adapter.PlacesAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.facebook.FacebookSdk.getApplicationContext;


public class FollowingsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static SQLiteHandler db;
    public int PLACE_PICKER_REQUEST = 0;
    @BindView(R.id.fragment_followings_follow_field)
    TextView tv;
    @BindView(R.id.fragment_followings_empty)
    TextView tvEmpty;
    @BindView(R.id.fragment_followings_list_view)
    RecyclerView recyclerViewItems;
    @BindView(R.id.fragment_followings_swipe_refresh)
    SwipeRefreshLayout swipeRefreshLayout;
    private SQLiteApi localDbApi;
    private LocationHandler location = new LocationHandler();
    private PlaceHandler place = new PlaceHandler();
    private PlacesAdapter placesAdapter;
    private List<com.iemqra.bme.lostnfound.model.Place> placesList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_followings, container, false);
        ButterKnife.bind(this, view);

        localDbApi = new SQLiteApi(getApplicationContext());
        db = new SQLiteHandler(getActivity().getApplicationContext());

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewItems.setLayoutManager(llm);

        placesList = new ArrayList<>();
        placesAdapter = new PlacesAdapter(getContext(), placesList);
        recyclerViewItems.setAdapter(placesAdapter);

        swipeRefreshLayout.setOnRefreshListener(this);

        getFollowedPlaces();

        return view;
    }

    @OnClick(R.id.fragment_followings_follow)
    public void handleAddButtonClick() {
        followLocation(new RemovePlaceFollowData(DataManager.INSTANCE.getUser().get("uid"), place.getUid()));
    }

    @OnClick(R.id.fragment_followings_pick_place)
    public void handlePlacePickerButtonClick() {
        location = MainActivity.getLocation();
        double num = 0.001;
        LatLngBounds currentLocation = new LatLngBounds(new LatLng(location.getLatitude() - num, location.getLongitude() - num), new LatLng(location.getLatitude() + num, location.getLongitude() + num));
        try {
            PlacePicker.IntentBuilder intentBuilder =
                    new PlacePicker.IntentBuilder();
            intentBuilder.setLatLngBounds(currentLocation);
            Intent intent = intentBuilder.build(getActivity());
            startActivityForResult(intent, PLACE_PICKER_REQUEST);

        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(final PlacesEvent event) {
        if (event.getThrowable() != null) {
            event.getThrowable().printStackTrace();
        } else if (event.getMessage().equals("places")) {
            if (event.getPlaces() != null) {
                List<com.iemqra.bme.lostnfound.model.Place> places = event.getPlaces();
                placesList.clear();
                placesList.addAll(places);
                placesAdapter.notifyDataSetChanged();

                if (placesList.isEmpty()) {
                    recyclerViewItems.setVisibility(View.GONE);
                    tvEmpty.setVisibility(View.VISIBLE);
                } else {
                    recyclerViewItems.setVisibility(View.VISIBLE);
                    tvEmpty.setVisibility(View.GONE);
                }

                for (com.iemqra.bme.lostnfound.model.Place place : placesList) {
                    db.addPlace(place.getUniqueId(), place.getName(), place.getAddress(), place.getPhoneNumber());
                    db.addFollowedPlace(place.getUniqueId());
                }
            } else {
                Toast.makeText(getApplicationContext(), event.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        swipeRefreshLayout.setRefreshing(false);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(final BaseEvent event) {
        if (event.getThrowable() != null) {
            getFollowedPlacesFromSQLite();
            event.getThrowable().printStackTrace();
            Toast.makeText(getApplicationContext(), "No internet connection, data loaded from local database!", Toast.LENGTH_SHORT).show();
        } else if (event.getMessage().equals("placeFollow")) {
            Toast.makeText(getActivity().getApplicationContext(), "Now you're following " + tv.getText(), Toast.LENGTH_SHORT).show();
            getFollowedPlaces();
        } else if (event.getMessage().equals("removePlace")) {
            getFollowedPlaces();
        }
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        if (((MainActivity) getActivity()).checkInternetConnection()) {
            getFollowedPlaces();
            placesAdapter.notifyDataSetChanged();
        } else {
            getFollowedPlacesFromSQLite();
            placesAdapter.notifyDataSetChanged();
        }
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PLACE_PICKER_REQUEST) {
                final Place pickedPlace = PlacePicker.getPlace(data, getActivity());

                place.setUid(pickedPlace.getId().trim());
                place.setName(String.valueOf(pickedPlace.getName()).trim());
                place.setAddress(String.valueOf(pickedPlace.getAddress()).trim());
                place.setPhoneNumber(String.valueOf(pickedPlace.getPhoneNumber()).trim());

                tv.setText(place.getName());
            }
        }
    }

    private void getFollowedPlaces() {
        ApiClient.getFollowedPlaces(DataManager.INSTANCE.getUser().get("uid"));
    }

    private void followLocation(RemovePlaceFollowData removePlaceFollowData) {
        ApiClient.addPlaceFollow(removePlaceFollowData);
    }

    private void getFollowedPlacesFromSQLite() {
        List<com.iemqra.bme.lostnfound.model.Place> placesFromLocalDb = localDbApi.getFollowedPlacesFromSQLite();
        if (placesFromLocalDb != null && placesFromLocalDb.size() > 0) {
            recyclerViewItems.setVisibility(View.VISIBLE);
            tvEmpty.setVisibility(View.GONE);
            placesList.clear();
            placesList.addAll(placesFromLocalDb);
            placesAdapter.notifyDataSetChanged();
        } else {
            recyclerViewItems.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.VISIBLE);
        }
    }
}

