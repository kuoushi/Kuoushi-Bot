package com.github.birdgeek.breadbot;

import java.util.Scanner;

import org.slf4j.LoggerFactory;

import com.github.birdgeek.breadbot.discord.DiscordMain;
import com.github.birdgeek.breadbot.hitbox.HitboxMain;
import com.github.birdgeek.breadbot.irc.IrcMain;
import com.github.birdgeek.breadbot.notifiers.NotifiersMain;
import com.github.birdgeek.breadbot.hlds.HLDSMain;
import com.github.birdgeek.breadbot.utility.ConfigFile;
import com.github.birdgeek.breadbot.utility.DiscordUtility;
import com.github.birdgeek.breadbot.utility.StatsFile;

import org.slf4j.Logger;

public class BotMain {
	
	public static long start;
	static ConfigFile config;
	static StatsFile stats;
	static String version;
	public static Logger discordLog;
	public static Logger ircLog;
	public static Logger notifiersLog;
	public static Logger hitboxchatLog;
	public static Logger hldschatLog;
	public static Logger systemLog;

	private static boolean shouldContinue;	
	
	/*
	 * Main method  for Breadbot
	 */
	public static void main(String[] args)  {
		
		discordLog = LoggerFactory.getLogger("Discord");
		ircLog = LoggerFactory.getLogger("IRC");
		systemLog = LoggerFactory.getLogger("System");
		notifiersLog = LoggerFactory.getLogger("Notifiers");
		hitboxchatLog = LoggerFactory.getLogger("Hitbox Chat");
		hldschatLog = LoggerFactory.getLogger("HLDS Chat");
	
		
		config = new ConfigFile();
		stats = new StatsFile();
		
		start = System.currentTimeMillis();
		version =  ConfigFile.getVersion();
		

		discordLog.info("Logging in using Token from config file.");
		DiscordMain.setup(discordLog);
		
		NotifiersMain.setup(notifiersLog);
		systemLog.trace("Enabled notifiers");
		
		if (ConfigFile.isServiceEnabled("twitch")) { //Should we enable the IRC portion?
			IrcMain.setup(ircLog);
			systemLog.trace("Enabled twitch chat");
		}
		
		if(ConfigFile.isServiceEnabled("hitbox")) {
			HitboxMain.setup(hitboxchatLog);
			systemLog.trace("Enabled hitbox chat");
		}
		
		if(ConfigFile.isServiceEnabled("hlds")) {
			HLDSMain.setup(hldschatLog);
			systemLog.trace("Enabled HLDS connection");
		}
		
		goLive();
	}

	public static void goLive(){
		
		shouldContinue = true;
		
		Scanner scanner = new Scanner(System.in);
		
		while(shouldContinue){
			
			String input = scanner.nextLine();
			char command = input.charAt(0);
			String contents = input.substring(1);
			
			switch(command){
			
			case 'k':
				
				discordLog.debug("Commanded to kill");
				DiscordUtility.sendMessage("Quiting from Console");
				shouldContinue = false;
				break;
				
			case 'c':
				
				discordLog.debug("Commanded to chat");
				DiscordUtility.sendMessage("[console] " + contents);
				break;
				
			case 'd':
				
				discordLog.debug("Commanded to print diagnostics");
				DiscordUtility.printDiagnostics();
				break;
				
			case 't':
				if (ConfigFile.isServiceEnabled("twitch")) {
					ircLog.debug("Commanded to chat");
					IrcMain.sendMessage(contents);
				}
				break;

			case 'j':
				HitboxMain.joinChannel("kuoushi");
				break;
				
			case 'f':
				IrcMain.joinChannel("kuoushi");
				break;
		}
	}
/*		
		DiscordMain.jda.shutdown();
		IrcMain.kill();
		NotifiersMain.kill();
*/
		HLDSMain.disconnect();
		scanner.close();
		System.exit(0);
	}	
}
