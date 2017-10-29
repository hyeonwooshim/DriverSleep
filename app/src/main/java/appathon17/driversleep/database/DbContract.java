package appathon17.driversleep.database;

import android.provider.BaseColumns;

/**
 * Database contract class to keep track of names/definitions.
 * Created by hyeon on 10/28/2017.
 */

public final class DbContract {
  private static final String TAG = DbContract.class.getSimpleName();

  private DbContract() {} // This class should not be instantiated

  /* Inner class that defines the table contents */
  public static class EventEntry implements BaseColumns {
    public static final String TABLE_NAME = "event";
    public static final String CN_TIMESTAMP = "time";
    public static final String CN_TRIP_ID = "trip_id";
    public static final String CN_TYPE = "type";
    public static final String CN_LAT = "lat";
    public static final String CN_LNG = "lng";
  }

  public static final String SQL_CREATE_EVENT_ENTRY =
      "CREATE TABLE " + EventEntry.TABLE_NAME + " ("
          + EventEntry.CN_TIMESTAMP + " INTEGER NOT NULL,"
          + EventEntry.CN_TRIP_ID + " INTEGER NOT NULL,"
          + EventEntry.CN_TYPE + " TEXT NOT NULL,"
          + EventEntry.CN_LAT + " REAL,"
          + EventEntry.CN_LNG + " REAL,"
          + "PRIMARY KEY (" + EventEntry.CN_TIMESTAMP + ", "
              + EventEntry.CN_TRIP_ID + ", " + EventEntry.CN_TYPE + "),"
          + "FOREIGN KEY(" + EventEntry.CN_TRIP_ID + ") REFERENCES "
              + TripEntry.TABLE_NAME + "(" + TripEntry.CN_TRIP_ID + ")"
      + ")";

  public static final String SQL_DELETE_EVENT_ENTRIES =
      "DROP TABLE IF EXISTS " + EventEntry.TABLE_NAME;

  /* Inner class that defines the table contents */
  public static class TripEntry implements BaseColumns {
    public static final String TABLE_NAME = "trip";
    public static final String CN_TRIP_ID = "trip_id";
    public static final String CN_START_TIME = "start_time";
    public static final String CN_END_TIME = "end_time";

    public static final long UNINITIALIZED_END_TIME = -1;
  }

  public static final String SQL_CREATE_TRIP_ENTRY =
      "CREATE TABLE " + TripEntry.TABLE_NAME + " ("
          + TripEntry.CN_TRIP_ID + " INTEGER NOT NULL,"
          + TripEntry.CN_START_TIME + " INTEGER NOT NULL,"
          + TripEntry.CN_END_TIME + " INTEGER NOT NULL,"
          + "PRIMARY KEY (" + TripEntry.CN_TRIP_ID + ")"
      + ")";

  public static final String SQL_DELETE_TRIP_ENTRIES =
      "DROP TABLE IF EXISTS " + TripEntry.TABLE_NAME;
}
