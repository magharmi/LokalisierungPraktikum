package com.example.praktikum;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class MainActivity extends AppCompatActivity {

    Button buttonWeiter;
    Spinner spinnerDatenquellen;
    private GraphView graph;
    int x = 20;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialisierung

        buttonWeiter = findViewById(R.id.buttonWeiter);
        spinnerDatenquellen = findViewById(R.id.spinnerDatenquellen);

        graph = (GraphView) findViewById(R.id.graph);

        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMaxX(x);
        graph.getViewport().setMaxY(100);

        LineGraphSeries<DataPoint>xywerte = new LineGraphSeries<DataPoint>();
        xywerte.setColor(Color.RED);


        int n = 5;
        for(double i=0;i <= n; i+= 0.1)
        {
            DataPoint dp = new DataPoint(i,Math.pow(4,i));
            xywerte.appendData(dp,false,1000);
        }
         graph.addSeries(xywerte);

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
                }
            }
        });

    }
}
