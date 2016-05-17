package com.fan.skymusic;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.nineoldandroids.view.ViewHelper;

/**
 * Created by fan on 16-5-16.
 */
public class PlayLayout extends FrameLayout {
    private ViewDragHelper mDragHelp;
    private SplitLayout topSplit;
    private ViewGroup midContent;
    private SplitLayout bottomSplit;
    private ControlPanel controlPanel;

    private int mTop;
    private int mStartTop;
    private int mEndTop;
    private int mRange;


    private float splitScale = 0.5f;
    private float radias=200;

    private int touchSlop;

    public enum Status {
        OPEN, DRAGING, CLOSE
    }

    public void setSplitScale(float value) {
        this.splitScale = value;
        topSplit.setSplitScale(value);
        bottomSplit.setSplitScale(1 - value);
    }

    public void setRadias(float value) {
        this.radias=value;
        topSplit.setRadias(value);
        bottomSplit.setRadias(value);
        controlPanel.setRadias(value);
    }

    public void setSplitBackgroud(int resId) {
        Bitmap bm = BitmapFactory.decodeResource(getResources(), resId);
        topSplit.setBg(bm);
        bottomSplit.setBg(bm);
    }

    public void setSplitBackgroud(Bitmap bm) {
        topSplit.setBg(bm);
        bottomSplit.setBg(bm);
    }


    public void setControlBackground(int resId) {
        Bitmap bm = BitmapFactory.decodeResource(getResources(), resId);
        controlPanel.setBg(bm);
    }

    public void setControlBackground(Bitmap bm) {
        controlPanel.setBg(bm);
    }



    private Status status = Status.CLOSE;

    public PlayLayout(Context context) {
        this(context, null);
    }

    public PlayLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PlayLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        mDragHelp = ViewDragHelper.create(this, mDragCallbak);
        status = Status.CLOSE;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        try {
            midContent = (ViewGroup) getChildAt(0);
            topSplit = (SplitLayout) getChildAt(1);
            bottomSplit = (SplitLayout) getChildAt(2);
            controlPanel = (ControlPanel) getChildAt(3);
            topSplit.setPosition(SplitLayout.Position.TOP);
        } catch (Exception e) {
            String str = "the layout must contains three child view group as follow:midContent->top->bottom";
            try {
                throw new Exception(str);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        int hh = getMeasuredHeight();
        int topH = (int) (hh * 1.0f * splitScale + 0.5f);
        mStartTop = hh;
        mEndTop = topH;
        mTop = hh;
        mRange = mStartTop - mEndTop;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            measureChild(getChildAt(i), widthMeasureSpec, heightMeasureSpec);
        }
        int w = MeasureSpec.getSize(widthMeasureSpec);
        int h = MeasureSpec.getSize(heightMeasureSpec);
        int topH = (int) (h * 1.0f * splitScale + 0.5f);
        int bottomH = h - topH;
        ViewGroup.LayoutParams params1 = topSplit.getLayoutParams();
        ViewGroup.LayoutParams params2 = bottomSplit.getLayoutParams();
       ViewGroup.LayoutParams params3 = controlPanel.getLayoutParams();
        params1.height = topH;
        params1.width = getMeasuredWidth();
        params2.height = bottomH;
        params2.width = getMeasuredWidth();
        params3.height = (int) (radias*2);
        params3.width = (int) (radias*2) ;
        topSplit.setLayoutParams(params1);
        bottomSplit.setLayoutParams(params2);
        controlPanel.setLayoutParams(params3);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int h = getMeasuredHeight();
        int topH = (int) (h * 1.0f * splitScale + 0.5f);
        int bottomH = h - topH;
        midContent.layout(left, top, right, bottom);
        topSplit.layout(left, -topH, right, 0);
        bottomSplit.layout(left, h, right, h + bottomH);
        controlPanel.layout((int)(getMeasuredWidth()*1.0/2-radias),
                (int)(topH-radias),
                (int)(getMeasuredWidth()*1.0/2+radias),
                (int) (topH+ radias));
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (status == Status.CLOSE) return false;
        return mDragHelp.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDragHelp.processTouchEvent(event);
        return true;
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mDragHelp.continueSettling(true))
            ViewCompat.postInvalidateOnAnimation(this);
    }

    private ViewDragHelper.Callback mDragCallbak = new ViewDragHelper.Callback() {

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return child == bottomSplit;
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            if (top > mStartTop)
                top = mStartTop;
            if (top < mEndTop)
                top = mEndTop;
            return top;
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);
            mTop += dy;
            mTop = Math.max(Math.min(mTop, mStartTop), mEndTop);
            dispatchScrollEvent();
        }

        @Override
        public int getViewVerticalDragRange(View child) {
            return mRange;
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            if (mTop < mEndTop + mRange / 2) {
                open();
            } else {
                close();
            }
            ViewCompat.postInvalidateOnAnimation(PlayLayout.this);
        }
    };

    private Status updateStatus() {
        if (mTop <= mEndTop + 5) {
            status = Status.OPEN;
            System.out.println("status->>" + "open");
        } else if (mTop >= mStartTop - 5) {
            status = Status.CLOSE;
            System.out.println("status->>" + "close");

        } else {
            status = Status.DRAGING;
        }
        return status;
    }

    public void open() {
        mDragHelp.smoothSlideViewTo(bottomSplit, 0, mEndTop);
        ViewCompat.postInvalidateOnAnimation(this);
    }

    private void close() {
        mDragHelp.smoothSlideViewTo(bottomSplit, 0, mStartTop);
        ViewCompat.postInvalidateOnAnimation(this);
    }

    private void dispatchScrollEvent() {
        updateStatus();
        float percent = (mStartTop - mTop) * 1.0f / mRange;
        ViewHelper.setTranslationY(topSplit, percent * 1.0f * mEndTop + 0.5f);
        ViewHelper.setScaleY(controlPanel, percent);
        ViewHelper.setScaleX(controlPanel, percent);
        ViewHelper.setRotation(controlPanel, (1 - percent) *360);
    }
}
