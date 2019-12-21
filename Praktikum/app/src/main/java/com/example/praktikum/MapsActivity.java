package com.example.praktikum;

import androidx.fragment.app.FragmentActivity;

import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    double[] latitude, longitude;
    ArrayList<Location> gbsData, interpolationListeSM, interpolationListeME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Bundle b = this.getIntent().getExtras();
        /*
        latitude = b.getDoubleArray("Latitude");
        longitude = b.getDoubleArray("Longitude");
        interpolationListe = b.getParcelableArrayList("InterpolationListe");
        */
        gbsData = b.getParcelableArrayList("GPSData");
        interpolationListeSM = b.getParcelableArrayList("interpolationListeSM");
        interpolationListeME = b.getParcelableArrayList("interpolationListeME");
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
        LatLng latLng, latLng_1, gbsLatLang = null;
        List <LatLng> position = new ArrayList<LatLng>();
        List <LatLng> position_1 = new ArrayList<LatLng>();
        List <LatLng> interpolationPosition = new ArrayList<LatLng>();
        Polyline polyline, polyline_1;

        for(int i = 0; i < interpolationListeSM.size(); i++){
            latLng = new LatLng(interpolationListeSM.get(i).getLatitude(), interpolationListeSM.get(i).getLongitude());
            position.add(latLng);
        }
        for(int i = 0; i < interpolationListeME.size(); i++){
            latLng_1 = new LatLng(interpolationListeME.get(i).getLatitude(), interpolationListeME.get(i).getLongitude());
            position.add(latLng_1);
        }

        for(int i = 0; i < gbsData.size(); i++){
            gbsLatLang = new LatLng(gbsData.get(i).getLatitude(), gbsData.get(i).getLongitude());
            position_1.add(gbsLatLang);
        }


        polyline = mMap.addPolyline(new PolylineOptions().addAll(position).width(5).color(Color.RED));
        polyline_1 = mMap.addPolyline(new PolylineOptions().addAll(position_1).width(5).color(Color.BLUE));

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(polyline.getPoints().get(0), 17);
        mMap.animateCamera(cameraUpdate);
    }
}
