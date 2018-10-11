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
		token = (String)user.get("token");
		serverId = (String)settings.get("server-id");
		ownerId = (String)settings.get("owner-id");
		channelId = (String)settings.get("home-channel-id");
		sendWelcome = (boolean)settings.get("send-welcome");
		enableDelete = (boolean)settings.get("enable-delete");
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
}
