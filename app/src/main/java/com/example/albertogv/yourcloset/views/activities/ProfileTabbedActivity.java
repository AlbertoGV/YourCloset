package com.example.albertogv.yourcloset.views.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.albertogv.yourcloset.GlideApp;
import com.example.albertogv.yourcloset.R;
import com.example.albertogv.yourcloset.views.fragments.AnunciosFragment;
import com.example.albertogv.yourcloset.views.fragments.FavoritosFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import static com.firebase.ui.auth.AuthUI.TAG;


public class ProfileTabbedActivity extends AppCompatActivity {
    private int mPostsCount = 0;
    TextView tvPosts;
    FirebaseStorage firebaseStorage;
    FirebaseUser mUser;
    private FirebaseDatabase database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_tabbed);
        FirebaseAuth firebaseAuth;
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();
        getIntent().getExtras();
        ImageView imageView = findViewById(R.id.image3);
        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        firebaseStorage = FirebaseStorage.getInstance();
        tvPosts = findViewById(R.id.tvProducts);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        TextView tvname = findViewById(R.id.name);
        GlideApp.with(ProfileTabbedActivity.this).load(mUser.getPhotoUrl()).into(imageView);
        tvname.setText(mUser.getDisplayName());



    }

    @Override
    protected void onResume() {
        super.onResume();
        getPostsCount();
    }

    private void getPostsCount(){
        mPostsCount = 0;
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child("products/user-products")
                .child("uid-"+mUser.getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot :  dataSnapshot.getChildren()){
                    Log.d(TAG, "onDataChange: found product:" + singleSnapshot.getValue());
                    mPostsCount++;
                }
                tvPosts.setText(String.valueOf(mPostsCount));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        @StringRes
        private final int[] TAB_TITLES = new int[]{R.string.tab_text_1, R.string.tab_text_2};
        private final Context mContext;

        public SectionsPagerAdapter(Context context, FragmentManager fm) {
            super(fm);
            mContext = context;
        }

        @Override
        public Fragment getItem(int position) {
            switch(position){

                case 0 :
                    return  new AnunciosFragment();

                case 1 :
                    return new FavoritosFragment();

                    default : return new AnunciosFragment();

            }
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return mContext.getResources().getString(TAB_TITLES[position]);
        }



        @Override
        public int getCount() {
            // Show 2 total pages.

            return 2;

        }


    }


}