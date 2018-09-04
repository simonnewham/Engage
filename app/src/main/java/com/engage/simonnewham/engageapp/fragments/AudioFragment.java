package com.engage.simonnewham.engageapp.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.engage.simonnewham.engageapp.R;
import com.engage.simonnewham.engageapp.activities.ContentActivity;
import com.engage.simonnewham.engageapp.activities.SignUpActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.concurrent.TimeUnit;

/**
 * Class reponsible for handling audio file logic
 *  Adapted from Tutorials Point Media Player Tutorial https://www.tutorialspoint.com/android/android_mediaplayer.htm
 *  @author simonnewham
 */

@SuppressLint("ValidFragment")
public class AudioFragment extends Fragment {

    private static final String TAG = "AudioFragment";

    private FloatingActionButton play;
    private Button pause;
    private FloatingActionButton  forward;
    private FloatingActionButton  backward;
    private SeekBar seekbar;
    private TextView progress;
    private TextView total;

    private String path;
    private MediaPlayer mediaPlayer;

    private double startTime = 0;
    private double finalTime = 0;
    private int forwardTime = 5000;
    private int backwardTime = 5000;
    private Handler myHandler = new Handler();;
    public static int oneTimeOnly = 0;

    private Boolean playing =false;
    Boolean ready = false;

    DownloadAudioTask downloadAudioTask;

    @SuppressLint("ValidFragment")
    public AudioFragment(String path){
        this.path = path;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_audio, container, false);

        play = view.findViewById(R.id.buttonPlay);
        forward = view.findViewById(R.id.buttonForward);
        backward = view.findViewById(R.id.buttonBack);
        progress = view.findViewById(R.id.textProgress);
        seekbar = view.findViewById(R.id.seekBar);
        total = view.findViewById(R.id.textTotal);

        //download media
        mediaPlayer = new MediaPlayer();
        downloadAudioTask = new AudioFragment.DownloadAudioTask();
        downloadAudioTask.execute("https://engage.cs.uct.ac.za"+path);

        seekbar.setClickable(false);
        //seekbar.setMax(mediaPlayer.getDuration());

        play.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){

                if(ready){
                    if(playing){
                        playing = false;
                        mediaPlayer.pause();
                        play.setImageDrawable(ContextCompat.getDrawable(getContext(),R.drawable.ic_arrow));

                    }
                    else{
                        mediaPlayer.start();
                        playing = true;
                        play.setImageDrawable(ContextCompat.getDrawable(getContext(),R.drawable.ic_pause));

                        seekbar.setMax(mediaPlayer.getDuration());
                        finalTime = mediaPlayer.getDuration();

                        total.setText(String.format("%d min, %d sec",
                                TimeUnit.MILLISECONDS.toMinutes((long) finalTime),
                                TimeUnit.MILLISECONDS.toSeconds((long) finalTime)-
                                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                                toMinutes((long) finalTime)))
                        );
                        startTime = mediaPlayer.getCurrentPosition();
                        seekbar.setProgress((int)startTime);
                        myHandler.postDelayed(UpdateSongTime,100);


                    }
                }
            }
        });


        forward.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                if(ready){
                    int temp = (int)startTime;

                    if((temp+forwardTime)<=finalTime){
                        startTime = startTime + forwardTime;
                        mediaPlayer.seekTo((int) startTime);
                    }else{
                        Toast.makeText(getActivity(),"Cannot jump forward 5 seconds",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

       backward.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){

                if(ready){
                    int temp = (int)startTime;

                    if((temp-backwardTime)>0){
                        startTime = startTime - backwardTime;
                        mediaPlayer.seekTo((int) startTime);
                    }else{
                        Toast.makeText(getActivity(),"Cannot jump backward 5 seconds",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        return view;
    }


    private Runnable UpdateSongTime = new Runnable() {
        public void run() {
            if(mediaPlayer!=null){
                startTime = mediaPlayer.getCurrentPosition();
                //startTime = mediaPlayer.getDuration();
                progress.setText(String.format("%d min, %d sec",
                        TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                        TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                        toMinutes((long) startTime)))
                );
                seekbar.setProgress((int)startTime);
                myHandler.postDelayed(this, 100);
            }
        }
    };

    /**
     *
     */
    private class DownloadAudioTask extends AsyncTask<String, Void, String> {
        //MediaPlayer mediaPlayer;

        public DownloadAudioTask() {

            //this.mediaPlayer = mediaPlayer;
        }

        protected String doInBackground(String... urls) {
            String urldisplay = urls[0];

            try {
                String url = urldisplay;
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.setDataSource(url);
                mediaPlayer.prepare();
                return "";

            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return "";
        }

        protected void onPostExecute(String result) {
            downloadAudioTask = null;
            progress.setText("Item Ready");
            ready = true;

        }

        protected void onCancelled() {
            downloadAudioTask = null;

        }

    }

    public void endMedia (){

        mediaPlayer.release();
        mediaPlayer =null;

    }

}
