package com.example.wearetherunningman;

import io.socket.SocketIO;

import java.net.MalformedURLException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Application;


/**
 * @author  JeongMyoungHak
 */
public class WsConn extends Application {

	private SocketIO socket;
    /**
	 */
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
    // �޽��� ����
    public void emitMessage(Player p) {
    	JSONObject json = new JSONObject();
    	try {
    		json.put("uid", p.uid);
		    json.put("name", p.name);
		    json.put("team",p.team);
            json.put("latitude", p.latitude);
            json.put("longitude", p.longitude);
            json.put("item", p.item);
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        socket.emit("message", json);
    }
    // �� ���� �޼ҵ� 
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
            
            socket.emit("join", callback, json);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}