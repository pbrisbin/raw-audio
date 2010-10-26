package rawAudio.apk;

import android.app.Activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;

import android.os.Bundle;
import android.os.IBinder;

import android.util.Log;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class RawAudio extends Activity implements OnClickListener {
    private static final String APP = "RawAudio";

    // our background player service
    private StreamMusic streamMusic = null;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // setup ui
        setContentView(R.layout.main);

        // setup play/pause button
        Button ppButton = (Button)findViewById(R.id.playpause);
        ppButton.setOnClickListener(this);

        // connect/create our service
        connectToService();
    }

    @Override public void onDestroy() {
        // disconnect from our service
        unbindService(onService);

        while (streamMusic != null) {
            try {
                Thread.sleep(250);
            }
            catch (InterruptedException e) {
                Log.e(APP, "sleep command interrupted", e);
            }
        }

        super.onDestroy();
    }

    public void onClick(View v) {
        if (streamMusic != null) {
            EditText t = (EditText)findViewById(R.id.entry);
            String   s = t.getText().toString();

            // send play/pause command
            streamMusic.playPause(this, s);
        } 
        else {
            Log.d(APP, "service not bound yet.");
        }
    }

    // establish the connection
    private void connectToService() {
        try {
            bindService(new Intent(this, StreamMusic.class), onService, BIND_AUTO_CREATE);
        }
        catch (Exception e) {
            Log.e(APP, "error binding to service.", e);
        }
    }

    // once connected, set up the interface object
    private ServiceConnection onService = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder iBinder) {
            streamMusic = ((StreamMusic.LocalBinder)iBinder).getService();
        }

        public void onServiceDisconnected(ComponentName className) {
            streamMusic = null;
        }
    };
}
