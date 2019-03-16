package com.example.albertogv.yourcloset;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.albertogv.yourcloset.model.Anuncio;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public abstract class AnuncioListFragment extends Fragment {



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_anuncio_list, container, false);

        final RecyclerView recyclerView = view.findViewById(R.id.anuncioList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

     /*   final AnunciosRecyclerAdapter anunciosRecyclerAdapter = new AnunciosRecyclerAdapter();
        recyclerView.setAdapter(anunciosRecyclerAdapter);*/


        return view;
    }





    @Override public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) { switch (requestCode) { case 1: {
        // If request is cancelled, the result arrays are empty.

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

        } return; } // add other cases for more permissions } }
    }
    }




    abstract LiveData<List<Anuncio>>

    getAnuncios();


    public AnuncioListFragment() {
        // Required empty public constructor
    }


}


