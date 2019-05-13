
package com.example.albertogv.yourcloset.view.activities;
        import android.Manifest;
        import android.annotation.SuppressLint;
        import android.content.Context;
        import android.content.Intent;
        import android.content.pm.PackageManager;
        import android.graphics.Bitmap;
        import android.graphics.Color;
        import android.location.Criteria;
        import android.location.Location;
        import android.location.LocationManager;
        import android.os.Build;
        import android.support.annotation.RequiresApi;
        import android.support.design.widget.CollapsingToolbarLayout;
        import android.support.design.widget.FloatingActionButton;
        import android.support.v4.app.ActivityCompat;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.transition.Transition;
        import android.view.View;
        import android.widget.ImageView;
        import android.widget.TextView;

        import com.example.albertogv.yourcloset.GlideApp;
        import com.example.albertogv.yourcloset.R;
        import com.example.albertogv.yourcloset.view.chat.ChatActivity;
        import com.google.android.gms.maps.CameraUpdateFactory;
        import com.google.android.gms.maps.GoogleMap;
        import com.google.android.gms.maps.OnMapReadyCallback;
        import com.google.android.gms.maps.SupportMapFragment;
        import com.google.android.gms.maps.model.Circle;
        import com.google.android.gms.maps.model.CircleOptions;
        import com.google.android.gms.maps.model.LatLng;



public class DetailActivityLists extends AppCompatActivity implements OnMapReadyCallback {


    Context context;
    FloatingActionButton fab;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapse);
        Bitmap bitmap;
        Bundle bundle = getIntent().getExtras();
        Intent intent = getIntent();
        intent.getExtras();
        TextView tvprecio = findViewById(R.id.textViewPrecio);
        ImageView ivimagen = findViewById(R.id.imageViewImagen);
        ImageView ivimagenPerfil = findViewById(R.id.fotoPerfil);
        TextView tvdescripcion = findViewById(R.id.textViewDEscripcion);
        TextView tvfecha = findViewById(R.id.textViewFecha);
        TextView tvautor = findViewById(R.id.nombre_user);
        context = this;
        int permissionCheck = ActivityCompat.checkSelfPermission(DetailActivityLists.this,
                Manifest.permission.READ_EXTERNAL_STORAGE);
        fab= findViewById(R.id.fabdetail);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DetailActivityLists.this,ChatActivity.class);
                startActivity(i);
            }
        });
        if (bundle != null) {

            String precio = bundle.getString("PRECIO");

            String titulo = bundle.getString("TITULO");

            String imagenperfil = bundle.getString("IMGPERFIL");

            String autor = bundle.getString("AUTOR");

            String descripcion = bundle.getString("DESCRIPCION");

            String fecha = bundle.getString("FECHA");

            String imagen = intent.getStringExtra("IMAGEN");

            /* Glide.with(getApplicationContext()).load(imagen).into(ivimagen);*/

            GlideApp.with(context)
                    .asBitmap()
                    .load(imagen)
                    .into(ivimagen);
            /*ivimagen.setImageURI(Uri.parse(imagen));*/


            GlideApp.with(context)
                    .asBitmap()
                    .load(imagenperfil)
                    .into(ivimagenPerfil);

            tvprecio.setText(precio);
            tvfecha.setText(fecha);
            tvautor.setText(autor);
            collapsingToolbarLayout.setTitle(titulo);
            collapsingToolbarLayout.setExpandedTitleColor(Color.DKGRAY);
            collapsingToolbarLayout.setCollapsedTitleTextColor(Color.DKGRAY);
            tvdescripcion.setText(descripcion);

            Transition fade = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                fade = getWindow().getEnterTransition();
            }
            final Transition finalFade = fade;
            ((Transition) fade).addListener(new Transition.TransitionListener() {
                @Override
                public void onTransitionStart(Transition transition) {

                }

                @SuppressLint("RestrictedApi")
                @Override
                public void onTransitionEnd(Transition transition) {
                    fab.setVisibility(View.VISIBLE);
                    finalFade.removeListener(this);
                }

                @Override
                public void onTransitionCancel(Transition transition) {

                }

                @Override
                public void onTransitionPause(Transition transition) {

                }

                @Override
                public void onTransitionResume(Transition transition) {

                }
            });

        }
        SupportMapFragment mapFragment2 = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_subiranuncio);
        mapFragment2.getMapAsync(this);


    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("RestrictedApi")
    @Override
    public void onBackPressed() {
        fab.setVisibility(View.VISIBLE);
        finishAfterTransition();
    }

    GoogleMap gMap;

    @Override
    public void onMapReady(GoogleMap googleMap) {

        gMap = googleMap;

        if (ActivityCompat.checkSelfPermission(DetailActivityLists.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(DetailActivityLists.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(DetailActivityLists.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
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












