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
	protected String defaultDiscordAnnounceChannel;
	protected boolean enable;
	
	public Service() {
		
	}
	
	public Service(JSONObject user, JSONObject settings) {
		username = makeString(user.get("username"));
		password = makeString(user.get("password"));
		if(user.containsKey("oauth")) {
			password = makeString(user.get("oauth"));
		}
		defaultDiscordRelayChannel = makeString(settings.get("default-discord-relay-channel-id"));
		defaultDiscordAnnounceChannel = makeString(settings.get("default-discord-announce-channel-id"));
		enable = makeBoolean(settings.get("enable"));
		
		moderators = makeList((JSONArray)settings.get("moderators"));
		ignoredUsers = makeList((JSONArray)settings.get("ignored-users"));
	}
	
	protected static List<String> makeList(JSONArray j) {
		List<String> r = new ArrayList<String>();
		if(j == null)
			return r;
		
		for(Object o : j.toArray()) {
			r.add((String)o);
		}
		return r;
	}
	
	protected static String makeString(Object j) {
		if(j != null)
			return (String)j;
		return "";
	}
	
	protected static boolean makeBoolean(Object j) {
		if(j != null) {
			if(j.getClass().getName().equalsIgnoreCase("java.lang.Boolean"))
				return ((Boolean)j).booleanValue();
			else
				return ((String)j).equalsIgnoreCase("true");
		}
		return false;
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
	
	public String getDefaultDiscordAnnounceChannel() {
		return defaultDiscordAnnounceChannel;
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
	
	public String toString() {
		return "Name: " + username + "\nPassword: " + password + "\nModerators: " + moderators.toString() + "\nIgnored Users: " + ignoredUsers.toString() + "\nDefault Discord Relay Channel: " + defaultDiscordRelayChannel + "\nDefault Discord Announce Channel: " + defaultDiscordAnnounceChannel + "\nEnable: " + enable;
	}
}
