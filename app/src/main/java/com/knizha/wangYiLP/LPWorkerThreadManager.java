package com.knizha.wangYiLP;

import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.fenwjian.sdcardutil.RBTNode;
import com.fenwjian.sdcardutil.RBTree;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Created by ASDZXC on 2017/12/4.
 */

//MasterThread
public class LPWorkerThreadManager extends Thread{
    //![0]过滤器
    class prefixff implements FileFilter {
        private String prefix;
        prefixff(String prefix){
            this.prefix = prefix;
        }
        @Override
        public boolean accept(File pathname) {
            return pathname.getName().startsWith(prefix+"OVIHCS");
        }

    }

    public static RBTree<String> processedNodeTree = new RBTree<String>();
    MainActivity displayer;
    String save_path,load_path;
    Handler handler;
    private boolean inter=false;
    public LPWorkerThreadManager(MainActivity a){
        LPWorkerThread.alive_count=CMN.opt.multiThreadNumber;
        displayer=a;
        save_path=CMN.opt.save_path;
        load_path=CMN.opt.load_path;
        handler=CMN.opt.handler;
    }

    private void resetText(){
        handler.post(new Runnable(){
            @Override
            public void run(){
                ((Button)displayer.findViewById(R.id.run_button)).setText("运行");
            }
        });
    }

    public void interrupt(){
        inter = true;
        super.interrupt();
    }
    static boolean isHasFilter;
    @Override
    public void run(){
        Filemy path=new Filemy(load_path);
        String textInEditor = displayer.f1.ettoptop.getText().toString();
        String textOld = displayer.f1.oldSearchingTitle;
        if(textInEditor.equals(""))
            isHasFilter = false;
        else
            isHasFilter = true;
        if(!textInEditor.equals(textOld)) {//清空列表，重新搜索。
            //Looper.prepare();
            //CMN.show("re-search!");
            //Looper.loop();
            displayer.f1.oldSearchingTitle = textInEditor;
            CMN.opt.handler.sendEmptyMessage(R.id.refresh_main_list);
            processedNodeTree.clear();
        }
        LPWorkerThread t;
        System.gc();
        //File[] files = path.listFiles();
        //Arrays.sort(files, new Comparator<File>() {
        //    public int compare(File f1, File f2) {
        //        long diff = f1.lastModified() - f2.lastModified();
        //        if (diff > 0)
        //            return 1;
        //        else if (diff == 0)
        //            return 0;
        //        else
        //            return -1;//如果 if 中修改为 返回-1 同时此处修改为返回 1  排序就会是递减
        //    }
        //    public boolean equals(Object obj) {
        //        return true;
        //    }
        //});

        //遍历所有Json-lrc
        for(File f:path.listFiles()){
            if(!inter){
                if(f.isDirectory())
                    continue;
                String id_Name = f.getName();
                RBTNode<String> closet = processedNodeTree.sxing(id_Name);
                //查树
                if(closet!=null && closet.getKey().equals(id_Name))
                    continue;//找到，跳过当前。
                this.processedNodeTree.insert(id_Name+"");//记录
                File[] flist = CMN.opt.hisHandle.listFiles(new prefixff(id_Name));
                if(flist.length >= 1){
                    //if Cache found
                    String name_tmp = flist[0].getName().split("OVIHCS")[1];
                    if(!isHasFilter){
                        ResultUpdateRunnable updater = new ResultUpdateRunnable(displayer, f.getAbsolutePath(), name_tmp);
                        handler.post(updater);
                    }else if(name_tmp.toLowerCase().contains(displayer.f1.oldSearchingTitle.toLowerCase())){
                        ResultUpdateRunnable updater = new ResultUpdateRunnable(displayer, f.getAbsolutePath(), name_tmp);
                        handler.post(updater);
                    }
                    processedNodeTree.insert(id_Name);
                }else if(CMN.opt.ForbidGetTagFormNet){//不要神马名字了
                    String name_tmp = f.getName();
                    if(!isHasFilter){
                        ResultUpdateRunnable updater=new ResultUpdateRunnable(displayer,f.getAbsolutePath(),f.getName());
                        handler.post(updater);
                    }else if(name_tmp.toLowerCase().contains(displayer.f1.oldSearchingTitle.toLowerCase())){
                        ResultUpdateRunnable updater = new ResultUpdateRunnable(displayer, f.getAbsolutePath(), name_tmp);
                        handler.post(updater);
                    }
                    processedNodeTree.insert(id_Name);
                }else{//干
                    LPWorkerThread.alive_count--;
                    t=new LPWorkerThread(save_path,f.getAbsolutePath(),displayer,handler,CMN.opt.hisHandle.getAbsolutePath());

                    t.start();
                    //线程池处理。当池满时，主线程等待。
                    while(LPWorkerThread.alive_count<=0 && !inter){
                        //System.out.println("locking!!!!!!!" + f.getName());
                        try{
                            this.sleep(5);
                        }catch(InterruptedException e){
                            System.out.println(getName()+"从阻塞中退出...");
                            System.out.println("this.isInterrupted()="+this.isInterrupted());
                            resetText();
                        }

                        //continue;
                    }
                }
            }
        }
        //System.out.println("all thread done.!!!!!!!");
        try {
            Thread.sleep(500);
            resetText();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }




    }





}//master
