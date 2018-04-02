package com.knizha.plod.ui;

import android.content.Context;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.knizha.wangYiLP.CMN;

import static android.support.v7.appcompat.R.attr;

/**
 * Created by KnIfER.
 */
public  class BaseToolbar extends Toolbar {

	public BaseToolbar(Context context) {
		this(context, null);
		ini();
	}

	public BaseToolbar(Context context, AttributeSet attrs) {
		this(context, attrs, attr.toolbarStyle);
		ini();
	}

	public BaseToolbar(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		ini();
	}

	void ini(){
		setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent e) {
				detector.onTouchEvent(e);
				if(isOnFlingDected)
				{
					isOnFlingDected=false;
					return true;
				}
				return false;
			}
		});
	}
	boolean isOnFlingDected = false;
	final GestureDetector detector = new GestureDetector(CMN.a, new GestureDetector.SimpleOnGestureListener() {
		public boolean onFling(MotionEvent e1, MotionEvent e2, final float velocityX,
							   final float velocityY) {
			if(Math.abs(velocityX/velocityY)>2.144) {
				//CMN.a.switchToSearchModeDelta((int) Math.signum(velocityX));
				CMN.show(""+Math.signum(velocityX));
				isOnFlingDected=true;
			}
			return false;
		}
	});

	@Override
	public boolean onInterceptTouchEvent(MotionEvent e) {
		//ev.get
		//if(ev.getX()<((View) actionView).getLeft() || ev.getX()>((View) actionView).getRight() )
		detector.onTouchEvent(e);
		if(isOnFlingDected)
		{
			isOnFlingDected=false;
			return false;
		}
		return false;
		//return true;
	}


}