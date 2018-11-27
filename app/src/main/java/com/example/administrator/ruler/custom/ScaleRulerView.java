package com.example.administrator.ruler.custom;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Scroller;

/**
 * Created by Administrator on 2018/8/1.
 *
 * scrollTo移动的是内容，如果是ViewGroup移动的就是所有的子View
 * 如果是View移动的就是画布，移动的过程中View在绝对坐标中的位置是不变的
 */

public class ScaleRulerView extends View {

    private Paint mScalePaint;
    private Paint mTextPaint;

    private int mScreenWidth;//屏幕宽度
    private int mScreenScaleCount;//一个屏幕内允许显示刻度的个数
    private int mScaleMargin;//刻度的间隔

    private int mScaleTotalCount;//总刻度
    private int mStartScale;//刚开始的刻度值
    private int mInitScrollX;//初始刻度值
    private int mWidth;//总宽度
    private int mHeight;//总高度

    private int mTextSize = 30;
    private int mScaleHeight = 45;
    private int mScaleColor;//刻度以及文字的颜色

    private int mCurrentScale;//当前的刻度

    private int mScrollX;//上一次X轴滑动的距离

    private int mLastScale;
    private Scroller mScroller;
    private ScrollCallback mScrollCallback;
    private int mOffset;




    public ScaleRulerView(Context context) {
        super(context);
    }

    public ScaleRulerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ScaleRulerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init(Context context){
        mScalePaint = new Paint();
        mScalePaint.setAntiAlias(true);
        mScalePaint.setStrokeWidth(3);
        mScalePaint.setColor(Color.WHITE);

        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setTextAlign(Paint.Align.CENTER);

        mScreenWidth = getPhoneW(context);
        mScreenScaleCount = 40;
        mScaleMargin = mScreenWidth/mScreenScaleCount;

        mScaleTotalCount = 200;
        mStartScale = 0;
        mInitScrollX = mScaleTotalCount/2;
        mWidth = mScaleMargin*mScaleTotalCount;
        mScroller = new Scroller(context);
    }



    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(mWidth,heightSize);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        mScaleHeight = h/5;
        mOffset = mHeight/7;
        mTextPaint.setTextSize(mOffset);
        //将刻度右移
        scrollTo(mInitScrollX*mScaleMargin,0);
        mScrollX = getScrollX();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawScale(canvas);
        changeScale();
    }

    private void drawScale(Canvas canvas){
        for(int i =0;i<=mScaleTotalCount;i++){
            if(i%10==0){
                mScalePaint.setStrokeWidth(6);
                canvas.drawLine(i*mScaleMargin,0,
                        i*mScaleMargin,mScaleHeight*2,mScalePaint);
                canvas.drawText(i+mStartScale+"",i*mScaleMargin,
                        mScaleHeight*2+mOffset,mTextPaint);
            }else {
                mScalePaint.setStrokeWidth(3);
                canvas.drawLine(i*mScaleMargin,0,
                        i*mScaleMargin,mScaleHeight,mScalePaint);
            }
        }
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if(mScroller.computeScrollOffset()){
            scrollTo(mScroller.getCurrX(),0);
            postInvalidate();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if(mScroller!=null&&mScroller.isFinished()){
                    mScroller.abortAnimation();
                }
                mLastScale = x;
                return true;
            case MotionEvent.ACTION_MOVE:

                int dx = mLastScale - x + mScrollX;
                scrollTo(dx,0);
                changeScale();
                return true;
            case MotionEvent.ACTION_UP:
                mScrollX = getScrollX();
                int transX;
                int offSetX = mScaleMargin*(mScreenScaleCount/2);
                if(mScrollX>mWidth-offSetX){//超出最大值
                    transX = getScrollX() - mWidth + offSetX;
                    mScrollX = mWidth - offSetX;
                    Log.i("zs","大于最大值");
                }else if(getScrollX()<-offSetX){//小于最小值
                    transX =  getScrollX() + offSetX;
                    mScrollX =  -offSetX;
                    Log.i("zs","小于最小值");
                }else {//在范围内
                    transX = getScrollX()%mScaleMargin;
                    if(transX>(mScaleMargin/2)){
                        transX = -(mScaleMargin - transX);
                    }
                    mScrollX = getScrollX() - transX;
                    Log.i("zs","在范围内");
                }
                mScroller.startScroll(getScrollX(),0,-transX,0);
                postInvalidate();
                return true;
        }
        return super.onTouchEvent(event);
    }

    private void changeScale(){
        int minScale = mStartScale;
        int maxScale = mStartScale + mScaleTotalCount;
        int scrollScale = (int) Math.rint((double) getScrollX()/
                (double) mScaleMargin);
        //初始刻度值+滚动刻度值
        mCurrentScale =  scrollScale + mScreenScaleCount/2 + mStartScale;
        if(mScrollCallback!=null){
            //超出最大值
            if(mCurrentScale>maxScale){
                mCurrentScale =  maxScale ;
            }
            //低于最小值
            else if(mCurrentScale<minScale){
                mCurrentScale =  minScale ;
            }
            mScrollCallback.setScale(mCurrentScale);
        }
    }

    /**
     * 获取手机分辨率--W
     * */
    public static int getPhoneW(Context context){
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        int disW = dm.widthPixels;
        return disW;
    }

    public void setScrollCallback(ScrollCallback scrollCallback){
        this.mScrollCallback = scrollCallback;
    }

    public interface ScrollCallback{
        void setScale(int scale);
    }


    //设置总刻度是
    public void setScaleTotalCount(int scaleTotalCount) {
        this.mScaleTotalCount = scaleTotalCount;
    }
    //设置第一个刻度值
    public void setStartScale(int startScale) {
        this.mStartScale = startScale;
    }
    //设置初始刻度值
    public void setInitScrollX(int initScrollX) {
        this.mInitScrollX = initScrollX;
        mWidth = mScaleMargin*mScaleTotalCount;
    }

    public void setTextSize(int textSize) {
        this.mTextSize = textSize;
        mTextPaint.setTextSize(mTextSize);
    }

    public void setScaleColor(int scaleColor) {
        this.mScaleColor = scaleColor;
        mScalePaint.setColor(mScaleColor);
        mTextPaint.setColor(mScaleColor);
    }
}
