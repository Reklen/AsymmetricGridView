package com.felipecsl.asymmetricgridview.app.widget;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.felipecsl.asymmetricgridview.app.model.DemoItem;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SampleDbAdapter {

  public static final String KEY_TEXT = "text";
  public static final String KEY_ROW_SPAN = "rowspan";
  public static final String KEY_COL_SPAN = "colspan";
  public static final String KEY_ROWID = "_id";
  private static final String TAG = "CountriesDbAdapter";
  private DatabaseHelper dbHelper;
  private SQLiteDatabase database;

  private static final String DATABASE_NAME = "World";
  private static final String SQLITE_TABLE = "Country";
  private static final int DATABASE_VERSION = 3;

  private final Context mCtx;

  private static final String DATABASE_CREATE =
      "CREATE TABLE if not exists " + SQLITE_TABLE + " (" +
      KEY_ROWID + " integer PRIMARY KEY autoincrement," +
      KEY_TEXT + "," + KEY_ROW_SPAN + "," + KEY_COL_SPAN + "," +
      " UNIQUE (" + KEY_TEXT + "));";

  private class DatabaseHelper extends SQLiteOpenHelper {

    DatabaseHelper(Context context) {
      super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override public void onCreate(@NotNull SQLiteDatabase db) {
      Log.w(TAG, DATABASE_CREATE);
      db.execSQL(DATABASE_CREATE);
    }

    @Override public void onUpgrade(@NotNull SQLiteDatabase db, int oldVersion, int newVersion) {
      Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                 + newVersion + ", which will destroy all old data");
      db.execSQL("DROP TABLE IF EXISTS " + SQLITE_TABLE);
      onCreate(db);
    }
  }

  public SampleDbAdapter(Context ctx) {
    this.mCtx = ctx;
  }

  public SampleDbAdapter open() throws SQLException {
    dbHelper = new DatabaseHelper(mCtx);
    database = dbHelper.getWritableDatabase();
    return this;
  }

  public void close() {
    if (dbHelper != null) {
      dbHelper.close();
    }
  }

  public long createItem(String text, int rowSpan, int colSpan) {
    ContentValues initialValues = new ContentValues();
    initialValues.put(KEY_TEXT, text);
    initialValues.put(KEY_ROW_SPAN, rowSpan);
    initialValues.put(KEY_COL_SPAN, colSpan);

    return database.insert(SQLITE_TABLE, null, initialValues);
  }

  public SampleDbAdapter deleteAllData() {
    int doneDelete = database.delete(SQLITE_TABLE, null, null);
    Log.w(TAG, Integer.toString(doneDelete));
    return this;

  }

  public Cursor fetchDataByName(String inputText) throws SQLException {
    Log.w(TAG, inputText);
    Cursor mCursor = null;
    if (inputText == null || inputText.length() == 0) {
      mCursor = database.query(SQLITE_TABLE,
                               new String[]{KEY_ROWID, KEY_TEXT, KEY_ROW_SPAN, KEY_COL_SPAN},
                               null, null, null, null, null);

    } else {
      mCursor = database.query(true, SQLITE_TABLE,
                               new String[]{KEY_ROWID, KEY_TEXT, KEY_ROW_SPAN, KEY_COL_SPAN},
                               KEY_TEXT + " like '%" + inputText + "%'", null, null, null, null,
                               null);
    }
    if (mCursor != null) {
      mCursor.moveToFirst();
    }
    return mCursor;

  }

  public Cursor fetchAllData() {
    Cursor mCursor = database.query(SQLITE_TABLE,
                                    new String[]{KEY_ROWID, KEY_TEXT, KEY_ROW_SPAN, KEY_COL_SPAN},
                                    null, null, null, null, null);

    if (mCursor != null) {
      mCursor.moveToFirst();
    }
    return mCursor;
  }

  public SampleDbAdapter seedDatabase(List<DemoItem> items) {
    for (DemoItem item : items) {
      createItem(String.valueOf(item.getPosition()), item.getRowSpan(), item.getColumnSpan());
    }
    return this;
  }
}
