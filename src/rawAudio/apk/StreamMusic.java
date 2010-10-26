package rawAudio.apk;

import java.io.IOException;

import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;

import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import android.util.Log;

public class StreamMusic extends Service {
    private static final String APP = "StreamMusic";

    private static MediaPlayer mediaPlayer = null;
    private static String      STREAM      = "";

    private final Binder binder = new LocalBinder();

    private ProgressDialog pd;

    @Override public IBinder onBind(Intent intent) {
        return(binder);
    }

    @Override public void onDestroy() {
        // stop the music
        mediaPlayer.stop();
        mediaPlayer = null;

        super.onDestroy();
    }

    public class LocalBinder extends Binder {
        StreamMusic getService() {
            return (StreamMusic.this);
        }
    }
    
    // called from the main activity
    synchronized public void playPause(Context _c, String _s) {
        if (mediaPlayer == null) {
            // show progress dialog
            pd = ProgressDialog.show(_c, "Raw Audio", "connecting to stream...", true, true);

            // not playing -> load stream
            STREAM = _s;
            loadStream();
        }
        else if (mediaPlayer.isPlaying()) {
            // playing -> pause
            mediaPlayer.pause();
        } 
        else {
            // paused -> resume
            mediaPlayer.start();
        }
    }

    // load the stream in another thread
    private void loadStream() {
        // run the stream in its own thread
        Runnable r = new Runnable() {
            public void run() {
                try {
                    mediaPlayer = new MediaPlayer();

                    mediaPlayer.setDataSource(STREAM);
                    mediaPlayer.prepare();
                    mediaPlayer.start();

                    // dismiss the dialog
                    handler.sendEmptyMessage(0);
                }
                catch (IOException e) {
                    Log.e(APP, "error loading stream " + STREAM + ".", e);
                    return;
                }
            }
        };

        new Thread(r).start();
    }

    // a handler to dismiss the dialog once mp starts
    private Handler handler = new Handler() {
        @Override public void handleMessage(Message msg) {
            pd.dismiss();
        }
    };
}
