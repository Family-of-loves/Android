package com.example.wearetherunningman;

import java.util.Arrays;

import io.socket.IOAcknowledge;
import io.socket.IOCallback;
import io.socket.SocketIOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class WsCallback  implements IOCallback, IOAcknowledge {
    
	OtherPlayerSave ops=new OtherPlayerSave();	
	private WsCallbackInterface callback;
    
    public WsCallback(WsCallbackInterface callback) {
        this.callback = callback;
    }

	@Override
	public void ack(Object... data) {
      /*  try {
		//	callback.callback(new JSONArray(Arrays.asList(data)));
		} catch (JSONException e) {
			e.printStackTrace();
		}*/
    }

	@Override
    public void on(String event, IOAcknowledge ack, Object... args) {
		if ("message".equals(event) && args.length > 0) {			
			Object[] recv = args;
			String jt = recv[0].toString();
			JSONObject jsondata = null;
						
			try {
				jsondata = new JSONObject(jt);
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			ops.compare(jsondata);
        }
    }
	
    @Override
    public void onMessage(JSONObject json, IOAcknowledge ack) {
    	 try {
                System.out.println("Server said:" + json.toString(2));
            } catch (JSONException e) {
                e.printStackTrace();
            }
    }
    @Override
    public void onMessage(String data, IOAcknowledge ack) {
    	System.out.println("Server said: " + data);
    }
    @Override
    public void onError(SocketIOException socketIOException) {
    	System.out.println("an Error occured");
        socketIOException.printStackTrace();
    }
    @Override
    public void onDisconnect() {
    	System.out.println("Connection terminated.");
    }
    @Override
    public void onConnect() {
    	System.out.println("Connection established");
    }
}
