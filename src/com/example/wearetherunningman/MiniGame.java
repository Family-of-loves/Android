package com.example.wearetherunningman;

import android.content.Context;
import android.widget.Toast;
import android.util.Log;

public class MiniGame {
	
	public MiniGame() {
		// TODO Auto-generated constructor stub
	}

	public String compare(String my,String you)
	{
		String result = null;
		Log.i("결과","비교하는중");
		int num = Integer.parseInt(you);
		
		Log.i("my",my);
		Log.i("you",num+"");
		
		if(my.equals("0")){
			
			switch(num)
			{
				case 0:
					//Log.i("결과","비김");
					result ="비김";
					break;
				case 1:
					//Log.i("결과","졌슴");
					result ="졌슴";
					break;
				case 2:
					//Log.i("결과","이김");
					result ="이김";
					break;
			}
		}
		
		else if(my.equals("1")){
			switch(num)
			{
				case 0:
					//Log.i("결과","이김");
					result ="이김";
					break;
				case 1:
					//Log.i("결과","비김");
					result ="비김";
					break;
				case 2:
					//Log.i("결과","졌슴"); 
					result ="졌슴";
					break;
			}
			
		}
		
		else if(my.equals("2")){
			
			switch(num)
			{
				case 0:
					//Log.i("결과","졌슴"); 
					result ="졌슴";
					break;
				case 1:
					//Log.i("결과","이김");
					result ="이김";
					break;
				case 2:
					//Log.i("결과","비김");
					result ="비김";
					break;
			}
		}
		
		return result;
	}
	
}
