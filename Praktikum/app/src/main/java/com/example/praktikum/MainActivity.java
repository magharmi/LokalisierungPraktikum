package com.example.praktikum;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;

import com.example.praktikum.Netzwerkpositionierung.NetzwerkP_Activity;

public class MainActivity extends AppCompatActivity {

    Button buttonWeiter;
    Spinner spinnerDatenquellen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialisierung
        buttonWeiter = findViewById(R.id.buttonWeiter);
        spinnerDatenquellen = findViewById(R.id.spinnerDatenquellen);

        buttonWeiter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(spinnerDatenquellen.getSelectedItem().toString().equals("Accelerometer")) {
                    startActivity(new Intent(MainActivity.this, Accelerometer.class));
                }
                else if(spinnerDatenquellen.getSelectedItem().toString().equals("GPS")){
                    startActivity(new Intent(MainActivity.this, GPS.class));
                }
                else if(spinnerDatenquellen.getSelectedItem().toString().equals("Gyro")){
                    startActivity(new Intent(MainActivity.this, Gyro.class));
                }else if(spinnerDatenquellen.getSelectedItem().toString().equals("Netzwerkpositionierung")){
                    startActivity(new Intent(MainActivity.this, NetzwerkP_Activity.class));
                }
            }
        });

    }
}
