package appathon17.driversleep;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.Random;

import appathon17.driversleep.database.DbHelper;
import appathon17.driversleep.database.DbOpenHelper;
import appathon17.driversleep.logging.Trip;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps2);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        DbOpenHelper openHelper = new DbOpenHelper(this);
        DbHelper dbHelper = new DbHelper(openHelper);

        List<Integer> allTripIds = dbHelper.getAllTripIds();
        List<Trip> allTripInfos = dbHelper.getAllTripInfos();
        List<Trip> allTripsWithEvents = dbHelper.getAllTripsWithEventList();
    }
}
