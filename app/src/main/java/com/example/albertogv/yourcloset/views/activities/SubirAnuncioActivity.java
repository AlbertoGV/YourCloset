package com.example.albertogv.yourcloset.views.activities;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.albertogv.yourcloset.GlideApp;
import com.example.albertogv.yourcloset.R;
import com.example.albertogv.yourcloset.manager.MediaFiles;
import com.example.albertogv.yourcloset.model.Anuncio;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import maes.tech.intentanim.CustomIntent;

public class SubirAnuncioActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int GALLERY_REQUEST_CODE = 1;
    private static final int CAMERA_REQUEST_CODE = 2;

    private Button buttonAceptar;
    Uri mediaUri;
    String mediaType;
    DatabaseReference mReference;

    FirebaseUser mUser;
    String uid;
    String fullAddress;
    boolean recording = false;

    private MediaRecorder mRecorder = null;
    EditText etNombre, etArticulo, etPrecio;
    ImageView imagePreview;
    ImageView imagePreviewGal;
    private GoogleApiClient googleApiClient;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private static final int PHOTO_SEND = 1;
    static final int REQUEST_RECORD_AUDIO_PERMISSION = 1212;
    static final int REQUEST_RECORD_FILE_PERMISSION = 1213;
    public RadioButton hombreRb;
    public boolean vendido;
    boolean reservado;
    String articulo;
    String precio;
    TextView tvaddress;
    public RadioGroup radioGenero;
    public RadioGroup radioTipoPrenda;
    String nombre;
    public RadioButton mujerRb;
    Uri mFileUri;
    Location myLocation;
    LocationManager lm;
    double latitude;
    double longitude;
    public RadioButton parteSuperior;
    public RadioButton parteInferior;
    public RadioButton parteCalzado;
    public RadioButton parteComplemento;
    boolean hombrePrenda;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anuncio);
        mReference = FirebaseDatabase.getInstance().getReference();
        etNombre = findViewById(R.id.campo_nombre);
        etArticulo = findViewById(R.id.campo_articulo);
        etPrecio = findViewById(R.id.campo_precio);

        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
         myLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        latitude = myLocation.getLatitude();
        longitude = myLocation.getLongitude();
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO_PERMISSION);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},REQUEST_RECORD_FILE_PERMISSION);
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        imagePreview = findViewById(R.id.imagePreview);
        mReference = FirebaseDatabase.getInstance().getReference();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        uid = "uid-" + mUser.getUid();
        hombreRb = findViewById(R.id.hombre);
        mujerRb = findViewById(R.id.mujer);
        tvaddress = findViewById(R.id.tv_ubicacion);
        parteSuperior = findViewById(R.id.radioarriba);
        parteInferior = findViewById(R.id.radiobajo);
        parteCalzado = findViewById(R.id.calzado);
        buttonAceptar = findViewById(R.id.aceptarAnuncio);
        parteComplemento = findViewById(R.id.radiocomplemento);
        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        Button button = findViewById(R.id.ampliarmapa2);
        nombre = etNombre.getText().toString();
        articulo = etArticulo.getText().toString();
        precio = etPrecio.getText().toString();
        radioGenero = findViewById(R.id.radioGroupGenero);
        radioTipoPrenda = findViewById(R.id.radioGroupTipoPrenda);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SubirAnuncioActivity.this,MapsActivity2.class);
                startActivity(i);
            CustomIntent.customType(SubirAnuncioActivity.this,"fadein-to-fadeout");
            }
        });

        imagePreview = findViewById(R.id.imagePreview);
        imagePreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogChooseCamera();

            }
        });

        buttonAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    v.setEnabled(false);
                    Toast.makeText(SubirAnuncioActivity.this, "Subiendo...espere por favor", Toast.LENGTH_SHORT).show();
                    submitPost();

                } catch (java.text.ParseException e) {
                    e.printStackTrace();
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
    public void submitPost() throws  java.text.ParseException{
        final String postText = etArticulo.getText().toString();
        final String postName = etNombre.getText().toString();
        final String postPrecio = etPrecio.getText().toString();

        if (!postText.isEmpty() && !postName.isEmpty() && !postPrecio.isEmpty() && mediaUri != null && radioGenero.getCheckedRadioButtonId() != -1 && radioTipoPrenda.getCheckedRadioButtonId() != -1){
            Toast.makeText(SubirAnuncioActivity.this, "Subiendo...espere por favor", Toast.LENGTH_SHORT).show();
            buttonAceptar.setVisibility(View.INVISIBLE);
            uploadAndWriteNewPost(postText,postName,postPrecio);
        }else{
            Toast.makeText(SubirAnuncioActivity.this, "Rellene todos los campos, por favor", Toast.LENGTH_SHORT).show();
            if(postName.isEmpty()) {
                etNombre.setError("Introduzca el nombre artículo");
            }if (postText.isEmpty()) {
                etArticulo.setError("Introduzca la descripcion");
            }if (postPrecio.isEmpty()) {
                etPrecio.setError("Introduzca el precio");
            }if (mediaUri == null) {
                Toast.makeText(this, "Seleccione una imagen para continuar", Toast.LENGTH_SHORT).show();
            }
            if (radioGenero.getCheckedRadioButtonId() ==  -1){
                Toast.makeText(this, "Selecciona un género para continuar", Toast.LENGTH_SHORT).show();
            }if (radioTipoPrenda.getCheckedRadioButtonId() == -1){
                Toast.makeText(this, "Selecciona un tipo de prenda para continuar", Toast.LENGTH_SHORT).show();
            }
            buttonAceptar.setEnabled(true);
        }

    }



    public void writeNewPost(String description, String name, String price, String mediaUri) {

        String productKey = "product-" + mReference.push().getKey();
        Anuncio anuncio = new Anuncio(uid, mUser.getDisplayName(), mUser.getPhotoUrl().toString(), description,name,price, mediaUri, mediaType,vendido,reservado,fullAddress,latitude,longitude);

        long ts = -new Date().getTime();
        vendido= false;
        reservado = false;
        Map<String, Object> postValues = anuncio.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("products/data/" + productKey, postValues);
        childUpdates.put("products/all-products/" + productKey, ts);

        childUpdates.put("products/user-products/" + uid + "/" + productKey, ServerValue.TIMESTAMP);
        if (hombreRb.isChecked() && parteSuperior.isChecked()){
            childUpdates.put("products/hombresSup/" + productKey, ServerValue.TIMESTAMP);
        }else if(hombreRb.isChecked() && parteInferior.isChecked()){
            childUpdates.put("products/hombresInf/" + productKey,   ServerValue.TIMESTAMP);
        }else if(hombreRb.isChecked() && parteCalzado.isChecked()){
            childUpdates.put("products/hombresCalz/" + productKey,   ServerValue.TIMESTAMP);
        }else if (hombreRb.isChecked() && parteComplemento.isChecked()){
            childUpdates.put("products/hombresCompl/" + productKey,   ServerValue.TIMESTAMP);
        }else if (mujerRb.isChecked() && parteSuperior.isChecked()){
            childUpdates.put("products/mujeresSup/" + productKey,   ServerValue.TIMESTAMP);
        }else if(mujerRb.isChecked() && parteInferior.isChecked()){
            childUpdates.put("products/mujeresInf/" + productKey,   ServerValue.TIMESTAMP);
        }else if(mujerRb.isChecked() && parteCalzado.isChecked()){
            childUpdates.put("products/mujeresCalz/" + productKey,  ServerValue.TIMESTAMP);
        }else if (mujerRb.isChecked() && parteComplemento.isChecked()){
            childUpdates.put("products/mujeresCompl/" + productKey,   ServerValue.TIMESTAMP);
        }


        mReference.updateChildren(childUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                finish();
            }
        });
    }

    private void uploadAndWriteNewPost(final String postText, final String postName, final String postPrecio ) {
        if (mediaType != null) {
            FirebaseStorage.getInstance().getReference(mediaType + "/" + UUID.randomUUID().toString() + mediaUri.getLastPathSegment()).putFile(mediaUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    return task.getResult().getStorage().getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        String downloadUri = task.getResult().toString();
                        writeNewPost(postText,postName,postPrecio, downloadUri);

                    }
                }
            });
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK)
            switch (requestCode) {
                case GALLERY_REQUEST_CODE:
                        mFileUri= data.getData();
                        mediaType = "image";
                        GlideApp.with(this).load(mFileUri).into(imagePreview);


                case CAMERA_REQUEST_CODE:
                    mediaUri = mFileUri;
                    mediaType = "image";
                        GlideApp.with(this).load(mFileUri).into(imagePreview);

                        break;

                }
            }


    public void dialogChooseCamera() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SubirAnuncioActivity.this);
        builder.setTitle("Escoja fuente de imagen");
        builder.setItems(new CharSequence[] {"Galería", "Tomar Foto"},
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                dispatchGaleryPictureIntent();
                                break;
                            case 1:
                               dispatchTakePictureIntent();
                            default:
                                break;
                        }
                    }
                });

        builder.show();
    }
    private void dispatchGaleryPictureIntent() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickPhoto.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(pickPhoto,GALLERY_REQUEST_CODE);


    }

    private void dispatchTakePictureIntent() {

        Uri fileUri = null;
        try {
            fileUri = MediaFiles.createFile(this, MediaFiles.Type.IMAGE).uri;
        } catch (IOException ex) {
            // No se pudo crear el fichero
        }

        if (fileUri != null) {
            mFileUri = fileUri;

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            startActivityForResult(intent, CAMERA_REQUEST_CODE);
        }


    }

        GoogleMap gMap;
        @Override
        public void onMapReady (GoogleMap googleMap){
            Geocoder geocoder;
            List<Address> addresses;
            gMap = googleMap;


            geocoder = new Geocoder(this, Locale.getDefault());


            if (ActivityCompat.checkSelfPermission(SubirAnuncioActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(SubirAnuncioActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(SubirAnuncioActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                if (!gMap.isMyLocationEnabled())
                    gMap.setMyLocationEnabled(true);


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
                    try {
                        addresses = geocoder.getFromLocation(myLocation.getLatitude(),myLocation.getLongitude(),1);

                        String address = addresses.get(0).getAddressLine(0);
                        String area = addresses.get(0).getLocality();
                        String city = addresses.get(0).getAdminArea();
                        String country = addresses.get(0).getCountryName();
                        String postalcode= addresses.get(0).getPostalCode();

                        fullAddress = area + ","+" " + postalcode;

                        tvaddress.setText(fullAddress);


                    } catch (IOException e) {
                        e.printStackTrace();
                    }


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