package com.example.albertogv.yourcloset;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import com.example.albertogv.yourcloset.db.AnuncioDAO;
import com.example.albertogv.yourcloset.db.AnuncioRoomDataBase;
import com.example.albertogv.yourcloset.model.Anuncio;

import java.util.List;

public class AnuncioRepository {

    private AnuncioDAO mAnunDao;

    AnuncioRepository(Application application) {
        mAnunDao = AnuncioRoomDataBase.getDatabase(application).anuncioDAO();
    }

    LiveData<List<Anuncio>> getAllAnucios() {
        return mAnunDao.getAllAnuncios();
    }

    LiveData<List<Anuncio>>getAllAnunciosOrderedByTitle() {
        return mAnunDao.getAllAnunciosOrderedByTitle();
    }

    LiveData<List<Anuncio>> getAllAnunciosOrderedByDate() {
        return mAnunDao.getAllAnunciosOrderedByDate();
    }

    LiveData<List<Anuncio>> getAllPoemsOrderedByRating() {
        return mAnunDao.getAllAnunciosOrderedByRating();
    }

    LiveData<Anuncio> getAnuncio(int id){ return mAnunDao.getAnuncio(id); }

    public void insert(Anuncio anuncio) {
        new InsertAsyncTask(mAnunDao).execute(anuncio);
    }

    private static class InsertAsyncTask extends AsyncTask<Anuncio, Void, Void> {

        private AnuncioDAO mAsyncTaskDao;

        InsertAsyncTask(AnuncioDAO dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Anuncio... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }

    public void setRating(Anuncio anuncio){
        new SetRatingAsyncTask(mAnunDao).execute(anuncio);
    }

    private static class SetRatingAsyncTask extends AsyncTask<Anuncio, Void, Void> {

        private AnuncioDAO mAsyncTaskDao;

        SetRatingAsyncTask(AnuncioDAO dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Anuncio... poems) {
            mAsyncTaskDao.setRating(poems[0].id, poems[0].rating);
            return null;
        }
    }

    public void deleteAllAnuncios(){
        new DeleteAsyncTask(mAnunDao).execute();
    }

    private static class DeleteAsyncTask extends AsyncTask<Anuncio, Void, Void> {

        private AnuncioDAO mAsyncTaskDao;

        DeleteAsyncTask(AnuncioDAO dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Anuncio... anuncios) {
            mAsyncTaskDao.deleteAllAnuncios();
            return null;
        }
    }
}