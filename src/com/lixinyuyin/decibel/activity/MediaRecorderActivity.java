package com.lixinyuyin.decibel.activity;

import java.io.IOException;

import android.app.Activity;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.widget.TextView;

import com.example.decibel.R;


public class MediaRecorderActivity extends Activity {

    private static String mRecordPath = Environment.getExternalStorageDirectory().getAbsolutePath()
            + "/decibel.3gp";

    private TextView realtimeTextView;
    private MediaRecorder mRecorder;



    final int mDelay = 1000; // 单位毫秒
    final Handler mHandler = new Handler();
    Runnable calculateRunnable = new Runnable() {

        @Override
        public void run() {
            realtimeTextView.setText(String.format("%1$.2fDB", getVolume()));
            mHandler.postDelayed(this, mDelay);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
    }


    private void initView() {
        realtimeTextView = (TextView) findViewById(R.id.textview_realtime);
    }

    private void initData() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mRecorder.setOutputFile(mRecordPath);
        try {
            mRecorder.prepare();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mRecorder.start();
        mHandler.postDelayed(calculateRunnable, mDelay);
    }

    private double getVolume() {
        int temp = mRecorder.getMaxAmplitude();
        double volume = 0;
        if (0 != temp) {
            volume = 20 * Math.log10(temp);
        }
        return volume;
    }

    @Override
    protected void onDestroy() {
        clean();
        super.onDestroy();
    }


    private void clean() {
        mHandler.removeCallbacks(calculateRunnable);
        mRecorder.stop();
        mRecorder.reset();
        mRecorder.release();
    }
}
