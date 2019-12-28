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
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    double[] latitude, longitude;
    ArrayList<Location> gbsData, interpolationListeSM, interpolationListeME;
    int counter = 0;
    static ArrayList<ArrayList<Double>> fehlerListe = new ArrayList<>();

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
    LatLng latLng;
    Location a = null;
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng latLng_1;
        LatLng gbsLatLang = null;
        List <LatLng> position = new ArrayList<LatLng>();
        List <LatLng> position_1 = new ArrayList<LatLng>();
        List <LatLng> interpolationPosition = new ArrayList<LatLng>();
        Polyline polyline, polyline_1;


        /*
        for(int i = 0; i < interpolationListeME.size(); i++){
            latLng_1 = new LatLng(interpolationListeME.get(i).getLatitude(), interpolationListeME.get(i).getLongitude());
            position.add(latLng_1);
        }
*/
        for(int i = 0; i < gbsData.size(); i++){
            gbsLatLang = new LatLng(gbsData.get(i).getLatitude(), gbsData.get(i).getLongitude());
            position_1.add(gbsLatLang);
        }
        ArrayList<LatLng> z = new ArrayList<>();
        ArrayList<Double> fehler = new ArrayList<>();
        interpolationListeME.stream().forEach(j ->{
            interpolationListeSM.add(j);
        });
        interpolationListeSM.stream().forEach(x->{
            latLng = new LatLng(x.getLatitude(), x.getLongitude());
            position.add(latLng);
        });
        counter = 0;
        interpolationListeSM.stream() .forEachOrdered(x->{
            gbsData.stream().forEachOrdered(j->{
                    if(x.getTime() == j.getTime()){
                        counter++;
                        a = j;
                        return ;
                    }
                    else if(x.getTime() < j.getTime() +2 && x.getTime() > j.getTime() -2 && counter != 1){
                        counter++;
                        a = j;
                        return ;
                    }
                });
                if(a != null && counter !=0){
                    mMap.addPolyline(new PolylineOptions().add(new LatLng(x.getLatitude(),x.getLongitude())).add(new LatLng(a.getLatitude(),a.getLongitude())).width(5).color(Color.YELLOW));
                    fehler.add((double)a.distanceTo(x));
                    counter = 0;
                }
        });
        fehlerListe.add(fehler);
        /*
        gbsData.stream().forEach( x->{
            interpolationListeSM.stream().forEach(j ->{
               if(x.getTime() < j.getTime() +1 && x.getTime() > j.getTime() -2){
                   counter++;
                   LatLng gps = new LatLng(x.getLatitude(),x.getLongitude());
                   LatLng gt = new LatLng(j.getLatitude(),j.getLongitude());
                   Location a = new Location("gps");
                   a.setLatitude(x.getLatitude());
                   a.setLongitude(x.getLongitude());
                   Location b = new Location("gt");
                   b.setLatitude(j.getLatitude());
                   b.setLongitude(j.getLongitude());
                   mMap.addPolyline(new PolylineOptions().add(gps).add(gt).width(5).color(Color.YELLOW));
                   fehler.add((double)a.distanceTo(b));
               }
            });
            interpolationListeME.stream().forEach(j ->{
               if(x.getTime() < j.getTime() +1 && x.getTime() > j.getTime() -1){
                   counter++;
                   LatLng gps = new LatLng(x.getLatitude(),x.getLongitude());
                   LatLng gt = new LatLng(j.getLatitude(),j.getLongitude());
                   Location a = new Location("gps");
                   a.setLatitude(x.getLatitude());
                   a.setLongitude(x.getLongitude());
                   Location b = new Location("gt");
                   b.setLatitude(j.getLatitude());
                   b.setLongitude(j.getLongitude());
                   mMap.addPolyline(new PolylineOptions().add(gps).add(gt).width(5).color(Color.YELLOW));
                   fehler.add((double)a.distanceTo(b));
               }
            });
        });
        fehlerListe.add(fehler);
*/

        polyline = mMap.addPolyline(new PolylineOptions().addAll(position).width(5).color(Color.RED));
        polyline_1 = mMap.addPolyline(new PolylineOptions().addAll(position_1).width(5).color(Color.BLUE));

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(polyline.getPoints().get(0), 17);
        mMap.animateCamera(cameraUpdate);
    }
}
