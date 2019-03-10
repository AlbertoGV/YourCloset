package com.example.albertogv.yourcloset.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.example.albertogv.yourcloset.model.Anuncio;

@Database(entities = {Anuncio.class}, version = 2)
public abstract class AnuncioRoomDataBase extends RoomDatabase {

    public abstract AnuncioDAO anuncioDAO();

    private static volatile AnuncioRoomDataBase INSTANCE;

    public static AnuncioRoomDataBase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AnuncioRoomDataBase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AnuncioRoomDataBase.class, "anuncio_database")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}