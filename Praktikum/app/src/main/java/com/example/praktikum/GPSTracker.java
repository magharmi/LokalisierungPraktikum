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
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;

import java.io.IOException;
import java.util.List;

public class GPSTracker implements LocationListener {
    LocationRequest locationRequest;
    Geocoder geocoder;

    int priority;
    long interval, fastestInterval;
    TextView textViewAdresse;

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
        getLocation(priority, interval, fastestInterval);
        if(location != null && datensammlungAktiv == true) {
            GPS.getInstance().textViewGPSKoordinaten.setText("aktuelle Koordinaten\n\nLatitude: "+location.getLatitude()+"\nLongitude: "+location.getLongitude()+"\nAltitude: "+location.getAltitude());
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