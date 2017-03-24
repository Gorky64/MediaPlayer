package com.example.fs02.mediaplayer;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import android.media.MediaPlayer.OnPreparedListener;

import static android.R.attr.button;
import static android.R.attr.resumeWhilePausing;
import static android.R.attr.start;

public class MainActivity extends AppCompatActivity {

    File audiofile = null;
    MediaRecorder recorder;
    Button btnMPplayer;
    Button btnMPplayerStop;
    Button btnSPool;
    Button btnMRStart;
    Button btnMRStop;
    private MediaPlayer mediaPlayer;
    //SOUNDPOOL
    private SoundPool soundPool;
    private int soundID1;
    private int playedSoundID1;
    boolean loaded = false;
    boolean mSoundManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        VideoView videoView = (VideoView) findViewById(R.id.vvVideoView);
        btnMPplayer = (Button) findViewById(R.id.btnMPplayer);
        btnMPplayerStop = (Button) findViewById(R.id.btnMPplayerStop);
        btnSPool = (Button) findViewById(R.id.btnSPool);
        btnMRStart = (Button) findViewById(R.id.btnMRStart);
        btnMRStop = (Button) findViewById(R.id.btnMRStop);

        soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleID, int status) {
                loaded=true;
            }
        });
        soundID1 = soundPool.load(this, R.raw.g0_ogg,1);


        videoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.video));
        videoView.setMediaController(new MediaController(this));
        videoView.requestFocus();

        mp3();
        initSoundPool();
        recording();

    }

    public void initSoundPool() {
        btnSPool.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {
                if(motionEvent.getAction()==MotionEvent.ACTION_DOWN)

                {
                if (loaded){
                    soundPool.play(soundID1, 1.0f,1.0f,0,0,1.0f);
                            }
                }
                if(motionEvent.getAction()==MotionEvent.ACTION_UP){
                    soundPool.stop(playedSoundID1);
                    playedSoundID1= soundPool.play(soundID1, 1.0f,1.0f,0,0,1.0f);
                }
                return false;
            }
        });
    }

    public void mp3() {

        btnMPplayer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    bass();
                    mediaPlayer.start();
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {

                    mediaPlayer.stop();
                }
                return false;
            }
        });

        btnMPplayerStop.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    song();
                    mediaPlayer.start();
                }
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    mediaPlayer.stop();
                }
                return false;
            }
        });


    }


    public void recording() {
        btnMRStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startRecording();
            }
        });
        btnMRStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopRecording();
            }
        });
    }

    private void startRecording() {
        btnMRStart.setEnabled(false);
        btnMRStop.setEnabled(true);
        File sampleDir = Environment.getExternalStorageDirectory();
        try {
            audiofile = File.createTempFile("zvuk", ".3gp", sampleDir);
        } catch (IOException e) {
            return;
        }
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setOutputFile(audiofile.getAbsolutePath());
        try {
            recorder.prepare();
        } catch (IllegalStateException | IOException e) {
            e.printStackTrace();
        }
        recorder.start();
    }

    private void bass() {
        mediaPlayer = MediaPlayer.create(this, R.raw.g0_mp3);
    }

    private void song() {
        mediaPlayer = MediaPlayer.create(this, R.raw.song);
    }

    private void stopRecording() {
        btnMRStart.setEnabled(true);
        btnMRStop.setEnabled(false);
        recorder.stop();
        recorder.release();
        addRecordingToMediaLibrary();
    }

    private void addRecordingToMediaLibrary() {
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(audiofile));
        sendBroadcast(intent);
    }

}

