package com.knizha.wangYiLP.ui;



import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.widget.ScrollView;

import com.knizha.wangYiLP.CMN;

public class ScrollViewmy extends ScrollView {  
    private Context mContext;  
  
    public ScrollViewmy(Context context) {  
        super(context);  
        init(context);  
    }  
  
    public ScrollViewmy(Context context, AttributeSet attrs) {  
        super(context, attrs);  
        init(context);  
  
    }  
  
    public ScrollViewmy(Context context, AttributeSet attrs, int defStyleAttr) {  
        super(context, attrs, defStyleAttr);  
        init(context);  
    }  
  
    private void init(Context context) {  
        mContext = context;  
    }  

    
    @Override  
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {  
        try {  
            //最大高度显示为屏幕内容高度的一半  
            Display display = ((Activity) mContext).getWindowManager().getDefaultDisplay();  
            DisplayMetrics d = new DisplayMetrics();  
            display.getMetrics(d);  
        //此处是关键，设置控件高度不能超过屏幕高度一半（d.heightPixels / 2）（在此替换成自己需要的高度）
            //heightMeasureSpec = MeasureSpec.makeMeasureSpec((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100,d), MeasureSpec.AT_MOST);
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(10, MeasureSpec.AT_MOST);

        } catch (Exception e) {  
            e.printStackTrace();
            CMN.show(e.getLocalizedMessage());
        }  
        //重新计算控件高、宽  
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(10, MeasureSpec.AT_MOST));
    }  
}  