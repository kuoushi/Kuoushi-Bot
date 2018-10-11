package com.github.birdgeek.breadbot.utility;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.slf4j.Logger;

import com.github.birdgeek.breadbot.BotMain;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

public class DiscordUtility {
	static JDA jda;
	static Logger discordLog;
	private static String helpFileName ="help.txt";
	static String[] approvedUsers = getApprovedUsers();
	
	public DiscordUtility(JDA api, Logger log) {
		DiscordUtility.jda = api;
		DiscordUtility.discordLog = log;
	}
	
	/**
	 * Return ID of a user from a GuildMessage
	 * @param e
	 * @return
	 */
	public static String getUsernameID(GuildMessageReceivedEvent e) {
		return e.getAuthor().getId();
	}
	/**
	 * Checks to see if the string username of a user matches the string from the config file for Owner powers
	 * @param username
	 * @return
	 */
	public static boolean isOwner(String username) {
		if (ConfigFile.getOwnerID().toString().equalsIgnoreCase(username))
			return true;
		else
			return false;
	}

	//TODO: Comment all these functions below
	public static void delMessage(GuildMessageReceivedEvent e) {

		if (ConfigFile.shouldDelete()) {
			
			e.getMessage().delete();
		}
	}

	/**
	 * Sends default help text to the channel
	 */
	public static void sendGlobalHelp(GuildMessageReceivedEvent e) {
		e.getChannel().sendMessage(new MessageBuilder()
				.append("Welcome to the help command! Below are all the commands you can run!")
				.appendCodeBlock(getHelpCommands(), "python")
				.build()).queue();
		
	}

	/**
	 * Sends helps to a user via PM
	 * @param e
	 */
	public static void sendHelp(GuildMessageReceivedEvent e) {
		if (!e.getAuthor().getName().equalsIgnoreCase(jda.getSelfUser().getName())) {
			e.getAuthor().openPrivateChannel().queue((channel) ->
			{
				channel.sendMessage("Welcome to the help command! Below are all the commands you can run!").queue();
				channel.sendMessage(new MessageBuilder().appendCodeBlock(getHelpCommands(), "python").build()).queue();
			});
		}
		else {
			e.getChannel().sendMessage("Cannot send help to yourself in PM; try #global help").queue();
		}
	}
	/**
	 * Sends help to a user - Initated via PM
	 * @param e
	 */
	public static void sendHelp(PrivateChannel e) {
		e.sendMessage(new MessageBuilder().appendCodeBlock(getHelpCommands(), "python").build()).queue();
	}
	
	/**
	 * Sends uptime to a public channel
	 * @param e
	 */
			
	public static void sendUptime(GuildMessageReceivedEvent e) {
		long different = System.currentTimeMillis() - BotMain.start;
		long secondsInMilli = 1000;
		long minutesInMilli = secondsInMilli * 60;
		long hoursInMilli = minutesInMilli * 60;
		long daysInMilli = hoursInMilli * 24;

		long elapsedDays = different / daysInMilli;
		different = different % daysInMilli;
		
		long elapsedHours = different / hoursInMilli;
		different = different % hoursInMilli;
		
		long elapsedMinutes = different / minutesInMilli;
		different = different % minutesInMilli;
		
		long elapsedSeconds = different / secondsInMilli;
		String time = String.format("%d days, %d hours, %d minutes, %d seconds%n", //THANKS FOR FIXING THIS SHIT VAN
		    elapsedDays,
		    elapsedHours, elapsedMinutes, elapsedSeconds);
		
		e.getChannel().sendMessage( "I have been online for " + time).queue();	
		}

	/**
	 * Outdated
	 * @return
	 */
	private static String getHelpCommands() {
		
		StringBuilder sb = new StringBuilder();
		
		try {
			FileReader fr = new FileReader(helpFileName);
			
			BufferedReader br = new BufferedReader(fr);
			String line = br.readLine();
			while (line != null) {
				sb.append(line);
				sb.append(System.lineSeparator());
				line = br.readLine();
			}
			String everything = sb.toString();
			br.close();
			return everything;
		}
		catch (FileNotFoundException ex) {

			discordLog.info("Unable to open file: " + helpFileName);
		}
		catch (IOException ex) {
			discordLog.info("Error reading file");
		}
		return "It must have failed on me :(";
	}
	/**
	 * Returns String array of approved Users
	 * @return
	 */
	public static String[] getApprovedUsers() {
		return ConfigFile.getApprovedUsers();
		
	}
	/**
	 * 
	 * @param e
	 * @return String of a user from given guild message event
	 */
	public static String getUsername(GuildMessageReceivedEvent e) {
		return e.getAuthor().getName();
	}
	/**
	 * If a user is contained in the string array
	 * @param username
	 * @return
	 */
	public static boolean isApprovedUser(String username) {
		for (int i=0; i < approvedUsers.length; i++) {
			if (username.equalsIgnoreCase(approvedUsers[i])) {
				return true;
			}
		}
			return false;
	}
	/**
	 * Debug
	 */
	public static void printDiagnostics() {
		jda.getTextChannelById(ConfigFile.getHomeChannel()).sendMessage("test").queue();
		jda.getTextChannelById(ConfigFile.getHomeChannel()).sendMessage(
				new MessageBuilder().appendCodeBlock(""
				+ "Home Channel: " + ConfigFile.getHomeChannel() + "/" + jda.getTextChannelById(ConfigFile.getHomeChannel()).getName()
				+ "\nHome Guild:" + ConfigFile.getHomeGuild() + "/" + jda.getGuildById(ConfigFile.getHomeGuild()).getName()
				+ "\nOwner: " + ConfigFile.getOwnerID() + "/" + jda.getUserById(ConfigFile.getOwnerID())
				, "python")
				.build()).queue();
		/*
		sendMessage(new MessageBuilder().appendCodeBlock(""
				+ "Home Channel: " + ConfigFile.getHomeChannel() + "/" + jda.getTextChannelById(ConfigFile.getHomeChannel()).getName()
				+ "\nHome Guild:" + ConfigFile.getHomeGuild() + "/" + jda.getGuildById(ConfigFile.getHomeGuild()).getName()
				+ "\nOwner: " + ConfigFile.getOwnerID() + "/" + jda.getUserById(ConfigFile.getOwnerID())
				, "python")
				.build());
		*/
		}
	/**
	 * Sends a message using String to home channel
	 * @param contents
	 */
	public static void sendMessage(String contents) {
		jda.getTextChannelById(ConfigFile.getHomeChannel()).sendMessage(contents).queue();
	
	}
	/**
	 * Sends a message using type Message to home channel
	 * @param message
	 */
	public static void sendMessage(Message message) {
		jda.getTextChannelById(ConfigFile.getHomeChannel()).sendMessage(message).queue();
	}

}
