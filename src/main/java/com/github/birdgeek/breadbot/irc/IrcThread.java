package com.github.birdgeek.breadbot.irc;

import java.util.ArrayList;
import java.util.List;

import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.output.OutputIRC;

import com.github.birdgeek.breadbot.utility.ConfigFile;

public class IrcThread implements Runnable {

	private Thread t;
	private String threadName = "IRC Thread";

	@Override
	public void run() {

		try {
			List<String> x = new ArrayList<String>();
			for(int i = 0; i < ConfigFile.getChannels("twitch").size(); i++) {
				x.add("#" + ConfigFile.getChannels("twitch").get(i).getName());
			}
			Configuration config = new Configuration.Builder()
					.setName(ConfigFile.getTwitchLoginUser())
					.addServer("irc.twitch.tv", 6667)
					.setServerPassword(ConfigFile.getOAuth())
					.addListener(new ChatListener())
					.setAutoReconnect(true)
					.setAutoReconnectDelay(30000)
					.setAutoReconnectAttempts(30)
					.addAutoJoinChannels(x)
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