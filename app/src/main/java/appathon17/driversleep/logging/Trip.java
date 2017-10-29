package appathon17.driversleep.logging;

import java.util.List;

/**
 * Class to hold data.
 * Created by hyeon on 10/29/2017.
 */

public class Trip {
  private int tripId;
  private long startTime;
  private long endTime;
  private double startLat;
  private double startLng;
  private double endLat;
  private double endLng;

  private List<Event> events;

  public Trip(int tripId, long startTime, long endTime, double startLat, double startLng, double endLat,
      double endLng) {
    this.tripId = tripId;
    this.startTime = startTime;
    this.endTime = endTime;
    this.startLat = startLat;
    this.startLng = startLng;
    this.endLat = endLat;
    this.endLng = endLng;
  }

  public Trip(int tripId, long startTime, long endTime) {
    this.tripId = tripId;
    this.startTime = startTime;
    this.endTime = endTime;
  }

  public int getTripId() {
    return tripId;
  }

  public long getStartTime() {
    return startTime;
  }

  public long getEndTime() {
    return endTime;
  }

  public double getStartLat() {
    return startLat;
  }

  public double getStartLng() {
    return startLng;
  }

  public double getEndLat() {
    return endLat;
  }

  public double getEndLng() {
    return endLng;
  }

  public List<Event> getEvents() {
    return events;
  }

  public void setEvents(List<Event> events) {
    this.events = events;
  }

  @Override
  public String toString() {
    return "Trip{" +
        "tripId=" + tripId +
        ", startTime=" + startTime +
        ", endTime=" + endTime +
        '}';
  }
}
