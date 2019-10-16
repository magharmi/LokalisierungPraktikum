package com.example.praktikum;

import androidx.appcompat.app.AppCompatActivity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

public class GPS extends AppCompatActivity {

    Switch switchOnOff;
    TextView textViewGPSKoordinaten;
    SensorManager sensorMan;
    LocationManager locationMan;
    LocationListener locationEventList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps);

        //Initialisierung
        sensorMan = (SensorManager) getSystemService(SENSOR_SERVICE);
        locationMan = (LocationManager) getSystemService(LOCATION_SERVICE);
        switchOnOff = findViewById(R.id.switchOnOff);

        textViewGPSKoordinaten = findViewById(R.id.textViewGPSKoordinaten);


        final SensorEventListener sensEventList = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                switch (event.sensor.getType()) {
                    case Sensor.TYPE_ACCELEROMETER:
                        textViewGPSKoordinaten.setText("Aktuelle Koordinaten\n\nX: " + event.values[0] + "\nY: " + event.values[1] + "\nZ: " + event.values[2]);
                        break;
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };

        switchOnOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true) {
                    switchOnOff.setText("Datensammlung deaktivieren");
                    sensorMan.registerListener(sensEventList, sensorMan.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
                } else {
                    switchOnOff.setText("Datensammlung aktivieren");
                    sensorMan.unregisterListener(sensEventList);
                }
            }
        });
    }
}