package com.example.dmoney.beeping;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.CountDownTimer;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import java.io.IOException;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import static android.R.attr.handle;

public class Mp3player extends Activity {

    private Button buttonPlayStop;
    private MediaPlayer mediaPlayer;
    private SeekBar seekBar;
    private final Handler handler = new Handler();
    private AudioManager audioManager;
    private RingtoneManager ringtone;
    private Timer timer = new Timer();

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_main);
        RelativeLayout color = (RelativeLayout) findViewById(R.id.activity_main);
        initViews();
        flashtime();


        Intent BATTERYintent = this.registerReceiver(null, new IntentFilter(
                Intent.ACTION_BATTERY_CHANGED));
        int level = BATTERYintent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        Log.v(null, "LEVEL" + level);


        //We change the color to RED for the first time as the program loads
        //Create the timer object which will run the desired operation on a schedule or at a given time
        //Timer timer = new Timer();

        //Create a task which the timer will execute.  This should be an implementation of the TimerTask interface.
        //I have created an inner class below which fits the bill.
        MyTimer mt = new MyTimer();



        final AudioManager mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        final int originalVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
        MediaPlayer mp = new MediaPlayer();
        mp.setAudioStreamType(AudioManager.STREAM_MUSIC);


        //We schedule the timer task to run after 1000 ms and continue to run every 1000 ms.
        if(level > 20) {
            timer.schedule(mt, 100, 100);
            mediaPlayer.start();
            mediaPlayer.setLooping(true);
            startPlayProgressUpdater();
        }



    }
    private void initViews() {
        buttonPlayStop = (Button) findViewById(R.id.ButtonPlayStop);
        buttonPlayStop.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonClick();
            }
        });

        mediaPlayer = MediaPlayer.create(this, R.raw.sound);
        seekBar = (SeekBar) findViewById(R.id.SeekBar01);
        seekBar.setMax(mediaPlayer.getDuration());
        seekBar.setOnTouchListener(new OnTouchListener() {
            @Override
               public boolean onTouch(View v, MotionEvent event) {
                seekChange(v);
                return false;
            }
        });
    }

    public void startPlayProgressUpdater() {
        seekBar.setProgress(mediaPlayer.getCurrentPosition());

        if (mediaPlayer.isPlaying()) {
            Runnable notification = new Runnable() {
                public void run() {
                    startPlayProgressUpdater();
                }
            };
            handler.postDelayed(notification, 1000);
        } else {
            mediaPlayer.pause();
            buttonPlayStop.setText(getString(R.string.stop_str));
            seekBar.setProgress(0);
        }
    }

    private void seekChange(View v) {

        //begin of change
        //end of change

        if (mediaPlayer.isPlaying()) {
            SeekBar sb = (SeekBar) v;
            mediaPlayer.seekTo(sb.getProgress());
        }

    }

    private void buttonClick() {

        buttonPlayStop.setText(getString(R.string.stop_str));
        timer.cancel();
        mediaPlayer.stop();
        finish();

    }


    //An inner class which is an implementation of the TImerTask interface to be used by the Timer.
    class MyTimer extends TimerTask {

        public void run() {

            //<activity android:name=".Flash"/>

            //This runs in a background thread.
            //We cannot call the UI from this thread, so we must call the main UI thread and pass a runnable
            runOnUiThread(new Runnable() {

                public void run() {
                    Random random = new Random();
                    int rendomNumber = random.nextInt(3);
                    RelativeLayout color = (RelativeLayout) findViewById(R.id.activity_main);
                    if(rendomNumber == 1)
                        color.setBackgroundColor(Color.RED);
                    if(rendomNumber == 2)
                        color.setBackgroundColor(Color.BLUE);
                    if(rendomNumber ==3)
                        color.setBackgroundColor(Color.BLUE);
                    //The random generator creates values between [0,256) for use as RGB values used below to create a random color
                    //We call the RelativeLayout object and we change the color.  The first parameter in argb() is the alpha.
                    //color.setBackgroundColor(Color.argb(255, rand.nextInt(256), rand.nextInt(256), rand.nextInt(256)));
                }

            });

            /////////////////////////////////////
        }

    }

    public void flashtime()
    {
        new CountDownTimer(6000, 1000)
        {
            public void onTick(long millisUntilFinished)
            {

            }

            public void onFinish()
            {
                timer.cancel();
                mediaPlayer.stop();
            finish();
            }
        }.start();
    }


}