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
import java.util.Random;

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

  public void randomMarkers(View view) {
    Random n = new Random();
    int x = n.nextInt(90) - 30;
    int y = n.nextInt(90) - 30;
    mMap.addMarker(new MarkerOptions().position(new LatLng(x, y)));
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

    LatLng gatech = new LatLng(33.7756, -84.3963); //33.7756° N, 84.3963° W
    //mMap.addMarker(new MarkerOptions().position(gatech).title("Marker in Sydney"));
    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(gatech, 15));
  }


}
