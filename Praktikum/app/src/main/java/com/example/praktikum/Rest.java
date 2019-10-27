package com.example.praktikum;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
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

public class Rest {
    static Rest rest;

    int trackid;

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
                    Log.e("trackid", getTrackid()+"");
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

        String URL = "http://pi-bo.dd-dns.de:8080/LM-Server/api/v2/data";
        try {
            Log.e("Test", "ok");
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("teamid", 25);
            jsonBody.put("latitude", latitude);
            jsonBody.put("longitude", longitude);
            jsonBody.put("altitude", altitude);
            jsonBody.put("timestamp", 5123124);
            jsonBody.put("trackid", getTrackid());
            jsonBody.put("session", "Low Power");
            jsonBody.put("counter", 1);
            final String requestBody = jsonBody.toString();
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

}