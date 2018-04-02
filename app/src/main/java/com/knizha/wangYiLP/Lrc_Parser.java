package com.knizha.wangYiLP; 

/*
 * 
 * WARN:it's a reduced version.
 * 
*/
import java.util.*;
import java.net.*;
import java.io.*;
import java.util.regex.*;

class Lrc_Parser_Result
{
	HashMap<String,String> data;
	String Title,Album,Artist,Lyric;
	int id;
	boolean is_Finish_Parse;
}
 
class Lrc_Parser_Info
{
	String Artist=null,Title=null,Album=null;
}

enum Option_Lrc_Type
{
	Raw_Lrc,
	Trans_Lrc,
	Both_Raw_And_Trans_Lrc
	}

enum Option_Lrc_Combine_Type
{
	New_Line_And_Raw_Lrc_First,
	New_Line_And_Trans_Lrc_First,
	Side_By_Side_And_Raw_Lrc_First,
	Side_By_Side_And_Trans_Lrc_First
	}

class Lrc_Parser_Expr
{
	int id=0;

	String expr_split_lrc="(\\[\\d{2}\\d*\\:\\d{2}(\\.\\d*)?\\])(.*)";
	int lrc_split_id=3;
	String expr_lrc="(\\[\\d{2}\\d*\\:\\d{2}(\\.\\d*)?\\](\\s*.*?))(?=\\s*\\\\n)";
	int lrc_id = 1; 
	String expr_tag="\\[\\s*([^\\d]+?)\\s*\\:\\s*(.+?)\\s*\\]";
	int tag_name_id=1;
	int tag_value_id=2;
	String expr_lrc_time="\\[(\\d{2}\\d*)\\:(\\d{2})(\\.(\\d*))?\\]";
	int lrc_time_min=1;
	int lrc_time_sec=2;
	int lrc_time_msec=4;
	String expr_online_info="(<title>)((.+?)\\s-\\s(.+?))((?=锛�)|(?=\\s*-?\\s*缃戞槗浜戦煶涔�)|(?=</title>))";
	int online_title_id=3;
	int online_artist_id=4;
	String expr_data="\"\\s*([\\w\\d\"-]+)\"\\s*\\:\\s*(((\"\")|(\"(.+?)\")|((-?[\\d\\w]+)))(?=(\\})|(\\,\"\\s*([\\w\\d\"-]+))\"\\s*\\:))";
	int sub_data_name_id = 1;
	int sub_data_value_id = 2; // or 3

	String expr_info_name="data\\-res\\-name\\=\"(.+)\"";
	int info_name_id=1;
	String expr_info_artist="data-res-author=\"(.+)\"";
	int info_artist_id=1;
	//boolean LoadExprFormFile(String absolute_path){return false;}

}

class Lrc_Parser{

	String last_result=null;
	HashMap < String, String > data=new HashMap<String,String>();

	ArrayList < String > raw_lrc=new ArrayList<String>();
	ArrayList<String>trans_lrc=new ArrayList<String>();
	ArrayList<String>lrc=new ArrayList<String>();

	Lrc_Parser_Expr  expr=new Lrc_Parser_Expr();

	public Lrc_Parser(Lrc_Parser_Expr e){
		if(e!=null)
			expr=e;
	}


	public Lrc_Parser_Info GetTagFromNet(int id,String _url)throws Exception{
		String buf=new String();
		Lrc_Parser_Info info=new Lrc_Parser_Info();
		String addr=null;
		if(id>0)
			addr="http://music.163.com/m/song/"+id+"/?userid=0";
		else
			addr=_url;
			URL url = new URL(addr);
			HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
			
			httpCon.setConnectTimeout(30000);
			String response = httpCon.getResponseMessage();
			buf+=("HTTP/1.x " + httpCon.getResponseCode() + " " + response + "\n");
			
			InputStream in = new BufferedInputStream(httpCon.getInputStream());
			Reader r = new InputStreamReader(in);
			int c;
			while ((c = r.read()) != -1) {
				buf+=(String.valueOf((char) c));
			}
			in.close();
		

			if(buf.length()==0){
				Exception e=new Exception("Cant get html from net :"+addr);
				e.printStackTrace();
				throw e;
			}
		
			//haha
		
    	Pattern reg=Pattern.compile(expr.expr_info_name);
		Matcher result=reg.matcher(buf);
		int count=0;
		while(result.find()){
			for(int i=0;i<=result.groupCount();i++){
				if(i==expr.info_artist_id){
					info.Artist=result.group();
					continue;
				}
				if(i==expr.info_name_id){
					info.Title=result.group();
				}
			}
			count++;
		}
		
		if(info.Title==null||info.Artist==null){
			reg=Pattern.compile(expr.expr_online_info);
			result=reg.matcher(buf);
			while(result.find()){
				info.Title=result.group(expr.online_title_id);
				info.Artist=result.group(expr.online_artist_id);
				count++;
			}
		}
		//
		return info;
	}



}
