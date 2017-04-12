package com.iemqra.bme.lostnfound.ui.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.iemqra.bme.lostnfound.event.BaseEvent;
import com.iemqra.bme.lostnfound.helper.LocationHandler;
import com.iemqra.bme.lostnfound.helper.PlaceHandler;
import com.iemqra.bme.lostnfound.helper.SQLiteHandler;
import com.iemqra.bme.lostnfound.manager.DataManager;
import com.iemqra.bme.lostnfound.model.APIHelper.ItemUploadData;
import com.iemqra.bme.lostnfound.ui.activity.MainActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static com.facebook.FacebookSdk.getApplicationContext;

public class NewItemFragment extends Fragment {

    private static final String TAG = MainActivity.class.getSimpleName();
    public int REQUEST_CAMERA = 0, SELECT_FILE = 1, PLACE_PICKER_REQUEST = 2;
    @BindView(R.id.fragment_newitem_header)
    TextView tv;
    @BindView(R.id.fragment_newitem_title)
    EditText titleText;
    @BindView(R.id.fragment_newitem_description)
    EditText descriptionText;
    @BindView(R.id.fragment_newitem_image)
    ImageView image;
    private LocationHandler location = new LocationHandler();
    private PlaceHandler place = new PlaceHandler();
    private Bitmap photo;
    private String photoPath;
    private String imageId;
    private AlertDialog.Builder builder;
    private ProgressDialog pDialog;
    private SQLiteHandler db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_newitem, container, false);
        ButterKnife.bind(this, view);

        pDialog = new ProgressDialog(getActivity());
        pDialog.setCancelable(false);
        db = new SQLiteHandler(getActivity().getApplicationContext());

        final CharSequence[] items = {getActivity().getString(R.string.photo_picker_camera), getActivity().getString(R.string.photo_picker_galery)};
        builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getActivity().getString(R.string.photo_picker_title));
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals(getActivity().getString(R.string.photo_picker_camera))) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, REQUEST_CAMERA);
                } else if (items[item].equals(getActivity().getString(R.string.photo_picker_galery))) {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(
                            Intent.createChooser(intent, getActivity().getString(R.string.photo_picker_file)), SELECT_FILE);
                }
            }
        });

        return view;
    }

    @OnClick(R.id.fragment_newitem_get_photo)
    public void handleGetPhotoButtonClick() {
        builder.show();
    }

    @OnClick(R.id.fragment_newitem_place_picker)
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

    @OnClick(R.id.fragment_newitem_add)
    public void handleAddButtonClick() {
        pDialog.setMessage("Please wait ...");
        showDialog(true);
        String title = titleText.getText().toString().trim();
        String description = descriptionText.getText().toString().trim();
        String user_uid = DataManager.INSTANCE.getUser().get("uid");

        if (!title.isEmpty() && !description.isEmpty()) {
            if (place.getUid().length() > 0 && photo != null && !photoPath.isEmpty())
                uploadImage(photoPath);
                //addItem(user_uid, place, title, description);
            else
                Toast.makeText(getActivity().getApplicationContext(), R.string.pick_place_and_photo, Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(getActivity().getApplicationContext(), R.string.type_title_and_description, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                photo = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                photo.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

                File destination = new File(Environment.getExternalStorageDirectory(),
                        System.currentTimeMillis() + ".jpg");

                FileOutputStream fo;
                try {
                    destination.createNewFile();
                    fo = new FileOutputStream(destination);
                    fo.write(bytes.toByteArray());
                    fo.close();
                } catch (java.io.IOException e) {
                    e.printStackTrace();
                }
                photoPath = destination.getAbsolutePath();
                image.setImageBitmap(photo);
            } else if (requestCode == SELECT_FILE) {
                Uri selectedImageUri = data.getData();
                photoPath = getRealPathFromURI(selectedImageUri);
                String[] projection = {MediaStore.MediaColumns.DATA};
                Cursor cursor = ((MainActivity) getActivity()).getContentResolver().query(selectedImageUri, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                cursor.moveToFirst();

                String selectedImagePath = cursor.getString(column_index);

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(selectedImagePath, options);
                final int REQUIRED_SIZE = 200;
                int scale = 1;
                while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                        && options.outHeight / scale / 2 >= REQUIRED_SIZE)
                    scale *= 2;
                options.inSampleSize = scale;
                options.inJustDecodeBounds = false;
                photo = BitmapFactory.decodeFile(selectedImagePath, options);

                image.setImageBitmap(photo);
            } else if (requestCode == PLACE_PICKER_REQUEST) {

                final Place pickedPlace = PlacePicker.getPlace(data, getActivity());

                place.setUid(pickedPlace.getId().trim());
                place.setName(String.valueOf(pickedPlace.getName()).trim());
                place.setAddress(String.valueOf(pickedPlace.getAddress()).trim());
                place.setPhoneNumber(String.valueOf(pickedPlace.getPhoneNumber()).trim());
            }

        } else {
            super.onActivityResult(requestCode, resultCode, data);
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
    public void onEventMainThread(final BaseEvent event) {
        showDialog(false);
        if (event.getThrowable() != null) {
            event.getThrowable().printStackTrace();
            Toast.makeText(getApplicationContext(), event.getMessage(), Toast.LENGTH_SHORT).show();
        } else if (event.getMessage().equals("imageId")) {
            if (event.getData() != null) {
                imageId = event.getData();
                uploadItem(new ItemUploadData(titleText.getText().toString().trim(), descriptionText.getText().toString().trim(), DataManager.INSTANCE.getUser().get("uid"), Integer.valueOf(event.getData()), place.getUid().trim()));
            } else {
                Toast.makeText(getApplicationContext(), event.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else if (event.getMessage().equals("itemUploaded")) {
            Toast.makeText(getApplicationContext(), "Item successfully uploaded!", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadImage(String filePath) {
        File file = new File(filePath);

        RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("upload", file.getName(), reqFile);
        ApiClient.uploadImage(body);
    }

    private void uploadItem(ItemUploadData item) {
        ApiClient.uploadItem(item);
    }


    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContext().getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    private void showDialog(boolean req) {
        if (req == true) {
            if (!pDialog.isShowing())
                pDialog.show();
        } else {
            if (pDialog.isShowing())
                pDialog.dismiss();
        }
    }
}