package com.knizha.wangYiLP;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.support.design.widget.TabLayout;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
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
import android.widget.ToggleButton;

import com.fenwjian.sdcardutil.RBTNode;
import com.fenwjian.sdcardutil.RBTree;
import com.fenwjian.sdcardutil.ScrollViewmy;
import com.fenwjian.sdcardutil.SeekBarmy;
import com.fenwjian.sdcardutil.SplitSeekBarmy;
import com.fenwjian.sdcardutil.TextViewmy;
import com.fenwjian.sdcardutil.myCpr;
import com.jaygoo.widget.RangeSeekBar;
import android.widget.NumberPicker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import db.DBWangYiLPController;

public class Main_editor_Fragment extends Fragment
		implements View.OnClickListener,RelativeLayoutmy.OnResizeListener,
					Toolbar.OnMenuItemClickListener,
					View.OnFocusChangeListener, TabLayout.OnTabSelectedListener {
    AlertDialog d;
    View dv;
    private final int Default_InvalidationTime=800;
	public int startCandidate=0;
	int intervalHeaderMarker_line;
	String currentProjID;
	int oldAdjustingKey;
    public RBTNode<myCpr<Integer, Integer>> lastAttach;
	//public String doc_Mp3 = "/sdcard/netease/cloudmusic/Music/t.mp3";
	//public String doc_Lrc = "/sdcard/netease/cloudmusic/Music/t.txt";
	public String doc_Mp3 = "";
	public String doc_Lrc = "";
	boolean isPausedExpected = true;
	public boolean isMediaPrepared = false;
	MediaPlayer mMediaPlayer;
	View playButton;

	ScrollView sv;
	TextView totalT;
	TextView currenT;
	SeekBarmy mSeekBar;
	public RBTree<myCpr<Integer, Integer>> tree;//时间-行
	public RBTree<myCpr<Integer, Integer>> tree2;//行-时间//TODO opt 复用结点
	ScrollLinearLayoutManager scrollLinearLayoutManager;
	ScrollLinearLayoutManager scrollLinearLayoutManager2;
	ScrollView seekScroll;
	ListView lv_seekbars;
	private long prevABTime = 0;
	private long nxtABTime = 0;
	private boolean isABLooping=false;
	ImageView adjustBar_toggle;
	RelativeLayout top_adjustLayout;
	RelativeLayout itemsLayout;
	RangeSeekBar top_adjustBar;
	TextView top_time;
	RecyclerView main_lv;//use sub_lv&&main_lv in a multiplex mode
	//RecyclerView sub_lv;
	public main_list_Adapter mainLyricAdapter;
	//public main_list_Adapter subLyricAdapter;
	public ArrayList<String> mainLyricAdapterData = new ArrayList<String>();
	public ArrayList<String> subLyricAdapterData = new ArrayList<String>();
	DBWangYiLPController dbCon;

	ViewGroup src_layout;
	TextView src_tv;
	TextView doc_mp3_tv;
	TextView doc_lrc_tv;
	public EditText src_title_et;

	int seekSetTimeSplitNumber = 0;
	int seekSetSpareTime = 0;
	int seekSetActiveIdx = 0;
	boolean seekScrollVisible;
	SeekSetAdapter AdaptermySeekSet = new SeekSetAdapter();
	int colorSheet[] = new int[20];
	View Oldtablet;


	private int actionBarHeight=0;
	private int tvPaddingTop=0,tvPaddingBottom=0;
	public ToggleButton viewpager_locker;
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
	//int dataIdx;
	StringBuffer sb;
	//StringBuilder sb;
	private RBTree<myCpr<Integer,Integer>> subs_timeNodeTree;//时间-文本偏移
	private RBTree<myCpr<Integer,Integer>> subs_timeNodeTree2;//文本偏移-时间
	float rangeSBDelta;
	private RBTNode<myCpr<Integer,Integer>> prevTimeNode,nxtTimeNode,
			prevOffNode,nxtOffNode;
	protected TabLayout tabLayout;
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
		tabLayout = (TabLayout) main_edit_layout.findViewById(R.id.tabLayout);
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
		//tv.setMovementMethod(new ScrollingMovementMethod());
		bottomtop=(RelativeLayout)main_edit_layout.findViewById(R.id.bottomtop);
		bottom=(RelativeLayout)main_edit_layout.findViewById(R.id.bottom);
		breakABLoop.setOnClickListener(new View.OnClickListener(){
			@Override public void onClick(View v) {
				isABLooping = false;
				breakABLoop.setVisibility(View.INVISIBLE);
			}});
		mSeekBar = (SeekBarmy) main_edit_layout.findViewById(R.id.progress);
		sv = (ScrollView) main_edit_layout.findViewById(R.id.sv);
		totalT = (TextView) main_edit_layout.findViewById(R.id.totalT);
		currenT = (TextView) main_edit_layout.findViewById(R.id.currentT);
		Oldtablet =
		main_lv = (RecyclerView) main_edit_layout.findViewById(R.id.main_lyric_list);
		//sub_lv = (RecyclerView) main_edit_layout.findViewById(R.id.sub_lyric_list);
		seekScroll = (ScrollView) main_edit_layout.findViewById(R.id.seekScroll);
		top_adjustLayout = (RelativeLayout) main_edit_layout.findViewById(R.id.DingLayout);
		itemsLayout = (RelativeLayout) main_edit_layout.findViewById(R.id.items_layout);
		top_adjustBar = (RangeSeekBar) main_edit_layout.findViewById(R.id.adjustBar);
		top_time = (TextView) main_edit_layout.findViewById(R.id.jingShi);
		lv_seekbars = (ListView) main_edit_layout.findViewById(R.id.lv_seekbars);
		adjustBar_toggle = (ImageView) main_edit_layout.findViewById(R.id.adjustBar_toggle);
		src_layout = ((ViewGroup)main_edit_layout.findViewById(R.id.src_layout));
		src_tv = ((TextView)main_edit_layout.findViewById(R.id.src_tv));
		doc_mp3_tv = ((TextView)main_edit_layout.findViewById(R.id.doc_mp3_tv));
		doc_lrc_tv = ((TextView)main_edit_layout.findViewById(R.id.doc_lrc_tv));
		src_title_et = ((EditText)main_edit_layout.findViewById(R.id.src_title_et));
		adjustBar_toggle.setOnClickListener(this);
		playButton = main_edit_layout.findViewById(R.id.play);
		playButton.setOnClickListener(this);
		main_edit_layout.findViewById(R.id.splitTime).setOnClickListener(this);
		main_edit_layout.findViewById(R.id.browser_widget1).setOnClickListener(this);
		main_edit_layout.findViewById(R.id.browser_widget2).setOnClickListener(this);
		main_edit_layout.findViewById(R.id.browser_widget3).setOnClickListener(this);
		main_edit_layout.findViewById(R.id.browser_widget4).setOnClickListener(this);
		main_edit_layout.findViewById(R.id.browser_widget5).setOnClickListener(this);
		main_edit_layout.findViewById(R.id.deletln).setOnClickListener(this);
		main_edit_layout.findViewById(R.id.addln).setOnClickListener(this);

		viewpager_locker=(ToggleButton)main_edit_layout.findViewById(R.id.items1);
		ToggleButton t2=(ToggleButton)main_edit_layout.findViewById(R.id.items2);
		ToggleButton t3=(ToggleButton)main_edit_layout.findViewById(R.id.items3);
		ToggleButton t4=(ToggleButton)main_edit_layout.findViewById(R.id.items4);
		ToggleButton t5=(ToggleButton)main_edit_layout.findViewById(R.id.toptoggle_follow);
		ToggleButton t6=(ToggleButton)main_edit_layout.findViewById(R.id.toptoggle_adjustAll);
		ToggleButton t7=(ToggleButton)main_edit_layout.findViewById(R.id.toptoggle_loop);
		ToggleButton t8=(ToggleButton)main_edit_layout.findViewById(R.id.toptoggle_release);
		viewpager_locker.setOnClickListener(this);
		t2.setOnClickListener(this);
		t3.setOnClickListener(this);
		t4.setOnClickListener(this);
		t5.setOnClickListener(this);
		t6.setOnClickListener(this);
		t7.setOnClickListener(this);
		if(CMN.opt.viewPagerLocked){
			viewpager_locker.setChecked(true);
		}
		if(CMN.opt.attatch_cursor_coupled){
			t2.setChecked(true);
		}
		if(CMN.opt.auto_move_cursor){
			t3.setChecked(true);
		}
		if(CMN.opt.add_agjust_coupled){
			t4.setChecked(true);
		}
		if(CMN.opt.adjust_seek_coupled){
			t5.setChecked(true);
		}
		if(CMN.opt.adjust_all){
			t6.setChecked(true);
		}
		if(CMN.opt.isAttABLooping){
			t7.setChecked(true);
		}
		mSeekBar.ini();
		mSeekBar.tree = tree;
		top_adjustBar.setOnRangeChangedListener(new RangeSeekBar.OnRangeChangedListener(){
			float oldPos=0f;
			float overShootThresholdDelta=0.0f;
			@Override
			public void onRangeChanged(RangeSeekBar view, final float pos, boolean isFromUser) {
				if(isFromUser){
					if(tree.getRoot()==null)//nothing to adjust
						return;
					int newKey=-1;
					RBTNode<myCpr<Integer, Integer>> nodeTmp=null;
					if(lastAttach!=null) {
						nodeTmp = tree.successor(lastAttach);
						//newKey = (int) (oldAdjustingKey + (pos - 0.5f) * 60 * 1000);
						newKey = (int) (oldAdjustingKey + (pos) * timeScaler);
						if (newKey < 0)
							newKey = 0;
						newKey = Math.min(newKey, mMediaPlayer.getDuration());
					}
					if(!CMN.opt.adjust_all){
						//CMN.showT(pos+"");
						if(newKey!=-1) {
							int valTmp = lastAttach.getKey().value;
							if (nodeTmp != null && newKey >= nodeTmp.getKey().key) {
								int valTmp2 = nodeTmp.getKey().key;
								lastAttach.getKey().value = nodeTmp.getKey().value;
								lastAttach.getKey().key = valTmp2;
								nodeTmp.getKey().value = valTmp;
								lastAttach = nodeTmp;
							} else {
								nodeTmp = tree.predecessor(lastAttach);
								if (nodeTmp != null && newKey <= nodeTmp.getKey().key) {
									int valTmp2 = nodeTmp.getKey().key;
									lastAttach.getKey().value = nodeTmp.getKey().value;
									lastAttach.getKey().key = valTmp2;
									nodeTmp.getKey().value = valTmp;
									lastAttach = nodeTmp;
								}
							}

							RBTNode<myCpr<Integer, Integer>> treeNodeTmp = tree2.search(new myCpr(lastAttach.getKey().value, 0));
							if (treeNodeTmp != null)
								treeNodeTmp.getKey().value = newKey;//修正行数-时间中的时间
							lastAttach.getKey().key = newKey;
							AdaptermySeekSet.notifyDataSetChanged();
						}
					}else {//全部调整
						rangeSBDelta = ((pos - oldPos) * timeScaler);
						float tmpV = 0;
						if (overShootThresholdDelta != 0) {
							tmpV = overShootThresholdDelta;
							overShootThresholdDelta += rangeSBDelta;//超射代偿
							if (overShootThresholdDelta == 0 || Math.signum(overShootThresholdDelta) * Math.signum(tmpV) == -1) {
								oldPos = pos;
								return;
							} else {
								overShootThresholdDelta = 0;
								rangeSBDelta += tmpV;
							}
						}
						float min = tree.minimum().key + rangeSBDelta;
						float max = tree.maximum().key + rangeSBDelta;
						if (min >= 0 && max <= mSeekBar.getMax()) {

						} else {//记录初次超射值
							if (min < 0) {
								overShootThresholdDelta = min;
								rangeSBDelta = -tree.minimum().key;
							} else {
								overShootThresholdDelta = max - mSeekBar.getMax();
								rangeSBDelta = mSeekBar.getMax() - tree.maximum().key;
							}
						}
						tree.setInOrderDo(new RBTree.inOrderDo() {
							@Override
							public void dothis(RBTNode node) {
								myCpr<Integer, Integer> resTmp = ((RBTNode<myCpr<Integer, Integer>>) node).getKey();
								resTmp.key =(int)(resTmp.key+rangeSBDelta);
							}
						});
						tree.inOrderDo();
						mSeekBar.invalidate();
						AdaptermySeekSet.notifyDataSetChanged();
						tree2.setInOrderDo(new RBTree.inOrderDo() {
							@Override
							public void dothis(RBTNode node) {
								myCpr<Integer, Integer> resTmp = ((RBTNode<myCpr<Integer, Integer>>) node).getKey();
								resTmp.value =(int)(resTmp.value+rangeSBDelta);							}
						});
						tree2.inOrderDo();

					}
					//CMN.showTT("到达！");
					if (CMN.opt.adjust_seek_coupled) {
						mMediaPlayer.seekTo(newKey);
						AdaptermySeekSet.setProgress(newKey);
						mSeekBar.setProgress(newKey);
					}
					if(lastAttach!=null)
						top_time.setText(CMN.FormTime(lastAttach.getKey().key,0));
					oldPos = pos;
				}
			}

			@Override
			public void onStartTrackingTouch(RangeSeekBar view, boolean isLeft) {
				if(lastAttach!=null)
					oldAdjustingKey = lastAttach.getKey().key;
			}

			@Override
			public void onStopTrackingTouch(RangeSeekBar view, boolean isLeft) {
				top_adjustBar.reSetPosition();
				updateSelectedPositions();
				oldPos=0f;
				overShootThresholdDelta=0.0f;
			}
		});
		lv_seekbars.setAdapter(AdaptermySeekSet);
		scrollLinearLayoutManager = new ScrollLinearLayoutManager(CMN.a);
		scrollLinearLayoutManager2 = new ScrollLinearLayoutManager(CMN.a);
		scrollLinearLayoutManager.setScrollEnabled(true);
		scrollLinearLayoutManager2.setScrollEnabled(true);
		//main_lv.setLayoutManager(new LinearLayoutManager(CMN.a));
		main_lv.setLayoutManager(scrollLinearLayoutManager);
		//sub_lv.setLayoutManager(scrollLinearLayoutManager2);
		mainLyricAdapter = new main_list_Adapter(mainLyricAdapterData);
		//subLyricAdapter = new main_list_Adapter(subLyricAdapterData);//翻译适配器
		mainLyricAdapter.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(View view, int position) {
				updateStartCandidate(position);
			}
		});
		main_lv.setAdapter(mainLyricAdapter);
		//sub_lv.setAdapter(subLyricAdapter);
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
						//if(!isDraggingEffecting) {
							tk_gross tktk = new tk_gross(110);
							timer2 = new Timer();
							timer2.schedule(tktk, Default_InvalidationTime);
						//}
						//TODO:: 怎么办，我也很绝望啊
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
							//previewLineIdx.setText(line+"");
							int offStrt = tv.getLayout().getOffsetForHorizontal(line, 0)+1;
							//nxtOffNode = subs_timeNodeTree2.sxing(new myCpr(offStrt, 0));
							//prevOffNode = subs_timeNodeTree2.predecessor(nxtOffNode);
							prevOffNode = subs_timeNodeTree2.xxing(new myCpr(offStrt, 0));
							if(prevOffNode==null) return;//TODO:xxx 输入法弹出导致不匹配而崩溃
							//TODO opt
							nxtOffNode =  subs_timeNodeTree2.sxing(new myCpr(offStrt, 0));
							RBTNode<myCpr<Integer,Integer>> tmpSNode = tree.sxing(new myCpr(prevOffNode.getKey().value, 0));
							if(tmpSNode!=null)
							previewLineIdx.setText(tmpSNode.getKey().value+"");
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
		jumpTo.setOnClickListener(this);
		previewLineIdx.setOnClickListener(this);
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
				//原
			}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

			}
		});

		if(cursorBlinking) {
			mTimer = new Timer();
			mTimer.schedule(new TimerTask() {
				@Override
				public void run() {
					handler.sendEmptyMessage(1); // 发送消息
				}
			}, 0, 250);
		}

		tabLayout.setScrollPosition(1,0,true);
		tabLayout.getTabAt(1).select();
		onTabSelected(tabLayout.getTabAt(0));
		onTabSelected(tabLayout.getTabAt(1));
		return main_edit_layout;
	}









	int currenTime;
	private boolean isDragging=false;
	public static boolean isDraggingEffecting=false;
	Timer timer2 = new Timer();
	//called when:
	//seekbar dragged by user
	//mMediaPlayer timer
	//seekSet dragged by user
	private void notifyTimeChanged() {
		//if current Time is out of domain[prev,nxt]
		if(currenTime>=nxtTimeNode.getKey().key || currenTime <prevTimeNode.getKey().key) {
			//CMN.showTT("triggered!");
			tv.post(new Runnable() {
				public void run() {
					prevTimeNode = subs_timeNodeTree.xxing(new myCpr((int) currenTime, 0));
					if(prevTimeNode==null)
						return;
					nxtTimeNode = subs_timeNodeTree.successor(prevTimeNode);
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
					y-=heightDelta/CMN.dm.density;
					if(!isDraggingEffecting ){
						s.smoothScrollTo(0, y);
					}else {
						//if current subscript line is out of scrollview's scope
						if ((y < scrollA || z >= scrollB) && !isDraggingEffecting) {//&& !isDragging)
							s.smoothScrollTo(0, y);
							s.smoothScrollTo(0, y);//guess why a duplication,it's very android:)
						}
					}
				}
			});
			//![0]post
		}
		//intervalHeaderMarker
		RBTNode<myCpr<Integer,Integer>> intervalHeaderMarker = tree.xxing(new myCpr(currenTime, 0));
		if(intervalHeaderMarker!=null){
			int OldLine = intervalHeaderMarker_line;
			intervalHeaderMarker_line = intervalHeaderMarker.getKey().value;
			if(intervalHeaderMarker_line!=OldLine){
				View v = scrollLinearLayoutManager.getChildAt(OldLine-scrollLinearLayoutManager.findFirstVisibleItemPosition());
				if(v!=null)
					v.findViewById(R.id.item_bg_root).setBackground(null);
				v = scrollLinearLayoutManager.getChildAt(intervalHeaderMarker_line-scrollLinearLayoutManager.findFirstVisibleItemPosition());
				if(v!=null)
					v.findViewById(R.id.item_bg_root).setBackground(getResources().getDrawable(R.drawable.shixian));
			}
		}
	}

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


	public void prepareMedia(String path) {
		if(path!=null)
			doc_Mp3=path;
		else
			path=doc_Mp3;
		if(mMediaPlayer!=null) {
			pause();
			mMediaPlayer.stop();
			mMediaPlayer.release();
		}
		if(path==null){
			mMediaPlayer = new MediaPlayer();
			mSeekBar.setProgress(0);
			AdaptermySeekSet.setProgress(0);
			return;
		}
		doc_mp3_tv.setText(doc_Mp3);
		mMediaPlayer = new MediaPlayer();
		try {
			mMediaPlayer.setDataSource(path);
			mMediaPlayer.prepare();
			isMediaPrepared = true;
		} catch (Exception e) {
			isMediaPrepared = false;
			e.printStackTrace();
			//CMN.showTT("prepareMedia ERROR:" + e.getLocalizedMessage());
			//return;
		}
		int lengthTmp=0;
		if(isMediaPrepared) {
			lengthTmp = mMediaPlayer.getDuration();
			//CMN.showTT("已准备！");
		}else{
			CMN.sh("Editor:未找到Mp3文件！");
			if(tree.maximum()!=null) {
				lengthTmp = tree.maximum().key;
				CMN.sh("正在使用歌词文件定义的时长");
			}
		}
		mSeekBar.setMax(lengthTmp);
		seekSetTimeSplitNumber = lengthTmp/(60*1000);
		seekSetSpareTime = lengthTmp - seekSetTimeSplitNumber*(60*1000);
		if(seekSetSpareTime!=0) ++seekSetTimeSplitNumber;
		seekSetActiveIdx = 0;
		AdaptermySeekSet.notifyDataSetChanged();
		mSeekBar.setProgress(0);
		AdaptermySeekSet.setProgress(0);
		totalT.setText(CMN.FormTime(mMediaPlayer.getDuration(), 2));

	}

	//将path歌词文件扫入,自动初始化。
	//通路：从提取界面进入编辑界面
	//扫入main、sub_lv
	//
	public void prepare_lyric_assign(String path) {
		if(path!=null)
			doc_Lrc=path;
		else
			path=doc_Lrc;
		if(path==null){
			mainLyricAdapterData.clear();
			subLyricAdapterData.clear();
			return;
		}
		doc_lrc_tv.setText(doc_Lrc);
		mainLyricAdapterData.clear();
		subLyricAdapterData.clear();
		mainLyricAdapterData= new ArrayList<String>();
		subLyricAdapterData = new ArrayList<String>();
		try {
			String str = new String(CMN.readParse(path));
			if (str.startsWith("{")) {//JSON
				JSONArray jsonArray = new JSONArray("[" + str + "]");
				JSONObject ducoj = jsonArray.getJSONObject(0);
				String lyrics = ducoj.optString("lyric");
				String translyrics = ducoj.optString("translateLyric");
				HashMap<Integer, String> lst1_texts = new HashMap<>();
				final HashMap<Integer, String> lst2_texts = new HashMap<Integer, String>();
				if ((lyrics != null) && lyrics.length() > 0) {
					if (lyrics.startsWith("[")) //JSON with time 类似于提取歌词的处理
					{
						String[] lst1 = lyrics.split("\\n");
						int lnCounter = 0;
						tree.clear();
						tree2.clear();
						for (String i : lst1) {//处理主歌词
							boolean isLyric = true;
							int offa = i.indexOf("[");
							int offb = i.indexOf("]", offa);
							if (offa == -1 || offb == -1) {
								continue;
							}
							int oldValidOffb = offb;
							ArrayList<Integer> times = new ArrayList<Integer>();

							while(true) {  //遍历所有tags
								if (offa == -1 || offb == -1) {
									break;
								}
								oldValidOffb = offb;
								String boli = i.substring(offa + 1, offb);
								String[] tmp = boli.split("[: .]");
								//格式检查
								int time = -1;
								if (tmp.length == 3)//00:00.000
								{
									if (tmp[2].length() == 2)
										tmp[2] = tmp[2] + "0";

									try{
										time = Integer.valueOf(tmp[0]) * 60000 + Integer.valueOf(tmp[1]) * 1000 + Integer.valueOf(tmp[2]);//时间码
									}catch (NumberFormatException e){time = -1;}
								}
								else if (tmp.length == 2)//00:00
								{
									try{
										time = Integer.valueOf(tmp[0]) * 60000 + Integer.valueOf(tmp[1]) * 1000;//时间码
									}catch (NumberFormatException e){time = -1;}
								}
								if(time!=-1)
									times.add(time);

								offa = i.indexOf("[", offb);
								offb = i.indexOf("]", offa);
							}


							for(int time:times){
								String text = (oldValidOffb + 1) < i.length() ? i.substring(oldValidOffb + 1) : "";//主歌词
								//if (!"".equals(text)) {//跳过冗余
								mainLyricAdapterData.add(text);
								myCpr tmpNode = new myCpr<Integer, Integer>(time,lnCounter++);//时间-行
								//lst1_texts.put(time,text);//TODO::好像这个对象可以取代mainLyricAdapterData。。不管了
								tree.insert(tmpNode);
								tree2.insert(tmpNode.Swap());
								//}
							}
						}
						if(ducoj.has("translateLyric") && translyrics.length()>0){//处理翻译歌词
							src_tv.setText(new StringBuilder().append(lyrics).append(translyrics));
							lst1 = translyrics.split("\\n");
							for (String i : lst1) {//处理翻译歌词
								boolean isLyric = true;
								int offa = i.indexOf("[");
								int offb = i.indexOf("]");
								if (offa == -1 || offb == -1) {
									continue;
								}

								String boli = i.substring(offa + 1, offb);
								String[] tmp = boli.split("[: .]");
								//格式检查
								int time = -1;
								if (tmp.length == 3)
								{
									if (tmp[2].length() == 2)
										tmp[2] = tmp[2] + "0";

									try{
										time = Integer.valueOf(tmp[0]) * 60000 + Integer.valueOf(tmp[1]) * 1000 + Integer.valueOf(tmp[2]);//时间码
									}catch (NumberFormatException e){time = -1;}
								}
								if (time!=-1) {
									String text = (offb + 1) < i.length() ? i.substring(offb + 1) : "";//翻译歌词
									lst2_texts.put(time,text);
								}
							}
						}else{
							src_tv.setText(new StringBuilder().append(lyrics));
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
						if(ducoj.has("translateLyric") && translyrics.length()>0){
							src_tv.setText(new StringBuilder().append(lyrics).append(translyrics));
						}else{
							src_tv.setText(new StringBuilder().append(lyrics));
						}
					}
				}
				for(int i=0;i<mainLyricAdapterData.size();i++) {//保证main、sub_lv的行数一致。

					if(lst2_texts.size()>0){//如果该行有时间记载，试图以时间取翻译
						RBTNode<myCpr<Integer, Integer>> searcherNode = tree2.sxing(new myCpr<Integer, Integer>(i, 0));//行数-时间
						if(searcherNode!=null)
							subLyricAdapterData.add(lst2_texts.get(searcherNode.getKey().value));
						else
							subLyricAdapterData.add("");
					}else {
						subLyricAdapterData.add("");
					}
				}
			} else {//普通，逐行散列

			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		//if(mainLyricAdapterData.size()>0)
		//	startCandidate=0;
		//else
		//	startCandidate=-1;
		mainLyricAdapter.notifyDataSetChanged();
		//CMN.showT("mainLyricAdapter");
	}

	public void init_yuan(){
		doc_lrc_tv.setText(doc_Lrc);
		doc_mp3_tv.setText(doc_Mp3);
		try {
			String str = new String(CMN.readParse(doc_Lrc));
			if (str.startsWith("{")) {//不普通
				JSONArray jsonArray = new JSONArray("[" + str + "]");
				JSONObject ducoj = jsonArray.getJSONObject(0);
				String lyrics = ducoj.optString("lyric");
				String translyrics = ducoj.optString("translateLyric");

				if ((lyrics != null) && lyrics.length() > 0) {
					if(ducoj.has("translateLyric") && translyrics.length()>0){
						src_tv.setText(new StringBuilder().append(lyrics).append(translyrics));
					}else{
						src_tv.setText(new StringBuilder().append(lyrics));
					}
				}
			} else {//普通，逐行散列

			}} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		src_title_et.setText(doc_Lrc+"");
	}

	int heightDelta = 0;
	@Override//监听键盘弹出
	public void onResize(int w, int h, int oldw, int oldh) {
		//CMN.showTT((main_lv.getVisibility()==View.VISIBLE)+"");
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
			if(main_lv.getVisibility()!=View.VISIBLE)
				return;
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
			if(s.getVisibility()==View.VISIBLE)
				notifyTimeChanged();
		}
		//int distance = h - old;
		//EventBus.getDefault().post(new InputMethodChangeEvent(distance,mCurrentImageId));
	}

	@Override//toolbar菜单
	public boolean onMenuItemClick(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.save:
				if(CMN.curr_proj_timecode==-1)
					dbCon.dumpNewProj(""+src_title_et.getText(),doc_Mp3, doc_Lrc, mSeekBar.tree, mainLyricAdapterData, subLyricAdapterData);
				else
					dbCon.updateProj(CMN.curr_proj_timecode+"",""+src_title_et.getText(),doc_Mp3,doc_Lrc,mSeekBar.tree, mainLyricAdapterData, subLyricAdapterData);
				break;
			case R.id.open:
				View dialog = CMN.inflater.inflate(R.layout.settings_choosing_dialog, (ViewGroup) CMN.a.findViewById(R.id.dialog));
				ListView lv = (ListView) dialog.findViewById(R.id.lv);
				AlertDialog.Builder builder = new AlertDialog.Builder(CMN.a);
				sonclick = null;
				daProjsAdapter_.refresh();
				lv.setAdapter(daProjsAdapter_);
				builder.setView(dialog);
				builder.setIcon(R.mipmap.ic_directory_parent);
				builder.setNeutralButton("删除",null);
				d = builder.create();
				d.show();
				//d.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
				//d.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
				Window window = d.getWindow();
				WindowManager.LayoutParams params = window.getAttributes();
				params.width =  WindowManager.LayoutParams.MATCH_PARENT;//如果不设置,可能部分机型出现左右有空隙,也就是产生margin的感觉
				params.height = WindowManager.LayoutParams.WRAP_CONTENT;
				//params.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE;//显示dialog的时候,就显示软键盘
				params.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;//就是这个属性导致不能获取焦点,默认的是FLAG_NOT_FOCUSABLE,故名思义不能获取输入焦点,
				params.dimAmount=0.5f;//设置对话框的透明程度背景(非布局的透明度)
				window.setAttributes(params);
				//window.getDecorView().setPadding(0, 0, 0, 0);//使match parent 生效??

				d.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						daProjsAdapter_.showDelete=!daProjsAdapter_.showDelete;
						daProjsAdapter_.notifyDataSetChanged();
					}
				});				
				break;
			case R.id.export:
				try {
					//获取文件名
					int fcount=0;
					String fn = String.valueOf(src_title_et.getText())+".exported_No."+fcount+".lrc";
					while(new File(CMN.opt.save_path + fn).exists()){
						fn = String.valueOf(src_title_et.getText())+".exported_No."+(++fcount)+".lrc";
					}
					File outFile = new File(CMN.opt.save_path + fn);
					sb = new StringBuffer();
					switch (CMN.opt.translationFormatNumber){
						case 0:CMN.opt.co_lyrics_sep = "\r\n";
							break;
						case 1:CMN.opt.co_lyrics_sep = " ";
							break;
					}
					tree.setInOrderDo(new RBTree.inOrderDo() {
						@Override
						public void dothis(RBTNode node) {
							myCpr<Integer, Integer> cprTmp = (myCpr<Integer, Integer>) node.getKey();
							int dataIdx= cprTmp.value;
							int time = cprTmp.key;
							sb.append("[")
								.append(CMN.FormTime(time,CMN.opt.timeFormatNumber))
								.append("]")
								.append(mainLyricAdapterData.get(dataIdx))
								;
							String tmp = subLyricAdapterData.get(dataIdx);
							if(!tmp.equals(""))
								sb.append(CMN.opt.co_lyrics_sep)
								  .append(tmp).append("\r\n");
							else
								sb.append("\r\n");

						}
					});
					tree.inOrderDo();



					//最终写入

					outFile.createNewFile();
					OutputStreamWriter oufi = new OutputStreamWriter(new FileOutputStream(CMN.opt.save_path + fn), CMN.charsetNames[CMN.opt.charSetNumber]);
					oufi.write(sb.toString());
					sb.setLength(0);
					sb=null;
					//TODO 复用测试
					oufi.close();
					CMN.sh("已导出: "+fn);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
		}

		return false;
	}

	View view_clicked;
	@Override//点击事件
	public void onClick(View v) {
		view_clicked=v;
		switch (v.getId()) {//text
			case R.id.play:
				if (!mMediaPlayer.isPlaying()) {//播放
					play();
					isPausedExpected = false;
				} else {
					pause();
					isPausedExpected = true;
				}
				break;
			case R.id.browser_widget3://corlet
				boolean needUpdateLastAttatch=false;
				if(lastAttach !=null && startCandidate== lastAttach.getKey().value)
					needUpdateLastAttatch = true;
				int timeNow;
				if(isMediaPrepared)
					timeNow = mMediaPlayer.getCurrentPosition();
				else
					timeNow = mSeekBar.getProgress();
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
				if(CMN.opt.add_agjust_coupled || needUpdateLastAttatch) {
					lastAttach = tree.insertUpdate(timeLineSearcherlet);
					top_adjustBar.setActiveColor(colorSheet[lastAttach.getKey().value%colorSheet.length]);//根据行数取颜色
					show_adjustBar_toggle();
				}else
					tree.insertUpdate(timeLineSearcherlet);
				AdaptermySeekSet.notifyDataSetChanged();
				mainLyricAdapter.selectedPositions.put(startCandidate, CMN.FormTime(timeNow, 1));

				mSeekBar.invalidate();
				//移动main_lv等
				if(scrollLinearLayoutManager.findLastVisibleItemPosition()<startCandidate ||
						scrollLinearLayoutManager.findFirstVisibleItemPosition()>startCandidate)
					scrollLinearLayoutManager.scrollToPositionWithOffset(startCandidate, 100);
				currenTime=mSeekBar.getProgress();
				notifyTimeChanged();
				if(CMN.a.opt.auto_move_cursor)
					updateStartCandidate(startCandidate + 1);
				else
					updateStartCandidate(startCandidate);
				break;
			case R.id.browser_widget2://zuo
				if (tree.getRoot() == null) break;
				lastAttach = tree.xxing_samsara(new myCpr(mSeekBar.getProgress(), 0));
				currenTime = lastAttach.getKey().key;
				mMediaPlayer.seekTo(currenTime);
				currenT.setText(CMN.FormTime(currenTime, 2));//TODO opt
				mSeekBar.setProgress(currenTime);
				AdaptermySeekSet.setProgress(currenTime);
				top_time.setText(CMN.FormTime(currenTime,0));
				//移动main_lv等
				if(scrollLinearLayoutManager.findLastVisibleItemPosition()< lastAttach.getKey().value ||
						scrollLinearLayoutManager.findFirstVisibleItemPosition()> lastAttach.getKey().value)
					scrollLinearLayoutManager.scrollToPositionWithOffset(lastAttach.getKey().value, 100);

				if(CMN.a.opt.attatch_cursor_coupled){
					updateStartCandidate(lastAttach.getKey().value);
				}
				top_adjustBar.setActiveColor(colorSheet[lastAttach.getKey().value%colorSheet.length]);//根据行数取颜色
				notifyTimeChanged();
				show_adjustBar_toggle();//动画
				top_adjustBar.invalidate();//颜色
				break;
			case R.id.browser_widget4://you
				if (tree.getRoot() == null) break;
				lastAttach = tree.sxing_samsara(new myCpr(mSeekBar.getProgress(), 0));
				currenTime = lastAttach.getKey().key;
				mMediaPlayer.seekTo(currenTime);
				currenT.setText(CMN.FormTime(lastAttach.getKey().key, 2));
				mSeekBar.setProgress(currenTime);
				AdaptermySeekSet.setProgress(currenTime);
				top_time.setText(CMN.FormTime(currenTime,0));
				//移动main_lv等
				if(scrollLinearLayoutManager.findLastVisibleItemPosition()< lastAttach.getKey().value ||
						scrollLinearLayoutManager.findFirstVisibleItemPosition()> lastAttach.getKey().value)
					scrollLinearLayoutManager.scrollToPositionWithOffset(lastAttach.getKey().value, 0);
				if(CMN.a.opt.attatch_cursor_coupled){
					updateStartCandidate(lastAttach.getKey().value);
				}
				top_adjustBar.setActiveColor(colorSheet[lastAttach.getKey().value%colorSheet.length]);
				notifyTimeChanged();
				show_adjustBar_toggle();
				top_adjustBar.invalidate();

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
			case R.id.browser_widget1:
				if(top_adjustLayout.getVisibility()==View.VISIBLE)
					top_adjustLayout.setVisibility(View.INVISIBLE);
				else
					top_adjustLayout.setVisibility(View.VISIBLE);
				break;
			case R.id.browser_widget5:
				if(itemsLayout.getVisibility()==View.VISIBLE)
					itemsLayout.setVisibility(View.INVISIBLE);
				else
					itemsLayout.setVisibility(View.VISIBLE);
				break;
			case R.id.jumpTo:
			case R.id.previewLineIdx:
				//s.setScrollY(s.getScrollY());//停止scrollview滑动 无效
				isDragging=isDraggingEffecting=false;//停止scrollview滑动
				if(prevOffNode==null)
					return;
				//todo:xxx
				mMediaPlayer.seekTo(prevOffNode.getKey().value);
				mSeekBar.setProgress(prevOffNode.getKey().value);
				AdaptermySeekSet.setProgress(prevOffNode.getKey().value);
				if(!mMediaPlayer.isPlaying())
					play();
				break;
			case R.id.items1:
				CMN.opt.viewPagerLocked=!CMN.opt.viewPagerLocked;
				if(CMN.opt.viewPagerLocked){
					CMN.a.viewPager.setNoScroll(true);
				}else{
                    CMN.a.viewPager.setNoScroll(false);
                }
				if(CMN.a!=null && CMN.a.f1!=null)
					CMN.a.f1.viewpager_locker.setChecked(CMN.opt.viewPagerLocked);
				break;
			case R.id.items2:
				CMN.opt.attatch_cursor_coupled=!CMN.opt.attatch_cursor_coupled;
				break;
			case R.id.items3:
				CMN.opt.auto_move_cursor=!CMN.opt.auto_move_cursor;
				break;
			case R.id.items4:
				CMN.opt.add_agjust_coupled=!CMN.opt.add_agjust_coupled;
				break;
			case R.id.toptoggle_follow:
				CMN.opt.adjust_seek_coupled=!CMN.opt.adjust_seek_coupled;
				break;
			case R.id.toptoggle_adjustAll:
				CMN.opt.adjust_all=!CMN.opt.adjust_all;
				break;
			case R.id.toptoggle_loop:
				CMN.opt.isAttABLooping=!CMN.opt.isAttABLooping;
				break;
			case R.id.deletln://删除行
				if(d!=null) {
					d.dismiss();
					d=null;
				}
                dv = CMN.inflater.inflate(R.layout.dialog_double_number_picker_dln,(ViewGroup) CMN.a.findViewById(R.id.dialog));
                dv.findViewById(R.id.confirm).setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                    }
                });
                ((NumberPicker)dv.findViewById(R.id.number1)).setMaxValue(mainLyricAdapterData.size());
                ((NumberPicker)dv.findViewById(R.id.number1)).setValue(startCandidate);
                ((NumberPicker)dv.findViewById(R.id.number2)).setMaxValue(mainLyricAdapterData.size());
                ((NumberPicker)dv.findViewById(R.id.number2)).setValue(startCandidate);
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
				dv.findViewById(R.id.confirm).setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if(mainLyricAdapterData.size()!=0) {
							int start = (((NumberPicker) dv.findViewById(R.id.number1)).getValue() + 0);
							int end = (((NumberPicker) dv.findViewById(R.id.number2)).getValue() + 0);
							int lnCount=0;
							for(int i=start;i<=end;i++) {//删除第i行
								mainLyricAdapterData.remove(i);
								subLyricAdapterData .remove(i);
								++lnCount;
							}
							for(int i=start;i<=end;i++){//删除第i行对应的时间标记
								RBTNode<myCpr<Integer,Integer>> nodeTmp = tree2.sxing(new myCpr(start, 0));
								if(nodeTmp!=null){
									tree2.removeIntrinsic(nodeTmp);
									tree.remove(new myCpr(nodeTmp.getKey().value, 0));
								}
							}
							if(tree.getRoot()!=null && lnCount!=0){//
								RBTNode<myCpr<Integer,Integer>> nodeTmp = tree2.sxing(new myCpr(start, 0));
								while(nodeTmp!=null){
									((myCpr<Integer,Integer>)tree.search(new myCpr(nodeTmp.getKey().value, 0)).getKey()).value-=lnCount;
									nodeTmp.getKey().key-=lnCount;
									nodeTmp=tree2.successor(nodeTmp);
								}
								updateSelectedPositions();
							}
							mainLyricAdapter.notifyDataSetChanged();
						}
						d.dismiss();
						d = null;
					}
				});
                android.view.WindowManager.LayoutParams lp = d.getWindow().getAttributes();  //获取对话框当前的参数值
                lp.height = 500;
                d.getWindow().setAttributes(lp);
				break;
            case R.id.addln://添加行
				if(d!=null) {
					d.dismiss();
					d=null;
				}
                dv = CMN.inflater.inflate(R.layout.dialog_double_number_picker_addln,(ViewGroup) CMN.a.findViewById(R.id.dialog));
                dv.findViewById(R.id.confirm).setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                    }
                });
                ((NumberPicker)dv.findViewById(R.id.number1)).setMaxValue(mainLyricAdapterData.size());
                ((NumberPicker)dv.findViewById(R.id.number1)).setValue(startCandidate);
                ((NumberPicker)dv.findViewById(R.id.number2)).setMaxValue(12);
                ((NumberPicker)dv.findViewById(R.id.number2)).setMinValue(1);
                AlertDialog.Builder builder3 = new AlertDialog.Builder(CMN.a);
                builder3.setView(dv);
                d = builder3.create();
                d.setOnDismissListener(new AlertDialog.OnDismissListener(){
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        dv = null;
                        d = null;
                    }
                });
                d.show();
                dv.findViewById(R.id.confirm).setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						int start = (((NumberPicker)dv.findViewById(R.id.number1)).getValue()+1);
						if(mainLyricAdapterData.size()==0)
							start=0;
						//int start = Math.min((((NumberPicker)dv.findViewById(R.id.number1)).getValue()+1),mainLyricAdapterData.size()-1);
						int lnCount=0;
						for(int i=start;i<start+((NumberPicker)dv.findViewById(R.id.number2)).getValue();i++) {
							mainLyricAdapterData.add(i, "");
							subLyricAdapterData .add(i, "");
							++lnCount;
						}
						mainLyricAdapter.notifyDataSetChanged();
						if(tree.getRoot()!=null && lnCount!=0){//
							RBTNode<myCpr<Integer,Integer>> nodeTmp = tree2.sxing(new myCpr(start, 0));
							while(nodeTmp!=null){
								((myCpr<Integer,Integer>)tree.search(new myCpr(nodeTmp.getKey().value, 0)).getKey()).value+=lnCount;
								nodeTmp.getKey().key+=lnCount;
								nodeTmp=tree2.successor(nodeTmp);
							}
							updateSelectedPositions();
						}
						d.dismiss();
						d=null;
					}
				});
                android.view.WindowManager.LayoutParams lp2 = d.getWindow().getAttributes();  //获取对话框当前的参数值
                lp2.height = 500;
                d.getWindow().setAttributes(lp2);
                break;
		}
	}

	public void show_adjustBar_toggle(){
		adjustBar_toggle.setVisibility(View.VISIBLE);
		//adjustBar_toggle.setColorFilter(Color.parseColor("#0000ff"));
		//TODO::神奇的事，以下对bg drawable的修改作用于全局?
		adjustBar_toggle.getBackground().setColorFilter(new LightingColorFilter(top_adjustBar.getActiveColor(), 0x00000000));
		ScaleAnimation anima = new ScaleAnimation(1.5f,1,1.5f,1, Animation.RELATIVE_TO_SELF, 0.5f);
		anima.setDuration(300);
		anima.setFillAfter(true);
		adjustBar_toggle.startAnimation(anima);
	}
	//
	/*在第startCandidate行处出显示红色方块标记，表示下一个时间点将assign到该行。*/
	private void updateStartCandidate(int position) {
		if (position < 0) {
			return;
		}else if(position >= mainLyricAdapterData.size()-1){
			startCandidate = mainLyricAdapterData.size()-1;
			mainLyricAdapter.notifyItemChanged(startCandidate);
			return;
		}
		if (position == startCandidate){
			mainLyricAdapter.notifyItemChanged(startCandidate);
			return;
		}
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


	/*根据时间重建播放器*/
	public void resumePlayer(int userPauseTime) {
		if (!isMediaPrepared || mediaStarted)
			return;
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
	boolean flipTimer;
	boolean cursorBlinking = true;
	boolean mediaStarted = false;
	public void play() {
		playButton.setBackgroundResource(R.drawable.ic_pause_black_24dp);
		isPausedExpected = false;
		// 每半秒触发一次
		if(!cursorBlinking) {
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
		}
		if(isMediaPrepared) {
			mMediaPlayer.start();
			mediaStarted = true;
		}
	}

	public void pause() {
		mediaStarted=false;
		playButton.setBackgroundResource(R.drawable.ic_play_arrow_black_24dp);
		isPausedExpected = true;
		if(!cursorBlinking) {
			mTimer.cancel();
		}
		//CMN.showTT(isMediaPrepared+"");
		if(isMediaPrepared)
			mMediaPlayer.pause();
		//currenT.setText(CMN.FormTime(mMediaPlayer.getCurrentPosition(),2));
	}

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if(mediaStarted) {
				int position = mMediaPlayer.getCurrentPosition();
				if(CMN.opt.isAttABLooping){
					if(lastAttach!=null){
						RBTNode<myCpr<Integer, Integer>> nodeTmp = tree.successor(lastAttach);
						Integer bTime = nodeTmp!=null?nodeTmp.getKey().key:lastAttach.getKey().key;
						if(position<lastAttach.getKey().key||position>bTime){
							mMediaPlayer.seekTo(lastAttach.getKey().key);
							AdaptermySeekSet.setProgress(lastAttach.getKey().key);
							mSeekBar.setProgress(lastAttach.getKey().key);
							currenTime = lastAttach.getKey().key;
							notifyTimeChanged();
						}
					}
				}
				int duration = mMediaPlayer.getDuration();
				if (duration > 0) {
					mSeekBar.setProgress(position);
					currenT.setText(CMN.FormTime(position, 2));
					AdaptermySeekSet.setProgress(position);
					currenTime = position;
					notifyTimeChanged();
				}
			}

			if(cursorBlinking){
				View v = scrollLinearLayoutManager.getChildAt(startCandidate-scrollLinearLayoutManager.findFirstVisibleItemPosition());
				if(v!=null){
					if(flipTimer)
						v.findViewById(R.id.tv3).setVisibility(View.VISIBLE);
					else
						v.findViewById(R.id.tv3).setVisibility(View.INVISIBLE);
					flipTimer=!flipTimer;
				}
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
				break;
				case 1001:
					tv.setText(sb.toString());

					break;
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
				//sb = new StringBuffer().append("\r\n");
				sb = new StringBuffer().append("\r\n");
				subs_timeNodeTree = new  RBTree<myCpr<Integer,Integer>>();
				subs_timeNodeTree2 = new RBTree<myCpr<Integer,Integer>>();
				subs_timeNodeTree.insert(new myCpr(0, sb.length()));
				subs_timeNodeTree2.insert(new myCpr(sb.length(), 0));
				tree.setInOrderDo(new RBTree.inOrderDo() {
					@Override
					public void dothis(RBTNode node) {
						myCpr<Integer, Integer> cprTmp = (myCpr<Integer, Integer>) node.getKey();
						int dataIdx= cprTmp.value;
						int time = cprTmp.key;
						subs_timeNodeTree.insert(new myCpr(time, sb.length()));
						subs_timeNodeTree2.insert(new myCpr(sb.length(), time));
						sb.append(mainLyricAdapterData.get(dataIdx))
								.append("\r\n");
						String tmp = subLyricAdapterData.get(dataIdx);
						if(tmp==null)
							return;//TODO 有次初次实现editor懒加载时tmp==null崩溃，let checkout why？
						if(!tmp.equals(""))
							sb.append(tmp)
							.append("\r\n\r\n");
						else
							sb.append("\r\n");
					}
				});
				tree.inOrderDo();
				subs_timeNodeTree.insert(new myCpr(999999999, sb.length()));
				subs_timeNodeTree2.insert(new myCpr(sb.length(),999999999));
				prevTimeNode = subs_timeNodeTree.getRoot();
				nxtTimeNode = subs_timeNodeTree.sxing(prevTimeNode.getKey());
				tv.setText(sb.toString());
				notifyTimeChanged();
				Oldtablet.setVisibility(View.VISIBLE);
				break;
			case 1:
				Oldtablet = main_lv;
				Oldtablet.setVisibility(View.VISIBLE);
                mainLyricAdapter.module_set = mainLyricAdapterData;
                mainLyricAdapter.notifyDataSetChanged();
				break;
			case 2:
				Oldtablet = main_lv;
				Oldtablet.setVisibility(View.VISIBLE);
                mainLyricAdapter.module_set = subLyricAdapterData;
                mainLyricAdapter.notifyDataSetChanged();
				break;
			case 3:
				Oldtablet = src_layout;
				Oldtablet.setVisibility(View.VISIBLE);

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
		if (focusedView != null) {
            ((EditText) focusedView).setSingleLine(true);
            //((ViewGroup)((EditText) focusedView).getParent().getParent()).findViewById(R.id.clear).setVisibility(View.INVISIBLE);
        }
		focusedView = v;
		((EditText) focusedView).setSingleLine(false);
        //((ViewGroup)((EditText) focusedView).getParent().getParent()).findViewById(R.id.clear).setVisibility(View.VISIBLE);
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
		public TextView item_bg_root;
		TextView time;
		TextView line;
		TextView indicator;
		TextView indicator2;
		private EditText url;

		public MyViewHolder(View view) {
			super(view);
			url = (EditText) view.findViewById(R.id.et);
			time = (TextView) view.findViewById(R.id.tv1);
			item_bg_root = (TextView) view.findViewById(R.id.item_bg_root);
			line = (TextView) view.findViewById(R.id.tv0);
			indicator = (TextView) view.findViewById(R.id.tv3);
			indicator2 = (TextView) view.findViewById(R.id.tv4);
			//tv.setTextColor(Color.parseColor("#ff000000"));
			//tv.setTextSize(TypedValue.COMPLEX_UNIT_PX,CMN.a.scale(81));//TODO: optimize
		}
	}

	/*for main list - lyrics
	   参见：live pull -> main -> main_list_Adapter
	   参见：P.L.O.D -> float search view -> HomeAdapter*/
	public class main_list_Adapter extends RecyclerView.Adapter<MyViewHolder> {
		//作为recycler view 的数据存储
		public ArrayList<String> module_set;

		//构造
		main_list_Adapter(ArrayList<String> module_set_) {
			module_set = module_set_;
		}

		//protected void update_slots_time(int position, String val) {
		//	if (val != null)
		//		slots_time.put(position, val);
		//	else {
		//		slots_time.remove(position);
		//	}
		//	notifyItemChanged(position);
		//}

		@Override
		public int getItemCount() {
			return module_set.size();
		}

		public HashMap<Integer, String> selectedPositions = new HashMap<Integer, String>();//TODO use tree directly
		//public HashMap<Integer, String> slots_time = new HashMap<Integer, String>();       //TODO use tree directly
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
				holder.item_bg_root.setBackground(getResources().getDrawable(R.drawable.shixian));
			}else{
				holder.item_bg_root.setBackground(null);
			}
			if (selectedPositions.containsKey(position)) {
				holder.itemView.setBackgroundColor(Color.parseColor("#4F7FDF"));//FF4081//4F7FDF//aaa0f0f0
				holder.time.setText(selectedPositions.get(position));
				holder.indicator2.setVisibility(View.VISIBLE);
				holder.indicator2.setBackgroundColor(colorSheet[position%20]);
			} else {
				holder.itemView.setBackgroundColor(Color.parseColor("#00a0f0f0"));//aaa0f0f0
				holder.time.setText("---");
				holder.indicator2.setVisibility(View.INVISIBLE);
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
			holder.url.setOnFocusChangeListener(Main_editor_Fragment.this);
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
						//CMN.showT("onItemClick!!");
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
			}catch (Exception e){
				//CMN.showT(e.getLocalizedMessage());
				//TODO! 这里会报错，当打开工程时。
			}

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

	//参见:settings_frament chooseSettingsAdapter
	interface SubOnItemClickListener{
		void onClick(String strasd);
	}
	SubOnItemClickListener sonclick;
	private final daProjsAdapter daProjsAdapter_ = new daProjsAdapter();
	class daProjsAdapter extends BaseAdapter {
		ArrayList<String> list  = new ArrayList<String>();
		ArrayList<Long> list2 = new ArrayList<Long>();
		boolean showDelete=false;
		long today;
		//View EditingLv=null;
		public daProjsAdapter()
		{
			//refresh();
		}
		public void refresh(){
			today = System.currentTimeMillis();
			list  = new ArrayList<String>();
			list2 = new ArrayList<Long>();
			showDelete=false;
			//list = new ArrayList(java.util.Arrays.asList(CMN.opt.module_sets_Handle.listFiles()));
			dbCon.getAllEntries(list,list2);
		}
		@Override public long getItemId(int pos){return pos;}
		@Override public int getCount(){return list.size();}
		@Override public String getItem(int pos){return list.get(pos);}
		@Override
		public View getView(final int pos, View convertView, ViewGroup parent)
		{
			final View lv = CMN.inflater.inflate(R.layout.list_item3, null);
			final TextView tv=(TextView) lv.findViewById(R.id.text);
			final ImageView remove=(ImageView) lv.findViewById(R.id.remove);
			final ImageView modify=(ImageView) lv.findViewById(R.id.modify);
			modify.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					remove.setVisibility(View.VISIBLE);
					ViewCompat.setBackgroundTintList(remove,new ColorStateList(new int[][]{new int[0]}, new int[]{0xff0000ff}));
					ViewCompat.setBackgroundTintList(modify,new ColorStateList(new int[][]{new int[0]}, new int[]{0xff0000ff}));
					modify.setBackground(CMN.a.getResources().getDrawable(R.drawable.ic_keyboard_return_black_24dp));
					tv.setEnabled(true);
					tv.setText(list.get(pos));
					remove.setOnClickListener(new View.OnClickListener(){
						@Override public void onClick(View v) {
							tv.setText(list.get(pos));
							notifyDataSetChanged();
						}
					});
					v.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							dbCon.renameProj(list2.get(pos)+"",tv.getText()+"");
							refresh();
							daProjsAdapter.this.notifyDataSetChanged();
						}
					});
				}
			});

			StringBuilder sb=new StringBuilder()
					//.append(pos)
					//.append(" |")
					.append((today-list2.get(pos))/(24 * 60 * 60 * 1000))
					.append("天前|")
					.append(list.get(pos));

			tv.setText(sb.toString());

			if(showDelete) {//TODO OPT
				remove.setVisibility(View.VISIBLE);
				remove.setOnClickListener(new View.OnClickListener(){
					@Override public void onClick(View v) {
						dbCon.removeProjectFromProjectsTable(list2.get(pos)+"");
						list.remove(pos);
						list2.remove(pos);
						notifyDataSetChanged();
					}
				});
			}
			else
				remove.setVisibility(View.GONE);

			lv.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					//CMN.showTT("cc!");
						CMN.curr_proj_timecode = list2.get(pos);
						dbCon.setUpEditorByProj(Main_editor_Fragment.this,CMN.curr_proj_timecode +"");
						doc_lrc_tv.setText(doc_Lrc);
						doc_mp3_tv.setText(doc_Mp3);
						try {
							String str = new String(CMN.readParse(doc_Lrc));
							if (str.startsWith("{")) {//不普通
								JSONArray jsonArray = new JSONArray("[" + str + "]");
								JSONObject ducoj = jsonArray.getJSONObject(0);
								String lyrics = ducoj.optString("lyric");
								String translyrics = ducoj.optString("translateLyric");

								if ((lyrics != null) && lyrics.length() > 0) {
									if(ducoj.has("translateLyric") && translyrics.length()>0){
										src_tv.setText(new StringBuilder().append(lyrics).append(translyrics));
									}else{
										src_tv.setText(new StringBuilder().append(lyrics));
									}
								}
							} else {//普通，逐行散列

							}} catch (IOException e) {
							e.printStackTrace();
						} catch (JSONException e) {
							e.printStackTrace();
						}
						CMN.sh("打开成功！");
						prepareMedia(null);
						tabLayout.setScrollPosition(1,0,true);
						tabLayout.getTabAt(1).select();
						onTabSelected(tabLayout.getTabAt(1));//TODO opt
						d.dismiss();
				}
			});
			return lv;
		}
	}
}
