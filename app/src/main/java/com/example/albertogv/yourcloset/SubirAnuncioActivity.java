package com.example.albertogv.yourcloset;


import android.Manifest;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.ParseException;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.albertogv.yourcloset.model.Anuncio;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import maes.tech.intentanim.CustomIntent;

import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;

public class SubirAnuncioActivity extends AppCompatActivity implements OnMapReadyCallback {
    static final int RC_IMAGE_PICK = 9000;

    private Button button_imagen;
    private Button button_ubicacion;
    private Button buttonAceptar;
    private Button buttonCancelar;
    EditText etNombre, etArticulo, etPrecio;
    ImageView imagePreview;
    AnuncioViewModel anuncioViewModel;
    private GoogleApiClient googleApiClient;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private static final int PHOTO_SEND = 1;

    Uri mediaUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anuncio);

        etNombre = findViewById(R.id.campo_nombre);
        etArticulo = findViewById(R.id.campo_articulo);
        etPrecio = findViewById(R.id.campo_precio);

        imagePreview = findViewById(R.id.imagePreview);
        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        Button button = findViewById(R.id.ampliarmapa2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SubirAnuncioActivity.this,MapsActivity2.class);
                startActivity(i);
             /*   CustomIntent.customType(SubirAnuncioActivity.this,"fadein-to-fadeout");*/
            }
        });
       /* button_ubicacion = findViewById(R.id.boton_ubicacion);
        button_ubicacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SubirAnuncioActivity.this,MapsActivity.class);
                startActivity(i);


            }
        });*/


        imagePreview = findViewById(R.id.imagePreview);
        imagePreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                intent.addFlags(FLAG_GRANT_READ_URI_PERMISSION);
                startActivityForResult(intent, RC_IMAGE_PICK);

            }
        });

        buttonAceptar = findViewById(R.id.aceptarAnuncio);
        buttonAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertarAnuncio();
                if (anuncioViewModel != null) {
                    finish();
                }

            }

        });


        if (firebaseAuthListener == null) {
            firebaseAuth = FirebaseAuth.getInstance();
            firebaseAuthListener = new FirebaseAuth.AuthStateListener() {

                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    user.getDisplayName();

                }
            };
        }


        SupportMapFragment mapFragment2 = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_subiranuncio);
        mapFragment2.getMapAsync(this);

    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {
            if (requestCode == RC_IMAGE_PICK) {
                mediaUri = data.getData();
                GlideApp.with(SubirAnuncioActivity.this).asBitmap().centerInside().override(500,500).load(mediaUri).into(imagePreview);
                /*imagePreview.setImageURI(mediaUri);*/
            }
        }

        }



    void insertarAnuncio() {
        String nombre = etNombre.getText().toString();
        if (nombre.isEmpty()) {
            etNombre.setError("Introduzca el nombre del artículo");
            return;
        }
        String articulo = etArticulo.getText().toString();
        if (articulo.isEmpty()) {
            etArticulo.setError("Introduzca la descripcion");
            return;
        }
        String precio = etPrecio.getText().toString();
        if (precio.isEmpty()) {
            etPrecio.setError("Introduzca el precio");
            return;
        }


        if (mediaUri == null) {
            Toast.makeText(this, "Seleccione una imagen para continuar", Toast.LENGTH_SHORT).show();

            return;
        }


    /*    }  if (mediaUri2 == null || mediaUri2.toString().isEmpty()) {
            Toast.makeText(this, "Seleccione una imagen para continuar", Toast.LENGTH_SHORT).show();
            return;
        } if (mediaUri3 == null || mediaUri3.toString().isEmpty()) {
            Toast.makeText(this, "Seleccione una imagen para continuar", Toast.LENGTH_SHORT).show();
            return;
        } if (mediaUri4 == null || mediaUri4.toString().isEmpty()) {
            Toast.makeText(this, "Seleccione una imagen para continuar", Toast.LENGTH_SHORT).show();
            return;
        }*/


        // Assume thisActivity is the current activity
        int permissionCheck = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE);
        if (firebaseAuth != null) {
            FirebaseUser user = firebaseAuth.getCurrentUser();

            anuncioViewModel = ViewModelProviders.of(this).get(AnuncioViewModel.class);
            Anuncio anuncio = new Anuncio();
            anuncio.setImageperfil(String.valueOf(user.getPhotoUrl()));
            anuncio.setAutor(user.getDisplayName());
            anuncio.imageperfil=anuncio.getImageperfil();
            anuncio.autor = anuncio.getAutor();
            anuncio.tituloAnuncio = nombre;
            anuncio.descripcion = articulo;
            anuncio.precio = precio;
            anuncio.imageUri = mediaUri.toString();
            anuncio.fechapublicacion = new SimpleDateFormat("dd/MM/yyyy  HH:mm").format(Calendar.getInstance().getTime());

            anuncioViewModel.insertAnuncio(anuncio);

        }
    }


        GoogleMap gMap;
        @Override
        public void onMapReady (GoogleMap googleMap){

            gMap = googleMap;

            if (ActivityCompat.checkSelfPermission(SubirAnuncioActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(SubirAnuncioActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(SubirAnuncioActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
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


    @Override
    protected void onStop() {
        super.onStop();
        if(firebaseAuthListener != null ){
            firebaseAuth.removeAuthStateListener(firebaseAuthListener);

        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        if (firebaseAuth != null) {
            firebaseAuth.addAuthStateListener(firebaseAuthListener);
        }
    }

    }