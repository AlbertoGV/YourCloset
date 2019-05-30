package com.example.albertogv.yourcloset.view.UI;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
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
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import maes.tech.intentanim.CustomIntent;

public class SubirAnuncioActivity extends AppCompatActivity implements OnMapReadyCallback {
    static final int RC_IMAGE_PICK = 9000;
    static final int RC_IMAGE_TAKE = 8000;
    static final int RC_VIDEO_TAKE = 8001;

    static final int RC_VIDEO_PICK = 9001;
    static final int RC_AUDIO_PICK = 9002;
    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;
    private Button buttonAceptar;
    private Button buttonCancelar;
    Uri mFileUri;
    Uri mediaUri;
    String mediaType;
    DatabaseReference mReference;

    FirebaseUser mUser;
    String uid;

    boolean recording = false;

    private MediaRecorder mRecorder = null;
    EditText etNombre, etArticulo, etPrecio;
    ImageView imagePreview;
    private GoogleApiClient googleApiClient;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private static final int PHOTO_SEND = 1;
    static final int REQUEST_RECORD_AUDIO_PERMISSION = 1212;
    public RadioButton hombreRb;

    public RadioButton mujerRb;
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
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO_PERMISSION);
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        imagePreview = findViewById(R.id.imagePreview);
        mReference = FirebaseDatabase.getInstance().getReference();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        uid = "uid-" + mUser.getUid();
        hombreRb = findViewById(R.id.hombre);
        mujerRb = findViewById(R.id.mujer);
        parteSuperior = findViewById(R.id.radioarriba);
        parteInferior = findViewById(R.id.radiobajo);
        parteCalzado = findViewById(R.id.calzado);
        parteComplemento = findViewById(R.id.radiocomplemento);
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
            CustomIntent.customType(SubirAnuncioActivity.this,"fadein-to-fadeout");
            }
        });


        imagePreview = findViewById(R.id.imagePreview);
        imagePreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();

            }
        });

        buttonAceptar = findViewById(R.id.aceptarAnuncio);
        buttonAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    submitPost();
                    progressLoading();
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

        if (postText.isEmpty()) {
            etArticulo.setError("Introduzca la descripcion");
        } else if (postName.isEmpty()) {
            etNombre.setError("Introduzca el nombre artículo ");
        } else if (postPrecio.isEmpty()) {
            etPrecio.setError("Introduzca el precio");
        } if (mediaUri == null) {
            Toast.makeText(this, "Seleccione una imagen para continuar", Toast.LENGTH_SHORT).show();
            return;
        }

        uploadAndWriteNewPost(postText,postName,postPrecio);

        buttonAceptar.setEnabled(false);

    }

    public void writeNewPost(String description, String name, String price, String mediaUri) {

        String productKey = "product-" + mReference.push().getKey();
        Anuncio anuncio = new Anuncio(uid, mUser.getDisplayName(), mUser.getPhotoUrl().toString(), description,name,price, mediaUri, mediaType, ServerValue.TIMESTAMP);

        Map<String, Object> postValues = anuncio.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("products/data/" + productKey, postValues);
        childUpdates.put("products/all-products/" + productKey, ServerValue.TIMESTAMP);

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
    public void progressLoading(){
        final ContentLoadingProgressBar pd = new ContentLoadingProgressBar(getApplicationContext());
        pd.show();
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

        if (requestCode == RC_IMAGE_TAKE && resultCode == RESULT_OK) {
            mediaUri = mFileUri;
            mediaType = "image";
            GlideApp.with(this).load(mediaUri).into(imagePreview);

              }

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
            startActivityForResult(intent, RC_IMAGE_TAKE);
        }
    }

    private void dispatchTakeVideoIntent() {

        Uri fileUri = null;
        try {
            fileUri = MediaFiles.createFile(this, MediaFiles.Type.VIDEO).uri;
        } catch (IOException ex) {
            // No se pudo crear el fichero
        }

        if (fileUri != null) {
            mFileUri = fileUri;

            Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            startActivityForResult(intent, RC_VIDEO_TAKE);
        }
    }

    void startRecording(){
        MediaFiles.UriPathFile file = null;
        try {
            file = MediaFiles.createFile(this, MediaFiles.Type.AUDIO);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(file != null) {
            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mRecorder.setOutputFile(file.path);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

            try {
                mRecorder.prepare();
            } catch (IOException e) {

            }

            mediaType = "audio";
            mediaUri = file.uri;
            mRecorder.start();
        }
    }

    void stopRecording(){
        if(mRecorder != null) {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
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


        // Assume thisActivity is the current activity
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