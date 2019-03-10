package com.example.albertogv.yourcloset;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;

public class HolderMensaje extends RecyclerView.ViewHolder {

    private TextView nombre;
    private  TextView mensaje;
    private TextView hora;
    private CircleImageView fotoMensajePerfil;
    private ImageView fotomensaje;


    public TextView getMensaje() {
        return mensaje;
    }

    public void setMensaje(TextView mensaje) {
        this.mensaje = mensaje;
    }

    public HolderMensaje(@NonNull View itemView) {
        super(itemView);
        nombre= itemView.findViewById(R.id.tv_nombre_mensaje);
        mensaje = itemView.findViewById(R.id.mensaje_mensaje);
        hora = itemView.findViewById(R.id.tv_hora);
        fotoMensajePerfil= itemView.findViewById(R.id.fotoPerfil);
        fotomensaje = itemView.findViewById(R.id.mensajeFoto);


    }

    public TextView getNombre() {
        return nombre;
    }

    public void setNombre(TextView nombre) {
        this.nombre = nombre;
    }

    public TextView getHora() {
        return hora;
    }

    public void setHora(TextView hora) {
        this.hora = hora;
    }

    public CircleImageView getFotoMensajePerfil() {
        return fotoMensajePerfil;
    }

    public void setFotoMensajePerfil(CircleImageView fotoMensajePerfil) {
        this.fotoMensajePerfil = fotoMensajePerfil;
    }

    public ImageView getFotomensaje() {
        return fotomensaje;
    }

    public void setFotomensaje(ImageView fotomensaje) {
        this.fotomensaje = fotomensaje;
    }
}
