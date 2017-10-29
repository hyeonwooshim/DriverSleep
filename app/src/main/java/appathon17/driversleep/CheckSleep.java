package appathon17.driversleep;


import com.google.android.gms.vision.face.Face;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by Eric on 10/28/2017.
 * Class created to check if driver is asleep.
 */

class CheckSleep {
  private Queue<Boolean> history;
  private int numSleep;
  private int numFrames = 6;
  private double proportion = 0.80;

  CheckSleep() {
    history = new LinkedList<>();
    for (int i = 0; i < numFrames; i++) {
      history.add(false); // set up to check history of past 4 frames
    }
    numSleep = 0;
  }

  public void update(Face face) {
    if (face.getIsLeftEyeOpenProbability() <= 0.4 && face.getIsRightEyeOpenProbability() <= 0.4) {
      if (!history.remove()) {
        numSleep++;
      }
      history.add(true);
      return;
    }

    if (history.remove()) {
      numSleep--;
    }
    history.add(false);
  }

  public boolean isSleep() {
    return numSleep >= numFrames * proportion;
  }

  public void clear() {
    history.clear();
    for (int i = 0; i < numFrames; i++){
      history.add(false);
    }
    numSleep = 0;
  }
}
