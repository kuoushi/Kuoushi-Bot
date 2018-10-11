package com.github.birdgeek.breadbot.utility;


import java.io.FileReader;
import java.util.List;
import java.util.ArrayList;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import com.github.birdgeek.breadbot.BotMain;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class ConfigFile {
	static String filename = "config.cfg"; //TODO Set this different for releases
	static JSONObject jsconfig;
	public static PropertiesConfiguration config;
	private static List<Channel> channels;
	private static Service twitch;
	private static Service hitbox;
	private static DiscordService discord;
	private static String version;
	
	public ConfigFile ()  {
		JSONParser parser = new JSONParser();
		
		try {
			jsconfig = (JSONObject) parser.parse(new FileReader("config.json"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		version = (String) jsconfig.get("version");
		
		twitch = new Service((JSONObject)((JSONObject)jsconfig.get("login")).get("twitch"),(JSONObject)jsconfig.get("twitch-settings"));
		hitbox = new Service((JSONObject)((JSONObject)jsconfig.get("login")).get("hitbox"),(JSONObject)jsconfig.get("hitbox-settings"));
		discord = new DiscordService((JSONObject)((JSONObject)jsconfig.get("login")).get("discord"),(JSONObject)jsconfig.get("discord-settings"));
		
		JSONArray temp = (JSONArray)jsconfig.get("channels");
		channels = new ArrayList<Channel>();
		for(int i = 0; i < temp.size(); i++) {
			JSONObject j = (JSONObject)temp.get(i);
			channels.add(i,new Channel(j));
			if(!j.containsKey("relay-channel")) {
				channels.get(i).updateRelayChannel("");
			}
			if(!j.containsKey("announce-channel")) {
				channels.get(i).updateAnnounceChannel("");
			}
		}
		
		try {
			ConfigFile.config = new PropertiesConfiguration(filename);
			
		} catch (ConfigurationException e) {
			BotMain.systemLog.warn(e.getMessage());
		}
	}
	
	/*
	 * String Arrays
	 */
	public static String[] getApprovedUsers() {
		return config.getStringArray("Approved_Users");
	}
	
	public static String[] getApprovedIRCUsers() {
		return config.getStringArray("Approved_IRC_Users");
	}
	
	public static String[] getIgnoredIrcUsers() {
		return config.getStringArray("Ignored_IRC_Users");
	}
	public static String getHitboxRepeaters() {
		String build = "";
		for(String a : config.getStringArray("Hitbox_Image_Repeat_Channels")) {
			build += a + ",";
		}
		build = build.substring(0, build.length() - 1);
		return build;
//		return config.getStringArray("Hitbox_Image_Repeat_Channels");
	}
	/*
	 * Strings
	 */
	public static String getEmail() throws ConfigurationException {
		return config.getString("Email");
	}
	
	public static String getPassword() throws ConfigurationException {
		return config.getString("Password");
	}
	public static String getBotToken() throws ConfigurationException {
		return config.getString("Token");
	}
	
	public static String getVersion() {
		return version;
	}
	
	public static String getTwitchChannel() {
		String build = "";
		for(String a : config.getStringArray("Twitch_Channel")) {
			build += a + ",";
		}
		build = build.substring(0, build.length() - 1);
		return build; //config.getString("Twitch_Channel");
	}
	
	public static String getHitboxChannel() {
		String build = "";
		for(String a : config.getStringArray("Hitbox_Channel")) {
			build += a + ",";
		}
		build = build.substring(0, build.length() - 1);
		return build; //config.getString("Twitch_Channel");
	}
	
	public static String getOAuth() {
		return config.getString("Twitch_OAuth");
	}
	
	public static String getTwitchLoginUser() {
		return config.getString("Twitch_Login_User");
	}
	
	public static String getHitboxLoginUser() {
		return config.getString("Hitbox_Login_User");
	}
	public static String getHitboxLoginPass() {
		return config.getString("Hitbox_Password");
	}
	/*
	 * Booleans
	 */
	public static boolean shouldEnableTwitch() {
		return config.getBoolean("Twitch_Enable");
	}
	
	public static boolean shouldDelete() {
		return config.getBoolean("delcmd");
	}
	
	public static boolean shouldSendWelcomeMention() {
		return config.getBoolean("Send_Welcome_Mention");
	}	
	public static boolean shouldIrcRelay() {
		return config.getBoolean("IRC_Relay");
	}
	public static void setIrcRelay(boolean value) {
		config.setProperty("IRC_Relay", value);
	}
	
	/*
	 * Ints
	 */
	public static String getHomeGuild() {
		String temp = (String)((JSONObject)jsconfig.get("discord-settings")).get("server-id");
		System.out.println(temp);
		return config.getBigInteger("Home_Guild_ID").toString();
	}
	
	public static String getHomeChannel() {
		return config.getBigInteger("Home_Channel_ID").toString();
	}	
	
	public static String getOwnerID() {
		return config.getBigInteger("Owner_ID").toString();
	}
	
	public static String getTwitchDiscordChannelID() {
		return config.getBigInteger("Twitch_Discord_Channel_ID").toString();
	}

}
