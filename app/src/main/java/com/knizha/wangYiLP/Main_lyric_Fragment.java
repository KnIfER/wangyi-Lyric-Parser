package com.knizha.wangYiLP;


import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.support.design.widget.TabLayout;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import com.fenwjian.sdcardutil.RBTNode;
import com.fenwjian.sdcardutil.RBTree;
import com.fenwjian.sdcardutil.SeekBarmy;
import com.fenwjian.sdcardutil.SplitSeekBarmy;
import com.fenwjian.sdcardutil.myCpr;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import db.DBWangYiLPController;

public class Main_lyric_Fragment extends Fragment
	implements View.OnClickListener,RelativeLayoutmy.OnResizeListener,
		Toolbar.OnMenuItemClickListener,
		View.OnFocusChangeListener {
	int startCandidate;
	String currentProjID;
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
	@Override
	public View onCreateView(LayoutInflater inflater, final ViewGroup container,
							 Bundle savedInstanceState) {
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
		tree = new RBTree<myCpr<Integer, Integer>>();
		tree2 = new RBTree<myCpr<Integer, Integer>>();
		tree.clear();
		tree2.clear();
		//
		mSeekBar = (SeekBarmy) main_edit_layout.findViewById(R.id.progress);
		sv = (ScrollView) main_edit_layout.findViewById(R.id.sv);
		totalT = (TextView) main_edit_layout.findViewById(R.id.totalT);
		currenT = (TextView) main_edit_layout.findViewById(R.id.currentT);
		main_lv = (RecyclerView) main_edit_layout.findViewById(R.id.main_lyric_list);
		seekScroll = (ScrollView) main_edit_layout.findViewById(R.id.seekScroll);
		lv_seekbars = (ListView) main_edit_layout.findViewById(R.id.lv_seekbars);

		main_edit_layout.findViewById(R.id.play).setOnClickListener(this);
		main_edit_layout.findViewById(R.id.splitTime).setOnClickListener(this);
		main_edit_layout.findViewById(R.id.browser_widget2).setOnClickListener(this);
		main_edit_layout.findViewById(R.id.browser_widget3).setOnClickListener(this);
		main_edit_layout.findViewById(R.id.browser_widget4).setOnClickListener(this);


		mSeekBar.ini();
		mSeekBar.tree = tree;

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

		mSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				if (fromUser) {
					mMediaPlayer.seekTo(progress);
					currenT.setText(CMN.FormTime(progress, 2));
					AdaptermySeekSet.setProgress(progress);
				}
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

			}
		});
		return main_edit_layout;
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
					if (lyrics.startsWith("[")) //不不普通
					{
						//int lnIndex = lyrics.indexOf("\r\n");
						//while(lnIndex!=-1){
						//	mainLyricAdapterData.add()
						//	lnIndex = lyrics.indexOf("\r\n");
						//}
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

	@Override//监听键盘弹出
	public void onResize(int w, int h, int oldw, int oldh) {
		//如果第一次初始化
		if (oldh == 0) {
			return;
		}
		//如果用户横竖屏转换
		if (w != oldw) {
			return;
		}
		if (h < oldh) {
			//输入法弹出
			CMN.showT("输入法弹出");
			scrollLinearLayoutManager.scrollToPositionWithOffset((Integer) focusedView.getTag(R.id.position), 0);
			//main_lv.scrollToPosition((Integer) focusedView.getTag(R.id.position));
		} else if (h > oldh) {
			//输入法关闭
			CMN.showT("输入法关闭");

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
				int timeNow = mMediaPlayer.getCurrentPosition();
				//CMN.showT("add"+mSeekBar.getProgress()+":"+startCandidate);
				myCpr timeLineSearcherlet = new myCpr(startCandidate, timeNow);//行数-时间
				RBTNode<myCpr<Integer, Integer>> timeLineSearchedNode = tree2.search(timeLineSearcherlet);
				//TODO::优化search
				//根据行数搜时间
				if (timeLineSearchedNode != null) {//previously an another ms-time was assigned to the line at "startCandidate",thus we shall invalidate that assignment.
					myCpr timeLineSearchRes = timeLineSearchedNode.getKey();
					//CMN.showT("del:"+timeLineSearchRes.key);
					tree.remove(timeLineSearchRes.Swap());//时间-行数
				}
				tree2.insertUpdate(timeLineSearcherlet);
				//根据时间搜行数
				timeLineSearcherlet = new myCpr(timeNow, startCandidate);
				timeLineSearchedNode = tree.search(timeLineSearcherlet);
				if (timeLineSearchedNode != null) {//previously an another line possesses this ms-time,thus we shall invalidate that assignment.
					int posTmp = timeLineSearchedNode.getKey().value;
					CMN.showT("found!!!" + posTmp);
					mainLyricAdapter.selectedPositions.remove(posTmp);
					tree2.remove(timeLineSearchedNode.getKey().Swap());
					mainLyricAdapter.notifyItemChanged(posTmp);
				}
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
					mSeekBar.setProgress(lastAtach.getKey().key);
					AdaptermySeekSet.setProgress(lastAtach.getKey().key);
					scrollLinearLayoutManager.scrollToPositionWithOffset(lastAtach.getKey().value, 0);
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
					scrollLinearLayoutManager.scrollToPositionWithOffset(lastAtach.getKey().value, 0);
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
		}
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
		tree2.SetInOrderDo(new RBTree.inOrderDo() {
			@Override
			public void dothis(RBTNode node) {
				myCpr<Integer, Integer> tmp = (myCpr<Integer, Integer>) node.getKey();
				mainLyricAdapter.selectedPositions.put(tmp.key, CMN.FormTime(tmp.value, 0));
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
		}, 0, 1000);
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
			}
		}

		;
	};
	//![0]

	//
	//![1]用于:列表点击时展开，否则collapse
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
			if (selectedPositions.containsKey(position)) {
				holder.itemView.setBackgroundColor(Color.parseColor("#4F7FDF"));//FF4081
				holder.time.setText(selectedPositions.get(position));
				holder.indicator.setVisibility(View.VISIBLE);
				if (startCandidate != position)
					holder.indicator.setBackgroundColor(Color.parseColor("#0000ff"));
				else
					holder.indicator.setBackgroundColor(Color.parseColor("#ff0000"));
			} else {
				holder.itemView.setBackgroundColor(Color.parseColor("#00a0f0f0"));//aaa0f0f0
				holder.time.setText("---");
				holder.indicator.setBackgroundColor(Color.parseColor("#ff0000"));
			}
			holder.line.setText(position + "");
			holder.url.setTag(R.id.position, position);
			holder.url.clearFocus();
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
			if (mTouchItemPosition == position) {
				holder.url.requestFocus();
				//holder.url.setSelection(holder.url.getText().length());
			} else {
				holder.url.clearFocus();
				holder.url.setSingleLine();
			}

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
					mSeekBar.setProgress(progress+tagTmp*60*1000);
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
				vh.seeklet.setMax(60*1000);
				vh.seeklet.setOnSeekBarChangeListener(this);
				convertView.setTag(vh);
			}
			return convertView;
		}
	};
	//![3]


}
