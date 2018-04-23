package com.knizha.wangYiLP;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Looper;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.angads25.filepicker.controller.DialogSelectionListener;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by ASDZXC on 2018/1/5.
 */

public class Settings_fragment extends Fragment {
    public static View option_Layout;
    Button mtnPicker;
    AlertDialog d;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        CMN.show("onCreateView");
        option_Layout = inflater.inflate(R.layout.option_layout, container, false);
        ScrollView.LayoutParams lp = new ScrollView.LayoutParams(-1, -1);
        //final ScrollView rl=(ScrollView)inflater.inflate(R.layout.option_layout,null);
        option_Layout.setLayoutParams(lp);
        Button save_button = (Button) option_Layout.findViewById(R.id.save_button);
        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View p1) {
                //option_Layout
                CMN.a.onKeyDown(KeyEvent.KEYCODE_BACK, new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_BACK));
                //main.removeView(option_Layout);
                //topBlurView.updateBlur();
                //main.findViewById(R.id.bottomBlurView).setVisibility(View.VISIBLE);
            }
        });

        final Spinner _spn = (Spinner) option_Layout.findViewById(R.id.mtnPickerspn);
        _spn.setAdapter(new adaptermy2(CMN.a));
        //btn for maxium sub-threads number
        mtnPicker = ((Button) option_Layout.findViewById(R.id.mtnPicker));
        mtnPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Spinner) ((RelativeLayout) v.getParent()).findViewById(R.id.mtnPickerspn)).performClick();
            }
        });
        //myClrBtn1
        ((Button) option_Layout.findViewById(R.id.myClrBtn1)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View dialog = CMN.inflater.inflate(R.layout.dialog, (ViewGroup) CMN.a.findViewById(R.id.dialog));
                TextView tv = (TextView) dialog.findViewById(R.id.tv);
                tv.setTextColor(android.support.v7.appcompat.R.color.accent_material_dark);
                tv.setText("将会删除本app私有目录中的历史记录\r\n（不会影响已提取的歌词），确认？");
                AlertDialog.Builder builder = new AlertDialog.Builder(CMN.a);
                //builder.setTitle("将会删除本app私有目录中的历史记录\r\n（不会影响已提取的歌词），确认？"); 
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for (File i : CMN.opt.hisHandle.listFiles()) {
                            if (!i.isDirectory()) i.delete();
                        }
                    }
                });
                builder.setView(dialog);
                builder.setIcon(R.mipmap.ic_directory_parent);
                builder.show();
            }
        });
        //myClrBtn2
        ((Button) option_Layout.findViewById(R.id.myClrBtn2)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View dialog = CMN.inflater.inflate(R.layout.dialog, (ViewGroup) CMN.a.findViewById(R.id.dialog));
                TextView tv = (TextView) dialog.findViewById(R.id.tv);
                tv.setTextColor(android.support.v7.appcompat.R.color.accent_material_dark);
                tv.setText("将会删除/sdcard/netease/cloudmusic/Download/Lyric/\r\n下的所有文件，确认？");
                AlertDialog.Builder builder = new AlertDialog.Builder(CMN.a);
                //builder.setTitle("将会删除/sdcard/netease/cloudmusic/Download/Lyric/\r\n下的所有文件，确认？"); 
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for (File i : new File("/sdcard/netease/cloudmusic/Download/Lyric/").listFiles()) {
                            if (!i.isDirectory()) i.delete();
                        }
                    }
                });
                builder.setView(dialog);
                builder.setIcon(R.mipmap.ic_directory_parent);
                builder.show();
            }
        });
        //myClrBtn3
        ((Button) option_Layout.findViewById(R.id.myClrBtn3)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View dialog = CMN.inflater.inflate(R.layout.dialog, (ViewGroup) CMN.a.findViewById(R.id.dialog));
                TextView tv = (TextView) dialog.findViewById(R.id.tv);
                tv.setTextColor(android.support.v7.appcompat.R.color.accent_material_dark);
                tv.setText("将会删除/sdcard/netease/cloudmusic/Cache/Lyric/\r\n下的所有文件，确认？");
                AlertDialog.Builder builder = new AlertDialog.Builder(CMN.a);
                //builder.setTitle("将会删除/sdcard/netease/cloudmusic/Cache/Lyric/\r\n下的所有文件，确认？"); 
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for (File i : new File("/sdcard/netease/cloudmusic/Cache/Lyric/").listFiles()) {
                            if (!i.isDirectory()) i.delete();
                        }
                    }
                });
                builder.setView(dialog);
                builder.setIcon(R.mipmap.ic_directory_parent);
                builder.show();
            }
        });


        SpannableStringBuilder span = new SpannableStringBuilder(mtnPicker.getText().toString().split("）：")[0] + "）：" + CMN.opt.multiThreadNumber);
        span.setSpan(new ForegroundColorSpan(Color.RED), 11, span.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mtnPicker.setText(span);
        scanSettings();

//目的目录
        ((Button) option_Layout.findViewById(R.id.pickToFolder)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogProperties properties = new DialogProperties();
                properties.selection_mode = DialogConfigs.SINGLE_MODE;
                properties.selection_type = DialogConfigs.DIR_SELECT;
                properties.root = new File(DialogConfigs.DEFAULT_DIR);
                properties.error_dir = new File(DialogConfigs.DEFAULT_DIR);
                properties.offset = new File(CMN.opt.save_path);
                properties.extensions = null;
                FilePickerDialog dialog = new FilePickerDialog(CMN.a, properties);
                dialog.setTitle("请选择保存目录");
                dialog.setDialogSelectionListener(new DialogSelectionListener() {
                    @Override
                    public void
                    onSelectedFilePaths(String[] files) { //files is the array of the paths of files selected by the Application User. 
                        CMN.opt.save_path = files[0] + "/";
                        ((TextView) option_Layout.findViewById(R.id.save_path_text)).setText(CMN.opt.save_path);
                    }
                });
                dialog.show();
            }
        });
//来源目录
        ((Button) option_Layout.findViewById(R.id.pickFromFolder)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogProperties properties = new DialogProperties();
                properties.selection_mode = DialogConfigs.SINGLE_MODE;
                properties.selection_type = DialogConfigs.DIR_SELECT;
                properties.root = new File(DialogConfigs.DEFAULT_DIR);
                properties.error_dir = new File(DialogConfigs.DEFAULT_DIR);
                properties.offset = new File(CMN.opt.load_path);

                properties.extensions = null;
                FilePickerDialog dialog = new FilePickerDialog(CMN.a, properties);
                dialog.setTitle("请选择来源目录");
                dialog.setDialogSelectionListener(new DialogSelectionListener() {
                    @Override
                    public void
                    onSelectedFilePaths(String[] files) { //files is the array of the paths of files selected by the Application User. 
                        CMN.opt.load_path = files[0] + "/";
                        ((TextView) option_Layout.findViewById(R.id.load_path_text)).setText(CMN.opt.load_path);
                    }
                });
                dialog.show();
            }
        });
        //保存配置
        ((Button) option_Layout.findViewById(R.id.mySettingsDumpBtn))
                .setOnClickListener(SettingsDumper_onClick);
        //选择配置
        ((Button) option_Layout.findViewById(R.id.mySettingsPicker))
                .setOnClickListener(SettingsPicker_onClick);

        return option_Layout;
    }

    private void dumpSettings() {
        CMN.opt.save_path = ((TextView) option_Layout.findViewById(R.id.save_path_text)).getText().toString();
        CMN.opt.load_path = ((TextView) option_Layout.findViewById(R.id.load_path_text)).getText().toString();
        File f = new File(CMN.opt.save_path);
        if (!f.exists() || !f.isDirectory()) {
            Toast.makeText(CMN.a, "invaid save file path,reverting to default", Toast.LENGTH_SHORT).show();
            ((TextView) option_Layout.findViewById(R.id.save_path_text)).setText(MainActivity.DefaultSavePath);
            CMN.opt.save_path = MainActivity.DefaultSavePath;
        } else {
            CMN.opt.save_path = f.getAbsolutePath() + "/";
        }
        f = new File(CMN.opt.load_path);
        if (!f.exists() || !f.isDirectory()) {
            ((TextView) option_Layout.findViewById(R.id.load_path_text)).setText(MainActivity.DefaultLoadPath);
            Toast.makeText(CMN.a, "invaid source file path,reverting to default", Toast.LENGTH_SHORT).show();
            CMN.opt.load_path = MainActivity.DefaultLoadPath;
        } else {
            CMN.opt.load_path = f.getAbsolutePath() + "/";
        }
        if(CMN.opt.ForbidGetTagFormNet != ((CheckBox) option_Layout.findViewById(R.id.check_ForbidGetTagFormNet)).isChecked()){
            CMN.lvAdapter.clear();
            CMN.opt.ForbidGetTagFormNet = ((CheckBox) option_Layout.findViewById(R.id.check_ForbidGetTagFormNet)).isChecked();
        }
        CMN.opt.PreviewRawJsonLyric = ((CheckBox) option_Layout.findViewById(R.id.check_PreviewRawJsonLyric)).isChecked();
        CMN.opt.addisSongLyricHeader = ((CheckBox) option_Layout.findViewById(R.id.addisSongLyricHeader)).isChecked();
        CMN.opt.addOtherNoneLyricInfos = ((CheckBox) option_Layout.findViewById(R.id.addOtherNoneLyricInfos)).isChecked();
        CMN.opt.Overwrite = ((CheckBox) option_Layout.findViewById(R.id.check_Overwrite)).isChecked();
        CMN.opt.SelectAll = ((CheckBox) option_Layout.findViewById(R.id.check_SelectAll)).isChecked();
        CMN.opt.charSetNumber = ((Spinner) option_Layout.findViewById(R.id.spn_pickCharset)).getSelectedItemPosition();
        CMN.opt.timeFormatNumber = ((Spinner) option_Layout.findViewById(R.id.spn_pickGeshi1)).getSelectedItemPosition();
        CMN.opt.translationFormatNumber = ((Spinner) option_Layout.findViewById(R.id.spn_pickGeshi2)).getSelectedItemPosition();

    }
    private void scanSettings() {
        ((Spinner) option_Layout.findViewById(R.id.spn_pickCharset)).setSelection(CMN.opt.charSetNumber);
        ((Spinner) option_Layout.findViewById(R.id.spn_pickGeshi2)).setSelection(CMN.opt.translationFormatNumber);
        ((Spinner) option_Layout.findViewById(R.id.spn_pickGeshi1)).setSelection(CMN.opt.timeFormatNumber);
        ((CheckBox) option_Layout.findViewById(R.id.check_SelectAll)).setChecked(CMN.opt.SelectAll);
        ((CheckBox) option_Layout.findViewById(R.id.check_Overwrite)).setChecked(CMN.opt.Overwrite);
        ((CheckBox) option_Layout.findViewById(R.id.check_PreviewRawJsonLyric)).setChecked(CMN.opt.PreviewRawJsonLyric);
        ((CheckBox) option_Layout.findViewById(R.id.check_ForbidGetTagFormNet)).setChecked(CMN.opt.ForbidGetTagFormNet);
        ((CheckBox) option_Layout.findViewById(R.id.addisSongLyricHeader)).setChecked(CMN.opt.addisSongLyricHeader);
        ((CheckBox) option_Layout.findViewById(R.id.addOtherNoneLyricInfos)).setChecked(CMN.opt.addOtherNoneLyricInfos);
        if (CMN.opt.save_path.length() != 0)
            ((TextView) option_Layout.findViewById(R.id.save_path_text)).setText(CMN.opt.save_path);
        if (CMN.opt.load_path.length() != 0)
            ((TextView) option_Layout.findViewById(R.id.load_path_text)).setText((CMN.opt.load_path));
    }

    @Override
    public void onDestroyView() {
        CMN.show("onDestroyView");
        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        CMN.show("onDetach");
        super.onDetach();
    }
    @Override
    public void onSaveInstanceState(Bundle b) {
        CMN.show("onSaveInstanceState");
        super.onSaveInstanceState(b);
    }
    @Override
    public void onActivityCreated(Bundle b) {
        CMN.show("onActivityCreated");
        super.onActivityCreated(b);
    }
    @Override
    public void onAttach(Context c) {
        CMN.show("onAttach");
        super.onAttach(c);
    }
    @Override
    public void onDestroy() {
        CMN.show("onDestroy");
        super.onDestroy();
    }
    @Override
    public void onStop() {
        //CMN.show("onStop");
        dumpSettings();
        super.onStop();
    }
    @Override
    public void onStart() {
        CMN.show("onStart");
        scanSettings();
        super.onStart();
    }
    @Override
    public void onPause() {
        CMN.show("onPause");
        super.onPause();
    }
    @Override
    public void onResume() {
        CMN.show("onResume");
        super.onResume();
    }
    @Override
    public void onHiddenChanged(boolean hidden) {
        CMN.show("onHiddenChanged");
    }

    //admy2
    class adaptermy2 extends BaseAdapter {
        ArrayList<Item> list=new ArrayList<Item>();
        public ArrayList<Item> phenoList;
        Random rand;
        private LayoutInflater mInflater;
        public adaptermy2(Context ctx)
        {
            for(int i=0;i<7;i++)
                list.add(new Item());
            mInflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        @Override
        public long getItemId(int p1)
        {return p1;}
        @Override
        public int getCount()
        {return 7;}
        @Override
        public Object getItem(int p)
        {return list.get(p);}
        @Override
        public View getView(final int pos, View p2, ViewGroup p3)
        {
            View item = mInflater.inflate(R.layout.my_spinner_style, null);
            TextView tv=(TextView) item.findViewById(R.id.text);
            final int count = pos*2+1;
            tv.setTextColor(Color.rgb(0,0,0));
            if(pos!=getCount()-1){
                tv.setText(""+count);
                item.setOnTouchListener(new View.OnTouchListener(){
                    final GestureDetector detector = new GestureDetector(CMN.a, new GestureDetector.SimpleOnGestureListener() {
                        @Override
                        public boolean onSingleTapUp(MotionEvent e) {
                            if(pos!=getCount()-1){
                                LPWorkerThread.alive_count=count;
                                CMN.opt.multiThreadNumber=count;
                                SpannableStringBuilder span = new SpannableStringBuilder(mtnPicker.getText().toString().split("）：")[0]+"）："+count);
                                span.setSpan(new ForegroundColorSpan(Color.RED),11,span.length(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                mtnPicker.setText(span);
                            }
                            return super.onSingleTapUp(e);
                        }
                    });
                    @Override
                    public boolean onTouch(View v, MotionEvent e) {
                        //Toast.makeText(MainActivity.this,""+e.getAction(), Toast.LENGTH_LONG).show();
                        detector.onTouchEvent(e);
                        //使用默认adapter无法修改字体颜色。fuck android.
                        //use setonclickListener,water feedback disapper.fuck android.
                        //return false 不执行。return true;反馈消失。fuck android.
                        return true;
                    }});
            }else{
                tv.setText("其他...");
            }
            return item;
        }
    }
    private final View.OnClickListener SettingsPicker_onClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            View dialog = CMN.inflater.inflate(R.layout.settings_choosing_dialog, (ViewGroup) CMN.a.findViewById(R.id.dialog));
            ListView lv = (ListView) dialog.findViewById(R.id.lv);
            AlertDialog.Builder builder = new AlertDialog.Builder(CMN.a);
            sonclick = null;
            chooseSettingsAdapter_.refresh();
            lv.setAdapter(chooseSettingsAdapter_);
            builder.setView(dialog);
            builder.setIcon(R.mipmap.ic_directory_parent);
            builder.setNeutralButton("删除",null);
            d = builder.create();
            d.show();
            d.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    chooseSettingsAdapter_.showDelete=!chooseSettingsAdapter_.showDelete;
                    chooseSettingsAdapter_.notifyDataSetChanged();
                }
            });
        }
    };
    public void copyFileToPath(File from,File to){
        try {
            to.delete();
            InputStream inStream = new FileInputStream(from); //读入原文件
            FileOutputStream fs = new FileOutputStream(to);
            byte[] buffer = new byte[1444];
            int byteread = 0;
            int bytesum = 0;
            while ((byteread = inStream.read(buffer)) != -1) {
                bytesum += byteread; //字节数 文件大小
                System.out.println(bytesum);
                fs.write(buffer, 0, byteread);
            }
            inStream.close();
            fs.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private final View.OnClickListener SettingsDumper_onClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            View dialog = CMN.inflater.inflate(R.layout.settings_dumping_dialog, (ViewGroup) CMN.a.findViewById(R.id.dialog));
            ListView lv = (ListView) dialog.findViewById(R.id.lv);
            final EditText et = (EditText) dialog.findViewById(R.id.et);
            ImageView iv = (ImageView) dialog.findViewById(R.id.confirm);
            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Settings_fragment.this.dumpSettings();
                    CMN.a.dumpSettings(Activity.MODE_PRIVATE);
                    String fn = et.getText().toString();
                    final File f = new File(CMN.opt.module_sets_Handle.getAbsolutePath()+"/"+fn+".xml");
                    if(f.exists()){
                        View dialog = CMN.inflater.inflate(R.layout.dialog,(ViewGroup) CMN.a.findViewById(R.id.dialog));
                        AlertDialog.Builder builder = new AlertDialog.Builder(CMN.a);
                        builder.setTitle("文件已存在，覆盖保存？");
                        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                copyFileToPath(new File(CMN.opt.dataDir + "/shared_prefs/lock.xml"),f);
                                d.dismiss();
                            } });
                        builder.setView(dialog);
                        builder.setIcon(R.mipmap.ic_directory_parent);
                        builder.show();

                    }else{
                        copyFileToPath(new File(CMN.opt.dataDir + "/shared_prefs/lock.xml"),f);
                        d.dismiss();
                    }
                }
            });
            AlertDialog.Builder builder = new AlertDialog.Builder(CMN.a);
            chooseSettingsAdapter_.refresh();
            lv.setAdapter(chooseSettingsAdapter_);
            sonclick = new SubOnItemClickListener(){
                @Override
                public void onClick(String strasd) {
                    et.setText(strasd);
                }
            };
            builder.setView(dialog);
            builder.setIcon(R.mipmap.ic_directory_parent);
            builder.setNeutralButton("删除",null);
            d = builder.create();
            d.show();
            d.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    chooseSettingsAdapter_.showDelete=!chooseSettingsAdapter_.showDelete;
                    chooseSettingsAdapter_.notifyDataSetChanged();
                }
            });

        }
    };

    interface SubOnItemClickListener{
        void onClick(String strasd);
    }
    SubOnItemClickListener sonclick;
    private final chooseSettingsAdapter chooseSettingsAdapter_ = new chooseSettingsAdapter();
    class chooseSettingsAdapter extends BaseAdapter {
        ArrayList<File> list;
        boolean showDelete=false;
        public chooseSettingsAdapter()
        {refresh();}
        public void refresh(){
            showDelete=false;
            list = new ArrayList(java.util.Arrays.asList(CMN.opt.module_sets_Handle.listFiles()));
        }
        @Override public long getItemId(int pos){return pos;}
        @Override public int getCount(){return list.size();}
        @Override public File getItem(int pos){return list.get(pos);}
        @Override
        public View getView(final int pos, View convertView, ViewGroup parent)
        {
            View v = CMN.inflater.inflate(R.layout.list_item3, null);
            TextView tv=(TextView) v.findViewById(R.id.text);
            ImageView remove=(ImageView) v.findViewById(R.id.remove);
            final File item = list.get(pos);
            final String fn = item.getName().substring(0,item.getName().lastIndexOf("."));
            tv.setText(fn);

            if(showDelete) {//TODO OPT
                remove.setVisibility(View.VISIBLE);
                remove.setOnClickListener(new View.OnClickListener(){
                    @Override public void onClick(View v) {
                        list.get(pos).delete();
                        list.remove(list.get(pos));
                        notifyDataSetChanged();
                    }
                });
            }
            else
                remove.setVisibility(View.GONE);
            v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(sonclick==null) {
                            try {
                                File fold = new File(CMN.opt.dataDir + "/shared_prefs/lock.xml");
                                fold.delete();
                                //fold.createNewFile();
                                //CMN.a.resetSettings();
                                InputStream inStream = new FileInputStream(list.get(pos)); //读入原文件
                                FileOutputStream fs = new FileOutputStream(fold);
                                byte[] buffer = new byte[1444];
                                int byteread = 0;
                                int bytesum = 0;
                                while ((byteread = inStream.read(buffer)) != -1) {
                                    bytesum += byteread; //字节数 文件大小
                                    System.out.println(bytesum);
                                    fs.write(buffer, 0, byteread);
                                }
                                inStream.close();
                                fs.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            CMN.a.scanSettings(Activity.MODE_MULTI_PROCESS);
                            Settings_fragment.this.scanSettings();
                            d.dismiss();
                        }else{
                            sonclick.onClick(fn);
                        }
                    }
                });
            return v;
        }
    }
}

