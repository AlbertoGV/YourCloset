package com.example.albertogv.yourcloset;


import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.example.albertogv.yourcloset.model.Anuncio;

import java.util.List;


public class AnuncioViewModel extends AndroidViewModel {

    private AnuncioRepository mRepository;

    public AnuncioViewModel(Application application) {
        super(application);
        mRepository = new AnuncioRepository(application);
    }

    LiveData<List<Anuncio>> getAllAnuncios() { return mRepository.getAllAnucios(); }

    LiveData<List<Anuncio>> getAllAnunciosOrderedByTitle() { return mRepository.getAllAnunciosOrderedByTitle(); }
    LiveData<List<Anuncio>> getAllAnunciosOrderedByDate() { return mRepository.getAllAnunciosOrderedByDate(); }
    LiveData<List<Anuncio>> getAllAnunciosOrderedByRating() { return mRepository.getAllPoemsOrderedByRating(); }

    LiveData<Anuncio> getAnuncio(int id){ return mRepository.getAnuncio(id); }

    public void insertAnuncio(Anuncio anuncio) { mRepository.insert(anuncio); }

    public void setRating(Anuncio anuncio) { mRepository.setRating(anuncio); }

    public void deleteAllPoems() { mRepository.deleteAllAnuncios(); }
}