package com.example.wearetherunningman;

import java.util.regex.Pattern;

import io.socket.IOAcknowledge;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.DialogFragment;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {
	static EditText room;
	static String inputRoom;
	static EditText name;
	static String inputName;
	static String team;
	static String item;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}

	}

	// 뒤로가기 버튼
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			new AlertDialog.Builder(this)
					.setTitle("종료")
					.setMessage("종료 하시겠어요?")
					.setPositiveButton("예",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									android.os.Process
											.killProcess(android.os.Process
													.myPid());
								}
							}).setNegativeButton("아니오", null).show();
			return false;
		case KeyEvent.KEYCODE_MENU:
			return true;

		default:
			return false;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.game, menu);
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
	@SuppressLint("NewApi")
	public static class PlaceholderFragment extends Fragment implements	View.OnClickListener, WsCallbackInterface {
		
		WsConn ws = new WsConn(this);
		
		AlertDialog.Builder alertMsg;
		UserInfoDialog mUserInfoDialog;
		HowToDialog mHowToDialog;
		
		public PlaceholderFragment() {}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,	Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container, false);
			room = (EditText) rootView.findViewById(R.id.put_num);

			ImageButton b_enter = (ImageButton) rootView.findViewById(R.id.enter);
			b_enter.setOnClickListener(this);
			
			ImageButton how_to_use = (ImageButton) rootView.findViewById(R.id.how);
			how_to_use.setOnClickListener(this);
			ImageButton web = (ImageButton) rootView.findViewById(R.id.web);web.setOnClickListener(this);
			
			alertMsg = new AlertDialog.Builder(getActivity());

			
			
			ws.run("http://dev.hagi4u.net:3000");
			
			
			return rootView;
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub

			switch (v.getId()) {
			case R.id.enter:
				inputRoom = room.getText().toString();
				ws.chkRoom(1, inputRoom);
				if (inputRoom.equals("")) 
					Toast.makeText(getActivity().getBaseContext(),"방 이름을 입력하세요", Toast.LENGTH_SHORT)	.show();
				break;
			case R.id.how:
				mHowToDialog = new HowToDialog();
				mHowToDialog.show(getFragmentManager(), "사용방법");
				break;
			case R.id.web:
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://dev.hagi4u.net:3000/"));
				startActivity(browserIntent);
				break;
			}
		}
		@Override
		public void on(String event, JSONObject obj) {
			// TODO Auto-generated method stub
			if(event.equals("established")){
				try {
					String statCode = obj.getString("statCode");
					String statMsg = obj.getString("statMsg");
					
					
					if(statCode.equals("403") || statCode.equals("600")){
						
						Bundle data = new Bundle();
						Message msg = Message.obtain();
						//Data Setting
						data.putString("data", statMsg);
						
						//Message Setting
						msg.what = 0;
						msg.setData(data);
						
						alertMsgHandler.sendMessage(msg);
					} else if(statCode.equals("200")){
						mUserInfoDialog = new UserInfoDialog();
						mUserInfoDialog.show(getFragmentManager(), "USER");
						
					}
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				
			}
		}
		@Override
		public void callback(JSONArray data) throws JSONException {}
		@Override
		public void onMessage(String message) {}
		@Override
		public void onMessage(JSONObject json) {}
		@Override
		public void onConnect() {}
		@Override
		public void onDisconnect() {}
		@Override
		public void onConnectFailure() {}
		
		private Handler alertMsgHandler = new Handler() {
			public void handleMessage(Message msg) {
				String statMsg = msg.getData().getString("data");
				switch (msg.what) {
					case 0:
						new AlertDialog.Builder(getActivity())
						   .setTitle(statMsg)
						   .setNegativeButton("확인", new DialogInterface.OnClickListener() {
						    
						    @Override
						    public void onClick(DialogInterface dialog, int which) {
						     // TODO Auto-generated method stub
						    }
						   }).show();;
					break;
					default :
					break;
				}
			}
		};
	}

	public static class HowToDialog extends DialogFragment {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {

			AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());

			mBuilder.setTitle("사용방법");
			mBuilder.setMessage("1. 주최자가 웹을 통해 방을 생성합니다.\n"
					+ "    http://dev.hagi4u.net:3000/\n"
					+ "2. 참가자는 생성된 방으로 참가를 하고\n" + "    정보를 입력 후 게임을 시작합니다.");

			mBuilder.setCancelable(false).setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,	int whichButton) {
							dialog.cancel();
						}
					});

			return mBuilder.create();
		}
		public void onStop() {
			super.onStop();
		}

	}

	public static class UserInfoDialog extends DialogFragment {
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			inputName = null;
			team = null;
			item = null;

			AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
			LayoutInflater mLayoutInflater = getActivity().getLayoutInflater();

			// name = (EditText) findViewById(R.id.name); // 방이름 입력 받음
			// mBuilder.setView(mLayoutInflater.inflate(R.layout.dialog, null,
			// false));

			mBuilder.setTitle("User Information");
			View dialogView = mLayoutInflater.inflate(R.layout.dialog, null);
			mBuilder.setView(dialogView);

			name = (EditText) dialogView.findViewById(R.id.name); // 방이름 입력 받음
			RadioGroup teamgroup = (RadioGroup) dialogView.findViewById(R.id.radioGrup1);
			RadioGroup itemgroup = (RadioGroup) dialogView.findViewById(R.id.radioGrup2);

			teamgroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
						public void onCheckedChanged(RadioGroup group, int checkedId) {
							// TODO Auto-generated method stub
							switch (checkedId) {
								case R.id.b_red:
									team = "1";
									break;
								case R.id.b_blue:
									team = "0";
									break;
								default:
									team = null;
									break;
							}
						}
					});
			itemgroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
						public void onCheckedChanged(RadioGroup group,int checkedId) {
							// TODO Auto-generated method stub
							switch (checkedId) {
								case R.id.b_scissor:
									item = "0";
									break;
								case R.id.b_rock:
									item = "1";
									break;
								case R.id.b_paper:
									item = "2";
									break;
								default:
									item = null;
									break;
							}
						}
					});
			mBuilder.setCancelable(false).setPositiveButton("OK",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,	int whichButton) {
									inputName = name.getText().toString();
									
									Pattern ps = Pattern.compile("^[a-zA-Z0-9ㄱ-ㅎ가-흐]+$");//영문, 숫자, 한글만 허용
				                    if(!ps.matcher(inputName).matches()){
				                        
				                    	Toast.makeText(
												getActivity().getBaseContext(),
												"이름에 특수문자가 포함되어있습니다.", Toast.LENGTH_SHORT)
												.show();
				                    	
				                    	
				                    }
				                    else{
				                    	
				                    	
				                    	
				                    	if (inputName.equals("")) {
											Toast.makeText(
													getActivity().getBaseContext(),
													"이름을 입력하세요", Toast.LENGTH_SHORT)
													.show();

										} else if (team == null) {
											Toast.makeText(
													getActivity().getBaseContext(),
													"팀을 선택하세요", Toast.LENGTH_SHORT)
													.show();
										} else if (item == null) {
											Toast.makeText(
													getActivity().getBaseContext(),
													"아이템을 선택하세요",
													Toast.LENGTH_SHORT).show();
										} else {
											Intent myIntent = new Intent(
													((Dialog) dialog).getContext(),
													GameActivity.class);

											myIntent.putExtra("param1", inputRoom);
											myIntent.putExtra("param2", inputName);
											myIntent.putExtra("param3", team);
											myIntent.putExtra("param4", item);
											System.out.println(team + " / " + item);
											startActivity(myIntent); // If usert
																		// push
																		// "ok"button,
																		// turn the
																		// page to
																		// GameActivity
											return;
										}
				                    }
									
									
									
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
	}
}