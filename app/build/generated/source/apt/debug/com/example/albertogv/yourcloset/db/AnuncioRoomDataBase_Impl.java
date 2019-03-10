package com.example.albertogv.yourcloset.db;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.db.SupportSQLiteOpenHelper;
import android.arch.persistence.db.SupportSQLiteOpenHelper.Callback;
import android.arch.persistence.db.SupportSQLiteOpenHelper.Configuration;
import android.arch.persistence.room.DatabaseConfiguration;
import android.arch.persistence.room.InvalidationTracker;
import android.arch.persistence.room.RoomOpenHelper;
import android.arch.persistence.room.RoomOpenHelper.Delegate;
import android.arch.persistence.room.util.TableInfo;
import android.arch.persistence.room.util.TableInfo.Column;
import android.arch.persistence.room.util.TableInfo.ForeignKey;
import android.arch.persistence.room.util.TableInfo.Index;
import java.lang.IllegalStateException;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.HashMap;
import java.util.HashSet;

@SuppressWarnings("unchecked")
public class AnuncioRoomDataBase_Impl extends AnuncioRoomDataBase {
  private volatile AnuncioDAO _anuncioDAO;

  @Override
  protected SupportSQLiteOpenHelper createOpenHelper(DatabaseConfiguration configuration) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(configuration, new RoomOpenHelper.Delegate(2) {
      @Override
      public void createAllTables(SupportSQLiteDatabase _db) {
        _db.execSQL("CREATE TABLE IF NOT EXISTS `Anuncio` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `autor` TEXT, `tituloAnuncio` TEXT, `descripcion` TEXT, `precio` TEXT, `rating` REAL NOT NULL, `fechapublicacion` TEXT, `imageperfil` TEXT, `imageUri` TEXT)");
        _db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        _db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, \"2cc290da902ce9b5bddafde0bd5dbaed\")");
      }

      @Override
      public void dropAllTables(SupportSQLiteDatabase _db) {
        _db.execSQL("DROP TABLE IF EXISTS `Anuncio`");
      }

      @Override
      protected void onCreate(SupportSQLiteDatabase _db) {
        if (mCallbacks != null) {
          for (int _i = 0, _size = mCallbacks.size(); _i < _size; _i++) {
            mCallbacks.get(_i).onCreate(_db);
          }
        }
      }

      @Override
      public void onOpen(SupportSQLiteDatabase _db) {
        mDatabase = _db;
        internalInitInvalidationTracker(_db);
        if (mCallbacks != null) {
          for (int _i = 0, _size = mCallbacks.size(); _i < _size; _i++) {
            mCallbacks.get(_i).onOpen(_db);
          }
        }
      }

      @Override
      protected void validateMigration(SupportSQLiteDatabase _db) {
        final HashMap<String, TableInfo.Column> _columnsAnuncio = new HashMap<String, TableInfo.Column>(9);
        _columnsAnuncio.put("id", new TableInfo.Column("id", "INTEGER", true, 1));
        _columnsAnuncio.put("autor", new TableInfo.Column("autor", "TEXT", false, 0));
        _columnsAnuncio.put("tituloAnuncio", new TableInfo.Column("tituloAnuncio", "TEXT", false, 0));
        _columnsAnuncio.put("descripcion", new TableInfo.Column("descripcion", "TEXT", false, 0));
        _columnsAnuncio.put("precio", new TableInfo.Column("precio", "TEXT", false, 0));
        _columnsAnuncio.put("rating", new TableInfo.Column("rating", "REAL", true, 0));
        _columnsAnuncio.put("fechapublicacion", new TableInfo.Column("fechapublicacion", "TEXT", false, 0));
        _columnsAnuncio.put("imageperfil", new TableInfo.Column("imageperfil", "TEXT", false, 0));
        _columnsAnuncio.put("imageUri", new TableInfo.Column("imageUri", "TEXT", false, 0));
        final HashSet<TableInfo.ForeignKey> _foreignKeysAnuncio = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesAnuncio = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoAnuncio = new TableInfo("Anuncio", _columnsAnuncio, _foreignKeysAnuncio, _indicesAnuncio);
        final TableInfo _existingAnuncio = TableInfo.read(_db, "Anuncio");
        if (! _infoAnuncio.equals(_existingAnuncio)) {
          throw new IllegalStateException("Migration didn't properly handle Anuncio(com.example.albertogv.yourcloset.model.Anuncio).\n"
                  + " Expected:\n" + _infoAnuncio + "\n"
                  + " Found:\n" + _existingAnuncio);
        }
      }
    }, "2cc290da902ce9b5bddafde0bd5dbaed", "7ca9db6636c2c6d03c8ca2b3a68192f1");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(configuration.context)
        .name(configuration.name)
        .callback(_openCallback)
        .build();
    final SupportSQLiteOpenHelper _helper = configuration.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  protected InvalidationTracker createInvalidationTracker() {
    return new InvalidationTracker(this, "Anuncio");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `Anuncio`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  public AnuncioDAO anuncioDAO() {
    if (_anuncioDAO != null) {
      return _anuncioDAO;
    } else {
      synchronized(this) {
        if(_anuncioDAO == null) {
          _anuncioDAO = new AnuncioDAO_Impl(this);
        }
        return _anuncioDAO;
      }
    }
  }
}
