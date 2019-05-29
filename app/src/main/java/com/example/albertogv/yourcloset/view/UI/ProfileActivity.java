package com.example.albertogv.yourcloset.view.UI;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.request.RequestOptions;
import com.example.albertogv.yourcloset.GlideApp;
import com.example.albertogv.yourcloset.R;
import com.example.albertogv.yourcloset.model.Anuncio;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import static android.view.View.VISIBLE;
import static com.firebase.ui.auth.AuthUI.TAG;

public class ProfileActivity extends AppCompatActivity {
    public DatabaseReference mReference;
    public FirebaseUser mUser;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mAuth;
    private int mPostsCount = 0;
    private FirebaseAuth firebaseAuth;
    ImageView imageView;
    TextView tvPosts;
    TextView tvEdit;
    TextView tvProfileName;
    TextView tvweb;
    TextView tvbio;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mReference = FirebaseDatabase.getInstance().getReference();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        tvPosts = findViewById(R.id.tvPosts);
        tvEdit = findViewById(R.id.textEditProfile);
        tvProfileName = findViewById(R.id.display_name);
        tvweb = findViewById(R.id.tv_web);
        tvbio = findViewById(R.id.tv_bio);
        imageView = findViewById(R.id.image3);
        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        RecyclerView recycler = findViewById(R.id.rvPosts1);
        GlideApp.with(ProfileActivity.this).load(mUser.getPhotoUrl()).into(imageView);

        getProductCount();
        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Anuncio>()
                .setIndexedQuery(setQuery(), mReference.child("products/data"), Anuncio.class)
                .setLifecycleOwner(this)
                .build();




        recycler.setLayoutManager(new StaggeredGridLayoutManager(3,GridLayoutManager.VERTICAL));
        recycler.setAdapter(new FirebaseRecyclerAdapter<Anuncio, AnuncioViewHolder>(options) {
            @Override
            public AnuncioViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                return new AnuncioViewHolder(inflater.inflate(R.layout.item_profile_post, viewGroup, false));
            }


            @Override
            protected void onBindViewHolder(final AnuncioViewHolder anuncioviewHolder, final int position, final Anuncio anuncio) {
                final String postKey = getRef(position).getKey();
                    if(anuncio.mediaUrl!= null) {
                        GlideApp.with(ProfileActivity.this).load(anuncio.getMediaUrl()).apply(RequestOptions.centerCropTransform()).into(anuncioviewHolder.image);
                        anuncioviewHolder.image.setVisibility(VISIBLE);
                        if (firebaseAuthListener == null) {
                            firebaseAuth = FirebaseAuth.getInstance();
                        }
                    anuncioviewHolder.image.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                           Anuncio anuncio = new Anuncio();
                            Intent intent = new Intent(ProfileActivity.this,DetailActivity.class);

                            String precio = anuncio.getPrecioAnuncio();
                            String titulo = anuncio.getTituloAnuncio();
                            String descripcion = anuncio.getDescripcion();
                            String autor = firebaseAuth.getCurrentUser().getDisplayName();
                            String fecha = String.valueOf(anuncio.getTime());
                            String imagenperfil = anuncio.getAuthorPhotoUrl();
                            String imagen = anuncio.getMediaUrl();

                            intent.putExtra("nombre", autor);
                            intent.putExtra("fecha", fecha);
                            intent.putExtra("imgperfil", imagenperfil);
                            intent.putExtra("precio", precio);
                            intent.putExtra("Titulo", titulo);
                            intent.putExtra("descripcion", descripcion);
                            intent.putExtra("imagen", imagen);
                            startActivityForResult(intent, 1);
                        }
                    });
                        }

                    }


        });

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

}