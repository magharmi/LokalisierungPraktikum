package com.example.praktikum;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Location;
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
import com.google.android.material.textfield.TextInputEditText;

public class GPS extends AppCompatActivity {

    Switch switchOnOff, switchSessionSpeichern;
    TextView textViewGPSKoordinaten, textViewAdresse, textViewTimestampedLocations;
    TextInputEditText textInputIntervall, textInputFastestIntervall, textInputName, textInputBeschreibung, textInputTrackid;
    Spinner spinnerGPSPriority;
    GPSTracker gpsTracker;
    Location location;
    Rest rest;
    Button buttonLaden;

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

        buttonLaden = findViewById(R.id.buttonLaden);

        rest = new Rest(getApplicationContext());
        

        switchOnOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true) {
                    switchOnOff.setText("Datensammlung aktiviert");
                    konfigurationAktiv(false);
                    rest.postSession(textInputName.getText().toString(), textInputBeschreibung.getText().toString());
                    gpsTracker = new GPSTracker(getApplicationContext());
                    gpsTracker.setLocationRequest(getGPSPriority(), Integer.parseInt(textInputIntervall.getText().toString()), Integer.parseInt(textInputFastestIntervall.getText().toString()));
                    //Log.e("GPSPriority", getGPSPriority()+"");
                    location = gpsTracker.getLocation();
                } else {
                    switchOnOff.setText("Datensammlung deaktiviert");
                    konfigurationAktiv(true);
                    gpsTracker.datensammlungAktiv = false;
                }
            }
        });

        switchSessionSpeichern.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked == true){
                    switchSessionSpeichern.setText("Session wird gespeichert");
                    sessionSpeichern = true;
                }
                else{
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
    }


    public void konfigurationAktiv(boolean aktivDeaktiv){
        spinnerGPSPriority.setEnabled(aktivDeaktiv);
        switchSessionSpeichern.setEnabled(aktivDeaktiv);
        textInputIntervall.setEnabled(aktivDeaktiv);
        textInputFastestIntervall.setEnabled(aktivDeaktiv);
        buttonLaden.setEnabled(aktivDeaktiv);
        textInputTrackid.setEnabled(aktivDeaktiv);
    }

    public int getGPSPriority(){
        int position = spinnerGPSPriority.getSelectedItemPosition();
        String[] values_GPSPriority = getResources().getStringArray(R.array.values_GPSPriority);
        return Integer.parseInt(values_GPSPriority[position]);
    }

    public static GPS getInstance(){
        return gps;
    }
}