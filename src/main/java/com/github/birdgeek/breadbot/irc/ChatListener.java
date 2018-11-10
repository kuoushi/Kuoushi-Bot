package com.github.birdgeek.breadbot.irc;

import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;

import com.github.birdgeek.breadbot.utility.ChatHandler;
import com.github.birdgeek.breadbot.utility.Message;


public class ChatListener extends ListenerAdapter {

	
	/*
	 * On IRC Message Received
	 * @see org.pircbotx.hooks.ListenerAdapter#onMessage(org.pircbotx.hooks.events.MessageEvent)
	 */
	public void onMessage(MessageEvent e) {
		Message received = new Message(e);
		ChatHandler.onMessageReceived(received);
	}
}
