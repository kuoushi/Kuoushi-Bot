package com.github.birdgeek.breadbot.utility;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.birdgeek.breadbot.BotMain;
import com.github.birdgeek.breadbot.discord.DiscordMain;
import com.github.birdgeek.breadbot.hitbox.HitboxMain;
import com.github.birdgeek.breadbot.hlds.HLDSMain;
import com.github.birdgeek.breadbot.irc.IrcMain;
import com.github.birdgeek.breadbot.notifiers.StreamStats;


public class ChatHandler {

	public static void onMessageReceived(Message m) {
//		System.out.println("Message Received: " + m);
		String  s = m.getService();
		if(s.equalsIgnoreCase("discord")) {
			onDiscordReceived(m);
		}
		else if(s.equalsIgnoreCase("twitch")) {
			onTwitchReceived(m);
		}
		else if(s.equalsIgnoreCase("hitbox")) {
			onHitboxReceived(m);
		}
	}
	
	public static void onCommandReceived(Message m) {
		switch(m.getMessage().substring(1)) {
			case "serverstatus":	sendResponse(HLDSMain.getServerStatus(),m);
									break;
			case "server":
			case "discord":			sendResponse(ConfigFile.getDiscordInviteLink(),m);
									break;
			default:				break;
		}
	}
	
	public static void sendResponse(String toSend, Message m) {
		if(m.getService().equalsIgnoreCase("discord")) {
			try {
				DiscordMain.jda.getTextChannelById(m.getChannel())
				.sendMessage(toSend).queue();
			}
			catch (Exception ex) {
				BotMain.ircLog.info("Error sending message to Discord: " + ex.toString());
			}
		}
		else if(m.getService().equalsIgnoreCase("twitch")) {
			for(String s : toSend.split("\n")) {
				if(!s.equals("")) {
					IrcMain.sendMessage(s,m.getChannel().substring(1));
				}
			}
		}
	}
	
	public static void onDiscordReceived(Message m) {
		boolean isHomeChannel = m.getChannel().equalsIgnoreCase(ConfigFile.getHomeChannel());
		boolean isRelayChannel = ConfigFile.isRelayChannel(m.getChannel());
		
		if(isRelayChannel && !m.isCommand()) {
			if(!m.getAuthorId().equalsIgnoreCase(DiscordMain.jda.getSelfUser().getId())) {
				BotMain.discordLog.info("[" + m.getAuthor() + "] " + m.getMessage());
				String contents = "{Discord} [" + m.getAuthor() + "] " + m.getMessage();
				
				
				try {
					if(ConfigFile.isServiceEnabled("twitch")) {
						List<Channel> c = ConfigFile.getOnlineChannels("twitch");
						for(Channel chan : c) {
							if(chan.getRelay() && chan.isOnline()) {
								IrcMain.sendMessage(contents, chan.getName());
							}
						}
					}
				}
				catch (Exception ex){
					BotMain.discordLog.info("Error sending message to IRC: " + ex.toString());
				}
				
				try {
					if(ConfigFile.isServiceEnabled("hitbox")) {
						List<Channel> c = ConfigFile.getOnlineChannels("hitbox");
						for(Channel chan : c) {
							if(chan.getRelay() && chan.isOnline()) {
								HitboxMain.sendMessage(contents, chan.getName());
							}
						}
					}
				}
				catch (Exception ex){
					BotMain.discordLog.info("Error sending message to Hitbox: " + ex.toString());
				}
				
				try {
					if(ConfigFile.isServiceEnabled("hlds")) {
						List<Server> s = ConfigFile.getServers();
						List<Channel> c = ConfigFile.getOnlineChannels();
						
						for(Channel chan : c) {
							if(chan.isOnline()) {
								for(Server serv : s) {
									if(serv.isRelayEnabled() && serv.shouldRelayChannel(chan.getName(), chan.getService(), chan.getGame())) {
										HLDSMain.sendMessage(contents, serv);
									}
								}
								break;
							}
						}
					}
				}
				catch (Exception ex) {
					BotMain.discordLog.info("Error sending message to HLDS: " + ex.toString());
				}
				
				BotMain.systemLog.trace("Should have sent: " + contents);
			}
		}
		else if(m.isCommand() && (isHomeChannel || isRelayChannel)) {
			onCommandReceived(m);
		}
	}
	
	public static void onTwitchReceived(Message m) {
		String chan = m.getChannel().substring(1);
		String user = m.getAuthor();
		String mess = m.getMessage();
		
		if (m.isCommand()) {
			onCommandReceived(m);
		}
		else if (!ConfigFile.isIgnoredUser(user, "twitch") && !user.equalsIgnoreCase(IrcMain.irc.getNick())) {
			if (ConfigFile.isRelayEnabled(chan) && !m.isCommand()) {
				if(IrcMain.channels.contains(chan)) {
					Channel c = ConfigFile.getChannel(chan, "twitch");
					
					try {
						DiscordMain.jda.getTextChannelById(c.getRelayChannel())
						.sendMessage(
								"{**" + chan + "**}" +
							    " [*" + user + "*] " + 
							    mess).queue();
					}
					catch (Exception ex) {
						BotMain.ircLog.info("Error sending message to Discord: " + ex.toString());
					}
					
					if(ConfigFile.isServiceEnabled("hlds")) {
						List<Server> s = ConfigFile.getServers();
						
						for(Server serv : s) {
							if(serv.isRelayEnabled() && serv.shouldRelayChannel(c.getName(), c.getService(), c.getGame())) {
								HLDSMain.sendMessage("{Twitch} [" + user + "] " + mess, serv);
							}
						}
					}
				}
			}
		}
	}
	
	public static void onHitboxReceived(Message m) {
		String chan = m.getChannel();
		String user = m.getAuthor();
		String mess = m.getMessage();
		
		if (!ConfigFile.isIgnoredUser(user, "hitbox") && !user.equalsIgnoreCase(HitboxMain.botUser())) {
			if (ConfigFile.isRelayEnabled(chan)) {
				Channel c = ConfigFile.getChannel(chan, "hitbox");
				
				try {
					DiscordMain.jda.getTextChannelById(c.getRelayChannel())
					.sendMessage(
							"{**" + chan + "**}" +
						    " [*" + user + "*] " + 
						    mess).queue();
				}
				catch (Exception ex) {
					BotMain.hitboxchatLog.info("Error sending message to Discord: " + ex.toString());
				}
					
				if(ConfigFile.isServiceEnabled("hlds")) {
					List<Server> s = ConfigFile.getServers();
					
					for(Server serv : s) {
						if(serv.isRelayEnabled() && serv.shouldRelayChannel(c.getName(), c.getService(), c.getGame())) {
							HLDSMain.sendMessage("{Hitbox} [" + user + "] " + mess);
						}
					}
				}
			}
		}
		
		/*
		 * HitboxMain.hitboxchatLog.info("{#" + channel + "} [" + name + "] " + text);
			
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
		}*/
	}
	
	public static void cameOnline(Channel channel) {
		if(!ConfigFile.isServiceEnabled(channel.getService())) {
			return;
		}
		
		channel.updateOnline(true);
		String properService = channel.getService().substring(0, 1).toUpperCase() + channel.getService().substring(1);
		
		if(channel.getAnnounce()) {
			DiscordMain.jda.getTextChannelById(channel.getAnnounceChannel())
				.sendMessage("@here " + channel.getDisplayName() + " is live on " + properService + "! \"" + channel.getCurrentStatus() + "\" (" + channel.getGame() + "): " + channel.getViewers() + " viewers. <" + channel.getUrl() + ">").queue();
		}
		
		if(channel.getRelay()) {
			if(channel.getService().equals("twitch")) {
				IrcMain.joinChannel(channel.getName());
				IrcMain.sendMessage("Now relaying messages to and from our Discord", channel.getName());
			}
			else if (channel.getService().equals("hitbox")) {
				HitboxMain.joinChannel(channel.getName());
				HitboxMain.sendMessage("Now relaying messages to and from our Discord",channel.getName());
			}
			
			if(ConfigFile.isServiceEnabled("hlds")) {
				List<Server> s = ConfigFile.getServers();
				
				for(Server serv : s) {
					if(serv.isRelayEnabled() && serv.shouldRelayChannel(channel.getName(), channel.getService(), channel.getGame())) {
						HLDSMain.sendMessage(channel.getDisplayName() + " is now live on " + properService + ".",serv);
						HLDSMain.sendMessage("Now relaying messages from Discord/" + properService + ".",serv);
					}
				}
			}
			
			DiscordMain.jda.getTextChannelById(channel.getRelayChannel())
				.sendMessage("*Now relaying messages to and from #" + channel.getDisplayName() + " on " + properService + ".*").queue();
		}
	}
	
	public static void wentOffline(Channel channel, StreamStats end) {
		if(!ConfigFile.isServiceEnabled(channel.getService())) {
			return;
		}
		
		channel.updateOnline(false);
		String properService = channel.getService().substring(0, 1).toUpperCase() + channel.getService().substring(1);
		
		if(channel.getRelay()) {
			if(channel.getService().equals("twitch")) {
				IrcMain.sendMessage("Relay shutting down. Join our Discord if you want to chat more! https://discord.gg/0R8wxAjGrpBMK690",channel.getName());
				IrcMain.partChannel(channel.getName());
			}
			else if(channel.getService().equals("hitbox")) {
				HitboxMain.sendMessage("Relay shutting down. Join our Discord if you want to chat more! https://discord.gg/0R8wxAjGrpBMK690",channel.getName());
				HitboxMain.partChannel(channel.getName());
			}
			
			
			if(ConfigFile.isServiceEnabled("hlds")) {
				List<Server> s = ConfigFile.getServers();
				
				for(Server serv : s) {
					if(serv.isRelayEnabled() && serv.shouldRelayChannel(channel.getName(), channel.getService(), channel.getGame())) {
						HLDSMain.sendMessage(properService + " stream has gone offline. Relay is shutting down.");
					}
				}
			}
			
			DiscordMain.jda.getTextChannelById(channel.getRelayChannel())
				.sendMessage("*Relay to #" + channel.getDisplayName() + " on " + properService + " closed.*").queue();
			DiscordMain.jda.getTextChannelById(channel.getRelayChannel())
				.sendMessage(channel.getDisplayName() + "'s stream has ended. " + end.getViewerAverage() + " average viewers, " + end.getViewerPeak() + " peak viewers. (" + end.getStreamDurationString() + ")").queue();
		}
		
		if(channel.getAnnounce()) {
			if(!channel.getRelayChannel().equals(channel.getAnnounceChannel())) {
				DiscordMain.deleteLiveNotification(channel, properService);
			}			
		}
	}
}
