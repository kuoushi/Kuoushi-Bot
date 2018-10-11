package com.github.birdgeek.breadbot.utility;

import org.json.simple.JSONObject;

import java.util.List;
import java.util.ArrayList;

import org.json.simple.JSONArray;

public class HLDSService extends Service {
	
	List<Server> servers;

	public HLDSService(JSONObject user, JSONObject settings) {
		super(user, settings);
		JSONArray j = (JSONArray)settings.get("servers");
		servers = new ArrayList<Server>();
		if(j != null) {
			for(Object k : j.toArray()) {
				servers.add(new Server((JSONObject)k));
			}
		}
	}

	public List<Server> getServers() {
		return servers;
	}
	
	public String toString() {
		String t = super.toString();
		return t + servers.toString();
	}
}
