package com.example.ximena.tc3_ximenabolannos_2015073844;

import android.content.Context;
import android.content.res.Resources;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
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
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity {
    private final ArrayList<Song> names = new ArrayList<>();
    private final  ArrayList<ArrayList> lyrics =new ArrayList<ArrayList>();
    private final ArrayList<Integer> files =new ArrayList<>();
    private MediaPlayer mediaPlayer;
    private AudioManager audioManager;
    private SeekBar advancedSeekBar;
    private int indexSong = 0;
    private boolean playOn= false;
    private Animation animation1;
    private Button button;
    private double duration;
    private double song;
    private double currentLine=0;
    private double nextLine;
    private String time;

    static class Song {
        String song_name;
        String singer;

        public Song(String song_name, String singer) {
            this.song_name = song_name;
            this.singer = singer;
        }

        public String getSong_name() {
            return song_name;
        }

        public void setSong_name(String song_name) {
            this.song_name = song_name;
        }

        public String getSinger() {
            return singer;
        }

        public void setSinger(String singer) {
            this.singer = singer;
        }
    }

    public static class CustomListAdapter extends BaseAdapter {
        private ArrayList<Song> listData;
        private LayoutInflater layoutInflater;

        public CustomListAdapter(Context aContext, ArrayList<Song> listData) {
            this.listData = listData;
            layoutInflater = LayoutInflater.from(aContext);
        }

        @Override
        public int getCount() {
            return listData.size();
        }

        @Override
        public Object getItem(int position) {
            return listData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.list_row_layout, null);
                holder = new ViewHolder();
                holder.headlineView = (TextView) convertView.findViewById(R.id.name);
                holder.reporterNameView = (TextView) convertView.findViewById(R.id.singer);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.headlineView.setText(listData.get(position).getSong_name());
            holder.reporterNameView.setText(listData.get(position).getSinger());

            return convertView;
        }

        static class ViewHolder {
            TextView headlineView;
            TextView reporterNameView;

        }
    }
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
            seekBarsManager();

            //moveSongAdvancedSeekBar();
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
        seekBarsManager();

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
        seekBarsManager();

    }

    public void listRaw(){
        Field[] fields=R.raw.class.getFields();
        Resources res = getResources(); //resource handle
        for(int count=0; count < fields.length; count++){

            Integer resIdSound = res.getIdentifier (fields[count].getName(),  "raw", this.getPackageName());
            String name=fields[count].getName();
            String[] song_singer = name.split("0");
            String[] parts = song_singer[0].split("_");
            String[] singer = song_singer[1].split("_");

            System.out.println(parts[0]);
            System.out.println(!parts[0].equals("lyric"));
            if(!parts[0].equals("lyric")){

                Song song= new Song(fix_name(parts),fix_name(singer));
                names.add(song);

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
        currentLine=0;
        new Timer().scheduleAtFixedRate(

                new TimerTask() {

                    double numAux = currentLine;
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                    currentLine= mediaPlayer.getCurrentPosition()/song;
                                    System.out.println(currentLine);
                                    if (currentLine!=numAux) {
                                        if(lyrics.get(indexSong).size()>currentLine){
                                            animation((String) lyrics.get(indexSong).get((int) currentLine));
                                        }
                                        numAux = currentLine;
                                    }
                                    advancedSeekBar.setProgress(mediaPlayer.getCurrentPosition());



                            }
                        });
                    }
                }, 0, 1000
        );


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
        seekBarsManager();
        duration= mediaPlayer.getDuration();
        song= duration/(lyrics.get(indexSong).size());
        Log.d("duration", String.valueOf(duration));

        playOn=false;
        button.setBackgroundResource(R.drawable.play);
        if(!playOn){
            button.setBackgroundResource(R.drawable.pause);
            mediaPlayer.start();

            playOn=true;

        }


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

        listView.setAdapter(new CustomListAdapter(this, names));

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
        moveSongAdvancedSeekBar();

    }


}
