package com.example.albertogv.yourcloset.views.activities;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.albertogv.yourcloset.GlideApp;
import com.example.albertogv.yourcloset.R;
import com.example.albertogv.yourcloset.views.chat.ChatActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import maes.tech.intentanim.CustomIntent;


public class DetailActivity extends AppCompatActivity implements OnMapReadyCallback {
    public FirebaseUser mUser;
    FirebaseDatabase database;
    Context context;
    RatingBar ratingBar;
    TextView tvRating;
    double latitude;
    double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        Bundle bundle = getIntent().getExtras();
        final Intent intent = getIntent();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        intent.getExtras();
        TextView tvprecio = findViewById(R.id.textViewPrecio);
        ImageView ivimagen = findViewById(R.id.imageViewImagen);
        ImageView ivimagenperfil=findViewById(R.id.fotoPerfil);
        LinearLayout ivwhatsapp = findViewById(R.id.whatsapp);
        TextView tvdescripcion = findViewById(R.id.textViewDEscripcion);
        tvRating = findViewById(R.id.tvrating);
        TextView tvfecha = findViewById(R.id.textViewFecha);
        TextView tvNombre = findViewById(R.id.nombre_user);
        FloatingActionButton fab= findViewById(R.id.fabdetail);
        database = FirebaseDatabase.getInstance();
        ratingBar = findViewById(R.id.ratingBar);

        testRatingBar();

        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapse);
        context = this;
        int permissionCheck = ActivityCompat.checkSelfPermission(DetailActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE);

        Button button = findViewById(R.id.ampliarmapa);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(DetailActivity.this,MapsActivity2.class);
                startActivity(i);
                CustomIntent.customType(DetailActivity.this,"fadein-to-fadeout");
            }
        });

        if (bundle != null) {

            String imgperfil = bundle.getString("imgperfil");

            String precio = bundle.getString("precio");

            String titulo = bundle.getString("Titulo");

            String nombre = bundle.getString("nombre");

            String fecha = bundle.getString("fecha");

            String descripcion = bundle.getString("descripcion");

            final String productKey = bundle.getString("PRODUCT_KEY");

            final String messageKey = bundle.getString("MESSAGE_KEY");

            final String imagen = intent.getStringExtra("imagen");

            latitude = bundle.getDouble("latitude");

            longitude = bundle.getDouble("longitude");


            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(DetailActivity.this,ChatActivity.class);
                    i.putExtra("PRODUCT_KEY", productKey);

                    i.putExtra("MESSAGE_KEY",messageKey);
                    startActivity(i);
                }
            });


            Glide.with(context)
                    .asBitmap()
                    .load(imagen)
                    .into(ivimagen);
            ivimagen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(DetailActivity.this, MediaActivity.class);
                    i.putExtra("imagen", imagen);
                    startActivity(i);
                }
            });
            ivwhatsapp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent sendIntent = new Intent();
                    if(sendIntent!= null)
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, "Hola tengo un anuncio en YourCloset! Descarga la nueva app ya para verlo !");
                    sendIntent.setType("text/plain");
                    sendIntent.setPackage("com.whatsapp");
                    startActivity(sendIntent);

                }
            });



            tvprecio.setText(precio);
            tvNombre.setText(nombre);
            tvfecha.setText(fecha);
            collapsingToolbarLayout.setTitle(titulo);
            collapsingToolbarLayout.setExpandedTitleColor(Color.WHITE);
            collapsingToolbarLayout.setCollapsedTitleTextColor(Color.DKGRAY);
            tvdescripcion.setText(descripcion);

            GlideApp.with(this)
                    .load(imgperfil)
                        .into(ivimagenperfil);


        }

        SupportMapFragment mapFragment2 = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_subiranuncio);
        mapFragment2.getMapAsync(this);


    }
    private void testRatingBar() {
        final DatabaseReference ref = database.getReference("user/rating").child("rating");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                    float rating = Float.parseFloat(dataSnapshot.getValue().toString());
                    ratingBar.setRating(rating);
                    tvRating.setText("("+String.valueOf(rating)+")");

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (fromUser) ref.setValue(rating);
            }
        });
    }




    GoogleMap gMap;

    @Override
    public void onMapReady(GoogleMap googleMap) {

        gMap = googleMap;

        if (ActivityCompat.checkSelfPermission(DetailActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(DetailActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(DetailActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            if (!gMap.isMyLocationEnabled())
                gMap.setMyLocationEnabled(false);

            LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Location myLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if (myLocation == null) {
                Criteria criteria = new Criteria();
                criteria.setAccuracy(Criteria.ACCURACY_COARSE);
                String provider = lm.getBestProvider(criteria, true);
                myLocation = lm.getLastKnownLocation(provider);

            }

            if (myLocation != null) {
                LatLng userLocation = new LatLng(longitude, latitude);
                gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 11), 1500, null);
                Circle circle = gMap.addCircle(new CircleOptions()
                        .center(new LatLng(longitude, latitude))
                        .radius(4000)
                        .strokeColor(Color.TRANSPARENT)
                        .fillColor(0x220000FF));


            }
        }


    }




}












