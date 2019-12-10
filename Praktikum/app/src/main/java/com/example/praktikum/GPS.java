package com.example.praktikum;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
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
import com.google.android.material.textfield.TextInputEditText;

import java.lang.reflect.Array;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class GPS extends AppCompatActivity {

    Switch switchOnOff, switchSessionSpeichern;
    TextView textViewGPSKoordinaten, textViewAdresse, textViewTimestampedLocations,testView;
    TextInputEditText textInputIntervall, textInputFastestIntervall, textInputName, textInputBeschreibung, textInputTrackid;
    Spinner spinnerGPSPriority, spinnerGPSNetwork;
    GPSTracker gpsTracker;
    Location location;
    Rest rest;
    Button buttonLaden, buttonMaps,buttonTime;

    Date zeitstempel;
    String testzeit;

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
        testView.findViewById(R.id.testview);

        spinnerGPSPriority = findViewById(R.id.spinnerGPSPriority);
        spinnerGPSNetwork = findViewById(R.id.spinnerGPSNetwork);

        buttonLaden = findViewById(R.id.buttonLaden);
        buttonMaps = findViewById(R.id.buttonMaps);
        buttonTime = findViewById(R.id.time);


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


        buttonTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                zeitstempel = Calendar.getInstance().getTime();
            }
        });


        buttonLaden.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rest.getData();
            }
        });

        buttonMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Intent intent = new Intent(GPS.this, MapsActivity.class);
                String datensatz[][] = rest.datensatz;
                Location start = new Location("Start");
                Location ende = new Location("Ende");
                Interpolation interpolation = new Interpolation();
                String t1, t2;
                double[] latitude = new double[datensatz.length];
                double[] longitude = new double[datensatz.length];
                for(int i = 0; i < datensatz.length; i++) {
                    latitude[i] = Double.parseDouble(datensatz[i][0]);
                    longitude[i] = Double.parseDouble(datensatz[i][1]);
                }
                start.setLatitude(latitude[0]);
                start.setLongitude(longitude[0]);
                ende.setLatitude(latitude[latitude.length-1]);
                ende.setLongitude(longitude[longitude.length-1]);
                t1 = datensatz[0][3];
                t2 = datensatz[datensatz.length-1][3];
                Log.e("t1", t1+"");
                Log.e("t2", t2+"");
                ArrayList<Location> interpolationListe =  interpolation.koordinatenLinearInterpolieren(start, ende, Long.parseLong(t1), Long.parseLong(t2));
                intent.putExtra("Latitude", latitude);
                intent.putExtra("Longitude", longitude);
                intent.putExtra("InterpolationListe", interpolationListe);

                for(int i = 0; i < interpolationListe.size(); i++){
                    Log.e("Latitude", interpolationListe.get(i).getLatitude()+"");
                    Log.e("Longitude", interpolationListe.get(i).getLongitude()+"");
                }

                startActivity(intent);
            }
        });
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
}