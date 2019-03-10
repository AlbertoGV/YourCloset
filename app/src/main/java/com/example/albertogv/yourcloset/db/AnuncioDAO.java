package com.example.albertogv.yourcloset.db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.example.albertogv.yourcloset.model.Anuncio;

import java.util.List;


@Dao
public interface AnuncioDAO {

    @Insert
    abstract void insert(Anuncio anuncio);

    @Query("SELECT * FROM anuncio")
    abstract LiveData<List<Anuncio>> getAllAnuncios();

    @Query("SELECT * FROM anuncio ORDER BY fechapublicacion DESC")
    abstract LiveData<List<Anuncio>> getAllAnunciosOrderedByDate();

    @Query("SELECT * FROM anuncio ORDER BY tituloAnuncio")
    abstract LiveData<List<Anuncio>> getAllAnunciosOrderedByTitle();

    @Query("SELECT * FROM anuncio ORDER BY autor DESC")
    abstract LiveData<List<Anuncio>> getAllAnunciosOrderedByRating();

    @Query("SELECT * FROM anuncio WHERE id = :id")
    abstract LiveData<Anuncio> getAnuncio(int id);

    @Query("UPDATE anuncio SET rating=:rating WHERE id=:id")
    abstract void setRating(int id, float rating);

    @Query("DELETE FROM anuncio")
    abstract void deleteAllAnuncios();
}