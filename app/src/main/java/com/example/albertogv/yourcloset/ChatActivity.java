package com.example.albertogv.yourcloset;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.albertogv.yourcloset.model.MensajeEnviar;
import com.example.albertogv.yourcloset.model.MensajeRecibir;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    private CircleImageView fotoPerfil;
    private TextView nombre;
    private RecyclerView rvMensajes;
    private EditText txtMensaje;
    private Button btnEnviar;
    private AdapterMensajes adapter;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private ImageButton btnEnviarFoto;
    private String fotoPerfilCadena;
    FirebaseUser user;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;

    private static  final int PHOTO_SEND = 1;
    private  static  final  int PHOTO_PERFIL=2;

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(firebaseAuthListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        fotoPerfil = findViewById(R.id.fotoPerfil);
        nombre = findViewById(R.id.nombrechat);
        if (user != null) {
            nombre.setText(user.getDisplayName());
        }
        rvMensajes = findViewById(R.id.rvMensajes);
        txtMensaje = findViewById(R.id.txtMensaje);
        btnEnviar = findViewById(R.id.btnEnviar);
        btnEnviarFoto = findViewById(R.id.foto_enviar);
        fotoPerfilCadena = "";
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("chat");// sala del chat (nombre)
        firebaseStorage = FirebaseStorage.getInstance();

        android.support.v7.widget.Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        adapter = new AdapterMensajes(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvMensajes.setLayoutManager(linearLayoutManager);
        rvMensajes.setAdapter(adapter);
        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference.push().setValue((new MensajeEnviar(txtMensaje.getText().toString(), nombre.getText().toString(), fotoPerfilCadena, "1", ServerValue.TIMESTAMP)));
                txtMensaje.setText("");
            }
        });

        btnEnviarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Selecciona una foto"), PHOTO_SEND);

            }
        });
        fotoPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Selecciona una foto"), PHOTO_PERFIL);


            }
        });
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                setScrollBar();
            }
        });
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    setUserData(user);
                }

            }
        };





        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                MensajeRecibir m = dataSnapshot.getValue(MensajeRecibir.class);
                adapter.addMensaje(m);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setUserData(FirebaseUser user) {
        if (user != null) {
            //  setContentView(R.layout.nav_header_main);

            nombre.setText(user.getDisplayName());

            Glide.with(this).load(user.getPhotoUrl()).into(fotoPerfil);

        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (firebaseAuthListener != null) {
            firebaseAuth.removeAuthStateListener(firebaseAuthListener);

        }
    }

    private void setScrollBar(){
        rvMensajes.scrollToPosition(adapter.getItemCount()-1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PHOTO_SEND && resultCode == RESULT_OK){
             final Uri u = data.getData();
            storageReference = firebaseStorage.getReference("Imagenes chat");//imagenes del chat
            final StorageReference fotoReferencia= storageReference.child(u.getLastPathSegment());
            final UploadTask uploadTask =fotoReferencia.putFile(u);
            fotoReferencia.putFile(u).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }

                            // Continue with the task to get the download URL
                            return fotoReferencia.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri u = task.getResult();
                            } else {
                                // Handle failures
                                // ...
                            }
                        }
                    });

                    MensajeEnviar m = new MensajeEnviar(nombre.getText().toString()+" te ha enviado una foto",nombre.getText().toString(),fotoPerfil.toString(),"", u.toString(),ServerValue.TIMESTAMP);
                    databaseReference.push().setValue(m);
                }
            });

        }else if (requestCode == PHOTO_PERFIL && resultCode == RESULT_OK){

             final Uri u = data.getData();
            storageReference = firebaseStorage.getReference("Imagenes perfil");//imagenes de perfil
            final StorageReference fotoReferencia= storageReference.child(u.getLastPathSegment());
            final UploadTask uploadTask =fotoReferencia.putFile(u);
            fotoReferencia.putFile(u).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }

                            // Continue with the task to get the download URL
                            return fotoReferencia.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri u = task.getResult();
                            } else {
                                // Handle failures
                                // ...
                            }
                        }
                    });
                    MensajeEnviar m = new MensajeEnviar(nombre.getText().toString() + " ha actualizado la foto de su perfil ",u.toString(),nombre.getText().toString(),"",fotoPerfil.toString(),ServerValue.TIMESTAMP);
                    databaseReference.push().setValue(m);
                    Glide.with(ChatActivity.this).load(user.getPhotoUrl()).into(fotoPerfil);

                }
            });
        }


}}
