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


    CheckSleep() {
        history = new LinkedList<>();
        for (int i = 0; i < 4 ; i++) {
            history.add(false); // set up to check history of past 4 frames
        }
        numSleep = 0;
    }

    public void update(Face face) {
        if (face.getIsLeftEyeOpenProbability() <= 0.55 && face.getIsRightEyeOpenProbability() <= 0.55) {
            System.out.println(face.getIsLeftEyeOpenProbability());
            if (!history.remove()) {
                numSleep++;
            }
            history.add(true);
            return;
        }
        if(history.remove()) {
            numSleep--;
        }
        history.add(false);
    }

    public boolean isSleep() {
        return numSleep >= 3; // if 3 of the past 4 frames have the eyes closed, return true
    }
}
