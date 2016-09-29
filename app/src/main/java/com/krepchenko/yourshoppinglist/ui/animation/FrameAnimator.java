package com.krepchenko.yourshoppinglist.ui.animation;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.ImageView;

/**
 * Created by Ann on 13.08.2016.
 */
public class FrameAnimator implements AnimationListener {

    private Context context;
    private int[] resources;
    private ImageView imageView;
    private int currentViewNum;


    public FrameAnimator(Context context) {
        this.context = context;
    }

    public void setResources(int[] resources) {
        this.resources = resources;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }

    @Override
    public void onStart() {
        timer.start();
        currentViewNum = 0;
    }

    public void start(){
        onStart();
    }

    @Override
    public void onStop() {
        currentViewNum = 0;
    }

    public void stop() {
        timer.cancel();
        onStop();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onChangeView() {
        Log.d("Animation", currentViewNum+" max" + resources.length );
        imageView.setBackground(context.getDrawable(resources[currentViewNum]));
        currentViewNum++;
        if(currentViewNum==resources.length)
            stop();
    }

    private CountDownTimer timer = new CountDownTimer(4000, 100) {
        @Override
        public void onTick(long millisUntilFinished) {
            onChangeView();
        }

        @Override
        public void onFinish() {
            Log.d("Animation","finished");
            onStop();
        }
    };
}
