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
		Log.i("���","���ϴ���");
		int num = Integer.parseInt(you);
		
		Log.i("my",my);
		Log.i("you",num+"");
		
		if(my.equals("0")){
			
			switch(num)
			{
				case 0:
					//Log.i("���","���");
					result ="���";
					break;
				case 1:
					//Log.i("���","����");
					result ="����";
					break;
				case 2:
					//Log.i("���","�̱�");
					result ="�̱�";
					break;
			}
		}
		
		else if(my.equals("1")){
			switch(num)
			{
				case 0:
					//Log.i("���","�̱�");
					result ="�̱�";
					break;
				case 1:
					//Log.i("���","���");
					result ="���";
					break;
				case 2:
					//Log.i("���","����"); 
					result ="����";
					break;
			}
			
		}
		
		else if(my.equals("2")){
			
			switch(num)
			{
				case 0:
					//Log.i("���","����"); 
					result ="����";
					break;
				case 1:
					//Log.i("���","�̱�");
					result ="�̱�";
					break;
				case 2:
					//Log.i("���","���");
					result ="���";
					break;
			}
		}
		
		return result;
	}
	
}
