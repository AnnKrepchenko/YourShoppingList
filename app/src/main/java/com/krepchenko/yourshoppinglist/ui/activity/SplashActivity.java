package com.krepchenko.yourshoppinglist.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.krepchenko.yourshoppinglist.R;
import com.krepchenko.yourshoppinglist.ui.animation.FrameAnimator;

/**
 * Created by Ann on 13.08.2016.
 */
public class SplashActivity extends Activity implements View.OnClickListener {

    private static String FIRST_START = "first_start";

    private ImageView animationIv;
    private TextView appTv;
    private ImageButton nextIbt;

    private SharedPreferences defaultSharedPreferences;
    private FrameAnimator frameAnimation;
    private static final int[] IMAGE_RESOURCES = {R.drawable.launcher1,
            R.drawable.launcher2,
            R.drawable.launcher3,
            R.drawable.launcher4,
            R.drawable.launcher5,
            R.drawable.launcher6,
            R.drawable.launcher8,
            R.drawable.launcher9,
            R.drawable.launcher10,
            R.drawable.launcher11,
            R.drawable.launcher12,
            R.drawable.launcher13,
            R.drawable.launcher14,
            R.drawable.launcher15,
            R.drawable.launcher16,
            R.drawable.launcher17};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spash);
        defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        appTv = (TextView) findViewById(R.id.splash_tv);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/Torhok.ttf");
        appTv.setTypeface(typeface);
        animationIv = (ImageView) findViewById(R.id.splash_imv1);
        animationIv.setOnClickListener(this);
        nextIbt = (ImageButton) findViewById(R.id.splash_next_btn);
        nextIbt.setOnClickListener(this);
        if (defaultSharedPreferences.getBoolean(FIRST_START, false)) {
            nextIbt.setVisibility(View.GONE);
            timer.start();
        }
        frameAnimation = new FrameAnimator(this);
        frameAnimation.setResources(IMAGE_RESOURCES);
        frameAnimation.setImageView(animationIv);
        frameAnimation.start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.splash_imv1:
                frameAnimation.stop();
                frameAnimation.start();
                break;
            case R.id.splash_next_btn:
                startMain();
                defaultSharedPreferences.edit().putBoolean(FIRST_START, true).commit();
                break;
        }
    }

    private void startMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    CountDownTimer timer = new CountDownTimer(3000, 3000) {
        @Override
        public void onTick(long millisUntilFinished) {

        }

        @Override
        public void onFinish() {
            startMain();
        }
    };
}
