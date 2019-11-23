package com.example.praktikum;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.core.app.NotificationCompat;


import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;

public class GPSService extends Service {
    public static final String CHANNEL_ID = "MyForegroundServiceChannel";
    public static final int NOTIFICATION_ID = 1;
    public static AsyncTask task;

    public class GPSTracker implements LocationListener {
        Geocoder geocoder;

        boolean datensammlungAktiv = true;

        Context context;
        public GPSTracker(Context c){
            context = c;
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
                NotificationManager notificationManager = getSystemService(NotificationManager.class);
                notificationManager.notify(NOTIFICATION_ID, buildNotification("Latitude: " + location.getLatitude() + "\nLongitude: " + location.getLongitude() + "\nTimestamp: " + rest.getDate(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()))));
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


        return START_STICKY;
    }

    @Override
    public void onCreate() {
        startForeground(NOTIFICATION_ID, buildNotification("Was das"));
    }

    @Override
    public void onDestroy(){
        task.cancel(true);
        super.onDestroy();
    }

    public GPSService() {
    }

    private Notification buildNotification(String text){
        Intent notificationInted = new Intent(this, GPS.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationInted, FLAG_UPDATE_CURRENT);
        return new NotificationCompat.Builder(this, CHANNEL_ID).setContentTitle("GPSTracker").setContentText(text).setContentIntent(pendingIntent).setSmallIcon(R.mipmap.ic_launcher_round).setOngoing(true).build();
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
