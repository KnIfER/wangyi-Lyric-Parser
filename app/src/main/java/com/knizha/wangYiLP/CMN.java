package com.knizha.wangYiLP;

import android.app.Activity;
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
import java.util.zip.Inflater;

/**
 * Created by ASDZXC on 2018/1/6.
 */

public class CMN {
    public static LP_Option opt;
    public static MainActivity a;
    public static FragmentTransaction fragmentTransaction;
    public static LayoutInflater inflater;
    public static Main_list_Fragment.ResultListAdapter lvAdapter;
    public static DisplayMetrics dm;

    public static void show(String str) {//用来看app生命周期
        //Toast.makeText(a,str,Toast.LENGTH_SHORT).show();
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
        //mFormatBuilder.setLength(0);
        //0 : [%02d:%02d.%03d]
        //1 : [%02d:%02d.%02d]
        //2 :  [%02d:%02d]
        switch (type){
            case 0:
                return mFormatter.format("%02d:%02d.%03d",minutes,seconds,ms).toString();
            case 1:
                return mFormatter.format("%02d:%02d.%02d",minutes,seconds,ms/10).toString();
            case 2:default:
                return mFormatter.format("%02d:%02d",minutes,seconds,ms).toString();
        }
    }

}
