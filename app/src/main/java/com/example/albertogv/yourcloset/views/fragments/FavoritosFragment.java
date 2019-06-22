package com.example.albertogv.yourcloset.views.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.request.RequestOptions;
import com.example.albertogv.yourcloset.GlideApp;
import com.example.albertogv.yourcloset.R;
import com.example.albertogv.yourcloset.model.Anuncio;
import com.example.albertogv.yourcloset.views.activities.AnuncioViewHolder;
import com.example.albertogv.yourcloset.views.activities.DetailActivity;
import com.example.albertogv.yourcloset.views.activities.SettingsActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

/**
 * A simple {@link Fragment} subclass.
 */
public class FavoritosFragment extends Fragment {
    public DatabaseReference mReference;
    public FirebaseUser mUser;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private FirebaseAuth mAuth;
    private FirebaseAuth firebaseAuth;

    public FavoritosFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_favoritos, container, false);
        RecyclerView recycler =view.findViewById(R.id.rvAnunciosFav);
        mReference = FirebaseDatabase.getInstance().getReference();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Anuncio>()
                .setIndexedQuery(setQuery(), mReference.child("products/data"), Anuncio.class)
                .setLifecycleOwner(this)
                .build();

        recycler.setLayoutManager(new StaggeredGridLayoutManager(3,GridLayoutManager.VERTICAL));
        recycler.setAdapter(new FirebaseRecyclerAdapter<Anuncio, AnuncioViewHolder>(options) {
            @Override
            public AnuncioViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                return new AnuncioViewHolder(inflater.inflate(R.layout.item_profile_post, viewGroup,  false));

            }
            @Override
            protected void onBindViewHolder(@NonNull final AnuncioViewHolder viewHolder, int position, @NonNull final Anuncio anuncio) {
                final String productKey = getRef(position).getKey();
                if (anuncio.mediaUrl != null) {
                    GlideApp.with(getContext()).load(anuncio.mediaUrl).apply(RequestOptions.centerCropTransform()).into(viewHolder.image);

                    if (firebaseAuthListener == null) {
                        firebaseAuth = FirebaseAuth.getInstance();
                    }
                    viewHolder.image.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getContext(), DetailActivity.class);
                            final String date= (String) DateUtils.getRelativeTimeSpanString(anuncio.time, System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS);
                            String precio = anuncio.getPrecioAnuncio() +" Euros";
                            String titulo = anuncio.getTituloAnuncio();
                            String descripcion = anuncio.getDescripcion();
                            String autor = anuncio.getDisplayName();
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
        return view;

    }

    Query setQuery () {
        return mReference.child("products/user-likes").child(mUser.getUid()).limitToFirst(100);
    }
}
