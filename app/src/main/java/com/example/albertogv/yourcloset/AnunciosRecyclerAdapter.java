package com.example.albertogv.yourcloset;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.albertogv.yourcloset.model.Anuncio;
import java.util.List;

public class AnunciosRecyclerAdapter extends RecyclerView.Adapter<AnunciosRecyclerAdapter.AnuncioViewHolder> {

    List<Anuncio> list;

    @Override
    public AnuncioViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemAnuncio = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_anuncio, parent, false);
        return new AnuncioViewHolder(itemAnuncio);
    }


    @Override
    public void onBindViewHolder(final AnuncioViewHolder holder, int position) {
        final Anuncio anuncio = list.get(position);
        holder.anunautor.setText(anuncio.getAutor());
        holder.anunimagePerfil.setImageURI(Uri.parse(anuncio.getImageperfil()));
        holder.anunTitulo.setText(anuncio.tituloAnuncio);
        holder.anunDesc.setText(anuncio.descripcion);
        holder.anunFecha.setText(anuncio.getFechapublicacion());
        holder.anunPrecioList.setText(anuncio.getPrecio()+" Euros");
        RequestOptions ro = new RequestOptions()

                .centerInside()
                .transform(new RoundedCorners(15));
                anuncio.setImageUri(anuncio.imageUri);
        Glide.with(holder.itemView.getContext())
                .load(Uri.parse(anuncio.imageUri))
                .apply(ro)
                .into(holder.anunimage);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(holder.itemView.getContext(),DetailActivityLists.class);

                String precio = holder.anunPrecioList.getText().toString();
                String titulo = holder.anunTitulo.getText().toString();
                String imagenperfil = anuncio.getImageperfil();
                String fecha = holder.anunFecha.getText().toString();
                String autor = holder.anunautor.getText().toString();
                String descripcion= holder.anunDesc.getText().toString();

                String imagen = anuncio.getImageUri();

                intent.putExtra("AUTOR",autor);
                intent.putExtra("PRECIO", precio);
                intent.putExtra("TITULO",titulo);
                intent.putExtra("IMGPERFIL",imagenperfil);
                intent.putExtra("FECHA",fecha);
                intent.putExtra("DESCRIPCION",descripcion);
                intent.putExtra("IMAGEN",imagen);
                holder.itemView.getContext().startActivity(intent);

            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder dialogo = new AlertDialog.Builder(holder.itemView.getContext());
                dialogo.setTitle("Vas a descartar este Anuncio");
                dialogo.setMessage("Este anuncio no te volverá a aparecer en tu tablón de anuncios");
                dialogo.setMessage("¿Estas seguro?");
                dialogo.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {
                        Toast.makeText(holder.itemView.getContext(),"Anuncio descartado",Toast.LENGTH_SHORT).show();
                    }
                });
                dialogo.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {

                    }
                });
                dialogo.show();
                return false;
            }
        });







    }





    @Override
    public int getItemCount() {
        return (list != null ? list.size() : 0);
    }

    public void setList(List<Anuncio> anuncios){
        this.list = anuncios;
    }

    class AnuncioViewHolder extends RecyclerView.ViewHolder {
        private TextView anunDesc;
        private TextView anunTitulo;
        private ImageView anunimagePerfil;
        private ImageView anunimage;
        private TextView anunFecha;
        private  TextView anunautor;
        private TextView anunPrecioList;
        private RatingBar ratingBar;


        AnuncioViewHolder(final View itemAnuncio) {
            super(itemAnuncio);
            anunTitulo = itemAnuncio.findViewById(R.id.anuncio_author);
            anunDesc = itemAnuncio.findViewById(R.id.anuncio_descripcion);
            anunimage = itemAnuncio.findViewById(R.id.img_cliente);
            anunFecha = itemAnuncio.findViewById(R.id.anuncio_fecha);
            anunPrecioList = itemAnuncio.findViewById(R.id.anuncio_precioList);
            anunautor = itemAnuncio.findViewById(R.id.anuncioautorlist);
            anunimagePerfil = itemAnuncio.findViewById(R.id.image_perfil);
           // ratingBar = itemPoem.findViewById(R.id.ratingBar);



        }

    }
}
