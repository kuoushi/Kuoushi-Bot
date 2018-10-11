package com.github.birdgeek.breadbot.utility;


import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import com.github.birdgeek.breadbot.BotMain;
import com.github.birdgeek.breadbot.discord.ChatEvent;

import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;


public class StatsFile {
	//static PropertiesConfiguration statsFile = new PropertiesConfiguration();
	String fileName = "stats.cfg";
	static PropertiesConfiguration stats;
	static StringBuilder sb;
	public StatsFile ()  {
		try {
			StatsFile.stats = new PropertiesConfiguration(fileName);
		} catch (ConfigurationException e) {
			BotMain.systemLog.warn(e.getMessage());
		}
	}
	
	
	public int getStats(String cmd) {
		return stats.getInt(cmd);
	}
	
	static public void readKeysToConsole() {
		for (int i=0; i < ChatEvent.availableCommands.length; i++) {
			BotMain.discordLog.info(ChatEvent.availableCommands[i].toString() + " = " +stats.getInt(ChatEvent.availableCommands[i]));
		}
	}
	
	static public void readKeys(MessageReceivedEvent e) {
		Message mess = null;

		for (int i=0; i < ChatEvent.availableCommands.length; i++) {
			mess  = new MessageBuilder().append(ChatEvent.availableCommands[i].toString() + " = " +stats.getInt(ChatEvent.availableCommands[i])).build();
			sb.append(ChatEvent.availableCommands[i].toString() + "=" + stats.getInt(ChatEvent.availableCommands[i] + "\n"));
		}
		e.getChannel().sendMessage(sb.toString());
	}

	public static void readKeys(PrivateMessageReceivedEvent e) {
		Message mess = null;
		for (int i=0; i < ChatEvent.availableCommands.length; i++) {
			mess  = new MessageBuilder().append(ChatEvent.availableCommands[i].toString() + " = " +stats.getInt(ChatEvent.availableCommands[i])).build();
		}
		e.getChannel().sendMessage(mess);
	}

	static public void updateCount(String cmd)  {
		int i = stats.getInt(cmd);
		stats.setProperty(cmd, i + 1);
		try {
			stats.save();
		} catch (ConfigurationException e) {
			BotMain.systemLog.warn(e.getMessage());
		}
	}



}
