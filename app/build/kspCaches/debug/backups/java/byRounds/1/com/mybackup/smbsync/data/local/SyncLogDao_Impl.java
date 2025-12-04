package com.mybackup.smbsync.data.local;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.mybackup.smbsync.data.model.SyncLog;
import com.mybackup.smbsync.data.model.SyncLogWithName;
import com.mybackup.smbsync.data.model.SyncStatus;
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
public final class SyncLogDao_Impl implements SyncLogDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<SyncLog> __insertionAdapterOfSyncLog;

  private final Converters __converters = new Converters();

  private final SharedSQLiteStatement __preparedStmtOfDeleteOldLogs;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAllLogs;

  public SyncLogDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfSyncLog = new EntityInsertionAdapter<SyncLog>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `sync_logs` (`id`,`configId`,`timestamp`,`status`,`filesCopied`,`filesDeleted`,`filesSkipped`,`filesFailed`,`bytesTransferred`,`durationMs`,`errorMessage`,`detailedLog`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final SyncLog entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getConfigId());
        statement.bindLong(3, entity.getTimestamp());
        final String _tmp = __converters.fromSyncStatus(entity.getStatus());
        statement.bindString(4, _tmp);
        statement.bindLong(5, entity.getFilesCopied());
        statement.bindLong(6, entity.getFilesDeleted());
        statement.bindLong(7, entity.getFilesSkipped());
        statement.bindLong(8, entity.getFilesFailed());
        statement.bindLong(9, entity.getBytesTransferred());
        statement.bindLong(10, entity.getDurationMs());
        if (entity.getErrorMessage() == null) {
          statement.bindNull(11);
        } else {
          statement.bindString(11, entity.getErrorMessage());
        }
        if (entity.getDetailedLog() == null) {
          statement.bindNull(12);
        } else {
          statement.bindString(12, entity.getDetailedLog());
        }
      }
    };
    this.__preparedStmtOfDeleteOldLogs = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM sync_logs WHERE timestamp < ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteAllLogs = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM sync_logs";
        return _query;
      }
    };
  }

  @Override
  public Object insertLog(final SyncLog log, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfSyncLog.insertAndReturnId(log);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteOldLogs(final long beforeTimestamp,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteOldLogs.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, beforeTimestamp);
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
          __preparedStmtOfDeleteOldLogs.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteAllLogs(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAllLogs.acquire();
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
          __preparedStmtOfDeleteAllLogs.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<SyncLogWithName>> getRecentLogsWithNames(final int limit) {
    final String _sql = "\n"
            + "        SELECT sync_logs.*, sync_configurations.name as configName \n"
            + "        FROM sync_logs \n"
            + "        LEFT JOIN sync_configurations ON sync_logs.configId = sync_configurations.id \n"
            + "        ORDER BY timestamp DESC \n"
            + "        LIMIT ?\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, limit);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"sync_logs",
        "sync_configurations"}, new Callable<List<SyncLogWithName>>() {
      @Override
      @NonNull
      public List<SyncLogWithName> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfConfigId = CursorUtil.getColumnIndexOrThrow(_cursor, "configId");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfFilesCopied = CursorUtil.getColumnIndexOrThrow(_cursor, "filesCopied");
          final int _cursorIndexOfFilesDeleted = CursorUtil.getColumnIndexOrThrow(_cursor, "filesDeleted");
          final int _cursorIndexOfFilesSkipped = CursorUtil.getColumnIndexOrThrow(_cursor, "filesSkipped");
          final int _cursorIndexOfFilesFailed = CursorUtil.getColumnIndexOrThrow(_cursor, "filesFailed");
          final int _cursorIndexOfBytesTransferred = CursorUtil.getColumnIndexOrThrow(_cursor, "bytesTransferred");
          final int _cursorIndexOfDurationMs = CursorUtil.getColumnIndexOrThrow(_cursor, "durationMs");
          final int _cursorIndexOfErrorMessage = CursorUtil.getColumnIndexOrThrow(_cursor, "errorMessage");
          final int _cursorIndexOfDetailedLog = CursorUtil.getColumnIndexOrThrow(_cursor, "detailedLog");
          final int _cursorIndexOfConfigName = CursorUtil.getColumnIndexOrThrow(_cursor, "configName");
          final List<SyncLogWithName> _result = new ArrayList<SyncLogWithName>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final SyncLogWithName _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpConfigId;
            _tmpConfigId = _cursor.getLong(_cursorIndexOfConfigId);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final SyncStatus _tmpStatus;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfStatus);
            _tmpStatus = __converters.toSyncStatus(_tmp);
            final int _tmpFilesCopied;
            _tmpFilesCopied = _cursor.getInt(_cursorIndexOfFilesCopied);
            final int _tmpFilesDeleted;
            _tmpFilesDeleted = _cursor.getInt(_cursorIndexOfFilesDeleted);
            final int _tmpFilesSkipped;
            _tmpFilesSkipped = _cursor.getInt(_cursorIndexOfFilesSkipped);
            final int _tmpFilesFailed;
            _tmpFilesFailed = _cursor.getInt(_cursorIndexOfFilesFailed);
            final long _tmpBytesTransferred;
            _tmpBytesTransferred = _cursor.getLong(_cursorIndexOfBytesTransferred);
            final long _tmpDurationMs;
            _tmpDurationMs = _cursor.getLong(_cursorIndexOfDurationMs);
            final String _tmpErrorMessage;
            if (_cursor.isNull(_cursorIndexOfErrorMessage)) {
              _tmpErrorMessage = null;
            } else {
              _tmpErrorMessage = _cursor.getString(_cursorIndexOfErrorMessage);
            }
            final String _tmpDetailedLog;
            if (_cursor.isNull(_cursorIndexOfDetailedLog)) {
              _tmpDetailedLog = null;
            } else {
              _tmpDetailedLog = _cursor.getString(_cursorIndexOfDetailedLog);
            }
            final String _tmpConfigName;
            if (_cursor.isNull(_cursorIndexOfConfigName)) {
              _tmpConfigName = null;
            } else {
              _tmpConfigName = _cursor.getString(_cursorIndexOfConfigName);
            }
            _item = new SyncLogWithName(_tmpId,_tmpConfigId,_tmpConfigName,_tmpTimestamp,_tmpStatus,_tmpFilesCopied,_tmpFilesDeleted,_tmpFilesSkipped,_tmpFilesFailed,_tmpBytesTransferred,_tmpDurationMs,_tmpErrorMessage,_tmpDetailedLog);
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
  public Flow<List<SyncLog>> getRecentLogs(final int limit) {
    final String _sql = "SELECT * FROM sync_logs ORDER BY timestamp DESC LIMIT ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, limit);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"sync_logs"}, new Callable<List<SyncLog>>() {
      @Override
      @NonNull
      public List<SyncLog> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfConfigId = CursorUtil.getColumnIndexOrThrow(_cursor, "configId");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfFilesCopied = CursorUtil.getColumnIndexOrThrow(_cursor, "filesCopied");
          final int _cursorIndexOfFilesDeleted = CursorUtil.getColumnIndexOrThrow(_cursor, "filesDeleted");
          final int _cursorIndexOfFilesSkipped = CursorUtil.getColumnIndexOrThrow(_cursor, "filesSkipped");
          final int _cursorIndexOfFilesFailed = CursorUtil.getColumnIndexOrThrow(_cursor, "filesFailed");
          final int _cursorIndexOfBytesTransferred = CursorUtil.getColumnIndexOrThrow(_cursor, "bytesTransferred");
          final int _cursorIndexOfDurationMs = CursorUtil.getColumnIndexOrThrow(_cursor, "durationMs");
          final int _cursorIndexOfErrorMessage = CursorUtil.getColumnIndexOrThrow(_cursor, "errorMessage");
          final int _cursorIndexOfDetailedLog = CursorUtil.getColumnIndexOrThrow(_cursor, "detailedLog");
          final List<SyncLog> _result = new ArrayList<SyncLog>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final SyncLog _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpConfigId;
            _tmpConfigId = _cursor.getLong(_cursorIndexOfConfigId);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final SyncStatus _tmpStatus;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfStatus);
            _tmpStatus = __converters.toSyncStatus(_tmp);
            final int _tmpFilesCopied;
            _tmpFilesCopied = _cursor.getInt(_cursorIndexOfFilesCopied);
            final int _tmpFilesDeleted;
            _tmpFilesDeleted = _cursor.getInt(_cursorIndexOfFilesDeleted);
            final int _tmpFilesSkipped;
            _tmpFilesSkipped = _cursor.getInt(_cursorIndexOfFilesSkipped);
            final int _tmpFilesFailed;
            _tmpFilesFailed = _cursor.getInt(_cursorIndexOfFilesFailed);
            final long _tmpBytesTransferred;
            _tmpBytesTransferred = _cursor.getLong(_cursorIndexOfBytesTransferred);
            final long _tmpDurationMs;
            _tmpDurationMs = _cursor.getLong(_cursorIndexOfDurationMs);
            final String _tmpErrorMessage;
            if (_cursor.isNull(_cursorIndexOfErrorMessage)) {
              _tmpErrorMessage = null;
            } else {
              _tmpErrorMessage = _cursor.getString(_cursorIndexOfErrorMessage);
            }
            final String _tmpDetailedLog;
            if (_cursor.isNull(_cursorIndexOfDetailedLog)) {
              _tmpDetailedLog = null;
            } else {
              _tmpDetailedLog = _cursor.getString(_cursorIndexOfDetailedLog);
            }
            _item = new SyncLog(_tmpId,_tmpConfigId,_tmpTimestamp,_tmpStatus,_tmpFilesCopied,_tmpFilesDeleted,_tmpFilesSkipped,_tmpFilesFailed,_tmpBytesTransferred,_tmpDurationMs,_tmpErrorMessage,_tmpDetailedLog);
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
  public Flow<List<SyncLog>> getLogsByConfig(final long configId) {
    final String _sql = "SELECT * FROM sync_logs WHERE configId = ? ORDER BY timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, configId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"sync_logs"}, new Callable<List<SyncLog>>() {
      @Override
      @NonNull
      public List<SyncLog> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfConfigId = CursorUtil.getColumnIndexOrThrow(_cursor, "configId");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfFilesCopied = CursorUtil.getColumnIndexOrThrow(_cursor, "filesCopied");
          final int _cursorIndexOfFilesDeleted = CursorUtil.getColumnIndexOrThrow(_cursor, "filesDeleted");
          final int _cursorIndexOfFilesSkipped = CursorUtil.getColumnIndexOrThrow(_cursor, "filesSkipped");
          final int _cursorIndexOfFilesFailed = CursorUtil.getColumnIndexOrThrow(_cursor, "filesFailed");
          final int _cursorIndexOfBytesTransferred = CursorUtil.getColumnIndexOrThrow(_cursor, "bytesTransferred");
          final int _cursorIndexOfDurationMs = CursorUtil.getColumnIndexOrThrow(_cursor, "durationMs");
          final int _cursorIndexOfErrorMessage = CursorUtil.getColumnIndexOrThrow(_cursor, "errorMessage");
          final int _cursorIndexOfDetailedLog = CursorUtil.getColumnIndexOrThrow(_cursor, "detailedLog");
          final List<SyncLog> _result = new ArrayList<SyncLog>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final SyncLog _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpConfigId;
            _tmpConfigId = _cursor.getLong(_cursorIndexOfConfigId);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final SyncStatus _tmpStatus;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfStatus);
            _tmpStatus = __converters.toSyncStatus(_tmp);
            final int _tmpFilesCopied;
            _tmpFilesCopied = _cursor.getInt(_cursorIndexOfFilesCopied);
            final int _tmpFilesDeleted;
            _tmpFilesDeleted = _cursor.getInt(_cursorIndexOfFilesDeleted);
            final int _tmpFilesSkipped;
            _tmpFilesSkipped = _cursor.getInt(_cursorIndexOfFilesSkipped);
            final int _tmpFilesFailed;
            _tmpFilesFailed = _cursor.getInt(_cursorIndexOfFilesFailed);
            final long _tmpBytesTransferred;
            _tmpBytesTransferred = _cursor.getLong(_cursorIndexOfBytesTransferred);
            final long _tmpDurationMs;
            _tmpDurationMs = _cursor.getLong(_cursorIndexOfDurationMs);
            final String _tmpErrorMessage;
            if (_cursor.isNull(_cursorIndexOfErrorMessage)) {
              _tmpErrorMessage = null;
            } else {
              _tmpErrorMessage = _cursor.getString(_cursorIndexOfErrorMessage);
            }
            final String _tmpDetailedLog;
            if (_cursor.isNull(_cursorIndexOfDetailedLog)) {
              _tmpDetailedLog = null;
            } else {
              _tmpDetailedLog = _cursor.getString(_cursorIndexOfDetailedLog);
            }
            _item = new SyncLog(_tmpId,_tmpConfigId,_tmpTimestamp,_tmpStatus,_tmpFilesCopied,_tmpFilesDeleted,_tmpFilesSkipped,_tmpFilesFailed,_tmpBytesTransferred,_tmpDurationMs,_tmpErrorMessage,_tmpDetailedLog);
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
  public Flow<List<SyncLog>> getLogsByStatus(final SyncStatus status) {
    final String _sql = "SELECT * FROM sync_logs WHERE status = ? ORDER BY timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    final String _tmp = __converters.fromSyncStatus(status);
    _statement.bindString(_argIndex, _tmp);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"sync_logs"}, new Callable<List<SyncLog>>() {
      @Override
      @NonNull
      public List<SyncLog> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfConfigId = CursorUtil.getColumnIndexOrThrow(_cursor, "configId");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfFilesCopied = CursorUtil.getColumnIndexOrThrow(_cursor, "filesCopied");
          final int _cursorIndexOfFilesDeleted = CursorUtil.getColumnIndexOrThrow(_cursor, "filesDeleted");
          final int _cursorIndexOfFilesSkipped = CursorUtil.getColumnIndexOrThrow(_cursor, "filesSkipped");
          final int _cursorIndexOfFilesFailed = CursorUtil.getColumnIndexOrThrow(_cursor, "filesFailed");
          final int _cursorIndexOfBytesTransferred = CursorUtil.getColumnIndexOrThrow(_cursor, "bytesTransferred");
          final int _cursorIndexOfDurationMs = CursorUtil.getColumnIndexOrThrow(_cursor, "durationMs");
          final int _cursorIndexOfErrorMessage = CursorUtil.getColumnIndexOrThrow(_cursor, "errorMessage");
          final int _cursorIndexOfDetailedLog = CursorUtil.getColumnIndexOrThrow(_cursor, "detailedLog");
          final List<SyncLog> _result = new ArrayList<SyncLog>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final SyncLog _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpConfigId;
            _tmpConfigId = _cursor.getLong(_cursorIndexOfConfigId);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final SyncStatus _tmpStatus;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfStatus);
            _tmpStatus = __converters.toSyncStatus(_tmp_1);
            final int _tmpFilesCopied;
            _tmpFilesCopied = _cursor.getInt(_cursorIndexOfFilesCopied);
            final int _tmpFilesDeleted;
            _tmpFilesDeleted = _cursor.getInt(_cursorIndexOfFilesDeleted);
            final int _tmpFilesSkipped;
            _tmpFilesSkipped = _cursor.getInt(_cursorIndexOfFilesSkipped);
            final int _tmpFilesFailed;
            _tmpFilesFailed = _cursor.getInt(_cursorIndexOfFilesFailed);
            final long _tmpBytesTransferred;
            _tmpBytesTransferred = _cursor.getLong(_cursorIndexOfBytesTransferred);
            final long _tmpDurationMs;
            _tmpDurationMs = _cursor.getLong(_cursorIndexOfDurationMs);
            final String _tmpErrorMessage;
            if (_cursor.isNull(_cursorIndexOfErrorMessage)) {
              _tmpErrorMessage = null;
            } else {
              _tmpErrorMessage = _cursor.getString(_cursorIndexOfErrorMessage);
            }
            final String _tmpDetailedLog;
            if (_cursor.isNull(_cursorIndexOfDetailedLog)) {
              _tmpDetailedLog = null;
            } else {
              _tmpDetailedLog = _cursor.getString(_cursorIndexOfDetailedLog);
            }
            _item = new SyncLog(_tmpId,_tmpConfigId,_tmpTimestamp,_tmpStatus,_tmpFilesCopied,_tmpFilesDeleted,_tmpFilesSkipped,_tmpFilesFailed,_tmpBytesTransferred,_tmpDurationMs,_tmpErrorMessage,_tmpDetailedLog);
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
  public Flow<List<SyncLog>> getLogsByDateRange(final long startTime, final long endTime) {
    final String _sql = "SELECT * FROM sync_logs WHERE timestamp BETWEEN ? AND ? ORDER BY timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, startTime);
    _argIndex = 2;
    _statement.bindLong(_argIndex, endTime);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"sync_logs"}, new Callable<List<SyncLog>>() {
      @Override
      @NonNull
      public List<SyncLog> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfConfigId = CursorUtil.getColumnIndexOrThrow(_cursor, "configId");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfFilesCopied = CursorUtil.getColumnIndexOrThrow(_cursor, "filesCopied");
          final int _cursorIndexOfFilesDeleted = CursorUtil.getColumnIndexOrThrow(_cursor, "filesDeleted");
          final int _cursorIndexOfFilesSkipped = CursorUtil.getColumnIndexOrThrow(_cursor, "filesSkipped");
          final int _cursorIndexOfFilesFailed = CursorUtil.getColumnIndexOrThrow(_cursor, "filesFailed");
          final int _cursorIndexOfBytesTransferred = CursorUtil.getColumnIndexOrThrow(_cursor, "bytesTransferred");
          final int _cursorIndexOfDurationMs = CursorUtil.getColumnIndexOrThrow(_cursor, "durationMs");
          final int _cursorIndexOfErrorMessage = CursorUtil.getColumnIndexOrThrow(_cursor, "errorMessage");
          final int _cursorIndexOfDetailedLog = CursorUtil.getColumnIndexOrThrow(_cursor, "detailedLog");
          final List<SyncLog> _result = new ArrayList<SyncLog>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final SyncLog _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpConfigId;
            _tmpConfigId = _cursor.getLong(_cursorIndexOfConfigId);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final SyncStatus _tmpStatus;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfStatus);
            _tmpStatus = __converters.toSyncStatus(_tmp);
            final int _tmpFilesCopied;
            _tmpFilesCopied = _cursor.getInt(_cursorIndexOfFilesCopied);
            final int _tmpFilesDeleted;
            _tmpFilesDeleted = _cursor.getInt(_cursorIndexOfFilesDeleted);
            final int _tmpFilesSkipped;
            _tmpFilesSkipped = _cursor.getInt(_cursorIndexOfFilesSkipped);
            final int _tmpFilesFailed;
            _tmpFilesFailed = _cursor.getInt(_cursorIndexOfFilesFailed);
            final long _tmpBytesTransferred;
            _tmpBytesTransferred = _cursor.getLong(_cursorIndexOfBytesTransferred);
            final long _tmpDurationMs;
            _tmpDurationMs = _cursor.getLong(_cursorIndexOfDurationMs);
            final String _tmpErrorMessage;
            if (_cursor.isNull(_cursorIndexOfErrorMessage)) {
              _tmpErrorMessage = null;
            } else {
              _tmpErrorMessage = _cursor.getString(_cursorIndexOfErrorMessage);
            }
            final String _tmpDetailedLog;
            if (_cursor.isNull(_cursorIndexOfDetailedLog)) {
              _tmpDetailedLog = null;
            } else {
              _tmpDetailedLog = _cursor.getString(_cursorIndexOfDetailedLog);
            }
            _item = new SyncLog(_tmpId,_tmpConfigId,_tmpTimestamp,_tmpStatus,_tmpFilesCopied,_tmpFilesDeleted,_tmpFilesSkipped,_tmpFilesFailed,_tmpBytesTransferred,_tmpDurationMs,_tmpErrorMessage,_tmpDetailedLog);
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
  public Object getLogById(final long id, final Continuation<? super SyncLog> $completion) {
    final String _sql = "SELECT * FROM sync_logs WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<SyncLog>() {
      @Override
      @Nullable
      public SyncLog call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfConfigId = CursorUtil.getColumnIndexOrThrow(_cursor, "configId");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfFilesCopied = CursorUtil.getColumnIndexOrThrow(_cursor, "filesCopied");
          final int _cursorIndexOfFilesDeleted = CursorUtil.getColumnIndexOrThrow(_cursor, "filesDeleted");
          final int _cursorIndexOfFilesSkipped = CursorUtil.getColumnIndexOrThrow(_cursor, "filesSkipped");
          final int _cursorIndexOfFilesFailed = CursorUtil.getColumnIndexOrThrow(_cursor, "filesFailed");
          final int _cursorIndexOfBytesTransferred = CursorUtil.getColumnIndexOrThrow(_cursor, "bytesTransferred");
          final int _cursorIndexOfDurationMs = CursorUtil.getColumnIndexOrThrow(_cursor, "durationMs");
          final int _cursorIndexOfErrorMessage = CursorUtil.getColumnIndexOrThrow(_cursor, "errorMessage");
          final int _cursorIndexOfDetailedLog = CursorUtil.getColumnIndexOrThrow(_cursor, "detailedLog");
          final SyncLog _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpConfigId;
            _tmpConfigId = _cursor.getLong(_cursorIndexOfConfigId);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final SyncStatus _tmpStatus;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfStatus);
            _tmpStatus = __converters.toSyncStatus(_tmp);
            final int _tmpFilesCopied;
            _tmpFilesCopied = _cursor.getInt(_cursorIndexOfFilesCopied);
            final int _tmpFilesDeleted;
            _tmpFilesDeleted = _cursor.getInt(_cursorIndexOfFilesDeleted);
            final int _tmpFilesSkipped;
            _tmpFilesSkipped = _cursor.getInt(_cursorIndexOfFilesSkipped);
            final int _tmpFilesFailed;
            _tmpFilesFailed = _cursor.getInt(_cursorIndexOfFilesFailed);
            final long _tmpBytesTransferred;
            _tmpBytesTransferred = _cursor.getLong(_cursorIndexOfBytesTransferred);
            final long _tmpDurationMs;
            _tmpDurationMs = _cursor.getLong(_cursorIndexOfDurationMs);
            final String _tmpErrorMessage;
            if (_cursor.isNull(_cursorIndexOfErrorMessage)) {
              _tmpErrorMessage = null;
            } else {
              _tmpErrorMessage = _cursor.getString(_cursorIndexOfErrorMessage);
            }
            final String _tmpDetailedLog;
            if (_cursor.isNull(_cursorIndexOfDetailedLog)) {
              _tmpDetailedLog = null;
            } else {
              _tmpDetailedLog = _cursor.getString(_cursorIndexOfDetailedLog);
            }
            _result = new SyncLog(_tmpId,_tmpConfigId,_tmpTimestamp,_tmpStatus,_tmpFilesCopied,_tmpFilesDeleted,_tmpFilesSkipped,_tmpFilesFailed,_tmpBytesTransferred,_tmpDurationMs,_tmpErrorMessage,_tmpDetailedLog);
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
  public Object getLogCount(final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM sync_logs";
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
