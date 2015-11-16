package com.lixinyuyin.decibel.activity;


import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.example.decibel.R;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.lixinyuyin.decibel.listener.PaperButtonListener;
import com.lixinyuyin.decibel.view.LineGraph;
import com.lixinyuyin.decibel.view.PaperButton;

public class AudioRecordActivity extends Activity {

    private final static int SAMPLE_RATE = 8000;// 单位Hz;
    private final static int BUFFER_SIZE = AudioRecord.getMinBufferSize(SAMPLE_RATE,
            AudioFormat.CHANNEL_IN_DEFAULT, AudioFormat.ENCODING_PCM_16BIT);
    private final static int mDelay = 100;// 单位：毫秒

    private AudioRecord mRecorder;

    private TextView realtimeTextView;
    private GraphView graph;
    private PaperButton readButtonLeft;
    private LineGraph lineGraph;
    private PaperButtonListener mButtonListener;
    private TextView dateInfoTextView;

    private Handler mHandler = new Handler();
    private Runnable calculateRunnable;
    private LineGraphSeries<DataPoint> mSeries;
    private double xData = 5d;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
    }

    private void initView() {

        realtimeTextView = (TextView) findViewById(R.id.textview_realtime);
        dateInfoTextView = (TextView) findViewById(R.id.textview_dateinfo);
        graph = (GraphView) findViewById(R.id.graph);
        graph.setTitle(getResources().getString(R.string.title_table));
        graph.setTitleTextSize(getResources().getDimension(R.dimen.text_size_medium));

        readButtonLeft = (PaperButton) findViewById(R.id.button_read_left);
        lineGraph = (LineGraph) findViewById(R.id.linegraph);
        lineGraph.setLineNums(5);
        lineGraph.setYscale(6);
    }

    private void initData() {
        mSeries = new LineGraphSeries<DataPoint>();

        graph.addSeries(mSeries);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(100);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(40);
        mRecorder =
                new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE,
                        AudioFormat.CHANNEL_IN_DEFAULT, AudioFormat.ENCODING_PCM_16BIT, BUFFER_SIZE);
        mButtonListener = new PaperButtonListener(300) {

            @Override
            public void doClick(View v) {
                lineGraph.addLine((float) getVolume());
            }
        };
        readButtonLeft.setOnClickListener(mButtonListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        calculateRunnable = new Runnable() {
            @SuppressLint("SimpleDateFormat")
            @Override
            public void run() {
                double volume = getVolume();
                if (volume > 0) {
                    showRealTimeData(volume);
                    Date tempDate = new Date();
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日----hh:mm:ss");
                    dateInfoTextView.setText(formatter.format(tempDate));
                }
                mHandler.postDelayed(this, mDelay);
            }
        };
        mHandler.postDelayed(calculateRunnable, mDelay);
    }

    @Override
    protected void onPause() {
        mHandler.removeCallbacks(calculateRunnable);
        super.onPause();
    }

    private double getVolume() {
        double volume = 0d;
        if (null != mRecorder) {
            mRecorder.startRecording();
            short[] buffer = new short[BUFFER_SIZE];
            int n = mRecorder.read(buffer, 0, BUFFER_SIZE);
            long sum = 0;
            for (int i = 0; i < buffer.length; i++) {
                sum += buffer[i] * buffer[i];
            }
            double mean = sum / (double) n;
            volume = 10 * Math.log10(mean);
        }
        return volume + 10; // 10dB的偏置
        // TODO 偏置实测后微调
    }

    private void showRealTimeData(double volume) {
        realtimeTextView.setText(String.format(getString(R.string.realtime_hint), volume));
        xData += 1;
        mSeries.appendData(new DataPoint(xData, volume), true, 40);
    }
}
