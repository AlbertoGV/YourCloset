package com.example.albertogv.yourcloset;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

import maes.tech.intentanim.CustomIntent;


public class DetailActivity extends AppCompatActivity implements OnMapReadyCallback {


    Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        Bitmap bitmap;
        Bundle bundle = getIntent().getExtras();
        final Intent intent = getIntent();
        intent.getExtras();
        TextView tvprecio = findViewById(R.id.textViewPrecio);
        ImageView ivimagen = findViewById(R.id.imageViewImagen);
        ImageView ivimagenperfil=findViewById(R.id.fotoPerfil);
        TextView tvdescripcion = findViewById(R.id.textViewDEscripcion);
        TextView tvfecha = findViewById(R.id.textViewFecha);
        TextView tvNombre = findViewById(R.id.nombre_user);

        FloatingActionButton fab= findViewById(R.id.fabdetail);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DetailActivity.this,ChatActivity.class);
                startActivity(i);
            }
        });

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

            String imagen = intent.getStringExtra("imagen");

            /* Glide.with(getApplicationContext()).load(imagen).into(ivimagen);*/


            GlideApp.with(context)
                    .asBitmap()
                    .load(imagen)
                    .into(ivimagen);

            GlideApp.with(context)
                    .asBitmap()
                    .load(imgperfil)
                    .into(ivimagenperfil);
            ivimagenperfil.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(DetailActivity.this,ProfileActivity.class);
                    startActivity(i);
                }
            });
            tvprecio.setText(precio);
            tvNombre.setText(nombre);
            tvfecha.setText(fecha);
            collapsingToolbarLayout.setTitle(titulo);
            collapsingToolbarLayout.setExpandedTitleColor(Color.DKGRAY);
            collapsingToolbarLayout.setCollapsedTitleTextColor(Color.DKGRAY);
            tvdescripcion.setText(descripcion);

        }
        SupportMapFragment mapFragment2 = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_subiranuncio);
        mapFragment2.getMapAsync(this);


    }



    GoogleMap gMap;

    @Override
    public void onMapReady(GoogleMap googleMap) {

        gMap = googleMap;

        if (ActivityCompat.checkSelfPermission(DetailActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(DetailActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(DetailActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            if (!gMap.isMyLocationEnabled())
                gMap.setMyLocationEnabled(true);

            LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Location myLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if (myLocation == null) {
                Criteria criteria = new Criteria();
                criteria.setAccuracy(Criteria.ACCURACY_COARSE);
                String provider = lm.getBestProvider(criteria, true);
                myLocation = lm.getLastKnownLocation(provider);
            }

            if (myLocation != null) {
                LatLng userLocation = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
                gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 11), 1500, null);
                Circle circle = gMap.addCircle(new CircleOptions()
                        .center(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()))
                        .radius(4000)
                        .strokeColor(Color.TRANSPARENT)
                        .fillColor(0x220000FF));


            }
        }


    }



}











