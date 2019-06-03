package com.example.albertogv.yourcloset.view.views;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.albertogv.yourcloset.R;


public class AnuncioViewHolder extends RecyclerView.ViewHolder {

    public ImageView ivphoto;
    public TextView tvPrecio;
    public ImageView like;
    public ImageView image;
    public TextView time;
    public TextView tvnombreArticulo;
    public ImageView anunimagePerfil;
    public ImageView irChat;
    public ImageView settings;
    public TextView tvdescArticulo;
    public RelativeLayout likeLayout;
    public ImageView tvVendido;
    public ImageView tvReservado;

    public AnuncioViewHolder(View itemView) {
        super(itemView);
        settings = itemView.findViewById(R.id.settings_pr);
        ivphoto = itemView.findViewById(R.id.ivMainImage);
        time = itemView.findViewById(R.id.anuncio_fechatime);
        like = itemView.findViewById(R.id.like);
        tvReservado = itemView.findViewById(R.id.ivReservado);
        image = itemView.findViewById(R.id.image);
        irChat = itemView.findViewById(R.id.chat);
        tvVendido = itemView.findViewById(R.id.ivVendido);
        likeLayout = itemView.findViewById(R.id.like_layout);
        tvdescArticulo = itemView.findViewById(R.id.tvDescriptionArt);
        tvnombreArticulo= itemView.findViewById(R.id.tvArticulo);
        tvPrecio = itemView.findViewById(R.id.tvPrecio);
        anunimagePerfil = itemView.findViewById(R.id.image_perfilpass);

    }
}