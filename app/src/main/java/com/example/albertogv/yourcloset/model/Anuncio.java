package com.example.albertogv.yourcloset.model;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class Anuncio implements Serializable {


        @PrimaryKey(autoGenerate = true)
        public int id;

        public String autor;

        public int getId() {
                return id;
        }

        public void setId(int id) {
                this.id = id;
        }

        public String getAutor() {
                return autor;
        }



        public void setAutor(String autor) {
                this.autor = autor;
        }

        public String getTituloAnuncio() {
                return tituloAnuncio;
        }

        public void setTituloAnuncio(String tituloAnuncio) {
                this.tituloAnuncio = tituloAnuncio;
        }

        public String getDescripcion() {
                return descripcion;
        }

        public void setDescripcion(String descripcion) {
                this.descripcion = descripcion;
        }

        public float getRating() {
                return rating;
        }

        public void setRating(float rating) {
                this.rating = rating;
        }

        public String getFechapublicacion() {
                return fechapublicacion;
        }

        public void setFechapublicacion(String fechapublicacion) {
                this.fechapublicacion = fechapublicacion;
        }

        public String tituloAnuncio;
        public String descripcion;
        public String precio;
        public float rating;
        public String fechapublicacion;

        public String getImageperfil() {
                return imageperfil;
        }

        public void setImageperfil(String imageperfil) {
                this.imageperfil = imageperfil;
        }

        public String imageperfil;

        public String getPrecio() {
                return precio;
        }

        public void setPrecio(String precio) {
                this.precio = precio;
        }

        public String getImageUri() {
                return imageUri;
        }

        public void setImageUri(String imageUri) {
                this.imageUri = imageUri;
        }

        public String imageUri;
    }



