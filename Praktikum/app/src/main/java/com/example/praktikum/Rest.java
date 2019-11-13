package com.example.praktikum;

import android.content.Context;
import android.text.format.DateFormat;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Rest {

    static int trackid;
    static boolean postTrackStarten;
    static int counter = 0;
    String datensatz[][];

    Context context;
    public Rest(Context c){
        context = c;
    }

    public void getSession(){

        String URL="http://pi-bo.dd-dns.de:8080/LM-Server/api/v2/track?teamid=25";

        RequestQueue requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        JsonArrayRequest arrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                URL,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.e("getSession", response.toString());
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        Log.e("getSession ERROR", error.toString());
                    }
                }
        );
        requestQueue.add(arrayRequest);
    }

    public void getData(){

        String URL="http://pi-bo.dd-dns.de:8080/LM-Server/api/v2/data?teamid=25&trackid="+GPS.getInstance().textInputTrackid.getText().toString();

        RequestQueue requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        JsonArrayRequest arrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                URL,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.e("getData", response.toString());
                        GPS.getInstance().textViewTimestampedLocations.setText(response.toString());
                        try {
                            datenVerarbeitenUndAusgeben(response, true);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        Log.e("getData ERROR", error.toString());
                    }
                }
        );
        requestQueue.add(arrayRequest);
    }

    // Die Funktion gibt die Werte direkt in InputTextTimestampedLocation aus. String[][] besitzt die Daten auch, falls wir die woanders brauchen.
    public String[][] datenVerarbeitenUndAusgeben(JSONArray response, boolean ausgeben) throws JSONException {
        String[] latitude, longitude, altitude, timestamp, track, session, counter;
        latitude = new String[response.length()];
        longitude = new String[response.length()];
        altitude = new String[response.length()];
        timestamp = new String[response.length()];
        track = new String[response.length()];
        session = new String[response.length()];
        counter = new String[response.length()];

        GPS.getInstance().textViewTimestampedLocations.setText("Timestamped Locations");

        for(int i = 0; i < response.length(); i++){
            String daten = response.getJSONObject(i).toString();
            latitude[i] = daten.substring(daten.indexOf("latitude\":")+10, daten.indexOf(",\"longitude\":"));
            longitude[i] = daten.substring(daten.indexOf("longitude\":")+11, daten.indexOf(",\"altitude\":"));
            altitude[i] = daten.substring(daten.indexOf("altitude\":")+10, daten.indexOf(",\"timestamp\":"));
            timestamp[i] = daten.substring(daten.indexOf("timestamp\":")+11, daten.indexOf(",\"track\":"));
            track[i] = daten.substring(daten.indexOf("track\":")+8, daten.indexOf(",\"session\":")-1);
            session[i] = daten.substring(daten.indexOf("session\":")+10, daten.indexOf(",\"counter\"")-1);
            counter[i] = daten.substring(daten.indexOf("counter\":")+10, daten.indexOf("}")-1);

            if(ausgeben == true) {
                GPS.getInstance().textViewTimestampedLocations.append("\n\nLatitude: " + latitude[i] + "\nLongitude: " + longitude[i] + "\nAltitude: " + altitude[i] + "\nTimestamp: " + getDate(Long.parseLong(timestamp[i])));
            }
        }
        datensatz = new String[response.length()][7];

        for(int i = 0; i < response.length(); i++){
            datensatz[i][0] = latitude[i];
            datensatz[i][1] = longitude[i];
            datensatz[i][2] = altitude[i];
            datensatz[i][3] = timestamp[i];
            datensatz[i][4] = track[i];
            datensatz[i][5] = session[i];
            datensatz[i][6] = counter[i];
        }
        return datensatz;
    }

    private String getDate(long timestamp) {
        Calendar cal = Calendar.getInstance(Locale.GERMANY);
        cal.setTimeInMillis(timestamp * 1000);
        String date = DateFormat.format("dd.mm.yyyy hh:mm:ss", cal).toString();
        return date;
    }

    public void postSession(String name, String beschreibung) {

        String URL = "http://pi-bo.dd-dns.de:8080/LM-Server/api/v2/track";
        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("name", name);
            jsonBody.put("beschreibung", beschreibung);
            jsonBody.put("teamid", 25);
            final String requestBody = jsonBody.toString();

            RequestQueue requestQueue = Volley.newRequestQueue(context.getApplicationContext());
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.e("postSession", response);
                    response = response.substring(response.indexOf(":") + 1);
                    response = response.substring(0, response.indexOf(","));
                    trackid = Integer.parseInt(response);
                    GPS.getInstance().textInputTrackid.setText(getTrackid()+"");
                    postTrackStarten = true;

                    Log.e("postTrackStarten", getPostTrackStarten()+"");
                }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("postSession ERROR", error.toString());
                        }
                    }
            )
            {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return requestBody == null ? null : requestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                        return null;
                    }
                }
            };
            requestQueue.add(stringRequest);
        }
        catch(JSONException e){
            e.printStackTrace();
        }
    }

    public void postData(double latitude, double longitude, double altitude) {

        counter++;

        String URL = "http://pi-bo.dd-dns.de:8080/LM-Server/api/v2/data";
        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("teamid", 25);
            jsonBody.put("latitude", latitude);
            jsonBody.put("longitude", longitude);
            jsonBody.put("altitude", altitude);
            jsonBody.put("timestamp", TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
            jsonBody.put("trackid", getTrackid());
            jsonBody.put("session", GPS.getInstance().spinnerGPSPriority.getSelectedItem().toString());
            jsonBody.put("counter", counter);
            final String requestBody = "["+jsonBody.toString()+"]";
            Log.e("Test", requestBody);

            RequestQueue requestQueue = Volley.newRequestQueue(context.getApplicationContext());
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.e("postTrack", response);
                }
            },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("postTrack ERROR", error.toString());
                        }
                    }
            )
            {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return requestBody == null ? null : requestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                        return null;
                    }
                }
            };
            requestQueue.add(stringRequest);
        }
        catch(JSONException e){
            e.printStackTrace();
        }
    }

    public int getTrackid() {
        return trackid;
    }

    public boolean getPostTrackStarten(){
        return postTrackStarten;
    }

    public void resetCounter(){
        counter = 0;
    }

}