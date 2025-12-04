package com.mybackup.smbsync.data.local;

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
  private volatile SmbServerDao _smbServerDao;

  private volatile SyncConfigurationDao _syncConfigurationDao;

  private volatile SyncLogDao _syncLogDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(3) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `smb_servers` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `address` TEXT NOT NULL, `port` INTEGER NOT NULL, `protocol` TEXT NOT NULL, `username` TEXT NOT NULL, `encryptedPassword` TEXT NOT NULL, `domain` TEXT, `createdAt` INTEGER NOT NULL, `lastConnectedAt` INTEGER)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `sync_configurations` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `serverId` INTEGER NOT NULL, `localPath` TEXT NOT NULL, `remotePath` TEXT NOT NULL, `syncMode` TEXT NOT NULL, `enabled` INTEGER NOT NULL, `timeTolerance` INTEGER NOT NULL, `ignoreDst` INTEGER NOT NULL, `caseSensitive` INTEGER NOT NULL, `useChecksum` INTEGER NOT NULL, `minFileSize` INTEGER, `maxFileSize` INTEGER, `modifiedAfter` INTEGER, `modifiedBefore` INTEGER, `ignoreHiddenFiles` INTEGER NOT NULL, `ignoreZeroByteFiles` INTEGER NOT NULL, `fileExtensionFilter` TEXT, `archiveAgeDays` INTEGER NOT NULL, `archivePhotos` INTEGER NOT NULL, `archiveVideos` INTEGER NOT NULL, `maxFolderDepth` INTEGER, `bandwidthLimitKbps` INTEGER, `parallelTransfers` INTEGER NOT NULL, `scheduleType` TEXT NOT NULL, `intervalMinutes` INTEGER, `dailyTime` TEXT, `networkPreference` TEXT NOT NULL, `batteryRequirement` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, `lastSyncAt` INTEGER, FOREIGN KEY(`serverId`) REFERENCES `smb_servers`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_sync_configurations_serverId` ON `sync_configurations` (`serverId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `sync_logs` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `configId` INTEGER NOT NULL, `timestamp` INTEGER NOT NULL, `status` TEXT NOT NULL, `filesCopied` INTEGER NOT NULL, `filesDeleted` INTEGER NOT NULL, `filesSkipped` INTEGER NOT NULL, `filesFailed` INTEGER NOT NULL, `bytesTransferred` INTEGER NOT NULL, `durationMs` INTEGER NOT NULL, `errorMessage` TEXT, `detailedLog` TEXT, FOREIGN KEY(`configId`) REFERENCES `sync_configurations`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_sync_logs_configId` ON `sync_logs` (`configId`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_sync_logs_timestamp` ON `sync_logs` (`timestamp`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'cd320a028179e9a64948209e2df19cf4')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `smb_servers`");
        db.execSQL("DROP TABLE IF EXISTS `sync_configurations`");
        db.execSQL("DROP TABLE IF EXISTS `sync_logs`");
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
        final HashMap<String, TableInfo.Column> _columnsSmbServers = new HashMap<String, TableInfo.Column>(10);
        _columnsSmbServers.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSmbServers.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSmbServers.put("address", new TableInfo.Column("address", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSmbServers.put("port", new TableInfo.Column("port", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSmbServers.put("protocol", new TableInfo.Column("protocol", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSmbServers.put("username", new TableInfo.Column("username", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSmbServers.put("encryptedPassword", new TableInfo.Column("encryptedPassword", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSmbServers.put("domain", new TableInfo.Column("domain", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSmbServers.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSmbServers.put("lastConnectedAt", new TableInfo.Column("lastConnectedAt", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysSmbServers = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesSmbServers = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoSmbServers = new TableInfo("smb_servers", _columnsSmbServers, _foreignKeysSmbServers, _indicesSmbServers);
        final TableInfo _existingSmbServers = TableInfo.read(db, "smb_servers");
        if (!_infoSmbServers.equals(_existingSmbServers)) {
          return new RoomOpenHelper.ValidationResult(false, "smb_servers(com.mybackup.smbsync.data.model.SmbServer).\n"
                  + " Expected:\n" + _infoSmbServers + "\n"
                  + " Found:\n" + _existingSmbServers);
        }
        final HashMap<String, TableInfo.Column> _columnsSyncConfigurations = new HashMap<String, TableInfo.Column>(31);
        _columnsSyncConfigurations.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSyncConfigurations.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSyncConfigurations.put("serverId", new TableInfo.Column("serverId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSyncConfigurations.put("localPath", new TableInfo.Column("localPath", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSyncConfigurations.put("remotePath", new TableInfo.Column("remotePath", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSyncConfigurations.put("syncMode", new TableInfo.Column("syncMode", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSyncConfigurations.put("enabled", new TableInfo.Column("enabled", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSyncConfigurations.put("timeTolerance", new TableInfo.Column("timeTolerance", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSyncConfigurations.put("ignoreDst", new TableInfo.Column("ignoreDst", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSyncConfigurations.put("caseSensitive", new TableInfo.Column("caseSensitive", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSyncConfigurations.put("useChecksum", new TableInfo.Column("useChecksum", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSyncConfigurations.put("minFileSize", new TableInfo.Column("minFileSize", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSyncConfigurations.put("maxFileSize", new TableInfo.Column("maxFileSize", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSyncConfigurations.put("modifiedAfter", new TableInfo.Column("modifiedAfter", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSyncConfigurations.put("modifiedBefore", new TableInfo.Column("modifiedBefore", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSyncConfigurations.put("ignoreHiddenFiles", new TableInfo.Column("ignoreHiddenFiles", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSyncConfigurations.put("ignoreZeroByteFiles", new TableInfo.Column("ignoreZeroByteFiles", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSyncConfigurations.put("fileExtensionFilter", new TableInfo.Column("fileExtensionFilter", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSyncConfigurations.put("archiveAgeDays", new TableInfo.Column("archiveAgeDays", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSyncConfigurations.put("archivePhotos", new TableInfo.Column("archivePhotos", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSyncConfigurations.put("archiveVideos", new TableInfo.Column("archiveVideos", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSyncConfigurations.put("maxFolderDepth", new TableInfo.Column("maxFolderDepth", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSyncConfigurations.put("bandwidthLimitKbps", new TableInfo.Column("bandwidthLimitKbps", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSyncConfigurations.put("parallelTransfers", new TableInfo.Column("parallelTransfers", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSyncConfigurations.put("scheduleType", new TableInfo.Column("scheduleType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSyncConfigurations.put("intervalMinutes", new TableInfo.Column("intervalMinutes", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSyncConfigurations.put("dailyTime", new TableInfo.Column("dailyTime", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSyncConfigurations.put("networkPreference", new TableInfo.Column("networkPreference", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSyncConfigurations.put("batteryRequirement", new TableInfo.Column("batteryRequirement", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSyncConfigurations.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSyncConfigurations.put("lastSyncAt", new TableInfo.Column("lastSyncAt", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysSyncConfigurations = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysSyncConfigurations.add(new TableInfo.ForeignKey("smb_servers", "CASCADE", "NO ACTION", Arrays.asList("serverId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesSyncConfigurations = new HashSet<TableInfo.Index>(1);
        _indicesSyncConfigurations.add(new TableInfo.Index("index_sync_configurations_serverId", false, Arrays.asList("serverId"), Arrays.asList("ASC")));
        final TableInfo _infoSyncConfigurations = new TableInfo("sync_configurations", _columnsSyncConfigurations, _foreignKeysSyncConfigurations, _indicesSyncConfigurations);
        final TableInfo _existingSyncConfigurations = TableInfo.read(db, "sync_configurations");
        if (!_infoSyncConfigurations.equals(_existingSyncConfigurations)) {
          return new RoomOpenHelper.ValidationResult(false, "sync_configurations(com.mybackup.smbsync.data.model.SyncConfiguration).\n"
                  + " Expected:\n" + _infoSyncConfigurations + "\n"
                  + " Found:\n" + _existingSyncConfigurations);
        }
        final HashMap<String, TableInfo.Column> _columnsSyncLogs = new HashMap<String, TableInfo.Column>(12);
        _columnsSyncLogs.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSyncLogs.put("configId", new TableInfo.Column("configId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSyncLogs.put("timestamp", new TableInfo.Column("timestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSyncLogs.put("status", new TableInfo.Column("status", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSyncLogs.put("filesCopied", new TableInfo.Column("filesCopied", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSyncLogs.put("filesDeleted", new TableInfo.Column("filesDeleted", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSyncLogs.put("filesSkipped", new TableInfo.Column("filesSkipped", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSyncLogs.put("filesFailed", new TableInfo.Column("filesFailed", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSyncLogs.put("bytesTransferred", new TableInfo.Column("bytesTransferred", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSyncLogs.put("durationMs", new TableInfo.Column("durationMs", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSyncLogs.put("errorMessage", new TableInfo.Column("errorMessage", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSyncLogs.put("detailedLog", new TableInfo.Column("detailedLog", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysSyncLogs = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysSyncLogs.add(new TableInfo.ForeignKey("sync_configurations", "CASCADE", "NO ACTION", Arrays.asList("configId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesSyncLogs = new HashSet<TableInfo.Index>(2);
        _indicesSyncLogs.add(new TableInfo.Index("index_sync_logs_configId", false, Arrays.asList("configId"), Arrays.asList("ASC")));
        _indicesSyncLogs.add(new TableInfo.Index("index_sync_logs_timestamp", false, Arrays.asList("timestamp"), Arrays.asList("ASC")));
        final TableInfo _infoSyncLogs = new TableInfo("sync_logs", _columnsSyncLogs, _foreignKeysSyncLogs, _indicesSyncLogs);
        final TableInfo _existingSyncLogs = TableInfo.read(db, "sync_logs");
        if (!_infoSyncLogs.equals(_existingSyncLogs)) {
          return new RoomOpenHelper.ValidationResult(false, "sync_logs(com.mybackup.smbsync.data.model.SyncLog).\n"
                  + " Expected:\n" + _infoSyncLogs + "\n"
                  + " Found:\n" + _existingSyncLogs);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "cd320a028179e9a64948209e2df19cf4", "c458dd61b566048fa21a8f959843a7fc");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "smb_servers","sync_configurations","sync_logs");
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
      _db.execSQL("DELETE FROM `smb_servers`");
      _db.execSQL("DELETE FROM `sync_configurations`");
      _db.execSQL("DELETE FROM `sync_logs`");
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
    _typeConvertersMap.put(SmbServerDao.class, SmbServerDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(SyncConfigurationDao.class, SyncConfigurationDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(SyncLogDao.class, SyncLogDao_Impl.getRequiredConverters());
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
  public SmbServerDao smbServerDao() {
    if (_smbServerDao != null) {
      return _smbServerDao;
    } else {
      synchronized(this) {
        if(_smbServerDao == null) {
          _smbServerDao = new SmbServerDao_Impl(this);
        }
        return _smbServerDao;
      }
    }
  }

  @Override
  public SyncConfigurationDao syncConfigurationDao() {
    if (_syncConfigurationDao != null) {
      return _syncConfigurationDao;
    } else {
      synchronized(this) {
        if(_syncConfigurationDao == null) {
          _syncConfigurationDao = new SyncConfigurationDao_Impl(this);
        }
        return _syncConfigurationDao;
      }
    }
  }

  @Override
  public SyncLogDao syncLogDao() {
    if (_syncLogDao != null) {
      return _syncLogDao;
    } else {
      synchronized(this) {
        if(_syncLogDao == null) {
          _syncLogDao = new SyncLogDao_Impl(this);
        }
        return _syncLogDao;
      }
    }
  }
}
