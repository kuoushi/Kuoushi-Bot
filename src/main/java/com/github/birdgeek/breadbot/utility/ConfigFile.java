package com.github.birdgeek.breadbot.utility;


import java.io.FileReader;
import java.util.List;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class ConfigFile {
	static String filename = "config.json"; //TODO Set this different for releases
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
			jsconfig = (JSONObject) parser.parse(new FileReader(filename));
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
	 * General Functions
	 */
	
	public static String getVersion() {
		return version;
	}
	
	private static Service getService(String s) {
		if(s.equalsIgnoreCase("discord"))
			return discord;
		else if (s.equalsIgnoreCase("twitch"))
			return twitch;
		else if(s.equalsIgnoreCase("hitbox"))
			return hitbox;
		else if(s.equalsIgnoreCase("hlds"))
			return servers;
		else
			return null;
	}
	
	public static String getUsername(String service) {
		return getService(service).getUsername();
	}
	
	public static String getPassword(String service) {
		return getService(service).getPassword();
	}
	
	public static String getRelayChannel(String service) {
		return getService(service).getDefaultDiscordRelayChannel();
	}
	
	public static String getAnnounceChannel(String service) {
		return getService(service).getDefaultDiscordAnnounceChannel();
	}
	
	public static String getAnnounceChannel(String channel, String service) {
		return getService(service).getDefaultDiscordAnnounceChannel();
	}
	
	public static boolean isServiceEnabled(String service) {
		return getService(service).isEnabled();
	}
	
	public static List<String> getModerators(String service) {
		return getService(service).getModerators();
	}
	
	public static List<String> getIgnoredUsers(String service) {
		return getService(service).getIgnoredUsers();
	}
	
	public static boolean isIgnoredUser(String user, String service) {
		List<String> users = getIgnoredUsers(service);
		for(String u : users) {
			if(u.equalsIgnoreCase(user))
				return true;
		}
		return false;
	}
	
	public static boolean isModerator(String user, String service) {
		List<String> users = getModerators(service);
		for(String u : users) {
			if(u.equalsIgnoreCase(user))
				return true;
		}
		return false;
	}
	
	public static boolean isRelayEnabled(String channel) {
		for(Channel c : channels) {
			if(c.getName().equalsIgnoreCase(channel)) {
				return c.getRelay();
			}
		}
		return false;
	}
	
	public static Channel getChannel(String channel, String service) {
		for(Channel c : channels) {
			if(c.getName().equalsIgnoreCase(channel)) {
				return c;
			}
		}
		return null;
	}
	
	public static boolean isRelayChannel(String channel) {
		for(Channel c : channels) {
			if(c.getRelayChannel().equalsIgnoreCase(channel))
				return true;
		}
		return false;
	}
	
	public static List<Channel> getChannels(String service) {
		List<Channel> build = new ArrayList<Channel>();
		for(Channel a : channels) {
			if(a.getService().equals(service))
				build.add(a);
		}
		return build;
	}
	
	public static List<Channel> getOnlineChannels() {
		List<Channel> build = new ArrayList<Channel>();
		for(Channel a : channels) {
			if(a.isOnline())
				build.add(a);
		}
		return build;
	}
	
	public static List<Channel> getOnlineChannels(String service) {
		List<Channel> build = new ArrayList<Channel>();
		for(Channel a : channels) {
			if(a.getService().equals(service) && a.isOnline())
				build.add(a);
		}
		return build;
	}
	
	/*
	 * Discord Functions
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
	
	public static boolean shouldDelete() {
		return discord.isDeleteEnabled();
	}
	
	public static boolean shouldSendWelcomeMention() {
		return discord.isWelcomeEnabled();
	}
	
	public static String getHomeGuild() {
		return discord.getServerId();
	}
	
	public static String getHomeChannel() {
		return discord.getChannelId();
	}	
	
	public static String getOwnerID() {
		return discord.getOwnerId();
	}
	
	/*
	 * Hitbox Functions
	 */
	public static String getHitboxLoginUser() {
		return hitbox.getUsername();
	}
	
	public static String getHitboxLoginPass() {
		return hitbox.getPassword();
	}
	
	public static List<String> getHitboxChannels() {
		List<String> build = new ArrayList<String>();
		for(Channel a : channels) {
			if(a.getService().equals("hitbox"))
				build.add(a.getName());
		}
		return build;
	}
	
	public static List<String> getHitboxRepeaters() {
		List<String> build = new ArrayList<String>();
		for(Channel a : channels) {
			if(a.getService().equalsIgnoreCase("hitbox") && a.getImageRepeat() == true)
				build.add(a.getName());
		}
		return build;
	}
	
	/*
	 * Twitch Functions
	 */
	
	public static String getOAuth() {
		return twitch.getPassword();
	}
	
	public static String getTwitchLoginUser() {
		return twitch.getUsername();
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
	
	public static List<String> getTwitchChannels() {
		List<String> build = new ArrayList<String>();
		for(Channel a : channels) {
			if(a.getService().equals("twitch"))
				build.add(a.getName());
		}
		return build;
	}
	
	public static String getTwitchDiscordChannelID() {
		return twitch.getDefaultDiscordRelayChannel();
	}
	
	public static String getTwitchDiscordAnnounceChannelID() {
		return twitch.getDefaultDiscordAnnounceChannel();
	}
	
	public static String getTwitchDiscordChannelID(String channel) {
		for(Channel c : channels) {
			if(c.getName().equalsIgnoreCase(channel))
				return c.getRelayChannel();
		}
		return twitch.getDefaultDiscordRelayChannel();
	}
	
	public static String getTwitchDiscordAnnounceChannelID(String channel) {
		for(Channel c : channels) {
			if(c.getName().equalsIgnoreCase(channel))
				return c.getAnnounceChannel();
		}
		return twitch.getDefaultDiscordAnnounceChannel();
	}
	
	/*
	 * HLDS
	 */
	
	public static List<Server> getServers() {
		return servers.getServers();
	}

}
