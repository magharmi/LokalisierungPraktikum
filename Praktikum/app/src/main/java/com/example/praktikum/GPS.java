package com.example.praktikum;

import androidx.appcompat.app.AppCompatActivity;

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

import java.util.List;

public class GPS extends AppCompatActivity {

    Switch switchOnOff, switchSessionSpeichern;
    TextView textViewGPSKoordinaten, textViewAdresse, textViewTimestampedLocations;
    TextInputEditText textInputIntervall, textInputFastestIntervall, textInputName, textInputBeschreibung, textInputTrackid;
    Spinner spinnerGPSPriority, spinnerGPSNetwork;
    GPSTracker gpsTracker;
    Location location;
    Rest rest;
    Button buttonLaden, buttonMaps;

    boolean sessionSpeichern;

    static GPS gps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        gps = this;

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
                    gpsTracker = new GPSTracker(getApplicationContext());
                    gpsTracker.setLocationRequest(getGPSPriority(), Integer.parseInt(textInputIntervall.getText().toString()), Integer.parseInt(textInputFastestIntervall.getText().toString()));
                    //Log.e("GPSPriority", getGPSPriority()+"");
                    location = gpsTracker.getLocation();
                } else {
                    switchOnOff.setText("Datensammlung deaktiviert");
                    konfigurationAktiv(true);
                    gpsTracker.datensammlungAktiv = false;

                    Interpolation interpolation = new Interpolation();

                    Location a = new Location("A");
                    Location b = new Location("B");
                    a.setLatitude(51.52823);
                    a.setLongitude(7.35489);
                    b.setLatitude(51.52886);
                    b.setLongitude(7.35399);
                    List<Location> interpolierenListe =  interpolation.koordinatenLinearInterpolieren(a, b, 5000, 30000);

                    for(int i = 0; i < interpolierenListe.size(); i++){
                        Log.e(i + "", interpolierenListe.get(i).getLatitude() + "");
                        Log.e(i + "", interpolierenListe.get(i).getLongitude() + "");
                    }
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

        buttonMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Intent intent = new Intent(GPS.this, MapsActivity.class);
                String datensatz[][] = rest.datensatz;
                double[] latitude = new double[datensatz.length];
                double[] longitude = new double[datensatz.length];
                for(int i = 0; i < datensatz.length; i++) {
                    latitude[i] = Double.parseDouble(datensatz[i][0]);
                    longitude[i] = Double.parseDouble(datensatz[i][1]);
                }
                intent.putExtra("Latitude", latitude);
                intent.putExtra("Longitude", longitude);
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

    public static GPS getInstance(){
        return gps;
    }
}