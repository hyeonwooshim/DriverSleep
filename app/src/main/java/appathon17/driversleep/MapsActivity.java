package appathon17.driversleep;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.Random;

import appathon17.driversleep.database.DbHelper;
import appathon17.driversleep.database.DbOpenHelper;
import appathon17.driversleep.logging.Trip;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

  private GoogleMap mMap;
  private Toolbar mToolbar;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_maps2);
    // Obtain the SupportMapFragment and get notified when the map is ready to be used.
    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
        .findFragmentById(R.id.map);
    mapFragment.getMapAsync(this);

    mToolbar = findViewById(R.id.toolbar);
    initToolbar();
  }

  private void initToolbar() {
    setSupportActionBar(mToolbar);
    mToolbar.setTitle("History Map");
    //mToolbar.setNavigationIcon(R.drawable.ic_layers);
    if (getSupportActionBar() != null) {
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      getSupportActionBar().setDisplayShowHomeEnabled(true);
    }
    mToolbar.setNavigationOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        onBackPressed();
      }
    });
  }

  public void goTo(View view) {
    Intent Intent = new Intent(this, MainActivity.class);
    startActivity(Intent);
  }

  public void clearingMap(View view) {
    if (mMap != null) {
      mMap.clear();
    }
  }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Sydney and move the camera
        LatLng tech = new LatLng(33.7756, -84.3963);
        mMap.addMarker(new MarkerOptions().position(tech).title("GA TECH"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(tech, 15));

        DbOpenHelper openHelper = new DbOpenHelper(this);
        DbHelper dbHelper = new DbHelper(openHelper);

        List<Integer> allTripIds = dbHelper.getAllTripIds();
        List<Trip> allTripInfos = dbHelper.getAllTripInfos();
        List<Trip> allTripsWithEvents = dbHelper.getAllTripsWithEventList();
    }

  public void randomMarkers(View view) {
    Random n = new Random();
    double x = n.nextDouble() * 0.04;
    double y = n.nextDouble() * 0.04;
    x = x+33.7756 - 0.02;
    y = y-84.3963 - 0.02;
    mMap.addMarker(new MarkerOptions().position(new LatLng(x, y)));
  }

}
