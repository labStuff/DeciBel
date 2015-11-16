package com.lixinyuyin.decibel.listener;

import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;

public abstract class PaperButtonListener implements OnClickListener {

    private long mLastClickTime = 0;
    private long mDelayTime = 0;

    public PaperButtonListener(long delayTime) {
        mDelayTime = delayTime;
    }

    public abstract void doClick(View v);

    @Override
    public void onClick(final View v) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - mLastClickTime < 800) {
            return;
        }
        mLastClickTime = currentTime;
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doClick(v);
            }
        }, mDelayTime);
    }
}
