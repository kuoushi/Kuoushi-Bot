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
	}
}
