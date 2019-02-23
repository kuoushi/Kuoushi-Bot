package com.github.birdgeek.breadbot.utility;

import org.json.simple.JSONObject;

public class Channel {
	private String name;
	private String displayName;
	private String service;
	private boolean relay;
	private boolean announce;
	private boolean commands;
	private boolean imageRepeat;
	private String relayChannel;
	private String announceChannel;
	private String currentStatus;
	private String currentGame;
	private int currentViewers;
	private String url;
	private boolean online;
	private String discordLink;
	
	public Channel() {
		name = "kuoushi";
		service = "twitch";
		relay = true;
		announce = true;
		commands = true;
		relayChannel = "";
		announceChannel = "";
		imageRepeat = false;
		online = false;
	}
	
	public Channel(JSONObject j) {
		name = (String)j.get("username");
		service = (String)j.get("service");
		relay = (boolean)j.get("relay");
		announce = (boolean)j.get("announce");
		commands = true;
		if(j.containsKey("commands")) {
			commands = (boolean)j.get("commands");
		}
		if(service.equals("hitbox") && j.containsKey("image-repeat")) {
			imageRepeat = (boolean)j.get("image-repeat");
		}
		else {
			imageRepeat = false;
		}
		if(j.containsKey("relay-channel")) {
			relayChannel = (String)j.get("relay-channel");
		}
		if(j.containsKey("announce-channel")) {
			announceChannel = (String)j.get("announce-channel");
		}
		if(j.containsKey("discordLink")) {
			discordLink = (String)j.get("discordLink");
		}
		else {
			discordLink = ConfigFile.getDiscordInviteLink();
		}
		online = false;
		currentStatus = "";
		currentGame = "";
		currentViewers = 0;
		url = "";
	}
	
	public Channel(String n, String serv, boolean rel, boolean ann, String relC, String annC) {
		name = n;
		service = serv;
		relay = rel;
		announce = ann;
		relayChannel = relC;
		announceChannel = annC;
		imageRepeat = false;
		online = false;
		currentStatus = "";
		currentGame = "";
		currentViewers = 0;
		url = "";
	}
	
	public Channel(String n, String serv, boolean rel, boolean ann, String relC, String annC, boolean imageR) {
		name = n;
		service = serv;
		relay = rel;
		announce = ann;
		relayChannel = relC;
		announceChannel = annC;
		imageRepeat = imageR;
		online = false;
		currentStatus = "";
		currentGame = "";
		currentViewers = 0;
		url = "";
	}
	
	public void updateName(String n) {
		name = n;
	}
	
	public void updateGame(String n) {
		currentGame = n;
	}
	
	public void updateURL(String n) {
		url = n;
	}
	
	public void updateViewers(int n) {
		currentViewers = n;
	}
	
	public void updateService(String n) {
		service = n;
	}
	
	public void updateRelayChannel(String n) {
		relayChannel = n;
	}
	
	public void updateAnnounceChannel(String n) {
		announceChannel = n;
	}
	
	public void updateDisplayName(String n) {
		displayName = n;
	}
	
	public void updateCurrentStatus(String n) {
		currentStatus = n;
	}
	
	public void updateAnnounce(boolean n) {
		announce = n;
	}
	
	public void updateRelay(boolean n) {
		relay = n;
	}
	
	public void updateImageRepeat(boolean n) {
		imageRepeat = n;
	}
	
	public void updateOnline(boolean n) {
		online = n;
	}
	
	public String getName() {
		return name;
	}
	
	public String getGame() {
		return currentGame;
	}

	public String getUrl() {
		return url;
	}

	public int getViewers() {
		return currentViewers;
	}
	
	public String getDisplayName() {
		return displayName;
	}
	
	public String getCurrentStatus() {
		return currentStatus;
	}
	
	public String getService() {
		return service;
	}
	
	public String getRelayChannel() {
		return relayChannel;
	}
	
	public String getAnnounceChannel() {
		return announceChannel;
	}
	
	public String getDiscordLink() {
		return discordLink;
	}
	
	public boolean getRelay() {
		return relay;
	}
	
	public boolean getAnnounce() {
		return announce;
	}
	
	public boolean getImageRepeat() {
		return imageRepeat;
	}
	
	public boolean getCommands() {
		return commands;
	}
	
	public boolean isOnline() {
		return online;
	}
	
	public String toString() {
		return "Name: " + name + "\nService: " + service + "\nRelay: " + relay + "\nAnnounce: " + announce + "\nRelay Channel: " + relayChannel + "\nAnnounce Channel: " + announceChannel + "\nImage Repeat: " + imageRepeat;
	}
	
}
