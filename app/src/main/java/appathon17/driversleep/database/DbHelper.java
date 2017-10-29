package appathon17.driversleep.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import appathon17.driversleep.database.DbContract.EventEntry;
import appathon17.driversleep.database.DbContract.TripEntry;
import appathon17.driversleep.logging.Event;
import appathon17.driversleep.logging.Trip;
import java.util.ArrayList;
import java.util.List;

/**
 * You can use this class to read easily from the database.
 * Created by hyeon on 10/29/2017.
 */

public class DbHelper {
  private SQLiteDatabase wdb;
  private SQLiteDatabase rdb;

  public DbHelper(DbOpenHelper dbOpenHelper) {
    wdb = dbOpenHelper.getWritableDatabase();
    rdb = dbOpenHelper.getReadableDatabase();
  }

  public List<Integer> getAllTripIds() {

    return null;
  }

  public List<Trip> getAllTripInfos() {
    String[] projection = {
        TripEntry.CN_TRIP_ID,
        TripEntry.CN_START_TIME,
        TripEntry.CN_END_TIME
    };

    Cursor cursor = rdb.query(
        TripEntry.TABLE_NAME,                     // The table to query
        projection,                               // The columns to return
        null,                                // The columns for the WHERE clause
        null,                            // The values for the WHERE clause
        null,                                     // don't group the rows
        null,                                     // don't filter by row groups
        null                                 // The sort order
    );

    List<Trip> trips = new ArrayList<>();
    while(cursor.moveToNext()) {
      int itemId = cursor.getInt(cursor.getColumnIndexOrThrow(TripEntry.CN_TRIP_ID));
      long startTime = cursor.getLong(cursor.getColumnIndexOrThrow(TripEntry.CN_START_TIME));
      long endTime = cursor.getLong(cursor.getColumnIndexOrThrow(TripEntry.CN_END_TIME));

      trips.add(new Trip(itemId, startTime, endTime));
    }
    cursor.close();
    return trips;
  }

  public List<Trip> getAllTripsWithEventList() {
    List<Trip> trips = getAllTripInfos();
    for (Trip trip : trips) {
      trip.setEvents(getAllEventsFromTrip(trip.getTripId()));
    }
    return trips;
  }

  public List<Event> getAllEventsFromTrip(int tripId) {
    String[] projection = {
        EventEntry.CN_TIMESTAMP,
        EventEntry.CN_TRIP_ID,
        EventEntry.CN_TYPE,
        EventEntry.CN_LAT,
        EventEntry.CN_LNG
    };

    String selection = EventEntry.CN_TRIP_ID + "=" + tripId;

    Cursor cursor = rdb.query(
        EventEntry.TABLE_NAME,                     // The table to query
        projection,                               // The columns to return
        selection,                                // The columns for the WHERE clause
        null,                            // The values for the WHERE clause
        null,                                     // don't group the rows
        null,                                     // don't filter by row groups
        null                                 // The sort order
    );

    List<Event> events = new ArrayList<>();
    while(cursor.moveToNext()) {
      long time = cursor.getLong(cursor.getColumnIndexOrThrow(EventEntry.CN_TIMESTAMP));
      int id = cursor.getInt(cursor.getColumnIndexOrThrow(EventEntry.CN_TRIP_ID));
      String type = cursor.getString(cursor.getColumnIndexOrThrow(EventEntry.CN_TYPE));
      double lat = cursor.getDouble(cursor.getColumnIndexOrThrow(EventEntry.CN_LAT));
      double lng = cursor.getDouble(cursor.getColumnIndexOrThrow(EventEntry.CN_LNG));

      events.add(new Event(id, time, type, lat, lng));
    }
    cursor.close();
    return events;
  }
}
