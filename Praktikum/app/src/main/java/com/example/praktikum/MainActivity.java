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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {
    Button buttonWeiter;
    Button graph_button;
    Spinner spinnerDatenquellen;
    private GraphView graph;
    double maxFehler = 100.0;
    int counter = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialisierung

        buttonWeiter = findViewById(R.id.buttonWeiter);
        graph_button = findViewById(R.id.graph_button);
        spinnerDatenquellen = findViewById(R.id.spinnerDatenquellen);
        graph = (GraphView) findViewById(R.id.graph);
        graph_button.setOnClickListener( x->{
            if(MapsActivity.fehlerListe.size() != 0){
                counter = 0;
                MapsActivity.fehlerListe.forEach(j ->{
                    counter ++;
                    graph.getViewport().setYAxisBoundsManual(true);
                    graph.getViewport().setXAxisBoundsManual(true);
                    graph.getViewport().setMaxX(maxFehler);
                    graph.getViewport().setMaxY(1.0);

                    ArrayList<ArrayList<Double>> fehler = MapsActivity.fehlerListe;
                    LineGraphSeries<DataPoint> xywerte = new LineGraphSeries<DataPoint>();
                    switch (counter){
                        case 1:
                            xywerte.setColor(Color.RED);
                            break;
                        case 2:
                            xywerte.setColor(Color.BLUE);
                            break;
                        case 3:
                            xywerte.setColor(Color.GREEN);
                            break;
                        default:
                                xywerte.setColor(Color.RED);
                    }

                    xywerte.setDrawDataPoints(true);
                    xywerte.setDataPointsRadius(6);
                    DataPoint dp ;
                    Collections.sort(j);
                    for(int i=0;i < j.size(); i++)
                    {
                        dp = new DataPoint(j.get(i),cdf(j,j.get(i)));
                        xywerte.appendData(dp,false,1000);
                    }
                    graph.addSeries(xywerte);
                });

            }
        });



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
    double summe = 0.0;
    private double cdf(ArrayList<Double> werte, double e){
        summe = 0.0;
        werte.forEach( x-> {

            if(x<= e){
                summe ++;
            }
        });
        return summe/werte.size();
    }
}
