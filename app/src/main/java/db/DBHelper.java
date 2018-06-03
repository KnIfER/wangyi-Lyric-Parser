package db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import com.knizha.wangYiLP.CMN;

/**
 * Created by DuanJiaNing on 2017/6/30.
 * 数据库将创建于 data/com.duan.musicoco/databases/ 下
 */

public class DBHelper extends SQLiteOpenHelper {
    Context c;

    public DBHelper(Context context, String name) {
        super(context, name, null, CMN.dbVersionCode);
        c=context;
        oldVersion=CMN.dbVersionCode;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {//第一次
        initTableForDBMusicoco(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int _oldVersion, int newVersion) {
        //在setVersion前已经调用
        oldVersion=_oldVersion;
        Toast.makeText(c,"编辑器：项目系统的数据库架构需要更新，请随便保存一个项目以更新",Toast.LENGTH_LONG).show();
        //Toast.makeText(c,oldVersion+":"+newVersion+":"+db.getVersion(),Toast.LENGTH_SHORT).show();

    }
    //lazy Upgrade
    int oldVersion=1;
    @Override
    public void onOpen(SQLiteDatabase db) {
        db.setVersion(oldVersion);

    }
    private void initTableForDBMusicoco(SQLiteDatabase db) {
        DBWangYiLPController.createProjectsTable(db);
    }

}
