package sg.edu.rp.c302.id19034275.p10_getting_mylocations_enhanced;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.File;
import java.io.FileWriter;

public class MainActivity extends AppCompatActivity {
    private GoogleMap map;
    Button btnUpdate, btnRemove, btnRecords;
    TextView tvlatlng;
    LocationRequest mLocationRequest;
    LocationCallback mLocationCallback;
    FusedLocationProviderClient client;
    LatLng poi_Marker;
    String folderLocation;
    ToggleButton toggleMusic;
    //    Double lat,log;
    boolean toggleCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnUpdate = findViewById(R.id.btnStartDector);
        btnRemove = findViewById(R.id.btnStopDector);
        btnRecords = findViewById(R.id.btnRecords);
        tvlatlng = findViewById(R.id.tvLatLng);
        toggleMusic = findViewById(R.id.tbMusic);

        String[] permission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        ActivityCompat.requestPermissions(MainActivity.this, permission, 0);
        if(checkPermission()){
            client = LocationServices.getFusedLocationProviderClient(MainActivity.this);
            Task<Location> task = client.getLastLocation();
            task.addOnSuccessListener(MainActivity.this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    // Log.i("check",location.toString());
                    String msg ;
                    if (location != null) {
                        msg = "Last known location: \nLatititude: " + location.getLatitude() + "\nLongtitude: " + location.getLongitude();
                        poi_Marker = new LatLng(location.getLatitude(), location.getLongitude());
                        msg = "Marker Exists";
                    } else {
                        msg = "No last known location found";
                    }
                    tvlatlng.setText(msg);
                    Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                }
            });
        }



        FragmentManager fm = getSupportFragmentManager();
        SupportMapFragment mapFragment = (SupportMapFragment) fm.findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;
                UiSettings ui = map.getUiSettings();
                ui.setCompassEnabled(true);
                ui.setZoomControlsEnabled(true);
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(1.4431133093468227, 103.78554439536298),15));
                int permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION);

                if (permissionCheck == PermissionChecker.PERMISSION_GRANTED) {
                    map.setMyLocationEnabled(true);
                } else {
                    Log.e("GMap - Permission", "GPS access has not been granted");
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
                } }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (checkPermission()) {
//                    Intent i = new Intent(MainActivity.this,MyServiceLocation.class);
//                    bindService(i,connection,BIND_AUTO_CREATE);
                    startService(new Intent(MainActivity.this,DetectorService.class));
                } else {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                }
            }
        });
        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkPermission()) {
                    stopService(new Intent(MainActivity.this,DetectorService.class));
                } else {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                }
            }
        });
        btnRecords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this,CheckRecords.class);
                startActivity(i);
            }
        });
        toggleMusic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    toggleCheck = true;
                    startService(new Intent(MainActivity.this,MusicService.class));

                }else{
                    toggleCheck = false;
                    stopService(new Intent(MainActivity.this,MusicService.class));
                }
            }
        });


    }

    private boolean checkPermission() {
//        int permissionCheck_Coarse = ContextCompat.checkSelfPermission(
//                MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int permissionCheck_Fine = ContextCompat.checkSelfPermission(
                MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);

        if (
//                permissionCheck_Coarse == PermissionChecker.PERMISSION_GRANTED ||
                permissionCheck_Fine == PermissionChecker.PERMISSION_GRANTED
        ) {
            return true;
        } else {
//            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},0);
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(checkPermission()){
            client = LocationServices.getFusedLocationProviderClient(MainActivity.this);
            Task<Location> task = client.getLastLocation();
            task.addOnSuccessListener(MainActivity.this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    String msg ;
                    if (location != null) {
                        msg = "Last known location: \nLatititude: " + location.getLatitude() + "\nLongtitude: " + location.getLongitude();
                        poi_Marker = new LatLng(location.getLatitude(), location.getLongitude());

                    } else {
                        msg = "No last known location found";
                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                    tvlatlng.setText(msg);

                }
            });
            if (toggleCheck){
                toggleMusic.setChecked(true);
            }else{
                toggleMusic.setChecked(false);
            }
        }
    }
}