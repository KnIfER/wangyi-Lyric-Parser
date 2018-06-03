package com.knizha.wangYiLP;

import android.Manifest;
import android.app.AlertDialog;
import android.support.v4.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.fenwjian.sdcardutil.NoScrollViewPager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends FragmentActivity
{
	List<Fragment> fragments;
	Main_extractor_Fragment f1;
	public Main_editor_Fragment f2;
	protected NoScrollViewPager viewPager;  //对应的viewPager

    static String DefaultLoadPath="/sdcard/netease/cloudmusic/Cache/Lyric/";
    //static String DefaultLoadPath="/sdcard/netease/cloudmusic/Download/Lyric/";
    static String DefaultSavePath = "/sdcard/netease/cloudmusic/Music/";
	// 要申请的权限
	private String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
	private AlertDialog dialog;

	public LayoutInflater inflater;

	public LP_Option opt;






	//RelativeLayout main;
	private int userPauseTime = 0;
	private long exitTime = 0;

	//抽屉
	DrawerLayout mDrawerLayout;
	private RelativeLayout mDrawerRelative;
	private ListView mDrawerList;

	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	private String[] mPlanetTitles;
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
			int count = f1.main.getChildCount();
			if(count<=5||viewPager.getCurrentItem()==1){
				if((System.currentTimeMillis()-exitTime) > 2000){
					showT("再按一次退出!");
					exitTime = System.currentTimeMillis();
				} else {
                    dumpSettings(MODE_PRIVATE);
                    //onDestroy();
					finish();
					System.exit(0);
					//android.os.Process.killProcess(android.os.Process.myPid());
				}
			}else{
				//option_layout = main.getChildAt(3);
				//if(count==4) main.removeViewAt(3);
				if(f1.lyric_layout!=null && ViewCompat.isAttachedToWindow(f1.lyric_layout)){
					f1.main.removeViewAt(3);
				}else {
					getFragmentManager().beginTransaction()
					.detach(settingsF).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
					.commit();
				}
				f1.topBlurView.updateBlur();
				f1.bottomBlurView.setVisibility(View.VISIBLE);
				///mDrawerLayout.setEnabled(false);
				mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		super.onCreate(savedInstanceState);
		//disable keyboard smashing layout
		//getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING|WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);
		opt=new LP_Option();
		CMN.opt = opt;
		CMN.a = this;
		CMN.dm = getResources().getDisplayMetrics();
		viewPager = (NoScrollViewPager) findViewById(R.id.viewpager);
		fragments=new ArrayList<Fragment>();
		fragments.add(f1 = new Main_extractor_Fragment());

		FragAdapter adapterf = new FragAdapter(getSupportFragmentManager(), fragments);
		viewPager.setAdapter(adapterf);
		viewPager.setCurrentItem(0);
		//android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
		//setSupportActionBar(toolbar);
		// 版本判断。当手机系统大于 23 时，才有必要去判断权限是否获取
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			// 检查该权限是否已经获取
			int i = checkSelfPermission(permissions[0]);
			// 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
			if (i != PackageManager.PERMISSION_GRANTED) {
				// 如果没有授予该权限，就去提示用户请求
				showDialogTipUserRequestPermission();
			}
		}

		try {
			opt.dataDir = getPackageManager().getApplicationInfo(CMN.a.getPackageName(), 0).dataDir;
		} catch (PackageManager.NameNotFoundException e) {
		}
		opt.handler=new Handler(){
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
					case 1:
						f1.adaptermymymymy.notifyDataSetChanged();
					break;

					case R.id.refresh_main_list:
						f1.adaptermymymymy.clear();
						//adaptermymymymy=new ResultListAdapter();
						//CMN.lvAdapter = adaptermymymymy;
						//lv.setAdapter(adaptermymymymy);
						break;
				}}};
		inflater=LayoutInflater.from(getApplicationContext());
		CMN.inflater = inflater;

		//opt.ctx=getApplicationContext();
        scanSettings(MODE_PRIVATE);
		establishHisHanditory();
		if(opt.viewPagerLocked) {
			viewPager.setNoScroll(true);
		}else{
			initF2();
		}
		if(opt.isInEditor){
			initF2();
			viewPager.setCurrentItem(1);
		}


		final Drawable windowBackground = getWindow().getDecorView().getBackground();
		//抽屉
		mPlanetTitles = getResources().getStringArray(R.array.planets_array);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);
		mDrawerRelative= (RelativeLayout) findViewById(R.id.mDrawerRelative);
		mDrawerRelative.setOnClickListener(new OnClickListener() {
			@Override public void onClick(View v) {
				mDrawerLayout.closeDrawer(mDrawerRelative);
		}});
		//mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
		mDrawerLayout.setScrimColor(0x00ffffff);
		mDrawerList.setAdapter(new ArrayAdapter<String>(this,R.layout.drawer_list_item, mPlanetTitles));
		//抽屉监听类
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

		mDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener(){
			int prevFrame=-1;
			@Override
			public void onDrawerStateChanged(int arg0) {
				Log.i("drawer", "drawer的状态：" + arg0);
			}

			@Override
			public void onDrawerSlide(View arg0, float arg1) {

			}

			@Override
			public void onDrawerOpened(View arg0) {
				invalidateOptionsMenu();
			}

			@Override
			public void onDrawerClosed(View arg0) {
				invalidateOptionsMenu();
			}
		});


	}
	///[OnCreate END]


	@Override
	protected void onPause(){
		super.onPause();
		if(f2!=null && f2.isMediaPrepared) {
			userPauseTime = f2.mMediaPlayer.getCurrentPosition();
			f2.mMediaPlayer.pause();
		}
		//CMN.showTT("onPause");
		CMN.a=null;
	}
	@Override
	protected void onResume(){
		super.onResume();
		CMN.a=this;
		if(f2!=null && f2.isAdded())
			f2.resumePlayer(userPauseTime);
		//CMN.showTT("onResume");
	}

	public void initF2() {
		if(f2==null) {
			fragments.add(f2 = new Main_editor_Fragment());
			viewPager.getAdapter().notifyDataSetChanged();
		}
		//![0]	当使用intent调用本程序
		Intent intent = getIntent();
		Uri data = intent.getData();
		if (intent.getType()!=null&&(intent.getType().indexOf("audio/") != -1)) {
			f2.prepareMedia(data.getPath());
			f2.try_prepare_lyrics_for_media(data.getPath());
			viewPager.setCurrentItem(1);
		}
	}


	//抽屉监听类
	private class DrawerItemClickListener implements ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		mDrawerList.setItemChecked(position, true);
		//按条目分配任务
		switch(position){
			//第一条目:全选
			case 0:
				f1.selectAll();
				break;
			//第二条目:全不选
			case 1:
				f1.deSelectAll();
				break;
			//第三条目:反选
			case 2:
				f1.reversionSelect();
				break;
			//第四条目:间选
			case 3:
				break;
		}
		mDrawerLayout.closeDrawer(mDrawerRelative);
		}
	}

	// 提示用户该请求权限的弹出框
	private void showDialogTipUserRequestPermission() {

		new AlertDialog.Builder(this)
				.setTitle("存储权限不可用")
				.setMessage("歌词助手需要获取存储空间；\n否则，您将无法正常使用歌词助手")
				.setPositiveButton("立即开启", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						startRequestPermission();
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				}).setCancelable(false).show();
	}

	// 开始提交请求权限
	private void startRequestPermission() {
		requestPermissions(permissions, 321);
	}
    
	//ActivityResult回调
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 123) {
			if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
				int i = checkSelfPermission(permissions[0]);
				if (i != PackageManager.PERMISSION_GRANTED) {
					//动态申请不成功，转为手动开启权限
					showDialogTipUserGoToAppSettting();
				} else {
					if (dialog != null && dialog.isShowing()) {
						dialog.dismiss();
					}
					Toast.makeText(this, "权限获取成功", Toast.LENGTH_SHORT).show();
				}
			}
		}
	}

	//权限申请回调
	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);

		if (requestCode == 321) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
				if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
					// 判断用户是否 点击了不再提醒。(检测该权限是否还可以申请)
					boolean b = shouldShowRequestPermissionRationale(permissions[0]);
					if (!b) {
						// 用户还是想用我的 APP 的
						// 提示用户去应用设置界面手动开启权限
						showDialogTipUserGoToAppSettting();
					} else
						finish();
				} else {
					Toast.makeText(this, "权限获取成功", Toast.LENGTH_SHORT).show();
				}
			}
		}
	}

	// 提示用户 去设置界面 手动开启权限
	private void showDialogTipUserGoToAppSettting() {
		dialog = new AlertDialog.Builder(this)
				.setTitle("存储权限不可用")
				.setMessage("请在-应用设置-权限-中，允许歌词助手使用存储权限来保存用户数据")
				.setPositiveButton("立即开启", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// 跳转到应用设置界面
						goToAppSetting();
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				}).setCancelable(false).show();
	}

	// 跳转设置界面
	private void goToAppSetting() {
		Intent intent = new Intent();

		intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
		Uri uri = Uri.fromParts("package", getPackageName(), null);
		intent.setData(uri);

		startActivityForResult(intent, 123);
	}

    @Override
    protected void onDestroy() {
    	dumpSettings(MODE_PRIVATE);
        super.onDestroy();
    }
	void resetSettings(){
		getSharedPreferences("lock", MODE_MULTI_PROCESS)
			.edit().clear().commit();
	}
    void scanSettings(int mode){
        //文件网络
		opt.hisHandle = new File(this.getExternalFilesDir(null).getAbsolutePath()+"/History/");
		opt.module_sets_Handle = new File(this.getExternalFilesDir(null).getAbsolutePath()+"/settings/");
		opt.favourite_dirs = new File(this.getExternalFilesDir(null).getAbsolutePath()+"/favorites/");
		SharedPreferences read = getSharedPreferences("lock", mode);
		opt.load_path = read.getString("load_path",DefaultLoadPath);
		opt.save_path = read.getString("save_path",DefaultSavePath);
        opt.Overwrite      =   read.getBoolean("Overwrite", false);
        opt.SelectAll      =   read.getBoolean("SelectAll", true);
        opt.ForbidGetTagFormNet      =   read.getBoolean("ForbidGetTagFormNet", false);
        opt.PreviewRawJsonLyric      =   read.getBoolean("PreviewRawJsonLyric", false);
        opt.addisSongLyricHeader     =   read.getBoolean("addisSongLyricHeader", false);
        opt.addOtherNoneLyricInfos   =   read.getBoolean("addOtherNoneLyricInfos", true);
        opt.attatch_cursor_coupled   =   read.getBoolean("attatch_cursor_coupled", true);
        opt.add_agjust_coupled   =   read.getBoolean("add_agjust_coupled", false);//follow
        opt.adjust_all   =   read.getBoolean("adjust_all", false);
        opt.confinedAdjust   =   read.getBoolean("confinedAdjust", false);
        opt.isAttABLooping   =   read.getBoolean("isAttABLooping", false);
        opt.viewPagerLocked   =   read.getBoolean("viewPagerLocked", false);
        opt.auto_move_cursor   =   read.getBoolean("auto_move_cursor", false);
        opt.isInEditor   =   read.getBoolean("isInEditor", false);
        opt.coupledCut   =   read.getBoolean("coupledCut", true);
        opt.editor_collapse_item_when_NotFocused   =   read.getBoolean("editor_collapse_item_when_NotFocused", false);
        opt.multiThreadNumber        =   read.getInt("multiThreadNumber", 5);
        //opt.cutterMode        =   read.getInt("cutterMode", 0);
		opt.charSetNumber			 =   read.getInt("charSetNumber", 0);
		opt.translationFormatNumber			 =   read.getInt("translationFormatNumber", 0);
		opt.timeFormatNumber			 =   read.getInt("timeFormatNumber", 0);
	}
	void dumpSettings(int mode){
		SharedPreferences.Editor editor = getSharedPreferences("lock",mode).edit();
        //文件网络
        editor.putString("load_path",opt.load_path);
        editor.putString("save_path",opt.save_path);
        editor.putBoolean("Overwrite"		   ,       opt.Overwrite   );
        editor.putBoolean("SelectAll"		   ,       opt.SelectAll   );
        editor.putBoolean("ForbidGetTagFormNet",       opt.ForbidGetTagFormNet   );
        editor.putBoolean("PreviewRawJsonLyric",       opt.PreviewRawJsonLyric   );
        editor.putBoolean("addisSongLyricHeader",      opt.addisSongLyricHeader  );
        editor.putBoolean("addOtherNoneLyricInfos",    opt.addOtherNoneLyricInfos);
        editor.putBoolean("editor_collapse_item_when_NotFocused",    opt.editor_collapse_item_when_NotFocused);
        editor.putBoolean("viewPagerLocked",           opt.viewPagerLocked);
        editor.putBoolean("auto_move_cursor",          opt.auto_move_cursor);
        editor.putBoolean("isInEditor",          viewPager.getCurrentItem()==1);
        editor.putBoolean("add_agjust_coupled",          opt.add_agjust_coupled);//follow
        editor.putBoolean("adjust_all",          opt.adjust_all);
        editor.putBoolean("confinedAdjust",          opt.confinedAdjust);
        editor.putBoolean("isAttABLooping",          opt.isAttABLooping);
        editor.putBoolean("attatch_cursor_coupled",    opt.attatch_cursor_coupled);
        editor.putBoolean("coupledCut",    opt.coupledCut);
        editor.putInt("multiThreadNumber",             opt.multiThreadNumber);
        editor.putInt("charSetNumber",                 opt.charSetNumber);
        editor.putInt("translationFormatNumber",       opt.translationFormatNumber);
        editor.putInt("timeFormatNumber",              opt.timeFormatNumber);
		editor.commit();
	}
 
  
	
	 private void establishHisHanditory(){
	            File curr_HisHanditory=opt.hisHandle;
	            if(curr_HisHanditory.exists()){
	                if(!curr_HisHanditory.isDirectory()) {
	                    curr_HisHanditory.delete();
	                    curr_HisHanditory.mkdirs();
	                }
	            }else{
	                curr_HisHanditory.mkdirs();
	            }
			 curr_HisHanditory=opt.module_sets_Handle;
			 if(curr_HisHanditory.exists()){
				 if(!curr_HisHanditory.isDirectory()) {
					 curr_HisHanditory.delete();
					 curr_HisHanditory.mkdirs();
				 }
			 }else{
				 curr_HisHanditory.mkdirs();
			 }
			 curr_HisHanditory=opt.favourite_dirs;
			 if(curr_HisHanditory.exists()){
				 if(!curr_HisHanditory.isDirectory()) {
					 curr_HisHanditory.delete();
					 curr_HisHanditory.mkdirs();
				 }
			 }else{
				 curr_HisHanditory.mkdirs();
			 }
	    }

	Settings_fragment settingsF;
	//配置界面
	public void OnOptionButtonClick(View viewer){
		//viewer.clearFocus();
		viewer.onTouchEvent((MotionEvent.obtain(0, 0, MotionEvent.ACTION_UP,0,0, 0)));
		f1.bottomBlurView.setVisibility(View.INVISIBLE);
		CMN.fragmentTransaction =getFragmentManager().beginTransaction();
		if(settingsF==null) {//第一次实例化
			settingsF = new Settings_fragment();
			CMN.fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.main_layout, settingsF);
		}else{
			CMN.fragmentTransaction.attach(settingsF);
		}
		CMN.fragmentTransaction.commit();
		//mDrawerLayout.setEnabled(false);
		mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
	}


	
	class LPExOnItemSelectedListener2 implements OnItemSelectedListener{
		public LP_Option lp_option;
		public LPExOnItemSelectedListener2(LP_Option op)
		{
			lp_option=op;
		}

		@Override
		public void onItemSelected(AdapterView<?> p1, View p2, int p3, long p4)
		{
			
			
		}

		@Override
		public void onNothingSelected(AdapterView<?> p1)
		{

		}

	}

	
	class LPExOnItemSelectedListener implements OnItemSelectedListener{
		public LP_Option lp_option;
		public Spinner spn;
		public LPExOnItemSelectedListener(LP_Option op,Spinner _spn)
		{
			lp_option=op;
			spn=_spn;
		}

		@Override
		public void onItemSelected(AdapterView<?> p1, View p2, int p3, long p4)
		{

			
		}

		@Override
		public void onNothingSelected(AdapterView<?> p1)
		{

		}

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

	public static void showT(String text)
	{
		if(m_currentToast != null)
			m_currentToast.cancel();
		m_currentToast = Toast.makeText(CMN.a, text, Toast.LENGTH_SHORT);
		m_currentToast.show();
	}static Toast m_currentToast;

}
///[Activity END]





