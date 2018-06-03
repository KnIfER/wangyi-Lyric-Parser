package com.knizha.wangYiLP;

import android.content.Context;
import android.os.Handler;

import java.io.File;

public class LP_Option{
	File hisHandle;
	File module_sets_Handle;
	File favourite_dirs;
	String save_path;
	Handler handler;
	String load_path;
	Context ctx;
	int thread_count;
	boolean isCacheNamesOnly = true;
	boolean SelectAll=false;
	boolean Overwrite=false;
	boolean ForbidGetTagFormNet=false;
	boolean PreviewRawJsonLyric=false;
	boolean addisSongLyricHeader=true;
	boolean addOtherNoneLyricInfos=true;
	boolean editor_collapse_item_when_NotFocused=false;
	int multiThreadNumber=0;
	int charSetNumber=0;
	int timeFormatNumber=0;
	int translationFormatNumber=0;
	String co_lyrics_sep = "\r\n";
	public String dataDir="";

	boolean viewPagerLocked = false;
	boolean auto_move_cursor=false;
	boolean attatch_cursor_coupled=true;
	boolean add_agjust_coupled=false;
	boolean adjust_seek_coupled=true;//follow
	boolean adjust_auto_relase=true;
	boolean adjust_loop=true;
	boolean adjust_all=true;
	boolean confinedAdjust=false;
	boolean isAttABLooping=false;
	boolean showPaste_repalce_button;

	public int     cutterMode=0;
	public boolean coupledCut=true;

	boolean isInEditor=false;
	public int key_lyric_idx=0;
	float ajustBar_offY=0;
	float seekSet_offY=0;



	public LP_Option(){
	}
}
