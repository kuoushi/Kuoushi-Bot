package com.github.birdgeek.breadbot.utility;

import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;

public class IrcUtility extends ListenerAdapter  {
	
	static String targetChannel;
	
	static String[] ircCommands = {"help", "toggle"};
	
	public IrcUtility() {
		targetChannel = "#" + ConfigFile.getTwitchChannel();
	}

	public static boolean isIgnored(String nick) {
		String[] ignoredUsers = ConfigFile.getIgnoredIrcUsers();
		
		for (int i = 0 ; i < ignoredUsers.length; i++) {
			if (ignoredUsers[i].equalsIgnoreCase(nick))
				return true;
		}
		
		return false;
	}

	//Should we relay the command?
	public static boolean isCommand(MessageEvent e) {
		
		for (String ircCommand : ircCommands) {
			
			if (e.getMessage().equalsIgnoreCase(ircCommand)) {
				return true;
			}
			else {
				return false;
			}
		}
		return false;
	}
	
	/*
	 * Should we be relaying the IRC chat to the discord channel?
	 */
	public static boolean isDoingRelay() {
		return ConfigFile.shouldIrcRelay();
	}

	/*
	 * Method for finding is a specific user is admin on Bot
	 */
	public static boolean isApprovedUser(String username) {
		for (int i=0; i < ConfigFile.getApprovedIRCUsers().length; i++) {
			if (username.equalsIgnoreCase(ConfigFile.getApprovedIRCUsers()[i])) {
				return true;
			}
		}
			return false;
	}	
}
 