package com.example.mohamedramadan.mychat;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

public class WelcomeActivity extends AppCompatActivity {
    ImageView cirecl1, cirecl2, cirecl3, send1, send2, send3, send4;
    MediaPlayer mediaPlayer;
    Handler handler, handler1, handler2, handler3, handler4, handler5, handler6, handler7, handler8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcom_activity);
        cirecl1 = findViewById(R.id.circle1);
        cirecl2 = findViewById(R.id.circle2);
        cirecl3 = findViewById(R.id.circle3);
        send1 = findViewById(R.id.circle1_send1);
        send2 = findViewById(R.id.circle3_send1);
        send3 = findViewById(R.id.circle1_send2);
        send4 = findViewById(R.id.circle3_send2);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mediaPlayer = MediaPlayer.create(this, R.raw.sound_message);
        handler2 = new Handler();
                handler2.postDelayed(new Runnable() {
            @Override
            public void run() {
                cirecl1.animate().translationX(0f).setDuration(2000);
            }
        }, 0);
        handler3 = new Handler();
        handler3.postDelayed(new Runnable() {
            @Override
            public void run() {
                cirecl3.animate().translationX(0f).setDuration(2000);
            }
        }, 1000);
       handler1 = new Handler();

        handler1.postDelayed(new Runnable() {
            @Override
            public void run() {
                send1.animate().alpha(1f).setDuration(2000);
                mediaPlayer.start();
            }
        },3000);

        handler4 = new Handler();
        handler4.postDelayed(new Runnable() {
            @Override
            public void run() {
                send2.animate().alpha(1f).setDuration(2000);
            }
        }, 4000);
        handler5 = new Handler();
        handler5.postDelayed(new Runnable() {
            @Override
            public void run() {
                send3.animate().alpha(1f).setDuration(2000);
            }
        }, 5000);
        handler6 = new Handler();
        handler6.postDelayed(new Runnable() {
            @Override
            public void run() {
                send4.animate().alpha(1f).setDuration(2000);
            }
        }, 6500);

        handler7 = new Handler();
        handler7.postDelayed(new Runnable() {
            @Override
            public void run() {
                mediaPlayer.release();
            }
        }, 10000);
        handler8 = new Handler();
        handler8.postDelayed(new Runnable() {
            @Override
            public void run() {
                cirecl2.animate().translationY(0f).setDuration(2000);
            }
        }, 7000);

        handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        }, 11000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mediaPlayer.release();
        handler.removeCallbacksAndMessages(null);
        handler1.removeCallbacksAndMessages(null);
        handler2.removeCallbacksAndMessages(null);
        handler3.removeCallbacksAndMessages(null);
        handler4.removeCallbacksAndMessages(null);
        handler5.removeCallbacksAndMessages(null);
        handler6.removeCallbacksAndMessages(null);
        handler7.removeCallbacksAndMessages(null);
        handler8.removeCallbacksAndMessages(null);
    }

}
