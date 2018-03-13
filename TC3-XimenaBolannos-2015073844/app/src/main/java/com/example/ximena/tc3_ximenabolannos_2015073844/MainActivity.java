package com.example.ximena.tc3_ximenabolannos_2015073844;

import android.content.Context;
import android.content.res.Resources;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import static java.util.Arrays.asList;

public class MainActivity extends AppCompatActivity {

    final ArrayList<String> names =new ArrayList<String>();
    final ArrayList<Integer> files =new ArrayList<>();
    MediaPlayer mediaPlayer;
    AudioManager audioManager;
    boolean isActivePlaying=false;

    public void pauseClick(View view){
        mediaPlayer.pause();
    }
    public void playClick(View view){
        mediaPlayer.start();

    }
    public void listRaw(){
        Field[] fields=R.raw.class.getFields();
        Resources res = getResources(); //resource handle

        for(int count=0; count < fields.length; count++){
            names.add(fields[count].getName());

            InputStream is = getClass().getClassLoader().getResourceAsStream("raw/");
            Integer resIdSound = res.getIdentifier (fields[count].getName(),  "raw", this.getPackageName());
            files.add(resIdSound);
            Log.i("Raw Asset: ", fields[count].getName());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listRaw();
        ListView listView=findViewById(R.id.musicListView);
        //Inicializar coleccion
        audioManager= (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        int maxVolume=audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int currentVolume= audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        SeekBar volumeSeekBar= findViewById(R.id.volumSeekBar);
        volumeSeekBar.setMax(maxVolume);
        volumeSeekBar.setProgress(currentVolume);

        ArrayAdapter<String> arrayAdapter= new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1,names);
        listView.setAdapter(arrayAdapter);
        mediaPlayer= MediaPlayer.create(getApplicationContext(), files.get(0));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                mediaPlayer.stop();
                Toast.makeText(getApplicationContext(),names.get(i),Toast.LENGTH_SHORT).show();
                mediaPlayer= MediaPlayer.create(getApplicationContext(), files.get(i));
                mediaPlayer.start();
            }
        });
        int duration = mediaPlayer.getDuration();
        int progress= mediaPlayer.getCurrentPosition();
        //




        volumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean fromUser) {
                Log.d("volume:", Integer.toString(i));
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,i,0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {


            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        final SeekBar advancedSeekBar= findViewById(R.id.advanceSeekBar);

        advancedSeekBar.setMax(duration);
        advancedSeekBar.setProgress(progress);

        advancedSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean fromUser) {
                mediaPlayer.seekTo(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        new Timer().scheduleAtFixedRate(
                new TimerTask() {
                    @Override
                    public void run() {
                        advancedSeekBar.setProgress(mediaPlayer.getCurrentPosition());
                    }
                }, 0, 1000
        );
    }

}
