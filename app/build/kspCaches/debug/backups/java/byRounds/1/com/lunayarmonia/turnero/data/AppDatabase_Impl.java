package com.lunayarmonia.turnero.data;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile PacienteDao _pacienteDao;

  private volatile TratamientoDao _tratamientoDao;

  private volatile TurnoDao _turnoDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `pacientes` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `nombre` TEXT NOT NULL, `telefono` TEXT NOT NULL, `email` TEXT, `notas` TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `tratamientos` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `nombre` TEXT NOT NULL, `categoria` TEXT NOT NULL, `precio` REAL NOT NULL, `duracionMinutos` INTEGER NOT NULL, `activo` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `turnos` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `pacienteId` INTEGER NOT NULL, `tratamientoId` INTEGER NOT NULL, `inicio` TEXT NOT NULL, `duracionMinutos` INTEGER NOT NULL, `nombreTratamientoSnapshot` TEXT NOT NULL, `precioSnapshot` REAL NOT NULL, `estado` TEXT NOT NULL, `notas` TEXT, `recordatorioEnviado` INTEGER NOT NULL, FOREIGN KEY(`pacienteId`) REFERENCES `pacientes`(`id`) ON UPDATE NO ACTION ON DELETE NO ACTION , FOREIGN KEY(`tratamientoId`) REFERENCES `tratamientos`(`id`) ON UPDATE NO ACTION ON DELETE NO ACTION )");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'aec011218b73250ea220410919b2e42b')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `pacientes`");
        db.execSQL("DROP TABLE IF EXISTS `tratamientos`");
        db.execSQL("DROP TABLE IF EXISTS `turnos`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        db.execSQL("PRAGMA foreign_keys = ON");
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsPacientes = new HashMap<String, TableInfo.Column>(5);
        _columnsPacientes.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPacientes.put("nombre", new TableInfo.Column("nombre", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPacientes.put("telefono", new TableInfo.Column("telefono", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPacientes.put("email", new TableInfo.Column("email", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPacientes.put("notas", new TableInfo.Column("notas", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysPacientes = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesPacientes = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoPacientes = new TableInfo("pacientes", _columnsPacientes, _foreignKeysPacientes, _indicesPacientes);
        final TableInfo _existingPacientes = TableInfo.read(db, "pacientes");
        if (!_infoPacientes.equals(_existingPacientes)) {
          return new RoomOpenHelper.ValidationResult(false, "pacientes(com.lunayarmonia.turnero.data.Paciente).\n"
                  + " Expected:\n" + _infoPacientes + "\n"
                  + " Found:\n" + _existingPacientes);
        }
        final HashMap<String, TableInfo.Column> _columnsTratamientos = new HashMap<String, TableInfo.Column>(6);
        _columnsTratamientos.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTratamientos.put("nombre", new TableInfo.Column("nombre", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTratamientos.put("categoria", new TableInfo.Column("categoria", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTratamientos.put("precio", new TableInfo.Column("precio", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTratamientos.put("duracionMinutos", new TableInfo.Column("duracionMinutos", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTratamientos.put("activo", new TableInfo.Column("activo", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysTratamientos = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesTratamientos = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoTratamientos = new TableInfo("tratamientos", _columnsTratamientos, _foreignKeysTratamientos, _indicesTratamientos);
        final TableInfo _existingTratamientos = TableInfo.read(db, "tratamientos");
        if (!_infoTratamientos.equals(_existingTratamientos)) {
          return new RoomOpenHelper.ValidationResult(false, "tratamientos(com.lunayarmonia.turnero.data.Tratamiento).\n"
                  + " Expected:\n" + _infoTratamientos + "\n"
                  + " Found:\n" + _existingTratamientos);
        }
        final HashMap<String, TableInfo.Column> _columnsTurnos = new HashMap<String, TableInfo.Column>(10);
        _columnsTurnos.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTurnos.put("pacienteId", new TableInfo.Column("pacienteId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTurnos.put("tratamientoId", new TableInfo.Column("tratamientoId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTurnos.put("inicio", new TableInfo.Column("inicio", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTurnos.put("duracionMinutos", new TableInfo.Column("duracionMinutos", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTurnos.put("nombreTratamientoSnapshot", new TableInfo.Column("nombreTratamientoSnapshot", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTurnos.put("precioSnapshot", new TableInfo.Column("precioSnapshot", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTurnos.put("estado", new TableInfo.Column("estado", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTurnos.put("notas", new TableInfo.Column("notas", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTurnos.put("recordatorioEnviado", new TableInfo.Column("recordatorioEnviado", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysTurnos = new HashSet<TableInfo.ForeignKey>(2);
        _foreignKeysTurnos.add(new TableInfo.ForeignKey("pacientes", "NO ACTION", "NO ACTION", Arrays.asList("pacienteId"), Arrays.asList("id")));
        _foreignKeysTurnos.add(new TableInfo.ForeignKey("tratamientos", "NO ACTION", "NO ACTION", Arrays.asList("tratamientoId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesTurnos = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoTurnos = new TableInfo("turnos", _columnsTurnos, _foreignKeysTurnos, _indicesTurnos);
        final TableInfo _existingTurnos = TableInfo.read(db, "turnos");
        if (!_infoTurnos.equals(_existingTurnos)) {
          return new RoomOpenHelper.ValidationResult(false, "turnos(com.lunayarmonia.turnero.data.Turno).\n"
                  + " Expected:\n" + _infoTurnos + "\n"
                  + " Found:\n" + _existingTurnos);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "aec011218b73250ea220410919b2e42b", "b66181c58dac69462dcf61b0466d6916");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "pacientes","tratamientos","turnos");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    final boolean _supportsDeferForeignKeys = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP;
    try {
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = FALSE");
      }
      super.beginTransaction();
      if (_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA defer_foreign_keys = TRUE");
      }
      _db.execSQL("DELETE FROM `turnos`");
      _db.execSQL("DELETE FROM `pacientes`");
      _db.execSQL("DELETE FROM `tratamientos`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = TRUE");
      }
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(PacienteDao.class, PacienteDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(TratamientoDao.class, TratamientoDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(TurnoDao.class, TurnoDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public PacienteDao pacienteDao() {
    if (_pacienteDao != null) {
      return _pacienteDao;
    } else {
      synchronized(this) {
        if(_pacienteDao == null) {
          _pacienteDao = new PacienteDao_Impl(this);
        }
        return _pacienteDao;
      }
    }
  }

  @Override
  public TratamientoDao tratamientoDao() {
    if (_tratamientoDao != null) {
      return _tratamientoDao;
    } else {
      synchronized(this) {
        if(_tratamientoDao == null) {
          _tratamientoDao = new TratamientoDao_Impl(this);
        }
        return _tratamientoDao;
      }
    }
  }

  @Override
  public TurnoDao turnoDao() {
    if (_turnoDao != null) {
      return _turnoDao;
    } else {
      synchronized(this) {
        if(_turnoDao == null) {
          _turnoDao = new TurnoDao_Impl(this);
        }
        return _turnoDao;
      }
    }
  }
}
