package com.github.birdgeek.breadbot.utility;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Server {

	private String address;
	private String type;
	private String rcon;
	private List<Channel> channels;
	private List<String> relayGames;
	private boolean relay;
	
	public Server(JSONObject j) {
		address = Service.makeString(j.get("ip"));
		type = Service.makeString(j.get("type"));
		rcon = Service.makeString(j.get("rcon"));
		relay = Service.makeBoolean(j.get("relay"));
		relayGames    = Service.makeList((JSONArray)j.get("relay-games"));
		
		channels = new ArrayList<Channel>();
		JSONArray relayChannels = (JSONArray)j.get("relay-channels");
		for(Object chan : relayChannels) {
			Channel c = new Channel();
			c.updateName(Service.makeString(((JSONObject)chan).get("channel")));
			c.updateService(Service.makeString(((JSONObject)chan).get("service")));
			channels.add(c);
		}
	}
	
	public String getServerAddress() {
		return address;
	}
	
	public String getServerType() {
		return type;
	}
	
	public String getServerRcon() {
		return rcon;
	}
	
	public boolean isRelayEnabled() {
		return relay;
	}
	
	public boolean isRelayGame(String game) {
		for(String g : relayGames) {
			if(g.equalsIgnoreCase(game)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean shouldRelayChannel(String name, String service, String game) {
		for(Channel c : channels) {
			if(c.getName().equalsIgnoreCase(name) && c.getService().equalsIgnoreCase(service) && isRelayGame(game)) {
				return true;
			}
		}
		return false;
	}
	
	
	public String toString() {
		return "Server Address: " + address + "\nServer Type: " + type + "\nServer RCON: " + rcon;
	}
}
