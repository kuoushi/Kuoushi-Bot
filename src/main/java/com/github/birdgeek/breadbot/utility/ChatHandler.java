package com.github.birdgeek.breadbot.utility;

import com.github.birdgeek.breadbot.discord.DiscordMain;
import com.github.birdgeek.breadbot.hitbox.HitboxMain;
import com.github.birdgeek.breadbot.hlds.HLDSMain;
import com.github.birdgeek.breadbot.irc.IrcMain;
import com.github.birdgeek.breadbot.notifiers.StreamStats;


public class ChatHandler {

	public static void onMessageReceived(Message m) {
		
	}
	
	public static void onMessageReceived(Message m, Channel c) {
		
	}
	
	public static void cameOnline(Channel channel) {
		if(!ConfigFile.isServiceEnabled(channel.getService())) {
			return;
		}
		
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
			
			if(ConfigFile.isServiceEnabled("hlds") && channel.getName().equalsIgnoreCase("kuoushi") && (channel.getGame().equalsIgnoreCase("Half-Life") || channel.getGame().equalsIgnoreCase("Sven Co-Op"))) {
				HLDSMain.sendMessage(channel.getDisplayName() + " is now live on " + properService + ".",channel.getName());
				HLDSMain.sendMessage("Now relaying messages from Discord/" + properService + ".",channel.getName());
			}
			
			DiscordMain.jda.getTextChannelById(channel.getRelayChannel())
				.sendMessage("*Now relaying messages to and from #" + channel.getDisplayName() + " on " + properService + ".*").queue();
		}
	}
	
	public static void wentOffline(Channel channel, StreamStats end) {
		if(!ConfigFile.isServiceEnabled(channel.getService())) {
			return;
		}
		
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
			
			
			if(ConfigFile.isServiceEnabled("hlds") && channel.getName().equalsIgnoreCase("kuoushi") && (channel.getGame().equalsIgnoreCase("Half-Life") || channel.getGame().equalsIgnoreCase("Sven Co-Op"))) {
				HLDSMain.sendMessage(properService + " stream has gone offline. Relay is shutting down.","force");
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
