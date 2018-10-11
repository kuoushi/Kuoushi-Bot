package com.github.birdgeek.breadbot.irc;

import java.io.IOException;

import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.exception.IrcException;
import org.pircbotx.output.OutputIRC;

import com.github.birdgeek.breadbot.utility.ConfigFile;

public class IrcThread implements Runnable {

	private Thread t;
	private String threadName = "IRC Thread";

	@Override
	public void run() {

		try {
			Configuration config = new Configuration.Builder()
					.setName(ConfigFile.getTwitchLoginUser())
					.addServer("irc.twitch.tv", 6667)
					.setServerPassword(ConfigFile.getOAuth())
					.addListener(new ChatListener())
					.setAutoReconnect(true)
					.setAutoReconnectDelay(30000)
					.setAutoReconnectAttempts(30)
					.buildConfiguration();
				
			IrcMain.irc = new PircBotX(config);
			IrcMain.irc.startBot();
			IrcMain.ircLog.info("IRC thread started.");
			IrcMain.output = new OutputIRC(IrcMain.irc);
			IrcMain.ircLog.info("IRC output started.");
		} catch (Exception e) {
			IrcMain.ircLog.error(e.getMessage());
		}
	}
	
	public void start() {
		if(t==null) {
			t = new Thread(this,threadName);
			t.start();
		}		
	}
	
}