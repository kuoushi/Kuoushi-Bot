package com.github.birdgeek.breadbot.utility;

import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import org.json.JSONObject;
import org.pircbotx.hooks.events.MessageEvent;
import java.util.Date;

public class Message {
	String author;
	String message;
	String channel;
	String service;
	Date receivedOn;
	
	public Message (GuildMessageReceivedEvent e) {
		author = e.getAuthor().getName();
		channel = e.getChannel().getId();
		message = e.getMessage().getContentDisplay();
		service = "discord";
		receivedOn = new Date();
	}
	
	public Message (MessageEvent e) {
		author = e.getUser().getNick();
		channel = e.getChannel().getName();
		message = e.getMessage();
		service = "twitch";
		receivedOn = new Date();
	}
	
	public Message (JSONObject e) {
		author = e.getString("name");
		channel = e.getString("channel");
		message = e.getString("text");
		service = "hitbox";
		receivedOn = new Date();
	}
	
	public boolean isCommand() {
		return message.charAt(0) == '#';
	}
	
	public String toString() {
		return "{" + service + "} [" + channel + "] <" + author + "> " + message;
	}
}
