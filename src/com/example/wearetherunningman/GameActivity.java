package com.example.wearetherunningman;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.os.Vibrator;
import android.provider.Settings;
import android.provider.Settings.Secure;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.drawable.BitmapDrawable;

public class GameActivity extends ActionBarActivity implements WsCallbackInterface {
	
	/*
	 * Creator Objects 
	 */
	/**
	 * @uml.property  name="player"
	 * @uml.associationEnd  
	 */
	Player player;
	/**
	 * @uml.property  name="participant"
	 * @uml.associationEnd  
	 */
	Participant participant;
	/**
	 * @uml.property  name="gmap"
	 * @uml.associationEnd  
	 */
	GoogleMap gmap;
	/**
	 * @uml.property  name="ws"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	WsConn ws = new WsConn(this);
	Vibrator vib ;// ����ȿ��
	/*
	 * Using variables
	 */
    /**
	 * @uml.property  name="room"
	 */
    String room;
    /**
	 * @uml.property  name="name"
	 */
    String name;
    /**
	 * @uml.property  name="team"
	 */
    String team;
    /**
	 * @uml.property  name="item"
	 */
    String item;
    String uid;
    
    String youruid; // "�̴ϰ���" �̺�Ʈ�� �޾����� ������ �ǵ���̽��� uid�� �����ϰٴ�.
    String resultyouruid;// "res�̴ϰ���" �̺�Ʈ�� �޾����� ���������� ���� ����̽��� uid�� �����Ѵ�.(���� �ɾ���, �ٸ������ ����)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_game);
		vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		
		gmap = ((SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.gMap)).getMap();
		
		ws.run("http://dev.hagi4u.net:3000");
		
		Intent intent = getIntent(); // ���� �޾ƿ´�.
	    room = intent.getExtras().getString("param1"); 
	    name = intent.getExtras().getString("param2"); 
	    team = intent.getExtras().getString("param3"); 
	    item = intent.getExtras().getString("param4");
	    uid= Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
	    
	    TextView nameview= (TextView)findViewById(R.id.textView1) ;
	    nameview.setText(name+"��");
	    player = new Player(uid,name ,team,item ,getApplicationContext(), gmap);
	    //participant = new Participant(getApplicationContext(), gmap);
	    participant = new Participant(team ,getApplicationContext(), gmap,handler);
	    
	    
	    
	    ws.emitJoin(room, player);
		emitServer();
	}
	
	public void emitServer(){
		/*new Thread(new Runnable() {           
			public void run() {
				while (true) {
					try {
						
						Thread.sleep(2000);
                    } catch (InterruptedException e) {
                    	e.printStackTrace();
                    }
                }
            }
        }).start();*/
		new Thread(new Runnable() {           
			public void run() {
				while (true) {
					try {
						Thread.sleep(500);
						player.item=item;
						ws.emitMessage(player);
						participant.regMarker();
                    } catch (InterruptedException e) {
                    	e.printStackTrace();
                    }
                }
            }
        }).start();
		
	}

	/*
	 * WebSocket Connection.
	 */
	@Override
	public void on(String event, JSONObject obj) {
		if (event.equals("message")){
			participant.regParticipant(obj);
		} else if (event.equals("leaved")){
			participant.unRegParticipant(obj);
			
		}else if(event.equals("minigame")){	// �������� ������ ��û������,, 
			String myuid = null;
			try {
				myuid=obj.getString("desUid");		// �������� ���忡�� ������ ���� �ȴ�.
				youruid=obj.getString("uid");		// �� �������� �����̵ȴ�.
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if( uid.equals(myuid)){
				ghandler.sendEmptyMessage(0);
			}
			
		}else if(event.equals("resMinigame")){
			String resultmyuid = null;
			String answer=null;
			try {
				resultmyuid=obj.getString("desUid");
				resultyouruid=obj.getString("uid");
				answer=obj.getString("result");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
						
			if( uid.equals(resultmyuid)){
				if(answer.equals("����"))
					ghandler.sendEmptyMessage(3);
				else if(answer.equals("����"))
					//ghandler.sendEmptyMessage(2);
					ghandler.sendEmptyMessage(4);
				else
					ghandler.sendEmptyMessage(2);
			}
		}
		else {
			// ����ó��
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
	public static class PlaceholderFragment extends Fragment {
		public PlaceholderFragment() {}
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_game, container,false);

			return rootView;
		}
	}//
	// �̴� ���� ���̾�α׸� ���� ���� ���鷯 
	
	public Handler handler = new Handler()	{
		public void handleMessage( Message msg )		{
			AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);
			
				builder.setTitle("�̴ϰ���");
				builder.setMessage("���� �Ͻðٽ��ϱ�?");
				builder.setCancelable(true);        // �ڷ� ��ư Ŭ���� ��� ���� ����
				final String matchuid = msg.getData().getString("data");
				final String[] consort= participant.search(matchuid);		// ��Ŀ�κ��� ���� uid�� ���� ��񿡼� ã�Ƽ� �迭�� ����
				
				builder.setPositiveButton("��", new DialogInterface.OnClickListener() {			
					@SuppressWarnings("deprecation")
					public void onClick(DialogInterface dialog, int whichButton) {
						
						ws.gameStart(uid, consort[0]);	// ���� uid�� �����uid�� ������ ����
						AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);
						
						
						LayoutInflater mLayoutInflater = GameActivity.this.getLayoutInflater();
						View dialogView = mLayoutInflater.inflate(R.layout.contest, null);
						ImageView iv1= (ImageView)dialogView.findViewById(R.id.imageView1);	// ������ �̹����信 ǥ��
						BitmapDrawable dr1 = null;
						ImageView iv2= (ImageView)dialogView.findViewById(R.id.imageView2);	// ������ �̹����信 ǥ��
						BitmapDrawable dr2 = null;
						
						if(item.equals("0")){
							dr1 = (BitmapDrawable)getResources().getDrawable(R.drawable.btn_scissor);
						}
						else if(item.equals("1")){
							dr1 = (BitmapDrawable)getResources().getDrawable(R.drawable.btn_rock);
						}
						else{
							dr1 = (BitmapDrawable)getResources().getDrawable(R.drawable.btn_paper);
						}
						iv1.setImageDrawable(dr1);
												
						iv2.setImageDrawable(dr2);
						
						builder.setView(dialogView);
						builder.setCancelable(true);

						builder.setNegativeButton("�������", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {
								
								dialog.cancel();
							}
						});

						builder.show();
					}
				});	
						
				builder.setNegativeButton("�ƴϿ�", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						dialog.cancel();
					}
				});

				builder.show();
				
				//break;
						
			//}
			
			super.handleMessage( msg );
		}
	};
	
	public Handler ghandler = new Handler()	{
		public void handleMessage( Message msg )		{
			AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);
			
			
			LayoutInflater mLayoutInflater = GameActivity.this.getLayoutInflater();
			View dialogView = mLayoutInflater.inflate(R.layout.contest, null);
			ImageView iv1= (ImageView)dialogView.findViewById(R.id.imageView1);	// ������ �̹����信 ǥ��
			BitmapDrawable dr1 = null;
			ImageView iv2= (ImageView)dialogView.findViewById(R.id.imageView2);	// ������ �̹����信 ǥ��
			BitmapDrawable dr2 = null;
			
			switch ( msg.what )	{
			case	 0	:	// ������ �������忡�� �ߴ� ���̾��α�
				vib.vibrate(5000);
				builder.setTitle("�̴ϰ���");
				builder.setMessage("�������� ������ ��û�߽��ϴ�. ���� �Ͻðٽ��ϱ�?");
				builder.setCancelable(true);        // �ڷ� ��ư Ŭ���� ��� ���� ����
								
				builder.setPositiveButton("��", new DialogInterface.OnClickListener() {			
					public void onClick(DialogInterface dialog, int whichButton) {
						//ghandler.sendEmptyMessage(1);
						String re="����";
						ws.gameResult(uid,youruid,re);
						ghandler.sendEmptyMessage(1);
						dialog.cancel();
					}
				});	

				builder.setNegativeButton("�ƴϿ�", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						String re="����";
						ws.gameResult(uid,youruid,re);
						dialog.cancel();
					}
				});

				builder.show();
				
				break;
				
			case 1:	//������ ���� ���忡�� �����ϰٴٰ� ������ ���������� ����â ���̾�α׸� �ٿ�
				final String[] opponent= participant.search(youruid);
				builder.setTitle("����â");
				
				if(item.equals("0")){
					dr1 = (BitmapDrawable)getResources().getDrawable(R.drawable.btn_scissor);
				}
				else if(item.equals("1")){
					dr1 = (BitmapDrawable)getResources().getDrawable(R.drawable.btn_rock);
				}
				else{
					dr1 = (BitmapDrawable)getResources().getDrawable(R.drawable.btn_paper);
				}
				iv1.setImageDrawable(dr1);
				
				
				if(opponent[4].equals("0")){
					dr2 = (BitmapDrawable)getResources().getDrawable(R.drawable.btn_scissor);
				}
				else if(opponent[4].equals("1")){
					dr2 = (BitmapDrawable)getResources().getDrawable(R.drawable.btn_rock);
				}
				else{
					dr2 = (BitmapDrawable)getResources().getDrawable(R.drawable.btn_paper);
				}
				iv2.setImageDrawable(dr2);
				
				builder.setView(dialogView);
				builder.setCancelable(true);  
				
				builder.setPositiveButton("���Ȯ��", new DialogInterface.OnClickListener() {			
					public void onClick(DialogInterface dialog, int whichButton) {
					
						MiniGame mg = new MiniGame();
						final String re=mg.compare(item,opponent[4]);
						//Toast.makeText(getApplicationContext(), re, Toast.LENGTH_LONG) .show();
						ws.gameResult(uid,youruid,re);
						dialog.cancel();
						AlertDialog.Builder builder2 = new AlertDialog.Builder(GameActivity.this);
						builder2.setTitle("����â");
						
						if(re.equals("�̱�")){
							builder2.setMessage("����� �̰���ϴ�.");
							builder2.setCancelable(true); 
							builder2.setPositiveButton("Ȯ��", new DialogInterface.OnClickListener() {			
								public void onClick(DialogInterface dialog, int whichButton) {
									
									dialog.cancel();
								}
							});
						
							builder2.setNegativeButton("�����۹ٲٱ�", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int whichButton) {
									item= opponent[4];
									//ws.gameResult(uid,youruid,re);
									dialog.cancel();
								}
							});

							builder2.show();
						}
						else if(re.equals("����")){
							builder2.setMessage("����� �����ϴ�.");
							builder2.setCancelable(true); 
							builder2.setPositiveButton("����", new DialogInterface.OnClickListener() {			
								public void onClick(DialogInterface dialog, int whichButton) {
									//ws.gameResult(uid,youruid,re);
									dialog.cancel();
								}
							});
						
							builder2.setNegativeButton("������", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int whichButton) {
									//ws.gameResult(uid,youruid,re);
									dialog.cancel();
								}
							});

							builder2.show();
						}
						else{	
							builder2.setMessage("����� �����ϴ�.");
							builder2.setCancelable(true); 
							builder2.setPositiveButton("Ȯ��", new DialogInterface.OnClickListener() {			
								public void onClick(DialogInterface dialog, int whichButton) {
									//ws.gameResult(uid,youruid,re); 
									dialog.cancel();
								}
							});
						
							builder2.show();
						}
					}
				});
								
				builder.show();
				break;	
				
				
			case 2:		// ������ ��û�� ����̽��� ����� ���� �Ŀ� ������� ���̾�α� ( ����â ȭ��)
				final String[] opponent1= participant.search(resultyouruid);
				vib.vibrate(5000);
				builder.setTitle("���");
				
				if(item.equals("0")){
					dr1 = (BitmapDrawable)getResources().getDrawable(R.drawable.btn_scissor);
				}
				else if(item.equals("1")){
					dr1 = (BitmapDrawable)getResources().getDrawable(R.drawable.btn_rock);
				}
				else{
					dr1 = (BitmapDrawable)getResources().getDrawable(R.drawable.btn_paper);
				}
				iv1.setImageDrawable(dr1);
								
				if(opponent1[4].equals("0")){
					dr2 = (BitmapDrawable)getResources().getDrawable(R.drawable.btn_scissor);
				}
				else if(opponent1[4].equals("1")){
					dr2 = (BitmapDrawable)getResources().getDrawable(R.drawable.btn_rock);
				}
				else{
					dr2 = (BitmapDrawable)getResources().getDrawable(R.drawable.btn_paper);
				}
				iv2.setImageDrawable(dr2);
				
				builder.setView(dialogView);
				builder.setCancelable(true);  
				
				builder.setPositiveButton("���Ȯ��", new DialogInterface.OnClickListener() {			
					public void onClick(DialogInterface dialog, int whichButton) {
					
						MiniGame mg = new MiniGame();
						final String re=mg.compare(item,opponent1[4]);
											
						dialog.cancel();
						AlertDialog.Builder builder2 = new AlertDialog.Builder(GameActivity.this);
						builder2.setTitle("����â");
						
						if(re.equals("�̱�")){
							
							builder2.setMessage("����� �̰���ϴ�.");
							builder2.setCancelable(true); 
							builder2.setPositiveButton("Ȯ��", new DialogInterface.OnClickListener() {			
								public void onClick(DialogInterface dialog, int whichButton) {
									dialog.cancel();
								}
							});
						
							builder2.setNegativeButton("�����۹ٲٱ�", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int whichButton) {
									item= opponent1[4];
									
									dialog.cancel();
								}
							});

							builder2.show();
						}
						else if(re.equals("����")){
							builder2.setMessage("����� �����ϴ�.");
							builder2.setCancelable(true); 
							builder2.setPositiveButton("����", new DialogInterface.OnClickListener() {			
								public void onClick(DialogInterface dialog, int whichButton) {
									dialog.cancel();
								}
							});
						
							builder2.setNegativeButton("������", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int whichButton) {
									dialog.cancel();
								}
							});

							builder2.show();
						}
						else{	
							builder2.setMessage("����� �����ϴ�.");
							builder2.setCancelable(true); 
							builder2.setPositiveButton("Ȯ��", new DialogInterface.OnClickListener() {			
								public void onClick(DialogInterface dialog, int whichButton) {
									
									dialog.cancel();
								}
							});
													
							builder2.show();
						}
					}
				});
				
				builder.show();
				
				break;	
				
			case	 3	:	// ������ �������忡�� �ߴ� ���̾��α�
				builder.setTitle("�̴ϰ���");
				builder.setMessage("�������߽��ϴ�.");
				builder.setCancelable(true);        // �ڷ� ��ư Ŭ���� ��� ���� ����
								
				builder.setPositiveButton("��", new DialogInterface.OnClickListener() {			
					public void onClick(DialogInterface dialog, int whichButton) {
						
						dialog.cancel();
					}
				});	
				
				builder.show();
				
				break;
				
			case	 4	:	// ������ �������忡�� �ߴ� ���̾��α�
				builder.setTitle("�̴ϰ���");
				builder.setMessage("�����Ͽ����ϴ�.��ٷ��ּ���.");
				builder.setCancelable(true);        // �ڷ� ��ư Ŭ���� ��� ���� ����
								
				builder.setPositiveButton("��", new DialogInterface.OnClickListener() {			
					public void onClick(DialogInterface dialog, int whichButton) {
												
						dialog.cancel();
					}
				});	

				builder.show();
				
				break;	
				
			}
		
			
			super.handleMessage( msg );
		}
	};
	
	
	
}