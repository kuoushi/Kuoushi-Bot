package com.github.birdgeek.breadbot.discord;

import javax.security.auth.login.LoginException;

import org.apache.commons.configuration.ConfigurationException;
import org.slf4j.Logger;

import com.github.birdgeek.breadbot.utility.ConfigFile;
import com.github.birdgeek.breadbot.utility.DiscordUtility;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.AccountType;

public class DiscordMain {
	
	public static JDA jda;
	static Logger discordLog;
	
	public static void setup(Logger log) {
		discordLog = log;
	
		try {
			jda = new JDABuilder(AccountType.BOT)
				.setToken(ConfigFile.getBotToken())
				.addEventListener(new ChatEvent(jda, discordLog)) //Pass API and Specific Logger
				.addEventListener(new InviteEvent()) 
				.addEventListener(new DiscordToTwitchEvent())
				.addEventListener(new PmEvent(discordLog)) //Passes Logger
				.setGame(Game.of("KuoushiBot v" + ConfigFile.getVersion()))
				.buildBlocking();
		}
		catch (LoginException | IllegalArgumentException | InterruptedException | RateLimitedException e) {
			discordLog.error(e.getMessage());
		} //Builds the discord bot - Blocks everything until API is ready
	
		new DiscordUtility(DiscordMain.jda, discordLog); //Setup for Util class - passes JDA and Logger		
		sendWelcome();
	}
	
	/*
	 * Sends on connect welcome to home discord channel
	 */
	public static void sendWelcome() {
		
		jda.getTextChannelById(ConfigFile.getHomeChannel()).sendMessage(
				new MessageBuilder()
				.appendCodeBlock("Kuoushi Bot is now active! \n"
						+ "Version: " + ConfigFile.getVersion()
						, "python")
				.build()).queue();
		
		if (ConfigFile.shouldSendWelcomeMention()) { //Should we mention the Owner
			jda.getTextChannelById("" + ConfigFile.getHomeChannel()).sendMessage(new MessageBuilder()
					.append("I am being run by ")
					.append(jda.getUserById("" + ConfigFile.getOwnerID()).getAsMention())
					.build()).queue();
		}
	}
	
}
