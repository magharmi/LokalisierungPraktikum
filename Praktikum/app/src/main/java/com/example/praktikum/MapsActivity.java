package com.example.praktikum;

import androidx.fragment.app.FragmentActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Bundle b = this.getIntent().getExtras();
        latitude = b.getDoubleArray("Latitude");
        longitude = b.getDoubleArray("Longitude");
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
        LatLng latLng;
        List <LatLng> position = new ArrayList<LatLng>();
        Polyline polyline;

        //LatLng currentPosition = new LatLng(latitude, longitude);
        //mMap.addCircle(new CircleOptions().center(currentPosition).radius(2.0).strokeColor(Color.RED).fillColor(Color.GREEN));
        //CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(currentPosition, 17);
        //mMap.animateCamera(cameraUpdate);

        for(int i = 0; i < latitude.length; i++){
            latLng = new LatLng(latitude[i], longitude[i]);
            Log.e("LatLng", latLng.latitude+"");
            Log.e("LatLng", latLng.longitude+"");
            position.add(latLng);
        }
        polyline = mMap.addPolyline(new PolylineOptions().addAll(position).width(5).color(Color.RED));

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(polyline.getPoints().get(0), 17);
        mMap.animateCamera(cameraUpdate);
    }
}
