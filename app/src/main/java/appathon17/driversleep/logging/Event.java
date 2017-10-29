package appathon17.driversleep.logging;

/**
 * Class to hold data.
 * Created by hyeon on 10/29/2017.
 */

public class Event {
  private int tripId;
  private long time;
  private String type;
  private Double lat;
  private Double lng;

  public Event(int tripId, long time, String type, Double lat, Double lng) {
    this.tripId = tripId;
    this.time = time;
    this.type = type;
    this.lat = lat;
    this.lng = lng;
  }

  public int getTripId() {
    return tripId;
  }

  public long getTime() {
    return time;
  }

  public String getType() {
    return type;
  }

  public Double getLat() {
    return lat;
  }

  public Double getLng() {
    return lng;
  }

  @Override
  public String toString() {
    return "Event{" +
        "tripId=" + tripId +
        ", time=" + time +
        ", type='" + type + '\'' +
        ", lat=" + lat +
        ", lng=" + lng +
        '}';
  }
}
