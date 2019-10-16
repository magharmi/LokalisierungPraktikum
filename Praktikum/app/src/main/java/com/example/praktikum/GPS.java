package com.example.praktikum;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

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
        spinnerSamplingFrequenzen = findViewById(R.id.spinnerSamplingFrequenzen);
        buttonSpeicherort = findViewById(R.id.buttonSpeicherort);

        ActivityCompat.requestPermissions(GPS.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},123);

        switchOnOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true) {
                    switchOnOff.setText("Datensammlung deaktivieren");
                    konfigurationAktiv(false);
                    gpsTracker = new GPSTracker(getApplicationContext());
                    location = gpsTracker.getLocation();
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