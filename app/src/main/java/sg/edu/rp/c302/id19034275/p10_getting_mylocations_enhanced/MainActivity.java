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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.File;
import java.io.FileWriter;

public class MainActivity extends AppCompatActivity {
    Button btnStartDetector, btnStopDetector, btnShowRecords;
    TextView tvlatlng;
    ToggleButton tbMusic;
    private GoogleMap map;
    FusedLocationProviderClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnShowRecords = findViewById(R.id.btnRecords);
        tbMusic = findViewById(R.id.tbMusic);
        btnStartDetector = findViewById(R.id.btnStartDector);
        btnStopDetector = findViewById(R.id.btnStopDector);
        tvlatlng = findViewById(R.id.tvLatLng);


        client = LocationServices.getFusedLocationProviderClient(MainActivity.this);

        FragmentManager fm = getSupportFragmentManager();
        SupportMapFragment mapFragment = (SupportMapFragment)
                fm.findFragmentById(R.id.map);


        // Map Async
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;

                UiSettings ui = map.getUiSettings();
                ui.setZoomControlsEnabled(true);
                ui.setCompassEnabled(true);


                if (checkPermission() == true) {
                    Task<Location> task = client.getLastLocation();
                    task.addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            String msg;
                            if (location != null) {
                                msg = "Latititude: " + location.getLatitude() + "\nLongtitude: " + location.getLongitude();
                                LatLng lastKnownLoc = new LatLng(location.getLatitude(), location.getLongitude());
                                map.moveCamera(CameraUpdateFactory.newLatLngZoom(lastKnownLoc, 15));
                                map.addMarker(new MarkerOptions()
                                        .position(lastKnownLoc)
                                        .title("Here is your last location")
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));

                            } else {
                                msg = "No Last Known Location found";
                                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                            }
                            try {
                                String folder = getFilesDir().getAbsolutePath() + "/P10";
                                File targetFile = new File(folder, "P10LocationData.txt");
                                FileWriter writer = new FileWriter(targetFile, true);
                                writer.write(location.getLatitude() + "," + location.getLongitude() + "\n");
                                writer.flush();
                                writer.close();
                            } catch (Exception e) {
                                Toast.makeText(MainActivity.this, "Failed to write!", Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }
                            tvlatlng.setText(msg);

                        }
                    });
                } else {
                    Toast.makeText(MainActivity.this, "No Permission.", Toast.LENGTH_LONG).show();
                }
            }
        });


        btnStartDetector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, DetectorService.class);
                startService(i);

            }
        });

        btnStopDetector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, DetectorService.class);
                stopService(i);
                Toast.makeText(getApplicationContext(), "Services stopped.", Toast.LENGTH_SHORT).show();
            }

        });

        btnShowRecords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, CheckRecords.class);
                startActivity(i);
            }
        });


        tbMusic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true) {
                    startService(new Intent(MainActivity.this, MusicService.class));
                } else {
                    stopService(new Intent(MainActivity.this, MusicService.class));
                }
            }
        });
    }

    private boolean checkPermission() {
        int permissionCheck_Fine = ContextCompat.checkSelfPermission(
                MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);

        if (permissionCheck_Fine == PermissionChecker.PERMISSION_GRANTED) {
            return true;
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
            return false;
        }
    }
}