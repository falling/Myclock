package com.example.falling.myclock;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

public class clock extends AppCompatActivity implements View.OnClickListener {
    private static int STOP = 0;
    private static int START = 1;
    private static int PAUSE = 2;

    private int mStates;
    private long startTime;
    private long pauseTime;
    private String spans;
    private ArrayList<Long> mTimeSpans;
    private Clock_view mClockView;
    private FloatingActionButton mFab;
    private ImageButton mImageButton;
    private Timer mTimeRunner;
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clock);
        mClockView = (Clock_view) findViewById(R.id.myClock);
        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mImageButton = (ImageButton) findViewById(R.id.imageButton);
        mTextView = (TextView) findViewById(R.id.timespans);
        mImageButton.setOnClickListener(this);
        findViewById(R.id.myClock).setOnClickListener(this);
        mFab.setOnClickListener(this);
        init();
    }

    private void init() {
        mStates = STOP;
        mImageButton.setVisibility(View.INVISIBLE);
        mTimeRunner = new Timer();
        mTimeSpans = new ArrayList<>();
        spans = "";
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.myClock:
            case R.id.fab:
                if (mStates == STOP) {
                    ///STOP->START
                    startTime = System.currentTimeMillis();
                    mTimeSpans.clear();
                    mTimeSpans.add(0L);
                    mFab.setImageDrawable(getDrawable(R.mipmap.ic_fab_pause));
                    mImageButton.setImageDrawable(getDrawable(R.mipmap.ic_lap));
                    mImageButton.setVisibility(View.VISIBLE);
                    mStates = START;
                    mTimeRunner.startRunning();
                    new Thread(mTimeRunner).start();
                } else if (mStates == START) {
                    //START ->PAUSE
                    mFab.setImageDrawable(getDrawable(R.mipmap.ic_fab_play));
                    mImageButton.setImageDrawable(getDrawable(R.mipmap.ic_reset));
                    mImageButton.setVisibility(View.VISIBLE);
                    mStates = PAUSE;
                    pauseTime = System.currentTimeMillis();
                    mTimeRunner.stopRunning();
                } else if (mStates == PAUSE) {
                    //PAUSE->START
                    startTime = startTime + System.currentTimeMillis() - pauseTime;
                    mTimeRunner.startRunning();
                    new Thread(mTimeRunner).start();
                    mFab.setImageDrawable(getDrawable(R.mipmap.ic_fab_pause));
                    mStates = START;
                    mImageButton.setImageDrawable(getDrawable(R.mipmap.ic_lap));
                    mImageButton.setVisibility(View.VISIBLE);
                }
                break;

            case R.id.imageButton:
                if (mStates == PAUSE) {
                    //清零
                    //PAUSE -> STOP
                    sendMessageToClock(0);
                    spans = "";
                    mTextView.setText(spans);
                    mImageButton.setVisibility(View.INVISIBLE);
                    mStates = STOP;
                } else if (mStates == START) {
                    //记录时间间隔
                    mTimeSpans.add(System.currentTimeMillis() - startTime);
                    long timespan = mTimeSpans.get(mTimeSpans.size() - 1) - mTimeSpans.get(mTimeSpans.size() - 2);
                    String span = "#" + (mTimeSpans.size() - 1)
                            + "  " + timespan / 60000
                            + " " + ((timespan % 60000 / 1000) < 10 ? "0" + (timespan % 60000 / 1000) : (timespan % 60000 / 1000))
                            + "." + ((timespan % 1000) / 10 < 10 ? "0" + (timespan % 1000) / 10 : (timespan % 1000) / 10)
                            + "   "
                            + mTimeSpans.get(mTimeSpans.size() - 1) / 60000
                            + " " + ((mTimeSpans.get(mTimeSpans.size() - 1) % 60000 / 1000) < 10 ? "0" + (mTimeSpans.get(mTimeSpans.size() - 1) % 60000 / 1000) : (mTimeSpans.get(mTimeSpans.size() - 1) % 60000 / 1000))
                            + "." + ((mTimeSpans.get(mTimeSpans.size() - 1) % 1000) / 10 < 10 ? "0" + (mTimeSpans.get(mTimeSpans.size() - 1) % 1000) / 10 : (mTimeSpans.get(mTimeSpans.size() - 1) % 1000) / 10) + "\n";
                    spans = span + mTextView.getText().toString();
                    mTextView.setText(spans);
                }
                break;
        }
    }

    private void sendMessageToClock(long time) {
        Bundle bundle = new Bundle();
        bundle.putInt("Minute", (int) (time / 60000));
        bundle.putInt("Second", (int) (time % 60000 / 1000));
        bundle.putInt("Millisecond", (int) ((time % 1000) / 10));
        Message msg = mClockView.getClockHandle().obtainMessage();
        msg.what = Clock_view.MESSAGE_CODE;
        msg.setData(bundle);
        mClockView.getClockHandle().sendMessage(msg);
    }

    private class Timer implements Runnable {
        boolean isContinue = true;

        @Override
        public void run() {
            while (isContinue) {
                long TimeNow = System.currentTimeMillis() - startTime;
                sendMessageToClock(TimeNow);

                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public void stopRunning() {
            isContinue = false;
        }

        public void startRunning() {
            isContinue = true;
        }
    }
}
