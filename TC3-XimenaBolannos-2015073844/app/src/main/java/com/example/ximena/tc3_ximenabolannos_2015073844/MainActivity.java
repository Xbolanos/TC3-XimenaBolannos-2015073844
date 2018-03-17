package com.example.ximena.tc3_ximenabolannos_2015073844;

import android.content.Context;
import android.content.res.Resources;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    final ArrayList<String> names =new ArrayList<String>();
    final  ArrayList<ArrayList> lyrics =new ArrayList<ArrayList>();
    final ArrayList<Integer> files =new ArrayList<>();
    MediaPlayer mediaPlayer;
    AudioManager audioManager;
    SeekBar advancedSeekBar;
    int indexSong = 0;
    boolean playOn= false;
    Animation animation1;
    Button button;
    double duration;
    double song;
    double currentLine;
    boolean calledThread=false;


    public void readtxt(int fileRaw){
        BufferedReader reader;
        try{
            final InputStream file = getResources().openRawResource(fileRaw);
            reader = new BufferedReader(new InputStreamReader(file));
            String line = reader.readLine();
            ArrayList<String> lines=new ArrayList<>();
            while(line != null){
                lines.add(line);
                line = reader.readLine();
            }
            lyrics.add(lines);
        } catch(IOException ioe){
            ioe.printStackTrace();
        }

    }

    public void playClick(View view){
        if(!playOn){
            button.setBackgroundResource(R.drawable.pause);
            mediaPlayer.start();
            moveSongAdvancedSeekBar();
            playOn=true;

        }else{
            button.setBackgroundResource(R.drawable.play);
            mediaPlayer.pause();
            playOn=false;
            //animation1.cancel();
        }
    }

    public void moveSongPositive(View view){
        if(indexSong<files.size()-1){
            indexSong++;
        }else{
            indexSong=0;
        }
        if(!playOn){
            mediaPlayer= MediaPlayer.create(getApplicationContext(), files.get(indexSong));
        }else{
            mediaPlayer.stop();
            mediaPlayer= MediaPlayer.create(getApplicationContext(), files.get(indexSong));
            mediaPlayer.start();
        }

    }
    public void moveSongNegative(View view){
        if(indexSong==0){
            indexSong=files.size()-1;
        }else{
            indexSong--;
        }
        if(!playOn){
            mediaPlayer= MediaPlayer.create(getApplicationContext(), files.get(indexSong));
        }else{
            mediaPlayer.stop();
            mediaPlayer= MediaPlayer.create(getApplicationContext(), files.get(indexSong));
            mediaPlayer.start();
        }

    }
    public void listRaw(){
        Field[] fields=R.raw.class.getFields();
        Resources res = getResources(); //resource handle
        for(int count=0; count < fields.length; count++){

            Integer resIdSound = res.getIdentifier (fields[count].getName(),  "raw", this.getPackageName());
            String name=fields[count].getName();
            String[] parts = name.split("_");
            System.out.println(parts[0]);
            System.out.println(!parts[0].equals("lyric"));
            if(!parts[0].equals("lyric")){

                names.add(fix_name(parts));
                files.add(resIdSound);
            }else{
                readtxt(resIdSound);
            }
        }
    }

    public String fix_name(String[] parts){
        String nameSong="";
        for(String part: parts){
            String newPart=part.substring(0, 1).toUpperCase() + part.substring(1);

            nameSong= nameSong.concat(newPart);
            nameSong= nameSong.concat(" ");
        }
        return  nameSong;

    }
    public void seekBarsManager(){
        int duration = mediaPlayer.getDuration();
        int progress= mediaPlayer.getCurrentPosition();
        int maxVolume=audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int currentVolume= audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        SeekBar volumeSeekBar= findViewById(R.id.volumSeekBar);
        volumeSeekBar.setMax(maxVolume);
        volumeSeekBar.setProgress(currentVolume);


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
        advancedSeekBar= findViewById(R.id.advanceSeekBar);
        advancedSeekBar.setMax(duration);
        advancedSeekBar.setProgress(progress);
        advancedSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean fromUser) {
                if(fromUser) {
                    mediaPlayer.seekTo(i);

                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public void moveSongAdvancedSeekBar(){

            new Thread() {

                double numAux = currentLine;

                public void run() {
                    while (playOn) {

                        try {
                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {

                                    currentLine = mediaPlayer.getCurrentPosition() / song;
                                    Log.d("currentLine", String.valueOf(currentLine));
                                    if (currentLine != numAux) {
                                        animation((String) lyrics.get(indexSong).get((int) currentLine));
                                        numAux = currentLine;
                                    }
                                    advancedSeekBar.setProgress(mediaPlayer.getCurrentPosition());


                                }
                            });
                            sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }


                }
            }.start();

    }

    public void animation(String text){
        TextView textView = findViewById(R.id.txtLetter);
        animation1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blink);
        textView.setText(text);
        textView.startAnimation(animation1);
    }
    public void changeSong(int i){
        indexSong=i;
        mediaPlayer.stop();
        mediaPlayer= MediaPlayer.create(getApplicationContext(), files.get(i));
        duration= mediaPlayer.getDuration();
        song= duration/(lyrics.get(indexSong).size());
        Log.d("duration", String.valueOf(duration));

        playOn=false;
        button.setBackgroundResource(R.drawable.play);
        if(!playOn){
            button.setBackgroundResource(R.drawable.pause);
            moveSongAdvancedSeekBar();
            mediaPlayer.start();
            playOn=true;

        }

        seekBarsManager();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button= findViewById(R.id.playbtn);
        listRaw();
        indexSong=0;

        ListView listView=findViewById(R.id.musicListView);

        audioManager= (AudioManager)getSystemService(Context.AUDIO_SERVICE);


        ArrayAdapter<String> arrayAdapter= new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1,names);
        listView.setAdapter(arrayAdapter);
        mediaPlayer= MediaPlayer.create(getApplicationContext(), files.get(indexSong));
        duration= mediaPlayer.getDuration();
        song= duration/(lyrics.get(indexSong).size());

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                changeSong(i);

            }
        });
        seekBarsManager();

    }


}
