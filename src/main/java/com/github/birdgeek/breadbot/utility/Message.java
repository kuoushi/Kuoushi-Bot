package com.github.birdgeek.breadbot.utility;

import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import org.json.JSONObject;
import org.pircbotx.hooks.events.MessageEvent;
import java.util.Date;

public class Message {
	String author;
	String authorId;
	String message;
	String channel;
	String service;
	Date receivedOn;
	
	public Message (GuildMessageReceivedEvent e) {
		author = e.getAuthor().getName();
		authorId = e.getAuthor().getId();
		channel = e.getChannel().getId();
		message = e.getMessage().getContentStripped();
		service = "discord";
		receivedOn = new Date();
	}
	
	public Message (MessageEvent e) {
		author = e.getUser().getNick();
		authorId = author.toLowerCase();
		channel = e.getChannel().getName();
		message = e.getMessage();
		service = "twitch";
		receivedOn = new Date();
	}
	
	public Message (JSONObject e) {
		author = e.getString("name");
		authorId = author.toLowerCase();
		channel = e.getString("channel");
		message = e.getString("text");
		service = "hitbox";
		receivedOn = new Date();
	}
	
	public boolean isCommand() {
		return message.charAt(0) == '#';
	}
	
	public String getAuthor() {
		return author;
	}
	
	public String getAuthorId() {
		return authorId;
	}
	
	public String getMessage() {
		return message;
	}
	
	public String getChannel() {
		return channel;
	}
	
	public String getService() {
		return service;
	}
	
	public Date getTime() {
		return receivedOn;
	}
	
	public String toString() {
		return "{" + service + "} [" + channel + "] <" + author + "> " + message;
	}
}
