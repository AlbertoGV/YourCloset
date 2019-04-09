package com.example.albertogv.yourcloset;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.albertogv.yourcloset.model.Anuncio;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.List;

import maes.tech.intentanim.CustomIntent;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.OnConnectionFailedListener {
    public DatabaseReference mReference;
    public FirebaseUser mUser;
    RecyclerView rvMain;
    TextView tvnombre;
    TextView tvdireccion;
    ImageView imagegoogle;
    Context context;

    List<Anuncio> list;
    private GoogleApiClient googleApiClient;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        mReference = FirebaseDatabase.getInstance().getReference();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        if (googleApiClient == null) {

            googleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this, this)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();
        }


        final FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SubirAnuncioActivity.class);
                startActivity(intent);
                CustomIntent.customType(MainActivity.this, "bottom-to-up");

                /**left-to-right
                 *right-to-left
                 *bottom-to-up
                 *up-to-bottom
                 *fadein-to-fadeout
                 *rotateout-to-rotatein*/
            }
        });


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemBackground(null);
        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Anuncio>()
                .setIndexedQuery(setQuery(), mReference.child("products/data"), Anuncio.class)
                .setLifecycleOwner(this)
                .build();

        rvMain = findViewById(R.id.rvMain);
        rvMain.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        rvMain.setAdapter(new FirebaseRecyclerAdapter<Anuncio, AnuncioViewHolder>(options){

            @Override
            protected void onBindViewHolder(@NonNull final AnuncioViewHolder anuncioViewHolder, int i, @NonNull final Anuncio anuncio) {

                final String productKey = getRef(i).getKey();

                RequestOptions ro = new RequestOptions()
                        .override(500, 500)
                        .optionalFitCenter()
                        .centerCrop()
                        .fallback(R.drawable.ic_launcher_background)
                        .transform(new RoundedCorners(30));
                if (anuncio.mediaUrl!= null) {
                  Glide.with(anuncioViewHolder.itemView.getContext())
                            .asBitmap()
                            .load(Uri.parse(anuncio.getMediaUrl()))
                            .apply(ro)
                            .into(anuncioViewHolder.ivphoto);

                    anuncioViewHolder.irChat.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(MainActivity.this,ChatActivity.class);
                            i.putExtra("PRODUCT_KEY", productKey);
                            startActivity(i);
                        }
                    });
                    anuncioViewHolder.anunimagePerfil.setImageURI(Uri.parse(anuncio.getAuthorPhotoUrl()));
                    anuncioViewHolder.time.setText(DateUtils.getRelativeTimeSpanString(anuncio.time,
                            System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS));
                    anuncioViewHolder.tvPrecio.setText(anuncio.getPrecioAnuncio()+" Euros");

                    anuncioViewHolder.tvnombreArticulo.setText(anuncio.getTituloAnuncio());
                    anuncioViewHolder.tvdescArticulo.setText(anuncio.description);

                    if (anuncio.likes.containsKey(mUser.getUid())) {
                        anuncioViewHolder.like.setImageResource(R.drawable.heart_on);

                    } else {
                        anuncioViewHolder.like.setImageResource(R.drawable.heart_off);
                    }
                    anuncioViewHolder.likeLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (anuncio.likes.containsKey(mUser.getUid())) {
                                mReference.child("products/data").child(productKey).child("likes").child(mUser.getUid()).setValue(null);
                                mReference.child("products/user-likes").child(mUser.getUid()).child(productKey).setValue(null);
                            } else {
                                mReference.child("products/data").child(productKey).child("likes").child(mUser.getUid()).setValue(true);
                                mReference.child("products/user-likes").child(mUser.getUid()).child(productKey).setValue(true);
                            }
                        }
                    });
                }



                    anuncioViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent intent = new Intent(MainActivity.this, DetailActivity.class);

                            String precio = anuncioViewHolder.tvPrecio.getText().toString();
                            String titulo = anuncioViewHolder.tvnombreArticulo.getText().toString();
                            String descripcion = anuncioViewHolder.tvdescArticulo.getText().toString();
                            String autor = anuncio.displayName;
                            String fecha = anuncioViewHolder.time.getText().toString();
                            String imagenperfil = anuncio.getAuthorPhotoUrl();
                            String imagen = anuncio.mediaUrl;

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
                    anuncioViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            descartar();
                            return false;
                        }
                    });


                }



            @NonNull
            @Override
            public AnuncioViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                return new AnuncioViewHolder(inflater.inflate(R.layout.grid_rv, viewGroup, false)) {
                };
            }
        });





            fab.setRippleColor(Color.DKGRAY);
        rvMain.setOnFlingListener(new RecyclerView.OnFlingListener() {
            @Override
            public boolean onFling(int velocityX, int velocityY) {
                if (velocityY < 0)
                    fab.show();
                    //Code to hide the UI, I have  a custom one that slides down the nav  bar and the fab
                else if (velocityY > 0)
                    fab.hide();
                //Code to show the UI

                return false;
            }
        });
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);

        View header = navigationView.getHeaderView(0);
        tvnombre =   header.findViewById(R.id.tv_sesion_nombre);
        tvdireccion= header.findViewById(R.id.tv_sesion_direccion);
        imagegoogle= header.findViewById(R.id.imageView_sesion);


        imagegoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,ProfileActivity.class);
                startActivity(intent);
            }
        });

        if (firebaseAuthListener == null) {
            firebaseAuth = FirebaseAuth.getInstance();
            firebaseAuthListener = new FirebaseAuth.AuthStateListener() {

                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if (user != null) {
                        setUserData(user);
                    } else {
                        goLogInScreen();
                    }

                }
            };

        }
    }






    private void setUserData(FirebaseUser user) {

        if (user != null) {
            tvnombre.setText(user.getDisplayName());
            tvdireccion.setText(user.getEmail());
        Glide.with(this).load(user.getPhotoUrl()).into(imagegoogle);
        }
    }

    @Override public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) { switch (requestCode) { case 1: {
        // If request is cancelled, the result arrays are empty.

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } return; } // add other cases for more permissions } }
        }

    }



  @Override
    protected void onStart() {
      super.onStart();
      if (firebaseAuth != null) {
          firebaseAuth.addAuthStateListener(firebaseAuthListener);
      }

  }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_salir) {
            shutdown();
            return true;

        }

        if (id == R.id.acercade){
            Intent i =  new Intent(this,InfoActivity.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public void shutdown(){


        AlertDialog.Builder dialogo = new AlertDialog.Builder(this);
        dialogo.setTitle("Vas a salir de Yourcloset");
        dialogo.setMessage("¿Estas seguro?");
        dialogo.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {
                finish();
                System.exit(0);
            }
        });
        dialogo.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {

            }
        });

        dialogo.show();

    }
    public void descartar(){

        AlertDialog.Builder dialogo = new AlertDialog.Builder(this);
        dialogo.setTitle("Vas a descartar este Anuncio");
        dialogo.setMessage("Este anuncio no te volverá a aparecer en tu tablón de anuncios");
        dialogo.setMessage("¿Estas seguro?");
        dialogo.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {
               Toast.makeText(context,"Anuncio descartado",Toast.LENGTH_SHORT).show();
            }
        });
        dialogo.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {

            }
        });
        dialogo.show();

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_subiranuncio) {
          Intent intent = new Intent(this, SubirAnuncioActivity.class);
            startActivity(intent);
            // Handle the camera action
        } else if (id == R.id.nav_favorites) {
           Intent intent = new Intent(this, TabbedActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_cerrar) {
            AuthUI.getInstance()
                    .signOut(MainActivity.this)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        public void onComplete(@NonNull Task<Void> task) {
                            startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        }
                    });

        } else if (id == R.id.nav_share) {
            Intent intent = new Intent(this, MyChatsActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_send) {
           shutdown();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void goLogInScreen() {
        Intent i = new Intent ( this, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }





    @Override
    protected void onStop() {
        super.onStop();
        if(firebaseAuthListener != null ){
            firebaseAuth.removeAuthStateListener(firebaseAuthListener);

        }

    }
    Query setQuery(){
        return  mReference.child("products/all-products").limitToFirst(100);
    }



}

