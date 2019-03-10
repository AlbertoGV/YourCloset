package com.example.albertogv.yourcloset;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.albertogv.yourcloset.model.MensajeRecibir;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class  AdapterMensajes  extends RecyclerView.Adapter<HolderMensaje> {
    List<MensajeRecibir> mensajeList= new ArrayList<>();
    private Context c;

    public AdapterMensajes( Context c) {

        this.c = c;
    }
    public void addMensaje(MensajeRecibir m){
        mensajeList.add (m);
        notifyItemInserted(mensajeList.size());
    }

    @NonNull
    @Override
    public HolderMensaje onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(c).inflate(R.layout.card_view_mensajes, parent, false);
        return new HolderMensaje(v);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderMensaje holder, int position) {
        holder.getNombre().setText(mensajeList.get(position).getNombre());
        holder.getMensaje().setText(mensajeList.get(position).getMensaje());
        if(mensajeList.get(position).getType_mensaje().equals("2")){
            holder.getFotomensaje().setVisibility(View.VISIBLE);
            holder.getMensaje().setVisibility(View.VISIBLE);
            Glide.with(c).load(mensajeList.get(position).getUrlFoto()).into(holder.getFotomensaje());
        }else if(mensajeList.get(position).getType_mensaje().equals("1")){
           holder.getFotomensaje().setVisibility(View.GONE);
            holder.getMensaje().setVisibility(View.VISIBLE);

        }
        if(mensajeList.get(position).getFotoPerfil().isEmpty()){
          holder.getFotomensaje().setImageResource(R.mipmap.ic_launcher);
        }else{
            Glide.with(c).load(mensajeList.get(position).getUrlFoto()).into(holder.getFotomensaje());
        }

        Long codigoHora = mensajeList.get(position).getHora();
        Date d = new Date(codigoHora);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm:ss a");//a=Am o PM
        holder.getHora().setText(simpleDateFormat.format(d));

    }

    @Override
    public int getItemCount() {
        return mensajeList.size();
    }
}
