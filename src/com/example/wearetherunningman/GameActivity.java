package com.example.wearetherunningman;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.drawable.BitmapDrawable;

public class GameActivity extends ActionBarActivity implements
		WsCallbackInterface {

	/*
	 * Creator Objects
	 */

	/**
	 * @uml.property name="player"
	 * @uml.associationEnd
	 */

	Player player;

	/**
	 * @uml.property name="participant"
	 * @uml.associationEnd
	 */

	Participant participant;

	/**
	 * @uml.property name="gmap"
	 * @uml.associationEnd
	 */

	GoogleMap gmap;

	/**
	 * @uml.property name="ws"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */

	WsConn ws = new WsConn(this);
	Vibrator vib;// 진동효과
	/*
	 * Using variables
	 */

	/**
	 * @uml.property name="room"
	 */
	String room;
	/**
	 * @uml.property name="name"
	 */
	String name;
	/**
	 * @uml.property name="team"
	 */
	String team;
	/**
	 * @uml.property name="item"
	 */
	String item;
	String uid;

	String youruid; // "미니게임" 이벤트를 받앗을때 게임을 건디바이스의 uid를 저장하겟다. -- 게임제안을 받은놈만이 이
					// 값을 사용한다.
	String resultyouruid;// "res미니게임" 이벤트를 받았을때 게임제안을 받은 디바이스의 uid를 저장한다.(내가
							// 걸었고, 다른사람이 받음)
							// --> 내가 게임 제안을 걸었고 , 상대에 의해 결과값이 보내졌을때 나는 다시 받게되고
							// 그 상대의 uid를 저장하게된다.

	AlertDialog mydialog;// 게임신청자가 게임신청후 자신의 아이템만 띄워주는 다이얼로그// 자동종료됨
	AlertDialog rejectdialog;// 거절시 뜨는 다이얼로그
	AlertDialog okdialog;// 승인시 뜨는 다이얼로그
	AlertDialog startdialog;// 받은사람에게 게임을 진행하겟냐고 묻는 다이얼로그
	AlertDialog resultdialog;

	ImageView myItem;
	ImageView myTeam;

	int okNum = 0;
	int rejectNum = 0;
	TextView okGame;
	TextView rejectGame;
	int myTeamCount = 0;
	int otherTeamCount = 0;
	TextView myTeamNum;
	TextView otherTeamNum;

	int gameOver = 0;
	int oneTOone = 0;
    
   
    
    @Override

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_game);

		vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

		gmap = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.gMap)).getMap();

		ws.run("http://dev.hagi4u.net:3000");

		Intent intent = getIntent(); // 값을 받아온다.
		room = intent.getExtras().getString("param1");
		name = intent.getExtras().getString("param2");
		team = intent.getExtras().getString("param3");
		item = intent.getExtras().getString("param4");
		uid = Secure
				.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

		TextView nameview = (TextView) findViewById(R.id.viewUserName);
		nameview.setText(name + "님");
		player = new Player(uid, name, team, item, getApplicationContext(),
				gmap);
		// participant = new Participant(getApplicationContext(), gmap);
		participant = new Participant(team, getApplicationContext(), gmap,
				handler, player);
		// 슬라이딩메뉴 부분 초기화
		myItem = (ImageView) findViewById(R.id.myItem);
		myTeam = (ImageView) findViewById(R.id.myTeam);
		okGame = (TextView) findViewById(R.id.num_game);
		rejectGame = (TextView) findViewById(R.id.num_refuse);
		myTeamNum = (TextView) findViewById(R.id.num_myTeam);
		otherTeamNum = (TextView) findViewById(R.id.num_enemyTeam);

		ImageButton logOut = (ImageButton) findViewById(R.id.btn_logout);
		logOut.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder2 = new AlertDialog.Builder(
						GameActivity.this);
				builder2.setTitle("종료");
				builder2.setMessage("종료하시겠습니까?");
				builder2.setCancelable(true);
				builder2.setPositiveButton("확인",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								android.os.Process
										.killProcess(android.os.Process.myPid());
								dialog.cancel();
							}
						});

				builder2.setNegativeButton("아니오",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {

								dialog.cancel();
							}
						});

				builder2.show();
			}
		});

		ws.emitJoin(room, player);
		emitServer();
	}

	public void emitServer() {

		new Thread(new Runnable() {
			public void run() {
				while (true) {
					try {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								BitmapDrawable dmyItem = null;
								if (item.equals("0")) {
									dmyItem = (BitmapDrawable) getResources()
											.getDrawable(R.drawable.btn_scissor);
								} else if (item.equals("1")) {
									dmyItem = (BitmapDrawable) getResources()
											.getDrawable(R.drawable.btn_rock);
								} else {
									dmyItem = (BitmapDrawable) getResources()
											.getDrawable(R.drawable.btn_paper);
								}
								myItem.setImageDrawable(dmyItem);

								BitmapDrawable dmyTeam = null;
								if (team.equals("0")) {
									dmyTeam = (BitmapDrawable) getResources()
											.getDrawable(R.drawable.btn_blue);
								} else {
									dmyTeam = (BitmapDrawable) getResources()
											.getDrawable(R.drawable.btn_red);
								}
								myTeam.setImageDrawable(dmyTeam);

								okGame.setText(okNum + "회");
								rejectGame.setText(rejectNum + "회/5회");

								String[] st = null;
								ArrayList<String[]> pa = pa = new ArrayList<String[]>();
								;
								ArrayList<String[]> pa1 = new ArrayList<String[]>();

								pa1 = participant.read();

								if (pa1 != null) {
									pa.addAll(pa1);

									if (pa.size() != 0) {

										for (int i = 0; i < pa.size(); i++) {
											st = pa.get(i);

											if (team.equals(st[2]))
												myTeamCount++;

											else
												otherTeamCount++;
										}
									}
								}
								if (otherTeamCount == 0)
									gameOver += 1; // 상대편 수가 0인 채로 지속되면 증가

								if (gameOver == 7)
									ghandler.sendEmptyMessage(5); // 게임을 이겻다고 알림

								if (otherTeamCount == 1 && myTeamCount == 0) // 나와 상대편 한명이 남았을 경우

									oneTOone += 1;

								if (oneTOone == 7) {
									if (item.equals(st[5])) {
										vib.vibrate(3000);// 진동
										Random r = new Random();
										item = Integer.toString(r.nextInt(2 - 0 + 1) + 0);

									}
								}
								myTeamNum.setText(myTeamCount + "명");
								otherTeamNum.setText(otherTeamCount + "명");
								myTeamCount = 0;
								otherTeamCount = 0;
							}
						});

						Thread.sleep(3000);
						player.item = item;
						ws.emitMessage(player);
						participant.regMarker();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();

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

	/*
	 * WebSocket Connection.
	 */
	@Override
	public void on(String event, JSONObject obj) {
		if (event.equals("message")) {
			participant.regParticipant(obj);
		} else if (event.equals("leaved")) {
			participant.unRegParticipant(obj);

		} else if (event.equals("minigame")) { // 게임신청을 받은 사람이 수행하는 부분(모두가 받겟지만)
			String myuid = null;
			try {
				myuid = obj.getString("desUid"); // 게임을 신청 받은놈이 목적지 uid를 자신의uid에
													// 저장
				youruid = obj.getString("uid"); // 보낸사람의uid를 상대uid로 지정 // 나중에
												// 이값을 사용하게 된다.
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (uid.equals(myuid)) { // 내 uid와 비교해서 맞다면..
				ghandler.sendEmptyMessage(0); // 게임을 진행하겟냐는 다이얼로그를 띄원준다.
			}

		} else if (event.equals("resMinigame")) { // 게임을 제안한 사람이 수행
			String resultmyuid = null;
			String answer = null;
			try {
				resultmyuid = obj.getString("desUid");
				resultyouruid = obj.getString("uid");
				answer = obj.getString("result");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (uid.equals(resultmyuid)) { // 게임 결과에 대해서 내 uid와 일치한다면
				if (answer.equals("거절"))
					ghandler.sendEmptyMessage(3);// 거절 다이얼로그
				else if (answer.equals("승인"))
					// ghandler.sendEmptyMessage(2);
					ghandler.sendEmptyMessage(4);// 승인 다이얼로그
				else
					// re가 거절,승인 외에 이김,졌음,비김 일 경우
					ghandler.sendEmptyMessage(2); // 결과가 나온후의 게임창 다이얼로그를 띄운다.
													// (내무기,상대무기 다보임)
			}
		} else {
			// 에러처리
		}
	}

	@Override
	public void callback(JSONArray data) throws JSONException {
	}

	@Override
	public void onMessage(String message) {
	}

	@Override
	public void onMessage(JSONObject json) {
	}

	@Override
	public void onConnect() {
	}

	@Override
	public void onDisconnect() {
	}

	@Override
	public void onConnectFailure() {
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
	public static class PlaceholderFragment extends Fragment {
		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_game, container,
					false);

			return rootView;
		}
	}//

	private final int MSG_ONLY_DISMISS = 1;

	// 다이얼로그 자동종료를 위한 핸들러
	private Handler dHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0: // 내무기만 뜨는 게임창 부분에 대한 자동종료

				if (mydialog != null && mydialog.isShowing()) {
					mydialog.dismiss();
				}
				sendEmptyMessageDelayed(MSG_ONLY_DISMISS, 2000);
				break;

			case 1: // 거절 부분에 대한 자동종료

				if (rejectdialog != null && rejectdialog.isShowing()) {
					rejectdialog.dismiss();
				}
				sendEmptyMessageDelayed(MSG_ONLY_DISMISS, 2000);
				break;

			case 2: // 승인에 대한 자동종료

				if (okdialog != null && okdialog.isShowing()) {
					okdialog.dismiss();
				}
				sendEmptyMessageDelayed(MSG_ONLY_DISMISS, 2000);
				break;

			case 3: // 게임을 받은 입장에서 게임을 진행하겟냐에 대한 의사가 없을때 승인으로 받아 들이고 자동종료함.

				if (startdialog != null && startdialog.isShowing()) {
					startdialog.dismiss();
					okNum++;
					String re = "승인";
					ws.gameResult(uid, youruid, re);
					ghandler.sendEmptyMessage(1);
				}
				sendEmptyMessageDelayed(MSG_ONLY_DISMISS, 2000);
				break;

			case 4: // 게임을 받은 입장에서 결과 확인을 눌러주지 않으면 게임 신청자가 결과를 확인하지 못하는걸 방지함

				if (resultdialog != null && resultdialog.isShowing()) {
					resultdialog.dismiss();

					final String[] opponent = participant.search(youruid);

					MiniGame mg = new MiniGame();
					final String re = mg.compare(item, opponent[5]);

					ws.gameResult(uid, youruid, re); // 확인과 함께 결과를 상대방에게도 보내준다.

					AlertDialog.Builder builder2 = new AlertDialog.Builder(
							GameActivity.this);
					builder2.setTitle("게임창");

					if (re.equals("이김")) {
						builder2.setMessage("당신은 이겼습니다.");
						builder2.setCancelable(true);
						builder2.setPositiveButton("확인",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int whichButton) {

										dialog.cancel();
									}
								});

						builder2.setNegativeButton("아이템바꾸기",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int whichButton) {
										item = opponent[5];
										// ws.gameResult(uid,youruid,re);
										dialog.cancel();
									}
								});

						builder2.show();
					} else if (re.equals("졌슴")) {
						builder2.setMessage("당신은 졌습니다.");
						builder2.setCancelable(true);

						builder2.setNegativeButton("나가기",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int whichButton) {
										ws.gameOut(uid);
										android.os.Process
												.killProcess(android.os.Process
														.myPid());
										dialog.cancel();
									}
								});

						builder2.show();
					} else {
						builder2.setMessage("당신은 비겼습니다.");
						builder2.setCancelable(true);
						builder2.setPositiveButton("확인",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int whichButton) {
										// ws.gameResult(uid,youruid,re);
										dialog.cancel();
									}
								});

						builder2.show();
					}

				}
				sendEmptyMessageDelayed(MSG_ONLY_DISMISS, 2000);
				break;

			}
		}
	};

	// 마커를 찍은후 다이얼로그를 띄우기 위한 랜들러
	// 마커를 찍어서 게임을 신청하는 입장에서 사용한다.
	public Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			AlertDialog.Builder builder = new AlertDialog.Builder(
					GameActivity.this);

			builder.setTitle("미니게임");
			builder.setMessage("진행 하시겟습니까?");
			builder.setCancelable(true); // 뒤로 버튼 클릭시 취소 가능 설정
			final String matchuid = msg.getData().getString("data"); // 마커로부터 받은
																		// uid값을
																		// 저장한다.
			final String[] consort = participant.search(matchuid); // 마커로부터 받은
																	// uid를 통해
																	// 디비에서 찾아서
																	// 배열에 대입

			builder.setPositiveButton("예",
					new DialogInterface.OnClickListener() {
						@SuppressWarnings("deprecation")
						public void onClick(DialogInterface dialog,
								int whichButton) {

							ws.gameStart(uid, consort[0]); // 나의 uid와 상대의uid를
															// 서버로 전송
							AlertDialog.Builder mybuilder = new AlertDialog.Builder(
									GameActivity.this);

							LayoutInflater mLayoutInflater = GameActivity.this
									.getLayoutInflater();
							View dialogView = mLayoutInflater.inflate(
									R.layout.contest, null);
							ImageView iv1 = (ImageView) dialogView
									.findViewById(R.id.imageView1); // 내무기 이미지뷰에
																	// 표시
							BitmapDrawable dr1 = null;
							ImageView iv2 = (ImageView) dialogView
									.findViewById(R.id.imageView2); // 상대무기
																	// 이미지뷰에
																	// 표시..
																	// null값이다.
							BitmapDrawable dr2 = null;

							if (item.equals("0")) {
								dr1 = (BitmapDrawable) getResources()
										.getDrawable(R.drawable.btn_scissor);
							} else if (item.equals("1")) {
								dr1 = (BitmapDrawable) getResources()
										.getDrawable(R.drawable.btn_rock);
							} else {
								dr1 = (BitmapDrawable) getResources()
										.getDrawable(R.drawable.btn_paper);
							}
							iv1.setImageDrawable(dr1);

							iv2.setImageDrawable(dr2); // null값을 그냥 사용함으로써 상대의
														// 무기는 알수가 없게 된다.

							mybuilder.setView(dialogView);
							mybuilder.setCancelable(true);

							mybuilder.setNegativeButton("계속진행",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int whichButton) {

											dialog.cancel();
										}
									});
							mydialog = mybuilder.create();
							mydialog.show();
							dHandler.sendEmptyMessageDelayed(0, 3000); // 시간지나면
																		// 자동종료
						}
					});

			builder.setNegativeButton("아니오",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							dialog.cancel();
						}
					});

			builder.show();

			super.handleMessage(msg);
		}
	};

	// 여러 다이얼로그들을 제어
	public Handler ghandler = new Handler() {
		public void handleMessage(Message msg) {
			AlertDialog.Builder builder = new AlertDialog.Builder(
					GameActivity.this);

			LayoutInflater mLayoutInflater = GameActivity.this
					.getLayoutInflater();
			View dialogView = mLayoutInflater.inflate(R.layout.contest, null);
			ImageView iv1 = (ImageView) dialogView
					.findViewById(R.id.imageView1); // 내무기 이미지뷰에 표시
			BitmapDrawable dr1 = null;
			ImageView iv2 = (ImageView) dialogView
					.findViewById(R.id.imageView2); // 상대무기 이미지뷰에 표시
			BitmapDrawable dr2 = null;

			switch (msg.what) {
			case 0: // 게임을 받은입장에서 뜨는 다이어얼로그
				vib.vibrate(5000);// 진동

				if (rejectNum == 5) {
					builder.setTitle("미니게임");
					builder.setMessage("더이상 거절할수 없습니다. 게임을 진행합니다.");
					startdialog = builder.create();
					startdialog.show();
					dHandler.sendEmptyMessageDelayed(3, 5000);
<<<<<<< HEAD
				}
				else{

				builder.setTitle("�̴ϰ���");
				builder.setMessage("�������� ������ ��û�߽��ϴ�. ���� �Ͻðٽ��ϱ�?");
				builder.setCancelable(true);        // �ڷ� ��ư Ŭ���� ��� ���� ����
								
				builder.setPositiveButton("��", new DialogInterface.OnClickListener() {			
					public void onClick(DialogInterface dialog, int whichButton) {
						//ghandler.sendEmptyMessage(1);
						okNum++;
						String re="����";
						ws.gameResult(uid,youruid,re);
						ghandler.sendEmptyMessage(1);	// ����â ���̾�α� ���� ����� ��빫�⸦ �����ش�.
						dialog.cancel();
					}
				});	

				builder.setNegativeButton("�ƴϿ�", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						rejectNum++;
						String re="����";
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
									//item = opponent[5];
									//ws.gameResult(uid,youruid,re);
									dialog.cancel();
								}
							});
=======
				} else {
					builder.setTitle("미니게임");
					builder.setMessage("누군가가 게임을 신청했습니다. 진행 하시겟습니까?");
					builder.setCancelable(true); // 뒤로 버튼 클릭시 취소 가능 설정
>>>>>>> 14863f8d038cd6487cb537bd88d0b2d348ef644e

					builder.setPositiveButton("예",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									// ghandler.sendEmptyMessage(1);
									okNum++;
									String re = "승인";
									ws.gameResult(uid, youruid, re);
									ghandler.sendEmptyMessage(1); // 게임창 다이얼로그
																	// 나의 무기와
																	// 상대무기를
																	// 보여준다.
									dialog.cancel();
								}
							});

					builder.setNegativeButton("아니오",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									rejectNum++;
									String re = "거절";
									ws.gameResult(uid, youruid, re);
									dialog.cancel();
								}
							});
					startdialog = builder.create();
					startdialog.show();
					dHandler.sendEmptyMessageDelayed(3, 5000); // 시간지나면 자동종료,,
																// 승인을 뜻하게된다.
				}
				break;

			case 1: // 게임을 받은 입장에서 게임하겟다고 했을때 가위바위보 게임창 다이얼로그를 뛰움 -->> 게임을 받은놈이
					// 게임을하겟다고 하면 띄는 게임창 다이얼로그
				final String[] opponent = participant.search(youruid);
				builder.setTitle("게임창");

				if (item.equals("0")) {
					dr1 = (BitmapDrawable) getResources().getDrawable(
							R.drawable.btn_scissor);
				} else if (item.equals("1")) {
					dr1 = (BitmapDrawable) getResources().getDrawable(
							R.drawable.btn_rock);
				} else {
					dr1 = (BitmapDrawable) getResources().getDrawable(
							R.drawable.btn_paper);
				}
				iv1.setImageDrawable(dr1);

				if (opponent[5].equals("0")) {
					dr2 = (BitmapDrawable) getResources().getDrawable(
							R.drawable.btn_scissor);
				} else if (opponent[5].equals("1")) {
					dr2 = (BitmapDrawable) getResources().getDrawable(
							R.drawable.btn_rock);
				} else {
					dr2 = (BitmapDrawable) getResources().getDrawable(
							R.drawable.btn_paper);
				}
				iv2.setImageDrawable(dr2);

				builder.setView(dialogView);
				builder.setCancelable(true);

				builder.setPositiveButton("확인",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {

								MiniGame mg = new MiniGame();
								final String re = mg.compare(item, opponent[5]);

								ws.gameResult(uid, youruid, re); // 확인과 함께 결과를
																	// 상대방에게도
																	// 보내준다.
								dialog.cancel();
								AlertDialog.Builder builder2 = new AlertDialog.Builder(
										GameActivity.this);
								builder2.setTitle("게임창");

								if (re.equals("이김")) {
									builder2.setMessage("당신은 이겼습니다.");
									builder2.setCancelable(true);
									builder2.setPositiveButton(
											"확인",
											new DialogInterface.OnClickListener() {
												public void onClick(
														DialogInterface dialog,
														int whichButton) {

													dialog.cancel();
												}
											});

									builder2.setNegativeButton(
											"아이템바꾸기",
											new DialogInterface.OnClickListener() {
												public void onClick(
														DialogInterface dialog,
														int whichButton) {
													item = opponent[5];
													// ws.gameResult(uid,youruid,re);
													dialog.cancel();
												}
											});

									builder2.show();
								} else if (re.equals("졌슴")) {
									builder2.setMessage("당신은 졌습니다.");
									builder2.setCancelable(true);

									builder2.setNegativeButton(
											"나가기",
											new DialogInterface.OnClickListener() {
												public void onClick(
														DialogInterface dialog,
														int whichButton) {
													ws.gameOut(uid);
													android.os.Process
															.killProcess(android.os.Process
																	.myPid());
													dialog.cancel();
												}
											});

									builder2.show();
								} else {
									builder2.setMessage("당신은 비겼습니다.");
									builder2.setCancelable(true);
									builder2.setPositiveButton(
											"확인",
											new DialogInterface.OnClickListener() {
												public void onClick(
														DialogInterface dialog,
														int whichButton) {
													// ws.gameResult(uid,youruid,re);
													dialog.cancel();
												}
											});

									builder2.show();
								}
							}
						});

				resultdialog = builder.create();
				resultdialog.show();
				dHandler.sendEmptyMessageDelayed(4, 5000);
				break;

			case 2: // 게임을 신청한 디바이스에 결과가 나온 후에 띄워지는 다이얼로그 ( 게임창 화면)--> 결과를 받은후
					// 다이얼로그
				final String[] opponent1 = participant.search(resultyouruid);
				vib.vibrate(5000);
				builder.setTitle("결과");

				if (item.equals("0")) {
					dr1 = (BitmapDrawable) getResources().getDrawable(
							R.drawable.btn_scissor);
				} else if (item.equals("1")) {
					dr1 = (BitmapDrawable) getResources().getDrawable(
							R.drawable.btn_rock);
				} else {
					dr1 = (BitmapDrawable) getResources().getDrawable(
							R.drawable.btn_paper);
				}
				iv1.setImageDrawable(dr1);

				if (opponent1[5].equals("0")) {
					dr2 = (BitmapDrawable) getResources().getDrawable(
							R.drawable.btn_scissor);
				} else if (opponent1[5].equals("1")) {
					dr2 = (BitmapDrawable) getResources().getDrawable(
							R.drawable.btn_rock);
				} else {
					dr2 = (BitmapDrawable) getResources().getDrawable(
							R.drawable.btn_paper);
				}
				iv2.setImageDrawable(dr2);

				builder.setView(dialogView);
				builder.setCancelable(true);

				builder.setPositiveButton("결과확인",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {

								MiniGame mg = new MiniGame();
								final String re = mg
										.compare(item, opponent1[5]);

								dialog.cancel();
								AlertDialog.Builder builder2 = new AlertDialog.Builder(
										GameActivity.this);
								builder2.setTitle("게임창");

								if (re.equals("이김")) {

									builder2.setMessage("당신은 이겼습니다.");
									builder2.setCancelable(true);
									builder2.setPositiveButton(
											"확인",
											new DialogInterface.OnClickListener() {
												public void onClick(
														DialogInterface dialog,
														int whichButton) {
													dialog.cancel();
												}
											});

									builder2.setNegativeButton(
											"아이템바꾸기",
											new DialogInterface.OnClickListener() {
												public void onClick(
														DialogInterface dialog,
														int whichButton) {
													item = opponent1[5];

													dialog.cancel();
												}
											});

									builder2.show();
								} else if (re.equals("졌슴")) {
									builder2.setMessage("당신은 졌습니다.");
									builder2.setCancelable(true);

									builder2.setNegativeButton(
											"나가기",
											new DialogInterface.OnClickListener() {
												public void onClick(
														DialogInterface dialog,
														int whichButton) {
													ws.gameOut(uid);
													android.os.Process
															.killProcess(android.os.Process
																	.myPid());
													dialog.cancel();
												}
											});

									builder2.show();
								} else {
									builder2.setMessage("당신은 비겼습니다.");
									builder2.setCancelable(true);
									builder2.setPositiveButton(
											"확인",
											new DialogInterface.OnClickListener() {
												public void onClick(
														DialogInterface dialog,
														int whichButton) {

													dialog.cancel();
												}
											});

									builder2.show();
								}
							}
						});

				builder.show();

				break;

			case 3: // 게임을 신청한입장에서 뜨는 다이어얼로그
				builder.setTitle("미니게임");
				builder.setMessage("거절당했습니다.");
				builder.setCancelable(true); // 뒤로 버튼 클릭시 취소 가능 설정

				builder.setPositiveButton("예",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {

								dialog.cancel();
							}
						});

				rejectdialog = builder.create();
				rejectdialog.show();
				dHandler.sendEmptyMessageDelayed(1, 5000); // 시간지나면 자동종료
				break;

			case 4: // 게임을 신청한입장에서 뜨는 다이어얼로그
				okNum++; // 게임을 신청한 입장에서 승인을 받으면 게임 카운트 1증가
				builder.setTitle("미니게임");
				builder.setMessage("승인하였습니다.기다려주세요.");
				builder.setCancelable(true); // 뒤로 버튼 클릭시 취소 가능 설정

				builder.setPositiveButton("예",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {

								dialog.cancel();
							}
						});
				okdialog = builder.create();
				okdialog.show();
				dHandler.sendEmptyMessageDelayed(2, 5000); // 시간지나면 자동종료
				break;

			case 5: // 게임을 신청한입장에서 뜨는 다이어얼로그
				vib.vibrate(5000);// 진동
				builder.setTitle("미니게임");
				builder.setMessage("게임에 승리하였습니다.");
				builder.setCancelable(true); // 뒤로 버튼 클릭시 취소 가능 설정

				builder.setPositiveButton("예",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								ws.gameOut(uid);
								android.os.Process
										.killProcess(android.os.Process
												.myPid());
								dialog.cancel();
							}
						});
				builder.show();

				break;

			}

			super.handleMessage(msg);
		}
	};

}