package com.krepchenko.yourshoppinglist.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.TextView;

import com.krepchenko.yourshoppinglist.R;
import com.krepchenko.yourshoppinglist.ui.animation.EyelidView;

/**
 * Created by Ann on 13.08.2016.
 */
public class SplashActivity extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 3000;

    private Animation operatingAnim, eye_left_Anim;
    private View mouse, eye_left, eye_right;
    private EyelidView eyelid_left, eyelid_right;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.catloading_main);
        mouse = findViewById(R.id.mouse);
        eye_left = findViewById(R.id.eye_left);
        eye_right = findViewById(R.id.eye_right);
        eyelid_left = (EyelidView) findViewById(R.id.eyelid_left);
        eyelid_left.setColor(Color.parseColor("#d0ced1"));
        eyelid_left.setFromFull(true);
        eyelid_right = (EyelidView) findViewById(R.id.eyelid_right);
        initialize();
        timer.start();

    }

    private void initialize() {
        operatingAnim = new RotateAnimation(360f, 0f, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        operatingAnim.setRepeatCount(Animation.INFINITE);
        operatingAnim.setDuration(2000);

        eye_left_Anim = new RotateAnimation(360f, 0f, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        eye_left_Anim.setRepeatCount(Animation.INFINITE);
        eye_left_Anim.setDuration(2000);

        LinearInterpolator lin = new LinearInterpolator();
        operatingAnim.setInterpolator(lin);
        eye_left_Anim.setInterpolator(lin);

        eyelid_right.setColor(Color.parseColor("#d0ced1"));

        eyelid_right.setFromFull(true);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/Torhok.ttf");
        ((TextView) findViewById(R.id.app_tv)).setTypeface(typeface);
        operatingAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                eyelid_left.resetAnimator();
                eyelid_right.resetAnimator();
            }
        });
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        startAnimation();
    }


    private void startMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void startAnimation() {
        mouse.setAnimation(operatingAnim);
        eye_left.setAnimation(eye_left_Anim);
        eye_right.setAnimation(eye_left_Anim);
        eyelid_left.startLoading();
        eyelid_right.startLoading();
    }

    private void stopAnimation() {
        eyelid_left.stopLoading();
        eyelid_right.stopLoading();
    }

    private CountDownTimer timer = new CountDownTimer(SPLASH_TIME_OUT, SPLASH_TIME_OUT) {
        @Override
        public void onTick(long l) {

        }

        @Override
        public void onFinish() {
            stopAnimation();
            startMain();
        }
    };
}
