package com.github.birdgeek.breadbot.discord;

import java.util.List;

import javax.security.auth.login.LoginException;

import org.slf4j.Logger;

import com.github.birdgeek.breadbot.utility.Channel;
import com.github.birdgeek.breadbot.utility.ConfigFile;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.AccountType;

public class DiscordMain {
	
	public static JDA jda;
	static Logger discordLog;
	
	public static void setup(Logger log) {
		discordLog = log;
	
		try {
			jda = new JDABuilder(AccountType.BOT)
				.setToken(ConfigFile.getBotToken())
//				.addEventListener(new ChatEvent(jda, discordLog)) //Pass API and Specific Logger
//				.addEventListener(new InviteEvent()) 
				.addEventListener(new DiscordToTwitchEvent())
//				.addEventListener(new PmEvent(discordLog)) //Passes Logger
				.setGame(Game.playing("KuoushiBot v" + ConfigFile.getVersion())).build();
//				.buildBlocking();
			jda.awaitReady();
		}
		catch (LoginException | IllegalArgumentException | InterruptedException e) {
			discordLog.error(e.getMessage());
		} //Builds the discord bot - Blocks everything until API is ready	

		sendWelcome();
	}
	
	/*
	 * Sends on connect welcome to home discord channel
	 */
	public static void sendWelcome() {
		
		if (ConfigFile.shouldSendWelcomeMention()) { //Should we mention the Owner
			jda.getTextChannelById(ConfigFile.getHomeChannel()).sendMessage(
				new MessageBuilder()
				.appendCodeBlock("Kuoushi Bot is now active! \n"
						+ "Version: " + ConfigFile.getVersion()
						, "python")
				.build()).queue();
		
		
			jda.getTextChannelById("" + ConfigFile.getHomeChannel()).sendMessage(new MessageBuilder()
					.append("I am being run by ")
					.append(jda.getUserById("" + ConfigFile.getOwnerID()).getAsMention())
					.build()).queue();
		}
	}
	
	public static void deleteLiveNotification(Channel channel, String service) {
		int checkCount = 100;
		for (Message message : jda.getTextChannelById(channel.getAnnounceChannel()).getIterableHistory())
	     {
			if (message.getContentDisplay().contains(channel.getUrl())) {
				message.delete().queue(); 
	        	break;
	        }
			
			checkCount--;
			if(checkCount <= 0)
				break;
	     }
	}
	
}
