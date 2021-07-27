package sg.edu.rp.c302.id19034275.p10_getting_mylocations_enhanced;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.io.FileWriter;

public class DetectorService extends Service {
    String folderLocation;
    LocationCallback locationCallback;
    LocationRequest locationRequest;
    FusedLocationProviderClient client;
    boolean started;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!started) {
            started = true;
            client = LocationServices.getFusedLocationProviderClient(getApplicationContext());
            //
            locationRequest = new LocationRequest();
            Log.d("check", "check");
            locationCallback = new LocationCallback() {
                public void onLocationResult(LocationResult locationResult) {
                    if (locationResult != null) {
                        Log.d("check", "check");
                        Location data = locationResult.getLastLocation();
                        Double lat = data.getLatitude();
                        Double log = data.getLongitude();
                        try {
                            folderLocation =
                                    Environment.getExternalStorageDirectory()
                                            .getAbsolutePath() + "/Folder";
                            File folder = new File(folderLocation);
                            if (folder.exists() == false) {
                                boolean result = folder.mkdir();
                                if (result == true) {
                                    Log.d("check", "Folder created");
                                }
                            }
                            try {
                                folderLocation = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Folder";
                                Log.d("check", "checky");
                                File targetFile = new File(folderLocation, "data2.txt");
                                FileWriter writer = new FileWriter(targetFile, true);
                                writer.write(lat + "," + log + "\n");
                                writer.flush();
                                writer.close();
                            } catch (Exception e) {
                                Log.d("check", folderLocation.toString());
                                Toast.makeText(getApplicationContext(), "Failed to write!", Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                            }
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), "Failed to create folder!", Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                        Toast.makeText(getApplicationContext(), lat + "\n" + log, Toast.LENGTH_SHORT).show();


                    }
                }
            };
            if (checkPermission()) {
                Log.d("check", "check2");
                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                locationRequest.setInterval(500);
                locationRequest.setFastestInterval(500);
                locationRequest.setSmallestDisplacement(0);
                client = LocationServices.getFusedLocationProviderClient(getApplicationContext());
                client.requestLocationUpdates(locationRequest, locationCallback, null);
            }
        }

        return START_STICKY;
    }

    private boolean checkPermission() {
        int permissionCheck_Fine = ContextCompat.checkSelfPermission(
                getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION);

        if (
                permissionCheck_Fine == PermissionChecker.PERMISSION_GRANTED
        ) {
            return true;
        } else {
            return false;
        }
    }


    @Override
    public void onDestroy() {
        client.removeLocationUpdates(locationCallback);
        super.onDestroy();
    }
}