package com.example.albertogv.yourcloset;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.Image;
import android.net.ParseException;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.os.Messenger;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.GestureDetector;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.albertogv.yourcloset.model.Anuncio;
import com.github.curioustechizen.ago.RelativeTimeTextView;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.like.IconType;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import maes.tech.intentanim.CustomIntent;
import xyz.hanks.library.bang.SmallBangView;

import static android.graphics.Color.BLACK;
import static java.security.AccessController.getContext;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.OnConnectionFailedListener {
    RecyclerView rvMain;
    TextView tvnombre;
    TextView tvdireccion;
    ImageView imagegoogle;
    Context context;


    AnuncioViewModel anuncioViewModel;
    List<Anuncio> list;
    private GoogleApiClient googleApiClient;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context=this;

        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        if(googleApiClient == null) {

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
                CustomIntent.customType(MainActivity.this,"bottom-to-up");

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
        rvMain= findViewById(R.id.rvMain);
        Anuncio anuncio = new Anuncio();
        final AnunciosRecyclerAdapter myReyclerAdapter = new AnunciosRecyclerAdapter(list);
        rvMain.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        rvMain.setAdapter(myReyclerAdapter);
        anuncioViewModel = ViewModelProviders.of(this).get(AnuncioViewModel.class);

        anuncioViewModel.getAllAnuncios().observe(this, new Observer<List<Anuncio>>() {
            @Override
            public void onChanged(@Nullable List<Anuncio> anuncios) {
                myReyclerAdapter.setList(anuncios);
                myReyclerAdapter.notifyDataSetChanged();
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

        } else if (id == R.id.nav_manage) {
             firebaseAuth.signOut();
            Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(@NonNull Status status) {
                    if (status.isSuccess()) {
                        Toast.makeText(getApplicationContext(), R.string.close_sesion, Toast.LENGTH_SHORT).show();
                        goLogInScreen();

                    } else {
                        Toast.makeText(getApplicationContext(), R.string.not_close_sesion, Toast.LENGTH_SHORT).show();
                    }
                }
            });


        } else if (id == R.id.nav_share) {
            Intent intent = new Intent(this, ChatActivity.class);
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

    public class AnunciosRecyclerAdapter extends RecyclerView.Adapter<AnunciosRecyclerAdapter.AnuncioViewHolder> {
        List<Anuncio> list;

        public AnunciosRecyclerAdapter(List<Anuncio> anuncio) {

        }

        int permissionCheck = ActivityCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE);

        @NonNull
        @Override
        public AnuncioViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemAnuncio = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_rv, parent, false);
            return new AnunciosRecyclerAdapter.AnuncioViewHolder(itemAnuncio);
        }

        @Override
        public void onBindViewHolder(@NonNull final AnuncioViewHolder anuncioViewHolder, int i) {
            final Anuncio anuncio = list.get(i);
            RequestOptions ro = new RequestOptions()
                    .override(500, 500)
                    .optionalFitCenter()
                    .centerCrop()
                    .fallback(R.drawable.ic_launcher_background)
                    .transform(new RoundedCorners(30));
            if (anuncio.imageUri != null) {

                anuncio.setImageUri(anuncio.imageUri);
                Glide.with(anuncioViewHolder.itemView.getContext())
                        .asBitmap()
                        .load(Uri.parse(anuncio.getImageUri()))
                        .apply(ro)
                        .into(anuncioViewHolder.anunimage1);
                anuncioViewHolder.anunPrecio.setText(anuncio.precio + " Euros");
                anuncioViewHolder.anunTitulo.setText(anuncio.tituloAnuncio);
                anuncioViewHolder.anunDescrip.setText(anuncio.descripcion);

                anuncioViewHolder.anunImagePerfil.setImageURI(Uri.parse(anuncio.getImageperfil()));
                anuncioViewHolder.anunFecha.setText(anuncio.getFechapublicacion());
                anuncioViewHolder.anunautor.setText(anuncio.getAutor());





                /*anuncioViewHolder.likeButton.setCircleEndColorRes(R.color.colorAccent);*/


                anuncioViewHolder.imagelike.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        anuncioViewHolder.imagelike.setImageResource(R.drawable.heart_on);
                        Toast.makeText(MainActivity.this, "Añadido a Favoritos", Toast.LENGTH_SHORT).show();

                    }
                });

                anuncioViewHolder.imagelike.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        anuncioViewHolder.imagelike.setImageResource(R.drawable.heart_off);
                        Toast.makeText(MainActivity.this,"Eliminado de favoritos",Toast.LENGTH_SHORT).show();

                        return true;
                    }


                });





                anuncioViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(MainActivity.this, DetailActivity.class);

                        String precio = anuncioViewHolder.anunPrecio.getText().toString();
                        String titulo = anuncioViewHolder.anunTitulo.getText().toString();
                        String descripcion = anuncioViewHolder.anunDescrip.getText().toString();
                        String autor = anuncioViewHolder.anunautor.getText().toString();
                        String fecha = anuncioViewHolder.anunFecha.getText().toString();
                        String imagenperfil = anuncio.getImageperfil();
                        String imagen = anuncio.getImageUri();

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
        }
        Context context;

        @Override
        public int getItemCount() {
            return (list != null ? list.size() : 0);
        }

        public void setList(List<Anuncio> anuncios){
            this.list = anuncios;
            notifyDataSetChanged();
        }


        class AnuncioViewHolder extends RecyclerView.ViewHolder {
            private TextView anunTitulo;
            private TextView anunPrecio;
            private TextView anunDescrip;
            private  ImageView anunImagePerfil;
            private  TextView anunautor;
            private ImageView anunimage1;
            private Button irChatAnuncio;
            private TextView anunFecha;
            private ImageView imagelike;



            AnuncioViewHolder(final View itemAnuncio) {
                super(itemAnuncio);
                anunTitulo = itemAnuncio.findViewById(R.id.tvArticulo);
                anunPrecio = itemAnuncio.findViewById(R.id.tvPrecio);
                anunimage1= itemAnuncio.findViewById(R.id.ivMainImage);
                anunImagePerfil = itemAnuncio.findViewById(R.id.image_perfil);
                anunDescrip = itemAnuncio.findViewById(R.id.tvDescriptionArt);
                irChatAnuncio = itemAnuncio.findViewById(R.id.button_chat);
                anunFecha = itemAnuncio.findViewById(R.id.anuncio_fecha);
                anunautor = itemAnuncio.findViewById(R.id.anuncioautormain);
                imagelike = itemAnuncio.findViewById(R.id.like);


                irChatAnuncio.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent (MainActivity.this,ChatActivity.class);
                        startActivity(i);
                    }
                });

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



}

