package com.example.wearetherunningman;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.GoogleMap;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.DialogFragment;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.os.Build;

public class UIMainActivity extends ActionBarActivity  implements WsCallbackInterface{
	
	 static String inputRoom ;
	 static EditText room ;
	
	 static EditText name;	//이름을 입력받음
	static String inputName;
	
	static String team;
	static String item;
	
	
	EditText uid;
	String inputUid;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ui_activity_main);
		
		
		//name = (EditText) findViewById(R.id.name);
				
		
		
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
			
			
		}
		//Intent mIntent = new Intent(this, GameActivity.class);
		//startActivity(mIntent);   //If usert push "ok"button, turn the page to GameActivity
	      // return; 	
		//
        
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment implements
			View.OnClickListener {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {

			View rootView = inflater.inflate(R.layout.ui_fragment_main, container,false);
			room = (EditText) rootView.findViewById(R.id.put_num);; // 방이름 입력 받음
			
			Button b_enter = (Button) rootView.findViewById(R.id.enter);
			
			
			b_enter.setOnClickListener(this);
			 //	
			return rootView;
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			//
			UserInfoDialog mUserInfoDialog;
			switch (v.getId()) {
			case R.id.enter:
				inputRoom = room.getText().toString();
				//inputName = name.getText().toString();	
				mUserInfoDialog = new UserInfoDialog();
				mUserInfoDialog.show(getFragmentManager(), "USER");
				break;
			}// 사용자 인증번호 허락시 방참가???
		}

	}

	public static class UserInfoDialog extends DialogFragment {

			
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
					
			AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
			LayoutInflater mLayoutInflater = getActivity().getLayoutInflater();
			
			//name = (EditText) findViewById(R.id.name); // 방이름 입력 받음				
			//mBuilder.setView(mLayoutInflater.inflate(R.layout.dialog, null,	false));
			
			mBuilder.setTitle("User Information");
			View dialogView = mLayoutInflater.inflate(R.layout.dialog, null);
			mBuilder.setView(dialogView);
			
			name = (EditText)dialogView.findViewById(R.id.name); // 방이름 입력 받음
			RadioGroup teamgroup = (RadioGroup)dialogView.findViewById(R.id.radioGrup1);
			RadioGroup itemgroup = (RadioGroup)dialogView.findViewById(R.id.radioGrup2);	
			
			
			teamgroup.setOnCheckedChangeListener (new RadioGroup.OnCheckedChangeListener() 
	        {							        
	        	public void onCheckedChanged(RadioGroup group, int checkedId) {
					// TODO Auto-generated method stub

					switch (checkedId){
					case R.id.b_red:
						team = "1";
						
						break;
					
					case R.id.b_blue:
						team = "0";
						break;
						
					default:
						team = "선택안함";
						break;
					}
				}										
	        
	        });
						
			itemgroup.setOnCheckedChangeListener (new RadioGroup.OnCheckedChangeListener() 
	        {							        
	        	public void onCheckedChanged(RadioGroup group, int checkedId) {
					// TODO Auto-generated method stub

					switch (checkedId){
					case R.id.b_scissor:
						item= "가위";
						break;
					
					case R.id.b_rock:
						item=  "바위";
						break;
						
					case R.id.b_paper:
						item= "보";
						break;
					default:
						item= "선택안함";
						break;	
					}
				}
	        							        
	        });	
						
			
			mBuilder.setCancelable(false)
					.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {
								
								public void onClick(DialogInterface dialog,int whichButton) {
									inputName = name.getText().toString();							
																		
									Intent myIntent = new Intent(((Dialog) dialog).getContext(), GameActivity.class);
																
																		
									myIntent.putExtra("param1", inputRoom);
									myIntent.putExtra("param2", inputName);
									myIntent.putExtra("param3", team);
									myIntent.putExtra("param4", item);
																	
									
									startActivity(myIntent);   //If usert push "ok"button, turn the page to GameActivity
								     return; 	
			
								}
							})
					.setNegativeButton("Cancel",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									dialog.cancel();
								}
							});
			
			
			
			
			
			return mBuilder.create();
			
		}

		public void onStop() {
			super.onStop();
		}
		
		
		
		
		
	} // 다이얼로그 클래스
	
		

	@Override
	public void callback(JSONArray data) throws JSONException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void on(String event, JSONObject data) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMessage(String message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMessage(JSONObject json) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onConnect() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDisconnect() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onConnectFailure() {
		// TODO Auto-generated method stub
		
	}
	
}
