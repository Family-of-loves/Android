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
	Vibrator vib;// ����ȿ��
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

	String youruid; // "�̴ϰ���" �̺�Ʈ�� �޾����� ������ �ǵ���̽��� uid�� �����ϰٴ�. -- ���������� �������� ��
					// ���� ����Ѵ�.
	String resultyouruid;// "res�̴ϰ���" �̺�Ʈ�� �޾����� ���������� ���� ����̽��� uid�� �����Ѵ�.(����
							// �ɾ���, �ٸ������ ����)
							// --> ���� ���� ������ �ɾ��� , ��뿡 ���� ������� ���������� ���� �ٽ� �ްԵǰ�
							// �� ����� uid�� �����ϰԵȴ�.

	AlertDialog mydialog;// ���ӽ�û�ڰ� ���ӽ�û�� �ڽ��� �����۸� ����ִ� ���̾�α�// �ڵ������
	AlertDialog rejectdialog;// ������ �ߴ� ���̾�α�
	AlertDialog okdialog;// ���ν� �ߴ� ���̾�α�
	AlertDialog startdialog;// ����������� ������ �����ϰٳİ� ���� ���̾�α�
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

		Intent intent = getIntent(); // ���� �޾ƿ´�.
		room = intent.getExtras().getString("param1");
		name = intent.getExtras().getString("param2");
		team = intent.getExtras().getString("param3");
		item = intent.getExtras().getString("param4");
		uid = Secure
				.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

		TextView nameview = (TextView) findViewById(R.id.viewUserName);
		nameview.setText(name + "��");
		player = new Player(uid, name, team, item, getApplicationContext(),
				gmap);
		// participant = new Participant(getApplicationContext(), gmap);
		participant = new Participant(team, getApplicationContext(), gmap,
				handler, player);
		// �����̵��޴� �κ� �ʱ�ȭ
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
				builder2.setTitle("����");
				builder2.setMessage("�����Ͻðڽ��ϱ�?");
				builder2.setCancelable(true);
				builder2.setPositiveButton("Ȯ��",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								android.os.Process
										.killProcess(android.os.Process.myPid());
								dialog.cancel();
							}
						});

				builder2.setNegativeButton("�ƴϿ�",
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

								okGame.setText(okNum + "ȸ");
								rejectGame.setText(rejectNum + "ȸ/5ȸ");

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
									gameOver += 1; // ����� ���� 0�� ä�� ���ӵǸ� ����

								if (gameOver == 7)
									ghandler.sendEmptyMessage(5); // ������ �̰�ٰ� �˸�

								if (otherTeamCount == 1 && myTeamCount == 0) // ���� ����� �Ѹ��� ������ ���
																				
									oneTOone += 1;

								if (oneTOone == 7) {
									if (item.equals(st[5])) {
										vib.vibrate(3000);// ����
										Random r = new Random();
										item = Integer.toString(r.nextInt(2 - 0 + 1) + 0);
										
									}
								}
								myTeamNum.setText(myTeamCount + "��");
								otherTeamNum.setText(otherTeamCount + "��");
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

	// �ڷΰ��� ��ư
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			new AlertDialog.Builder(this)
					.setTitle("����")
					.setMessage("���� �Ͻðھ��?")
					.setPositiveButton("��",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									android.os.Process
											.killProcess(android.os.Process
													.myPid());
								}
							}).setNegativeButton("�ƴϿ�", null).show();
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

		} else if (event.equals("minigame")) { // ���ӽ�û�� ���� ����� �����ϴ� �κ�(��ΰ� �ް�����)
			String myuid = null;
			try {
				myuid = obj.getString("desUid"); // ������ ��û �������� ������ uid�� �ڽ���uid��
													// ����
				youruid = obj.getString("uid"); // ���������uid�� ���uid�� ���� // ���߿�
												// �̰��� ����ϰ� �ȴ�.
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (uid.equals(myuid)) { // �� uid�� ���ؼ� �´ٸ�..
				ghandler.sendEmptyMessage(0); // ������ �����ϰٳĴ� ���̾�α׸� ����ش�.
			}

		} else if (event.equals("resMinigame")) { // ������ ������ ����� ����
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

			if (uid.equals(resultmyuid)) { // ���� ����� ���ؼ� �� uid�� ��ġ�Ѵٸ�
				if (answer.equals("����"))
					ghandler.sendEmptyMessage(3);// ���� ���̾�α�
				else if (answer.equals("����"))
					// ghandler.sendEmptyMessage(2);
					ghandler.sendEmptyMessage(4);// ���� ���̾�α�
				else
					// re�� ����,���� �ܿ� �̱�,����,��� �� ���
					ghandler.sendEmptyMessage(2); // ����� �������� ����â ���̾�α׸� ����.
													// (������,��빫�� �ٺ���)
			}
		} else {
			// ����ó��
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

	// ���̾�α� �ڵ����Ḧ ���� �ڵ鷯
	private Handler dHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0: // �����⸸ �ߴ� ����â �κп� ���� �ڵ�����

				if (mydialog != null && mydialog.isShowing()) {
					mydialog.dismiss();
				}
				sendEmptyMessageDelayed(MSG_ONLY_DISMISS, 2000);
				break;

			case 1: // ���� �κп� ���� �ڵ�����

				if (rejectdialog != null && rejectdialog.isShowing()) {
					rejectdialog.dismiss();
				}
				sendEmptyMessageDelayed(MSG_ONLY_DISMISS, 2000);
				break;

			case 2: // ���ο� ���� �ڵ�����

				if (okdialog != null && okdialog.isShowing()) {
					okdialog.dismiss();
				}
				sendEmptyMessageDelayed(MSG_ONLY_DISMISS, 2000);
				break;

			case 3: // ������ ���� ���忡�� ������ �����ϰٳĿ� ���� �ǻ簡 ������ �������� �޾� ���̰� �ڵ�������.

				if (startdialog != null && startdialog.isShowing()) {
					startdialog.dismiss();
					okNum++;
					String re = "����";
					ws.gameResult(uid, youruid, re);
					ghandler.sendEmptyMessage(1);
				}
				sendEmptyMessageDelayed(MSG_ONLY_DISMISS, 2000);
				break;

			case 4: // ������ ���� ���忡�� ��� Ȯ���� �������� ������ ���� ��û�ڰ� ����� Ȯ������ ���ϴ°� ������

				if (resultdialog != null && resultdialog.isShowing()) {
					resultdialog.dismiss();

					final String[] opponent = participant.search(youruid);

					MiniGame mg = new MiniGame();
					final String re = mg.compare(item, opponent[5]);

					ws.gameResult(uid, youruid, re); // Ȯ�ΰ� �Բ� ����� ���濡�Ե� �����ش�.

					AlertDialog.Builder builder2 = new AlertDialog.Builder(
							GameActivity.this);
					builder2.setTitle("����â");

					if (re.equals("�̱�")) {
						builder2.setMessage("����� �̰���ϴ�.");
						builder2.setCancelable(true);
						builder2.setPositiveButton("Ȯ��",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int whichButton) {

										dialog.cancel();
									}
								});

						builder2.setNegativeButton("�����۹ٲٱ�",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int whichButton) {
										item = opponent[5];
										// ws.gameResult(uid,youruid,re);
										dialog.cancel();
									}
								});

						builder2.show();
					} else if (re.equals("����")) {
						builder2.setMessage("����� �����ϴ�.");
						builder2.setCancelable(true);

						builder2.setNegativeButton("������",
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
						builder2.setMessage("����� �����ϴ�.");
						builder2.setCancelable(true);
						builder2.setPositiveButton("Ȯ��",
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

	// ��Ŀ�� ������ ���̾�α׸� ���� ���� ���鷯
	// ��Ŀ�� �� ������ ��û�ϴ� ���忡�� ����Ѵ�.
	public Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			AlertDialog.Builder builder = new AlertDialog.Builder(
					GameActivity.this);

			builder.setTitle("�̴ϰ���");
			builder.setMessage("���� �Ͻðٽ��ϱ�?");
			builder.setCancelable(true); // �ڷ� ��ư Ŭ���� ��� ���� ����
			final String matchuid = msg.getData().getString("data"); // ��Ŀ�κ��� ����
																		// uid����
																		// �����Ѵ�.
			final String[] consort = participant.search(matchuid); // ��Ŀ�κ��� ����
																	// uid�� ����
																	// ��񿡼� ã�Ƽ�
																	// �迭�� ����

			builder.setPositiveButton("��",
					new DialogInterface.OnClickListener() {
						@SuppressWarnings("deprecation")
						public void onClick(DialogInterface dialog,
								int whichButton) {

							ws.gameStart(uid, consort[0]); // ���� uid�� �����uid��
															// ������ ����
							AlertDialog.Builder mybuilder = new AlertDialog.Builder(
									GameActivity.this);

							LayoutInflater mLayoutInflater = GameActivity.this
									.getLayoutInflater();
							View dialogView = mLayoutInflater.inflate(
									R.layout.contest, null);
							ImageView iv1 = (ImageView) dialogView
									.findViewById(R.id.imageView1); // ������ �̹����信
																	// ǥ��
							BitmapDrawable dr1 = null;
							ImageView iv2 = (ImageView) dialogView
									.findViewById(R.id.imageView2); // ��빫��
																	// �̹����信
																	// ǥ��..
																	// null���̴�.
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

							iv2.setImageDrawable(dr2); // null���� �׳� ��������ν� �����
														// ����� �˼��� ���� �ȴ�.

							mybuilder.setView(dialogView);
							mybuilder.setCancelable(true);

							mybuilder.setNegativeButton("�������",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int whichButton) {

											dialog.cancel();
										}
									});
							mydialog = mybuilder.create();
							mydialog.show();
							dHandler.sendEmptyMessageDelayed(0, 3000); // �ð�������
																		// �ڵ�����
						}
					});

			builder.setNegativeButton("�ƴϿ�",
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

	// ���� ���̾�α׵��� ����
	public Handler ghandler = new Handler() {
		public void handleMessage(Message msg) {
			AlertDialog.Builder builder = new AlertDialog.Builder(
					GameActivity.this);

			LayoutInflater mLayoutInflater = GameActivity.this
					.getLayoutInflater();
			View dialogView = mLayoutInflater.inflate(R.layout.contest, null);
			ImageView iv1 = (ImageView) dialogView
					.findViewById(R.id.imageView1); // ������ �̹����信 ǥ��
			BitmapDrawable dr1 = null;
			ImageView iv2 = (ImageView) dialogView
					.findViewById(R.id.imageView2); // ��빫�� �̹����信 ǥ��
			BitmapDrawable dr2 = null;

			switch (msg.what) {
			case 0: // ������ �������忡�� �ߴ� ���̾��α�
				vib.vibrate(5000);// ����

				if (rejectNum == 5) {
					builder.setTitle("�̴ϰ���");
					builder.setMessage("���̻� �����Ҽ� �����ϴ�. ������ �����մϴ�.");
					startdialog = builder.create();
					startdialog.show();
					dHandler.sendEmptyMessageDelayed(3, 5000);
				} else {
					builder.setTitle("�̴ϰ���");
					builder.setMessage("�������� ������ ��û�߽��ϴ�. ���� �Ͻðٽ��ϱ�?");
					builder.setCancelable(true); // �ڷ� ��ư Ŭ���� ��� ���� ����

					builder.setPositiveButton("��",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									// ghandler.sendEmptyMessage(1);
									okNum++;
									String re = "����";
									ws.gameResult(uid, youruid, re);
									ghandler.sendEmptyMessage(1); // ����â ���̾�α�
																	// ���� �����
																	// ��빫�⸦
																	// �����ش�.
									dialog.cancel();
								}
							});

					builder.setNegativeButton("�ƴϿ�",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									rejectNum++;
									String re = "����";
									ws.gameResult(uid, youruid, re);
									dialog.cancel();
								}
							});
					startdialog = builder.create();
					startdialog.show();
					dHandler.sendEmptyMessageDelayed(3, 5000); // �ð������� �ڵ�����,,
																// ������ ���ϰԵȴ�.
				}
				break;

			case 1: // ������ ���� ���忡�� �����ϰٴٰ� ������ ���������� ����â ���̾�α׸� �ٿ� -->> ������ ��������
					// �������ϰٴٰ� �ϸ� ��� ����â ���̾�α�
				final String[] opponent = participant.search(youruid);
				builder.setTitle("����â");

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

				builder.setPositiveButton("Ȯ��",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {

								MiniGame mg = new MiniGame();
								final String re = mg.compare(item, opponent[5]);

								ws.gameResult(uid, youruid, re); // Ȯ�ΰ� �Բ� �����
																	// ���濡�Ե�
																	// �����ش�.
								dialog.cancel();
								AlertDialog.Builder builder2 = new AlertDialog.Builder(
										GameActivity.this);
								builder2.setTitle("����â");

								if (re.equals("�̱�")) {
									builder2.setMessage("����� �̰���ϴ�.");
									builder2.setCancelable(true);
									builder2.setPositiveButton(
											"Ȯ��",
											new DialogInterface.OnClickListener() {
												public void onClick(
														DialogInterface dialog,
														int whichButton) {

													dialog.cancel();
												}
											});

									builder2.setNegativeButton(
											"�����۹ٲٱ�",
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
								} else if (re.equals("����")) {
									builder2.setMessage("����� �����ϴ�.");
									builder2.setCancelable(true);

									builder2.setNegativeButton(
											"������",
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
									builder2.setMessage("����� �����ϴ�.");
									builder2.setCancelable(true);
									builder2.setPositiveButton(
											"Ȯ��",
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

			case 2: // ������ ��û�� ����̽��� ����� ���� �Ŀ� ������� ���̾�α� ( ����â ȭ��)--> ����� ������
					// ���̾�α�
				final String[] opponent1 = participant.search(resultyouruid);
				vib.vibrate(5000);
				builder.setTitle("���");

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

				builder.setPositiveButton("���Ȯ��",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {

								MiniGame mg = new MiniGame();
								final String re = mg
										.compare(item, opponent1[5]);

								dialog.cancel();
								AlertDialog.Builder builder2 = new AlertDialog.Builder(
										GameActivity.this);
								builder2.setTitle("����â");

								if (re.equals("�̱�")) {

									builder2.setMessage("����� �̰���ϴ�.");
									builder2.setCancelable(true);
									builder2.setPositiveButton(
											"Ȯ��",
											new DialogInterface.OnClickListener() {
												public void onClick(
														DialogInterface dialog,
														int whichButton) {
													dialog.cancel();
												}
											});

									builder2.setNegativeButton(
											"�����۹ٲٱ�",
											new DialogInterface.OnClickListener() {
												public void onClick(
														DialogInterface dialog,
														int whichButton) {
													item = opponent1[5];

													dialog.cancel();
												}
											});

									builder2.show();
								} else if (re.equals("����")) {
									builder2.setMessage("����� �����ϴ�.");
									builder2.setCancelable(true);

									builder2.setNegativeButton(
											"������",
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
									builder2.setMessage("����� �����ϴ�.");
									builder2.setCancelable(true);
									builder2.setPositiveButton(
											"Ȯ��",
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

			case 3: // ������ ��û�����忡�� �ߴ� ���̾��α�
				builder.setTitle("�̴ϰ���");
				builder.setMessage("�������߽��ϴ�.");
				builder.setCancelable(true); // �ڷ� ��ư Ŭ���� ��� ���� ����

				builder.setPositiveButton("��",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {

								dialog.cancel();
							}
						});

				rejectdialog = builder.create();
				rejectdialog.show();
				dHandler.sendEmptyMessageDelayed(1, 5000); // �ð������� �ڵ�����
				break;

			case 4: // ������ ��û�����忡�� �ߴ� ���̾��α�
				okNum++; // ������ ��û�� ���忡�� ������ ������ ���� ī��Ʈ 1����
				builder.setTitle("�̴ϰ���");
				builder.setMessage("�����Ͽ����ϴ�.��ٷ��ּ���.");
				builder.setCancelable(true); // �ڷ� ��ư Ŭ���� ��� ���� ����

				builder.setPositiveButton("��",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {

								dialog.cancel();
							}
						});
				okdialog = builder.create();
				okdialog.show();
				dHandler.sendEmptyMessageDelayed(2, 5000); // �ð������� �ڵ�����
				break;

			case 5: // ������ ��û�����忡�� �ߴ� ���̾��α�
				vib.vibrate(5000);// ����
				builder.setTitle("�̴ϰ���");
				builder.setMessage("���ӿ� �¸��Ͽ����ϴ�.");
				builder.setCancelable(true); // �ڷ� ��ư Ŭ���� ��� ���� ����

				builder.setPositiveButton("��",
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
