package com.example.albertogv.yourcloset.model;

import com.google.firebase.database.Exclude;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Anuncio {


        public String getUid() {
                return uid;
        }

        public void setUid(String uid) {
                this.uid = uid;
        }

        public String getDisplayName() {
                return displayName;
        }

        public void setDisplayName(String displayName) {
                this.displayName = displayName;
        }

        public String getAuthorPhotoUrl() {
                return photoUrl;
        }

        public void setAuthorPhotoUrl(String authorPhotoUrl) {
                this.photoUrl = authorPhotoUrl;
        }

        public String getDescripcion() {
                return description;
        }

        public void setDescripcion(String descripcion) {
                this.description = descripcion;
        }

        public String getMediaUrl() {
                return mediaUrl;
        }

        public void setMediaUrl(String mediaUrl) {
                this.mediaUrl = mediaUrl;
        }

        public String getTituloAnuncio() {
                return tituloAnuncio;
        }

        public void setTituloAnuncio(String tituloAnuncio) {
                this.tituloAnuncio = tituloAnuncio;
        }

        public String getPrecioAnuncio() {
                return precioAnuncio;
        }

        public void setPrecioAnuncio(String precioAnuncio) {
                this.precioAnuncio = precioAnuncio;
        }

        public String getMediaType() {
                return mediaType;
        }

        public void setMediaType(String mediaType) {
                this.mediaType = mediaType;
        }

        public long getTime() {
                return time;
        }

        public void setTime(long time) {
                this.time = time;
        }

        public Map<String, Boolean> getLikes() {
                return likes;
        }

        public void setLikes(Map<String, Boolean> likes) {
                this.likes = likes;
        }

        public String uid;
        public String displayName;
        public String photoUrl;
        public String description;
        public String mediaUrl;
        public String tituloAnuncio;
        public String precioAnuncio;
        public String mediaType;
        public long time;


        public Map<String, Boolean> likes = new HashMap<>();

        public Anuncio() {}


        public Anuncio(String uid, String author, String authorPhotoUrl, String descripcion,String tituloAnuncio,String precioAnuncio,String mediaUrl, String mediaType,Map timestamp) {
                this.uid = uid;
                this.displayName = author;
                this.photoUrl = authorPhotoUrl;
                this.description = descripcion;
                this.mediaUrl = mediaUrl;
                this.precioAnuncio = precioAnuncio;
                this.tituloAnuncio = tituloAnuncio;
                this.mediaType = mediaType;
                this.time = new Date().getTime();

        }
        @Exclude
        public Map<String, Object> toMap() {
                HashMap<String, Object> result = new HashMap<>();
                result.put("uid", uid);
                result.put("displayName", displayName);
                result.put("authorPhotoUrl", photoUrl);
                result.put("descripcion", description);
                result.put("mediaUrl", mediaUrl);
                result.put("precioAnuncio",precioAnuncio);
                result.put("tituloAnuncio",tituloAnuncio);
                result.put("mediaType", mediaType);
                result.put("time", time);
                result.put("likes", likes);

                return result;
        }
}