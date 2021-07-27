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

import java.io.File;
import java.io.FileWriter;

public class DetectorService extends Service {
    LocationRequest mLocationRequest = LocationRequest.create();
    LocationCallback mLocationCallback;
    FusedLocationProviderClient client;

    public DetectorService() {
    }

    @Override
    public void onCreate() {
        Log.d("MyService", "Service created");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("MyService", "Service started");
        client = LocationServices.getFusedLocationProviderClient(DetectorService.this);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null) {
                    Location data = locationResult.getLastLocation();
                    double lat = data.getLatitude();
                    double lng = data.getLongitude();
                    Toast.makeText(getApplicationContext(), lat + ", " + lng, Toast.LENGTH_SHORT).show();

                    // Folder creation
                    String folderLocation = getFilesDir().getAbsolutePath() + "/P10";
                    File folder = new File(folderLocation);
                    if (!folder.exists()) {
                        boolean result = folder.mkdir();
                        if (result)
                            Log.d("File Read/Write", "Folder created");
                        else
                            Log.d("File Read/Write", "Folder failed to create");
                    } else
                        Log.d("File Read/Write", "Folder already exist");

                    // File creation and writing
                    try {
                        File targetFile = new File(folderLocation, "P10LocationData.txt");
                        FileWriter writer = new FileWriter(targetFile, true);
                        writer.write(lat + ", " + lng + "\n");
                        writer.flush();
                        writer.close();
                    } catch (Exception e) {
                        Toast.makeText(DetectorService.this, "Failed to write!", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }
            }
        };
        if (checkPermission() == true) {

            mLocationRequest = LocationRequest.create();
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mLocationRequest.setInterval(30);
            mLocationRequest.setFastestInterval(100);
            mLocationRequest.setSmallestDisplacement(500);
            client.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d("MyService", "Service exited");
        client.removeLocationUpdates(mLocationCallback);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // Step 3b
    private boolean checkPermission() {
        int permissionCheck_Fine = ContextCompat.checkSelfPermission(DetectorService.this, Manifest.permission.ACCESS_FINE_LOCATION);

        if (permissionCheck_Fine == PermissionChecker.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }
}