package com.example.symptomease;

import android.database.Cursor;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings({"unchecked", "deprecation"})
public final class SavedSymptomsDAO_Impl implements SavedSymptomsDAO {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<SavedSymptomEntity> __insertionAdapterOfSavedSymptomEntity;

  private final EntityDeletionOrUpdateAdapter<SavedSymptomEntity> __updateAdapterOfSavedSymptomEntity;

  public SavedSymptomsDAO_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfSavedSymptomEntity = new EntityInsertionAdapter<SavedSymptomEntity>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR REPLACE INTO `symptom_table` (`id`,`symptom_name`,`symptom_description`) VALUES (nullif(?, 0),?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, SavedSymptomEntity value) {
        stmt.bindLong(1, value.getId());
        if (value.getSymptomName() == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.getSymptomName());
        }
        if (value.getSymptomDescription() == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.getSymptomDescription());
        }
      }
    };
    this.__updateAdapterOfSavedSymptomEntity = new EntityDeletionOrUpdateAdapter<SavedSymptomEntity>(__db) {
      @Override
      public String createQuery() {
        return "UPDATE OR ABORT `symptom_table` SET `id` = ?,`symptom_name` = ?,`symptom_description` = ? WHERE `id` = ?";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, SavedSymptomEntity value) {
        stmt.bindLong(1, value.getId());
        if (value.getSymptomName() == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.getSymptomName());
        }
        if (value.getSymptomDescription() == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.getSymptomDescription());
        }
        stmt.bindLong(4, value.getId());
      }
    };
  }

  @Override
  public void insertSavedSymptom(final SavedSymptomEntity saved) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfSavedSymptomEntity.insert(saved);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void updateSavedSymptom(final SavedSymptomEntity saved) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __updateAdapterOfSavedSymptomEntity.handle(saved);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public List<SavedSymptomEntity> getSavedSymptoms() {
    final String _sql = "SELECT * FROM symptom_table";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfSymptomName = CursorUtil.getColumnIndexOrThrow(_cursor, "symptom_name");
      final int _cursorIndexOfSymptomDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "symptom_description");
      final List<SavedSymptomEntity> _result = new ArrayList<SavedSymptomEntity>(_cursor.getCount());
      while(_cursor.moveToNext()) {
        final SavedSymptomEntity _item;
        final int _tmpId;
        _tmpId = _cursor.getInt(_cursorIndexOfId);
        final String _tmpSymptomName;
        if (_cursor.isNull(_cursorIndexOfSymptomName)) {
          _tmpSymptomName = null;
        } else {
          _tmpSymptomName = _cursor.getString(_cursorIndexOfSymptomName);
        }
        final String _tmpSymptomDescription;
        if (_cursor.isNull(_cursorIndexOfSymptomDescription)) {
          _tmpSymptomDescription = null;
        } else {
          _tmpSymptomDescription = _cursor.getString(_cursorIndexOfSymptomDescription);
        }
        _item = new SavedSymptomEntity(_tmpId,_tmpSymptomName,_tmpSymptomDescription);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
