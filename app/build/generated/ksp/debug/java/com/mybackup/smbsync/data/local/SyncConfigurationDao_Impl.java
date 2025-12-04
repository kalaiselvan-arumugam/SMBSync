package com.mybackup.smbsync.data.local;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.mybackup.smbsync.data.model.BatteryRequirement;
import com.mybackup.smbsync.data.model.NetworkPreference;
import com.mybackup.smbsync.data.model.ScheduleType;
import com.mybackup.smbsync.data.model.SyncConfiguration;
import com.mybackup.smbsync.data.model.SyncMode;
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
public final class SyncConfigurationDao_Impl implements SyncConfigurationDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<SyncConfiguration> __insertionAdapterOfSyncConfiguration;

  private final Converters __converters = new Converters();

  private final EntityDeletionOrUpdateAdapter<SyncConfiguration> __deletionAdapterOfSyncConfiguration;

  private final EntityDeletionOrUpdateAdapter<SyncConfiguration> __updateAdapterOfSyncConfiguration;

  private final SharedSQLiteStatement __preparedStmtOfSetEnabled;

  private final SharedSQLiteStatement __preparedStmtOfUpdateLastSync;

  public SyncConfigurationDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfSyncConfiguration = new EntityInsertionAdapter<SyncConfiguration>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `sync_configurations` (`id`,`name`,`serverId`,`localPath`,`remotePath`,`syncMode`,`enabled`,`timeTolerance`,`ignoreDst`,`caseSensitive`,`useChecksum`,`minFileSize`,`maxFileSize`,`modifiedAfter`,`modifiedBefore`,`ignoreHiddenFiles`,`ignoreZeroByteFiles`,`fileExtensionFilter`,`archiveAgeDays`,`archivePhotos`,`archiveVideos`,`maxFolderDepth`,`bandwidthLimitKbps`,`parallelTransfers`,`scheduleType`,`intervalMinutes`,`dailyTime`,`networkPreference`,`batteryRequirement`,`createdAt`,`lastSyncAt`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final SyncConfiguration entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindLong(3, entity.getServerId());
        statement.bindString(4, entity.getLocalPath());
        statement.bindString(5, entity.getRemotePath());
        final String _tmp = __converters.fromSyncMode(entity.getSyncMode());
        statement.bindString(6, _tmp);
        final int _tmp_1 = entity.getEnabled() ? 1 : 0;
        statement.bindLong(7, _tmp_1);
        statement.bindLong(8, entity.getTimeTolerance());
        final int _tmp_2 = entity.getIgnoreDst() ? 1 : 0;
        statement.bindLong(9, _tmp_2);
        final int _tmp_3 = entity.getCaseSensitive() ? 1 : 0;
        statement.bindLong(10, _tmp_3);
        final int _tmp_4 = entity.getUseChecksum() ? 1 : 0;
        statement.bindLong(11, _tmp_4);
        if (entity.getMinFileSize() == null) {
          statement.bindNull(12);
        } else {
          statement.bindLong(12, entity.getMinFileSize());
        }
        if (entity.getMaxFileSize() == null) {
          statement.bindNull(13);
        } else {
          statement.bindLong(13, entity.getMaxFileSize());
        }
        if (entity.getModifiedAfter() == null) {
          statement.bindNull(14);
        } else {
          statement.bindLong(14, entity.getModifiedAfter());
        }
        if (entity.getModifiedBefore() == null) {
          statement.bindNull(15);
        } else {
          statement.bindLong(15, entity.getModifiedBefore());
        }
        final int _tmp_5 = entity.getIgnoreHiddenFiles() ? 1 : 0;
        statement.bindLong(16, _tmp_5);
        final int _tmp_6 = entity.getIgnoreZeroByteFiles() ? 1 : 0;
        statement.bindLong(17, _tmp_6);
        if (entity.getFileExtensionFilter() == null) {
          statement.bindNull(18);
        } else {
          statement.bindString(18, entity.getFileExtensionFilter());
        }
        statement.bindLong(19, entity.getArchiveAgeDays());
        final int _tmp_7 = entity.getArchivePhotos() ? 1 : 0;
        statement.bindLong(20, _tmp_7);
        final int _tmp_8 = entity.getArchiveVideos() ? 1 : 0;
        statement.bindLong(21, _tmp_8);
        if (entity.getMaxFolderDepth() == null) {
          statement.bindNull(22);
        } else {
          statement.bindLong(22, entity.getMaxFolderDepth());
        }
        if (entity.getBandwidthLimitKbps() == null) {
          statement.bindNull(23);
        } else {
          statement.bindLong(23, entity.getBandwidthLimitKbps());
        }
        statement.bindLong(24, entity.getParallelTransfers());
        final String _tmp_9 = __converters.fromScheduleType(entity.getScheduleType());
        statement.bindString(25, _tmp_9);
        if (entity.getIntervalMinutes() == null) {
          statement.bindNull(26);
        } else {
          statement.bindLong(26, entity.getIntervalMinutes());
        }
        if (entity.getDailyTime() == null) {
          statement.bindNull(27);
        } else {
          statement.bindString(27, entity.getDailyTime());
        }
        final String _tmp_10 = __converters.fromNetworkPreference(entity.getNetworkPreference());
        statement.bindString(28, _tmp_10);
        final String _tmp_11 = __converters.fromBatteryRequirement(entity.getBatteryRequirement());
        statement.bindString(29, _tmp_11);
        statement.bindLong(30, entity.getCreatedAt());
        if (entity.getLastSyncAt() == null) {
          statement.bindNull(31);
        } else {
          statement.bindLong(31, entity.getLastSyncAt());
        }
      }
    };
    this.__deletionAdapterOfSyncConfiguration = new EntityDeletionOrUpdateAdapter<SyncConfiguration>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `sync_configurations` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final SyncConfiguration entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfSyncConfiguration = new EntityDeletionOrUpdateAdapter<SyncConfiguration>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `sync_configurations` SET `id` = ?,`name` = ?,`serverId` = ?,`localPath` = ?,`remotePath` = ?,`syncMode` = ?,`enabled` = ?,`timeTolerance` = ?,`ignoreDst` = ?,`caseSensitive` = ?,`useChecksum` = ?,`minFileSize` = ?,`maxFileSize` = ?,`modifiedAfter` = ?,`modifiedBefore` = ?,`ignoreHiddenFiles` = ?,`ignoreZeroByteFiles` = ?,`fileExtensionFilter` = ?,`archiveAgeDays` = ?,`archivePhotos` = ?,`archiveVideos` = ?,`maxFolderDepth` = ?,`bandwidthLimitKbps` = ?,`parallelTransfers` = ?,`scheduleType` = ?,`intervalMinutes` = ?,`dailyTime` = ?,`networkPreference` = ?,`batteryRequirement` = ?,`createdAt` = ?,`lastSyncAt` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final SyncConfiguration entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindLong(3, entity.getServerId());
        statement.bindString(4, entity.getLocalPath());
        statement.bindString(5, entity.getRemotePath());
        final String _tmp = __converters.fromSyncMode(entity.getSyncMode());
        statement.bindString(6, _tmp);
        final int _tmp_1 = entity.getEnabled() ? 1 : 0;
        statement.bindLong(7, _tmp_1);
        statement.bindLong(8, entity.getTimeTolerance());
        final int _tmp_2 = entity.getIgnoreDst() ? 1 : 0;
        statement.bindLong(9, _tmp_2);
        final int _tmp_3 = entity.getCaseSensitive() ? 1 : 0;
        statement.bindLong(10, _tmp_3);
        final int _tmp_4 = entity.getUseChecksum() ? 1 : 0;
        statement.bindLong(11, _tmp_4);
        if (entity.getMinFileSize() == null) {
          statement.bindNull(12);
        } else {
          statement.bindLong(12, entity.getMinFileSize());
        }
        if (entity.getMaxFileSize() == null) {
          statement.bindNull(13);
        } else {
          statement.bindLong(13, entity.getMaxFileSize());
        }
        if (entity.getModifiedAfter() == null) {
          statement.bindNull(14);
        } else {
          statement.bindLong(14, entity.getModifiedAfter());
        }
        if (entity.getModifiedBefore() == null) {
          statement.bindNull(15);
        } else {
          statement.bindLong(15, entity.getModifiedBefore());
        }
        final int _tmp_5 = entity.getIgnoreHiddenFiles() ? 1 : 0;
        statement.bindLong(16, _tmp_5);
        final int _tmp_6 = entity.getIgnoreZeroByteFiles() ? 1 : 0;
        statement.bindLong(17, _tmp_6);
        if (entity.getFileExtensionFilter() == null) {
          statement.bindNull(18);
        } else {
          statement.bindString(18, entity.getFileExtensionFilter());
        }
        statement.bindLong(19, entity.getArchiveAgeDays());
        final int _tmp_7 = entity.getArchivePhotos() ? 1 : 0;
        statement.bindLong(20, _tmp_7);
        final int _tmp_8 = entity.getArchiveVideos() ? 1 : 0;
        statement.bindLong(21, _tmp_8);
        if (entity.getMaxFolderDepth() == null) {
          statement.bindNull(22);
        } else {
          statement.bindLong(22, entity.getMaxFolderDepth());
        }
        if (entity.getBandwidthLimitKbps() == null) {
          statement.bindNull(23);
        } else {
          statement.bindLong(23, entity.getBandwidthLimitKbps());
        }
        statement.bindLong(24, entity.getParallelTransfers());
        final String _tmp_9 = __converters.fromScheduleType(entity.getScheduleType());
        statement.bindString(25, _tmp_9);
        if (entity.getIntervalMinutes() == null) {
          statement.bindNull(26);
        } else {
          statement.bindLong(26, entity.getIntervalMinutes());
        }
        if (entity.getDailyTime() == null) {
          statement.bindNull(27);
        } else {
          statement.bindString(27, entity.getDailyTime());
        }
        final String _tmp_10 = __converters.fromNetworkPreference(entity.getNetworkPreference());
        statement.bindString(28, _tmp_10);
        final String _tmp_11 = __converters.fromBatteryRequirement(entity.getBatteryRequirement());
        statement.bindString(29, _tmp_11);
        statement.bindLong(30, entity.getCreatedAt());
        if (entity.getLastSyncAt() == null) {
          statement.bindNull(31);
        } else {
          statement.bindLong(31, entity.getLastSyncAt());
        }
        statement.bindLong(32, entity.getId());
      }
    };
    this.__preparedStmtOfSetEnabled = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE sync_configurations SET enabled = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateLastSync = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE sync_configurations SET lastSyncAt = ? WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertConfiguration(final SyncConfiguration config,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfSyncConfiguration.insertAndReturnId(config);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteConfiguration(final SyncConfiguration config,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfSyncConfiguration.handle(config);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateConfiguration(final SyncConfiguration config,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfSyncConfiguration.handle(config);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object setEnabled(final long id, final boolean enabled,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfSetEnabled.acquire();
        int _argIndex = 1;
        final int _tmp = enabled ? 1 : 0;
        _stmt.bindLong(_argIndex, _tmp);
        _argIndex = 2;
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
          __preparedStmtOfSetEnabled.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updateLastSync(final long id, final long timestamp,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateLastSync.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, timestamp);
        _argIndex = 2;
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
          __preparedStmtOfUpdateLastSync.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<SyncConfiguration>> getAllConfigurations() {
    final String _sql = "SELECT * FROM sync_configurations ORDER BY name ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"sync_configurations"}, new Callable<List<SyncConfiguration>>() {
      @Override
      @NonNull
      public List<SyncConfiguration> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfServerId = CursorUtil.getColumnIndexOrThrow(_cursor, "serverId");
          final int _cursorIndexOfLocalPath = CursorUtil.getColumnIndexOrThrow(_cursor, "localPath");
          final int _cursorIndexOfRemotePath = CursorUtil.getColumnIndexOrThrow(_cursor, "remotePath");
          final int _cursorIndexOfSyncMode = CursorUtil.getColumnIndexOrThrow(_cursor, "syncMode");
          final int _cursorIndexOfEnabled = CursorUtil.getColumnIndexOrThrow(_cursor, "enabled");
          final int _cursorIndexOfTimeTolerance = CursorUtil.getColumnIndexOrThrow(_cursor, "timeTolerance");
          final int _cursorIndexOfIgnoreDst = CursorUtil.getColumnIndexOrThrow(_cursor, "ignoreDst");
          final int _cursorIndexOfCaseSensitive = CursorUtil.getColumnIndexOrThrow(_cursor, "caseSensitive");
          final int _cursorIndexOfUseChecksum = CursorUtil.getColumnIndexOrThrow(_cursor, "useChecksum");
          final int _cursorIndexOfMinFileSize = CursorUtil.getColumnIndexOrThrow(_cursor, "minFileSize");
          final int _cursorIndexOfMaxFileSize = CursorUtil.getColumnIndexOrThrow(_cursor, "maxFileSize");
          final int _cursorIndexOfModifiedAfter = CursorUtil.getColumnIndexOrThrow(_cursor, "modifiedAfter");
          final int _cursorIndexOfModifiedBefore = CursorUtil.getColumnIndexOrThrow(_cursor, "modifiedBefore");
          final int _cursorIndexOfIgnoreHiddenFiles = CursorUtil.getColumnIndexOrThrow(_cursor, "ignoreHiddenFiles");
          final int _cursorIndexOfIgnoreZeroByteFiles = CursorUtil.getColumnIndexOrThrow(_cursor, "ignoreZeroByteFiles");
          final int _cursorIndexOfFileExtensionFilter = CursorUtil.getColumnIndexOrThrow(_cursor, "fileExtensionFilter");
          final int _cursorIndexOfArchiveAgeDays = CursorUtil.getColumnIndexOrThrow(_cursor, "archiveAgeDays");
          final int _cursorIndexOfArchivePhotos = CursorUtil.getColumnIndexOrThrow(_cursor, "archivePhotos");
          final int _cursorIndexOfArchiveVideos = CursorUtil.getColumnIndexOrThrow(_cursor, "archiveVideos");
          final int _cursorIndexOfMaxFolderDepth = CursorUtil.getColumnIndexOrThrow(_cursor, "maxFolderDepth");
          final int _cursorIndexOfBandwidthLimitKbps = CursorUtil.getColumnIndexOrThrow(_cursor, "bandwidthLimitKbps");
          final int _cursorIndexOfParallelTransfers = CursorUtil.getColumnIndexOrThrow(_cursor, "parallelTransfers");
          final int _cursorIndexOfScheduleType = CursorUtil.getColumnIndexOrThrow(_cursor, "scheduleType");
          final int _cursorIndexOfIntervalMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "intervalMinutes");
          final int _cursorIndexOfDailyTime = CursorUtil.getColumnIndexOrThrow(_cursor, "dailyTime");
          final int _cursorIndexOfNetworkPreference = CursorUtil.getColumnIndexOrThrow(_cursor, "networkPreference");
          final int _cursorIndexOfBatteryRequirement = CursorUtil.getColumnIndexOrThrow(_cursor, "batteryRequirement");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfLastSyncAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSyncAt");
          final List<SyncConfiguration> _result = new ArrayList<SyncConfiguration>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final SyncConfiguration _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final long _tmpServerId;
            _tmpServerId = _cursor.getLong(_cursorIndexOfServerId);
            final String _tmpLocalPath;
            _tmpLocalPath = _cursor.getString(_cursorIndexOfLocalPath);
            final String _tmpRemotePath;
            _tmpRemotePath = _cursor.getString(_cursorIndexOfRemotePath);
            final SyncMode _tmpSyncMode;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfSyncMode);
            _tmpSyncMode = __converters.toSyncMode(_tmp);
            final boolean _tmpEnabled;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfEnabled);
            _tmpEnabled = _tmp_1 != 0;
            final int _tmpTimeTolerance;
            _tmpTimeTolerance = _cursor.getInt(_cursorIndexOfTimeTolerance);
            final boolean _tmpIgnoreDst;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIgnoreDst);
            _tmpIgnoreDst = _tmp_2 != 0;
            final boolean _tmpCaseSensitive;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfCaseSensitive);
            _tmpCaseSensitive = _tmp_3 != 0;
            final boolean _tmpUseChecksum;
            final int _tmp_4;
            _tmp_4 = _cursor.getInt(_cursorIndexOfUseChecksum);
            _tmpUseChecksum = _tmp_4 != 0;
            final Long _tmpMinFileSize;
            if (_cursor.isNull(_cursorIndexOfMinFileSize)) {
              _tmpMinFileSize = null;
            } else {
              _tmpMinFileSize = _cursor.getLong(_cursorIndexOfMinFileSize);
            }
            final Long _tmpMaxFileSize;
            if (_cursor.isNull(_cursorIndexOfMaxFileSize)) {
              _tmpMaxFileSize = null;
            } else {
              _tmpMaxFileSize = _cursor.getLong(_cursorIndexOfMaxFileSize);
            }
            final Long _tmpModifiedAfter;
            if (_cursor.isNull(_cursorIndexOfModifiedAfter)) {
              _tmpModifiedAfter = null;
            } else {
              _tmpModifiedAfter = _cursor.getLong(_cursorIndexOfModifiedAfter);
            }
            final Long _tmpModifiedBefore;
            if (_cursor.isNull(_cursorIndexOfModifiedBefore)) {
              _tmpModifiedBefore = null;
            } else {
              _tmpModifiedBefore = _cursor.getLong(_cursorIndexOfModifiedBefore);
            }
            final boolean _tmpIgnoreHiddenFiles;
            final int _tmp_5;
            _tmp_5 = _cursor.getInt(_cursorIndexOfIgnoreHiddenFiles);
            _tmpIgnoreHiddenFiles = _tmp_5 != 0;
            final boolean _tmpIgnoreZeroByteFiles;
            final int _tmp_6;
            _tmp_6 = _cursor.getInt(_cursorIndexOfIgnoreZeroByteFiles);
            _tmpIgnoreZeroByteFiles = _tmp_6 != 0;
            final String _tmpFileExtensionFilter;
            if (_cursor.isNull(_cursorIndexOfFileExtensionFilter)) {
              _tmpFileExtensionFilter = null;
            } else {
              _tmpFileExtensionFilter = _cursor.getString(_cursorIndexOfFileExtensionFilter);
            }
            final int _tmpArchiveAgeDays;
            _tmpArchiveAgeDays = _cursor.getInt(_cursorIndexOfArchiveAgeDays);
            final boolean _tmpArchivePhotos;
            final int _tmp_7;
            _tmp_7 = _cursor.getInt(_cursorIndexOfArchivePhotos);
            _tmpArchivePhotos = _tmp_7 != 0;
            final boolean _tmpArchiveVideos;
            final int _tmp_8;
            _tmp_8 = _cursor.getInt(_cursorIndexOfArchiveVideos);
            _tmpArchiveVideos = _tmp_8 != 0;
            final Integer _tmpMaxFolderDepth;
            if (_cursor.isNull(_cursorIndexOfMaxFolderDepth)) {
              _tmpMaxFolderDepth = null;
            } else {
              _tmpMaxFolderDepth = _cursor.getInt(_cursorIndexOfMaxFolderDepth);
            }
            final Integer _tmpBandwidthLimitKbps;
            if (_cursor.isNull(_cursorIndexOfBandwidthLimitKbps)) {
              _tmpBandwidthLimitKbps = null;
            } else {
              _tmpBandwidthLimitKbps = _cursor.getInt(_cursorIndexOfBandwidthLimitKbps);
            }
            final int _tmpParallelTransfers;
            _tmpParallelTransfers = _cursor.getInt(_cursorIndexOfParallelTransfers);
            final ScheduleType _tmpScheduleType;
            final String _tmp_9;
            _tmp_9 = _cursor.getString(_cursorIndexOfScheduleType);
            _tmpScheduleType = __converters.toScheduleType(_tmp_9);
            final Integer _tmpIntervalMinutes;
            if (_cursor.isNull(_cursorIndexOfIntervalMinutes)) {
              _tmpIntervalMinutes = null;
            } else {
              _tmpIntervalMinutes = _cursor.getInt(_cursorIndexOfIntervalMinutes);
            }
            final String _tmpDailyTime;
            if (_cursor.isNull(_cursorIndexOfDailyTime)) {
              _tmpDailyTime = null;
            } else {
              _tmpDailyTime = _cursor.getString(_cursorIndexOfDailyTime);
            }
            final NetworkPreference _tmpNetworkPreference;
            final String _tmp_10;
            _tmp_10 = _cursor.getString(_cursorIndexOfNetworkPreference);
            _tmpNetworkPreference = __converters.toNetworkPreference(_tmp_10);
            final BatteryRequirement _tmpBatteryRequirement;
            final String _tmp_11;
            _tmp_11 = _cursor.getString(_cursorIndexOfBatteryRequirement);
            _tmpBatteryRequirement = __converters.toBatteryRequirement(_tmp_11);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpLastSyncAt;
            if (_cursor.isNull(_cursorIndexOfLastSyncAt)) {
              _tmpLastSyncAt = null;
            } else {
              _tmpLastSyncAt = _cursor.getLong(_cursorIndexOfLastSyncAt);
            }
            _item = new SyncConfiguration(_tmpId,_tmpName,_tmpServerId,_tmpLocalPath,_tmpRemotePath,_tmpSyncMode,_tmpEnabled,_tmpTimeTolerance,_tmpIgnoreDst,_tmpCaseSensitive,_tmpUseChecksum,_tmpMinFileSize,_tmpMaxFileSize,_tmpModifiedAfter,_tmpModifiedBefore,_tmpIgnoreHiddenFiles,_tmpIgnoreZeroByteFiles,_tmpFileExtensionFilter,_tmpArchiveAgeDays,_tmpArchivePhotos,_tmpArchiveVideos,_tmpMaxFolderDepth,_tmpBandwidthLimitKbps,_tmpParallelTransfers,_tmpScheduleType,_tmpIntervalMinutes,_tmpDailyTime,_tmpNetworkPreference,_tmpBatteryRequirement,_tmpCreatedAt,_tmpLastSyncAt);
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
  public Flow<List<SyncConfiguration>> getEnabledConfigurations() {
    final String _sql = "SELECT * FROM sync_configurations WHERE enabled = 1 ORDER BY name ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"sync_configurations"}, new Callable<List<SyncConfiguration>>() {
      @Override
      @NonNull
      public List<SyncConfiguration> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfServerId = CursorUtil.getColumnIndexOrThrow(_cursor, "serverId");
          final int _cursorIndexOfLocalPath = CursorUtil.getColumnIndexOrThrow(_cursor, "localPath");
          final int _cursorIndexOfRemotePath = CursorUtil.getColumnIndexOrThrow(_cursor, "remotePath");
          final int _cursorIndexOfSyncMode = CursorUtil.getColumnIndexOrThrow(_cursor, "syncMode");
          final int _cursorIndexOfEnabled = CursorUtil.getColumnIndexOrThrow(_cursor, "enabled");
          final int _cursorIndexOfTimeTolerance = CursorUtil.getColumnIndexOrThrow(_cursor, "timeTolerance");
          final int _cursorIndexOfIgnoreDst = CursorUtil.getColumnIndexOrThrow(_cursor, "ignoreDst");
          final int _cursorIndexOfCaseSensitive = CursorUtil.getColumnIndexOrThrow(_cursor, "caseSensitive");
          final int _cursorIndexOfUseChecksum = CursorUtil.getColumnIndexOrThrow(_cursor, "useChecksum");
          final int _cursorIndexOfMinFileSize = CursorUtil.getColumnIndexOrThrow(_cursor, "minFileSize");
          final int _cursorIndexOfMaxFileSize = CursorUtil.getColumnIndexOrThrow(_cursor, "maxFileSize");
          final int _cursorIndexOfModifiedAfter = CursorUtil.getColumnIndexOrThrow(_cursor, "modifiedAfter");
          final int _cursorIndexOfModifiedBefore = CursorUtil.getColumnIndexOrThrow(_cursor, "modifiedBefore");
          final int _cursorIndexOfIgnoreHiddenFiles = CursorUtil.getColumnIndexOrThrow(_cursor, "ignoreHiddenFiles");
          final int _cursorIndexOfIgnoreZeroByteFiles = CursorUtil.getColumnIndexOrThrow(_cursor, "ignoreZeroByteFiles");
          final int _cursorIndexOfFileExtensionFilter = CursorUtil.getColumnIndexOrThrow(_cursor, "fileExtensionFilter");
          final int _cursorIndexOfArchiveAgeDays = CursorUtil.getColumnIndexOrThrow(_cursor, "archiveAgeDays");
          final int _cursorIndexOfArchivePhotos = CursorUtil.getColumnIndexOrThrow(_cursor, "archivePhotos");
          final int _cursorIndexOfArchiveVideos = CursorUtil.getColumnIndexOrThrow(_cursor, "archiveVideos");
          final int _cursorIndexOfMaxFolderDepth = CursorUtil.getColumnIndexOrThrow(_cursor, "maxFolderDepth");
          final int _cursorIndexOfBandwidthLimitKbps = CursorUtil.getColumnIndexOrThrow(_cursor, "bandwidthLimitKbps");
          final int _cursorIndexOfParallelTransfers = CursorUtil.getColumnIndexOrThrow(_cursor, "parallelTransfers");
          final int _cursorIndexOfScheduleType = CursorUtil.getColumnIndexOrThrow(_cursor, "scheduleType");
          final int _cursorIndexOfIntervalMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "intervalMinutes");
          final int _cursorIndexOfDailyTime = CursorUtil.getColumnIndexOrThrow(_cursor, "dailyTime");
          final int _cursorIndexOfNetworkPreference = CursorUtil.getColumnIndexOrThrow(_cursor, "networkPreference");
          final int _cursorIndexOfBatteryRequirement = CursorUtil.getColumnIndexOrThrow(_cursor, "batteryRequirement");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfLastSyncAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSyncAt");
          final List<SyncConfiguration> _result = new ArrayList<SyncConfiguration>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final SyncConfiguration _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final long _tmpServerId;
            _tmpServerId = _cursor.getLong(_cursorIndexOfServerId);
            final String _tmpLocalPath;
            _tmpLocalPath = _cursor.getString(_cursorIndexOfLocalPath);
            final String _tmpRemotePath;
            _tmpRemotePath = _cursor.getString(_cursorIndexOfRemotePath);
            final SyncMode _tmpSyncMode;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfSyncMode);
            _tmpSyncMode = __converters.toSyncMode(_tmp);
            final boolean _tmpEnabled;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfEnabled);
            _tmpEnabled = _tmp_1 != 0;
            final int _tmpTimeTolerance;
            _tmpTimeTolerance = _cursor.getInt(_cursorIndexOfTimeTolerance);
            final boolean _tmpIgnoreDst;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIgnoreDst);
            _tmpIgnoreDst = _tmp_2 != 0;
            final boolean _tmpCaseSensitive;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfCaseSensitive);
            _tmpCaseSensitive = _tmp_3 != 0;
            final boolean _tmpUseChecksum;
            final int _tmp_4;
            _tmp_4 = _cursor.getInt(_cursorIndexOfUseChecksum);
            _tmpUseChecksum = _tmp_4 != 0;
            final Long _tmpMinFileSize;
            if (_cursor.isNull(_cursorIndexOfMinFileSize)) {
              _tmpMinFileSize = null;
            } else {
              _tmpMinFileSize = _cursor.getLong(_cursorIndexOfMinFileSize);
            }
            final Long _tmpMaxFileSize;
            if (_cursor.isNull(_cursorIndexOfMaxFileSize)) {
              _tmpMaxFileSize = null;
            } else {
              _tmpMaxFileSize = _cursor.getLong(_cursorIndexOfMaxFileSize);
            }
            final Long _tmpModifiedAfter;
            if (_cursor.isNull(_cursorIndexOfModifiedAfter)) {
              _tmpModifiedAfter = null;
            } else {
              _tmpModifiedAfter = _cursor.getLong(_cursorIndexOfModifiedAfter);
            }
            final Long _tmpModifiedBefore;
            if (_cursor.isNull(_cursorIndexOfModifiedBefore)) {
              _tmpModifiedBefore = null;
            } else {
              _tmpModifiedBefore = _cursor.getLong(_cursorIndexOfModifiedBefore);
            }
            final boolean _tmpIgnoreHiddenFiles;
            final int _tmp_5;
            _tmp_5 = _cursor.getInt(_cursorIndexOfIgnoreHiddenFiles);
            _tmpIgnoreHiddenFiles = _tmp_5 != 0;
            final boolean _tmpIgnoreZeroByteFiles;
            final int _tmp_6;
            _tmp_6 = _cursor.getInt(_cursorIndexOfIgnoreZeroByteFiles);
            _tmpIgnoreZeroByteFiles = _tmp_6 != 0;
            final String _tmpFileExtensionFilter;
            if (_cursor.isNull(_cursorIndexOfFileExtensionFilter)) {
              _tmpFileExtensionFilter = null;
            } else {
              _tmpFileExtensionFilter = _cursor.getString(_cursorIndexOfFileExtensionFilter);
            }
            final int _tmpArchiveAgeDays;
            _tmpArchiveAgeDays = _cursor.getInt(_cursorIndexOfArchiveAgeDays);
            final boolean _tmpArchivePhotos;
            final int _tmp_7;
            _tmp_7 = _cursor.getInt(_cursorIndexOfArchivePhotos);
            _tmpArchivePhotos = _tmp_7 != 0;
            final boolean _tmpArchiveVideos;
            final int _tmp_8;
            _tmp_8 = _cursor.getInt(_cursorIndexOfArchiveVideos);
            _tmpArchiveVideos = _tmp_8 != 0;
            final Integer _tmpMaxFolderDepth;
            if (_cursor.isNull(_cursorIndexOfMaxFolderDepth)) {
              _tmpMaxFolderDepth = null;
            } else {
              _tmpMaxFolderDepth = _cursor.getInt(_cursorIndexOfMaxFolderDepth);
            }
            final Integer _tmpBandwidthLimitKbps;
            if (_cursor.isNull(_cursorIndexOfBandwidthLimitKbps)) {
              _tmpBandwidthLimitKbps = null;
            } else {
              _tmpBandwidthLimitKbps = _cursor.getInt(_cursorIndexOfBandwidthLimitKbps);
            }
            final int _tmpParallelTransfers;
            _tmpParallelTransfers = _cursor.getInt(_cursorIndexOfParallelTransfers);
            final ScheduleType _tmpScheduleType;
            final String _tmp_9;
            _tmp_9 = _cursor.getString(_cursorIndexOfScheduleType);
            _tmpScheduleType = __converters.toScheduleType(_tmp_9);
            final Integer _tmpIntervalMinutes;
            if (_cursor.isNull(_cursorIndexOfIntervalMinutes)) {
              _tmpIntervalMinutes = null;
            } else {
              _tmpIntervalMinutes = _cursor.getInt(_cursorIndexOfIntervalMinutes);
            }
            final String _tmpDailyTime;
            if (_cursor.isNull(_cursorIndexOfDailyTime)) {
              _tmpDailyTime = null;
            } else {
              _tmpDailyTime = _cursor.getString(_cursorIndexOfDailyTime);
            }
            final NetworkPreference _tmpNetworkPreference;
            final String _tmp_10;
            _tmp_10 = _cursor.getString(_cursorIndexOfNetworkPreference);
            _tmpNetworkPreference = __converters.toNetworkPreference(_tmp_10);
            final BatteryRequirement _tmpBatteryRequirement;
            final String _tmp_11;
            _tmp_11 = _cursor.getString(_cursorIndexOfBatteryRequirement);
            _tmpBatteryRequirement = __converters.toBatteryRequirement(_tmp_11);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpLastSyncAt;
            if (_cursor.isNull(_cursorIndexOfLastSyncAt)) {
              _tmpLastSyncAt = null;
            } else {
              _tmpLastSyncAt = _cursor.getLong(_cursorIndexOfLastSyncAt);
            }
            _item = new SyncConfiguration(_tmpId,_tmpName,_tmpServerId,_tmpLocalPath,_tmpRemotePath,_tmpSyncMode,_tmpEnabled,_tmpTimeTolerance,_tmpIgnoreDst,_tmpCaseSensitive,_tmpUseChecksum,_tmpMinFileSize,_tmpMaxFileSize,_tmpModifiedAfter,_tmpModifiedBefore,_tmpIgnoreHiddenFiles,_tmpIgnoreZeroByteFiles,_tmpFileExtensionFilter,_tmpArchiveAgeDays,_tmpArchivePhotos,_tmpArchiveVideos,_tmpMaxFolderDepth,_tmpBandwidthLimitKbps,_tmpParallelTransfers,_tmpScheduleType,_tmpIntervalMinutes,_tmpDailyTime,_tmpNetworkPreference,_tmpBatteryRequirement,_tmpCreatedAt,_tmpLastSyncAt);
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
  public Object getConfigurationById(final long id,
      final Continuation<? super SyncConfiguration> $completion) {
    final String _sql = "SELECT * FROM sync_configurations WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<SyncConfiguration>() {
      @Override
      @Nullable
      public SyncConfiguration call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfServerId = CursorUtil.getColumnIndexOrThrow(_cursor, "serverId");
          final int _cursorIndexOfLocalPath = CursorUtil.getColumnIndexOrThrow(_cursor, "localPath");
          final int _cursorIndexOfRemotePath = CursorUtil.getColumnIndexOrThrow(_cursor, "remotePath");
          final int _cursorIndexOfSyncMode = CursorUtil.getColumnIndexOrThrow(_cursor, "syncMode");
          final int _cursorIndexOfEnabled = CursorUtil.getColumnIndexOrThrow(_cursor, "enabled");
          final int _cursorIndexOfTimeTolerance = CursorUtil.getColumnIndexOrThrow(_cursor, "timeTolerance");
          final int _cursorIndexOfIgnoreDst = CursorUtil.getColumnIndexOrThrow(_cursor, "ignoreDst");
          final int _cursorIndexOfCaseSensitive = CursorUtil.getColumnIndexOrThrow(_cursor, "caseSensitive");
          final int _cursorIndexOfUseChecksum = CursorUtil.getColumnIndexOrThrow(_cursor, "useChecksum");
          final int _cursorIndexOfMinFileSize = CursorUtil.getColumnIndexOrThrow(_cursor, "minFileSize");
          final int _cursorIndexOfMaxFileSize = CursorUtil.getColumnIndexOrThrow(_cursor, "maxFileSize");
          final int _cursorIndexOfModifiedAfter = CursorUtil.getColumnIndexOrThrow(_cursor, "modifiedAfter");
          final int _cursorIndexOfModifiedBefore = CursorUtil.getColumnIndexOrThrow(_cursor, "modifiedBefore");
          final int _cursorIndexOfIgnoreHiddenFiles = CursorUtil.getColumnIndexOrThrow(_cursor, "ignoreHiddenFiles");
          final int _cursorIndexOfIgnoreZeroByteFiles = CursorUtil.getColumnIndexOrThrow(_cursor, "ignoreZeroByteFiles");
          final int _cursorIndexOfFileExtensionFilter = CursorUtil.getColumnIndexOrThrow(_cursor, "fileExtensionFilter");
          final int _cursorIndexOfArchiveAgeDays = CursorUtil.getColumnIndexOrThrow(_cursor, "archiveAgeDays");
          final int _cursorIndexOfArchivePhotos = CursorUtil.getColumnIndexOrThrow(_cursor, "archivePhotos");
          final int _cursorIndexOfArchiveVideos = CursorUtil.getColumnIndexOrThrow(_cursor, "archiveVideos");
          final int _cursorIndexOfMaxFolderDepth = CursorUtil.getColumnIndexOrThrow(_cursor, "maxFolderDepth");
          final int _cursorIndexOfBandwidthLimitKbps = CursorUtil.getColumnIndexOrThrow(_cursor, "bandwidthLimitKbps");
          final int _cursorIndexOfParallelTransfers = CursorUtil.getColumnIndexOrThrow(_cursor, "parallelTransfers");
          final int _cursorIndexOfScheduleType = CursorUtil.getColumnIndexOrThrow(_cursor, "scheduleType");
          final int _cursorIndexOfIntervalMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "intervalMinutes");
          final int _cursorIndexOfDailyTime = CursorUtil.getColumnIndexOrThrow(_cursor, "dailyTime");
          final int _cursorIndexOfNetworkPreference = CursorUtil.getColumnIndexOrThrow(_cursor, "networkPreference");
          final int _cursorIndexOfBatteryRequirement = CursorUtil.getColumnIndexOrThrow(_cursor, "batteryRequirement");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfLastSyncAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSyncAt");
          final SyncConfiguration _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final long _tmpServerId;
            _tmpServerId = _cursor.getLong(_cursorIndexOfServerId);
            final String _tmpLocalPath;
            _tmpLocalPath = _cursor.getString(_cursorIndexOfLocalPath);
            final String _tmpRemotePath;
            _tmpRemotePath = _cursor.getString(_cursorIndexOfRemotePath);
            final SyncMode _tmpSyncMode;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfSyncMode);
            _tmpSyncMode = __converters.toSyncMode(_tmp);
            final boolean _tmpEnabled;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfEnabled);
            _tmpEnabled = _tmp_1 != 0;
            final int _tmpTimeTolerance;
            _tmpTimeTolerance = _cursor.getInt(_cursorIndexOfTimeTolerance);
            final boolean _tmpIgnoreDst;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIgnoreDst);
            _tmpIgnoreDst = _tmp_2 != 0;
            final boolean _tmpCaseSensitive;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfCaseSensitive);
            _tmpCaseSensitive = _tmp_3 != 0;
            final boolean _tmpUseChecksum;
            final int _tmp_4;
            _tmp_4 = _cursor.getInt(_cursorIndexOfUseChecksum);
            _tmpUseChecksum = _tmp_4 != 0;
            final Long _tmpMinFileSize;
            if (_cursor.isNull(_cursorIndexOfMinFileSize)) {
              _tmpMinFileSize = null;
            } else {
              _tmpMinFileSize = _cursor.getLong(_cursorIndexOfMinFileSize);
            }
            final Long _tmpMaxFileSize;
            if (_cursor.isNull(_cursorIndexOfMaxFileSize)) {
              _tmpMaxFileSize = null;
            } else {
              _tmpMaxFileSize = _cursor.getLong(_cursorIndexOfMaxFileSize);
            }
            final Long _tmpModifiedAfter;
            if (_cursor.isNull(_cursorIndexOfModifiedAfter)) {
              _tmpModifiedAfter = null;
            } else {
              _tmpModifiedAfter = _cursor.getLong(_cursorIndexOfModifiedAfter);
            }
            final Long _tmpModifiedBefore;
            if (_cursor.isNull(_cursorIndexOfModifiedBefore)) {
              _tmpModifiedBefore = null;
            } else {
              _tmpModifiedBefore = _cursor.getLong(_cursorIndexOfModifiedBefore);
            }
            final boolean _tmpIgnoreHiddenFiles;
            final int _tmp_5;
            _tmp_5 = _cursor.getInt(_cursorIndexOfIgnoreHiddenFiles);
            _tmpIgnoreHiddenFiles = _tmp_5 != 0;
            final boolean _tmpIgnoreZeroByteFiles;
            final int _tmp_6;
            _tmp_6 = _cursor.getInt(_cursorIndexOfIgnoreZeroByteFiles);
            _tmpIgnoreZeroByteFiles = _tmp_6 != 0;
            final String _tmpFileExtensionFilter;
            if (_cursor.isNull(_cursorIndexOfFileExtensionFilter)) {
              _tmpFileExtensionFilter = null;
            } else {
              _tmpFileExtensionFilter = _cursor.getString(_cursorIndexOfFileExtensionFilter);
            }
            final int _tmpArchiveAgeDays;
            _tmpArchiveAgeDays = _cursor.getInt(_cursorIndexOfArchiveAgeDays);
            final boolean _tmpArchivePhotos;
            final int _tmp_7;
            _tmp_7 = _cursor.getInt(_cursorIndexOfArchivePhotos);
            _tmpArchivePhotos = _tmp_7 != 0;
            final boolean _tmpArchiveVideos;
            final int _tmp_8;
            _tmp_8 = _cursor.getInt(_cursorIndexOfArchiveVideos);
            _tmpArchiveVideos = _tmp_8 != 0;
            final Integer _tmpMaxFolderDepth;
            if (_cursor.isNull(_cursorIndexOfMaxFolderDepth)) {
              _tmpMaxFolderDepth = null;
            } else {
              _tmpMaxFolderDepth = _cursor.getInt(_cursorIndexOfMaxFolderDepth);
            }
            final Integer _tmpBandwidthLimitKbps;
            if (_cursor.isNull(_cursorIndexOfBandwidthLimitKbps)) {
              _tmpBandwidthLimitKbps = null;
            } else {
              _tmpBandwidthLimitKbps = _cursor.getInt(_cursorIndexOfBandwidthLimitKbps);
            }
            final int _tmpParallelTransfers;
            _tmpParallelTransfers = _cursor.getInt(_cursorIndexOfParallelTransfers);
            final ScheduleType _tmpScheduleType;
            final String _tmp_9;
            _tmp_9 = _cursor.getString(_cursorIndexOfScheduleType);
            _tmpScheduleType = __converters.toScheduleType(_tmp_9);
            final Integer _tmpIntervalMinutes;
            if (_cursor.isNull(_cursorIndexOfIntervalMinutes)) {
              _tmpIntervalMinutes = null;
            } else {
              _tmpIntervalMinutes = _cursor.getInt(_cursorIndexOfIntervalMinutes);
            }
            final String _tmpDailyTime;
            if (_cursor.isNull(_cursorIndexOfDailyTime)) {
              _tmpDailyTime = null;
            } else {
              _tmpDailyTime = _cursor.getString(_cursorIndexOfDailyTime);
            }
            final NetworkPreference _tmpNetworkPreference;
            final String _tmp_10;
            _tmp_10 = _cursor.getString(_cursorIndexOfNetworkPreference);
            _tmpNetworkPreference = __converters.toNetworkPreference(_tmp_10);
            final BatteryRequirement _tmpBatteryRequirement;
            final String _tmp_11;
            _tmp_11 = _cursor.getString(_cursorIndexOfBatteryRequirement);
            _tmpBatteryRequirement = __converters.toBatteryRequirement(_tmp_11);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpLastSyncAt;
            if (_cursor.isNull(_cursorIndexOfLastSyncAt)) {
              _tmpLastSyncAt = null;
            } else {
              _tmpLastSyncAt = _cursor.getLong(_cursorIndexOfLastSyncAt);
            }
            _result = new SyncConfiguration(_tmpId,_tmpName,_tmpServerId,_tmpLocalPath,_tmpRemotePath,_tmpSyncMode,_tmpEnabled,_tmpTimeTolerance,_tmpIgnoreDst,_tmpCaseSensitive,_tmpUseChecksum,_tmpMinFileSize,_tmpMaxFileSize,_tmpModifiedAfter,_tmpModifiedBefore,_tmpIgnoreHiddenFiles,_tmpIgnoreZeroByteFiles,_tmpFileExtensionFilter,_tmpArchiveAgeDays,_tmpArchivePhotos,_tmpArchiveVideos,_tmpMaxFolderDepth,_tmpBandwidthLimitKbps,_tmpParallelTransfers,_tmpScheduleType,_tmpIntervalMinutes,_tmpDailyTime,_tmpNetworkPreference,_tmpBatteryRequirement,_tmpCreatedAt,_tmpLastSyncAt);
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
  public Flow<List<SyncConfiguration>> getConfigurationsByServer(final long serverId) {
    final String _sql = "SELECT * FROM sync_configurations WHERE serverId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, serverId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"sync_configurations"}, new Callable<List<SyncConfiguration>>() {
      @Override
      @NonNull
      public List<SyncConfiguration> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfServerId = CursorUtil.getColumnIndexOrThrow(_cursor, "serverId");
          final int _cursorIndexOfLocalPath = CursorUtil.getColumnIndexOrThrow(_cursor, "localPath");
          final int _cursorIndexOfRemotePath = CursorUtil.getColumnIndexOrThrow(_cursor, "remotePath");
          final int _cursorIndexOfSyncMode = CursorUtil.getColumnIndexOrThrow(_cursor, "syncMode");
          final int _cursorIndexOfEnabled = CursorUtil.getColumnIndexOrThrow(_cursor, "enabled");
          final int _cursorIndexOfTimeTolerance = CursorUtil.getColumnIndexOrThrow(_cursor, "timeTolerance");
          final int _cursorIndexOfIgnoreDst = CursorUtil.getColumnIndexOrThrow(_cursor, "ignoreDst");
          final int _cursorIndexOfCaseSensitive = CursorUtil.getColumnIndexOrThrow(_cursor, "caseSensitive");
          final int _cursorIndexOfUseChecksum = CursorUtil.getColumnIndexOrThrow(_cursor, "useChecksum");
          final int _cursorIndexOfMinFileSize = CursorUtil.getColumnIndexOrThrow(_cursor, "minFileSize");
          final int _cursorIndexOfMaxFileSize = CursorUtil.getColumnIndexOrThrow(_cursor, "maxFileSize");
          final int _cursorIndexOfModifiedAfter = CursorUtil.getColumnIndexOrThrow(_cursor, "modifiedAfter");
          final int _cursorIndexOfModifiedBefore = CursorUtil.getColumnIndexOrThrow(_cursor, "modifiedBefore");
          final int _cursorIndexOfIgnoreHiddenFiles = CursorUtil.getColumnIndexOrThrow(_cursor, "ignoreHiddenFiles");
          final int _cursorIndexOfIgnoreZeroByteFiles = CursorUtil.getColumnIndexOrThrow(_cursor, "ignoreZeroByteFiles");
          final int _cursorIndexOfFileExtensionFilter = CursorUtil.getColumnIndexOrThrow(_cursor, "fileExtensionFilter");
          final int _cursorIndexOfArchiveAgeDays = CursorUtil.getColumnIndexOrThrow(_cursor, "archiveAgeDays");
          final int _cursorIndexOfArchivePhotos = CursorUtil.getColumnIndexOrThrow(_cursor, "archivePhotos");
          final int _cursorIndexOfArchiveVideos = CursorUtil.getColumnIndexOrThrow(_cursor, "archiveVideos");
          final int _cursorIndexOfMaxFolderDepth = CursorUtil.getColumnIndexOrThrow(_cursor, "maxFolderDepth");
          final int _cursorIndexOfBandwidthLimitKbps = CursorUtil.getColumnIndexOrThrow(_cursor, "bandwidthLimitKbps");
          final int _cursorIndexOfParallelTransfers = CursorUtil.getColumnIndexOrThrow(_cursor, "parallelTransfers");
          final int _cursorIndexOfScheduleType = CursorUtil.getColumnIndexOrThrow(_cursor, "scheduleType");
          final int _cursorIndexOfIntervalMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "intervalMinutes");
          final int _cursorIndexOfDailyTime = CursorUtil.getColumnIndexOrThrow(_cursor, "dailyTime");
          final int _cursorIndexOfNetworkPreference = CursorUtil.getColumnIndexOrThrow(_cursor, "networkPreference");
          final int _cursorIndexOfBatteryRequirement = CursorUtil.getColumnIndexOrThrow(_cursor, "batteryRequirement");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfLastSyncAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSyncAt");
          final List<SyncConfiguration> _result = new ArrayList<SyncConfiguration>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final SyncConfiguration _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final long _tmpServerId;
            _tmpServerId = _cursor.getLong(_cursorIndexOfServerId);
            final String _tmpLocalPath;
            _tmpLocalPath = _cursor.getString(_cursorIndexOfLocalPath);
            final String _tmpRemotePath;
            _tmpRemotePath = _cursor.getString(_cursorIndexOfRemotePath);
            final SyncMode _tmpSyncMode;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfSyncMode);
            _tmpSyncMode = __converters.toSyncMode(_tmp);
            final boolean _tmpEnabled;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfEnabled);
            _tmpEnabled = _tmp_1 != 0;
            final int _tmpTimeTolerance;
            _tmpTimeTolerance = _cursor.getInt(_cursorIndexOfTimeTolerance);
            final boolean _tmpIgnoreDst;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIgnoreDst);
            _tmpIgnoreDst = _tmp_2 != 0;
            final boolean _tmpCaseSensitive;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfCaseSensitive);
            _tmpCaseSensitive = _tmp_3 != 0;
            final boolean _tmpUseChecksum;
            final int _tmp_4;
            _tmp_4 = _cursor.getInt(_cursorIndexOfUseChecksum);
            _tmpUseChecksum = _tmp_4 != 0;
            final Long _tmpMinFileSize;
            if (_cursor.isNull(_cursorIndexOfMinFileSize)) {
              _tmpMinFileSize = null;
            } else {
              _tmpMinFileSize = _cursor.getLong(_cursorIndexOfMinFileSize);
            }
            final Long _tmpMaxFileSize;
            if (_cursor.isNull(_cursorIndexOfMaxFileSize)) {
              _tmpMaxFileSize = null;
            } else {
              _tmpMaxFileSize = _cursor.getLong(_cursorIndexOfMaxFileSize);
            }
            final Long _tmpModifiedAfter;
            if (_cursor.isNull(_cursorIndexOfModifiedAfter)) {
              _tmpModifiedAfter = null;
            } else {
              _tmpModifiedAfter = _cursor.getLong(_cursorIndexOfModifiedAfter);
            }
            final Long _tmpModifiedBefore;
            if (_cursor.isNull(_cursorIndexOfModifiedBefore)) {
              _tmpModifiedBefore = null;
            } else {
              _tmpModifiedBefore = _cursor.getLong(_cursorIndexOfModifiedBefore);
            }
            final boolean _tmpIgnoreHiddenFiles;
            final int _tmp_5;
            _tmp_5 = _cursor.getInt(_cursorIndexOfIgnoreHiddenFiles);
            _tmpIgnoreHiddenFiles = _tmp_5 != 0;
            final boolean _tmpIgnoreZeroByteFiles;
            final int _tmp_6;
            _tmp_6 = _cursor.getInt(_cursorIndexOfIgnoreZeroByteFiles);
            _tmpIgnoreZeroByteFiles = _tmp_6 != 0;
            final String _tmpFileExtensionFilter;
            if (_cursor.isNull(_cursorIndexOfFileExtensionFilter)) {
              _tmpFileExtensionFilter = null;
            } else {
              _tmpFileExtensionFilter = _cursor.getString(_cursorIndexOfFileExtensionFilter);
            }
            final int _tmpArchiveAgeDays;
            _tmpArchiveAgeDays = _cursor.getInt(_cursorIndexOfArchiveAgeDays);
            final boolean _tmpArchivePhotos;
            final int _tmp_7;
            _tmp_7 = _cursor.getInt(_cursorIndexOfArchivePhotos);
            _tmpArchivePhotos = _tmp_7 != 0;
            final boolean _tmpArchiveVideos;
            final int _tmp_8;
            _tmp_8 = _cursor.getInt(_cursorIndexOfArchiveVideos);
            _tmpArchiveVideos = _tmp_8 != 0;
            final Integer _tmpMaxFolderDepth;
            if (_cursor.isNull(_cursorIndexOfMaxFolderDepth)) {
              _tmpMaxFolderDepth = null;
            } else {
              _tmpMaxFolderDepth = _cursor.getInt(_cursorIndexOfMaxFolderDepth);
            }
            final Integer _tmpBandwidthLimitKbps;
            if (_cursor.isNull(_cursorIndexOfBandwidthLimitKbps)) {
              _tmpBandwidthLimitKbps = null;
            } else {
              _tmpBandwidthLimitKbps = _cursor.getInt(_cursorIndexOfBandwidthLimitKbps);
            }
            final int _tmpParallelTransfers;
            _tmpParallelTransfers = _cursor.getInt(_cursorIndexOfParallelTransfers);
            final ScheduleType _tmpScheduleType;
            final String _tmp_9;
            _tmp_9 = _cursor.getString(_cursorIndexOfScheduleType);
            _tmpScheduleType = __converters.toScheduleType(_tmp_9);
            final Integer _tmpIntervalMinutes;
            if (_cursor.isNull(_cursorIndexOfIntervalMinutes)) {
              _tmpIntervalMinutes = null;
            } else {
              _tmpIntervalMinutes = _cursor.getInt(_cursorIndexOfIntervalMinutes);
            }
            final String _tmpDailyTime;
            if (_cursor.isNull(_cursorIndexOfDailyTime)) {
              _tmpDailyTime = null;
            } else {
              _tmpDailyTime = _cursor.getString(_cursorIndexOfDailyTime);
            }
            final NetworkPreference _tmpNetworkPreference;
            final String _tmp_10;
            _tmp_10 = _cursor.getString(_cursorIndexOfNetworkPreference);
            _tmpNetworkPreference = __converters.toNetworkPreference(_tmp_10);
            final BatteryRequirement _tmpBatteryRequirement;
            final String _tmp_11;
            _tmp_11 = _cursor.getString(_cursorIndexOfBatteryRequirement);
            _tmpBatteryRequirement = __converters.toBatteryRequirement(_tmp_11);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpLastSyncAt;
            if (_cursor.isNull(_cursorIndexOfLastSyncAt)) {
              _tmpLastSyncAt = null;
            } else {
              _tmpLastSyncAt = _cursor.getLong(_cursorIndexOfLastSyncAt);
            }
            _item = new SyncConfiguration(_tmpId,_tmpName,_tmpServerId,_tmpLocalPath,_tmpRemotePath,_tmpSyncMode,_tmpEnabled,_tmpTimeTolerance,_tmpIgnoreDst,_tmpCaseSensitive,_tmpUseChecksum,_tmpMinFileSize,_tmpMaxFileSize,_tmpModifiedAfter,_tmpModifiedBefore,_tmpIgnoreHiddenFiles,_tmpIgnoreZeroByteFiles,_tmpFileExtensionFilter,_tmpArchiveAgeDays,_tmpArchivePhotos,_tmpArchiveVideos,_tmpMaxFolderDepth,_tmpBandwidthLimitKbps,_tmpParallelTransfers,_tmpScheduleType,_tmpIntervalMinutes,_tmpDailyTime,_tmpNetworkPreference,_tmpBatteryRequirement,_tmpCreatedAt,_tmpLastSyncAt);
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
