package com.fan.skymusic;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

/**
 * Created by fan on 16-5-16.
 */
public class SplitLayout extends FrameLayout {
    private PorterDuffXfermode mMode;
    private Paint mPaint;
    private Bitmap mBg;
    private float splitScale = 0.5f;


    private Position mPosition = Position.BOTTOM;
    private float radias = 200;

    public enum Position {
        TOP, BOTTOM;
    }

    public Position getPosition() {
        return mPosition;
    }

    public void setSplitScale(float value) {
        this.splitScale = value;
    }

    public void setPosition(Position mPosition) {
        this.mPosition = mPosition;
    }

    public float getRadias() {
        return radias;
    }

    public void setRadias(float radias) {
        this.radias = radias;
        invalidate();
    }

    public SplitLayout(Context context) {
        this(context, null);
    }

    public SplitLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SplitLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        mPaint = new Paint();
        mPaint.setAlpha(0);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
    }

    public void setBg(Bitmap bm) {
        this.mBg = bm;
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        //  super.onDraw(canvas);
        int w = getMeasuredWidth();
        int h = getMeasuredHeight();
        Bitmap bm = null;
        if (mBg != null) {
            int bitW = mBg.getWidth();
            int bitH = mBg.getHeight();
            System.out.println("sout->>bitH=" + bitH + " bitW=" + bitW);
            //因为该控件只显示父控件一定比例的高度，所以背景比例是按父控件高度计算的
            float scaleH = h * 1.0f / splitScale / bitH;//h*(1/splitScale)得到父控件高度
            float scaleW = w * 1.0f / bitW;
            Matrix matrix = new Matrix();
            matrix.setScale(scaleW, scaleH);
            bm = Bitmap.createBitmap(mBg, 0, 0, bitW, bitH, matrix, true);
        }
        int startDegree = mPosition == Position.TOP ? 180 : 0;
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas1 = new Canvas(bitmap);

        //根据Position决定绘制已经缩放图像的上半还是下半,在下半则起点为父控件高度减去自己的高度
        int t = mPosition == Position.TOP ? 0 : (int) (h * 1.0f / splitScale - h);
        if (mBg == null)
            canvas1.drawColor(Color.argb(200, 128, 128, 128));
        else
            canvas1.drawBitmap(bm,
                    new Rect(0, t, w, t + h),
                    new Rect(0, 0, w, h),
                    null);

        //画半边圆
        float top = h - radias;
        if (mPosition == Position.BOTTOM)
            top = -radias;
        float bottom = top + 2 * radias;
        RectF rectF = new RectF(w / 2 - radias, top, w / 2 + radias, bottom);
        canvas1.drawArc(rectF, startDegree, 180, true, mPaint);
        /*mPaint.setAlpha(255);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));*/
        canvas.drawBitmap(bitmap, 0, 0, null);
    }
}
