package com.github.birdgeek.breadbot.utility;

import org.json.simple.JSONObject;

public class DiscordService extends Service {
	protected String token;
	protected String serverId;
	protected String ownerId;
	protected String channelId;
	protected boolean sendWelcome;
	protected boolean enableDelete;
	
	public DiscordService(JSONObject user, JSONObject settings) {
		super(user,settings);
		token = makeString(user.get("token"));
		serverId = makeString(settings.get("server-id"));
		ownerId = makeString(settings.get("owner-id"));
		channelId = makeString(settings.get("home-channel-id"));
		sendWelcome = makeBoolean(settings.get("send-welcome"));
		enableDelete = makeBoolean(settings.get("enable-delete"));
	}
	
	public String getToken() {
		return token;
	}
	
	public String getOwnerId() {
		return ownerId;
	}
	
	public String getChannelId() {
		return channelId;
	}
	
	public String getServerId() {
		return serverId;
	}
	
	public boolean isWelcomeEnabled() {
		return sendWelcome;
	}
	
	public boolean isDeleteEnabled() {
		return enableDelete;
	}
	
	public String toString() {
		String temp = super.toString();
		return temp + "\nToken: " + token + "\nServer ID: " + serverId + "\nOwner ID: " + ownerId + "\nChannel ID: " + channelId + "\nSend Welcome: " + sendWelcome + "\nEnable Delete: " + enableDelete;
	}
}
