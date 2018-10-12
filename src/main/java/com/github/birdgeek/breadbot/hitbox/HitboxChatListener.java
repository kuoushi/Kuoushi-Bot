package com.github.birdgeek.breadbot.hitbox;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import com.github.birdgeek.breadbot.discord.DiscordMain;
import com.github.birdgeek.breadbot.utility.ConfigFile;
import com.kuoushi.hitboxjapi.HitboxChat;

public class HitboxChatListener extends HitboxChat {

	private List<String> repeatChannels;
	
	public HitboxChatListener(String name, String pass, String wsUrl) throws Exception {
		super(name, pass, wsUrl);
		
		repeatChannels = new ArrayList<String>();
		
		List<String> channels = ConfigFile.getHitboxRepeaters();
		for(String channel : channels) {
			repeatChannels.add(channel);
		}
	}

	@Override
	public void onChatMsg(JSONObject params) {
//		HitboxMain.hitboxchatLog.info("chatMsg Received: " + params.toString());
		String channel = params.getString("channel");
		String text    = params.getString("text");
		String name    = params.getString("name");
		
		if(!name.equalsIgnoreCase(this.name)) {
			HitboxMain.hitboxchatLog.info("{#" + channel + "} [" + name + "] " + text);
			
			if(params.getBoolean("media")) {
				String output = text.replaceAll("\\<.*?>","");
				
				int startIndex = text.indexOf("\"") + 1;
				int endIndex   = text.indexOf("\"", startIndex + 1);
				
				String type    = text.substring(startIndex,endIndex);
				if(type.equalsIgnoreCase("image")) {
					getMediaLog(text.substring(startIndex,endIndex),channel);
				}
				else {
					startIndex = text.indexOf("embed/") + 6;
					endIndex   = text.indexOf("\"",startIndex + 1);
					
					if(!output.equals("")) {
						output = output + " http://www.youtube.com/watch?v=" + text.substring(startIndex, endIndex);
					}
					else {
						output = "http://www.youtube.com/watch?v=" + text.substring(startIndex, endIndex);
					}
				}
				
				if(!output.equals("")) {
					try {
						DiscordMain.jda.getTextChannelById(ConfigFile.getTwitchDiscordChannelID())
						.sendMessage("{**" + channel + "**}" + " [*" + name + "*] " + output);
					}
					catch (Exception ex) {
						HitboxMain.hitboxchatLog.info("Error sending message to Discord: " + ex.toString());
					}
				}
				
			}
			else {
				try {
					DiscordMain.jda.getTextChannelById(ConfigFile.getTwitchDiscordChannelID())
					.sendMessage("{**" + channel + "**}" + " [*" + name + "*] " + text.replaceAll("\\<.*?>",""));
				}
				catch (Exception ex) {
					HitboxMain.hitboxchatLog.info("Error sending message to Discord: " + ex.toString());
				}
			}
	
			if(repeatChannels.contains(channel)) {
				String target  = text;
				String http = "((http:\\/\\/|https:\\/\\/)?(www.)?(([a-zA-Z0-9-]){2,}\\.){1,4}([a-zA-Z]){2,6}(\\/([a-zA-Z-_\\/\\.0-9#:?=&;,]*)?)?)(.jpg|.png|.jpeg|gif)";
				Pattern pattern = Pattern.compile(http);
				Matcher matcher = pattern.matcher(target);
				if(matcher.find()) {
					String temp = target.substring(matcher.start(), matcher.end());
					if(!temp.contains("http://") && !temp.contains("https://")) {
						temp = "http://" + temp;
					}
					HitboxMain.sendMessage(temp, channel);
				}
			}
		}
	}
	
	@Override
	public void onLoginMsg(JSONObject message) {
		HitboxMain.hitboxchatLog.info("loginMsg Received: " + message.toString());
	}
	
	@Override
	public void onDirectMsg(JSONObject message) {
		HitboxMain.hitboxchatLog.info("directMsg Received: " + message.toString());
	}
	
	@Override
	public void onInfoMsg(JSONObject message) {
		HitboxMain.hitboxchatLog.info("infoMsg Received: " + message.toString());
	}

	@Override
	public void onServerMsg(JSONObject message) {
		HitboxMain.hitboxchatLog.info("serverMsg Received: " + message.toString());
	}
	
	@Override
	public void onMotdMsg(JSONObject message) {
		HitboxMain.hitboxchatLog.info("motdMsg Received: " + message.toString());
	}
	
	@Override
	public void onPollMsg(JSONObject message) {
		HitboxMain.hitboxchatLog.info("pollMsg Received: " + message.toString());
	}
	
	@Override
	public void onRaffleMsg(JSONObject message) {
		HitboxMain.hitboxchatLog.info("raffleMsg Received: " + message.toString());
	}
	
	@Override
	public void onUnexpectedMsg(JSONObject message) {
		HitboxMain.hitboxchatLog.info("UnexpectedMsg Received: " + message.toString());
	}
	
	@Override
	public void onMediaLog(JSONArray log) {
//		HitboxMain.hitboxchatLog.info("mediaLog Received: " + log.toString());
		if(log.length() > 0) {
			JSONObject last = log.getJSONObject(log.length() - 1);
			try {
				DiscordMain.jda.getTextChannelById(ConfigFile.getTwitchDiscordChannelID())
				.sendMessage("{**" + last.getString("channel") + "**}" + " [*" + last.getString("name") + "*] " + last.getString("url"));
			}
			catch (Exception ex) {
				HitboxMain.hitboxchatLog.info("Error sending message to Discord: " + ex.toString());
			}
		}
	}
	
	@Override
	public void onChatLog(JSONObject log) {
//		HitboxMain.hitboxchatLog.info("chatLog Received: " + log.toString());
	}
}
