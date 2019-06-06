package com.example.albertogv.yourcloset.views.activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.albertogv.yourcloset.GlideApp;
import com.example.albertogv.yourcloset.R;
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


public class SettingsActivity extends AppCompatActivity implements OnMapReadyCallback {
    public FirebaseUser mUser;
     FirebaseDatabase database;
    Context context;
    RatingBar ratingBar;
    TextView tvRating;
    String uid;
    LinearLayout linearcomparte;
    AppCompatButton buttonvendido;
    TextView estadoProducto;
    AlertDialog.Builder dialogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        int permissionCheck = ActivityCompat.checkSelfPermission(SettingsActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE);
        context = this;
        Bundle bundle = getIntent().getExtras();
        final Intent intent = getIntent();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        intent.getExtras();
        buttonvendido = findViewById(R.id.buttonvendido);
        linearcomparte = findViewById(R.id.linearCompartir);
        estadoProducto = findViewById(R.id.estadoproducto);
        TextView tvprecio = findViewById(R.id.textViewPrecio);
        ImageView ivimagen = findViewById(R.id.imageViewImagen);
        ImageView ivimagenperfil=findViewById(R.id.fotoPerfil);
        TextView tvdescripcion = findViewById(R.id.textViewDEscripcion);
        tvRating = findViewById(R.id.tvrating);
        AppCompatButton appCompatButton = findViewById(R.id.buttonEliminar);
        AppCompatButton buttonnovendido = findViewById(R.id.buttonnovendido);
        AppCompatButton buttonresreservado= findViewById(R.id.buttonreservado);
        AppCompatButton buttonnoreservado = findViewById(R.id.buttonnoreservado);
        TextView tvfecha = findViewById(R.id.textViewFecha);
        TextView tvNombre = findViewById(R.id.nombre_user);
        database = FirebaseDatabase.getInstance();
        ratingBar = findViewById(R.id.ratingBar);
        Button button = findViewById(R.id.ampliarmapa);
        testRatingBar();
        uid = "uid-" + mUser.getUid();
        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapse);



        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(SettingsActivity.this,MapsActivity2.class);
                startActivity(i);
                CustomIntent.customType(SettingsActivity.this,"fadein-to-fadeout");
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

//            final String messageKey = bundle.getString("MESSAGE_KEY");

            final String imagen = intent.getStringExtra("imagen");
//
//            fab.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent i = new Intent(SettingsActivity.this, ChatActivity.class);
//                    i.putExtra("PRODUCT_KEY", productKey);
//
//                    i.putExtra("MESSAGE_KEY",messageKey);
//                    startActivity(i);
//                }
//            }

             linearcomparte.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     Intent sendIntent = new Intent();
                     sendIntent.setAction(Intent.ACTION_SEND);
                     sendIntent.putExtra(Intent.EXTRA_TEXT, "Hola tengo un anuncio en YourCloset! Descarga la nueva app ya para verlo !");
                     sendIntent.setType("text/plain");
                     sendIntent.setPackage("com.whatsapp");
                     startActivity(sendIntent);
                 }
             });
             buttonresreservado.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     ponerReservado(productKey);
                     estadoProducto.setText("El producto está ahora en estado Reservado");
                 }
             });
             buttonnoreservado.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     quitarReservado(productKey);
                     estadoProducto.setText("El producto está ahora en Venta");
                 }
             });
            buttonnovendido.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    quitarVendido(productKey);
                    estadoProducto.setText("El producto está ahora en Venta");

                }
            });
            buttonvendido.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   ponerVendido(productKey);
                    estadoProducto.setText("El producto está ahora en estado vendido");
                }
            });

            appCompatButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    eliminarAnuncio(productKey);

                }

            });

            Glide.with(context)
                    .asBitmap()
                    .load(imagen)
                    .into(ivimagen);
            ivimagen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(SettingsActivity.this, MediaActivity.class);
                    i.putExtra("imagen", imagen);
                    startActivity(i);
                }
            });

//
//            ivimagenperfil.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent i = new Intent(SettingsActivity.this, ProfileActivity.class);
//                    startActivity(i);
//                }
//            });
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

    public void eliminarAnuncio(final String productKey){

        final AlertDialog.Builder dialogo = new AlertDialog.Builder(this);
        dialogo.setTitle("Vas a eliminar un anuncio");
        dialogo.setMessage("Se va a proceder a eliminar este anuncio");
        dialogo.setMessage("¿Estas seguro?");
        dialogo.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {
                database.getReference("products/data").child(productKey).setValue(null);
                database.getReference("products/all-products").child(productKey).setValue(null);
                database.getReference("products/user-products").child(uid).child(productKey).setValue(null);
                database.getReference("products/hombresCalz").child(productKey).setValue(null);
                database.getReference("products/hombresInf").child(productKey).setValue(null);
                database.getReference("products/hombresSup").child(productKey).setValue(null);
                database.getReference("products/hombresCompl").child(productKey).setValue(null);
                database.getReference("products/mujeresCalz").child(productKey).setValue(null);
                database.getReference("products/mujeresInf").child(productKey).setValue(null);
                database.getReference("products/mujeresSup").child(productKey).setValue(null);
                database.getReference("products/mujeresCompl").child(productKey).setValue(null);

                Toast.makeText(context, "Anuncio Eliminado", Toast.LENGTH_SHORT).show();
                finish();
            }


        });

        dialogo.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {

            }
        });
        dialogo.show();




    }
    public void ponerVendido(final String productKey){
        Toast.makeText(context, "El producto se ha puesto como vendido", Toast.LENGTH_SHORT).show();
        database.getReference("products/data").child(productKey).child("vendido").setValue(true);
        database.getReference("products/data").child(productKey).child("reservado").setValue(false);

    }
    public void quitarVendido(final String productKey){
        Toast.makeText(context, "El producto se ha retirado como vendido", Toast.LENGTH_SHORT).show();
        database.getReference("products/data").child(productKey).child("vendido").setValue(false);

    }
    public void ponerReservado(final String productKey){
        Toast.makeText(context, "El producto se ha puesto como reservado", Toast.LENGTH_SHORT).show();
        database.getReference("products/data").child(productKey).child("reservado").setValue(true);
        database.getReference("products/data").child(productKey).child("vendido").setValue(false);

    }
    public void quitarReservado(final String productKey){
        Toast.makeText(context, "El producto se ha retirado como reservado", Toast.LENGTH_SHORT).show();
        database.getReference("products/data").child(productKey).child("reservado").setValue(false);

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



    public void onStop() {
        super.onStop();
        if (dialogo != null) {

            dialogo = null;
        }
    }

    GoogleMap gMap;

    @Override
    public void onMapReady(GoogleMap googleMap) {

        gMap = googleMap;

        if (ActivityCompat.checkSelfPermission(SettingsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(SettingsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(SettingsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
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












