package com.example.praktikum;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Gyro extends AppCompatActivity {

    Switch switchOnOff;
    TextView textViewGyrosskopKoordinaten;
    SensorManager sensorMan;
    Spinner spinnerSamplingFrequenzen;
    Button buttonSpeicherort;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accelerometer);

        //Initialisierung
        sensorMan = (SensorManager) getSystemService(SENSOR_SERVICE);
        switchOnOff = findViewById(R.id.switchOnOff);

        textViewGyrosskopKoordinaten = findViewById(R.id.textViewGyrosskopKoordinaten);
        spinnerSamplingFrequenzen = findViewById(R.id.spinnerGPSPriority);
        buttonSpeicherort = findViewById(R.id.buttonSpeicherort);


        final SensorEventListener sensEventList = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                switch (event.sensor.getType()) {
                    case Sensor.TYPE_GYROSCOPE:
                        textViewGyrosskopKoordinaten.setText("Aktuelle Koordinaten\n\nX: " + event.values[0] + "\nY: " + event.values[1] + "\nZ: " + event.values[2]);
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
                    konfigurationAktiv(false);
                    sensorMan.registerListener(sensEventList, sensorMan.getDefaultSensor(Sensor.TYPE_GYROSCOPE), getSensorDelay());
                    Log.e("Sensor-Delay", ""+getSensorDelay());
                } else {
                    switchOnOff.setText("Datensammlung aktivieren");
                    konfigurationAktiv(true);
                    sensorMan.unregisterListener(sensEventList);
                }
            }
        });
    }

    public int getSensorDelay(){
        if(spinnerSamplingFrequenzen.getSelectedItem().toString().equals("Normal")){
            return SensorManager.SENSOR_DELAY_NORMAL;
        }
        else if(spinnerSamplingFrequenzen.getSelectedItem().toString().equals("UI")){
            return SensorManager.SENSOR_DELAY_UI;
        }
        else if(spinnerSamplingFrequenzen.getSelectedItem().toString().equals("Game")){
            return SensorManager.SENSOR_DELAY_GAME;
        }
        else if(spinnerSamplingFrequenzen.getSelectedItem().toString().equals("Fastest")){
            return SensorManager.SENSOR_DELAY_FASTEST;
        }
        else
            return SensorManager.SENSOR_DELAY_NORMAL;
    }

    public void konfigurationAktiv(boolean aktivDeaktiv){
        spinnerSamplingFrequenzen.setEnabled(aktivDeaktiv);
        buttonSpeicherort.setEnabled(aktivDeaktiv);
    }
}
