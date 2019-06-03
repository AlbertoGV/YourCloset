package com.example.albertogv.yourcloset.view.views;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.ImageView;

import com.example.albertogv.yourcloset.GlideApp;
import com.example.albertogv.yourcloset.R;

public class MediaActivity extends AppCompatActivity {
    ImageView mImageView;
    private ScaleGestureDetector mScaleGestureDetector;
    private float mScaleFactor = 1.0f;

    public boolean onTouchEvent(MotionEvent motionEvent) {
        mScaleGestureDetector.onTouchEvent(motionEvent);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media);
        Intent intent = getIntent();
        mImageView = findViewById(R.id.imageView);
        String mediaUrl = intent.getStringExtra("imagen");
        mScaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());
        if(mImageView!= null) {
            GlideApp.with(this).load(mediaUrl).into(mImageView);
            }
        }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector){
            mScaleFactor *= scaleGestureDetector.getScaleFactor();
            mScaleFactor = Math.max(0.1f,
                    Math.min(mScaleFactor, 10.0f));
            mImageView.setScaleX(mScaleFactor);
            mImageView.setScaleY(mScaleFactor);
            return true;
        }
    }
    }

