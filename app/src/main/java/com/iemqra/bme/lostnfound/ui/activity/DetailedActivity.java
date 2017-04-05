package com.iemqra.bme.lostnfound.ui.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.iemqra.bme.lostnfound.R;
import com.iemqra.bme.lostnfound.api.ApiClient;
import com.iemqra.bme.lostnfound.api.SQLiteApi;
import com.iemqra.bme.lostnfound.event.BaseEvent;
import com.iemqra.bme.lostnfound.event.PlaceEvent;
import com.iemqra.bme.lostnfound.event.UserEvent;
import com.iemqra.bme.lostnfound.helper.AlertDialogManager;
import com.iemqra.bme.lostnfound.helper.ItemHandler;
import com.iemqra.bme.lostnfound.manager.DataManager;
import com.iemqra.bme.lostnfound.model.APIHelper.RemovePlaceFollowData;
import com.iemqra.bme.lostnfound.model.APIHelper.UserData;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DetailedActivity extends Activity {

    @BindView(R.id.activity_detailed_title)
    TextView tv_title;
    @BindView(R.id.activity_detailed_founder)
    TextView tv_founder;
    @BindView(R.id.activity_detailed_location)
    TextView tv_location;
    @BindView(R.id.activity_detailed_description)
    TextView tv_description;
    @BindView(R.id.activity_detailed_image)
    ImageView iv_image;

    private SQLiteApi localDbApi;
    private UserData userData;
    private ItemHandler item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_frame);
        ButterKnife.bind(this);

        localDbApi = new SQLiteApi(getApplicationContext());
        AlertDialogManager alert = new AlertDialogManager();

        item = getIntent().getParcelableExtra("item");

        if (item.getUser_uid() != null) {
            getUserData(item.getUser_uid());
            getPlaceData(item.getPlace_uid());

            tv_title.setText(item.getTitle());
            tv_description.setText(item.getDescription());
            if (!item.getImage_path().isEmpty()) {
                Picasso.with(this).load(ApiClient.API_URL + item.getImage_path().substring(1)).into(iv_image);
            } else {
                iv_image.setImageResource(R.drawable.no_picture);
            }

            FloatingActionButton fabContact = (FloatingActionButton) findViewById(R.id.activity_detailed_button_email);
            fabContact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                            "mailto", userData.getEmail(), null));
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.contact_1) + item.getTitle());
                    emailIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.contact_2) + userData.getFirstName());
                    startActivity(Intent.createChooser(emailIntent, "Send email..."));
                    Snackbar.make(view, "Sending mail..", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
            });

            FloatingActionButton fabSocialShare = (FloatingActionButton) findViewById(R.id.activity_detailed_button_social_share);
            fabSocialShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);

                    sharingIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    sharingIntent.putExtra(Intent.EXTRA_STREAM, iv_image.getDrawingCache());
                    String shareBody = getResources().getString(R.string.sharing_1) + " " + item.getTitle() + " " + getResources().getString(R.string.sharing_2) + " " + tv_location.getText() + getResources().getString(R.string.sharing_3);
                    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Lost n Found");
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                    sharingIntent.setType("image/png");

                    //sharingIntent.setType("text/plain");
                    //sharingIntent.putExtra(Intent.EXTRA_STREAM, MyItemsFragment.imageStringToBitmap(item.image_id));
                    startActivity(Intent.createChooser(sharingIntent, "Share via"));
                    Snackbar.make(view, "facebook", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
            });

            tv_location.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!tv_location.getText().equals("")) {
                        new AlertDialog.Builder(DetailedActivity.this)
                                .setTitle("Follow Google Place")
                                .setMessage("Are you sure you want to follow " + tv_location.getText() + "?")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        followLocation(new RemovePlaceFollowData(DataManager.INSTANCE.getUser().get("uid"), item.getPlace_uid()));
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
                }
            });
        }
    }

    @OnClick(R.id.activity_detailed_image)
    public void handleOnPictureClick() {
        if (Integer.valueOf(item.getImage_id()) != 0) {
            Intent intent = new Intent(DetailedActivity.this, ImageActivity.class);
            intent.putExtra("image", item.getImage_path());
            startActivity(intent);
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
    public void onEventMainThread(final PlaceEvent event) {
        if (event.getThrowable() != null) {
            event.getThrowable().printStackTrace();
            tv_location.setText(localDbApi.getPlaceNameByUid(item.getPlace_uid()));
        } else if (event.getMessage().equals("placeData")) {
            tv_location.setText(event.getPlace().getName());
        } else if (event.getMessage().equals("placeFollow")) {
            Toast.makeText(this, "Now you're following " + tv_location.getText(), Toast.LENGTH_SHORT).show();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(final UserEvent event) {
        if (event.getThrowable() != null) {
            event.getThrowable().printStackTrace();
            Toast.makeText(getApplicationContext(), event.getMessage(), Toast.LENGTH_SHORT).show();
        } else if (event.getMessage().equals("userData")) {
            userData = event.getUserData();
            tv_founder.setText(userData.getFirstName() + " " + userData.getLastName());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(final BaseEvent event) {
        if (event.getThrowable() != null) {
            event.getThrowable().printStackTrace();
            Toast.makeText(getApplicationContext(), event.getMessage(), Toast.LENGTH_SHORT).show();
        } else if (event.getMessage().equals("placeFollow")) {
            Toast.makeText(this, "Now you're following " + tv_location.getText(), Toast.LENGTH_SHORT).show();
        }
    }

    private void getPlaceData(String placeId) {
        ApiClient.getPlaceData(placeId);
    }

    private void getUserData(String userId) {
        ApiClient.getUserData(userId);
    }

//    private void getImagePath(String imageId) {
//        ApiClient.getImagePathById(imageId);
//    }

    private void followLocation(RemovePlaceFollowData removePlaceFollowData) {
        ApiClient.addPlaceFollow(removePlaceFollowData);
    }
}
