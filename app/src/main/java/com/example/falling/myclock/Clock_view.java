package com.example.falling.myclock;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

import java.lang.ref.WeakReference;


/**
 * Created by falling on 2016/1/25.
 */
public class Clock_view extends View {

    public final static int MESSAGE_CODE = 1000;
    private Paint mPaint;
    private Rect mRect;
    private int mMinute;
    private int mSecond;
    private int mMillisecond;
    private clockHandle mClockHandle;
    private int mWidth;
    private RectF mOval;
    private float mSweepangle;

    public Clock_view(Context context) {
        this(context, null);
    }

    public Clock_view(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Clock_view(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mRect = new Rect();
        mMinute = 0;
        mSecond = 0;
        mMillisecond = 0;
        mClockHandle = new clockHandle(this);
        mWidth = 0;
        mOval = new RectF();
    }


    public int getMinute() {
        return mMinute;
    }

    public int getSecond() {
        return mSecond;
    }

    public int getMillisecond() {
        return mMillisecond;
    }

    public void setMinute(int minute) {
        mMinute = minute;
    }

    public void setSecond(int second) {
        mSecond = second;
    }

    public void setMillisecond(int millisecond) {
        mMillisecond = millisecond;
    }

    public clockHandle getClockHandle() {
        return mClockHandle;
    }

    @Override
    public void onDraw(Canvas canvas) {
        mPaint.setColor(Color.WHITE);
        mPaint.setStrokeWidth(10f);
        mPaint.setAntiAlias(true);  //抗锯齿
        mPaint.setStyle(Paint.Style.STROKE);//设置空心
        //画表盘
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, getWidth() / 3, mPaint);

        //画弧线
        mOval.left = getWidth() / 2 - getWidth() / 3;  //左边
        mOval.top = getHeight() / 2 - getWidth() / 3;  //上边
        mOval.right = getWidth() / 2 + getWidth() / 3; //右边
        mOval.bottom = getHeight() / 2 + getWidth() / 3;//下边
        mPaint.setColor(Color.RED);
        mSweepangle = (float) ((mSecond*1000+mMillisecond*10) * 1.0 / 60000  * 360);
        canvas.drawArc(mOval, 270, mSweepangle, false, mPaint);


        mPaint.setColor(Color.WHITE);
        mPaint.setStrokeWidth(0);
        mPaint.setTextSize(getWidth() / 7);//设置 分 秒 的字体大小
        String minute = "" + mMinute;
        String second = "" + (mSecond < 10 ? "0" + mSecond : mSecond);
        String milliTime = "  " + (mMillisecond < 10 ? "0" + mMillisecond : mMillisecond);

        //画圈里的秒
        mPaint.getTextBounds(second, 0, second.length(), mRect);
        mWidth = mWidth == 0 ? mRect.width() : mWidth;
        canvas.drawText(second, getWidth() / 2 - mWidth / 2, getHeight() / 2 + mRect.height() / 2, mPaint);

        //画圈里面的分
        mPaint.getTextBounds(minute, 0, minute.length(), mRect);
        canvas.drawText(minute, getWidth() / 2 - mRect.width() - mWidth, getHeight() / 2 + mRect.height() / 2, mPaint);

        //画圈里面的毫秒
        mPaint.setTextSize(getWidth() / 10);//设置毫秒的字体大小
        canvas.drawText(milliTime, getWidth() / 2 + mWidth / 2, getHeight() / 2 + mRect.height() / 2, mPaint);


    }

    public static class clockHandle extends Handler {
        public final WeakReference<Clock_view> mClock_viewWeakReference;

        public clockHandle(Clock_view view) {
            mClock_viewWeakReference = new WeakReference<Clock_view>(view);
        }


        /**
         * 接受 分、秒、毫秒（十位和百位）的信息
         *
         * @param msg
         */
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            Clock_view mclock_view = mClock_viewWeakReference.get();
            switch (msg.what) {
                case MESSAGE_CODE:
                    mclock_view.setMinute(msg.getData().getInt("Minute"));
                    mclock_view.setSecond(msg.getData().getInt("Second"));
                    mclock_view.setMillisecond(msg.getData().getInt("Millisecond"));
                    mclock_view.invalidate();
                    break;
            }
        }
    }
}
