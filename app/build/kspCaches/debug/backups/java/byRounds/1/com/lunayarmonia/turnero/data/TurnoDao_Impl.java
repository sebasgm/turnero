package com.lunayarmonia.turnero.data;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
import java.lang.IllegalStateException;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.time.LocalDateTime;
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
public final class TurnoDao_Impl implements TurnoDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Turno> __insertionAdapterOfTurno;

  private final Converters __converters = new Converters();

  private final EntityDeletionOrUpdateAdapter<Turno> __deletionAdapterOfTurno;

  private final EntityDeletionOrUpdateAdapter<Turno> __updateAdapterOfTurno;

  public TurnoDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfTurno = new EntityInsertionAdapter<Turno>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `turnos` (`id`,`pacienteId`,`tratamientoId`,`inicio`,`duracionMinutos`,`nombreTratamientoSnapshot`,`precioSnapshot`,`estado`,`notas`,`recordatorioEnviado`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Turno entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getPacienteId());
        statement.bindLong(3, entity.getTratamientoId());
        final String _tmp = __converters.dateToTimestamp(entity.getInicio());
        if (_tmp == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, _tmp);
        }
        statement.bindLong(5, entity.getDuracionMinutos());
        statement.bindString(6, entity.getNombreTratamientoSnapshot());
        statement.bindDouble(7, entity.getPrecioSnapshot());
        final String _tmp_1 = __converters.fromEstadoTurno(entity.getEstado());
        statement.bindString(8, _tmp_1);
        if (entity.getNotas() == null) {
          statement.bindNull(9);
        } else {
          statement.bindString(9, entity.getNotas());
        }
        final int _tmp_2 = entity.getRecordatorioEnviado() ? 1 : 0;
        statement.bindLong(10, _tmp_2);
      }
    };
    this.__deletionAdapterOfTurno = new EntityDeletionOrUpdateAdapter<Turno>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `turnos` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Turno entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfTurno = new EntityDeletionOrUpdateAdapter<Turno>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `turnos` SET `id` = ?,`pacienteId` = ?,`tratamientoId` = ?,`inicio` = ?,`duracionMinutos` = ?,`nombreTratamientoSnapshot` = ?,`precioSnapshot` = ?,`estado` = ?,`notas` = ?,`recordatorioEnviado` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Turno entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getPacienteId());
        statement.bindLong(3, entity.getTratamientoId());
        final String _tmp = __converters.dateToTimestamp(entity.getInicio());
        if (_tmp == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, _tmp);
        }
        statement.bindLong(5, entity.getDuracionMinutos());
        statement.bindString(6, entity.getNombreTratamientoSnapshot());
        statement.bindDouble(7, entity.getPrecioSnapshot());
        final String _tmp_1 = __converters.fromEstadoTurno(entity.getEstado());
        statement.bindString(8, _tmp_1);
        if (entity.getNotas() == null) {
          statement.bindNull(9);
        } else {
          statement.bindString(9, entity.getNotas());
        }
        final int _tmp_2 = entity.getRecordatorioEnviado() ? 1 : 0;
        statement.bindLong(10, _tmp_2);
        statement.bindLong(11, entity.getId());
      }
    };
  }

  @Override
  public Object insertar(final Turno turno, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfTurno.insertAndReturnId(turno);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object borrar(final Turno turno, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfTurno.handle(turno);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object actualizar(final Turno turno, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfTurno.handle(turno);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<Turno>> observarPorRangoDeFecha(final LocalDateTime desde,
      final LocalDateTime hasta) {
    final String _sql = "SELECT * FROM turnos WHERE inicio >= ? AND inicio < ? ORDER BY inicio ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    final String _tmp = __converters.dateToTimestamp(desde);
    if (_tmp == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, _tmp);
    }
    _argIndex = 2;
    final String _tmp_1 = __converters.dateToTimestamp(hasta);
    if (_tmp_1 == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, _tmp_1);
    }
    return CoroutinesRoom.createFlow(__db, false, new String[] {"turnos"}, new Callable<List<Turno>>() {
      @Override
      @NonNull
      public List<Turno> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPacienteId = CursorUtil.getColumnIndexOrThrow(_cursor, "pacienteId");
          final int _cursorIndexOfTratamientoId = CursorUtil.getColumnIndexOrThrow(_cursor, "tratamientoId");
          final int _cursorIndexOfInicio = CursorUtil.getColumnIndexOrThrow(_cursor, "inicio");
          final int _cursorIndexOfDuracionMinutos = CursorUtil.getColumnIndexOrThrow(_cursor, "duracionMinutos");
          final int _cursorIndexOfNombreTratamientoSnapshot = CursorUtil.getColumnIndexOrThrow(_cursor, "nombreTratamientoSnapshot");
          final int _cursorIndexOfPrecioSnapshot = CursorUtil.getColumnIndexOrThrow(_cursor, "precioSnapshot");
          final int _cursorIndexOfEstado = CursorUtil.getColumnIndexOrThrow(_cursor, "estado");
          final int _cursorIndexOfNotas = CursorUtil.getColumnIndexOrThrow(_cursor, "notas");
          final int _cursorIndexOfRecordatorioEnviado = CursorUtil.getColumnIndexOrThrow(_cursor, "recordatorioEnviado");
          final List<Turno> _result = new ArrayList<Turno>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Turno _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpPacienteId;
            _tmpPacienteId = _cursor.getLong(_cursorIndexOfPacienteId);
            final long _tmpTratamientoId;
            _tmpTratamientoId = _cursor.getLong(_cursorIndexOfTratamientoId);
            final LocalDateTime _tmpInicio;
            final String _tmp_2;
            if (_cursor.isNull(_cursorIndexOfInicio)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _cursor.getString(_cursorIndexOfInicio);
            }
            final LocalDateTime _tmp_3 = __converters.fromTimestamp(_tmp_2);
            if (_tmp_3 == null) {
              throw new IllegalStateException("Expected NON-NULL 'java.time.LocalDateTime', but it was NULL.");
            } else {
              _tmpInicio = _tmp_3;
            }
            final int _tmpDuracionMinutos;
            _tmpDuracionMinutos = _cursor.getInt(_cursorIndexOfDuracionMinutos);
            final String _tmpNombreTratamientoSnapshot;
            _tmpNombreTratamientoSnapshot = _cursor.getString(_cursorIndexOfNombreTratamientoSnapshot);
            final double _tmpPrecioSnapshot;
            _tmpPrecioSnapshot = _cursor.getDouble(_cursorIndexOfPrecioSnapshot);
            final EstadoTurno _tmpEstado;
            final String _tmp_4;
            _tmp_4 = _cursor.getString(_cursorIndexOfEstado);
            _tmpEstado = __converters.toEstadoTurno(_tmp_4);
            final String _tmpNotas;
            if (_cursor.isNull(_cursorIndexOfNotas)) {
              _tmpNotas = null;
            } else {
              _tmpNotas = _cursor.getString(_cursorIndexOfNotas);
            }
            final boolean _tmpRecordatorioEnviado;
            final int _tmp_5;
            _tmp_5 = _cursor.getInt(_cursorIndexOfRecordatorioEnviado);
            _tmpRecordatorioEnviado = _tmp_5 != 0;
            _item = new Turno(_tmpId,_tmpPacienteId,_tmpTratamientoId,_tmpInicio,_tmpDuracionMinutos,_tmpNombreTratamientoSnapshot,_tmpPrecioSnapshot,_tmpEstado,_tmpNotas,_tmpRecordatorioEnviado);
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
  public Flow<List<Turno>> observarPorPaciente(final long pacienteId) {
    final String _sql = "SELECT * FROM turnos WHERE pacienteId = ? ORDER BY inicio DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, pacienteId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"turnos"}, new Callable<List<Turno>>() {
      @Override
      @NonNull
      public List<Turno> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPacienteId = CursorUtil.getColumnIndexOrThrow(_cursor, "pacienteId");
          final int _cursorIndexOfTratamientoId = CursorUtil.getColumnIndexOrThrow(_cursor, "tratamientoId");
          final int _cursorIndexOfInicio = CursorUtil.getColumnIndexOrThrow(_cursor, "inicio");
          final int _cursorIndexOfDuracionMinutos = CursorUtil.getColumnIndexOrThrow(_cursor, "duracionMinutos");
          final int _cursorIndexOfNombreTratamientoSnapshot = CursorUtil.getColumnIndexOrThrow(_cursor, "nombreTratamientoSnapshot");
          final int _cursorIndexOfPrecioSnapshot = CursorUtil.getColumnIndexOrThrow(_cursor, "precioSnapshot");
          final int _cursorIndexOfEstado = CursorUtil.getColumnIndexOrThrow(_cursor, "estado");
          final int _cursorIndexOfNotas = CursorUtil.getColumnIndexOrThrow(_cursor, "notas");
          final int _cursorIndexOfRecordatorioEnviado = CursorUtil.getColumnIndexOrThrow(_cursor, "recordatorioEnviado");
          final List<Turno> _result = new ArrayList<Turno>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Turno _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpPacienteId;
            _tmpPacienteId = _cursor.getLong(_cursorIndexOfPacienteId);
            final long _tmpTratamientoId;
            _tmpTratamientoId = _cursor.getLong(_cursorIndexOfTratamientoId);
            final LocalDateTime _tmpInicio;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfInicio)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfInicio);
            }
            final LocalDateTime _tmp_1 = __converters.fromTimestamp(_tmp);
            if (_tmp_1 == null) {
              throw new IllegalStateException("Expected NON-NULL 'java.time.LocalDateTime', but it was NULL.");
            } else {
              _tmpInicio = _tmp_1;
            }
            final int _tmpDuracionMinutos;
            _tmpDuracionMinutos = _cursor.getInt(_cursorIndexOfDuracionMinutos);
            final String _tmpNombreTratamientoSnapshot;
            _tmpNombreTratamientoSnapshot = _cursor.getString(_cursorIndexOfNombreTratamientoSnapshot);
            final double _tmpPrecioSnapshot;
            _tmpPrecioSnapshot = _cursor.getDouble(_cursorIndexOfPrecioSnapshot);
            final EstadoTurno _tmpEstado;
            final String _tmp_2;
            _tmp_2 = _cursor.getString(_cursorIndexOfEstado);
            _tmpEstado = __converters.toEstadoTurno(_tmp_2);
            final String _tmpNotas;
            if (_cursor.isNull(_cursorIndexOfNotas)) {
              _tmpNotas = null;
            } else {
              _tmpNotas = _cursor.getString(_cursorIndexOfNotas);
            }
            final boolean _tmpRecordatorioEnviado;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfRecordatorioEnviado);
            _tmpRecordatorioEnviado = _tmp_3 != 0;
            _item = new Turno(_tmpId,_tmpPacienteId,_tmpTratamientoId,_tmpInicio,_tmpDuracionMinutos,_tmpNombreTratamientoSnapshot,_tmpPrecioSnapshot,_tmpEstado,_tmpNotas,_tmpRecordatorioEnviado);
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
  public Object obtenerPorId(final long id, final Continuation<? super Turno> $completion) {
    final String _sql = "SELECT * FROM turnos WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Turno>() {
      @Override
      @Nullable
      public Turno call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPacienteId = CursorUtil.getColumnIndexOrThrow(_cursor, "pacienteId");
          final int _cursorIndexOfTratamientoId = CursorUtil.getColumnIndexOrThrow(_cursor, "tratamientoId");
          final int _cursorIndexOfInicio = CursorUtil.getColumnIndexOrThrow(_cursor, "inicio");
          final int _cursorIndexOfDuracionMinutos = CursorUtil.getColumnIndexOrThrow(_cursor, "duracionMinutos");
          final int _cursorIndexOfNombreTratamientoSnapshot = CursorUtil.getColumnIndexOrThrow(_cursor, "nombreTratamientoSnapshot");
          final int _cursorIndexOfPrecioSnapshot = CursorUtil.getColumnIndexOrThrow(_cursor, "precioSnapshot");
          final int _cursorIndexOfEstado = CursorUtil.getColumnIndexOrThrow(_cursor, "estado");
          final int _cursorIndexOfNotas = CursorUtil.getColumnIndexOrThrow(_cursor, "notas");
          final int _cursorIndexOfRecordatorioEnviado = CursorUtil.getColumnIndexOrThrow(_cursor, "recordatorioEnviado");
          final Turno _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpPacienteId;
            _tmpPacienteId = _cursor.getLong(_cursorIndexOfPacienteId);
            final long _tmpTratamientoId;
            _tmpTratamientoId = _cursor.getLong(_cursorIndexOfTratamientoId);
            final LocalDateTime _tmpInicio;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfInicio)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfInicio);
            }
            final LocalDateTime _tmp_1 = __converters.fromTimestamp(_tmp);
            if (_tmp_1 == null) {
              throw new IllegalStateException("Expected NON-NULL 'java.time.LocalDateTime', but it was NULL.");
            } else {
              _tmpInicio = _tmp_1;
            }
            final int _tmpDuracionMinutos;
            _tmpDuracionMinutos = _cursor.getInt(_cursorIndexOfDuracionMinutos);
            final String _tmpNombreTratamientoSnapshot;
            _tmpNombreTratamientoSnapshot = _cursor.getString(_cursorIndexOfNombreTratamientoSnapshot);
            final double _tmpPrecioSnapshot;
            _tmpPrecioSnapshot = _cursor.getDouble(_cursorIndexOfPrecioSnapshot);
            final EstadoTurno _tmpEstado;
            final String _tmp_2;
            _tmp_2 = _cursor.getString(_cursorIndexOfEstado);
            _tmpEstado = __converters.toEstadoTurno(_tmp_2);
            final String _tmpNotas;
            if (_cursor.isNull(_cursorIndexOfNotas)) {
              _tmpNotas = null;
            } else {
              _tmpNotas = _cursor.getString(_cursorIndexOfNotas);
            }
            final boolean _tmpRecordatorioEnviado;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfRecordatorioEnviado);
            _tmpRecordatorioEnviado = _tmp_3 != 0;
            _result = new Turno(_tmpId,_tmpPacienteId,_tmpTratamientoId,_tmpInicio,_tmpDuracionMinutos,_tmpNombreTratamientoSnapshot,_tmpPrecioSnapshot,_tmpEstado,_tmpNotas,_tmpRecordatorioEnviado);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object obtenerVencidosSinResolver(final LocalDateTime ahora,
      final Continuation<? super List<Turno>> $completion) {
    final String _sql = "SELECT * FROM turnos WHERE estado = 'AGENDADO' AND inicio < ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    final String _tmp = __converters.dateToTimestamp(ahora);
    if (_tmp == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, _tmp);
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<Turno>>() {
      @Override
      @NonNull
      public List<Turno> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPacienteId = CursorUtil.getColumnIndexOrThrow(_cursor, "pacienteId");
          final int _cursorIndexOfTratamientoId = CursorUtil.getColumnIndexOrThrow(_cursor, "tratamientoId");
          final int _cursorIndexOfInicio = CursorUtil.getColumnIndexOrThrow(_cursor, "inicio");
          final int _cursorIndexOfDuracionMinutos = CursorUtil.getColumnIndexOrThrow(_cursor, "duracionMinutos");
          final int _cursorIndexOfNombreTratamientoSnapshot = CursorUtil.getColumnIndexOrThrow(_cursor, "nombreTratamientoSnapshot");
          final int _cursorIndexOfPrecioSnapshot = CursorUtil.getColumnIndexOrThrow(_cursor, "precioSnapshot");
          final int _cursorIndexOfEstado = CursorUtil.getColumnIndexOrThrow(_cursor, "estado");
          final int _cursorIndexOfNotas = CursorUtil.getColumnIndexOrThrow(_cursor, "notas");
          final int _cursorIndexOfRecordatorioEnviado = CursorUtil.getColumnIndexOrThrow(_cursor, "recordatorioEnviado");
          final List<Turno> _result = new ArrayList<Turno>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Turno _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpPacienteId;
            _tmpPacienteId = _cursor.getLong(_cursorIndexOfPacienteId);
            final long _tmpTratamientoId;
            _tmpTratamientoId = _cursor.getLong(_cursorIndexOfTratamientoId);
            final LocalDateTime _tmpInicio;
            final String _tmp_1;
            if (_cursor.isNull(_cursorIndexOfInicio)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getString(_cursorIndexOfInicio);
            }
            final LocalDateTime _tmp_2 = __converters.fromTimestamp(_tmp_1);
            if (_tmp_2 == null) {
              throw new IllegalStateException("Expected NON-NULL 'java.time.LocalDateTime', but it was NULL.");
            } else {
              _tmpInicio = _tmp_2;
            }
            final int _tmpDuracionMinutos;
            _tmpDuracionMinutos = _cursor.getInt(_cursorIndexOfDuracionMinutos);
            final String _tmpNombreTratamientoSnapshot;
            _tmpNombreTratamientoSnapshot = _cursor.getString(_cursorIndexOfNombreTratamientoSnapshot);
            final double _tmpPrecioSnapshot;
            _tmpPrecioSnapshot = _cursor.getDouble(_cursorIndexOfPrecioSnapshot);
            final EstadoTurno _tmpEstado;
            final String _tmp_3;
            _tmp_3 = _cursor.getString(_cursorIndexOfEstado);
            _tmpEstado = __converters.toEstadoTurno(_tmp_3);
            final String _tmpNotas;
            if (_cursor.isNull(_cursorIndexOfNotas)) {
              _tmpNotas = null;
            } else {
              _tmpNotas = _cursor.getString(_cursorIndexOfNotas);
            }
            final boolean _tmpRecordatorioEnviado;
            final int _tmp_4;
            _tmp_4 = _cursor.getInt(_cursorIndexOfRecordatorioEnviado);
            _tmpRecordatorioEnviado = _tmp_4 != 0;
            _item = new Turno(_tmpId,_tmpPacienteId,_tmpTratamientoId,_tmpInicio,_tmpDuracionMinutos,_tmpNombreTratamientoSnapshot,_tmpPrecioSnapshot,_tmpEstado,_tmpNotas,_tmpRecordatorioEnviado);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object obtenerPagadosEnRango(final LocalDateTime desde, final LocalDateTime hasta,
      final Continuation<? super List<Turno>> $completion) {
    final String _sql = "SELECT * FROM turnos WHERE estado = 'PAGADO' AND inicio >= ? AND inicio < ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    final String _tmp = __converters.dateToTimestamp(desde);
    if (_tmp == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, _tmp);
    }
    _argIndex = 2;
    final String _tmp_1 = __converters.dateToTimestamp(hasta);
    if (_tmp_1 == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, _tmp_1);
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<Turno>>() {
      @Override
      @NonNull
      public List<Turno> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPacienteId = CursorUtil.getColumnIndexOrThrow(_cursor, "pacienteId");
          final int _cursorIndexOfTratamientoId = CursorUtil.getColumnIndexOrThrow(_cursor, "tratamientoId");
          final int _cursorIndexOfInicio = CursorUtil.getColumnIndexOrThrow(_cursor, "inicio");
          final int _cursorIndexOfDuracionMinutos = CursorUtil.getColumnIndexOrThrow(_cursor, "duracionMinutos");
          final int _cursorIndexOfNombreTratamientoSnapshot = CursorUtil.getColumnIndexOrThrow(_cursor, "nombreTratamientoSnapshot");
          final int _cursorIndexOfPrecioSnapshot = CursorUtil.getColumnIndexOrThrow(_cursor, "precioSnapshot");
          final int _cursorIndexOfEstado = CursorUtil.getColumnIndexOrThrow(_cursor, "estado");
          final int _cursorIndexOfNotas = CursorUtil.getColumnIndexOrThrow(_cursor, "notas");
          final int _cursorIndexOfRecordatorioEnviado = CursorUtil.getColumnIndexOrThrow(_cursor, "recordatorioEnviado");
          final List<Turno> _result = new ArrayList<Turno>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Turno _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpPacienteId;
            _tmpPacienteId = _cursor.getLong(_cursorIndexOfPacienteId);
            final long _tmpTratamientoId;
            _tmpTratamientoId = _cursor.getLong(_cursorIndexOfTratamientoId);
            final LocalDateTime _tmpInicio;
            final String _tmp_2;
            if (_cursor.isNull(_cursorIndexOfInicio)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _cursor.getString(_cursorIndexOfInicio);
            }
            final LocalDateTime _tmp_3 = __converters.fromTimestamp(_tmp_2);
            if (_tmp_3 == null) {
              throw new IllegalStateException("Expected NON-NULL 'java.time.LocalDateTime', but it was NULL.");
            } else {
              _tmpInicio = _tmp_3;
            }
            final int _tmpDuracionMinutos;
            _tmpDuracionMinutos = _cursor.getInt(_cursorIndexOfDuracionMinutos);
            final String _tmpNombreTratamientoSnapshot;
            _tmpNombreTratamientoSnapshot = _cursor.getString(_cursorIndexOfNombreTratamientoSnapshot);
            final double _tmpPrecioSnapshot;
            _tmpPrecioSnapshot = _cursor.getDouble(_cursorIndexOfPrecioSnapshot);
            final EstadoTurno _tmpEstado;
            final String _tmp_4;
            _tmp_4 = _cursor.getString(_cursorIndexOfEstado);
            _tmpEstado = __converters.toEstadoTurno(_tmp_4);
            final String _tmpNotas;
            if (_cursor.isNull(_cursorIndexOfNotas)) {
              _tmpNotas = null;
            } else {
              _tmpNotas = _cursor.getString(_cursorIndexOfNotas);
            }
            final boolean _tmpRecordatorioEnviado;
            final int _tmp_5;
            _tmp_5 = _cursor.getInt(_cursorIndexOfRecordatorioEnviado);
            _tmpRecordatorioEnviado = _tmp_5 != 0;
            _item = new Turno(_tmpId,_tmpPacienteId,_tmpTratamientoId,_tmpInicio,_tmpDuracionMinutos,_tmpNombreTratamientoSnapshot,_tmpPrecioSnapshot,_tmpEstado,_tmpNotas,_tmpRecordatorioEnviado);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object obtenerPendientesEnRango(final LocalDateTime desde, final LocalDateTime hasta,
      final Continuation<? super List<Turno>> $completion) {
    final String _sql = "SELECT * FROM turnos WHERE estado = 'PENDIENTE_DE_PAGO' AND inicio >= ? AND inicio < ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    final String _tmp = __converters.dateToTimestamp(desde);
    if (_tmp == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, _tmp);
    }
    _argIndex = 2;
    final String _tmp_1 = __converters.dateToTimestamp(hasta);
    if (_tmp_1 == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, _tmp_1);
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<Turno>>() {
      @Override
      @NonNull
      public List<Turno> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPacienteId = CursorUtil.getColumnIndexOrThrow(_cursor, "pacienteId");
          final int _cursorIndexOfTratamientoId = CursorUtil.getColumnIndexOrThrow(_cursor, "tratamientoId");
          final int _cursorIndexOfInicio = CursorUtil.getColumnIndexOrThrow(_cursor, "inicio");
          final int _cursorIndexOfDuracionMinutos = CursorUtil.getColumnIndexOrThrow(_cursor, "duracionMinutos");
          final int _cursorIndexOfNombreTratamientoSnapshot = CursorUtil.getColumnIndexOrThrow(_cursor, "nombreTratamientoSnapshot");
          final int _cursorIndexOfPrecioSnapshot = CursorUtil.getColumnIndexOrThrow(_cursor, "precioSnapshot");
          final int _cursorIndexOfEstado = CursorUtil.getColumnIndexOrThrow(_cursor, "estado");
          final int _cursorIndexOfNotas = CursorUtil.getColumnIndexOrThrow(_cursor, "notas");
          final int _cursorIndexOfRecordatorioEnviado = CursorUtil.getColumnIndexOrThrow(_cursor, "recordatorioEnviado");
          final List<Turno> _result = new ArrayList<Turno>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Turno _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpPacienteId;
            _tmpPacienteId = _cursor.getLong(_cursorIndexOfPacienteId);
            final long _tmpTratamientoId;
            _tmpTratamientoId = _cursor.getLong(_cursorIndexOfTratamientoId);
            final LocalDateTime _tmpInicio;
            final String _tmp_2;
            if (_cursor.isNull(_cursorIndexOfInicio)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _cursor.getString(_cursorIndexOfInicio);
            }
            final LocalDateTime _tmp_3 = __converters.fromTimestamp(_tmp_2);
            if (_tmp_3 == null) {
              throw new IllegalStateException("Expected NON-NULL 'java.time.LocalDateTime', but it was NULL.");
            } else {
              _tmpInicio = _tmp_3;
            }
            final int _tmpDuracionMinutos;
            _tmpDuracionMinutos = _cursor.getInt(_cursorIndexOfDuracionMinutos);
            final String _tmpNombreTratamientoSnapshot;
            _tmpNombreTratamientoSnapshot = _cursor.getString(_cursorIndexOfNombreTratamientoSnapshot);
            final double _tmpPrecioSnapshot;
            _tmpPrecioSnapshot = _cursor.getDouble(_cursorIndexOfPrecioSnapshot);
            final EstadoTurno _tmpEstado;
            final String _tmp_4;
            _tmp_4 = _cursor.getString(_cursorIndexOfEstado);
            _tmpEstado = __converters.toEstadoTurno(_tmp_4);
            final String _tmpNotas;
            if (_cursor.isNull(_cursorIndexOfNotas)) {
              _tmpNotas = null;
            } else {
              _tmpNotas = _cursor.getString(_cursorIndexOfNotas);
            }
            final boolean _tmpRecordatorioEnviado;
            final int _tmp_5;
            _tmp_5 = _cursor.getInt(_cursorIndexOfRecordatorioEnviado);
            _tmpRecordatorioEnviado = _tmp_5 != 0;
            _item = new Turno(_tmpId,_tmpPacienteId,_tmpTratamientoId,_tmpInicio,_tmpDuracionMinutos,_tmpNombreTratamientoSnapshot,_tmpPrecioSnapshot,_tmpEstado,_tmpNotas,_tmpRecordatorioEnviado);
            _result.add(_item);
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
