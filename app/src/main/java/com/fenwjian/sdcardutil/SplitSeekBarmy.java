/*
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fenwjian.sdcardutil;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.SeekBar;

import com.fenwjian.sdcardutil.chineseNumber.cnn;
import com.knizha.wangYiLP.CMN;
import com.knizha.wangYiLP.R;


/**
 * A SeekBar is an extension of ProgressBar that adds a draggable thumb. The user can touch
 * the thumb and drag left or right to set the current progress level or use the arrow keys.
 * Placing focusable widgets to the left or right of a SeekBar is discouraged. 
 * <p>
 * Clients of the SeekBar can attach a {@link OnSeekBarChangeListener} to
 * be notified of the user's actions.
 *
 * @attr ref android.R.styleable#SeekBar_thumb
 */
public class SplitSeekBarmy extends SeekBar {

    private float mLeft; // space between left of track and left of the view
    private float mRight; // space between right of track and left of the view
    private Paint mPaint;
    //public  RBTree<myCpr<Integer,Integer>> tree;
    public SeekBarmy seekGrosser;
    //public RBTree<myCpr<Integer, Integer>> tree2;//行-时间
    public int[] cs;
    //public long timeLength;
    private float xLeft;
    private float xRight;
    private float mThumbRadiusOnDragging;
    private int thumbDelta;

    public SplitSeekBarmy(Context context) {
		super(context);
		ini();
	}

	public SplitSeekBarmy(Context context, AttributeSet attrs) {
		super(context, attrs);
		ini();
	}


	public SplitSeekBarmy(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
		ini();
	}

	public SplitSeekBarmy(Context context, AttributeSet attrs, int defStyleAttr,
                          int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		ini();
	}

	public void ini(){
		//tree=new RBTree<myCpr<Integer,Integer>>();


        mThumbRadiusOnDragging=25;
		
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setTextAlign(Paint.Align.CENTER);	
        mPaint.setStrokeWidth(10);
	
	}
    static final String[] num_upper = { "零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖" ,"拾"};
    static final String[] num_upper_map = { "洞", "幺", "两", "三", "四", "五", "六", "拐", "八", "勾" };

    @Override
	protected
    void onDraw(Canvas canvas) {
        if(getTag(R.id.position)!=null) {
            int tmp = (int) getTag(R.id.position);
            mPaint.setColor(Color.BLACK);
            mPaint.setTextSize(28);
            mPaint.setTextAlign(Paint.Align.LEFT);
            Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
            float top = fontMetrics.top;//为基线到字体上边框的距离,即上图中的top
            float bottom = fontMetrics.bottom;//为基线到字体下边框的距离,即上图中的bottom

            int baseLineY = (int) (getMeasuredHeight() / 2 - top/2 - bottom/2);//基线中间点的y轴计算公式
            if (tmp <= 10) {
                canvas.drawText(num_upper[tmp], 1, baseLineY , mPaint);
            } else if (tmp < 1000) {
                char[] val = String.valueOf(tmp).toCharArray();
                int len = val.length;
                canvas.drawText(num_upper_map[Integer.valueOf(val[0] + "")]+ "..", 1, baseLineY, mPaint);
                for (int i = 1; i < len; i++) {
                    canvas.drawText(num_upper_map[Integer.valueOf(val[i] + "")], getMeasuredWidth()-mPaint.measureText("一")*(len-i), baseLineY, mPaint);
                }
            }
        }

        super.onDraw(canvas);
        
        xLeft = getPaddingLeft();
        xRight = getMeasuredWidth() - getPaddingRight() ;
		
		

        // sectionMark
       // canvas.drawCircle(x_, yTop, 10.0f, mPaint);


        if(seekGrosser.tree!=null)
            drawSubSet(canvas);
        //inOrderDraw(tree.getRoot(),canvas);

	}
    private void  drawSubSet(Canvas canvas){
        if(CMN.a==null)
            return;
        if(getTag(R.id.position)==null)
            return;
        int PosTmp = (int) getTag(R.id.position);
        //从适合的位置开始，遍历二叉树
        RBTNode<myCpr<Integer,Integer>> curr = seekGrosser.tree.sxing(new myCpr(PosTmp*(60*1000),0));
        while(curr!=null){
            int tmp2 = curr.key.key;
            //if(tmp*(60*1000)>tmp2) CMN.showT("error!!!"+tmp*(60*1000)+"should smaller than searched"+tmp2);
            if(tmp2>=PosTmp*(60*1000)+getMax())//超出，退出循环
                break;
            if(CMN.opt.cutterMode!=1) {//不是cutting mode
                if (cs != null)
                    //CMN.showTT(curr.getKey().value+"");//TODO may be -1.why?
                    mPaint.setColor(cs[Math.max(curr.getKey().value % cs.length, 0)]);
                //canvas.drawCircle((float)xLeft+(float)(tmp2-tmp*(60*1000) )/(float)getMax()*(-xLeft + xRight), 25, 3.0f, mPaint);
                float tmp3 = (float) xLeft + (float) (tmp2 - PosTmp * (60 * 1000)) / (float) getMax() * (-xLeft + xRight);
                if (Build.VERSION.SDK_INT >= 21)
                    canvas.drawOval(tmp3 - 3, 0, tmp3 + 3, getHeight(), mPaint);
                else
                    canvas.drawRect(tmp3 - 1.5f, 0, tmp3 + 1.5f, getHeight(), mPaint);
                if (curr == CMN.a.f2.lastAttach) {
                    mPaint.setColor(Color.parseColor("#ffffff"));
                    //canvas.drawOval(tmp3 - 6, getHeight() - 3, tmp3 + 6, getHeight(), mPaint);
                    //canvas.drawOval(tmp3 - 6, 0, tmp3 + 6, 3, mPaint);
                    canvas.drawCircle(tmp3, getHeight() / 2, 3.0f, mPaint);
                }
            }else{
                mPaint.setColor(Color.WHITE);
                //canvas.drawCircle((float)xLeft+(float)(tmp2-tmp*(60*1000) )/(float)getMax()*(-xLeft + xRight), 25, 3.0f, mPaint);
                float tmp3 = (float) xLeft + (float) (tmp2 - PosTmp * (60 * 1000)) / (float) getMax() * (-xLeft + xRight);
                if (Build.VERSION.SDK_INT >= 21)
                    canvas.drawOval(tmp3 - 3, 0, tmp3 + 3, getHeight(), mPaint);
                else
                    canvas.drawRect(tmp3 - 3, 0, tmp3 + 3, getHeight(), mPaint);
                if(CMN.opt.coupledCut){
                    if(curr.key.value==0){
                        canvas.drawRect(tmp3, 0,             tmp3+8, 3, mPaint);
                        canvas.drawRect(tmp3, getHeight()-3, tmp3+8, getHeight(), mPaint);
                    }else{
                        canvas.drawRect(tmp3-8, 0,             tmp3, 3, mPaint);
                        canvas.drawRect(tmp3-8, getHeight()-3, tmp3, getHeight(), mPaint);
                    }
                }
                if (curr == CMN.a.f2.lastAttach) {
                    mPaint.setColor(Color.parseColor("#ff0000"));
                    //canvas.drawOval(tmp3 - 6, getHeight() - 3, tmp3 + 6, getHeight(), mPaint);
                    //canvas.drawOval(tmp3 - 6, 0, tmp3 + 6, 3, mPaint);
                    canvas.drawCircle(tmp3, getHeight() / 2, 3.0f, mPaint);
                }
            }
            curr = seekGrosser.tree.successor(curr);
        }
        mPaint.setColor(Color.RED);
    }

    private void inOrderDraw(RBTNode<myCpr<Integer,Integer>> tree,Canvas canvas) {
        if(tree != null) {
            if(getTag(R.id.position)==null)
                return;
            int tmp = (int) getTag(R.id.position);
            if(tree.getKey().key<tmp*(60*1000) || tree.getKey().key>=tmp*(60*1000)+getMax() )
                return;
        	inOrderDraw(tree.left, canvas);
        	canvas.drawCircle((float)xLeft+(float)(tree.key.key-tmp*(60*1000) )/(float)getMax()*(-xLeft + xRight), 25, 3.0f, mPaint);
        	inOrderDraw(tree.right,canvas);
        }
    }

    @Override
    public void setThumb(Drawable thumb) {
        super.setThumb(thumb);
        if(thumb==null){
            return;
        }
       // mThumb = thumb;
        Rect thumbRect = thumb.getBounds();
        thumbDelta=thumb.getIntrinsicWidth();//thumbRect.left-thumbRect.right;
        //System.out.print("this is my delta:"+thumb.getIntrinsicWidth());
    }
}
