package com.example.praktikum;

import android.location.Location;
import android.util.Log;

import java.util.ArrayList;

public class Interpolation {

    public ArrayList<Location> koordinatenLinearInterpolieren(Location a, Location b, long t1, long t2){
        Log.e("Funktioniert", "koordinatenLinearInterpolieren wurde aufgerufen");
        ArrayList<Location> listeInterpolierteKoordinaten = new ArrayList<>();
        double dLongitude = b.getLongitude() - a.getLongitude();
        double dLatitude = b.getLatitude() - a.getLatitude();
        int schrittweiteInMillisekunden = 1000;

        long t21 = t2 - t1;
        long t = t1 + schrittweiteInMillisekunden;

        while(t < t2){
            double dT = (double)(t-t1)/(double) t21;
            double neueLongitude = a.getLongitude() + dLongitude * dT;
            double neueLatitude = a.getLatitude() + dLatitude * dT;
            Location neueKoordinate = new Location("Interpolation");
            neueKoordinate.setLongitude(neueLongitude);
            neueKoordinate.setLatitude(neueLatitude);
            listeInterpolierteKoordinaten.add(neueKoordinate);
            t = t + schrittweiteInMillisekunden;
        }


        for(int i = 0; i < listeInterpolierteKoordinaten.size(); i++){
            Log.e("Interpolation Latitude:", listeInterpolierteKoordinaten.get(i).getLatitude()+"");
            Log.e("Interpolation Longitu:", listeInterpolierteKoordinaten.get(i).getLongitude()+"");
        }

        return listeInterpolierteKoordinaten;
    }

}