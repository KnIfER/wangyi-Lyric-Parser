package com.fenwjian.sdcardutil;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.Build;
import android.text.Selection;
import android.text.Spannable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.knizha.wangYiLP.CMN;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


/**
 * Created by ASDZXC on 2017/11/4.
 */

public class TextViewmy extends android.support.v7.widget.AppCompatTextView {
	public int selEnd  ;
    public int selStart;
    public int sdkVersionCode = 100;
    public float xmy;
    public float ymy;
    
    Object ObjectSelectionC;
    Object ObjectEditor;
    Object ObjectActionMode;
    Class<?> mEditorClass,ActionModeClass;
    Method MethodShow;
    Method MethodgetSelectionCon;
    Method MethodStartAMMenu,MethodStartAMMenu2;
    Method MethodStopAMMenu;
    Method MethodHideAMMenu;
    Method MethodEnterDrag;
    Paint p2,p3;
    public int scrolly1,scrolly2,highlighty1,highlighty2;
    public TextViewmy(Context c) {
        super(c);
        init();
    }
    public TextViewmy(Context c, AttributeSet attrs) {
        super(c, attrs);
        init();
    }
    public TextViewmy(Context c, AttributeSet attrs, int defStyle) {
        super(c, attrs, defStyle);
        init();
    }
    @Override
    public boolean isFocused() {
        return true;
    }
    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        //?
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
    }
    private void init(){
        Field mEditor;
		try {
			mEditor = TextView.class.getDeclaredField("mEditor");
			mEditor.setAccessible(true);
			ObjectEditor= mEditor.get(this);

			mEditorClass=Class.forName("android.widget.Editor");
			MethodgetSelectionCon=mEditorClass.getDeclaredMethod("getSelectionController");
			MethodgetSelectionCon.setAccessible(true);
			
            MethodStartAMMenu=mEditorClass.getDeclaredMethod("startSelectionActionMode");
            MethodStartAMMenu.setAccessible(true);
            
            MethodStartAMMenu2=mEditorClass.getDeclaredMethod("startSelectionActionModeInternal");
            MethodStartAMMenu2.setAccessible(true); 
            
            MethodStopAMMenu=mEditorClass.getDeclaredMethod("stopTextActionModeWithPreservingSelection");
            MethodStopAMMenu.setAccessible(true);        
            
            MethodHideAMMenu=mEditorClass.getDeclaredMethod("hideFloatingToolbar");
            MethodHideAMMenu.setAccessible(true);  
            
            Field mTextActionMode = mEditorClass.getDeclaredField("mTextActionMode");
            mTextActionMode.setAccessible(true);
			ObjectActionMode= mTextActionMode.get(ObjectEditor);
			
			ActionModeClass=Class.forName("android.view.ActionMode");
			
    	} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(getContext().getApplicationContext(),"ShowTvSelecionHandle:failed  "+e.getMessage()+" "+e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
		}
        p2 = new Paint();
        p3 = new Paint();
        p2.setColor(Color.parseColor("#ffffff"));
        p2.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        p3.setColor(Color.YELLOW);
        p3.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		setLayerType(View.LAYER_TYPE_HARDWARE, null); 
    }
    public interface onSelectionChangedListener{
    	void onSelectionChanged(int selStart, int selEnd);
    }private onSelectionChangedListener SChanged;
    public void setOnSelectionChangedListener(onSelectionChangedListener s){
    	SChanged = s;
    } 
    @Override
    protected void onSelectionChanged(int selStart_, int selEnd_) {
        super.onSelectionChanged(selStart_,selEnd_);
        if(SChanged!=null)//(selEnd_!=selEnd||selStart_!=selStart) && 
        	SChanged.onSelectionChanged(selStart_,selEnd_);
        if(selEnd_!=selStart_) {
            //Toast.makeText(getContext().getApplicationContext(),this.getText().subSequence(this.getSelectionStart(),this.getSelectionEnd())+"",Toast.LENGTH_SHORT).show();
            //Toast.makeText(getContext().getApplicationContext(), selStart + ":" + selEnd, Toast.LENGTH_SHORT).show();
            selEnd = selEnd_;
            selStart = selStart_;
        }
    }

    public void restoreLastSelection(){
        Selection.setSelection((Spannable) this.getText(), selStart, selEnd);
        ShowTvSelecionHandle();
        this.setSelected(true);
        this.onTouchEvent(MotionEvent.obtain(0, 0, MotionEvent.ACTION_UP,0,0, 0));


    }

    public void ShowTvSelecionHandle(){
        try {

            if (sdkVersionCode==100)
                try {
                    sdkVersionCode = Build.VERSION.SDK_INT;
                }catch (Exception e) {
                    e.printStackTrace();
                }
            if(ObjectSelectionC==null){
    			ObjectSelectionC=MethodgetSelectionCon.invoke(ObjectEditor);

    	        MethodShow=ObjectSelectionC.getClass().getMethod("show");
    	        MethodShow.setAccessible(true);
    	        if(sdkVersionCode>=24){
	    	        MethodEnterDrag=ObjectSelectionC.getClass().getMethod("enterDrag",new Class[]{int.class});
	                MethodEnterDrag.setAccessible(true);
    	        }
            }

            if( sdkVersionCode>=24) {
                //my reflection
                MethodEnterDrag.invoke(ObjectSelectionC,new Object[] {2});
                //弹出 ActionMode Menu
                //if(!Main_lyric_Fragment.isDraggingEffecting){
            	boolean asd = (boolean)MethodStartAMMenu.invoke(ObjectEditor);
                //}
                //忽略下一次 ActionUp
                //Field mSelectionActionMode = mClass.getDeclaredField("mDiscardNextActionUp");
                //mSelectionActionMode.setAccessible(true);
                //mSelectionActionMode.set(object, true);
            }

            //Method show=Class.forName("android.widget.Editor.SelectionModifierCursorController").getDeclaredMethod("show");

            MethodShow.invoke(ObjectSelectionC);
            //tv.setHasTransientState(true);


        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext().getApplicationContext(),"ShowTvSelecionHandle:failed  "+e.getMessage()+" "+e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
        }
    }

    public void hideTvSelecionHandle(){
        try {
            Field mEditor = TextView.class.getDeclaredField("mEditor");
            mEditor.setAccessible(true);
            Object object= mEditor.get(this);

            Class<?> mClass=Class.forName("android.widget.Editor");
            Method method=mClass.getDeclaredMethod("getSelectionController");
            method.setAccessible(true);

            Object resultobject=method.invoke(object);
            //Method show=Class.forName("android.widget.Editor.SelectionModifierCursorController").getDeclaredMethod("show");
            Method show=resultobject.getClass().getMethod("hide");
            show.setAccessible(true);
            show.invoke(resultobject);
            setHasTransientState(true);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext().getApplicationContext(),"failed  "+e.getMessage()+" "+e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
        }
    }

    public void hideAM(){
        try {
            if(ObjectSelectionC==null){
    			ObjectSelectionC=MethodgetSelectionCon.invoke(ObjectEditor);
    	        if(sdkVersionCode>=24){
	    	        MethodEnterDrag=ObjectSelectionC.getClass().getMethod("enterDrag",new Class[]{int.class});
	                MethodEnterDrag.setAccessible(true);
    	        }
            }
            //MethodEnterDrag.invoke(ObjectSelectionC,new Object[] {2});
            MethodHideAMMenu.invoke(ObjectEditor);
            

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext().getApplicationContext(),"hideAM :failed  "+e.getMessage()+" "+e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
        }

    }    
    
    public void setCursorPosition(int ss){
        CharSequence text = this.getText();
        //Debug.asserts(text instanceof Spannable);
        if (text instanceof Spannable) {
            Spannable spanText = (Spannable)text;
            Selection.setSelection(spanText,ss);
            //Toast.makeText(getContext().getApplicationContext(),"asd"+tv.isFocused(),Toast.LENGTH_SHORT).show();
        }
        try {
            Method method = TextView.class.getDeclaredMethod("setCursorPosition_internal",new Class[]{int.class,int.class});
            method.setAccessible(true);

            //method.invoke(tv,new Object[]{ss,rr});

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext().getApplicationContext(),"failed  "+e.getMessage()+" "+e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
        }
    }

    
	@Override
	protected
    void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(0,scrolly1+getPaddingTop(), CMN.dm.widthPixels,scrolly2+getPaddingTop(), p2);
        canvas.drawRect(0,highlighty1+getPaddingTop(),CMN.dm.widthPixels,highlighty2+getPaddingTop(), p3);

	}
	
	
}
