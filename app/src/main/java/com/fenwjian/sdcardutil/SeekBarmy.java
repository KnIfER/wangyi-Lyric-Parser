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
import android.util.AttributeSet;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.AbsSeekBar;
import android.widget.SeekBar;
import android.widget.Toast;


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
public class SeekBarmy extends SeekBar {

    private float mLeft; // space between left of track and left of the view
    private float mRight; // space between right of track and left of the view
    private Paint mPaint;
    public  RBTree<myCpr<Integer,Integer>> tree;
    //public long timeLength;
    private float xLeft;
    private float xRight;
    private float mThumbRadiusOnDragging;
    private int thumbDelta;
    
    public SeekBarmy(Context context) {
		super(context);
		ini();
	}

	public SeekBarmy(Context context, AttributeSet attrs) {
		super(context, attrs);
		ini();
	}


	public SeekBarmy(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
		ini();
	}

	public SeekBarmy(Context context, AttributeSet attrs, int defStyleAttr,
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
	
	@Override
	protected
    void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        xLeft = getPaddingLeft()+thumbDelta/2;
        xRight = getMeasuredWidth() - getPaddingRight() -thumbDelta/2;
		
		
        mPaint.setColor(Color.RED);
        // sectionMark
       // canvas.drawCircle(x_, yTop, 10.0f, mPaint);
        if(tree!=null)
        inOrderDraw(tree.getRoot(),canvas);
	
	}

    private void inOrderDraw(RBTNode<myCpr<Integer,Integer>> tree,Canvas canvas) {
        if(tree != null) {
        	inOrderDraw(tree.left, canvas);
        	canvas.drawCircle((float)xLeft+(float)tree.key.key/(float)getMax()*(-xLeft + xRight), 25, 3.0f, mPaint);
            inOrderDraw(tree.right, canvas);
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
