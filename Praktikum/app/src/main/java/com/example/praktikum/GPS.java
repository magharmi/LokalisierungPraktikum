package com.example.praktikum;

import androidx.appcompat.app.AppCompatActivity;

import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.location.LocationRequest;

public class GPS extends AppCompatActivity {

    Switch switchOnOff;
    TextView textViewGPSKoordinaten;
    Spinner spinnerSamplingFrequenzen;
    Button buttonSpeicherort;
    GPSTracker gpsTracker;
    Location location;

    static GPS gps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps);

        gps = this;

        //Initialisierung
        switchOnOff = findViewById(R.id.switchOnOff);

        textViewGPSKoordinaten = findViewById(R.id.textViewGPSKoordinaten);
        spinnerSamplingFrequenzen = findViewById(R.id.spinnerGPSPriority);
        buttonSpeicherort = findViewById(R.id.buttonSpeicherort);
        

        switchOnOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true) {
                    switchOnOff.setText("Datensammlung deaktivieren");
                    konfigurationAktiv(false);
                    gpsTracker = new GPSTracker(getApplicationContext());
                    location = gpsTracker.getLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, 2000, 1000);
                } else {
                    switchOnOff.setText("Datensammlung aktivieren");
                    konfigurationAktiv(true);
                    gpsTracker.datensammlungAktiv = false;
                }
            }
        });
    }

    public void konfigurationAktiv(boolean aktivDeaktiv){
        spinnerSamplingFrequenzen.setEnabled(aktivDeaktiv);
        buttonSpeicherort.setEnabled(aktivDeaktiv);
    }

    public static GPS getInstance(){
        return gps;
    }
}