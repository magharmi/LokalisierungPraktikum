package com.example.praktikum;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;

public class GPSTracker implements LocationListener {
    LocationRequest locationRequest;

    int priority;
    long interval, fastestInterval;

    boolean datensammlungAktiv = true;

    Context context;
    public GPSTracker(Context c){
        context = c;
    }

    public Location getLocation(int priority, long interval, long fastestInterval){
        if(ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(GPS.getInstance(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 123);
            Toast.makeText(context,"Berechtigungen nicht gegeben", Toast.LENGTH_SHORT).show();
            return null;
        }
        this.priority = priority;
        this.interval = interval;
        this.fastestInterval = fastestInterval;
        locationRequest = new LocationRequest();
        locationRequest.setPriority(priority);
        locationRequest.setInterval(interval);
        locationRequest.setFastestInterval(fastestInterval);
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if(isGPSEnabled){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000,10,this);
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            return location;
        }
        else{
            Toast.makeText(context,"Bitte GPS aktivieren", Toast.LENGTH_LONG).show();
        }
        return null;
    }


    @Override
    public void onLocationChanged(Location location) {
        getLocation(priority, interval, fastestInterval);
        if(location != null && datensammlungAktiv == true) {
            GPS.getInstance().textViewGPSKoordinaten.setText("aktuelle Koordinaten\n\nLatitude: "+location.getLatitude()+"\nLongitude: "+location.getLongitude()+"\nAltitude: "+location.getAltitude());
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