package com.example.albertogv.yourcloset.view.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ZoomControls;

import com.example.albertogv.yourcloset.GlideApp;
import com.example.albertogv.yourcloset.R;

public class MediaActivity extends AppCompatActivity {
    ImageView mImageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media);
        Intent intent = getIntent();
        mImageView = findViewById(R.id.imageView);
        String mediaUrl = intent.getStringExtra("imagen");

        if(mImageView!= null) {
            GlideApp.with(this).load(mediaUrl).into(mImageView);
            }
        }
    }

