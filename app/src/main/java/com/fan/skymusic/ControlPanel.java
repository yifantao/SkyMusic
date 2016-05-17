package com.fan.skymusic;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * Created by fan on 16-5-16.
 */
public class ControlPanel extends FrameLayout {

    private BitmapShader mShader;
    private Paint mPaint;
    private Bitmap mBg;
    private float radias=200;
    public ControlPanel(Context context) {
        this(context, null);
    }

    public ControlPanel(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ControlPanel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public void setRadias(float radias) {
        this.radias = radias;
        invalidate();
    }

    public void setBg(Bitmap bm) {
        this.mBg = bm;
        invalidate();
    }

    private void initView() {
        mPaint = new Paint();
        mPaint.setColor(Color.RED);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int w = getMeasuredWidth();
        int h = getMeasuredHeight();

        if (mBg == null) {
            canvas.drawCircle(w / 2, h / 2, radias, mPaint);
        }
        else{
            int bitW = mBg.getWidth();
            int bitH = mBg.getHeight();
            System.out.println("sout->>bitH=" + bitH + " bitW=" + bitW);
            //因为该控件只显示父控件一定比例的高度，所以背景比例是按父控件高度计算的
            float scaleH = h * 1.0f / bitH;//h*(1/splitScale)得到父控件高度
            float scaleW = w * 1.0f / bitW;
            Matrix matrix = new Matrix();
            matrix.setScale(scaleW, scaleH);
            Bitmap bitmap=Bitmap.createBitmap(mBg,0,0,bitW,bitH,matrix,true);
            mShader=new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            mPaint.setShader(mShader);
            canvas.drawCircle(w/2,h/2,radias,mPaint);
        }

    }
}
