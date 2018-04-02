package com.knizha.wangYiLP;

import android.os.Handler;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.fenwjian.sdcardutil.RBTNode;
import com.fenwjian.sdcardutil.RBTree;
import com.fenwjian.sdcardutil.charsetDec;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Locale;


public class ResultUpdateRunnable implements Runnable{
    Lrc_Parser_Result result;
    RelativeLayout displayer;
    String abs_path,title;
    private ResultUpdateRunnable(){}
    public ResultUpdateRunnable(Lrc_Parser_Result _result,RelativeLayout _displayer,String _abs_path){
        displayer=_displayer;
        result=_result;
        abs_path=_abs_path;
    }

    //mycode2
    public ResultUpdateRunnable(RelativeLayout _displayer,String _abs_path,String title){
        displayer=_displayer;
        abs_path=_abs_path;
        this.title=title;
    }
    @Override
    public void run()
    {
        //mycode2
        Main_list_Fragment.ResultListAdapter adapter=(Main_list_Fragment.ResultListAdapter)((ListView)CMN.a.findViewById(R.id.main_list)).getAdapter();
        adapter.addItem(adapter.getCount()+": "+title,title,abs_path);
    }

}
