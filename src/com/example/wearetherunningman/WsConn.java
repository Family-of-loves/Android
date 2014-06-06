package com.example.wearetherunningman;

import io.socket.SocketIO;

import java.net.MalformedURLException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Application;

public class WsConn extends Application {

	private SocketIO socket;
    private WsCallback callback;
           
	public WsConn(WsCallbackInterface callback){
		this.callback = new WsCallback(callback);
	}
    public void run(String URL) {
    	try {
			socket = new SocketIO(URL, callback);

		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
    }
    // 메시지 전달
    public void emitMessage(Player p) {
    	JSONObject json = new JSONObject();
    	try {
    		json.put("uid", p.uid);
		    json.put("name", p.name);
		    json.put("team",p.team);
            json.put("latitude", p.latitude);
            json.put("longitude", p.longitude);
            json.put("item", p.item);
            json.put("flag", p.flag);
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        socket.emit("message", json);
    }
    // 방 참가 메소드 
    public void emitJoin(String roomId, Player p ){
    	try {
            JSONObject json = new JSONObject();
            json.put("roomid", roomId);
            json.put("uid", p.uid);
		    json.put("name", p.name);
		    json.put("team",p.team);
            json.put("latitude", p.latitude);
            json.put("longitude", p.longitude);
            json.put("item", p.item);
            json.put("flag", p.flag);
            
            socket.emit("join", callback, json);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public void gameResult(String uid, String destUid,String result ){
    	try {
            JSONObject json = new JSONObject();
            json.put("uid", uid);
            json.put("desUid", destUid);
		    json.put("result", result);            
            socket.emit("resMinigame", callback, json);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
     
    //게임을 하자고 메세지를 보냄
    public void gameStart(String uid, String destUid ){
    	try {
            JSONObject json = new JSONObject();
            json.put("uid", uid);
            json.put("desUid", destUid);
		                
            socket.emit("minigame", callback, json);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public void gameOut(String uid, String name){
    	try {
            JSONObject json = new JSONObject();
            json.put("uid", uid);
            json.put("name", name);
            		                
            socket.emit("leave", callback, json);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}