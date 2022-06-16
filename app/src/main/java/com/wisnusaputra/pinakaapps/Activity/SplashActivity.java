package com.wisnusaputra.pinakaapps.Activity;

import android.content.Intent;
import android.os.Handler;

import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.wisnusaputra.pinakaapps.R;

public class SplashActivity extends AppCompatActivity {

    TextView titlepage, subtitlepage;
    ImageView icstates;
    Animation btt, bttwo, imganim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        btt = AnimationUtils.loadAnimation(this, R.anim.btt);
        bttwo = AnimationUtils.loadAnimation(this, R.anim.bttwo);
        imganim = AnimationUtils.loadAnimation(this, R.anim.imganim);

        icstates = findViewById(R.id.icstates);
        titlepage = findViewById(R.id.titlepage);
        subtitlepage = findViewById(R.id.subtitlepage);

        icstates.startAnimation(imganim);
        titlepage.startAnimation(btt);
        subtitlepage.startAnimation(bttwo);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }
        },3200);
    }
}
