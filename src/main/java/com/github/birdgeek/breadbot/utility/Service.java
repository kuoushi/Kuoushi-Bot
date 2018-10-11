package com.github.birdgeek.breadbot.utility;

import java.util.List;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Service {
	protected String username;
	protected String password;
	protected List<String> moderators;
	protected List<String> ignoredUsers;
	protected String defaultDiscordRelayChannel;
	protected boolean enable;
	
	public Service() {
		
	}
	
	public Service(JSONObject user, JSONObject settings) {
		username = (String)user.get("username");
		if(user.containsKey("password")) {
			password = (String)user.get("password");
		}
		else if(user.containsKey("oauth")) {
			password = (String)user.get("oauth");
		}
		defaultDiscordRelayChannel = (String)settings.get("default-discord-relay-channel-id");
		
		enable = false;
		if(settings.containsKey("enable")) {
			enable = (boolean)settings.get("enable");
		}
		
		moderators = makeList((JSONArray)settings.get("moderators"));
		ignoredUsers = makeList((JSONArray)settings.get("ignored-users"));
	}
	
	protected List<String> makeList(JSONArray j) {
		List<String> r = new ArrayList<String>();
		if(j == null)
			return r;
		
		for(Object o : j.toArray()) {
			r.add((String)o);
		}
		return r;
	}
	
	public String getUsername() {
		return username;
	}
	
	public String getPassword() {
		return password;
	}
	
	public String getDefaultDiscordRelayChannel() {
		return defaultDiscordRelayChannel;
	}
	
	public boolean isEnabled() {
		return enable;
	}
	
	public List<String> getModerators() {
		return moderators;
	}
	
	public List<String> getIgnoredUsers() {
		return ignoredUsers;
	}
}
