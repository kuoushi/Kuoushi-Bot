package com.github.birdgeek.breadbot.utility;


import java.io.FileReader;
import java.util.List;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class ConfigFile {
	static String filename = "config.cfg"; //TODO Set this different for releases
	private static List<Channel> channels;
	private static Service twitch;
	private static Service hitbox;
	private static DiscordService discord;
	private static HLDSService servers;
	private static String version;
	
	public ConfigFile ()  {
		JSONParser parser = new JSONParser();
		
		JSONObject jsconfig = new JSONObject();
		
		try {
			jsconfig = (JSONObject) parser.parse(new FileReader("config.json"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		version = (String) jsconfig.get("version");
		if(version == null)
			version = "0.0.0";
		
		twitch = new Service((JSONObject)((JSONObject)jsconfig.get("login")).get("twitch"),(JSONObject)jsconfig.get("twitch-settings"));
		hitbox = new Service((JSONObject)((JSONObject)jsconfig.get("login")).get("hitbox"),(JSONObject)jsconfig.get("hitbox-settings"));
		discord = new DiscordService((JSONObject)((JSONObject)jsconfig.get("login")).get("discord"),(JSONObject)jsconfig.get("discord-settings"));
		servers = new HLDSService(new JSONObject(),(JSONObject)jsconfig.get("hlds-settings"));
		
		JSONArray temp = (JSONArray)jsconfig.get("channels");
		channels = new ArrayList<Channel>();
		for(int i = 0; i < temp.size(); i++) {
			JSONObject j = (JSONObject)temp.get(i);
			channels.add(i,new Channel(j));
			if(!j.containsKey("relay-channel")) {
				if(channels.get(i).getService().equals("twitch")) {
					channels.get(i).updateRelayChannel(twitch.getDefaultDiscordRelayChannel());
				}
				else if(channels.get(i).getService().equals("hitbox")) {
					channels.get(i).updateRelayChannel(hitbox.getDefaultDiscordRelayChannel());
				}
				else if(channels.get(i).getService().equals("hlds")) {
					channels.get(i).updateRelayChannel(servers.getDefaultDiscordRelayChannel());
				}
			}
			if(!j.containsKey("announce-channel")) {
				if(channels.get(i).getService().equals("twitch")) {
					channels.get(i).updateAnnounceChannel(twitch.getDefaultDiscordAnnounceChannel());
				}
				else if(channels.get(i).getService().equals("hitbox")) {
					channels.get(i).updateAnnounceChannel(hitbox.getDefaultDiscordAnnounceChannel());
				}
				else if(channels.get(i).getService().equals("hlds")) {
					channels.get(i).updateAnnounceChannel(servers.getDefaultDiscordAnnounceChannel());
				}
			}
		}
	}
	
	/*
	 * String Arrays
	 */
	public static String[] getApprovedUsers() {
		String[] build = new String[discord.getModerators().size()];
		for(int i = 0; i < discord.getModerators().size(); i++) {
			build[i] = discord.getModerators().get(i);
		}
		return build;
	}
	
	public static String[] getApprovedIRCUsers() {
		String[] build = new String[twitch.getModerators().size()];
		for(int i = 0; i < twitch.getModerators().size(); i++) {
			build[i] = twitch.getModerators().get(i);
		}
		return build;
	}
	
	public static String[] getIgnoredIrcUsers() {
		String[] build = new String[twitch.getIgnoredUsers().size()];
		for(int i = 0; i < twitch.getIgnoredUsers().size(); i++) {
			build[i] = twitch.getIgnoredUsers().get(i);
		}
		return build;
	}
	public static String getHitboxRepeaters() {
		String build = "";
		for(Channel a : channels) {
			if(a.getImageRepeat() == true)
				build += a.getName() + ",";
		}
		if(build.length() > 0)
			build = build.substring(0, build.length() - 1);
		
		return build;
	}
	/*
	 * Strings
	 */
	public static String getEmail() {
		return discord.getUsername();
	}
	
	public static String getPassword() {
		return discord.getPassword();
	}
	public static String getBotToken() {
		return discord.getToken();
	}
	
	public static String getVersion() {
		return version;
	}
	
	public static String getTwitchChannel() {
		String build = "";
		for(Channel a : channels) {
			if(a.getService().equals("twitch"))
				build += a.getName() + ",";
		}
		if(build.length() > 0)
			build = build.substring(0, build.length() - 1);
		
		return build;
	}
	
	public static String getHitboxChannel() {
		String build = "";
		for(Channel a : channels) {
			if(a.getService().equals("hitbox"))
				build += a.getName() + ",";
		}
		if(build.length() > 0)
			build = build.substring(0, build.length() - 1);
		
		return build;
	}
	
	public static String getOAuth() {
		return twitch.getPassword();
	}
	
	public static String getTwitchLoginUser() {
		return twitch.getUsername();
	}
	
	public static String getHitboxLoginUser() {
		return hitbox.getUsername();
	}
	public static String getHitboxLoginPass() {
		return hitbox.getPassword();
	}
	
	/*
	 * Booleans
	 */
	public static boolean shouldEnableTwitch() {
		return twitch.isEnabled();
	}
	
	public static boolean shouldDelete() {
		return discord.isDeleteEnabled();
	}
	
	public static boolean shouldSendWelcomeMention() {
		return discord.isWelcomeEnabled();
	}	
	public static boolean shouldIrcRelay() {
		return twitch.isEnabled();
	}
	
	/*
	 * Ints
	 */
	public static String getHomeGuild() {
		return discord.getServerId();
	}
	
	public static String getHomeChannel() {
		return discord.getChannelId();
	}	
	
	public static String getOwnerID() {
		return discord.getOwnerId();
	}
	
	public static String getTwitchDiscordChannelID() {
		return twitch.getDefaultDiscordRelayChannel();
	}
	
	public static List<Server> getServers() {
		return servers.getServers();
	}

}
