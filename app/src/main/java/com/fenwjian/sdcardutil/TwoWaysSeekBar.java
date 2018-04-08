package com.fenwjian.sdcardutil;

import java.math.BigDecimal;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.knizha.wangYiLP.R;

public class TwoWaysSeekBar extends View {

    private static final int CLICK_ON_PRESS = 1;    //点击在滑块上
    private static final int CLICK_INVAILD = 0;

    private static final int[] STATE_NORMAL = {};
    private static final int[] STATE_PRESSED = {android.R.attr.state_pressed,android.R.attr.state_window_focused};
    private static final String tag = "TwoWaysSeekBar";

    private Drawable notScrollBarBg;    //滑动条背景图
    private Drawable hasScrollBarBg;    //滑动条滑动时背景图
    private Drawable mThumb;    //滑块
    private Drawable mCenterCircle;     //中间分隔小圆点

    private int mSeekBarWidth;  //控件宽度
    private int mSeekBarHeight; //滑动条高度
    private int mCenterCircleWidth; //中间分割点宽度
    private int mCenterCircleHeight;    //中间分割点高度
    private int mThumbWidth;    //滑块宽度
    private int mThumbHeight;   //滑块高度

    private double mThumbOffset = 0;    //滑块中心坐标
    private double mDefaultThumbOffSet = 100;   //默认滑块位置百分比
    private int mThumbMarginTop = 70;   //滑动块顶部距离上边框距离，也就是距离字体顶部的距离
    private int mDistance = 0;  //滑动的总距离，固定值
    private int mFlag = CLICK_INVAILD;
    private OnSeekBarChangeListener mSeekBarChangeListener;

    public TwoWaysSeekBar(Context context) {
        this(context, null);
    }

    public TwoWaysSeekBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TwoWaysSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init(){
        Resources resources = getResources();
        notScrollBarBg = resources.getDrawable(R.drawable.seek_background_line);
        hasScrollBarBg = resources.getDrawable(R.drawable.seek_progress_line);
        mThumb = resources.getDrawable(R.drawable.seek_adjust_btn);
        mCenterCircle = resources.getDrawable(R.drawable.seek_middle_btn);

        mSeekBarWidth = notScrollBarBg.getIntrinsicWidth();
        mSeekBarHeight = notScrollBarBg.getIntrinsicHeight();
        mThumbWidth = mThumb.getIntrinsicWidth();
        mThumbHeight = mThumb.getIntrinsicHeight();
        mCenterCircleWidth = mCenterCircle.getIntrinsicWidth();
        mCenterCircleHeight = mCenterCircle.getIntrinsicHeight();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = measureWidth(widthMeasureSpec);
        mSeekBarWidth = width;
        mThumbOffset = width/2;
        mDistance = width - mThumbWidth;
        mThumbOffset = formatDouble(mDefaultThumbOffSet/200*(mDistance) + mThumbWidth/2);
        setMeasuredDimension(width, mThumbHeight + mThumbMarginTop+2);
    }

    private int measureWidth(int measureSpec){
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if(specMode == MeasureSpec.AT_MOST){

        } else if (specMode == MeasureSpec.EXACTLY) {

        }
        return specSize;
    }

    @SuppressWarnings("unused")
    private int measureHeight(int measureSpec){
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        int defaultHeight = 100;
        //wrap_content
        if (specMode == MeasureSpec.AT_MOST) {
        }
        //fill_parent
        else if (specMode == MeasureSpec.EXACTLY) {
            defaultHeight = specSize;
        }

        return defaultHeight;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right,int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Paint text_Paint = new Paint();
        text_Paint.setTextAlign(Paint.Align.CENTER);
        text_Paint.setColor(Color.parseColor("#4e8795"));
        text_Paint.setAntiAlias(true);  //抗锯齿
        text_Paint.setAlpha(255);   //透明度
        text_Paint.setTextSize(40);

        int aaa = mThumbMarginTop + mThumbHeight/2 - mSeekBarHeight/2;
        int bbb = aaa + mSeekBarHeight;

        int ccc = mThumbMarginTop + mThumbHeight/2 - mCenterCircleHeight/2;
        int ddd = ccc + mCenterCircleHeight;

        notScrollBarBg.setBounds(mThumbWidth/2, aaa, mSeekBarWidth-mThumbWidth/2, bbb); //左，上，右，下
        notScrollBarBg.draw(canvas);

        if(mThumbOffset > mSeekBarWidth/2){
            hasScrollBarBg.setBounds(mSeekBarWidth/2, aaa, (int)mThumbOffset, bbb);
        }else if(mThumbOffset < mSeekBarWidth/2){
            hasScrollBarBg.setBounds((int)mThumbOffset, aaa, mSeekBarWidth/2, bbb);
        }else{
            hasScrollBarBg.setBounds((int)mThumbOffset, aaa, mSeekBarWidth/2, bbb);
        }
        hasScrollBarBg.draw(canvas);

        mCenterCircle.setBounds(mSeekBarWidth/2 - mCenterCircleWidth/2, ccc, mSeekBarWidth/2 + mCenterCircleWidth/2, ddd);
        mCenterCircle.draw(canvas);

        mThumb.setBounds((int)mThumbOffset - mThumbWidth/2,mThumbMarginTop,(int)mThumbOffset + mThumbWidth/2,mThumbMarginTop+mThumbHeight);
        mThumb.draw(canvas);

        double progress = formatDouble((mThumbOffset - mThumbWidth/2) * 200 / mDistance);   //progress初始值为100
        if((int)progress == 100){
            progress = 0;
        }else if((int)progress > 100){
            progress -= 100;
        }else if((int)progress < 100){
            progress -=100;
        }
        Log.d(tag, "progress:"+progress);
        canvas.drawText((int)progress+"", (int) mThumbOffset - 2 - 2, 50, text_Paint);

        if(mSeekBarChangeListener != null){
            mSeekBarChangeListener.onProgressChanged(this, progress);
        }

    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if(mSeekBarChangeListener != null){

                }
                mFlag = getAreaFlag(event);
                if(mFlag == CLICK_ON_PRESS){
                    mThumb.setState(STATE_PRESSED);
                    if(mSeekBarChangeListener!=null)
                    mSeekBarChangeListener.onProgressBefore();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if(mFlag == CLICK_ON_PRESS){
                    if(event.getX() < 0 || event.getX() <= mThumbWidth/2){
                        mThumbOffset = mThumbWidth/2;
                    }else if(event.getX() >= mSeekBarWidth - mThumbWidth/2){
                        mThumbOffset = mDistance + mThumbWidth/2;
                    }else{
                        mThumbOffset = formatDouble(event.getX());
                    }
                }
                refresh();
                break;
            case MotionEvent.ACTION_UP:
                mThumb.setState(STATE_NORMAL);
                if(mSeekBarChangeListener != null){
                    mSeekBarChangeListener.onProgressAfter();
                }
                break;
            default:
                break;
        }
        return true;
    }

    public int getAreaFlag(MotionEvent e){
        int top = mThumbMarginTop;
        int bottom = mThumbMarginTop + mThumbHeight;
        if(e.getY() >= top && e.getY() <= bottom && e.getX() >= (mThumbOffset - mThumbWidth/2) && e.getX() <= (mThumbOffset + mThumbWidth/2)){
            return CLICK_ON_PRESS;
        }else{
            return CLICK_INVAILD;
        }

    }

    private void refresh(){
        invalidate();
    }

    //设置进度
    public void setProgress(double progress){
        this.mDefaultThumbOffSet = progress;
        /*if(progress == 0){
            mThumbOffset = formatDouble(100/200*(mDistance))+mThumbWidth/2;
        }else */if(progress >= 0){
            mThumbOffset = formatDouble((100 + progress)/200*(mDistance))+mThumbWidth/2;
        }else if(progress < 0){
            mThumbOffset = formatDouble((100 + progress)/200*(mDistance))+mThumbWidth/2;
        }
        refresh();
    }

    public void setOnSeekBarChangeListener(OnSeekBarChangeListener mListener){
        this.mSeekBarChangeListener = mListener;
    }

    public interface OnSeekBarChangeListener {
        //滑动前
        public void onProgressBefore();

        //滑动中
        public void onProgressChanged(TwoWaysSeekBar seekBar, double progress);

        //滑动后
        public void onProgressAfter();
    }

    public static double formatDouble(double mDouble){
        BigDecimal bd = new BigDecimal(mDouble);
        BigDecimal bd1 = bd.setScale(2,BigDecimal.ROUND_HALF_UP);
        mDouble = bd1.doubleValue();
        return mDouble;
    }
}