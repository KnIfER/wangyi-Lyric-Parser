package com.knizha.wangYiLP;

import android.os.Handler;
import android.widget.RelativeLayout;

import com.fenwjian.sdcardutil.charsetDec;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Locale;

//subSlaveThread
public class LPWorkerThread extends Thread{
    MainActivity displayer;
    String save_path,load_path;
    Handler handler;
    String HistoryHeader;
    public static volatile int alive_count=5;

    private LPWorkerThread(){}
    public LPWorkerThread(String _save_path,String _load_path,MainActivity a,Handler h,String HistoryHeader){
        displayer=a;
        save_path=_save_path;
        load_path=_load_path;
        this.HistoryHeader = HistoryHeader;
        handler=h;
    }


    @Override
    public void run()
    {
        //alive_count--;
        try{
            //mycode
            String str = new String(CMN.readParse(load_path));
            //System.out.println("run!!!!!!!!!!!str = " + str);

            JSONArray jsonArray = new JSONArray("["+str+"]");
            JSONObject ducoj = jsonArray.getJSONObject(0);
            String lyrics = ducoj.optString("lyric");
            if((lyrics!=null) && lyrics.length()>0)
            {
                int	_id=Integer.parseInt(ducoj.optString("musicId"));
                //LPWorkerThreadManager.processedNodeTree.insert(_id+"");//记录树
                //获取文件名
                String fn,title = "";

                Lrc_Parser lprs=new Lrc_Parser(new Lrc_Parser_Expr());
                if(ducoj.has("musicId")==false){

                }

                Lrc_Parser_Info info = lprs.GetTagFromNet(_id,"");
                title = info.Title;
                fn = info.Artist.replace("/", " ").replace("单曲 - 网易云音乐", "").replace("&#39;", "'")+title.replace("&#39;", "'")+".lrc";

                //最终写入-只保留入口
                new File(HistoryHeader+"/"+_id+"OVIHCS"+fn).createNewFile();

                if(!LPWorkerThreadManager.isHasFilter){
                    ResultUpdateRunnable updater=new ResultUpdateRunnable(displayer,load_path,fn);
                    handler.post(updater);
                }else if(fn.contains(displayer.f1.oldSearchingTitle)){
                    ResultUpdateRunnable updater=new ResultUpdateRunnable(displayer,load_path,fn);
                    handler.post(updater);
                }
                //MainActivity.processedNodeTree.insert(new File(load_path).getName());
            }
        }catch(Exception e){
            e.fillInStackTrace();
            //System.out.println("eeeeeeeeenjjjjjjjdfg!!!!!!!!"+e);
        }finally{
            alive_count++;
            //System.out.println("++++++++++"+alive_count);
            super.run();
        }


        //alive_count++;
    }





}
