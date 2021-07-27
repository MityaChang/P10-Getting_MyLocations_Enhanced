package sg.edu.rp.c302.id19034275.p10_getting_mylocations_enhanced;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.IBinder;

import java.io.File;


public class MusicService extends Service {
    // ToDo: Declaring object of MediaPlayer
    private MediaPlayer player = new MediaPlayer();

    public MusicService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC), "music.mp3");
            // specify the path of audio file
            player.setDataSource(file.getPath());
            player.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // providing the boolean value as true to play the audio on Loop
        player.setLooping(true);

        // starting the process
        player.start();

        // returns the status of the program
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // stopping the process
        player.stop();
    }
}