package com.github.birdgeek.breadbot.utility;

import org.json.simple.JSONObject;

public class Server {

	private String address;
	private String type;
	private String rcon;
	
	public Server(JSONObject j) {
		address = Service.makeString(j.get("ip"));
		type = Service.makeString(j.get("type"));
		rcon = Service.makeString(j.get("rcon"));
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
	
	public String toString() {
		return "Server Address: " + address + "\nServer Type: " + type + "\nServer RCON: " + rcon;
	}
}
