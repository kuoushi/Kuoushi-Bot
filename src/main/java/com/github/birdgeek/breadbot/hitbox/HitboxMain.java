package com.github.birdgeek.breadbot.hitbox;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;

import com.github.birdgeek.breadbot.utility.ConfigFile;
import com.kuoushi.hitboxjapi.Hitbox;


public class HitboxMain {

	public static HitboxChatListener hitboxchat;
	static Logger hitboxchatLog;
	public static Map<String,HitboxChatListener> channels;
	private static String botuser = ConfigFile.getHitboxLoginUser();
	private static String botpass = ConfigFile.getHitboxLoginPass();

	/*
	 * Main method for creation of Hitbox Bot
	 */
	public static void setup(Logger log) {
		hitboxchatLog = log;
//		channels = new HashMap<String,HitboxChatListener>();
		
		List<String> chans = ConfigFile.getHitboxChannels();
		Map<String,HitboxChatListener> temp = new HashMap<String,HitboxChatListener>();
		
		for(String channel : chans) {
			temp.put(channel, null);
		}
		
		channels = temp;
	}

	public static void sendMessage(String contents) {
		for(String key : channels.keySet()) {
			sendMessage(contents,key);
		}
	}
	
	public static void sendMessage(String contents, String channel) {
		HitboxChatListener curr = channels.get(channel);
		if(curr != null) {
			curr.sendMessage(contents, channel);
		}
	}
	
	public static void joinChannel(String contents) {
		HitboxChatListener curr = null;
		
		while(channels == null) {
			
		}
		
		curr = channels.get(contents);
		
		Boolean retry = true;
		if(curr != null) {
			retry = false;
		}
		
		while(retry) {
			try {
				String uri = getURI();
				hitboxchatLog.info("WS Received: " + uri);
				curr = new HitboxChatListener(botuser,botpass,uri);
				
				Thread.sleep(2000);
				if(curr.isOnline) {
					retry = false;
				}
				else {
					curr = null;
					hitboxchatLog.warn("Failed to connect. Retrying in 15 seconds.");
					Thread.sleep(15000);
				}
			}
			catch (Exception e) {
				hitboxchatLog.warn("Error creating connection. Retrying in 15 seconds.");
				retry = true;
				try {
					Thread.sleep(15000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
			channels.put(contents,curr);
		}
		curr.joinChannel(contents);	
	}
	
	public static void partChannel(String contents) {
		HitboxChatListener curr = channels.get(contents);
		curr.partChannel(contents);
		channels.put(contents, null);
	}
		
	private static String getURI() {
		String a = null;
		
		try {
			while(a == null) {
				a = Hitbox.getChatSocketURI();
				if(a == null) {
					HitboxMain.hitboxchatLog.warn("Invalid chatsocket response. Retrying in 15 seconds.");
					Thread.sleep(15000);
				}
			}
		}
		catch(Exception e) {
			HitboxMain.hitboxchatLog.info(e.getMessage());
		}
		
		return a;
	}
}
