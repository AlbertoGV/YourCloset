package com.example.albertogv.yourcloset;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


public class AnuncioViewHolder extends RecyclerView.ViewHolder {

    public ImageView ivphoto;
    public TextView tvPrecio;
    public ImageView like;
    public  TextView time;
    public TextView tvnombreArticulo;
    public ImageView anunimagePerfil;
    public TextView tvdescArticulo;
    public LinearLayout likeLayout;

    public AnuncioViewHolder(View itemView) {
        super(itemView);

        ivphoto = itemView.findViewById(R.id.ivMainImage);
        time = itemView.findViewById(R.id.anuncio_fechatime);
        like = itemView.findViewById(R.id.like);
        likeLayout = itemView.findViewById(R.id.like_layout);
        tvdescArticulo = itemView.findViewById(R.id.tvDescriptionArt);
        tvnombreArticulo= itemView.findViewById(R.id.tvArticulo);
        tvPrecio = itemView.findViewById(R.id.tvPrecio);
        anunimagePerfil = itemView.findViewById(R.id.image_perfilpass);

    }
}