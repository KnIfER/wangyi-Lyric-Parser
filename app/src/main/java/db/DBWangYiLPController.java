package db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;

import com.fenwjian.sdcardutil.RBTNode;
import com.fenwjian.sdcardutil.RBTree;
import com.fenwjian.sdcardutil.myCpr;
import com.knizha.wangYiLP.CMN;
import com.knizha.wangYiLP.Main_lyric_Fragment;

import android.support.v7.widget.RecyclerView;
import java.util.ArrayList;

/**
 * Created by DuanJiaNing on 2017/7/1.@Musicoco音乐播放器
 * Modified by KnIfER on 2018/3/31
 * 每个线程只能使用一个SQLiteOpenHelper，也就使得每个线程使用一个SQLiteDatabase对象（多线程操作数据库会报错）
 */

public class DBWangYiLPController {

    private final Context context;
    private final SQLiteDatabase database;

    private static final String TAG = "DBWangYiLPController";

    public static final String DATABASE = "WangYiLP.db";

    public static final String TABLE_PORJECTS = "proj";

    public static final String PORJECTS_ID = "_id"; //主键
    public static final String SONG_PATH = "path"; //路径
    public static final String TIME_LENGTH = "timelength"; //路径
    public static final String LRC_PATH = "path2"; //路径
    public static final String TIME_LINE = "timeline"; //
    public static final String LRC_MAIN = "main_lrc"; //
    public static final String LRC_SUB = "sub_lrc"; //
    public static final String PORJECT_CREATE = "create_time"; //创建时间


    static void createProjectsTable(SQLiteDatabase db) {
        String sql = "create table if not exists " + 
            TABLE_PORJECTS  + "(" +
            PORJECTS_ID     + " integer primary key autoincrement," +
            SONG_PATH       + " text," +
            LRC_PATH        + " text," +
            TIME_LINE       + " text," +
            LRC_MAIN        + " text," +
            LRC_SUB         + " text," +
            PORJECT_CREATE  + " text)" ;
        db.execSQL(sql);
    }

    /**
     * 在使用结束时应调用{@link #close()}关闭数据库连接
     */
    //构造
    public DBWangYiLPController(Context context, boolean writable) {
        DBHelper helper = new DBHelper(context, DATABASE);
        if (writable) {
            this.database = helper.getWritableDatabase();
        } else {
            this.database = helper.getReadableDatabase();
        }
        this.context = context;
    }

    public void close() {
        if (database.isOpen()) {
            database.close();
        }
    }
    String debug = "deb' ug";
    public void setUpEditorByProj(Main_lyric_Fragment f2, Long timeCode) {
        String sql = "select * from " + TABLE_PORJECTS + " where " + PORJECT_CREATE + " like ? ";
        Cursor cursor = database.rawQuery(sql, new String[]{debug});

        while (cursor.moveToNext()) {

            int id = cursor.getInt(cursor.getColumnIndex(PORJECTS_ID));
            f2.doc_Mp3 = cursor.getString(cursor.getColumnIndex(SONG_PATH));
            f2.doc_Lrc = cursor.getString(cursor.getColumnIndex(LRC_PATH));

            int lnIndexOld,lnIndex;//for string-split use
            String StringTmp = cursor.getString(cursor.getColumnIndex(TIME_LINE));                       //
            f2.tree.clear();
            f2.tree2.clear();
            //CMN.showTT(StringTmp);
            lnIndexOld=lnIndex=0;
            lnIndex = StringTmp.indexOf(" ");
            while(lnIndex!=-1){
                myCpr tmp = new myCpr<Integer, Integer>(Integer.valueOf(StringTmp.substring(lnIndexOld,lnIndex)),//哈哈哈是不是看的你头昏眼花眼花缭乱??!!!
                        Integer.valueOf(StringTmp.substring(lnIndex+1,
                                lnIndex = StringTmp.indexOf(" ",lnIndex+1)
                        )));//时间-行
                f2.tree.insert(tmp);
                f2.tree2.insert(tmp.Swap());
                f2.updateSelectedPositions();//TODO::优化
                lnIndexOld = lnIndex+1;
                lnIndex = StringTmp.indexOf(" ",lnIndexOld);
            }

            StringTmp = cursor.getString(cursor.getColumnIndex(LRC_MAIN));                                 //
            lnIndexOld=lnIndex=0;
            lnIndex = StringTmp.indexOf("\n");
            f2.mainLyricAdapterData.clear();
            while(lnIndex!=-1){
                f2.mainLyricAdapterData.add(StringTmp.substring(lnIndexOld,lnIndex));
                lnIndexOld = lnIndex+1;
                lnIndex = StringTmp.indexOf("\n",lnIndexOld);
            }

            StringTmp = cursor.getString(cursor.getColumnIndex(LRC_SUB));                                 //

            //long create = cursor.getLong(cursor.getColumnIndex(PORJECT_CREATE));                          //
            String create = cursor.getString(cursor.getColumnIndex(PORJECT_CREATE));                          //
            CMN.showT(create);
        }

        cursor.close();
    }



    public void dumpNewProj(String doc_Mp3,String doc_Lrc, RBTree<myCpr<Integer,Integer>> timeLine, ArrayList<String> main_lyric, @Nullable ArrayList<String> transLyric) {

        String lpt = debug;//String.valueOf(System.currentTimeMillis()) + "";
        ContentValues values = new ContentValues();
        values.put(SONG_PATH     , doc_Mp3);
        values.put(LRC_PATH      , doc_Lrc);
        final StringBuilder sb = new StringBuilder();
        sb.append("");//TODO::needed?
        timeLine.SetInOrderDo(new RBTree.inOrderDo(){
            //mycode
            public void dothis(RBTNode n) {
                sb.append(((myCpr<Integer,Integer>)n.getKey()).key)
                .append(" ")
                .append(((myCpr<Integer,Integer>)n.getKey()).value)
                .append(" ");
            }});
        timeLine.inOrderDo();
        values.put(TIME_LINE     , sb.toString());
        //TODO::is sb released????
        StringBuilder sb2 = new StringBuilder();
        for(int i=0;i<main_lyric.size();i++)
            sb2.append(main_lyric.get(i)).append("\n");
        values.put(LRC_MAIN      , sb2.toString());
        if(transLyric!=null){
        StringBuilder sb3 = new StringBuilder();
        for(int i=0;i<transLyric.size();i++)
            sb3.append(transLyric.get(i)).append("\n");
        values.put(LRC_SUB       , sb3.toString());
        }
        values.put(PORJECT_CREATE, lpt);

        database.insert(TABLE_PORJECTS, null, values);
    }

    //public void updateSongPlayTimes(int songID, int times) {
    //    ContentValues values = new ContentValues();
    //    values.put(SONG_PLAYTIMES, times);
    //    String whereClause = PORJECTS_ID + " = ?";
    //    String[] whereArgs = {songID + ""};
    //    database.update(TABLE_PORJECTS, values, whereClause, whereArgs);
    //}

    public boolean removeProjectFromProjectsTable(Long creationTime) {
        String where = PORJECT_CREATE + " like ? ";
        String[] whereArg = new String[]{creationTime+""};
        database.delete(TABLE_PORJECTS, where, whereArg);
        return true;
    }



}
