package com.fenwjian.sdcardutil;
//https://blog.csdn.net/u013279665/article/details/51932627
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ScrollView;

public class ScrollViewmy extends ScrollView {
    public interface ScrollViewListener {
        void onScrollChanged(View scrollView, int x, int y, int oldx, int oldy);
    }

    private ScrollViewListener scrollViewListener = null;

    public ScrollViewmy(Context context) {
        super(context);
    }

    public ScrollViewmy(Context context, AttributeSet attrs,
                                int defStyle) {
        super(context, attrs, defStyle);
    }

    public ScrollViewmy(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setScrollViewListener(ScrollViewListener scrollViewListener) {
        this.scrollViewListener = scrollViewListener;
    }

    @Override
    protected void onScrollChanged(int x, int y, int oldx, int oldy) {
        if (scrollViewListener != null)
            scrollViewListener.onScrollChanged(this, x, y, oldx, oldy);
        super.onScrollChanged(x, y, oldx, oldy);
    }
}