package com.example.albertogv.yourcloset.db;

import android.arch.lifecycle.ComputableLiveData;
import android.arch.lifecycle.LiveData;
import android.arch.persistence.db.SupportSQLiteStatement;
import android.arch.persistence.room.EntityInsertionAdapter;
import android.arch.persistence.room.InvalidationTracker.Observer;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.RoomSQLiteQuery;
import android.arch.persistence.room.SharedSQLiteStatement;
import android.database.Cursor;
import android.support.annotation.NonNull;
import com.example.albertogv.yourcloset.model.Anuncio;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@SuppressWarnings("unchecked")
public class AnuncioDAO_Impl implements AnuncioDAO {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter __insertionAdapterOfAnuncio;

  private final SharedSQLiteStatement __preparedStmtOfSetRating;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAllAnuncios;

  public AnuncioDAO_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfAnuncio = new EntityInsertionAdapter<Anuncio>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR ABORT INTO `Anuncio`(`id`,`autor`,`tituloAnuncio`,`descripcion`,`precio`,`rating`,`fechapublicacion`,`imageperfil`,`imageUri`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, Anuncio value) {
        stmt.bindLong(1, value.id);
        if (value.autor == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.autor);
        }
        if (value.tituloAnuncio == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.tituloAnuncio);
        }
        if (value.descripcion == null) {
          stmt.bindNull(4);
        } else {
          stmt.bindString(4, value.descripcion);
        }
        if (value.precio == null) {
          stmt.bindNull(5);
        } else {
          stmt.bindString(5, value.precio);
        }
        stmt.bindDouble(6, value.rating);
        if (value.fechapublicacion == null) {
          stmt.bindNull(7);
        } else {
          stmt.bindString(7, value.fechapublicacion);
        }
        if (value.imageperfil == null) {
          stmt.bindNull(8);
        } else {
          stmt.bindString(8, value.imageperfil);
        }
        if (value.imageUri == null) {
          stmt.bindNull(9);
        } else {
          stmt.bindString(9, value.imageUri);
        }
      }
    };
    this.__preparedStmtOfSetRating = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "UPDATE anuncio SET rating=? WHERE id=?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteAllAnuncios = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "DELETE FROM anuncio";
        return _query;
      }
    };
  }

  @Override
  public void insert(Anuncio anuncio) {
    __db.beginTransaction();
    try {
      __insertionAdapterOfAnuncio.insert(anuncio);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void setRating(int id, float rating) {
    final SupportSQLiteStatement _stmt = __preparedStmtOfSetRating.acquire();
    __db.beginTransaction();
    try {
      int _argIndex = 1;
      _stmt.bindDouble(_argIndex, rating);
      _argIndex = 2;
      _stmt.bindLong(_argIndex, id);
      _stmt.executeUpdateDelete();
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
      __preparedStmtOfSetRating.release(_stmt);
    }
  }

  @Override
  public void deleteAllAnuncios() {
    final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAllAnuncios.acquire();
    __db.beginTransaction();
    try {
      _stmt.executeUpdateDelete();
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
      __preparedStmtOfDeleteAllAnuncios.release(_stmt);
    }
  }

  @Override
  public LiveData<List<Anuncio>> getAllAnuncios() {
    final String _sql = "SELECT * FROM anuncio";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return new ComputableLiveData<List<Anuncio>>() {
      private Observer _observer;

      @Override
      protected List<Anuncio> compute() {
        if (_observer == null) {
          _observer = new Observer("anuncio") {
            @Override
            public void onInvalidated(@NonNull Set<String> tables) {
              invalidate();
            }
          };
          __db.getInvalidationTracker().addWeakObserver(_observer);
        }
        final Cursor _cursor = __db.query(_statement);
        try {
          final int _cursorIndexOfId = _cursor.getColumnIndexOrThrow("id");
          final int _cursorIndexOfAutor = _cursor.getColumnIndexOrThrow("autor");
          final int _cursorIndexOfTituloAnuncio = _cursor.getColumnIndexOrThrow("tituloAnuncio");
          final int _cursorIndexOfDescripcion = _cursor.getColumnIndexOrThrow("descripcion");
          final int _cursorIndexOfPrecio = _cursor.getColumnIndexOrThrow("precio");
          final int _cursorIndexOfRating = _cursor.getColumnIndexOrThrow("rating");
          final int _cursorIndexOfFechapublicacion = _cursor.getColumnIndexOrThrow("fechapublicacion");
          final int _cursorIndexOfImageperfil = _cursor.getColumnIndexOrThrow("imageperfil");
          final int _cursorIndexOfImageUri = _cursor.getColumnIndexOrThrow("imageUri");
          final List<Anuncio> _result = new ArrayList<Anuncio>(_cursor.getCount());
          while(_cursor.moveToNext()) {
            final Anuncio _item;
            _item = new Anuncio();
            _item.id = _cursor.getInt(_cursorIndexOfId);
            _item.autor = _cursor.getString(_cursorIndexOfAutor);
            _item.tituloAnuncio = _cursor.getString(_cursorIndexOfTituloAnuncio);
            _item.descripcion = _cursor.getString(_cursorIndexOfDescripcion);
            _item.precio = _cursor.getString(_cursorIndexOfPrecio);
            _item.rating = _cursor.getFloat(_cursorIndexOfRating);
            _item.fechapublicacion = _cursor.getString(_cursorIndexOfFechapublicacion);
            _item.imageperfil = _cursor.getString(_cursorIndexOfImageperfil);
            _item.imageUri = _cursor.getString(_cursorIndexOfImageUri);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    }.getLiveData();
  }

  @Override
  public LiveData<List<Anuncio>> getAllAnunciosOrderedByDate() {
    final String _sql = "SELECT * FROM anuncio ORDER BY fechapublicacion DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return new ComputableLiveData<List<Anuncio>>() {
      private Observer _observer;

      @Override
      protected List<Anuncio> compute() {
        if (_observer == null) {
          _observer = new Observer("anuncio") {
            @Override
            public void onInvalidated(@NonNull Set<String> tables) {
              invalidate();
            }
          };
          __db.getInvalidationTracker().addWeakObserver(_observer);
        }
        final Cursor _cursor = __db.query(_statement);
        try {
          final int _cursorIndexOfId = _cursor.getColumnIndexOrThrow("id");
          final int _cursorIndexOfAutor = _cursor.getColumnIndexOrThrow("autor");
          final int _cursorIndexOfTituloAnuncio = _cursor.getColumnIndexOrThrow("tituloAnuncio");
          final int _cursorIndexOfDescripcion = _cursor.getColumnIndexOrThrow("descripcion");
          final int _cursorIndexOfPrecio = _cursor.getColumnIndexOrThrow("precio");
          final int _cursorIndexOfRating = _cursor.getColumnIndexOrThrow("rating");
          final int _cursorIndexOfFechapublicacion = _cursor.getColumnIndexOrThrow("fechapublicacion");
          final int _cursorIndexOfImageperfil = _cursor.getColumnIndexOrThrow("imageperfil");
          final int _cursorIndexOfImageUri = _cursor.getColumnIndexOrThrow("imageUri");
          final List<Anuncio> _result = new ArrayList<Anuncio>(_cursor.getCount());
          while(_cursor.moveToNext()) {
            final Anuncio _item;
            _item = new Anuncio();
            _item.id = _cursor.getInt(_cursorIndexOfId);
            _item.autor = _cursor.getString(_cursorIndexOfAutor);
            _item.tituloAnuncio = _cursor.getString(_cursorIndexOfTituloAnuncio);
            _item.descripcion = _cursor.getString(_cursorIndexOfDescripcion);
            _item.precio = _cursor.getString(_cursorIndexOfPrecio);
            _item.rating = _cursor.getFloat(_cursorIndexOfRating);
            _item.fechapublicacion = _cursor.getString(_cursorIndexOfFechapublicacion);
            _item.imageperfil = _cursor.getString(_cursorIndexOfImageperfil);
            _item.imageUri = _cursor.getString(_cursorIndexOfImageUri);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    }.getLiveData();
  }

  @Override
  public LiveData<List<Anuncio>> getAllAnunciosOrderedByTitle() {
    final String _sql = "SELECT * FROM anuncio ORDER BY tituloAnuncio";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return new ComputableLiveData<List<Anuncio>>() {
      private Observer _observer;

      @Override
      protected List<Anuncio> compute() {
        if (_observer == null) {
          _observer = new Observer("anuncio") {
            @Override
            public void onInvalidated(@NonNull Set<String> tables) {
              invalidate();
            }
          };
          __db.getInvalidationTracker().addWeakObserver(_observer);
        }
        final Cursor _cursor = __db.query(_statement);
        try {
          final int _cursorIndexOfId = _cursor.getColumnIndexOrThrow("id");
          final int _cursorIndexOfAutor = _cursor.getColumnIndexOrThrow("autor");
          final int _cursorIndexOfTituloAnuncio = _cursor.getColumnIndexOrThrow("tituloAnuncio");
          final int _cursorIndexOfDescripcion = _cursor.getColumnIndexOrThrow("descripcion");
          final int _cursorIndexOfPrecio = _cursor.getColumnIndexOrThrow("precio");
          final int _cursorIndexOfRating = _cursor.getColumnIndexOrThrow("rating");
          final int _cursorIndexOfFechapublicacion = _cursor.getColumnIndexOrThrow("fechapublicacion");
          final int _cursorIndexOfImageperfil = _cursor.getColumnIndexOrThrow("imageperfil");
          final int _cursorIndexOfImageUri = _cursor.getColumnIndexOrThrow("imageUri");
          final List<Anuncio> _result = new ArrayList<Anuncio>(_cursor.getCount());
          while(_cursor.moveToNext()) {
            final Anuncio _item;
            _item = new Anuncio();
            _item.id = _cursor.getInt(_cursorIndexOfId);
            _item.autor = _cursor.getString(_cursorIndexOfAutor);
            _item.tituloAnuncio = _cursor.getString(_cursorIndexOfTituloAnuncio);
            _item.descripcion = _cursor.getString(_cursorIndexOfDescripcion);
            _item.precio = _cursor.getString(_cursorIndexOfPrecio);
            _item.rating = _cursor.getFloat(_cursorIndexOfRating);
            _item.fechapublicacion = _cursor.getString(_cursorIndexOfFechapublicacion);
            _item.imageperfil = _cursor.getString(_cursorIndexOfImageperfil);
            _item.imageUri = _cursor.getString(_cursorIndexOfImageUri);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    }.getLiveData();
  }

  @Override
  public LiveData<List<Anuncio>> getAllAnunciosOrderedByRating() {
    final String _sql = "SELECT * FROM anuncio ORDER BY autor DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return new ComputableLiveData<List<Anuncio>>() {
      private Observer _observer;

      @Override
      protected List<Anuncio> compute() {
        if (_observer == null) {
          _observer = new Observer("anuncio") {
            @Override
            public void onInvalidated(@NonNull Set<String> tables) {
              invalidate();
            }
          };
          __db.getInvalidationTracker().addWeakObserver(_observer);
        }
        final Cursor _cursor = __db.query(_statement);
        try {
          final int _cursorIndexOfId = _cursor.getColumnIndexOrThrow("id");
          final int _cursorIndexOfAutor = _cursor.getColumnIndexOrThrow("autor");
          final int _cursorIndexOfTituloAnuncio = _cursor.getColumnIndexOrThrow("tituloAnuncio");
          final int _cursorIndexOfDescripcion = _cursor.getColumnIndexOrThrow("descripcion");
          final int _cursorIndexOfPrecio = _cursor.getColumnIndexOrThrow("precio");
          final int _cursorIndexOfRating = _cursor.getColumnIndexOrThrow("rating");
          final int _cursorIndexOfFechapublicacion = _cursor.getColumnIndexOrThrow("fechapublicacion");
          final int _cursorIndexOfImageperfil = _cursor.getColumnIndexOrThrow("imageperfil");
          final int _cursorIndexOfImageUri = _cursor.getColumnIndexOrThrow("imageUri");
          final List<Anuncio> _result = new ArrayList<Anuncio>(_cursor.getCount());
          while(_cursor.moveToNext()) {
            final Anuncio _item;
            _item = new Anuncio();
            _item.id = _cursor.getInt(_cursorIndexOfId);
            _item.autor = _cursor.getString(_cursorIndexOfAutor);
            _item.tituloAnuncio = _cursor.getString(_cursorIndexOfTituloAnuncio);
            _item.descripcion = _cursor.getString(_cursorIndexOfDescripcion);
            _item.precio = _cursor.getString(_cursorIndexOfPrecio);
            _item.rating = _cursor.getFloat(_cursorIndexOfRating);
            _item.fechapublicacion = _cursor.getString(_cursorIndexOfFechapublicacion);
            _item.imageperfil = _cursor.getString(_cursorIndexOfImageperfil);
            _item.imageUri = _cursor.getString(_cursorIndexOfImageUri);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    }.getLiveData();
  }

  @Override
  public LiveData<Anuncio> getAnuncio(int id) {
    final String _sql = "SELECT * FROM anuncio WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    return new ComputableLiveData<Anuncio>() {
      private Observer _observer;

      @Override
      protected Anuncio compute() {
        if (_observer == null) {
          _observer = new Observer("anuncio") {
            @Override
            public void onInvalidated(@NonNull Set<String> tables) {
              invalidate();
            }
          };
          __db.getInvalidationTracker().addWeakObserver(_observer);
        }
        final Cursor _cursor = __db.query(_statement);
        try {
          final int _cursorIndexOfId = _cursor.getColumnIndexOrThrow("id");
          final int _cursorIndexOfAutor = _cursor.getColumnIndexOrThrow("autor");
          final int _cursorIndexOfTituloAnuncio = _cursor.getColumnIndexOrThrow("tituloAnuncio");
          final int _cursorIndexOfDescripcion = _cursor.getColumnIndexOrThrow("descripcion");
          final int _cursorIndexOfPrecio = _cursor.getColumnIndexOrThrow("precio");
          final int _cursorIndexOfRating = _cursor.getColumnIndexOrThrow("rating");
          final int _cursorIndexOfFechapublicacion = _cursor.getColumnIndexOrThrow("fechapublicacion");
          final int _cursorIndexOfImageperfil = _cursor.getColumnIndexOrThrow("imageperfil");
          final int _cursorIndexOfImageUri = _cursor.getColumnIndexOrThrow("imageUri");
          final Anuncio _result;
          if(_cursor.moveToFirst()) {
            _result = new Anuncio();
            _result.id = _cursor.getInt(_cursorIndexOfId);
            _result.autor = _cursor.getString(_cursorIndexOfAutor);
            _result.tituloAnuncio = _cursor.getString(_cursorIndexOfTituloAnuncio);
            _result.descripcion = _cursor.getString(_cursorIndexOfDescripcion);
            _result.precio = _cursor.getString(_cursorIndexOfPrecio);
            _result.rating = _cursor.getFloat(_cursorIndexOfRating);
            _result.fechapublicacion = _cursor.getString(_cursorIndexOfFechapublicacion);
            _result.imageperfil = _cursor.getString(_cursorIndexOfImageperfil);
            _result.imageUri = _cursor.getString(_cursorIndexOfImageUri);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    }.getLiveData();
  }
}
