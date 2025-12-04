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
import com.mybackup.smbsync.data.model.SmbProtocol;
import com.mybackup.smbsync.data.model.SmbServer;
import java.lang.Class;
import java.lang.Exception;
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
public final class SmbServerDao_Impl implements SmbServerDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<SmbServer> __insertionAdapterOfSmbServer;

  private final Converters __converters = new Converters();

  private final EntityDeletionOrUpdateAdapter<SmbServer> __deletionAdapterOfSmbServer;

  private final EntityDeletionOrUpdateAdapter<SmbServer> __updateAdapterOfSmbServer;

  private final SharedSQLiteStatement __preparedStmtOfUpdateLastConnected;

  public SmbServerDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfSmbServer = new EntityInsertionAdapter<SmbServer>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `smb_servers` (`id`,`name`,`address`,`port`,`protocol`,`username`,`encryptedPassword`,`domain`,`createdAt`,`lastConnectedAt`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final SmbServer entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindString(3, entity.getAddress());
        statement.bindLong(4, entity.getPort());
        final String _tmp = __converters.fromSmbProtocol(entity.getProtocol());
        statement.bindString(5, _tmp);
        statement.bindString(6, entity.getUsername());
        statement.bindString(7, entity.getEncryptedPassword());
        if (entity.getDomain() == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.getDomain());
        }
        statement.bindLong(9, entity.getCreatedAt());
        if (entity.getLastConnectedAt() == null) {
          statement.bindNull(10);
        } else {
          statement.bindLong(10, entity.getLastConnectedAt());
        }
      }
    };
    this.__deletionAdapterOfSmbServer = new EntityDeletionOrUpdateAdapter<SmbServer>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `smb_servers` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final SmbServer entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfSmbServer = new EntityDeletionOrUpdateAdapter<SmbServer>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `smb_servers` SET `id` = ?,`name` = ?,`address` = ?,`port` = ?,`protocol` = ?,`username` = ?,`encryptedPassword` = ?,`domain` = ?,`createdAt` = ?,`lastConnectedAt` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final SmbServer entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindString(3, entity.getAddress());
        statement.bindLong(4, entity.getPort());
        final String _tmp = __converters.fromSmbProtocol(entity.getProtocol());
        statement.bindString(5, _tmp);
        statement.bindString(6, entity.getUsername());
        statement.bindString(7, entity.getEncryptedPassword());
        if (entity.getDomain() == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.getDomain());
        }
        statement.bindLong(9, entity.getCreatedAt());
        if (entity.getLastConnectedAt() == null) {
          statement.bindNull(10);
        } else {
          statement.bindLong(10, entity.getLastConnectedAt());
        }
        statement.bindLong(11, entity.getId());
      }
    };
    this.__preparedStmtOfUpdateLastConnected = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE smb_servers SET lastConnectedAt = ? WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertServer(final SmbServer server, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfSmbServer.insertAndReturnId(server);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteServer(final SmbServer server, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfSmbServer.handle(server);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateServer(final SmbServer server, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfSmbServer.handle(server);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateLastConnected(final long id, final long timestamp,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateLastConnected.acquire();
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
          __preparedStmtOfUpdateLastConnected.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<SmbServer>> getAllServers() {
    final String _sql = "SELECT * FROM smb_servers ORDER BY name ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"smb_servers"}, new Callable<List<SmbServer>>() {
      @Override
      @NonNull
      public List<SmbServer> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "address");
          final int _cursorIndexOfPort = CursorUtil.getColumnIndexOrThrow(_cursor, "port");
          final int _cursorIndexOfProtocol = CursorUtil.getColumnIndexOrThrow(_cursor, "protocol");
          final int _cursorIndexOfUsername = CursorUtil.getColumnIndexOrThrow(_cursor, "username");
          final int _cursorIndexOfEncryptedPassword = CursorUtil.getColumnIndexOrThrow(_cursor, "encryptedPassword");
          final int _cursorIndexOfDomain = CursorUtil.getColumnIndexOrThrow(_cursor, "domain");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfLastConnectedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastConnectedAt");
          final List<SmbServer> _result = new ArrayList<SmbServer>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final SmbServer _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpAddress;
            _tmpAddress = _cursor.getString(_cursorIndexOfAddress);
            final int _tmpPort;
            _tmpPort = _cursor.getInt(_cursorIndexOfPort);
            final SmbProtocol _tmpProtocol;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfProtocol);
            _tmpProtocol = __converters.toSmbProtocol(_tmp);
            final String _tmpUsername;
            _tmpUsername = _cursor.getString(_cursorIndexOfUsername);
            final String _tmpEncryptedPassword;
            _tmpEncryptedPassword = _cursor.getString(_cursorIndexOfEncryptedPassword);
            final String _tmpDomain;
            if (_cursor.isNull(_cursorIndexOfDomain)) {
              _tmpDomain = null;
            } else {
              _tmpDomain = _cursor.getString(_cursorIndexOfDomain);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpLastConnectedAt;
            if (_cursor.isNull(_cursorIndexOfLastConnectedAt)) {
              _tmpLastConnectedAt = null;
            } else {
              _tmpLastConnectedAt = _cursor.getLong(_cursorIndexOfLastConnectedAt);
            }
            _item = new SmbServer(_tmpId,_tmpName,_tmpAddress,_tmpPort,_tmpProtocol,_tmpUsername,_tmpEncryptedPassword,_tmpDomain,_tmpCreatedAt,_tmpLastConnectedAt);
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
  public Object getServerById(final long id, final Continuation<? super SmbServer> $completion) {
    final String _sql = "SELECT * FROM smb_servers WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<SmbServer>() {
      @Override
      @Nullable
      public SmbServer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "address");
          final int _cursorIndexOfPort = CursorUtil.getColumnIndexOrThrow(_cursor, "port");
          final int _cursorIndexOfProtocol = CursorUtil.getColumnIndexOrThrow(_cursor, "protocol");
          final int _cursorIndexOfUsername = CursorUtil.getColumnIndexOrThrow(_cursor, "username");
          final int _cursorIndexOfEncryptedPassword = CursorUtil.getColumnIndexOrThrow(_cursor, "encryptedPassword");
          final int _cursorIndexOfDomain = CursorUtil.getColumnIndexOrThrow(_cursor, "domain");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfLastConnectedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastConnectedAt");
          final SmbServer _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpAddress;
            _tmpAddress = _cursor.getString(_cursorIndexOfAddress);
            final int _tmpPort;
            _tmpPort = _cursor.getInt(_cursorIndexOfPort);
            final SmbProtocol _tmpProtocol;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfProtocol);
            _tmpProtocol = __converters.toSmbProtocol(_tmp);
            final String _tmpUsername;
            _tmpUsername = _cursor.getString(_cursorIndexOfUsername);
            final String _tmpEncryptedPassword;
            _tmpEncryptedPassword = _cursor.getString(_cursorIndexOfEncryptedPassword);
            final String _tmpDomain;
            if (_cursor.isNull(_cursorIndexOfDomain)) {
              _tmpDomain = null;
            } else {
              _tmpDomain = _cursor.getString(_cursorIndexOfDomain);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpLastConnectedAt;
            if (_cursor.isNull(_cursorIndexOfLastConnectedAt)) {
              _tmpLastConnectedAt = null;
            } else {
              _tmpLastConnectedAt = _cursor.getLong(_cursorIndexOfLastConnectedAt);
            }
            _result = new SmbServer(_tmpId,_tmpName,_tmpAddress,_tmpPort,_tmpProtocol,_tmpUsername,_tmpEncryptedPassword,_tmpDomain,_tmpCreatedAt,_tmpLastConnectedAt);
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
