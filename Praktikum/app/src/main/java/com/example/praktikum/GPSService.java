package com.example.praktikum;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class GPSService extends Service {
    public static final String CHANNEL_ID = "MyForegroundServiceChannel";
    public static final int NOTIFICATION_ID = 1;
    public static AsyncTask task;

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
                //locationManager.requestLocationUpdates(GPS.getInstance().getGPSNetwork(), 1000,10,this);  // GPS und Netzwerk auswählbar. Bei FusedLocationProviderClient nicht
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

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        createNotificationChannel();

        task = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                // Hier programmieren, was im Hintergrund ablaufen soll. Z.B. GPS auslesen.
                final GPSTracker gpsTracker = new GPSTracker(getApplicationContext());
                new Handler(Looper.getMainLooper()).post(new Runnable(){
                    @Override
                    public void run() {
                        gpsTracker.onLocationChanged(GPS.getInstance().location);
                    }
                });

                return null;
            }
        }.execute();

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.notify(NOTIFICATION_ID, buildNotification(""));

        return START_STICKY;
    }

    @Override
    public void onCreate() {
        startForeground(NOTIFICATION_ID, buildNotification(""));
    }

    @Override
    public void onDestroy(){
        task.cancel(true);
        super.onDestroy();
    }

    public GPSService() {
    }

    private Notification buildNotification(String text){
        Intent notificationInted = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationInted, 0);
        return new NotificationCompat.Builder(this, CHANNEL_ID).setContentTitle("Mein Notification Titel").setContentText(text).setContentIntent(pendingIntent).setSmallIcon(R.mipmap.ic_launcher_round).setOngoing(true).build();
    }

    public void createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel serviceChannel = new NotificationChannel(CHANNEL_ID, "Foreground Service Channel", NotificationManager.IMPORTANCE_LOW);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(serviceChannel);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


}
