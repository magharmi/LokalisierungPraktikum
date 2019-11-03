package com.example.praktikum.Netzwerkpositionierung;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;

import com.example.praktikum.GPS;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.os.Looper;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.praktikum.R;

import java.io.IOException;
import java.util.List;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class NetzwerkP_Activity extends AppCompatActivity {



    public LocationRequest locationRequest;
    public Geocoder geocoader;
    private TextView netzwerkPText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_netzwerk_p_);
        if(ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION}, 123);
            Toast.makeText(getApplicationContext(),"Berechtigungen nicht gegeben", Toast.LENGTH_SHORT).show();
        }
        geocoader = new Geocoder(getApplicationContext());
        initReq();
        initFusedClient();
    }


    private LocationRequest initReq(){
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(2000);
        locationRequest.setFastestInterval(1000);
        return locationRequest;
    }

    private void initFusedClient(){
        getFusedLocationProviderClient(this).requestLocationUpdates(locationRequest,new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult){
                onLocationChanged(locationResult.getLastLocation());
            }
        }, Looper.myLooper());
    }

    private void onLocationChanged(Location location) {
        try{
            List<Address> adressDaten = geocoader.getFromLocation(location.getLatitude(),location.getLongitude(),1);
            Address address = adressDaten.get(0);
            String adresszeile = address.getAddressLine(0);
            netzwerkPText = findViewById(R.id.netzwerkpText);
            netzwerkPText.setText(
                    "Latitude :" + location.getLatitude()+
                    "\nLongtitude :" + location.getLongitude()
            );

        }catch (IOException e){
            e.printStackTrace();
        }
    }

}
