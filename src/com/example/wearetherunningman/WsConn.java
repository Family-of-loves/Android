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
    		json.put("uid", p.id);
		    json.put("username", p.name);
		    json.put("team",p.team);
            json.put("latitude", p.latitude);
            json.put("longitude", p.longitude);
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        socket.emit("message", json);
    }
    
    // 방 참가 메소드 
    public void emitJoin(String roomId, String userName ){
    	try {
            JSONObject json = new JSONObject();
            json.put("roomid", roomId);
            json.put("username", userName);
            socket.emit("join", callback, json);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}