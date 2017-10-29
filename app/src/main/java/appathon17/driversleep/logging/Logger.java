package appathon17.driversleep.logging;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;
import appathon17.driversleep.database.DbContract.EventEntry;
import appathon17.driversleep.database.DbContract.TripEntry;
import appathon17.driversleep.database.DbOpenHelper;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * Functions to log trips.
 * Created by hyeon on 8/14/2017.
 */

public class Logger {
  private static final String TAG = Logger.class.getSimpleName();

  private static final String EVENT_SLEEP = "ASLEEP";
  private static final String EVENT_LOOK_AWAY = "LOOK AWAY";

  private SQLiteDatabase db;
  private int tripID;

  private boolean begun = false;

  /**
   * db should be a writable database.
   * @param db writable database
   * @param tripID id of the trip to log about
   */
  public Logger(@NonNull SQLiteDatabase db, int tripID) {
    this.db  = db;
    this.tripID = tripID;
  }

  private void insertInitialTripInfo() {
    if (!db.isOpen()) return;

    ContentValues values = new ContentValues();
    values.put(TripEntry.CN_TRIP_ID, tripID);
    values.put(TripEntry.CN_START_TIME, System.currentTimeMillis());
    values.put(TripEntry.CN_END_TIME, TripEntry.UNINITIALIZED_END_TIME);

    long newRowId = db.insert(TripEntry.TABLE_NAME, null, values);
  }

  public void begin() {
    if (begun) {
      throw new IllegalStateException("Cannot begin again!");
    }
    insertInitialTripInfo();
    begun = true;
  }

  public void wipeAndStop() {
    if (!begun) {
      throw new IllegalStateException("Cannot stop when logging hasn't even begun!");
    }

    String selection = EventEntry.CN_TRIP_ID + "=" + tripID;
    db.delete(EventEntry.TABLE_NAME, selection, null);

    selection = TripEntry.CN_TRIP_ID + "=" + tripID;
    db.delete(TripEntry.TABLE_NAME, selection, null);

    begun = false;
  }

  public void log(String eventType, double lat, double lng) {
    if (!begun) throw new IllegalStateException("Cannot log when logging hasn't begun!");
    if (!db.isOpen()) return;

    // Create a new map of values, where column names are the keys
    ContentValues values = new ContentValues();
    values.put(EventEntry.CN_TIMESTAMP, System.currentTimeMillis());
    values.put(EventEntry.CN_TRIP_ID, tripID);
    values.put(EventEntry.CN_TYPE, eventType);
    values.put(EventEntry.CN_LAT, lat);
    values.put(EventEntry.CN_LNG, lng);

    long newRowId = db.insert(EventEntry.TABLE_NAME, null, values);
  }

  public void log(String eventType) {
    if (!begun) throw new IllegalStateException("Cannot log when logging hasn't begun!");
    if (!db.isOpen()) return;

    // Create a new map of values, where column names are the keys
    ContentValues values = new ContentValues();
    values.put(EventEntry.CN_TIMESTAMP, System.currentTimeMillis());
    values.put(EventEntry.CN_TRIP_ID, tripID);
    values.put(EventEntry.CN_TYPE, eventType);

    long newRowId = db.insert(EventEntry.TABLE_NAME, null, values);
  }

  public void conclude() {
    if (!begun) {
      throw new IllegalStateException("Cannot conclude when logging hasn't even begun!");
    }

    ContentValues values = new ContentValues();
    values.put(TripEntry.CN_END_TIME, System.currentTimeMillis());

    String selection = TripEntry.CN_TRIP_ID + "=" + tripID;

    int count = db.update(
        TripEntry.TABLE_NAME,
        values,
        selection,
        null);
    Log.d(TAG, count + " trip entries updated.");
  }

  public boolean isBegun() {
    return begun;
  }
}
