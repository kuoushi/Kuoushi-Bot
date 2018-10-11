package com.github.birdgeek.breadbot.utility;

import org.json.simple.JSONObject;

public class Channel {
	private String name;
	private String service;
	private boolean relay;
	private boolean announce;
	private boolean imageRepeat;
	private String relayChannel;
	private String announceChannel;
	
	public Channel() {
		name = "kuoushi";
		service = "twitch";
		relay = true;
		announce = true;
		relayChannel = "";
		announceChannel = "";
		imageRepeat = false;
	}
	
	public Channel(JSONObject j) {
		name = (String)j.get("username");
		service = (String)j.get("service");
		relay = (boolean)j.get("relay");
		announce = (boolean)j.get("announce");
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
	}
	
	public Channel(String n, String serv, boolean rel, boolean ann, String relC, String annC) {
		name = n;
		service = serv;
		relay = rel;
		announce = ann;
		relayChannel = relC;
		announceChannel = annC;
		imageRepeat = false;
	}
	
	public Channel(String n, String serv, boolean rel, boolean ann, String relC, String annC, boolean imageR) {
		name = n;
		service = serv;
		relay = rel;
		announce = ann;
		relayChannel = relC;
		announceChannel = annC;
		imageRepeat = imageR;
	}
	
	public void updateName(String n) {
		name = n;
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
	
	public void updateAnnounce(boolean n) {
		announce = n;
	}
	
	public void updateRelay(boolean n) {
		relay = n;
	}
	
	public void updateImageRepeat(boolean n) {
		imageRepeat = n;
	}
	
	public String getName() {
		return name;
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
	
	public boolean getRelay() {
		return relay;
	}
	
	public boolean getAnnounce() {
		return announce;
	}
	
	public boolean getImageRepeat() {
		return imageRepeat;
	}
	
}
