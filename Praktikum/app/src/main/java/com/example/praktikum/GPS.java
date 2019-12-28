package com.example.praktikum;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Debug;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.textfield.TextInputEditText;

import java.lang.reflect.Array;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class GPS extends AppCompatActivity {

    Switch switchOnOff, switchSessionSpeichern;
    TextView textViewGPSKoordinaten, textViewAdresse, textViewTimestampedLocations;
    TextInputEditText textInputIntervall, textInputFastestIntervall, textInputName, textInputBeschreibung, textInputTrackid;
    Spinner spinnerGPSPriority, spinnerGPSNetwork;
    GPSTracker gpsTracker;
    Location location;
    Rest rest;
    Button buttonLaden, buttonMaps, buttonTimestamp;

    boolean sessionSpeichern;

    static GPS gps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        gps = this;
        gpsTracker = new GPSTracker(getApplicationContext());


        //Initialisierung
        switchOnOff = findViewById(R.id.switchOnOff);
        switchSessionSpeichern = findViewById(R.id.switchSessionSpeichern);

        textInputFastestIntervall = findViewById(R.id.textInputFastestIntervall);
        textInputIntervall = findViewById(R.id.textInputIntervall);
        textInputName = findViewById(R.id.textInputName);
        textInputBeschreibung = findViewById(R.id.textInputBeschreibung);
        textInputTrackid = findViewById(R.id.textInputTrackid);

        textViewGPSKoordinaten = findViewById(R.id.textViewGPSKoordinaten);
        textViewAdresse = findViewById(R.id.textViewAdresse);
        textViewTimestampedLocations = findViewById(R.id.textViewTimestampedLocations);
        textViewTimestampedLocations.setMovementMethod(new ScrollingMovementMethod());

        spinnerGPSPriority = findViewById(R.id.spinnerGPSPriority);
        spinnerGPSNetwork = findViewById(R.id.spinnerGPSNetwork);

        buttonLaden = findViewById(R.id.buttonLaden);
        buttonMaps = findViewById(R.id.buttonMaps);
        buttonTimestamp = findViewById(R.id.buttonTimestamp);

        rest = new Rest(getApplicationContext());


        switchOnOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true) {
                    switchOnOff.setText("Datensammlung aktiviert");
                    konfigurationAktiv(false);
                    if (sessionSpeichern == true) {
                        rest.postSession(textInputName.getText().toString(), textInputBeschreibung.getText().toString());
                        rest.resetCounter();
                    }
                    gpsTracker.setLocationRequest(getGPSPriority(), Integer.parseInt(textInputIntervall.getText().toString()), Integer.parseInt(textInputFastestIntervall.getText().toString()));
                    location = gpsTracker.getLocation();
                    startService();
                } else {
                    switchOnOff.setText("Datensammlung deaktiviert");
                    konfigurationAktiv(true);
                    gpsTracker.datensammlungAktiv = false;
                    stopService();
                }
            }
        });

        switchSessionSpeichern.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true) {
                    switchSessionSpeichern.setText("Session wird gespeichert");
                    sessionSpeichern = true;
                } else {
                    switchSessionSpeichern.setText("Session wird nicht gespeichert");
                    sessionSpeichern = false;
                }
            }
        });

        buttonLaden.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rest.getData();
            }
        });

        buttonTimestamp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                route1();
            }
        });

        buttonMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                route2();
            }
        });
    }

    public void route1(){
        Intent intent = new Intent(GPS.this, MapsActivity.class);
        String datensatz[][] = rest.datensatz;
        ArrayList<Location> gbsData = this.parseIntoLocationList(datensatz);
        Location start = new Location("Start");
        Location middle = new Location("middle");
        Location ende = new Location("Ende");
        Interpolation interpolation = new Interpolation();
        Long t1, t2;
        start.setLatitude(51.445748);
        start.setLongitude(7.27277);
        middle.setLatitude(51.44788);
        middle.setLongitude(7.270667);
        ende.setLatitude(51.447737);
        ende.setLongitude(7.270097);
        t1 = gbsData.get(0).getTime();
        t2 = gbsData.get(gbsData.size()-1).getTime();
        long t12 = gbsData.get(gbsData.size()-7).getTime();
        long g = t2-t1;
        Log.e("t1", t1+"");
        Log.e("t2", t2+"");
        ArrayList<Location> interpolationListeSM =  interpolation.koordinatenLinearInterpolieren(start, middle, t1, t12);
        ArrayList<Location> interpolationListeME =  interpolation.koordinatenLinearInterpolieren(middle, ende, t12, t2);
        ArrayList<LatLng> z = new ArrayList<>();
        gbsData.stream().filter( x->{
            interpolationListeSM.stream().filter(j ->{
                if(x.getTime() == j.getTime()){
                    z.add(new LatLng(x.getLatitude(),j.getLongitude()));
                    z.add(new LatLng(x.getAltitude(),j.getAltitude()));
                }
                return true;
            });
            return true;
        });
        intent.putExtra("GPSData", gbsData);
        intent.putExtra("interpolationListeSM", interpolationListeSM);
        intent.putExtra("interpolationListeME", interpolationListeME);
        startActivity(intent);
    }
    public void route2(){
        Intent intent = new Intent(GPS.this, MapsActivity.class);
        String datensatz[][] = rest.datensatz;
        ArrayList<Location> gbsData = this.parseIntoLocationList(datensatz);
        Location start = new Location("Start");
        Location middle = new Location("middle");
        Location ende = new Location("Ende");
        Interpolation interpolation = new Interpolation();
        Long t1, t2;
        start.setLatitude(51.6310796);
        start.setLongitude(7.1937187);
        ende.setLatitude(51.6346145);
        ende.setLongitude(7.1938935);
        t1 = gbsData.get(0).getTime();
        t2 = gbsData.get(gbsData.size()-1).getTime();
        Log.e("t1", t1+"");
        Log.e("t2", t2+"");
        ArrayList<Location> interpolationListeSM =  interpolation.koordinatenLinearInterpolieren(start, ende, t1, t2);
        ArrayList<Location> interpolationListeME =  new ArrayList<>();
        ArrayList<LatLng> z = new ArrayList<>();
        gbsData.stream().filter( x->{
            interpolationListeSM.stream().filter(j ->{
                if(x.getTime() == j.getTime()){
                    z.add(new LatLng(x.getLatitude(),j.getLongitude()));
                    z.add(new LatLng(x.getAltitude(),j.getAltitude()));
                }
                return true;
            });
            return true;
        });
        intent.putExtra("GPSData", gbsData);
        intent.putExtra("interpolationListeSM", interpolationListeSM);
        intent.putExtra("interpolationListeME", interpolationListeME);
        startActivity(intent);
    }

    public void konfigurationAktiv(boolean aktivDeaktiv){
        spinnerGPSPriority.setEnabled(aktivDeaktiv);
        switchSessionSpeichern.setEnabled(aktivDeaktiv);
        textInputIntervall.setEnabled(aktivDeaktiv);
        textInputFastestIntervall.setEnabled(aktivDeaktiv);
        buttonLaden.setEnabled(aktivDeaktiv);
        textInputTrackid.setEnabled(aktivDeaktiv);
        spinnerGPSNetwork.setEnabled(aktivDeaktiv);
    }

    public int getGPSPriority(){
        int position = spinnerGPSPriority.getSelectedItemPosition();
        String[] values_GPSPriority = getResources().getStringArray(R.array.values_GPSPriority);
        return Integer.parseInt(values_GPSPriority[position]);
    }

    public String getGPSNetwork(){
        if(spinnerGPSNetwork.getSelectedItem().toString().equals("GPS")){
            return LocationManager.GPS_PROVIDER;
        }
        else if(spinnerGPSNetwork.getSelectedItem().toString().equals("Netzwerkpositionierung")){
            return LocationManager.NETWORK_PROVIDER;
        }
        else return null;
    }

    public void startService(){
        Intent intent = new Intent(this, GPSService.class);
        ContextCompat.startForegroundService(this, intent);
    }

    public void stopService(){
        Intent intent = new Intent(this, GPSService.class);
        stopService(intent);
    }

    public void setLocation(){
        location = gpsTracker.getLocation();
    }

    public static GPS getInstance(){
        return gps;
    }

    public ArrayList<Location> parseIntoLocationList(String datensatz[][] ){
        ArrayList<Location> locations = new ArrayList<Location>();

        for(int i = 0; i < datensatz.length; i++){
            Location a = new Location("db");
            a.setLatitude(Double.parseDouble(datensatz[i][0]));
            a.setLongitude(Double.parseDouble(datensatz[i][1]));
            a.setAltitude(Double.parseDouble(datensatz[i][2]));
            a.setTime(Long.parseLong(datensatz[i][3])*1000);
            locations.add(a);
        }
        return  locations;
    }
}