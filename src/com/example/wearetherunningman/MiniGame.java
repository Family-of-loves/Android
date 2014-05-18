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
		Log.i("°á°ú","ºñ±³ÇÏ´ÂÁß");
		int num = Integer.parseInt(you);
		
		Log.i("my",my);
		Log.i("you",num+"");
		
		if(my.equals("0")){
			
			switch(num)
			{
				case 0:
					//Log.i("°á°ú","ºñ±è");
					result ="ºñ±è";
					break;
				case 1:
					//Log.i("°á°ú","Á³½¿");
					result ="Á³½¿";
					break;
				case 2:
					//Log.i("°á°ú","ÀÌ±è");
					result ="ÀÌ±è";
					break;
			}
		}
		
		else if(my.equals("1")){
			switch(num)
			{
				case 0:
					//Log.i("°á°ú","ÀÌ±è");
					result ="ÀÌ±è";
					break;
				case 1:
					//Log.i("°á°ú","ºñ±è");
					result ="ºñ±è";
					break;
				case 2:
					//Log.i("°á°ú","Á³½¿"); 
					result ="Á³½¿";
					break;
			}
			
		}
		
		else if(my.equals("2")){
			
			switch(num)
			{
				case 0:
					//Log.i("°á°ú","Á³½¿"); 
					result ="Á³½¿";
					break;
				case 1:
					//Log.i("°á°ú","ÀÌ±è");
					result ="ÀÌ±è";
					break;
				case 2:
					//Log.i("°á°ú","ºñ±è");
					result ="ºñ±è";
					break;
			}
		}
		
		return result;
	}
	
}
