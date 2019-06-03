package com.example.albertogv.yourcloset.view.views;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.albertogv.yourcloset.GlideApp;
import com.example.albertogv.yourcloset.R;
import com.example.albertogv.yourcloset.model.Anuncio;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.view.View.VISIBLE;
import static com.firebase.ui.auth.AuthUI.TAG;

public class ProfileActivity extends AppCompatActivity {
    public DatabaseReference mReference;
    public FirebaseUser mUser;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mAuth;
    private int mPostsCount = 0;
    String uid;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    ImageView imageView;
    TextView tvNameProfile;
    TextView tvPosts;
    private  static  final  int PHOTO_PERFIL=2;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mReference = FirebaseDatabase.getInstance().getReference();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        tvPosts = findViewById(R.id.tvPosts);
        imageView = findViewById(R.id.image3);
        tvNameProfile = findViewById(R.id.name);
        database = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        Intent intent = getIntent();
        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        database = FirebaseDatabase.getInstance();
        RecyclerView recycler = findViewById(R.id.rvPosts1);
        GlideApp.with(ProfileActivity.this).load(mUser.getPhotoUrl()).into(imageView);

        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Anuncio>()
                .setIndexedQuery(setQuery(), mReference.child("products/data"), Anuncio.class)
                .setLifecycleOwner(this)
                .build();
        final String productKey = intent.getStringExtra("PRODUCT_KEY");
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Selecciona una foto"), PHOTO_PERFIL);


            }
        });

        tvNameProfile.setText(mUser.getDisplayName());
        recycler.setLayoutManager(new StaggeredGridLayoutManager(3,GridLayoutManager.VERTICAL));
        recycler.setAdapter(new FirebaseRecyclerAdapter<Anuncio, AnuncioViewHolder>(options) {
            @Override
            public AnuncioViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                return new AnuncioViewHolder(inflater.inflate(R.layout.item_profile_post, viewGroup, false));
            }


            @Override
            protected void onBindViewHolder(@NonNull final AnuncioViewHolder anuncioviewHolder, final int position, final Anuncio anuncio) {
                final String postKey = getRef(position).getKey();
                    if(anuncio.mediaUrl!= null) {
                        GlideApp.with(ProfileActivity.this).load(anuncio.getMediaUrl()).apply(RequestOptions.centerCropTransform()).into(anuncioviewHolder.image);
                        anuncioviewHolder.image.setVisibility(VISIBLE);
                        if (firebaseAuthListener == null) {
                            firebaseAuth = FirebaseAuth.getInstance();
                        }
                       final String date= (String) DateUtils.getRelativeTimeSpanString(anuncio.time, System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS);
                             anuncioviewHolder.image.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(ProfileActivity.this,SettingsActivity.class);

                            String precio = anuncio.getPrecioAnuncio() +" Euros";
                            String titulo = anuncio.getTituloAnuncio();
                            String descripcion = anuncio.getDescripcion();
                            String autor = firebaseAuth.getCurrentUser().getDisplayName();
                            String fecha = date;
                            String imagenperfil = anuncio.getAuthorPhotoUrl();
                            String imagen = anuncio.getMediaUrl();
                            String productKey1 = productKey;

                            intent.putExtra("nombre", autor);
                            intent.putExtra("fecha", fecha);
                            intent.putExtra("imgperfil", imagenperfil);
                            intent.putExtra("precio", precio);
                            intent.putExtra("Titulo", titulo);
                            intent.putExtra("descripcion", descripcion);
                            intent.putExtra("imagen", imagen);
                            intent.putExtra("PRODUCT_KEY", productKey1);
                            startActivityForResult(intent, 1);
                        }
                    });

                }
                    }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();
        getProductCount();

    }


    Query setQuery () {
        return mReference.child("products/user-products").child("uid-"+mUser.getUid()).limitToFirst(100);

    }
    private void getProductCount(){
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PHOTO_PERFIL && resultCode == RESULT_OK) {
            final Uri u = data.getData();
            storageReference = firebaseStorage.getReference("Imagenes perfil");//imagenes de perfil
            final StorageReference fotoReferencia = storageReference.child(u.getLastPathSegment());
            final UploadTask uploadTask = fotoReferencia.putFile(u);
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
                    FirebaseDatabase  database = FirebaseDatabase.getInstance();
                    DatabaseReference mDatabaseRef = database.getReference();
                    Glide.with(ProfileActivity.this).load(u).into(imageView);

                }

            });
        }
    }
}