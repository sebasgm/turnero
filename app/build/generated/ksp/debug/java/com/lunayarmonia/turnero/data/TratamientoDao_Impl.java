package com.lunayarmonia.turnero.data;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class TratamientoDao_Impl implements TratamientoDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Tratamiento> __insertionAdapterOfTratamiento;

  private final Converters __converters = new Converters();

  private final EntityDeletionOrUpdateAdapter<Tratamiento> __updateAdapterOfTratamiento;

  private final SharedSQLiteStatement __preparedStmtOfDesactivar;

  public TratamientoDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfTratamiento = new EntityInsertionAdapter<Tratamiento>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `tratamientos` (`id`,`nombre`,`categoria`,`precio`,`duracionMinutos`,`activo`) VALUES (nullif(?, 0),?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Tratamiento entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getNombre());
        final String _tmp = __converters.fromCategoria(entity.getCategoria());
        statement.bindString(3, _tmp);
        statement.bindDouble(4, entity.getPrecio());
        statement.bindLong(5, entity.getDuracionMinutos());
        final int _tmp_1 = entity.getActivo() ? 1 : 0;
        statement.bindLong(6, _tmp_1);
      }
    };
    this.__updateAdapterOfTratamiento = new EntityDeletionOrUpdateAdapter<Tratamiento>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `tratamientos` SET `id` = ?,`nombre` = ?,`categoria` = ?,`precio` = ?,`duracionMinutos` = ?,`activo` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Tratamiento entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getNombre());
        final String _tmp = __converters.fromCategoria(entity.getCategoria());
        statement.bindString(3, _tmp);
        statement.bindDouble(4, entity.getPrecio());
        statement.bindLong(5, entity.getDuracionMinutos());
        final int _tmp_1 = entity.getActivo() ? 1 : 0;
        statement.bindLong(6, _tmp_1);
        statement.bindLong(7, entity.getId());
      }
    };
    this.__preparedStmtOfDesactivar = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE tratamientos SET activo = 0 WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertarTodos(final List<Tratamiento> tratamientos,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfTratamiento.insert(tratamientos);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertar(final Tratamiento tratamiento,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfTratamiento.insertAndReturnId(tratamiento);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object actualizar(final Tratamiento tratamiento,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfTratamiento.handle(tratamiento);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object desactivar(final long id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDesactivar.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDesactivar.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<Tratamiento>> observarActivos() {
    final String _sql = "SELECT * FROM tratamientos WHERE activo = 1 ORDER BY categoria, nombre";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"tratamientos"}, new Callable<List<Tratamiento>>() {
      @Override
      @NonNull
      public List<Tratamiento> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfNombre = CursorUtil.getColumnIndexOrThrow(_cursor, "nombre");
          final int _cursorIndexOfCategoria = CursorUtil.getColumnIndexOrThrow(_cursor, "categoria");
          final int _cursorIndexOfPrecio = CursorUtil.getColumnIndexOrThrow(_cursor, "precio");
          final int _cursorIndexOfDuracionMinutos = CursorUtil.getColumnIndexOrThrow(_cursor, "duracionMinutos");
          final int _cursorIndexOfActivo = CursorUtil.getColumnIndexOrThrow(_cursor, "activo");
          final List<Tratamiento> _result = new ArrayList<Tratamiento>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Tratamiento _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpNombre;
            _tmpNombre = _cursor.getString(_cursorIndexOfNombre);
            final CategoriaTratamiento _tmpCategoria;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfCategoria);
            _tmpCategoria = __converters.toCategoria(_tmp);
            final double _tmpPrecio;
            _tmpPrecio = _cursor.getDouble(_cursorIndexOfPrecio);
            final int _tmpDuracionMinutos;
            _tmpDuracionMinutos = _cursor.getInt(_cursorIndexOfDuracionMinutos);
            final boolean _tmpActivo;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfActivo);
            _tmpActivo = _tmp_1 != 0;
            _item = new Tratamiento(_tmpId,_tmpNombre,_tmpCategoria,_tmpPrecio,_tmpDuracionMinutos,_tmpActivo);
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
    });
  }

  @Override
  public Object contar(final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM tratamientos";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
