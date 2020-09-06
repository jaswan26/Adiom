package com.homeautomation.adiom;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pedro.vlc.VlcListener;
import com.pedro.vlc.VlcVideoLibrary;
import java.util.Arrays;
import org.videolan.libvlc.MediaPlayer;

/**
 * Created by pedro on 25/06/17.
 */
public class MainActivity extends AppCompatActivity implements VlcListener {

    private VlcVideoLibrary vlcVideoLibrary;
    // private Button bStartStop;
    //  private EditText etEndpoint;
    private String streamUrl = null;

    private String[] options = new String[]{":fullscreen"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);

        //etEndpoint = (EditText) findViewById(R.id.et_endpoint);
        // etEndpoint.setVisibility(View.INVISIBLE);
        // Read from the database

        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("streamurl");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                streamUrl = dataSnapshot.getValue(String.class);
                // etEndpoint.setText(value);

                Log.d("Streming Activity", "Value is: " + streamUrl);

                if(streamUrl.toString().length()>0){
                    vlcVideoLibrary.play(streamUrl);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("Streming Activity", "Failed to read value.", error.toException());
            }
        });
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.surfaceView);



        vlcVideoLibrary = new VlcVideoLibrary(this, this, surfaceView);
        vlcVideoLibrary.setOptions(Arrays.asList(options));
    }

    @Override
    public void onComplete() {
        Toast.makeText(this, "Playing", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onError() {
        Toast.makeText(this, "Error, make sure your endpoint is correct", Toast.LENGTH_SHORT).show();
        vlcVideoLibrary.stop();

    }

    @Override
    public void onBuffering(MediaPlayer.Event event) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        vlcVideoLibrary.stop();
        finish();
    }



//    @Override
//    public void onClick(View view) {
//        if (!vlcVideoLibrary.isPlaying()) {
//            vlcVideoLibrary.play(etEndpoint.getText().toString());
//            bStartStop.setText(getString(R.string.stop_player));
//        } else {
//            vlcVideoLibrary.stop();
//            bStartStop.setText(getString(R.string.start_player));
//        }
//    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
