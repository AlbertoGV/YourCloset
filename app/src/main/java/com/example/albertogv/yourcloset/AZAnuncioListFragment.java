package com.example.albertogv.yourcloset;

import android.arch.lifecycle.LiveData;

import com.example.albertogv.yourcloset.model.Anuncio;

import java.util.List;

public class AZAnuncioListFragment extends AnuncioListFragment{

    @Override
    LiveData<List<Anuncio>> getAnuncios() {
        return null;
    }
}
