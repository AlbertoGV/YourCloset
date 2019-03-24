package com.example.albertogv.yourcloset;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

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

