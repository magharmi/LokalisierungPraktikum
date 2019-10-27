package com.example.praktikum;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.location.LocationRequest;
import com.google.android.material.textfield.TextInputEditText;

public class GPS extends AppCompatActivity {

    Switch switchOnOff, switchSessionSpeichern;
    TextView textViewGPSKoordinaten, textViewAdresse, textViewTimestampedLocations;
    TextInputEditText textInputIntervall, textInputFastestIntervall, textInputName, textInputBeschreibung;
    Spinner spinnerSamplingFrequenzen;
    GPSTracker gpsTracker;
    Location location;
    Rest rest;

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

        textViewGPSKoordinaten = findViewById(R.id.textViewGPSKoordinaten);
        textViewAdresse = findViewById(R.id.textViewAdresse);
        textViewTimestampedLocations = findViewById(R.id.textViewTimestampedLocations);
        textViewTimestampedLocations.setMovementMethod(new ScrollingMovementMethod());

        spinnerSamplingFrequenzen = findViewById(R.id.spinnerGPSPriority);

        rest = new Rest(getApplicationContext());
        

        switchOnOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true) {
                    switchOnOff.setText("Datensammlung deaktivieren");
                    konfigurationAktiv(false);
                    if(sessionSpeichern == true){
                        rest.postSession(textInputName.getText().toString(), textInputBeschreibung.getText().toString());
                    }
                    gpsTracker = new GPSTracker(getApplicationContext());
                    gpsTracker.setLocationRequest(LocationRequest.PRIORITY_HIGH_ACCURACY, Integer.parseInt(textInputIntervall.getText().toString()), Integer.parseInt(textInputFastestIntervall.getText().toString()));
                    location = gpsTracker.getLocation();
                } else {
                    switchOnOff.setText("Datensammlung aktivieren");
                    konfigurationAktiv(true);
                    gpsTracker.datensammlungAktiv = false;
                }
            }
        });

        switchSessionSpeichern.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked == true){
                    switchOnOff.setText("Session nicht speichern");
                    sessionSpeichern = true;
                }
                else{
                    switchSessionSpeichern.setText("Session speichern");
                    sessionSpeichern = false;
                }
            }
        });
    }

    public void konfigurationAktiv(boolean aktivDeaktiv){
        spinnerSamplingFrequenzen.setEnabled(aktivDeaktiv);
        switchSessionSpeichern.setEnabled(aktivDeaktiv);
        textInputIntervall.setEnabled(aktivDeaktiv);
        textInputFastestIntervall.setEnabled(aktivDeaktiv);
    }

    public static GPS getInstance(){
        return gps;
    }
}