package com.iemqra.bme.lostnfound.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.iemqra.bme.lostnfound.R;
import com.iemqra.bme.lostnfound.api.ApiClient;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.senab.photoview.PhotoViewAttacher;

public class ImageActivity extends AppCompatActivity {

    @BindView(R.id.activity_image_full_size_image)
    ImageView iv_image;
    PhotoViewAttacher pAttacher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        String path = intent.getStringExtra("image");
        if (!path.isEmpty()) {
            Picasso.with(this).load(ApiClient.API_URL + path.substring(1)).into(iv_image);
        } else {
            iv_image.setImageResource(R.drawable.no_picture);
        }

        pAttacher = new PhotoViewAttacher(iv_image);
        pAttacher.update();
    }
}
