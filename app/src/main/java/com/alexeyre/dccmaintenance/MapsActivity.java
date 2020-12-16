package com.alexeyre.dccmaintenance;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.alexeyre.dccmaintenance.Constants.COORDINATES;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private float MAP_ZOOM = 12;
    private ArrayList<TrafficLightModel> allTrafficLightsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        initComponents();
    }

    /**
     * Setup all functionality
     */
    private void initComponents() {
        //Setup Map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

    }

    /**
     * This will render all locations on the map each time it is called
     */
    private void createMapMarkers() {
        if (mMap != null) {
            mMap.clear(); // Clear any old markers
            for (TrafficLightModel trafficLightModel : allTrafficLightsList) {
               mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(trafficLightModel.getlatitude()), Double.parseDouble(trafficLightModel.getlongitude()))).title(trafficLightModel.getname()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            }


        }
    }

    private void getData() {
        //Get Coordinates from Firebase
        //This will trigger the onDataChange once there is a live change in the database under the coordinates node
        FirebaseDatabase.getInstance().getReference().child(COORDINATES).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allTrafficLightsList.clear();

                for (DataSnapshot trafficLightDataSnapshot : snapshot.getChildren()) {
                    TrafficLightModel newTrafficLightObject = trafficLightDataSnapshot.getValue(TrafficLightModel.class);
                    allTrafficLightsList.add(newTrafficLightObject);
                }

                createMapMarkers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(53.3498, -6.2603), MAP_ZOOM));
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                showMarkerDetails(marker);
                return false;
            }
        });
        getData();
    }

    private void showMarkerDetails(Marker marker) {
        Toast.makeText(this, marker.getTitle(), Toast.LENGTH_SHORT).show();
    }
}