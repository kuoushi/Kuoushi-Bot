package com.github.birdgeek.breadbot.irc;

import java.util.ArrayList;
import java.util.List;

import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.output.OutputIRC;
import org.slf4j.Logger;

import com.github.birdgeek.breadbot.utility.ConfigFile;

public class IrcMain {
	public static PircBotX irc;
	static Logger ircLog;
	public static List<String> channels;
	static OutputIRC output;
	private static IrcThread threaded;
	
	
	/*
	 * Main method for creation of IRC Bot
	 */
	public static void setup(Logger log) {
		ircLog = log;
		channels = new ArrayList<String>();
		
		/*Configuration config = new Configuration.Builder()
				.setName(ConfigFile.getTwitchLoginUser())
				.addServer("irc.twitch.tv", 6667)
				.setServerPassword(ConfigFile.getOAuth())
				.addListener(new ChatListener())
				.buildConfiguration();
				
		irc = new PircBotX(config);*/
		
		threaded = new IrcThread();
		threaded.start();
	}

	public static boolean shouldEnable() {
		return ConfigFile.shouldEnableTwitch();
	}
	
	public static void kill() {
		ircLog.trace("Trying to close IRC connection");
		output.quitServer();
		
		if (!irc.isConnected()) 
			ircLog.trace("Succesfully closed IRC connection");
		else 
			ircLog.warn("Didn't close out - force shutting down program");
		System.exit(2);
	}
	
	public static void sendMessage(String contents) {
		if(channels != null) {
			for(String a : channels) {
				IrcMain.sendMessage(contents,a);
			}
		}
	}
	
	public static void sendMessage(String contents, String channel) {
		irc.sendRaw().rawLine("PRIVMSG #" + channel +" :" + contents);
	}
	
	public static void joinChannel(String contents) {
		channels.add(contents);
		irc.sendRaw().rawLine("JOIN #" + contents);
	}
	
	public static void partChannel(String contents) {
		channels.remove(channels.indexOf(contents));
		irc.sendRaw().rawLine("PART #" + contents);
	}

}
