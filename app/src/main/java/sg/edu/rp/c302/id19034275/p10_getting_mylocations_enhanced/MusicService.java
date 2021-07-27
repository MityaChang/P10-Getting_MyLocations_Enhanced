package sg.edu.rp.c302.id19034275.p10_getting_mylocations_enhanced;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.io.FileWriter;

public class MusicService extends Service {
    private MediaPlayer player = new MediaPlayer();

    public MusicService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Music", "music.mp3");
            player.setDataSource(file.getPath());
            player.prepare();

        } catch (Exception e) {
            e.printStackTrace();
        }

        player.setLooping(true);
        player.start();
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        player.stop();
    }
}