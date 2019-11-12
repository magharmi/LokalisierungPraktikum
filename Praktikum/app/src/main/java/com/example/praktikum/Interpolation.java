package com.example.praktikum;

import android.location.Location;

import java.io.IOException;
import java.sql.Array;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class Interpolation {

    public List<Location> koordinatenLinearInterpolieren(Location a, Location b, int t1, int t2){
        List<Location> listeInterpolierteKoordinaten = new ArrayList<>();
        double dLongitude = b.getLongitude() - a.getLongitude();
        double dLatitude = b.getLatitude() - a.getLatitude();
        int schrittweiteInMillisekunden = 1000;

        long t21 = t2 - t1;
        long t = t1 + schrittweiteInMillisekunden;

        while(t < t2){
            long dT = (t-t1)/t21;
            double neueLongitude = a.getLongitude() + dLongitude * dT;
            double neueLatitude = a.getLatitude() + dLatitude * dT;
            Location neueKoordinate = new Location("Interpolation");
            neueKoordinate.setLongitude(neueLongitude);
            neueKoordinate.setLatitude(neueLatitude);
            listeInterpolierteKoordinaten.add(neueKoordinate);
            t = t + schrittweiteInMillisekunden;
        }

        return listeInterpolierteKoordinaten;
    }

}
