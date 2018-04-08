package com.knizha.wangYiLP;


import android.content.Context;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.support.design.widget.TabLayout;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import com.fenwjian.sdcardutil.RBTNode;
import com.fenwjian.sdcardutil.RBTree;
import com.fenwjian.sdcardutil.ScrollViewmy;
import com.fenwjian.sdcardutil.SeekBarmy;
import com.fenwjian.sdcardutil.SplitSeekBarmy;
import com.fenwjian.sdcardutil.TextViewmy;
import com.fenwjian.sdcardutil.myCpr;
import com.jaygoo.widget.RangeSeekBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import db.DBWangYiLPController;

public class Main_lyric_Fragment extends Fragment
		implements View.OnClickListener,RelativeLayoutmy.OnResizeListener,
					Toolbar.OnMenuItemClickListener,
					View.OnFocusChangeListener, TabLayout.OnTabSelectedListener {
	private final int Default_InvalidationTime=800;
	int startCandidate;
	int intervalHeaderMarker_line;
	String currentProjID;
	int oldAdjustingKey;
	RBTNode<myCpr<Integer, Integer>> lastAtach;
	public String doc_Mp3 = "/sdcard/netease/cloudmusic/Music/t.mp3";
	public String doc_Lrc = "/sdcard/netease/cloudmusic/Music/t.txt";
	boolean isPausedExpected = true;
	public boolean isMediaPrepared = false;
	MediaPlayer mMediaPlayer;

	ScrollView sv;
	TextView totalT;
	TextView currenT;
	SeekBarmy mSeekBar;
	public RBTree<myCpr<Integer, Integer>> tree;//时间-行
	public RBTree<myCpr<Integer, Integer>> tree2;//行-时间
	ScrollLinearLayoutManager scrollLinearLayoutManager;
	ScrollView seekScroll;
	ListView lv_seekbars;
	ImageView adjustBar_toggle;
	RelativeLayout top_adjustLayout;
	RangeSeekBar top_adjustBar;
	RecyclerView main_lv;
	RecyclerView sub_lv;
	public main_list_Adapter mainLyricAdapter;
	public ArrayList<String> mainLyricAdapterData = new ArrayList<String>();
	ArrayList<String> subLyricAdapterData = new ArrayList<String>();
	DBWangYiLPController dbCon;

	int seekSetTimeSplitNumber = 0;
	int seekSetSpareTime = 0;
	int seekSetActiveIdx = 0;
	boolean seekScrollVisible;
	SeekSetAdapter AdaptermySeekSet = new SeekSetAdapter();
	int colorSheet[] = new int[20];
	View Oldtablet;


	private int actionBarHeight=0;
	private int tvPaddingTop=0,tvPaddingBottom=0;
	RelativeLayout bottomtop;
	RelativeLayout bottom;
	TextViewmy tv;
	View menuView;
	RelativeLayout splitterrl;
	private ImageView jumpTo;
	private TextView previewLineIdx;
	private TextView splitterCT;
	ImageView more_speed;
	View breakABLoop;
	ScrollViewmy s;
	boolean isABLooping;
	int dataIdx;
	StringBuffer sb;
	private RBTree<myCpr<Integer,Integer>> subs_timeNodeTree;//时间-文本偏移
	private RBTree<myCpr<Integer,Integer>> subs_timeNodeTree2;//文本偏移-时间

	private RBTNode<myCpr<Integer,Integer>> prevTimeNode,nxtTimeNode,
			prevOffNode,nxtOffNode;

	@Override
	public View onCreateView(LayoutInflater inflater, final ViewGroup container,
							 Bundle savedInstanceState) {
		Random rand = new Random();
		for(int i=0;i<colorSheet.length;i++){
			colorSheet[i] = Color.rgb(rand.nextInt(255),rand.nextInt(255),rand.nextInt(255));
		}
		dbCon = new DBWangYiLPController(CMN.a, true);
		RelativeLayoutmy main_edit_layout = (RelativeLayoutmy) inflater.inflate(R.layout.main_edit_layout, container, false);
		Toolbar toolbar = (Toolbar) main_edit_layout.findViewById(R.id.toolbar);
		toolbar.inflateMenu(R.menu.menu);
		toolbar.setOnMenuItemClickListener(this);
		TabLayout tabLayout = (TabLayout) main_edit_layout.findViewById(R.id.tabLayout);
		tabLayout.setTabTextColors(Color.parseColor("#ffffff"), Color.parseColor("#ffffff"));
		tabLayout.addTab(tabLayout.newTab().setText("预览"));
		tabLayout.addTab(tabLayout.newTab().setText("主歌词"));
		tabLayout.addTab(tabLayout.newTab().setText("翻译"));
		tabLayout.addTab(tabLayout.newTab().setText("源"));
		tabLayout.addOnTabSelectedListener(this);
		tree = new RBTree<myCpr<Integer, Integer>>();
		tree2 = new RBTree<myCpr<Integer, Integer>>();
		tree.clear();
		tree2.clear();
		//
		splitterrl=(RelativeLayout)main_edit_layout.findViewById(R.id.splitterrl);
		splitterCT = (TextView) main_edit_layout.findViewById(R.id.splitterCT);
		previewLineIdx = (TextView) main_edit_layout.findViewById(R.id.previewLineIdx);
		jumpTo = (ImageView)main_edit_layout.findViewById(R.id.jumpTo);
		s = (ScrollViewmy)main_edit_layout.findViewById(R.id.sv);
		breakABLoop = main_edit_layout.findViewById(R.id.breakABLoop);
		tv = (TextViewmy)main_edit_layout.findViewById(R.id.webview);
		bottomtop=(RelativeLayout)main_edit_layout.findViewById(R.id.bottomtop);
		bottom=(RelativeLayout)main_edit_layout.findViewById(R.id.bottom);
		breakABLoop.setOnClickListener(new View.OnClickListener(){
			@Override public void onClick(View v) {
				isABLooping = false;
				breakABLoop.setVisibility(View.INVISIBLE);
			}});
		//tvPaddingTop = (int) (CMN.dm.heightPixels/2-actionBarHeight-getStatusBarHeight()-tv.getLineHeight());
		//tvPaddingBottom = (int) (CMN.dm.heightPixels/2-getStatusBarHeight()-(bottomtop.getLayoutParams().height+bottom.getLayoutParams().height)-2*tv.getLineHeight());
		//tv.setPadding(0,tvPaddingTop , 0, tvPaddingBottom);
		//
		mSeekBar = (SeekBarmy) main_edit_layout.findViewById(R.id.progress);
		sv = (ScrollView) main_edit_layout.findViewById(R.id.sv);
		totalT = (TextView) main_edit_layout.findViewById(R.id.totalT);
		currenT = (TextView) main_edit_layout.findViewById(R.id.currentT);
		Oldtablet =
		main_lv = (RecyclerView) main_edit_layout.findViewById(R.id.main_lyric_list);
		seekScroll = (ScrollView) main_edit_layout.findViewById(R.id.seekScroll);
		top_adjustLayout = (RelativeLayout) main_edit_layout.findViewById(R.id.DingLayout);
		top_adjustBar = (RangeSeekBar) main_edit_layout.findViewById(R.id.adjustBar);
		lv_seekbars = (ListView) main_edit_layout.findViewById(R.id.lv_seekbars);
		adjustBar_toggle = (ImageView) main_edit_layout.findViewById(R.id.adjustBar_toggle);

		adjustBar_toggle.setOnClickListener(this);
		main_edit_layout.findViewById(R.id.play).setOnClickListener(this);
		main_edit_layout.findViewById(R.id.splitTime).setOnClickListener(this);
		main_edit_layout.findViewById(R.id.browser_widget2).setOnClickListener(this);
		main_edit_layout.findViewById(R.id.browser_widget3).setOnClickListener(this);
		main_edit_layout.findViewById(R.id.browser_widget4).setOnClickListener(this);


		mSeekBar.ini();
		mSeekBar.tree = tree;
		top_adjustBar.setOnRangeChangedListener(new RangeSeekBar.OnRangeChangedListener(){
			@Override
			public void onRangeChanged(RangeSeekBar view, float pos, boolean isFromUser) {
				if(isFromUser){
					//CMN.showT(pos+"");
					RBTNode<myCpr<Integer, Integer>> nodeTmp = tree.successor(lastAtach);
					int newKey = (int)(oldAdjustingKey+(pos-0.5f)*60*1000);
					int valTmp = lastAtach.getKey().value;
					if(nodeTmp!=null && newKey>=nodeTmp.getKey().key){
						int valTmp2 = nodeTmp.getKey().key;
						lastAtach.getKey().value = nodeTmp.getKey().value;
						lastAtach.getKey().key = valTmp2;
						nodeTmp.getKey().value = valTmp;
						lastAtach = nodeTmp;
					}else{
						nodeTmp = tree.predecessor(lastAtach);
						if(nodeTmp!=null && newKey<=nodeTmp.getKey().key){
							int valTmp2 = nodeTmp.getKey().key;
							lastAtach.getKey().value = nodeTmp.getKey().value;
							lastAtach.getKey().key = valTmp2;
							nodeTmp.getKey().value = valTmp;
							lastAtach = nodeTmp;
						}
					}
					RBTNode<myCpr<Integer, Integer>> treeNodeTmp = tree2.search(new myCpr(lastAtach.getKey().value, 0));
					if(treeNodeTmp!=null)
						treeNodeTmp.getKey().value = newKey;//修正行数-时间中的时间
					lastAtach.getKey().key=newKey;
					AdaptermySeekSet.notifyDataSetChanged();
				}
			}

			@Override
			public void onStartTrackingTouch(RangeSeekBar view, boolean isLeft) {
				oldAdjustingKey = lastAtach.getKey().key;
			}

			@Override
			public void onStopTrackingTouch(RangeSeekBar view, boolean isLeft) {
				top_adjustBar.setPosition(0.5f);
			}
		});
		lv_seekbars.setAdapter(AdaptermySeekSet);
		scrollLinearLayoutManager = new ScrollLinearLayoutManager(CMN.a);
		scrollLinearLayoutManager.setScrollEnabled(true);
		//main_lv.setLayoutManager(new LinearLayoutManager(CMN.a));
		main_lv.setLayoutManager(scrollLinearLayoutManager);
		mainLyricAdapter = new main_list_Adapter(mainLyricAdapterData);
		mainLyricAdapter.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(View view, int position) {
				updateStartCandidate(position);
			}
		});
		main_lv.setAdapter(mainLyricAdapter);
		main_edit_layout.setOnResizeListener(this);

		prepareMedia(doc_Mp3);
		//自动排列歌词

		prepare_lyric_assign(doc_Lrc);

		actionBarHeight = tabLayout.getLayoutParams().height;

		//tv.setTextSize(TypedValue.COMPLEX_UNIT_PX,CMN.scale(40));
		tv.setTextSize(TypedValue.COMPLEX_UNIT_PX,scale(CMN.a,81));
		tv.setTextColor(Color.parseColor("#ffffaa"));
		tvPaddingTop = (int) (CMN.dm.heightPixels/2-actionBarHeight-CMN.getStatusBarHeight()-tv.getLineHeight()-120);//
		//tvPaddingTop = (int) (actionBarHeight+main_lv.getLayoutParams().height);
		tvPaddingBottom = (int) (CMN.dm.heightPixels/2-CMN.getStatusBarHeight()-(bottomtop.getLayoutParams().height+bottom.getLayoutParams().height)-2*tv.getLineHeight());
		tv.setPadding(0,tvPaddingTop , 0, tvPaddingBottom);
		tv.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent mv) {
				switch (mv.getAction()){
					case MotionEvent.ACTION_UP:
						isDragging=false;
						//tk_gross tktk = new tk_gross(110);
						//timer2 = new Timer();
						//timer2.schedule(tktk, Default_InvalidationTime);
						break;
					case MotionEvent.ACTION_DOWN:
						//splitterrl.setVisibility(View.VISIBLE);
						timer2.cancel();
						isDraggingEffecting=
								isDragging=true;
						tv.xmy = mv.getX();
						tv.ymy = mv.getY();
						break;

				}
				return false;
			}
		});
		s.setScrollViewListener(new ScrollViewmy.ScrollViewListener(){
			@Override
			public void onScrollChanged(View v, int scrollX, final int scrollY,
									   int oldScrollX, int oldScrollY) {
				if(isDragging)
					splitterrl.setVisibility(View.VISIBLE);
				//if(false)
				tv.post(new Runnable(){
					@Override
					public void run() {
						//CMN.showT(""+heightDelta/CMN.dm.density);
						int line =(int) ((scrollY+heightDelta/CMN.dm.density)/tv.getLineHeight()+1.5);
						if(line>=0 && line<tv.getLineCount()){
							previewLineIdx.setText(line+"");
							int offStrt = tv.getLayout().getOffsetForHorizontal(line, 0)+1;
							//nxtOffNode = subs_timeNodeTree2.sxing(new myCpr(offStrt, 0));
							//prevOffNode = subs_timeNodeTree2.predecessor(nxtOffNode);
							prevOffNode = subs_timeNodeTree2.xxing(new myCpr(offStrt, 0));
							//if(prevOffNode==null) return;
							nxtOffNode =  subs_timeNodeTree2.sxing(new myCpr(offStrt, 0));
							if(nxtOffNode==null) return;
							int lineEnd = tv.getLayout().getLineForOffset(nxtOffNode.getKey().key);
							int lineStrt = tv.getLayout().getLineForOffset(prevOffNode.getKey().key);
							tv.scrolly1=tv.getLayout().getLineTop(lineStrt);
							tv.scrolly2=tv.getLayout().getLineTop(lineEnd);

						}
						tv.invalidate();
					}
				});
			}});
		s.setOnTouchListener(new View.OnTouchListener() {
			private int lastY = 0;
			private int touchEventId = -9983761;
			Handler handler = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					super.handleMessage(msg);
					View scroller = (View) msg.obj;

					if (msg.what == touchEventId) {
						if (lastY == scroller.getScrollY()) {
							//停止了，此处你的操作业务
							tk_gross tktk = new tk_gross(110);
							timer2 = new Timer();
							timer2.schedule(tktk, Default_InvalidationTime);
						} else {
							handler.sendMessageDelayed(handler.obtainMessage(touchEventId, scroller), 1);
							lastY = scroller.getScrollY();
						}
					}
				}
			};
			@Override
			public boolean onTouch(View v, MotionEvent mv) {
				switch (mv.getAction()){
					case MotionEvent.ACTION_UP:
						isDragging=false;
						handler.sendMessageDelayed(handler.obtainMessage(touchEventId, v), 5);
						break;
					case MotionEvent.ACTION_DOWN://滑动
						timer2.cancel();
						isDraggingEffecting=
								isDragging=true;
						break;
				}
				return false;
			}
		});
		jumpTo.setOnClickListener(new View.OnClickListener(){
			@Override public void onClick(View v) {
				mMediaPlayer.seekTo(prevOffNode.getKey().value);
				mSeekBar.setProgress(prevOffNode.getKey().value);
				AdaptermySeekSet.setProgress(prevOffNode.getKey().value);
				if(!mMediaPlayer.isPlaying())
					play();
			}});
		previewLineIdx.setOnClickListener(new View.OnClickListener(){
			@Override public void onClick(View v) {
				mMediaPlayer.seekTo(prevOffNode.getKey().value);
				mSeekBar.setProgress(prevOffNode.getKey().value);
				AdaptermySeekSet.setProgress(prevOffNode.getKey().value);
				if(!mMediaPlayer.isPlaying())
					play();
			}});
		mSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				if (fromUser) {
					mMediaPlayer.seekTo(progress);
					currenT.setText(CMN.FormTime(progress, 2));
					AdaptermySeekSet.setProgress(progress);
					currenTime = progress;
					notifyTimeChanged();
				}

				//intervalHeaderMarker
				RBTNode<myCpr<Integer,Integer>> intervalHeaderMarker = tree.xxing(new myCpr(progress, 0));
				if(intervalHeaderMarker!=null){
					int OldLine = intervalHeaderMarker_line;
					intervalHeaderMarker_line = intervalHeaderMarker.getKey().value;
					if(intervalHeaderMarker_line!=OldLine){
						View v = scrollLinearLayoutManager.getChildAt(OldLine-scrollLinearLayoutManager.findFirstVisibleItemPosition());
						if(v!=null)
							v.findViewById(R.id.tv0).setBackground(	null);
						v = scrollLinearLayoutManager.getChildAt(intervalHeaderMarker_line-scrollLinearLayoutManager.findFirstVisibleItemPosition());
						if(v!=null)
							v.findViewById(R.id.tv0).setBackground(	getResources().getDrawable(R.drawable.shixian));

					}
				}

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

			}
		});

		tabLayout.setScrollPosition(0,0,true);
		onTabSelected(tabLayout.getTabAt(0));
		return main_edit_layout;
	}

	int currenTime;
	private boolean isDragging=false;
	public static boolean isDraggingEffecting=false;
	Timer timer2 = new Timer();
	private void notifyTimeChanged() {
		//if current Time is out of domain[prev,nxt]
		if(currenTime>=nxtTimeNode.getKey().key || currenTime <prevTimeNode.getKey().key) {
			tv.post(new Runnable() {
				public void run() {
					//TODO optimise
					prevTimeNode = subs_timeNodeTree.xxing(new myCpr((int) currenTime+1, 0));
					nxtTimeNode = subs_timeNodeTree.sxing(new myCpr((int) currenTime, 0));
					if (prevTimeNode.getKey().compareTo(nxtTimeNode.getKey()) < 0) {

						int lineA = tv.getLayout().getLineForOffset(prevTimeNode.getKey().value);
						int lineB = tv.getLayout().getLineForOffset(nxtTimeNode.getKey().value);
						////this only works in naked TextView
						////tv.bringPointIntoView(789);
						int y = (int) ((lineA - 0.5) * tv.getLineHeight());//  - toptopsv.getHeight());
						int z = (int) ((lineB + 1) * tv.getLineHeight());//  - toptopsv.getHeight());

						int scrollA = s.getScrollY();
						int scrollB = scrollA+s.getHeight()/2;

						tv.highlighty1=tv.getLayout().getLineTop(lineA);
						tv.highlighty2=tv.getLayout().getLineTop(lineB);
						tv.invalidate();
						if(true&& !isDraggingEffecting ){
							s.smoothScrollTo(0, y);
						}else
							//if current subscript line is out of scrollview's scope
							if ((y < scrollA || z >=scrollB) && !isDraggingEffecting) {//&& !isDragging)
								s.smoothScrollTo(0, y);
								s.smoothScrollTo(0, y);//guess why a duplication,it's very android:)
							}


					}
				}
			});
			//![0]post
	}}

	public int scale(Context context, float value) {
		DisplayMetrics mDisplayMetrics = getResources().getDisplayMetrics();
		return scale(mDisplayMetrics.widthPixels,
				mDisplayMetrics.heightPixels, value);
	}
	public static int scale(int displayWidth, int displayHeight, float pxValue)
	{
		float scale = 1;
		try {
			float scaleWidth = (float) displayWidth / 720;
			float scaleHeight = (float) displayHeight / 1280;
			scale = Math.min(scaleWidth, scaleHeight);
		}catch(Exception e){

		}
		return Math.round(pxValue * scale * 0.5f);
	}

	//将path歌词文件扫入。
	public void prepare_lyric_assign(String path) {
		mainLyricAdapterData.clear();
		try {
			String str = new String(CMN.readParse(path));
			if (str.startsWith("{")) {//不普通
				JSONArray jsonArray = new JSONArray("[" + str + "]");
				JSONObject ducoj = jsonArray.getJSONObject(0);
				String lyrics = ducoj.optString("lyric");
				if ((lyrics != null) && lyrics.length() > 0) {
					if (lyrics.startsWith("[")) //不不普通 类似于提取歌词的处理
					{
						String[] lst1 = lyrics.split("\\n");
						HashMap<Integer, String> lst2_texts = new HashMap<Integer, String>();
						ArrayList<com.knizha.wangYiLP.myCpr> lst1_texts = new ArrayList<com.knizha.wangYiLP.myCpr>();
						int lnCounter = 0;
						tree.clear();
						tree2.clear();
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
								//if (!"".equals(text)) {//跳过冗余
								mainLyricAdapterData.add(text);
								myCpr tmpNode = new myCpr<Integer, Integer>(time,lnCounter++);//时间-行
								tree.insert(tmpNode);
								tree2.insert(tmpNode.Swap());

								//}
							}
						}
						updateSelectedPositions();
					} else { //普普通通
						int lnIndex = lyrics.indexOf("\n");
						int lnIndexOld = 0;
						while (lnIndex != -1) {
							//CMN.showT(lyrics.substring(lnIndexOld,lnIndex));
							mainLyricAdapterData.add(lyrics.substring(lnIndexOld + 2, lnIndex));
							lnIndexOld = lnIndex;
							lnIndex = lyrics.indexOf("\n", lnIndexOld + 2);
						}
						if (lnIndexOld <= lyrics.length() - 2) {
							mainLyricAdapterData.add(lyrics.substring(lnIndexOld, lyrics.length()));
						}
					}
				}
			} else {//普通，逐行散列

			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		mainLyricAdapter.notifyDataSetChanged();
		//CMN.showT("mainLyricAdapter");
	}

	int heightDelta = 0;
	@Override//监听键盘弹出
	public void onResize(int w, int h, int oldw, int oldh) {
		if(focusedView==null)
			return;
		//如果第一次初始化
		if (oldh == 0) {
			return;
		}
		//如果用户横竖屏转换
		if (w != oldw) {
			return;
		}
		if (h < oldh) {
			heightDelta = h - oldh -250;
			//RelativeLayout.LayoutParams lp = ((RelativeLayout.LayoutParams)splitterrl.getLayoutParams());
			//lp.bottomMargin=250;
			//splitterrl.setLayoutParams(lp);
			splitterrl.setTranslationY(-tv.getLineHeight());
			//输入法弹出
			//--CMN.showT("输入法弹出");
			scrollLinearLayoutManager.scrollToPositionWithOffset((Integer) focusedView.getTag(R.id.position), 0);
			//main_lv.scrollToPosition((Integer) focusedView.getTag(R.id.position));
		} else if (h > oldh) {
			heightDelta=0;
			//输入法关闭
			splitterrl.setTranslationY(0);
			//CMN.showT("输入法关闭");

		}
		//int distance = h - old;
		//EventBus.getDefault().post(new InputMethodChangeEvent(distance,mCurrentImageId));
	}

	@Override//toolbar菜单
	public boolean onMenuItemClick(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.save:
				dbCon.dumpNewProj(doc_Mp3, doc_Lrc, mSeekBar.tree, mainLyricAdapterData, subLyricAdapterData);
				break;
			case R.id.open:
				dbCon.setUpEditorByProj(this, (long) 11);
				break;
		}

		return false;
	}

	@Override//点击事件
	public void onClick(View v) {
		switch (v.getId()) {//text
			case R.id.play:
				if (!mMediaPlayer.isPlaying()) {//播放
					v.setBackgroundResource(R.drawable.ic_pause_black_24dp);
					play();
					isPausedExpected = false;
				} else {
					v.setBackgroundResource(R.drawable.ic_play_arrow_black_24dp);
					pause();
					isPausedExpected = true;
				}
				break;
			case R.id.browser_widget3://corlet
				boolean needUpdateLastAttatch = false;
				if(lastAtach!=null && startCandidate==lastAtach.getKey().value)
					needUpdateLastAttatch = true;
				int timeNow = mMediaPlayer.getCurrentPosition();
				//CMN.showT("add"+mSeekBar.getProgress()+":"+startCandidate);
				myCpr timeLineSearcherlet = new myCpr(startCandidate, timeNow);//行数-时间
				RBTNode<myCpr<Integer, Integer>> timeLineSearchedNode = tree2.search(timeLineSearcherlet);
				//TODO::优化search
				//根据行数搜时间
				if (timeLineSearchedNode != null) {//previously an another ms-time was assigned to the line at "startCandidate",thus we shall invalidate that assignment.(del time-lyric_line node)
					myCpr timeLineSearchRes = timeLineSearchedNode.getKey();
					//CMN.showT("del time-lyric_line node @ time:"+timeLineSearchRes.key);
					tree.remove(timeLineSearchRes.Swap());//时间-行数
				}
				tree2.insertUpdate(timeLineSearcherlet);
				//根据时间搜行数
				timeLineSearcherlet = new myCpr(timeNow, startCandidate);
				timeLineSearchedNode = tree.search(timeLineSearcherlet);
				if (timeLineSearchedNode != null) {//previously an another line possesses this ms-time,thus we shall invalidate that assignment.(del lyric_line-time node)
					int posTmp = timeLineSearchedNode.getKey().value;
					//CMN.showT("found!!!" + posTmp);
					mainLyricAdapter.selectedPositions.remove(posTmp);
					tree2.remove(timeLineSearchedNode.getKey().Swap());
					mainLyricAdapter.notifyItemChanged(posTmp);
				}
				if(needUpdateLastAttatch)
					lastAtach = tree.insertUpdate(timeLineSearcherlet);
				else
					tree.insertUpdate(timeLineSearcherlet);
				AdaptermySeekSet.notifyDataSetChanged();
				mainLyricAdapter.selectedPositions.put(startCandidate, CMN.FormTime(timeNow, 0));

				mSeekBar.invalidate();
				updateStartCandidate(startCandidate + 1);
				break;
			case R.id.browser_widget2://zuo
				if (tree.getRoot() != null) {
					lastAtach = tree.xxing_samsara(new myCpr(mSeekBar.getProgress(), 0));
					mMediaPlayer.seekTo(lastAtach.getKey().key);
					//mMediaPlayer.setPosition(lastAtach.getKey()*1.0f/mMediaPlayer.getLength());
					if (!mMediaPlayer.isPlaying()) {
						currenT.setText(CMN.FormTime(lastAtach.getKey().key, 2));
						//mSeekBar.invalidate();
					}
					if(scrollLinearLayoutManager.findLastVisibleItemPosition()<lastAtach.getKey().value ||
							scrollLinearLayoutManager.findFirstVisibleItemPosition()>lastAtach.getKey().value)
						scrollLinearLayoutManager.scrollToPositionWithOffset(lastAtach.getKey().value, 100);
					mSeekBar.setProgress(lastAtach.getKey().key);
					AdaptermySeekSet.setProgress(lastAtach.getKey().key);top_adjustBar.setActiveColor(colorSheet[lastAtach.getKey().value%colorSheet.length]);
					show_adjustBar_toggle();
					top_adjustBar.invalidate();
				}
				break;
			case R.id.browser_widget4://you
				if (tree.getRoot() != null) {
					lastAtach = tree.sxing_samsara(new myCpr(mSeekBar.getProgress(), 0));
					mMediaPlayer.seekTo(lastAtach.getKey().key);
					//mMediaPlayer.setPosition(lastAtach.getKey()*1.0f/mMediaPlayer.getLength());
					if (!mMediaPlayer.isPlaying()) {
						currenT.setText(CMN.FormTime(lastAtach.getKey().key, 2));
						//mSeekBar.invalidate();
					}
					mSeekBar.setProgress(lastAtach.getKey().key);
					AdaptermySeekSet.setProgress(lastAtach.getKey().key);
					if(scrollLinearLayoutManager.findLastVisibleItemPosition()<lastAtach.getKey().value ||
							scrollLinearLayoutManager.findFirstVisibleItemPosition()>lastAtach.getKey().value)
						scrollLinearLayoutManager.scrollToPositionWithOffset(lastAtach.getKey().value, 0);
					top_adjustBar.setActiveColor(colorSheet[lastAtach.getKey().value%colorSheet.length]);
					show_adjustBar_toggle();
					top_adjustBar.invalidate();
				}
				break;
			case R.id.splitTime:
				if(seekScroll.getVisibility()==View.VISIBLE) {
					seekScroll.setVisibility(View.INVISIBLE);
					seekScrollVisible = false;
				}else {
					seekScroll.setVisibility(View.VISIBLE);
					seekScrollVisible = true;
				}
				break;
			case R.id.adjustBar_toggle:
				if(top_adjustLayout.getVisibility()==View.VISIBLE)
					top_adjustLayout.setVisibility(View.INVISIBLE);
				else
					top_adjustLayout.setVisibility(View.VISIBLE);
				break;
		}
	}

	public void show_adjustBar_toggle(){
		adjustBar_toggle.setVisibility(View.VISIBLE);
		//adjustBar_toggle.setColorFilter(Color.parseColor("#0000ff"));
		adjustBar_toggle.getBackground().setColorFilter(new LightingColorFilter(top_adjustBar.getActiveColor(), 0x00000000));
		ScaleAnimation anima = new ScaleAnimation(1.5f,1,1.5f,1, Animation.RELATIVE_TO_SELF, 0.5f);
		anima.setDuration(300);
		anima.setFillAfter(true);
		adjustBar_toggle.startAnimation(anima);
	}
	//
	/*在第startCandidate行处出显示红色方块标记，表示下一个时间点将assign到该行。*/
	private void updateStartCandidate(int position) {
		if (position == startCandidate)
			return;
		if (position < 0 || position >= mainLyricAdapterData.size())
			return;
		int tmp = startCandidate;
		startCandidate = position;
		mainLyricAdapter.notifyItemChanged(startCandidate);
		mainLyricAdapter.notifyItemChanged(tmp);
	}

	/*选中——>背景色变化，表示一个时间点已经assign到该行。*/
	public void updateSelectedPositions() {
		mainLyricAdapter.selectedPositions.clear();
		tree2.setInOrderDo(new RBTree.inOrderDo() {
			@Override
			public void dothis(RBTNode node) {
				myCpr<Integer, Integer> tmp = (myCpr<Integer, Integer>) node.getKey();
				mainLyricAdapter.selectedPositions.put(tmp.key, CMN.FormTime(tmp.value, 1));
				mainLyricAdapter.notifyItemChanged(tmp.key);
			}
		});
		tree2.inOrderDo();
	}

	//
	//![0]mediaplayer、ui stuffs
	private Timer mTimer = new Timer(); // 计时器

	public void prepareMedia(String path) {
		mMediaPlayer = new MediaPlayer();
		try {
			mMediaPlayer.setDataSource(path);
			mMediaPlayer.prepare();
			isMediaPrepared = true;
		} catch (IOException e) {
			isMediaPrepared = false;
			e.printStackTrace();
			CMN.show("prepareMedia ERROR:" + e.getLocalizedMessage());
			return;
		}
		int lengthTmp = mMediaPlayer.getDuration();
		mSeekBar.setMax(lengthTmp);
		seekSetTimeSplitNumber = lengthTmp/(60*1000);
		seekSetSpareTime = lengthTmp - seekSetTimeSplitNumber*(60*1000);
		if(seekSetSpareTime!=0) ++seekSetTimeSplitNumber;
		seekSetActiveIdx = 0;
		AdaptermySeekSet.notifyDataSetChanged();
		totalT.setText(CMN.FormTime(mMediaPlayer.getDuration(), 2));
	}

	/*根据时间重建播放器*/
	public void resumePlayer(int userPauseTime) {
		if (mMediaPlayer != null) {
			mMediaPlayer.reset();
			mMediaPlayer.release();
			mMediaPlayer = null;
		}
		prepareMedia(doc_Mp3);
		mMediaPlayer.seekTo(userPauseTime);
		if (!isPausedExpected) {//当前应当正在播放
			play();
		}
	}

	public void play() {
		isPausedExpected = false;
		// 每一秒触发一次
		mTimer = new Timer();
		mTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				if (mMediaPlayer == null)
					return;
				if (mMediaPlayer.isPlaying() && mSeekBar.isPressed() == false)
					handler.sendEmptyMessage(0); // 发送消息
			}
		}, 0, 500);
		mMediaPlayer.start();
	}

	public void pause() {
		isPausedExpected = true;
		mTimer.cancel();
		mMediaPlayer.pause();
	}

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			int position = mMediaPlayer.getCurrentPosition();
			int duration = mMediaPlayer.getDuration();
			if (duration > 0) {
				mSeekBar.setProgress(position);
				currenT.setText(CMN.FormTime(position, 2));
				AdaptermySeekSet.setProgress(position);
				currenTime = position;
				notifyTimeChanged();
			}
		}
	};
	//![0]
	Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case 110:
					isDraggingEffecting = false;
					splitterrl.setVisibility(View.INVISIBLE);
			}}
	};

	class tk_gross extends TimerTask{
		int msg = 0;
		tk_gross(int message){super();msg = message;}
		@Override
		public void run() {
			mHandler.sendEmptyMessage(msg);
		}
	};


	@Override public void onTabSelected(TabLayout.Tab tab) {
		if(Oldtablet!=null)
			Oldtablet.setVisibility(View.INVISIBLE);
		switch (tab.getPosition()){
			case 0:
				Oldtablet = s;
				sb = new StringBuffer().append("\r\n");
				dataIdx=0;
				subs_timeNodeTree = new  RBTree<myCpr<Integer,Integer>>();
				subs_timeNodeTree2 = new RBTree<myCpr<Integer,Integer>>();
				subs_timeNodeTree.insert(new myCpr(0, sb.length()));
				subs_timeNodeTree2.insert(new myCpr(sb.length(), 0));

				tree.setInOrderDo(new RBTree.inOrderDo() {
					@Override
					public void dothis(RBTNode node) {
						int time = ((myCpr<Integer,Integer>)node.getKey()).key;
						subs_timeNodeTree.insert(new myCpr(time, sb.length()));
						subs_timeNodeTree2.insert(new myCpr(sb.length(), time));
						sb.append(mainLyricAdapterData.get(dataIdx)).append("\r\n\r\n");
						dataIdx++;
					}
				});
				tree.inOrderDo();
				subs_timeNodeTree.insert(new myCpr(999999999, sb.length()));
				subs_timeNodeTree2.insert(new myCpr(sb.length(),999999999));
				prevTimeNode = subs_timeNodeTree.getRoot();
				nxtTimeNode = subs_timeNodeTree.sxing(prevTimeNode.getKey());
				tv.setTextKeepState(sb);

				Oldtablet.setVisibility(View.VISIBLE);
				break;
			case 1:
				Oldtablet = main_lv;
				Oldtablet.setVisibility(View.VISIBLE);
				break;
			case 2:
				break;
			case 3:
				break;
		}
	}
	@Override public void onTabUnselected(TabLayout.Tab tab) {

	}
	@Override public void onTabReselected(TabLayout.Tab tab) {

	}



	//
	//![1]用于:列表点击时展开，否则collapse
	//![1]用于:输入法弹出时自动跳转
	View focusedView = null;

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		if (focusedView != null)
			((EditText) focusedView).setSingleLine(true);
		focusedView = v;
		((EditText) focusedView).setSingleLine(false);
	}

	//![1]

	//![2]主歌词、翻译歌词的列表适配器 stuffs
	public interface OnItemClickListener {
		void onItemClick(View view, int position);
	}

	public interface OnItemLongClickListener {
		void onItemLongClick(View view, int position);
	}

	/*列表视图容器*/
	class MyViewHolder extends RecyclerView.ViewHolder {
		private ImageView icon;
		TextView time;
		TextView line;
		TextView indicator;
		private EditText url;

		public MyViewHolder(View view) {
			super(view);
			url = (EditText) view.findViewById(R.id.et);
			time = (TextView) view.findViewById(R.id.tv1);
			line = (TextView) view.findViewById(R.id.tv0);
			indicator = (TextView) view.findViewById(R.id.tv3);
			//tv.setTextColor(Color.parseColor("#ff000000"));
			//tv.setTextSize(TypedValue.COMPLEX_UNIT_PX,CMN.a.scale(81));//TODO: optimize
		}
	}

	/*for main list - lyrics
	   参见：live pull -> main -> main_list_Adapter
	   参见：P.L.O.D -> float search view -> HomeAdapter*/
	class main_list_Adapter extends RecyclerView.Adapter<MyViewHolder> {
		String currentProjId;
		//作为recycler view 的数据存储
		public ArrayList<String> module_set;

		//构造
		main_list_Adapter(ArrayList<String> module_set_) {
			module_set = module_set_;
		}

		protected void update_slots_time(int position, String val) {
			if (val != null)
				slots_time.put(position, val);
			else {
				slots_time.remove(position);
			}
			notifyItemChanged(position);
		}

		@Override
		public int getItemCount() {
			return module_set.size();
		}

		public HashMap<Integer, String> selectedPositions = new HashMap<Integer, String>();
		public HashMap<Integer, String> slots_time = new HashMap<Integer, String>();
		private int mTouchItemPosition = -1;
		private OnItemClickListener mOnItemClickListener;
		private OnItemLongClickListener mOnItemLongClickListener;

		//单击
		public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
			this.mOnItemClickListener = mOnItemClickListener;
		}

		//长按
		public void setOnItemLongClickListener(OnItemLongClickListener mOnItemLongClickListener) {
			this.mOnItemLongClickListener = mOnItemLongClickListener;
		}

		//Create
		@Override
		public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			//TODO find out why CMN.a.mInflater stimes NullPointerException
			MyViewHolder holder = new MyViewHolder(CMN.a.inflater.inflate(R.layout.listview_item1, parent, false));
			return holder;
		}

		//Bind
		@Override
		public void onBindViewHolder(final MyViewHolder holder, final int position) {
			if (startCandidate == position) {
				holder.indicator.setVisibility(View.VISIBLE);
			} else {
				holder.indicator.setVisibility(View.INVISIBLE);
			}
			if (intervalHeaderMarker_line == position) {
				holder.line.setBackground(getResources().getDrawable(R.drawable.shixian));
			}else{
				holder.line.setBackground(null);
			}
			if (selectedPositions.containsKey(position)) {
				holder.itemView.setBackgroundColor(Color.parseColor("#4F7FDF"));//FF4081//4F7FDF//aaa0f0f0
				holder.time.setText(selectedPositions.get(position));
				holder.indicator.setVisibility(View.VISIBLE);
				if (startCandidate != position)
					holder.indicator.setBackgroundColor(colorSheet[position%20]);
				else
					holder.indicator.setBackgroundColor(Color.parseColor("#ff0000"));
			} else {
				holder.itemView.setBackgroundColor(Color.parseColor("#00a0f0f0"));//aaa0f0f0
				holder.time.setText("---");
				holder.indicator.setBackgroundColor(Color.parseColor("#ff0000"));
			}
			holder.line.setText(position + "");
			holder.url.setTag(R.id.position, position);
			//holder.url.clearFocus();
			holder.url.setText(module_set.get(position));
			holder.url.addTextChangedListener(new TextWatcher() {
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				}

				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
				}

				@Override
				public void afterTextChanged(Editable s) {
					if (CMN.a == null) return;//TODO :optimize
					if (CMN.a.getCurrentFocus() == holder.url) {
						module_set.set((Integer) holder.url.getTag(R.id.position), s.toString());
					}
				}
			});

			holder.url.setOnTouchListener(new View.OnTouchListener() {
				@Override
				public boolean onTouch(View view, MotionEvent event) {
					//注意，此处必须使用getTag的方式，不能将position定义为final，写成mTouchItemPosition = position
					mTouchItemPosition = position;
					return false;
				}
			});
			//if(!CMN.a.opt.editor_collapse_item_when_NotFocused) {
			//	holder.url.setSingleLine(false);
			//}else{
			holder.url.setOnFocusChangeListener(Main_lyric_Fragment.this);
			//}
			//处理 Focus 冲突
			//if (mTouchItemPosition == position) {
			//	holder.url.requestFocus();
			//	//holder.url.setSelection(holder.url.getText().length());
			//} else {
			//	holder.url.clearFocus();
			//	holder.url.setSingleLine();
			//}

			if (mOnItemClickListener != null) {
				holder.itemView.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						//getLayoutPosition?
						mOnItemClickListener.onItemClick(holder.itemView, position); // 2
					}
				});
			}
			if (mOnItemLongClickListener != null) {
				holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
					@Override
					public boolean onLongClick(View v) {
						int position = holder.getLayoutPosition();
						mOnItemLongClickListener.onItemLongClick(holder.itemView, position);
						return true;
					}
				});
			}

		}

		public void removeSelected(int position) {
			selectedPositions.remove(position);
			notifyItemChanged(position);
		}

		public boolean containSelected(int position) {
			return selectedPositions.containsKey(position);
		}
	}
	//![2]


	//![3] bottom seekBars' set:split per minute
	/*for  editor layout - multi seekBars as one
           */
	static class viewHolder {
		private SplitSeekBarmy seeklet;
	}

	class SeekSetAdapter extends BaseAdapter implements  SeekBar.OnSeekBarChangeListener {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				if(fromUser){
					//TODO check
					int tagTmp = (int)seekBar.getTag(R.id.position);
					if(seekSetActiveIdx!=tagTmp) {//激发换行
						seekSetActiveIdx = tagTmp;
						AdaptermySeekSet.notifyDataSetChanged();
						seekBar.setThumb(getResources().getDrawable(R.drawable.ic_crop_free_black_24dp));
					}
					mMediaPlayer.seekTo(progress+tagTmp*60*1000);
					currenT.setText(CMN.FormTime(progress+tagTmp*60*1000,2));
					//if(!mMediaPlayer.isPlaying())
					currenTime = progress+tagTmp*60*1000;
					mSeekBar.setProgress(currenTime);
					notifyTimeChanged();
				}
			}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

			}

		public void setProgress(int progress){
			int seekSetActiveIdxTmp = progress/(60*1000);
			int seekSetSpareTime = progress - seekSetActiveIdxTmp*(60*1000);
			//if(seekSetSpareTime!=0 && seekSetActiveIdxTmp!=0){++seekSetActiveIdxTmp;}
			final int firstListItemPosition = 0;
			if(seekSetActiveIdxTmp!=seekSetActiveIdx){
				//CMN.showT(seekSetActiveIdxTmp+":"+seekSetActiveIdx);
				//try {
				//	((SplitSeekBarmy) ((ViewGroup) lv_seekbars.getChildAt(seekSetActiveIdx-firstListItemPosition)).getChildAt(0)).setThumb(null);
				//	if(seekSetActiveIdxTmp>seekSetActiveIdx)
				//		((SplitSeekBarmy) ((ViewGroup) lv_seekbars.getChildAt(seekSetActiveIdx-firstListItemPosition)).getChildAt(0)).setProgress(60*1000);
				//	else
				//		((SplitSeekBarmy) ((ViewGroup) lv_seekbars.getChildAt(seekSetActiveIdx-firstListItemPosition)).getChildAt(0)).setProgress(0);
				//}catch (Exception e){}
				// seekSetActiveIdx = seekSetActiveIdxTmp;
				seekSetActiveIdx = seekSetActiveIdxTmp;
				AdaptermySeekSet.notifyDataSetChanged();
			}
			try {
				((SplitSeekBarmy)((ViewGroup)lv_seekbars.getChildAt(seekSetActiveIdx-firstListItemPosition)).getChildAt(0)).setProgress(seekSetSpareTime);


				((SplitSeekBarmy)((ViewGroup)lv_seekbars.getChildAt(seekSetActiveIdx-firstListItemPosition)).getChildAt(0)).setThumb(getResources().getDrawable(R.drawable.ic_crop_free_black_24dp));
			}catch (Exception e){CMN.showT(e.getLocalizedMessage());}

		}

		@Override
		public int getCount() {
			return seekSetTimeSplitNumber;
		}

		@Override
		public Object getItem(int position) {return null;}

		@Override
		public long getItemId(int position) {return 0;}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			//return lstItemViews.get(position);
			viewHolder vh;
			if (convertView != null) {
				vh = (viewHolder) convertView.getTag();
				vh.seeklet.setTag(R.id.position,position);
				if(position<seekSetActiveIdx){
					vh.seeklet.setThumb(null);
					vh.seeklet.setProgress(60*1000);
				}else if(position>seekSetActiveIdx) {
					vh.seeklet.setThumb(null);
					vh.seeklet.setProgress(0);
				}else {
					vh.seeklet.setThumb(getResources().getDrawable(R.drawable.ic_crop_free_black_24dp));
				}
				vh.seeklet.setOnSeekBarChangeListener(this);
			} else {
				convertView = View.inflate(CMN.a, R.layout.item, null);
				vh = new viewHolder();
				vh.seeklet = (SplitSeekBarmy) convertView.findViewById(R.id.progress);
				vh.seeklet.setTag(R.id.position,position);
				if(position<seekSetActiveIdx){
					vh.seeklet.setThumb(null);
					vh.seeklet.setProgress(60*1000);
				}else if(position>seekSetActiveIdx) {
					vh.seeklet.setThumb(null);
					vh.seeklet.setProgress(0);
				}else {
					vh.seeklet.setThumb(getResources().getDrawable(R.drawable.ic_crop_free_black_24dp));
				}
				vh.seeklet.tree = tree;
				vh.seeklet.tree2 = tree2;
				vh.seeklet.cs = colorSheet;
				vh.seeklet.setMax(60*1000);
				vh.seeklet.setOnSeekBarChangeListener(this);
				convertView.setTag(vh);
			}
			return convertView;
		}
	};
	//![3]


}
