package com.knizha.wangYiLP;

import android.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.widget.Toast;

import com.fenwjian.sdcardutil.charsetDec;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Formatter;
import java.util.Locale;

/**
 * Created by ASDZXC on 2018/1/6.
 */

public class CMN {
    public static String[] charsetNames;
    public static long curr_proj_timecode=-1;
    public static LP_Option opt;
    public static MainActivity a;
    public static FragmentTransaction fragmentTransaction;
    public static LayoutInflater inflater;
    public static Main_extractor_Fragment.ResultListAdapter lvAdapter;
    public static DisplayMetrics dm;

    public static void show(String str) {//用来看app生命周期
        //Toast.makeText(a,str,Toast.LENGTH_SHORT).show();
    }
    public static void sh(String str) {//用来看app生命周期
        if(mt==null)
            mt = new Toast(a);
        mt.cancel();
        mt.makeText(a,str, Toast.LENGTH_SHORT).show();
    }
    public static void showT(String str) {//用来看app生命周期
        Toast.makeText(a,str,Toast.LENGTH_SHORT).show();
    }
    public static Toast mt;
    public static void showTT(String text)
    {//time tagged
        if(mt==null)
            mt = new Toast(a);
        //mt.cancel();
        mt.makeText(a, System.currentTimeMillis()+":"+ text, Toast.LENGTH_SHORT).show();
    }
    public static byte[] readParse(String fn) throws IOException {
        charsetDec cd = new charsetDec();
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int len = 0;
        InputStream inStream = new FileInputStream(new File(fn));
        while ((len = inStream.read(data)) != -1) {
            outStream.write(data, 0, len);
        }
        inStream.close();
        return outStream.toByteArray();
    }

    public static String FormTime(int timeMs,int type){
        StringBuilder mFormatBuilder;
        Formatter mFormatter;
        mFormatBuilder = new StringBuilder();
        mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());


        int totalSeconds = timeMs / 1000;

        int ms = timeMs%1000;
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours   = totalSeconds / 3600;
        float seconds_dot_ms = seconds+ms*1.f/100;
        mFormatBuilder.setLength(0);
        /*mFormatBuilder.setLength(0);
        0 : [%02d:%02d.%03d]
        1 : [%02d:%02d.%02d]
        2 :  [%02d:%02d]
        3 :  00.000s
        */
        switch (type){
            case 0:
                return mFormatter.format("%02d:%02d.%03d",minutes,seconds,ms).toString();
            case 1:
                return mFormatter.format("%02d:%02d.%02d",minutes,seconds,ms/10).toString();
            case 2:default:
                return mFormatter.format("%02d:%02d",minutes,seconds,ms).toString();
            case 3:
                return mFormatter.format("%02d.%03d",(int)totalSeconds,Math.abs(ms)).toString();
        }
    }

    public static int scale(float value) {
        if(a==null)
            return (int) value;
        DisplayMetrics mDisplayMetrics = a.getResources().getDisplayMetrics();
        float scale = a.getResources().getDisplayMetrics().density;

        float scaleWidth = (float) mDisplayMetrics.widthPixels / 720;
        float scaleHeight = (float) mDisplayMetrics.heightPixels / 1280;

        return Math.round(value *
                Math.min(scaleWidth, scaleHeight) * scale
                * 0.5f);
    }
    public static int getStatusBarHeight() {
        int result = 0;
        int resourceId = a.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = a.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
