package appathon17.driversleep;

import static com.google.android.gms.vision.face.FaceDetector.ALL_CLASSIFICATIONS;
import static com.google.android.gms.vision.face.FaceDetector.ALL_LANDMARKS;

import android.Manifest;
import android.Manifest.permission;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import appathon17.driversleep.database.DbHelper;
import appathon17.driversleep.database.DbOpenHelper;
import appathon17.driversleep.logging.Logger;
import appathon17.driversleep.ui.CameraSourcePreview;
import appathon17.driversleep.ui.GraphicOverlay;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.MultiDetector;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import java.io.IOException;

public class MainActivity extends AppCompatActivity
    implements LocationListener, Toolbar.OnMenuItemClickListener {

  private static final String TAG = MainActivity.class.getSimpleName();

  private static final int RC_HANDLE_GMS = 9001;
  // permission request codes need to be < 256
  private static final int PERM_REQ_CODE_CAMERA = 2;
  private static final int PERM_REQ_CODE_LOCATION = 3;
  private static final int PERM_REQ_CODE_ALL = 77;

  private static final String[] REQUIRED_PERMISSIONS = new String[]{
      permission.CAMERA,
      permission.ACCESS_FINE_LOCATION
  };

  private CameraSource mCameraSource = null;
  private CameraSourcePreview mPreview;
  private GraphicOverlay mGraphicOverlay;
  private MediaPlayer mp;

  private FaceTrackerCallback callback;
  private CheckSleep history = new CheckSleep();
  private int tripID = 1;

  // Snippet
  private DbOpenHelper dbOpenHelper;
  private DbHelper dbHelper;
  private Logger logger;

  private LocationManager mLocationManager;
  private Location mLocation;

  private static final long LOCATION_UPDATE_MIN_INTERVAL = 300;
  private static final float LOCATION_UPDATE_MIN_DIST = 0;

  private Toolbar mToolbar;
  private FloatingActionButton mFab;
  private boolean tripRunning = false;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    dbOpenHelper = new DbOpenHelper(this);
    dbHelper = new DbHelper(dbOpenHelper);

    mFab = findViewById(R.id.trip_button);
    mFab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (!checkAllPerms()) {
          requestAllPermissions();
          return;
        }

        if (tripRunning) {
          logger.conclude();
          tripRunning = false;
          mFab.setImageResource(R.drawable.ic_stop_white_24dp);
        } else {
          logger = new Logger(dbOpenHelper.getWritableDatabase(), dbHelper.getMaxTripId() + 1);
          logger.begin();
          tripRunning = true;
          mFab.setImageResource(R.drawable.ic_play_arrow_white_24dp);
        }
      }
    });

    mToolbar = findViewById(R.id.toolbar);
    initToolbar();

    mPreview = findViewById(R.id.preview);
    mGraphicOverlay = findViewById(R.id.faceOverlay);

    // Check all required permissions first!
    if (checkAllPerms()) {
      allPermissionsCheckedAction();
    } else {
      requestAllPermissions();
    }
  }

  private void initToolbar() {
    mToolbar.setTitle("Doze Tracker");
    mToolbar.inflateMenu(R.menu.navigation);
    mToolbar.setOnMenuItemClickListener(this);
  }

  @Override
  public boolean onMenuItemClick(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.nav_item_maps:
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
        return true;
      case R.id.nav_item_charts:
        Intent intent2 = new Intent(this, MapsActivity.class);
        startActivity(intent2);
        return true;
      case R.id.nav_item_settings:

        return true;
    }
    return super.onOptionsItemSelected(item);
  }

  private void allPermissionsCheckedAction() {
    createCameraSource();
    initLocation();
  }

  private void requestAllPermissions() {
    ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, PERM_REQ_CODE_ALL);
  }

  public void goToAnActivity(View view) {
    Intent Intent = new Intent(this, MapsActivity.class);
    startActivity(Intent);
  }

  private boolean checkAllPerms() {
    for (String perm : REQUIRED_PERMISSIONS) {
      if (!checkPerm(perm)) {
        Log.w(TAG, perm + ": permission not granted.");
        return false;
      }
    }
    return true;
  }

  private boolean checkPerm(String perm) {
    return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        == PackageManager.PERMISSION_GRANTED;
  }

  @SuppressWarnings({"MissingPermission"})
  private void initLocation() {
    mLocationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);

    if (mLocationManager == null) {
      Log.e(TAG, "LocationManager turned out to be null!");
      return;
    }

    // getting GPS status
    boolean isGPSEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

    if (!isGPSEnabled) {
      Log.w(TAG, "GPS not enabled on device.");
      AlertDialog.Builder builder = new AlertDialog.Builder(this);
      builder.setMessage(R.string.enable_gps)
          .setPositiveButton("Ok", new OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
              // TODO: actually get them to the settings
            }
          })
          .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
          });
      builder.create().show();
    }

    mLocationManager.requestLocationUpdates(
        LocationManager.GPS_PROVIDER,
        LOCATION_UPDATE_MIN_INTERVAL,
        LOCATION_UPDATE_MIN_DIST, this);

    mLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

    Log.d(TAG, "Location initialization complete.");
  }

  @Override
  public void onLocationChanged(Location location) {
    mLocation = location;
  }

  @Override
  public void onStatusChanged(String s, int i, Bundle bundle) {}

  @Override
  public void onProviderEnabled(String s) {}

  @Override
  public void onProviderDisabled(String s) {
    mLocation = null;
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setMessage(R.string.enable_gps)
        .setPositiveButton("Ok", new OnClickListener() {
          public void onClick(DialogInterface dialog, int id) {
            // TODO: actually get them to the settings
          }
        })
        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int id) {

          }
        });
    builder.create().show();
  }

  interface FaceTrackerCallback {
    void onUpdate(Face item);
    void wavPlayer();
  }

  /**
   * Creates and starts the camera.  Note that this uses a higher resolution in comparison
   * to other detection examples to enable the barcode detector to detect small barcodes
   * at long distances.
   */
  private void createCameraSource() {
    Context context = getApplicationContext();

    // A face detector is created to track faces.  An associated multi-processor instance
    // is set to receive the face detection results, track the faces, and maintain graphics for
    // each face on screen.  The factory is used by the multi-processor to create a separate
    // tracker instance for each face.
    FaceDetector faceDetector = new FaceDetector.Builder(context)
        .setProminentFaceOnly(true)
        .setLandmarkType(ALL_LANDMARKS) // Get all landmarks (track eyes)
        .setClassificationType(ALL_CLASSIFICATIONS) // Allow probabilities to be computed
        .build();

    callback = new FaceTrackerCallback() {
      @Override
      public void onUpdate(Face item) {
        history.update(item);
        if(history.isSleep() && !mp.isPlaying() && logger.isBegun()) {
          mp.start();
          history.clear();
          if (mLocation != null) {
            Log.d(TAG, "mLocation: " + mLocation);
            if (logger != null && logger.isBegun()) {
              logger.log("Sleep", mLocation.getLatitude(), mLocation.getLongitude());
            }
          } else {
            Log.d(TAG, "mLocation: NULL");
            if (logger != null && logger.isBegun()) {
              logger.log("Sleep");
            }
          }
        }
      }
      public void wavPlayer() {
        if(!mp.isPlaying() && logger.isBegun()) {
          mp.start();
        }
      }
    };

    mp = MediaPlayer.create(context, R.raw.buzzer);
    FaceTrackerFactory faceFactory = new FaceTrackerFactory(mGraphicOverlay, callback);
    faceDetector.setProcessor(new MultiProcessor.Builder<>(faceFactory).build());

    // A multi-detector groups the two detectors together as one detector.  All images received
    // by this detector from the camera will be sent to each of the underlying detectors, which
    // will each do face and barcode detection, respectively.  The detection results from each
    // are then sent to associated tracker instances which maintain per-item graphics on the
    // screen.
    MultiDetector multiDetector = new MultiDetector.Builder()
        .add(faceDetector)
        .build();

    if (!multiDetector.isOperational()) {
      // Note: The first time that an app using the barcode or face API is installed on a
      // device, GMS will download a native libraries to the device in order to do detection.
      // Usually this completes before the app is run for the first time.  But if that
      // download has not yet completed, then the above call will not detect any barcodes
      // and/or faces.
      //
      // isOperational() can be used to check if the required native libraries are currently
      // available.  The detectors will automatically become operational once the library
      // downloads complete on device.
      Log.w(TAG, "Detector dependencies are not yet available.");

      // Check for low storage.  If there is low storage, the native library will not be
      // downloaded, so detection will not become operational.
      IntentFilter lowStorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
      boolean hasLowStorage = registerReceiver(null, lowStorageFilter) != null;

      if (hasLowStorage) {
        Toast.makeText(this, R.string.low_storage_error, Toast.LENGTH_LONG).show();
        Log.w(TAG, getString(R.string.low_storage_error));
      }
    }

    // Creates and starts the camera.  Note that this uses a higher resolution in comparison
    // to other detection examples to enable the barcode detector to detect small barcodes
    // at long distances.
    mCameraSource = new CameraSource.Builder(getApplicationContext(), multiDetector)
        .setFacing(CameraSource.CAMERA_FACING_FRONT)
        .setRequestedPreviewSize(1600, 1024)
        .setRequestedFps(15.0f)
        .build();
  }

  /**
   * Restarts the camera.
   */
  @Override
  protected void onResume() {
    super.onResume();
    startCameraSource();
  }

  /**
   * Stops the camera.
   */
  @Override
  protected void onPause() {
    super.onPause();
    mPreview.stop();
  }

  /**
   * Releases the resources associated with the camera source, the associated detectors, and the
   * rest of the processing pipeline.
   */
  @Override
  protected void onDestroy() {
    super.onDestroy();
    if (logger != null && logger.isBegun()) logger.conclude();
    if (dbOpenHelper != null) dbOpenHelper.close();
    if (mCameraSource != null) {
      mCameraSource.release();
    }
  }


  /**
   * Callback for the result from requesting permissions. This method
   * is invoked for every call on {@link #requestPermissions(String[], int)}.
   * <p>
   * <strong>Note:</strong> It is possible that the permissions request interaction
   * with the user is interrupted. In this case you will receive empty permissions
   * and results arrays which should be treated as a cancellation.
   * </p>
   *
   * @param requestCode  The request code passed in {@link #requestPermissions(String[], int)}.
   * @param permissions  The requested permissions. Never null.
   * @param grantResults The grant results for the corresponding permissions
   *                     which is either {@link PackageManager#PERMISSION_GRANTED}
   *                     or {@link PackageManager#PERMISSION_DENIED}. Never null.
   * @see #requestPermissions(String[], int)
   */
  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    switch (requestCode) {
      case PERM_REQ_CODE_ALL:
        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          boolean good = true;
          for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
              good = false;
              break;
            }
          }
          if (!good) {
            Log.e(TAG, "Not all required permissions granted. Requesting again.");
            requestAllPermissions();
          } else {
            allPermissionsCheckedAction();
          }
        }
        break;
      case PERM_REQ_CODE_CAMERA:
        break;
      case PERM_REQ_CODE_LOCATION:
        break;
    }
  }
  /**
   * Starts or restarts the camera source, if it exists.  If the camera source doesn't exist yet
   * (e.g., because onResume was called before the camera source was created), this will be called
   * again when the camera source is created.
   */
  private void startCameraSource() {

    // check that the device has play services available.
    int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
        getApplicationContext());
    if (code != ConnectionResult.SUCCESS) {
      Dialog dlg =
          GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS);
      dlg.show();
    }

    if (mCameraSource != null) {
      try {
        mPreview.start(mCameraSource, mGraphicOverlay);
      } catch (IOException e) {
        Log.e(TAG, "Unable to start camera source.", e);
        mCameraSource.release();
        mCameraSource = null;
      }
    }
  }
}
