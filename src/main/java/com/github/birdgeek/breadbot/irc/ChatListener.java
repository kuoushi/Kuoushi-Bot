package com.github.birdgeek.breadbot.irc;

import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;

import com.github.birdgeek.breadbot.discord.DiscordMain;
import com.github.birdgeek.breadbot.hlds.HLDSMain;
import com.github.birdgeek.breadbot.utility.ConfigFile;


public class ChatListener extends ListenerAdapter {

	
	/*
	 * On IRC Message Received
	 * @see org.pircbotx.hooks.ListenerAdapter#onMessage(org.pircbotx.hooks.events.MessageEvent)
	 */
	public void onMessage(MessageEvent e) {
		
		String chan = e.getChannel().getName().substring(1);
		String user = e.getUser().getNick();
		String mess = e.getMessage();
		
		if (!ConfigFile.isIgnoredUser(user, "twitch") && !user.equalsIgnoreCase(IrcMain.irc.getNick())) {
		
		/*
		 * Send the IRC message to discord
		 */
			if (ConfigFile.isRelayEnabled(chan)) {
				if(IrcMain.channels.contains(chan)) {
					try {
						DiscordMain.jda.getTextChannelById(ConfigFile.getTwitchDiscordChannelID())
						.sendMessage(
								"{**" + chan + "**}" +
							    " [*" + user + "*] " + 
							    e.getMessage()).queue();
					}
					catch (Exception ex) {
						IrcMain.ircLog.info("Error sending message to Discord: " + ex.toString());
					}
					HLDSMain.sendMessage("{Twitch} [" + user + "] " + mess,chan);
				}
			}
		}
	}
}
