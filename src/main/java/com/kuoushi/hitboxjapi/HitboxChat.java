package com.kuoushi.hitboxjapi;

import java.net.URI;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_10;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONObject;

public class HitboxChat extends WebSocketClient {

	protected String name;
	protected String pass;
	public Boolean isOnline = false;

	public HitboxChat(String name, String pass, String wsUrl) throws Exception {
		super(new URI(wsUrl), new Draft_10());
		this.name = name;
		this.pass = pass;
		connectBlocking();
	}

	@Override
	public void onClose(int code, String reason, boolean remote) {
		isOnline = false;
	}

	@Override
	public void onError(Exception e) {
		e.printStackTrace();
	}

	@Override
	public void onMessage(String message) {
		if (message.equals("2::")) {
            onPing();
        }
		else if (message.equals("1::")) {
			onConnectResponse();
		}
		else if (message.startsWith("5:::")){
			JSONObject json = new JSONObject(message.substring(4));

			JSONObject args = new JSONObject(json.getJSONArray("args").getString(0));
			JSONObject params = args.getJSONObject("params");
			String method = args.getString("method");
			
			switch(method) {
				case "chatMsg":		onChatMsg(params); break;
				case "loginMsg":	onLoginMsg(params); break;
				case "infoMsg":		onInfoMsg(params); break;
				case "serverMsg":	onServerMsg(params); break;
				case "motdMsg":		onMotdMsg(params); break;
				case "directMsg":	onDirectMsg(params); break;
				case "pollMsg":     onPollMsg(params); break;
				case "raffleMsg":   onRaffleMsg(params); break;
				case "mediaLog":	onMediaLog(params.getJSONArray("data")); break;
				case "chatLog":		onChatLog(params); break;
				default:            onUnexpectedMsg(params);
			}
        }
	}

	@Override
	public void onOpen(ServerHandshake handshakeData) {
//		System.out.println(handshakeData.getHttpStatus() + ": " + handshakeData.getHttpStatusMessage());
	}
	
	public void onPing() {
		this.send("2::");
	}
	
	public void onConnectResponse() {
		isOnline = true;
	}
	
	public void onChatMessage(String message) {
		
	}
	
	public void onChatMsg(JSONObject message) {
		
	}
	
	public void onLoginMsg(JSONObject message) {
		
	}
	
	public void onDirectMsg(JSONObject message) {
		
	}
	
	public void onInfoMsg(JSONObject message) {
		
	}

	public void onServerMsg(JSONObject message) {
		
	}
	
	public void onMotdMsg(JSONObject message) {
		
	}
	
	public void onPollMsg(JSONObject message) {
		
	}
	
	public void onRaffleMsg(JSONObject message) {
		
	}
	
	public void onUnexpectedMsg(JSONObject message) {
		
	}
	
	public void onMediaLog(JSONArray log) {
		
	}
	
	public void onChatLog(JSONObject log) {
		
	}
	
	public void joinChannel(String channel) {
        this.send("5:::{\"name\":\"message\",\"args\":[{\"method\":\"joinChannel\",\"params\":{\"channel\":\"" + channel + "\",\"name\":\"" + name + "\",\"token\":\"" + Hitbox.getToken(name, pass) + "\",\"hideBuffered\":true}}]}");
    }
	
	public void partChannel(String channel) {
        this.send("5:::{\"name\":\"message\",\"args\":[{\"method\":\"partChannel\",\"params\":{\"channel\":\"" + channel + "\",\"name\":\"" + name + "\",\"token\":\"" + Hitbox.getToken(name, pass) + "\",\"hideBuffered\":true}}]}");
    }
	
	public void sendMessage(String message, String channel) {
		this.send(JSONMessageBuilder(message,"chatMsg",channel));
//		this.send("5:::{\"name\":\"message\",\"args\":[{\"method\":\"chatMsg\",\"params\":{\"channel\":\"" + channel + "\",\"name\":\"" + name + "\",\"nameColor\":\"FA5858\",\"text\":\"" + message + "\"}}]}");
	}
	
	public void getMediaLog(String type, String channel) {
		this.send("5:::{\"name\":\"message\",\"args\":[{\"method\":\"mediaLog\",\"params\":{\"channel\":\"" + channel + "\",\"name\":\"" + name + "\",\"type\":\"" + type + "\",\"token\":\"" + Hitbox.getToken(name, pass) + "\"}}]}");
	}
	
	public String JSONMessageBuilder(String content, String method, String channel) {
		JSONObject top = new JSONObject();
		JSONObject args = new JSONObject();
		JSONObject params = new JSONObject();
		
		args.accumulate("method",method);

		params.accumulate("channel",channel);
		params.accumulate("name",this.name);
		params.accumulate("nameColor", "FA5858");
		params.accumulate("text",content);
		args.accumulate("params", params);

		top.accumulate("name", "message");
		top.append("args", args);
		
		return "5:::" + top.toString();
	}
}
