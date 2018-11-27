package com.example.administrator.ruler.custom;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.example.administrator.ruler.R;


/**
 * Created by Administrator on 2018/11/22.
 */

public class ScaleViewGroup extends FrameLayout {
    private Paint mPaint;
    private float mCenterX;//中间X轴坐标，用于画指示器
    private int mScaleColor;//刻度以及文字颜色
    private int mIndicatorColor;//指示器颜色
    private int mStartScale;//开始刻度
    private int mTotalScale;//总刻度
    private int mInitScale;//初始化时移动的刻度(用来显示初始值)
    private int mTextSize;//初始化时移动的刻度

    private int mWidth,mHeight;
    private int mOffset;
    private ScaleRulerView mScaleRulerView;
    public ScaleViewGroup(@NonNull Context context) {
        super(context);
    }

    public ScaleViewGroup(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ScaleViewGroup(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array =  context.obtainStyledAttributes(attrs,R.styleable.Ruler);
        mScaleColor = array.getColor(R.styleable.Ruler_scaleColor, Color.WHITE);
        mIndicatorColor = array.getColor(R.styleable.Ruler_scaleColor, Color.WHITE);
        mStartScale =  array.getInteger(R.styleable.Ruler_startScale,-100);
        mTotalScale = array.getInteger(R.styleable.Ruler_scaleTotalCount,200);
        mInitScale =  array.getInteger(R.styleable.Ruler_initScrollX,50);
        mTextSize = (int) array.getDimension(R.styleable.Ruler_rulerTextSize,30);
        init(context);
    }

    private void init(Context context){

        mWidth = getPhoneW(context);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mIndicatorColor);

        LayoutInflater.from(context).inflate(R.layout.scale_group,this,true);
        mScaleRulerView = findViewById(R.id.scale_view);
        mScaleRulerView.setScaleTotalCount(mTotalScale);//设置总刻度
        mScaleRulerView.setStartScale(mStartScale);//设置第一个刻度值

        mScaleRulerView.setInitScrollX(mInitScale);//设置初始化时滚动的刻度
        mScaleRulerView.setTextSize(mTextSize);//设置文字的大小
        mScaleRulerView.setScaleColor(mScaleColor);//设置文字和刻度的颜色
        mScaleRulerView.requestLayout();//重新测量
        mScaleRulerView.postInvalidate();//重新绘制


    }

    public void setScrollCallback(ScaleRulerView.ScrollCallback scrollCallback){
        mScaleRulerView.setScrollCallback(scrollCallback);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        mOffset = (int) (mHeight/8.0);
        mCenterX = mWidth/2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Path path = new Path();
        path.moveTo(mCenterX-mOffset,0);
        path.lineTo(mCenterX+mOffset,0);
        path.lineTo(mCenterX, (float) (mOffset*1.2));
        path.close();
        canvas.drawPath(path,mPaint);
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
}
