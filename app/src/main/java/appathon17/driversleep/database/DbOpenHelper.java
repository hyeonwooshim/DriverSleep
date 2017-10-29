package appathon17.driversleep.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Helper to open new database for logging data
 * Created by hyeon on 10/28/2017.
 */

public class DbOpenHelper extends SQLiteOpenHelper {
  public static final int DATABASE_VERSION = 1;
  public static final String DATABASE_NAME = "local_db.db";

  public DbOpenHelper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    db.execSQL(DbContract.SQL_CREATE_TRIP_ENTRY);
    db.execSQL(DbContract.SQL_CREATE_EVENT_ENTRY);
  }

  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    // This database is only a cache for online data, so its upgrade policy is
    // to simply to discard the data and start over
    db.execSQL(DbContract.SQL_DELETE_TRIP_ENTRIES);
    db.execSQL(DbContract.SQL_DELETE_EVENT_ENTRIES);
    onCreate(db);
  }
  public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    onUpgrade(db, oldVersion, newVersion);
  }
}
