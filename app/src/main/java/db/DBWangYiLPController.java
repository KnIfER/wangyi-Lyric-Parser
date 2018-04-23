package db;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.fenwjian.sdcardutil.RBTNode;
import com.fenwjian.sdcardutil.RBTree;
import com.fenwjian.sdcardutil.myCpr;
import com.knizha.wangYiLP.CMN;
import com.knizha.wangYiLP.Main_editor_Fragment;
import com.knizha.wangYiLP.R;

import java.util.ArrayList;
import java.util.concurrent.Executors;

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
    public static final String KEY_LYRIC = "key_lyric"; //创建时间
    public static final String TITLE = "title"; //创建时间


    static void createProjectsTable(SQLiteDatabase db) {
        String sql = "create table if not exists " + 
            TABLE_PORJECTS  + "(" +
            PORJECTS_ID     + " integer primary key autoincrement," +
            KEY_LYRIC       + " text," +
            SONG_PATH       + " text," +
            LRC_PATH        + " text," +
            TIME_LINE       + " text," +
            LRC_MAIN        + " text," +
            LRC_SUB         + " text," +
            TITLE         + " text," +
            PORJECT_CREATE  + " text)"
                ;
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
    public void setUpEditorByProj(Main_editor_Fragment f2, String timeCode) {
        String sql = "select * from " + TABLE_PORJECTS + " where " + PORJECT_CREATE + " like ? ";
        Cursor cursor = database.rawQuery(sql, new String[]{timeCode});
        long create=-1;
        while (cursor.moveToNext()) {

            int id = cursor.getInt(cursor.getColumnIndex(PORJECTS_ID));
            f2.doc_Mp3 = cursor.getString(cursor.getColumnIndex(SONG_PATH));
            f2.doc_Lrc = cursor.getString(cursor.getColumnIndex(LRC_PATH));
            f2.src_title_et.setText(cursor.getString(cursor.getColumnIndex(TITLE)));

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

            f2.updateSelectedPositions();

            StringTmp = cursor.getString(cursor.getColumnIndex(LRC_MAIN));                                 //
            lnIndexOld=0;
            lnIndex = StringTmp.indexOf("\n");
            f2.mainLyricAdapterData.clear();
            f2.subLyricAdapterData.clear();
            //f2.mainLyricAdapterData = new ArrayList<String>();
            while(lnIndex!=-1){
                f2.mainLyricAdapterData.add(StringTmp.substring(lnIndexOld,lnIndex));
                lnIndexOld = lnIndex+1;
                lnIndex = StringTmp.indexOf("\n",lnIndexOld);
            }

            StringTmp = cursor.getString(cursor.getColumnIndex(LRC_SUB));                                 //
            lnIndexOld=0;
            lnIndex = StringTmp.indexOf("\n");
            f2.subLyricAdapterData.clear();
            f2.subLyricAdapterData = new ArrayList<String>();
            //f2.mainLyricAdapterData = new ArrayList<String>();
            while(lnIndex!=-1){
                f2.subLyricAdapterData.add(StringTmp.substring(lnIndexOld,lnIndex));
                lnIndexOld = lnIndex+1;
                lnIndex = StringTmp.indexOf("\n",lnIndexOld);
            }
            //create = cursor.getLong(cursor.getColumnIndex(PORJECT_CREATE));                          //
            //String create = cursor.getString(cursor.getColumnIndex(PORJECT_CREATE));                          //

        }

        //if(f2.mainLyricAdapterData.size()>0)
        //    f2.startCandidate=0;
        //else
        //    f2.startCandidate=-1;
        cursor.close();
    }



    public void dumpNewProj(String title, String doc_Mp3, String doc_Lrc, RBTree<myCpr<Integer,Integer>> timeLine, final ArrayList<String> main_lyric, @Nullable ArrayList<String> transLyric) {
        final String key_lyrics = main_lyric.get(0);
        String sql = "select * from " + TABLE_PORJECTS + " where " + KEY_LYRIC + " like ? ";
        Cursor cursor = database.rawQuery(sql, new String[]{key_lyrics});
        boolean shouldCreateNew = cursor.getCount() == 0 ? true : false;
        //String lpt = debug;//String.valueOf(System.currentTimeMillis()) + "";
        final ContentValues values = new ContentValues();
        values.put(KEY_LYRIC, main_lyric.get(Math.min(main_lyric.size()-1,CMN.opt.key_lyric_idx)));
        values.put(TITLE, title);
        values.put(SONG_PATH, doc_Mp3);
        values.put(LRC_PATH, doc_Lrc);
        final StringBuilder sb = new StringBuilder();
        sb.append("");//TODO::needed?
        timeLine.setInOrderDo(new RBTree.inOrderDo() {
            //mycode
            public void dothis(RBTNode n) {
                sb.append(((myCpr<Integer, Integer>) n.getKey()).key)
                        .append(" ")
                        .append(((myCpr<Integer, Integer>) n.getKey()).value)
                        .append(" ");
            }
        });
        timeLine.inOrderDo();
        values.put(TIME_LINE, sb.toString());
        //TODO::is sb released????
        StringBuilder sb2 = new StringBuilder();
        for (int i = 0; i < main_lyric.size(); i++)
            sb2.append(main_lyric.get(i)).append("\n");
        values.put(LRC_MAIN, sb2.toString());
        if (transLyric != null) {
            StringBuilder sb3 = new StringBuilder();
            for (int i = 0; i < transLyric.size(); i++)
                sb3.append(transLyric.get(i)).append("\n");
            values.put(LRC_SUB, sb3.toString());
        }

        //database.insert(TABLE_PORJECTS, null, values);
        if (shouldCreateNew) {
            newProj(TABLE_PORJECTS,values);
            CMN.sh("已新建");
        } else {
            View dialog = CMN.inflater.inflate(R.layout.dialog,(ViewGroup) CMN.a.findViewById(R.id.dialog));
            AlertDialog.Builder builder = new AlertDialog.Builder(CMN.a);
            builder.setTitle("同名项目已存在，覆盖保存？");
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    long today = System.currentTimeMillis();//摒弃旧的时间码
                    values.put(PORJECT_CREATE,today);
                    database.update(TABLE_PORJECTS, values, KEY_LYRIC + " = ?", new String[]{key_lyrics});
                    CMN.curr_proj_timecode=today;
                    CMN.showT("已覆盖保存");
            } });
            builder.setNegativeButton("No,新建项目", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    newProj(TABLE_PORJECTS,values);
                    CMN.sh("已新建");
                } });
            builder.setView(dialog);
            builder.setIcon(R.mipmap.ic_directory_parent);
            builder.show();
        }
    }
    public void newProj(String tableName,ContentValues values){
        long today = System.currentTimeMillis();
        values.put(PORJECT_CREATE,today);
        database.insert(tableName, null, values);
        CMN.curr_proj_timecode=today;
    }
    public void renameProj(String timeCode,String newName){
        String sql = "select * from " + TABLE_PORJECTS + " where " + PORJECT_CREATE + " like ? ";
        Cursor cursor = database.rawQuery(sql, new String[]{timeCode});
        boolean shouldCreateNew = cursor.getCount() == 0 ? true : false;

        ContentValues values = new ContentValues();
        values.put(KEY_LYRIC, newName);

        if (shouldCreateNew) {
            CMN.showTT("error");
        } else {
            database.update(TABLE_PORJECTS, values, PORJECT_CREATE + " = ?", new String[]{timeCode});
            CMN.showT("已重命名");
        }
    }

    public void updateProj(String timeCode,String title,String doc_Mp3,String doc_Lrc, RBTree<myCpr<Integer,Integer>> timeLine, ArrayList<String> main_lyric, @Nullable ArrayList<String> transLyric)
    {
        String sql = "select * from " + TABLE_PORJECTS + " where " + PORJECT_CREATE + " like ? ";
        Cursor cursor = database.rawQuery(sql, new String[]{timeCode});
        boolean shouldCreateNew = cursor.getCount() == 0 ? true : false;

        ContentValues values = new ContentValues();
        values.put(SONG_PATH, doc_Mp3);
        values.put(LRC_PATH, doc_Lrc);
        values.put(TITLE, title);
        final StringBuilder sb = new StringBuilder();
        sb.append("");//TODO::needed?
        timeLine.setInOrderDo(new RBTree.inOrderDo() {
            //mycode
            public void dothis(RBTNode n) {
                sb.append(((myCpr<Integer, Integer>) n.getKey()).key)
                        .append(" ")
                        .append(((myCpr<Integer, Integer>) n.getKey()).value)
                        .append(" ");
            }
        });
        timeLine.inOrderDo();
        values.put(TIME_LINE, sb.toString());
        //TODO::is sb released????
        StringBuilder sb2 = new StringBuilder();
        for (int i = 0; i < main_lyric.size(); i++)
            sb2.append(main_lyric.get(i)).append("\n");
        values.put(LRC_MAIN, sb2.toString());
        if (transLyric != null) {
            StringBuilder sb3 = new StringBuilder();
            for (int i = 0; i < transLyric.size(); i++)
                sb3.append(transLyric.get(i)).append("\n");
            values.put(LRC_SUB, sb3.toString());
        }
        if (shouldCreateNew) {
            values.put(KEY_LYRIC, main_lyric.get(Math.min(main_lyric.size()-1,CMN.opt.key_lyric_idx)));
            newProj(TABLE_PORJECTS,values);
            CMN.sh("已重建");
            //CMN.showTT("update error");
        } else {
            database.update(TABLE_PORJECTS, values, PORJECT_CREATE + " = ?", new String[]{timeCode});
            CMN.sh("已保存");
        }
    }

    //public void updateSongPlayTimes(int songID, int times) {
    //    ContentValues values = new ContentValues();
    //    values.put(SONG_PLAYTIMES, times);
    //    String whereClause = PORJECTS_ID + " = ?";
    //    String[] whereArgs = {songID + ""};
    //    database.update(TABLE_PORJECTS, values, whereClause, whereArgs);
    //}

    public boolean removeProjectFromProjectsTable(String timeCode) {
        String where = PORJECT_CREATE + " like ? ";
        String[] whereArg = new String[]{timeCode+""};
        database.delete(TABLE_PORJECTS, where, whereArg);
        return true;
    }


    public void getAllEntries(ArrayList<String> res,ArrayList<Long>l2) {
        //long today = System.currentTimeMillis();
        String sql = "SELECT * FROM " + TABLE_PORJECTS;
        Cursor c = database.rawQuery(sql, null); // 执行查询语句
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {    // 采用循环的方式检索数据
            res.add(c.getString(c.getColumnIndex(KEY_LYRIC)));
            l2.add(c.getLong(c.getColumnIndex(PORJECT_CREATE)));
        }
    }
}
