package com.knizha.wangYiLP;


public class ResultUpdateRunnable implements Runnable{
    Lrc_Parser_Result result;
    Main_extractor_Fragment.ResultListAdapter displayer;
    String abs_path,title;
    private ResultUpdateRunnable(){}
    //public ResultUpdateRunnable(Lrc_Parser_Result _result,RelativeLayout _displayer,String _abs_path){
    //    displayer=_displayer;
    //    result=_result;
    //    abs_path=_abs_path;
    //}

    //mycode2
    public ResultUpdateRunnable(MainActivity a,String _abs_path,String title){
        displayer=a.f1.adaptermymymymy;
        abs_path=_abs_path;
        this.title=title;
    }
    @Override
    public void run()
    {
        //mycode2
        //Main_extractor_Fragment.ResultListAdapter adapter=(Main_extractor_Fragment.ResultListAdapter)((ListView)displayer.findViewById(R.id.main_list)).getAdapter();
        displayer.addItem(displayer.getCount()+": "+title,title,abs_path);
    }

}
