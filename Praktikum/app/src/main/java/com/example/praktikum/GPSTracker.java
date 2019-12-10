package com.example.praktikum;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class GPSTracker implements LocationListener {
    LocationRequest locationRequest;
    Geocoder geocoder;

    int priority;
    long interval, fastestInterval;

    boolean datensammlungAktiv = true;

    Context context;
    public GPSTracker(Context c){
        context = c;
    }

    public Location getLocation(){
        if(ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(GPS.getInstance(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 123);
            Toast.makeText(context,"Berechtigungen nicht gegeben", Toast.LENGTH_SHORT).show();
            return null;
        }
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean isGPSEnabled = locationManager.isProviderEnabled(GPS.getInstance().getGPSNetwork());
        if(isGPSEnabled){
            locationManager.requestLocationUpdates(GPS.getInstance().getGPSNetwork(), 1000,10,this);  // GPS und Netzwerk auswählbar. Bei FusedLocationProviderClient nicht
            Location location = locationManager.getLastKnownLocation(GPS.getInstance().getGPSNetwork());
            FusedLocationProviderClient fusedLocationProviderClient = new FusedLocationProviderClient(context);
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, new LocationCallback(){
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    onLocationChanged(locationResult.getLastLocation());
                }
            }, Looper.myLooper());
            return location;
        }
        else{
            Toast.makeText(context,"Bitte GPS/WIFI aktivieren", Toast.LENGTH_LONG).show();
        }
        return null;
    }


    public void setLocationRequest(int priority, int interval, int fastestInterval){
        this.priority = priority;
        this.interval = interval;
        this.fastestInterval = fastestInterval;
        locationRequest = new LocationRequest();
        locationRequest.setPriority(priority);
        locationRequest.setInterval(interval);
        locationRequest.setFastestInterval(fastestInterval);
        locationRequest.setSmallestDisplacement(1);

        Log.e("LocationRequest", "Interval: " + locationRequest.getInterval() + " FastestIntervall " + locationRequest.getFastestInterval());
    }

    public void getAdresse(Location location){
        geocoder = new Geocoder(context);
        try {
            List<Address> adressdaten = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            Address adresse = adressdaten.get(0);
            GPS.getInstance().textViewAdresse.setText("Adresse: " + adresse.getAddressLine(0));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if(location != null && datensammlungAktiv == true) {
            Rest rest = new Rest(context);
            GPS.getInstance().textViewGPSKoordinaten.setText("aktuelle Koordinaten von " + rest.getDate(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())) + "\n\nLatitude: "+location.getLatitude()+"\nLongitude: "+location.getLongitude()+"\nAltitude: "+location.getAltitude());
            GPS.getInstance().textViewTimestampedLocations.append("\n\nLatitude: "+location.getLatitude()+"\nLongitude: "+location.getLongitude()+"\nAltitude: "+location.getAltitude()+"\nTimestamp: "+rest.getDate(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())));
            Log.e("Koordinaten: ", "Latitude: " + location.getLatitude() + "\nLongitude: " + location.getLongitude() + "\nAltidude: " + location.getAltitude());
            if(rest.getPostTrackStarten() == true) {
                rest.postData(location.getLatitude(), location.getLongitude(), location.getAltitude());
            }
            getAdresse(location);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}