package com.knizha.wangYiLP;

import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import eightbitlab.com.blurview.BlurView;

public class Main_list_Fragment extends Fragment{

	AlertDialog d;
	View dv;
	ExecutorService fixedThreadPool;
	String oldSearchingTitle = "";
	EditText ettoptop;

    private int mCurrentState = -1;
    
	RelativeLayout main;
	ListView lv;
	//View option_layout;
	ResultListAdapter adaptermymymymy;
	BlurView.ControllerSettings topViewSettings;
	BlurView topBlurView;

	BlurView bottomBlurView;
	LPWorkerThreadManager manager;
	private String[] charsetNames;

    boolean foundP= false;
	private final int parentDepth = 5;

	public void selectAll() {
		//法一
		//adaptermymymymy.phenoList = (ArrayList<Item>) adaptermymymymy.list.clone();
		//adaptermymymymy.notifyDataSetChanged();
		//法二
		ExecutorService fixedThreadPool = Executors.newFixedThreadPool(1);
		fixedThreadPool.execute(new Runnable() {
			@Override
			public void run() {
				Item it;
				//adaptermymymymy.phenoList = (ArrayList<Item>) adaptermymymymy.list.clone();
				for(int i=0;i<adaptermymymymy.list.size();i++) {
					it=adaptermymymymy.list.get(i);
					if(it.isItemSelected==false) {
						adaptermymymymy.list.get(i).isItemSelected = true;
						adaptermymymymy.phenoList.add(i);
					}
				}
				opt.handler.sendEmptyMessage(1);
			}
		});
	}

	public void deSelectAll() {

		ExecutorService fixedThreadPool = Executors.newFixedThreadPool(1);
		fixedThreadPool.execute(new Runnable() {
			@Override
			public void run() {
				Item it;
				for(int i=0;i<adaptermymymymy.list.size();i++) {
					it=adaptermymymymy.list.get(i);
					if(it.isItemSelected==true){
						it.isItemSelected=false;
						adaptermymymymy.phenoList.remove((Object)i);
					}
				}

				opt.handler.sendEmptyMessage(1);
			}
		});
	}

	public void reversionSelect() {

		//ArrayList<Item> tmp = new ArrayList<Item>();
		//tmp= (ArrayList<Item>) adaptermymymymy.list.clone();
		//tmp.removeAll(adaptermymymymy.phenoList);
		//adaptermymymymy.phenoList = tmp;
		//adaptermymymymy.notifyDataSetChanged();
		ExecutorService fixedThreadPool = Executors.newFixedThreadPool(1);
		fixedThreadPool.execute(new Runnable() {
			@Override
			public void run() {
				Item it;
				for(int i=0;i<adaptermymymymy.list.size();i++) {
					it=adaptermymymymy.list.get(i);
					if(it.isItemSelected==true){
						adaptermymymymy.phenoList.remove((Object)i);
						it.isItemSelected=false;
					}else{
						adaptermymymymy.phenoList.add(i);
						it.isItemSelected=true;
					}
				}
				opt.handler.sendEmptyMessage(1);
			}
		});
	}

	class tk_gross extends TimerTask{
		int msg = 0;
		tk_gross(int message){super();msg = message;}
		@Override
		public void run() {
			mHandler.sendEmptyMessage(msg);
		}
	};
	public LP_Option opt;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		final View main_list_layout= inflater.inflate(R.layout.main_list_layout, container,false);
		opt = CMN.a.opt;
		main=(RelativeLayout)main_list_layout.findViewById(R.id.main_layout);
		ettoptop = (EditText) main_list_layout.findViewById(R.id.ettoptop);
		topBlurView = (BlurView) main_list_layout.findViewById(R.id.topBlurView);
		bottomBlurView = (BlurView) main_list_layout.findViewById(R.id.bottomBlurView);
		adaptermymymymy=new ResultListAdapter();
		CMN.lvAdapter = adaptermymymymy;
		charsetNames = getResources().getStringArray(R.array.charsetNames);
		lv=(ListView)main_list_layout.findViewById(R.id.main_list);
		lv.setAdapter(adaptermymymymy);

		//Button run_button;
		((Button)main_list_layout.findViewById((R.id.run_button))).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View p1)
			{
				Button btn = (Button)p1;
				if("运行".equals(btn.getText())){
					btn.setText("停止");
					manager=new LPWorkerThreadManager((RelativeLayout)main_list_layout.findViewById((R.id.main_layout)));
					manager.start();

					//模糊特效
					//set background, if your root layout doesn't have one
					final Drawable windowBackground = CMN.a.getWindow().getDecorView().getBackground();
					final RelativeLayout root = (RelativeLayout) main_list_layout.findViewById(R.id.main_layout);
//
					topViewSettings = topBlurView.setupWith(root).windowBackground(windowBackground).blurRadius(0.1f);
//
					bottomBlurView.setupWith(root).windowBackground(windowBackground).blurRadius(2f);
					final ValueAnimator animator = ValueAnimator.ofFloat(1f, 16f);
					animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
						@Override
						public void onAnimationUpdate(ValueAnimator animation) {
							topViewSettings.blurRadius((float) animator.getAnimatedValue());
							topBlurView.updateBlur();
//
						} });
					animator.setDuration(500); animator.start();
				}else{
					manager.interrupt();
					btn.setText("运行");
				}
			}
		});
		Button extractButton = (Button)main_list_layout.findViewById(R.id.extractor);
		extractButton.setOnLongClickListener(new View.OnLongClickListener(){
			@Override
			public boolean onLongClick(View v) {
				if(!CMN.a.mDrawerLayout.isDrawerOpen(Gravity.LEFT))
					CMN.a.mDrawerLayout.openDrawer(Gravity.LEFT);
				return true;
			}
		});
		extractButton.setOnClickListener(new OnClickListener(){
			@Override public void onClick(View v)
			{
				if(adaptermymymymy.list.size()<=0){
					CMN.showT("什么都没有，你先点RUN");
					return;
				}

				if(adaptermymymymy.phenoList.size()<=0){
					CMN.showT("什么都没有选欸!~*￣▽￣*)o");
					return;
				}
				View dialog = CMN.inflater.inflate(R.layout.dialog,(ViewGroup) CMN.a.findViewById(R.id.dialog));
				AlertDialog.Builder builder = new AlertDialog.Builder(CMN.a);
				builder.setTitle("确认提取？");
				builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

						switch (CMN.opt.translationFormatNumber){
							case 0:CMN.opt.co_lyrics_sep = "\r\n";
								break;
							case 1:CMN.opt.co_lyrics_sep = " ";
								break;
						}
						//new Thread(){public void run(){
						fixedThreadPool = Executors.newFixedThreadPool(3);
						taskCounter =
								FileConvertorAliveCount = adaptermymymymy.phenoList.size();
						dv = CMN.inflater.inflate(R.layout.dialog_progress,(ViewGroup) CMN.a.findViewById(R.id.dialog));
						dv.findViewById(R.id.cancel).setOnClickListener(new OnClickListener(){
							@Override
							public void onClick(View v) {
								fixedThreadPool.shutdownNow();
							}
						});
						((TextView)dv.findViewById(R.id.tv)).setText("0/"+taskCounter);
						((SeekBar)dv.findViewById(R.id.seekbar)).setMax(taskCounter);
						AlertDialog.Builder builder2 = new AlertDialog.Builder(CMN.a);
						builder2.setView(dv);
						d = builder2.create();
						d.setOnDismissListener(new AlertDialog.OnDismissListener(){
							@Override
							public void onDismiss(DialogInterface dialog) {
								dv = null;
								d = null;
							}
						});
						d.show();
						android.view.WindowManager.LayoutParams lp = d.getWindow().getAttributes();  //获取对话框当前的参数值
						lp.height = 500;
						d.getWindow().setAttributes(lp);
						for(int i=0;i<taskCounter;i++)
						{
							final int index = i;
							fixedThreadPool.execute(new Runnable() {
								public void run() {
									extractLyrics(adaptermymymymy.list.get(adaptermymymymy.phenoList.get(index)));
									deleteOne();
								}
							});
						}
						fixedThreadPool.execute(new Runnable() {
							public void run() {
								while(FileConvertorAliveCount>0){try {
									//System.out.println(FileConvertorAliveCount+"FileConvertorAliveCount!!!!!!!!!!!str = ");
									Thread.sleep(50);
								}catch (InterruptedException e){e.printStackTrace();}
								}
								d.dismiss();
								Looper.prepare();
								CMN.showT("done!");
								//System.out.println("done!!!!!!!!!!!str = ");
								Looper.loop();
							}});
						fixedThreadPool.shutdown();
						//}}.run();

					} });
				builder.setView(dialog);
				builder.setIcon(R.mipmap.ic_directory_parent);
				builder.show();
			}});

		return main_list_layout;
	}



	//admy3
	class ResultListAdapter extends BaseAdapter {
		ArrayList<Item> list;
		//public ArrayList<Item> phenoList;
		public ArrayList<Integer> phenoList;
		Random rand;
		public void clear(){
			list.clear();
			phenoList.clear();
			manager.processedNodeTree.clear();
			notifyDataSetChanged();
		}
		//构造
		public ResultListAdapter()
		{
			list = new ArrayList<Item>();
			phenoList = new ArrayList<Integer>();
			rand = new Random();
		}

		public void addItem(String text,String SongName,String abs_path){
			Item i=new Item();
			i.color=Color.rgb(rand.nextInt(255),rand.nextInt(255),rand.nextInt(255));
			i.color=(int)(HSVtoRGB((list.size()%20+30)*5,0.1f,1.f)+i.color*0.0005f/100);
			i.text=text;
			i.abs_full_path=abs_path;
			i.Songname=SongName;
			if(opt.SelectAll){
				i.isItemSelected=true;
				phenoList.add(list.size());
			}
			list.add(i);
			notifyDataSetChanged();
		}

		@Override
		public long getItemId(int p1)
		{
			return p1;
		}

		@Override
		public int getCount()
		{
			return list.size();
		}

		@Override
		public Object getItem(int p1)
		{
			return list.get(p1);
		}

		@Override
		public View getView(final int pos, View convertView, ViewGroup p3)
		{
			final Item it = (Item)getItem(pos);
			viewHolder vh;
			if(convertView!=null){
				vh=(viewHolder)convertView.getTag();
				vh.cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
					@Override public void onCheckedChanged(CompoundButton box,boolean checked){}});
				if(it.isItemSelected)//phenoList.contains(it))
					vh.cb.setChecked(true);
				else
					vh.cb.setChecked(false);
				//这里真的很蛋疼……
				vh.cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
					@Override
					public void onCheckedChanged(CompoundButton box,boolean checked){
						it.isItemSelected=checked;
						if(checked)
							phenoList.add(pos);
						else
							phenoList.remove((Object)pos);
					}
				});
				vh.tv.setTextColor(it.color);
				vh.tv.setText(it.text);
				convertView.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View p1)
					{
						showLyricsPreview(it);
					}
				});
			}else{
				convertView = CMN.a.inflater.inflate(R.layout.list_item, null);
				vh=new viewHolder();
				vh.tv=  (TextView) convertView.findViewById(R.id.text);
				vh.cb = (CheckBox) convertView.findViewById(R.id.check);
				if(it.isItemSelected)//phenoList.contains(it))
					vh.cb.setChecked(true);
				vh.cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
					@Override
					public void onCheckedChanged(CompoundButton box,boolean checked){
						it.isItemSelected=checked;
						if(checked)
							phenoList.add(pos);
					}
				});
				vh.tv.setTextColor(it.color);
				vh.tv.setText(it.text);
				convertView.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View p1)
					{
						showLyricsPreview(it);
					}
				});
				convertView.setTag(vh);
			}
			return convertView;
		}
	}
	static class viewHolder{
		private CheckBox cb;
		private TextView tv;
	}
	public static LinearLayout lyric_layout;

	private void showLyricsPreview(final Item it) {
		bottomBlurView.setVisibility(View.INVISIBLE);
		if(lyric_layout==null){
			ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(-1,-1);
			lyric_layout=(LinearLayout)CMN.a.inflater.inflate(R.layout.lyric,null);
			lyric_layout.setLayoutParams(lp);}
		try{
			final String str = new String(CMN.readParse(it.abs_full_path));
			main.addView(lyric_layout,3);//添加歌词预览视图
			CMN.a.mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
			//Toast.makeText(getApplicationContext(), "添加歌词预览视图", Toast.LENGTH_SHORT).show();
			TextView tv = (TextView)lyric_layout.findViewById(R.id.lrc_text);
			JSONArray jsonArray = new JSONArray("["+str+"]");
			JSONObject ducoj = jsonArray.getJSONObject(0);
			String lyrics = ducoj.optString("lyric");
			String States = "";
			if(lyrics!=null && lyrics!="" && !CMN.a.opt.PreviewRawJsonLyric){
				if(ducoj.has("translateLyric") && ducoj.optString("translateLyric").length()>0){
					States+="有翻译歌词:true\n";
					lyrics+="\n"+ducoj.optString("translateLyric");
				}
				tv.setText("简单预览\n"+States+"\n"+lyrics.replace("\\n", "\n"));
			}else
				tv.setText("预览原文件\n"+str);
			((TextView)lyric_layout.findViewById(R.id.lrc_text)).setTextColor(Color.rgb(0,0,0));
			//((TextView)lyric_layout.findViewById(R.id.lrc_text)).setMovementMethod(ScrollingMovementMethod.getInstance());
			lyric_layout.setAlpha(0.85f);
			//提取
			((Button)lyric_layout.findViewById(R.id.lyric_back)).setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View h){
					switch (CMN.opt.translationFormatNumber){
						case 0:CMN.opt.co_lyrics_sep = "\r\n";
							break;
						case 1:CMN.opt.co_lyrics_sep = " ";
							break;
					}
					extractLyrics(it);
					CMN.showT("提取完成！");
				}
			});
			//返回
			tv.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View h){
					main.removeView(lyric_layout);
					topBlurView.updateBlur();
					bottomBlurView.setVisibility(View.VISIBLE);
					//lyric_layout.onKeyDown(KeyEvent.KEYCODE_BACK, new KeyEvent(KeyEvent.KEYCODE_BACK,KeyEvent.ACTION_DOWN));
					lyric_layout=null;
					CMN.a.mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
				}
			});
			((TextView)lyric_layout.findViewById(R.id.file_name_text)).setText(it.Songname);//设置标题
		}catch(Exception e){
			e.fillInStackTrace();
			CMN.showT("获取预览视图失败！"+e.getLocalizedMessage());
		}
	}
	private void extractLyrics(Item it){
		try {
			//获取文件名
			String fn = it.Songname;
			File outFile = new File(opt.save_path + fn);
			if(outFile.exists()&&!opt.Overwrite)//存在并且不覆写
				return;
			String str = null;
			str = new String(CMN.readParse(it.abs_full_path));
			//System.out.println("run!!!!!!!!!!!str = " + str);

			JSONArray jsonArray = new JSONArray("["+str+"]");
			JSONObject ducoj = jsonArray.getJSONObject(0);
			String lyrics = ducoj.optString("lyric");
			if(lyrics!=null && lyrics!="") {//确认提取
				String[] lst1 = lyrics.split("\\n");
				HashMap<Integer, String> lst2_texts = new HashMap<Integer, String>();
				ArrayList<myCpr> lst1_texts = new ArrayList<myCpr>();
				String lst1_textsStr = "";
				if (CMN.opt.translationFormatNumber != 2)
					if (ducoj.has("translateLyric")){
						String[] lst2 = ducoj.optString("translateLyric").split("\\n");
						for (String i : lst2) {//处理翻译的歌词 没有的话应该可以自动跳过
							int offa = i.indexOf("[");
							int offb = i.indexOf("]");
							if (offa == -1 || offb == -1) {
								continue;
							}
							String boli = i.substring(offa + 1, offb);
							String[] tmp = boli.split("[: .]");
							if (tmp.length != 3) continue;
							if (tmp[0].length() != 2 || tmp[1].length() != 2)
								continue;
							if (tmp[2].length() == 2)
								tmp[2] = tmp[2] + "0";
							if (tmp[2].length() != 3) continue;
							int time = Integer.valueOf(tmp[0]) * 60000 + Integer.valueOf(tmp[1]) * 1000 + Integer.valueOf(tmp[2]);
							lst2_texts.put(time, i.substring(offb + 1));
						}
					}
				for (String i : lst1) {//处理原版歌词
					boolean isLyric = true;
					int offa = i.indexOf("[");
					int offb = i.indexOf("]");
					if (offa == -1 || offb == -1) {
						continue;
					}

					String boli = i.substring(offa + 1, offb);
					String[] tmp = boli.split("[: .]");
					//格式检查
					if (tmp.length != 3)
						isLyric = false;
					else {
						if (tmp[0].length() != 2 || tmp[1].length() != 2)
							isLyric = false;
						if (tmp[2].length() == 2)
							tmp[2] = tmp[2] + "0";
						if (tmp[2].length() != 3) isLyric = false;
					}
					if (isLyric) {
						int time = Integer.valueOf(tmp[0]) * 60000 + Integer.valueOf(tmp[1]) * 1000 + Integer.valueOf(tmp[2]);//时间码
						String text = (offb + 1) < i.length() ? i.substring(offb + 1) : "";//原版歌词
						if (!"".equals(text)) {//跳过冗余
							if (lst2_texts.containsKey(time))
								//lst1_texts.add(new myCpr(time,i.substring(offb+1)+"\r\n"+lst2_texts.get(time)));
								lst1_textsStr += ms2ffmTime(Long.valueOf(time))  + i.substring(offb + 1) + CMN.opt.co_lyrics_sep + lst2_texts.get(time) + "\r\n";
							else
								//lst1_texts.add(new myCpr(time,i.substring(offb+1)));
								lst1_textsStr += ms2ffmTime(Long.valueOf(time)) + i.substring(offb + 1) + "\r\n";
						}
					} else {
						if(opt.addOtherNoneLyricInfos)
							lst1_textsStr += i + "\n";
					}
				}


				if (ducoj.has("musicId") == false) {
					//TODO
				}

				//System.out.println("strt");
				int _id = Integer.parseInt(ducoj.optString("musicId"));
				Object lp;


				//最终写入
				if (opt.addisSongLyricHeader)
					lst1_textsStr = "[is:songLrc]\r\n" + lst1_textsStr;
				outFile.createNewFile();
				OutputStreamWriter oufi = new OutputStreamWriter(new FileOutputStream(opt.save_path + fn), charsetNames[opt.charSetNumber]);
				oufi.write(lst1_textsStr);
				oufi.close();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public static String ms2ffmTime(Long MS){
		StringBuilder mFormatBuilder;
		Formatter mFormatter;
		mFormatBuilder = new StringBuilder();
		mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
		long totalSeconds = MS / 1000;
		long ms = MS%1000;
		long seconds = totalSeconds % 60;
		long minutes = (totalSeconds / 60) % 60;
		long hours   = totalSeconds / 3600;
		mFormatBuilder.setLength(0);
		if(CMN.opt.timeFormatNumber==0)
			return mFormatter.format("[%02d:%02d.%03d]", minutes, seconds,ms).toString();
		else
			return mFormatter.format("[%02d:%02d.%02d]", minutes, seconds,ms/10).toString();
	}

	/*
* 将色彩由HSV空间转换到RGB空间
*
* h  颜色      用角度表示，范围：0到360度
* s  色度      0.0到1.0   0为白色，越高颜色越“纯”
* v  亮度      0.0到1.0   0为黑色，越高越亮
*/
	public static int HSVtoRGB(float h /* 0~360 degrees */, float s /* 0 ~ 1.0 */, float v /* 0 ~ 1.0 */ )
	{
		float f, p, q, t;
		if( s == 0 ) { // achromatic (grey)
			return makeColor(v,v,v);
		}
		h /= 60;      // sector 0 to 5
		int i = (int) Math.floor( h );
		f = h - i;      // factorial part of h
		p = v * ( 1 - s );
		q = v * ( 1 - s * f );
		t = v * ( 1 - s * ( 1 - f ) );
		switch( i ) {
			case 0:
				return makeColor(v,t,p);
			case 1:
				return makeColor(q,v,p);
			case 2:
				return makeColor(p,v,t);
			case 3:
				return makeColor(p,q,v);
			case 4:
				return makeColor(t,p,v);
			default:    // case 5:
				return makeColor(v,p,q);
		}
	}
	private static int makeColor(float v, float t, float p)
	{
		return Color.rgb((int)(v*255),(int)(255*t),(int)(255*p));
	}
	static int FileConvertorAliveCount = 0;
	static int taskCounter = 0;
	public synchronized void deleteOne(){
		FileConvertorAliveCount --;
		mHandler.sendEmptyMessage(10086);
	}
	Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what){
				case 10086:
					((TextView)dv.findViewById(R.id.tv)).setText((taskCounter-FileConvertorAliveCount)+"/"+taskCounter);
					((SeekBar)dv.findViewById(R.id.seekbar)).setProgress(taskCounter-FileConvertorAliveCount);
					break;
			}
		}
	};
}
///[fragment END]

class myCpr implements Comparable<myCpr>{
	public int key;
	public String value;
	public myCpr(int k,String v){
		key=k;value=v;
	}

	public int compareTo(myCpr other) {
		return this.key-other.key;
	}


	public String toString(){
		return key+"_"+value;
	}

}

class Item{
	int color;
	int MusicID;
	String text,Songname,abs_full_path;
	boolean isItemSelected=false;
}
