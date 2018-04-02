package com.mycompany.myapp2;

import android.app.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import android.view.View.*;
import java.io.*;
import java.util.*;
import android.content.*;
import android.widget.AdapterView.*;
import android.opengl.*;
import android.graphics.*;
import java.util.zip.*;

import org.json.JSONArray;
import org.json.JSONObject;


import android.location.*;
import android.text.method.*;
import com.fenwjian.sdcardutil.*;
import com.github.angads25.filepicker.model.*;
import com.github.angads25.filepicker.view.*;
import com.github.angads25.filepicker.controller.*;
import android.widget.CompoundButton.*;
import java.nio.*;
import android.text.*;
import android.text.style.*;
import eightbitlab.com.blurview.*;
import android.graphics.drawable.*;
import android.animation.*;
import android.transition.*;

public class MainActivity extends Activity
{


	Button run_button;
	LayoutInflater inflater;
	LP_Option opt;
	RelativeLayout main;
	private long exitTime = 0;
	BlurView.ControllerSettings topViewSettings;
	BlurView topBlurView;
	BlurView bottomBlurView;
	ListView lv;
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){  
			int count = main.getChildCount();
			if(count<=3){
				if((System.currentTimeMillis()-exitTime) > 2000){  
					Toast.makeText(getApplicationContext(), "有种再按一次！", Toast.LENGTH_SHORT).show();                                
					exitTime = System.currentTimeMillis();   
				} else {
					finish();
					System.exit(0);
				}
			}else{
				if(count==4) main.removeViewAt(3);
				//else main.removeViews(3,count-1);
				bottomBlurView.setVisibility(View.VISIBLE);
				topBlurView.updateBlur();
			}
			return true;   
		}
		return super.onKeyDown(keyCode, event);
	}
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);
		main=(RelativeLayout)findViewById(R.id.main_layout);
		opt=new LP_Option();
		
		opt.handler=new Handler();

		inflater=LayoutInflater.from(getApplicationContext());
	
		ItemOnClickListener.inflater=inflater;
		ItemOnClickListener.main_layout=(RelativeLayout)findViewById(R.id.main_layout);
		ResultListAdapter adaptet=new ResultListAdapter(getApplicationContext());

		lv=(ListView)findViewById(R.id.main_list);
		lv.setAdapter(adaptet);

		run_button=(Button)findViewById((R.id.run_button));
		Button extractButton = (Button)findViewById(R.id.extractor);
		
		opt.ctx=getApplicationContext();
		opt.controller_button=run_button;
		opt.load_path="/sdcard/netease/cloudmusic/Cache/Lyric/";
		opt.save_path="/sdcard/netease/cloudmusic/Music/";
		opt.opt.ForceGetTagFormNet=false;
		opt.opt.ForceGetLrcFromNet=false;
		opt.opt.NomalTag=true;
		opt.opt.ExtraTag=true;

	    

		run_button.setOnClickListener(new Run_OnClickListener((RelativeLayout)findViewById((R.id.main_layout)),opt));
		extractButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				View dialog = inflater.inflate(R.layout.dialog,(ViewGroup) findViewById(R.id.dialog)); 
				final EditText  editText = (EditText) dialog.findViewById(R.id.et);

				AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
				builder.setTitle("这里是Title"); 
				builder.setPositiveButton("确定", new DialogInterface.OnClickListener() { 
						@Override 
						public void onClick(DialogInterface dialog, int which) {
							Toast.makeText(MainActivity.this, editText.getText().toString(), 
										   Toast.LENGTH_SHORT).show(); 
						} });
				builder.setView(dialog); 
				builder.setIcon(R.mipmap.ic_directory_parent);
				builder.show();
				}
		});
		
		final Drawable windowBackground = getWindow().getDecorView().getBackground();
		final RelativeLayout root = (RelativeLayout) findViewById(R.id.main_layout);
		topBlurView = (BlurView) findViewById(R.id.topBlurView);
		bottomBlurView = (BlurView) findViewById(R.id.bottomBlurView);
		bottomBlurView.setupWith(root).windowBackground(windowBackground).blurRadius(2f);
		ItemOnClickListener.bottomBlurView = bottomBlurView;
		ItemOnClickListener.topBlurView = topBlurView;
		
	}
	//onCreate结束
	
	
	
	
	
	
	
	
	
	
	
	
	
	//配置界面
	public void OnOptionButtonClick(View viewer){
		bottomBlurView.setVisibility(View.INVISIBLE);
		ScrollView.LayoutParams lp = new ScrollView.LayoutParams(-1,-1);
		ScrollView rl=(ScrollView)inflater.inflate(R.layout.option_layout,null);
		rl.setLayoutParams(lp);
		Button save_button=(Button)rl.findViewById(R.id.save_button);
		save_button.setOnClickListener(new Save_OnClickListener(main,rl,opt));

		Spinner _spn=(Spinner)rl.findViewById(R.id.mtnPickerspn);
		Spinner spn=(Spinner)rl.findViewById(R.id.ToastsHiderspn);
		//_spn.setAdapter( new ArrayAdapter<String>(this, R.layout.my_spinner_style, new String[]{"禁用toast1","禁用toast1","禁用toast1","禁用toast1","禁用toast1","禁用toast1","禁用toast1"}));
		spn.setAdapter(new adaptermy(this));
		_spn.setAdapter(new adaptermy2(this));
		
		//btn to maxium hide toasts.
		((Button)rl.findViewById(R.id.ToastsHider)).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				((Spinner)((RelativeLayout) v.getParent()).findViewById(R.id.ToastsHiderspn)).performClick();
			}
		});
		//btn for maxium sub-threads number
		((Button)rl.findViewById(R.id.mtnPicker)).setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v){
					((Spinner)((RelativeLayout) v.getParent()).findViewById(R.id.mtnPickerspn)).performClick();
				}
			});
		//
		Button numberbtn = ((Button)rl.findViewById(R.id.mtnPicker));
		String[] texts = numberbtn.getText().toString().split("）：");
		SpannableStringBuilder style=new SpannableStringBuilder(numberbtn.getText()); 
		style.setSpan(new ForegroundColorSpan(Color.RED),11,12,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		numberbtn.setText(style);
		//spn.setOnItemSelectedListener(new LPExOnItemSelectedListener2(opt));

		//黏附至窗口
		main.addView(rl,3);

		((CheckBox)findViewById(R.id.check_extratag)).setChecked(opt.opt.ExtraTag);
		((CheckBox)findViewById(R.id.check_normaltag)).setChecked(opt.opt.NomalTag);
		((CheckBox)findViewById(R.id.check_forcegetlrcfromnet)).setChecked(opt.opt.ForceGetLrcFromNet);
		((CheckBox)findViewById(R.id.check_forcegettagfromnet)).setChecked(opt.opt.ForceGetTagFormNet);
//目的目录
((Button)findViewById(R.id.pickToFolder)).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				DialogProperties properties = new DialogProperties();
				properties.selection_mode = DialogConfigs.SINGLE_MODE;
				properties.selection_type = DialogConfigs.DIR_SELECT; 
				properties.root = new File(DialogConfigs.DEFAULT_DIR);
				properties.error_dir = new File(DialogConfigs.DEFAULT_DIR);
				properties.offset = new File(opt.save_path);
				properties.extensions = null;
				FilePickerDialog dialog = new FilePickerDialog(MainActivity.this,properties);
				dialog.setTitle("请选择保存目录");
				
				dialog.setDialogSelectionListener(new DialogSelectionListener() {
						@Override public void
						onSelectedFilePaths(String[] files) { //files is the array of the paths of files selected by the Application User. 
					} });
				dialog.show();
				
			}
		});
		
//来源目录
((Button)findViewById(R.id.pickFromFolder)).setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v){
					DialogProperties properties = new DialogProperties();
					properties.selection_mode = DialogConfigs.SINGLE_MODE;
					properties.selection_type = DialogConfigs.DIR_SELECT; 
					properties.root = new File(DialogConfigs.DEFAULT_DIR);
					properties.error_dir = new File(DialogConfigs.DEFAULT_DIR);
					properties.offset = new File(opt.load_path);
					
					properties.extensions = null;
					FilePickerDialog dialog = new FilePickerDialog(MainActivity.this,properties);
					dialog.setTitle("请选择来源目录");
					dialog.setDialogSelectionListener(new DialogSelectionListener() {
							@Override public void
							onSelectedFilePaths(String[] files) { //files is the array of the paths of files selected by the Application User. 
							} });
					dialog.show();

				}
			});
			
		if(opt.save_path.length()!=0)
			((TextView)findViewById(R.id.save_path_text)).setText(opt.save_path);

		if(opt.load_path.length()!=0)
			((TextView)findViewById(R.id.load_path_text)).setText((opt.load_path));

	}

	public void OnSavePostData(LP_Option _opt){
		opt=_opt;
	}
	
	
	
	class Run_OnClickListener implements OnClickListener{
		RelativeLayout main_layout;
		LP_Option option;
		LPWorkerThreadManager manager;
		//Handler handle;

		private Run_OnClickListener(){}
		public Run_OnClickListener(RelativeLayout _main_layout,LP_Option _option){
			option=_option;
			main_layout=_main_layout;
			//handle=h;
		}

		@Override
		public void onClick(View p1)
		{
			Button btn = (Button)p1;
			if("Run".equals(btn.getText())){
				btn.setText("Stop");
				manager=new LPWorkerThreadManager(option.save_path,option.load_path,option.opt,main_layout,option.handler);
				manager.start();
				topBlurView = (BlurView) findViewById(R.id.topBlurView);

				final float radius = 25f;
				final float minBlurRadius = 10f;
				final float step = 4f;

				//set background, if your root layout doesn't have one
				final Drawable windowBackground = getWindow().getDecorView().getBackground();
				final RelativeLayout root = (RelativeLayout) findViewById(R.id.main_layout);
				
				topViewSettings = topBlurView.setupWith(root).windowBackground(windowBackground).blurRadius(0.1f);
				
				final ValueAnimator animator = ValueAnimator.ofFloat(1f, 16f); 
				animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { 
						@Override 
						public void onAnimationUpdate(ValueAnimator animation) {
							topViewSettings.blurRadius((float) animator.getAnimatedValue()); 
							topBlurView.updateBlur(); 
							
						} }); 
				animator.setDuration(500); animator.start();

			}else{
				manager.interrupt();
			}	

		}
	}
	
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



class Save_OnClickListener implements OnClickListener{
	RelativeLayout src;
	ScrollView back;
	LP_Option  opt;
	public Save_OnClickListener(View _src,View _back,LP_Option _opt){
		src=(RelativeLayout)_src;
		back=(ScrollView)_back;
		opt=_opt;
	}
	private Save_OnClickListener()
	{}
 
	@Override
	public void onClick(View p1)
	{
		opt.save_path=((TextView)back.findViewById(R.id.save_path_text)).getText().toString();
		opt.load_path=((TextView)back.findViewById(R.id.load_path_text)).getText().toString();
		try{
			File f=new File(opt.save_path);
			if(!f.exists()){
				throw new Exception("invaid save files path.");
			}
			f=new File(opt.load_path);
			if(!f.exists()){
				throw new Exception("invaid load files path");
			}
		}catch(Exception e){
			Toast.makeText(opt.ctx,e.getMessage(),Toast.LENGTH_SHORT).show();
			return;
		}
		opt.opt.ForceGetTagFormNet=((CheckBox)back.findViewById(R.id.check_forcegettagfromnet)).isChecked();
		opt.opt.ForceGetLrcFromNet=((CheckBox)back.findViewById(R.id.check_forcegetlrcfromnet)).isChecked();
		opt.opt.NomalTag=((CheckBox)back.findViewById(R.id.check_normaltag)).isChecked();
		opt.opt.ExtraTag=((CheckBox)back.findViewById(R.id.check_extratag)).isChecked();
		opt.controller_button.setEnabled((true));
		src.removeView(back);
		src.findViewById(R.id.bottomBlurView).setVisibility(View.VISIBLE);
	}

}

class LP_Option{
	Lrc_Parser_Option opt;
	String save_path;
	Handler handler;
	String load_path;
	Context ctx;
	Button controller_button;
	int thread_count;
	public LP_Option(){
		opt=new Lrc_Parser_Option();
		save_path=new String();
		load_path=new String();
	}
}



class LPWorkerThreadManager extends Thread{
	Lrc_Parser_Option option;
	RelativeLayout displayer;
	String save_path,load_path;
	Handler handler;
	private boolean inter=false;
	/*
	Button controller;
	Context ctx;*/
	public LPWorkerThreadManager(String _save_path,String _load_path,Lrc_Parser_Option _option,RelativeLayout _displayer,Handler h){
		LPWorkerThread.alive_count=3;
		displayer=_displayer;
		save_path=_save_path;
		load_path=_load_path;
		option=_option;
		handler=h;
	}

	private void resetText(){
		handler.post(new Runnable(){
				@Override
				public void run(){
					((Button)displayer.findViewById(R.id.run_button)).setText("Run");
				}
			});
	}
	
	public void interrupt(){
		inter = true;
		super.interrupt();
	 }
	
	@Override
	public void run(){
		File path=new File(load_path);

		LPWorkerThread t;
		File[] files=path.listFiles();
		System.gc();
		for(int i = 0;i<files.length && !inter;i++){
			File f = files[i];
			if(f.isDirectory())
				continue;
			LPWorkerThread.alive_count--;
			t=new LPWorkerThread(save_path,f.getAbsolutePath(),option,displayer,handler);
			
			t.start();
			while(LPWorkerThread.alive_count<=0 && !inter){
				//System.out.println("locking!!!!!!!" + f.getName());
				try{
					this.sleep(5);
				}catch(InterruptedException e){
					System.out.println(getName()+"从阻塞中退出...");
					System.out.println("this.isInterrupted()="+this.isInterrupted());
					resetText();
				}

				//continue;
			}
		}
		//System.out.println("all thread done.!!!!!!!");
		
		resetText();
		
	
		
	}

}

class LPWorkerThread extends Thread{
	Lrc_Parser_Option option;
	RelativeLayout displayer;
	String save_path,load_path;
	Handler handler;

	public static  volatile int alive_count=5;

	private LPWorkerThread(){}
	public LPWorkerThread(String _save_path,String _load_path,Lrc_Parser_Option _option,RelativeLayout _displayer,Handler h){
		displayer=_displayer;
		save_path=_save_path;
		load_path=_load_path;
		option=_option;
		handler=h;
	}
	private String ms2ffmTime(Long MS){
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
		return mFormatter.format("[%02d:%02d.%03d]", minutes, seconds,ms).toString();

	}
	
	@Override
	public void run()
	{
		//alive_count--;
		try{
			//mycode
			String str = new String(readParse(load_path));
			System.out.println("run!!!!!!!!!!!str = " + str);

			JSONArray jsonArray = new JSONArray("["+str+"]");
			JSONObject ducoj = jsonArray.getJSONObject(0);
			String lyrics = ducoj.optString("lyric");
			if(lyrics!=null && lyrics!="");
			{
				String[] lst1 =  lyrics.split("\\n");
				String[] lst2 =  ducoj.optString("translateLyric").split("\\n");
				HashMap<Integer,String> lst2_texts = new HashMap<Integer,String>();
				ArrayList<myCpr> lst1_texts = new ArrayList<myCpr>();
				String lst1_textsStr = "";
				if(ducoj.has("translateLyric"))
					for(String i:lst2){//处理翻译的歌词 没有的话应该可以自动跳过
						int offa = i.indexOf("[");
						int offb = i.indexOf("]");
						if(offa==-1||offb==-1)
						{
							continue;
						}
						String boli = i.substring(offa+1,offb);
						String[] tmp = boli.split("[: .]");
						if(tmp.length!=3) continue;
						if(tmp[0].length()!=2 || tmp[1].length()!=2) continue;
						if(tmp[2].length()==2) tmp[2] = tmp[2]+"0";
						if(tmp[2].length()!=3) continue;
						int time = Integer.valueOf(tmp[0])*60000+Integer.valueOf(tmp[1])*1000+Integer.valueOf(tmp[2]);
						lst2_texts.put(time,i.substring(offb+1));
					}
				for(String i:lst1){//处理原版歌词
					boolean isLyric=true;
					int offa = i.indexOf("[");
					int offb = i.indexOf("]");
					if(offa==-1||offb==-1)
					{
						continue;
					}

					String boli = i.substring(offa+1,offb);
					String[] tmp = boli.split("[: .]");
					//格式检查
					if(tmp.length!=3) 
						isLyric=false;
					else{
						if(tmp[0].length()!=2 || tmp[1].length()!=2) isLyric=false;
						if(tmp[2].length()==2) tmp[2] = tmp[2]+"0";
						if(tmp[2].length()!=3) isLyric=false;
					}
					if(isLyric){
						int time = Integer.valueOf(tmp[0])*60000+Integer.valueOf(tmp[1])*1000+Integer.valueOf(tmp[2]);//时间码
						String text = (offb+1)<i.length()?i.substring(offb+1):"";//原版歌词
						if(!"".equals(text)) {//跳过冗余
							if(lst2_texts.containsKey(time))
								//lst1_texts.add(new myCpr(time,i.substring(offb+1)+"\r\n"+lst2_texts.get(time)));
								lst1_textsStr += ms2ffmTime(Long.valueOf(time))+" "+i.substring(offb+1)+"\r\n"+lst2_texts.get(time)+"\r\n";
							else
								//lst1_texts.add(new myCpr(time,i.substring(offb+1)));
								lst1_textsStr += ms2ffmTime(Long.valueOf(time))+" "+i.substring(offb+1)+"\r\n";
						}
					}else{
						lst1_textsStr += i+"\n";
					}
				}
				//for(myCpr i:lst1_texts){
				// System.out.println(i);
				//}
				//获取文件名
				String fn,title = "";
				
				Lrc_Parser lprs=new Lrc_Parser(option,new Lrc_Parser_Expr());
				if(ducoj.has("musicId")==false){
					
				}
					
				System.out.println("strt");
				int	_id=Integer.parseInt(ducoj.optString("musicId"));
				Object lp;
				Lrc_Parser_Info info = lprs.GetTagFromNet(_id,"");
				title = info.Title;
				//System.out.println(info.Artist.replace("单曲 - 网易云音乐", "")+info.Title+".lrc");
				fn = info.Artist.replace("/", " ").replace("单曲 - 网易云音乐", "")+title+".lrc";
				
				//最终写入
				lst1_textsStr = "[is:songLrc]\r\n" + lst1_textsStr;
				File outFile=new File(save_path+fn);
				outFile.createNewFile();
				OutputStreamWriter oufi = new OutputStreamWriter(new FileOutputStream(save_path+fn),"GBK");
				//byte[] data = lst1_textsStr.getBytes();
				//oufi.write(lst1_textsStr, 0, lst1_textsStr.length());
				oufi.write(lst1_textsStr);
				oufi.close();



				//Handler handler=new Handler();
				ResultUpdateRunnable updater=new ResultUpdateRunnable(displayer,save_path+fn,title);
				handler.post(updater);


			}
		}catch(Exception e){
			e.fillInStackTrace();
			System.out.println("eeeeeeeeenjjjjjjjdfg!!!!!!!!"+e);
		}finally{
			alive_count++;
			System.out.println("++++++++++"+alive_count);
			super.run();
		}

		
		//alive_count++;
	}



	public static byte[] readParse(String fn) throws Exception {

		charsetDec cd = new charsetDec();

		ByteArrayOutputStream outStream = new ByteArrayOutputStream();

		byte[] data = new byte[1024];

		int len = 0;

		InputStream inStream = new FileInputStream(new File(fn));
		while ((len = inStream.read(data)) != -1) {
			outStream.write(data, 0, len);
		}
		inStream.close();
		return outStream.toByteArray();
		
	}

}

class ResultUpdateRunnable implements Runnable{
	Lrc_Parser_Result result;
	RelativeLayout displayer;
	String abs_path,title;
	private ResultUpdateRunnable(){}
	public ResultUpdateRunnable(Lrc_Parser_Result _result,RelativeLayout _displayer,String _abs_path){
		displayer=_displayer;
		result=_result;
		abs_path=_abs_path;
	}
	
	//mycode2
	public ResultUpdateRunnable(RelativeLayout _displayer,String _abs_path,String title){
		displayer=_displayer;
		abs_path=_abs_path;
		this.title=title;
	}
	@Override
	public void run()
	{
		//mycode2
		ResultListAdapter adapter=(ResultListAdapter)((ListView)displayer.findViewById(R.id.main_list)).getAdapter();
		adapter.addItem(adapter.getCount()+": "+new File(abs_path).getName(),title,abs_path);
	}

}





//adaptermy
class adaptermy extends BaseAdapter{
	public static Context ctx;
	ArrayList<Item> list=new ArrayList<Item>();
	public ArrayList<Item> phenoList;
	Random rand;
	private LayoutInflater mInflater;
	public adaptermy(Context ctx)
	{
		for(int i=0;i<7;i++)
		list.add(new Item());
		phenoList = new ArrayList<Item>();
		rand = new Random();
		this.ctx = ctx;
		mInflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}



	@Override
	public long getItemId(int p1)
	{
		return p1;
	}

	@Override
	public int getCount()
	{
		return 7;
	}

	@Override
	public Object getItem(int p)
	{
		return list.get(p);
	}

	@Override
	public View getView(final int pos, View p2, ViewGroup p3)
	{
		View item = mInflater.inflate(R.layout.list_item2, null);
		TextView tv=(TextView) item.findViewById(R.id.text);
		CheckBox cb = (CheckBox) item.findViewById(R.id.check);
		cb.setChecked(list.get(pos).isItemSelected);
		cb.setOnCheckedChangeListener(new OnCheckedChangeListener(){
				@Override
				public void onCheckedChanged(CompoundButton box,boolean checked){
					list.get(pos).isItemSelected = checked;
				}
			});


		//tv.setTextColor(i.color);
		//tv.setTextSize(13);
		tv.setText("禁用Toast"+1);
		//tv.setTextColor(Color.rgb(0,0,0));
		//tv.setOnClickListener(new ItemOnClickListener());
		return item;
	}

}


class adaptermy2 extends BaseAdapter{
public static Context ctx;
	ArrayList<Item> list=new ArrayList<Item>();
	public ArrayList<Item> phenoList;
	Random rand;
	private LayoutInflater mInflater;
	public adaptermy2(Context ctx)
	{
	for(int i=0;i<7;i++)
		list.add(new Item());
			phenoList = new ArrayList<Item>();
		rand = new Random();
		this.ctx = ctx;
		mInflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}



	@Override
	public long getItemId(int p1)
	{
	return p1;
		}

	@Override
	public int getCount()
	{
	return 7;
		}

	@Override
	public Object getItem(int p)
	{
	return list.get(p);
		}

	@Override
	public View getView(final int pos, View p2, ViewGroup p3)
	{
	View item = mInflater.inflate(R.layout.my_spinner_style, null);
		TextView tv=(TextView) item.findViewById(R.id.text);
	
		tv.setText(""+(pos*2+1));
		tv.setTextColor(Color.rgb(0,0,0));
		//tv.setOnClickListener(new ItemOnClickListener());
		return item;
		}

	}


class ResultListAdapter extends BaseAdapter{
	public static Context ctx;
	ArrayList<Item> list;
	public ArrayList<Item> phenoList;
	Random rand;
	private LayoutInflater mInflater;
	
	/* 
	 * 想构造一系列平滑过渡的颜色，用HSV颜色空间容易，用RGB较难。 
	 *  
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
		// TODO: Implement this method
		return Color.rgb((int)(v*255),(int)(255*t),(int)(255*p));
	}  
	
	
	public ResultListAdapter(Context ctx)
	{
		list = new ArrayList<Item>();
		phenoList = new ArrayList<Item>();
		rand = new Random();
		this.ctx = ctx;
		mInflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
	}
	
	public void addItem(String text,String SongName,String abs_path){
		Item i=new Item();
		i.color=Color.rgb(rand.nextInt(255),rand.nextInt(255),rand.nextInt(255));
		i.text=text;
		i.abs_full_path=abs_path;
		i.Songname=SongName;
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
	public View getView(final int pos, View p2, ViewGroup p3)
	{
		View item = mInflater.inflate(R.layout.list_item, null);
		TextView tv=(TextView) item.findViewById(R.id.text);
		((CheckBox) item.findViewById(R.id.check)).setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton box,boolean checked){
				Item it = (Item)getItem(pos);
				it.isItemSelected=checked;
				if(checked)
					phenoList.add(it);
				else
					phenoList.remove(it);
			}
		});

			
		Item i=list.get(pos);
		tv.setTextColor((int)(HSVtoRGB((pos%20+30)*5,0.1f,1.f)+i.color*0.0005f/100));
		//tv.setTextSize(13);
		tv.setText(i.text);
		//tv.setTextColor(Color.rgb(0,0,0));
		tv.setTag(i);
		tv.setOnClickListener(new ItemOnClickListener());
		return item;
	}

}

class Item{
	int color;
	String text,Songname,abs_full_path;
	boolean isItemSelected=false;
}

class ItemOnClickListener implements OnClickListener{
	public static RelativeLayout main_layout;
	public static LayoutInflater inflater;
	public static LinearLayout lyric_layout;
	public static BlurView topBlurView;
	public static BlurView bottomBlurView;
	@Override
	public void onClick(View p1)
	{
		bottomBlurView.setVisibility(View.INVISIBLE);
		if(lyric_layout==null)
		lyric_layout=(LinearLayout)inflater.inflate(R.layout.lyric,null);
		try{
			charsetDec cd = new charsetDec();
			//载入缓存好的转换的lrc歌词。
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(((Item)p1.getTag()).abs_full_path),cd.guessFileEncoding(new File(((Item)p1.getTag()).abs_full_path)).split(",")[0]));
			CharSequence chars="";
			int c;
			while((c=br.read())!=-1)
				chars=chars+String.valueOf((char)c);
			br.close();
			main_layout.addView(lyric_layout,3);
			TextView tv = (TextView)lyric_layout.findViewById(R.id.lrc_text);
			tv.setText(chars);
			((TextView)lyric_layout.findViewById(R.id.lrc_text)).setTextColor(Color.rgb(0,0,0));
			//((TextView)lyric_layout.findViewById(R.id.lrc_text)).setMovementMethod(ScrollingMovementMethod.getInstance());
			lyric_layout.setAlpha(0.85f);
			//提取
			((Button)lyric_layout.findViewById(R.id.lyric_back)).setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View h){
					
				}
			});
			//返回
			tv.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View h){
						main_layout.removeView(lyric_layout);
						topBlurView.updateBlur();
						bottomBlurView.setVisibility(View.VISIBLE);
						//lyric_layout.onKeyDown(KeyEvent.KEYCODE_BACK, new KeyEvent(KeyEvent.KEYCODE_BACK,KeyEvent.ACTION_DOWN));

						lyric_layout=null;
						
					}
				});
			((TextView)lyric_layout.findViewById(R.id.file_name_text)).setText(((Item)p1.getTag()).Songname);
		}catch(Exception e){
			e.fillInStackTrace();
		}
	}
}



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