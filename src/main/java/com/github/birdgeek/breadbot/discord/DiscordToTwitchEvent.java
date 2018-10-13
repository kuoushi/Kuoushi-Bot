package com.github.birdgeek.breadbot.discord;

import com.github.birdgeek.breadbot.BotMain;
import com.github.birdgeek.breadbot.hitbox.HitboxMain;
import com.github.birdgeek.breadbot.hlds.HLDSMain;
import com.github.birdgeek.breadbot.irc.IrcMain;
import com.github.birdgeek.breadbot.utility.ConfigFile;
import com.github.birdgeek.breadbot.utility.Message;
import com.github.birdgeek.breadbot.utility.ChatHandler;

import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class DiscordToTwitchEvent extends ListenerAdapter {
	
	public DiscordToTwitchEvent() {
		
	}
	
	public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
		
		Message received = new Message(e);
		ChatHandler.onMessageReceived(received);
		
		if (e.getChannel().getId().equalsIgnoreCase(ConfigFile.getTwitchDiscordChannelID())) {
			if(!e.getAuthor().getId().equalsIgnoreCase(DiscordMain.jda.getSelfUser().getId()) && !e.getMessage().getContentStripped().startsWith("#")) {
				DiscordMain.discordLog.info("[" + e.getAuthor().getName() + "] " + e.getMessage().getContentStripped());
				String contents = "{Discord} [" + e.getAuthor().getName() + "] " + e.getMessage().getContentStripped();
				
				
				try {
					IrcMain.sendMessage(contents);
				}
				catch (Exception ex){
					DiscordMain.discordLog.info("Error sending message to IRC: " + ex.toString());
				}
				
				try {
					HitboxMain.sendMessage(contents);
				}
				catch (Exception ex){
					DiscordMain.discordLog.info("Error sending message to Hitbox: " + ex.toString());
				}
				
				try {
					HLDSMain.sendMessage(contents);
				}
				catch (Exception ex) {
					DiscordMain.discordLog.info("Error sending message to HLDS: " + ex.toString());
				}
				
				BotMain.systemLog.trace("Should have sent: " + contents);
			}
		}		
	}
}
