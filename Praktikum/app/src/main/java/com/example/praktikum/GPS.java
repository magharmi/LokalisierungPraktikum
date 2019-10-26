package com.example.praktikum;

import androidx.appcompat.app.AppCompatActivity;

import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.location.Address;

import com.google.android.gms.location.LocationRequest;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.util.List;

public class GPS extends AppCompatActivity {

    Switch switchOnOff;
    TextView textViewGPSKoordinaten, textViewAdresse;
    TextInputEditText textInputIntervall, textInputFastestIntervall;
    Spinner spinnerSamplingFrequenzen;
    Button buttonSpeicherort;
    GPSTracker gpsTracker;
    Location location;

    static GPS gps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        gps = this;

        //Initialisierung
        switchOnOff = findViewById(R.id.switchOnOff);

        textInputFastestIntervall = findViewById(R.id.textInputFastestIntervall);
        textInputIntervall = findViewById(R.id.textInputIntervall);

        textViewGPSKoordinaten = findViewById(R.id.textViewGPSKoordinaten);
        textViewAdresse = findViewById(R.id.textViewAdresse);
        spinnerSamplingFrequenzen = findViewById(R.id.spinnerGPSPriority);
        buttonSpeicherort = findViewById(R.id.buttonSpeicherort);
        

        switchOnOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true) {
                    switchOnOff.setText("Datensammlung deaktivieren");
                    konfigurationAktiv(false);
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
    }

    public void konfigurationAktiv(boolean aktivDeaktiv){
        spinnerSamplingFrequenzen.setEnabled(aktivDeaktiv);
        buttonSpeicherort.setEnabled(aktivDeaktiv);
    }

    public static GPS getInstance(){
        return gps;
    }
}