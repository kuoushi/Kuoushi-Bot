package com.github.birdgeek.breadbot.notifiers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.birdgeek.breadbot.discord.DiscordMain;
import com.github.birdgeek.breadbot.irc.IrcMain;
import com.github.birdgeek.breadbot.hlds.HLDSMain;
import com.github.birdgeek.breadbot.utility.ConfigFile;
import com.mb3364.http.RequestParams;
import com.mb3364.twitch.api.handlers.StreamsResponseHandler;
import com.mb3364.twitch.api.models.Channel;
import com.mb3364.twitch.api.models.Stream;

public class TwitchNotifiers implements Runnable {

	private Thread t;
	private static String threadName;
	private Integer pollTime;
	
	public static Map<String, Stream> streams          = new HashMap<String, Stream>();
	public static Map<String, StreamStats> streamStats = new HashMap<String, StreamStats>();
	private static Map<String, Integer> sanity         = new HashMap<String, Integer>();
	private static RequestParams params;
	private static boolean keepGoing;
	
	public TwitchNotifiers(Integer i) {
		threadName = "Twitch Agent";
		keepGoing = true;
		pollTime = i;
		
		String[] channels = ConfigFile.getTwitchChannel().split(",");
		for(String channel : channels) {
			Channel x = new Channel();
			Stream temp = new Stream();
			
			x.setName(channel);
			x.setDisplayName(channel);
			temp.setId(0);
			temp.setChannel(x);
			
			streams.put(channel, temp);
			sanity.put(channel, 0);
			streamStats.put(channel, null);
		}
	}
	
	@Override
	public void run() {	
		params = new RequestParams();
		params.put("channel",ConfigFile.getTwitchChannel());
		
		while(keepGoing) {
			try {
				TwitchNotifiers.updateTwitch();

				Thread.sleep(pollTime);
			}
			catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}	
	}
	
	public void start() {
		if(t==null) {
			t = new Thread(this,threadName);
			t.start();
		}
	}
	
	public static void updateTwitch() {
		NotifiersMain.twitch.streams().get(params, new StreamsResponseHandler() {
			
			@Override
			public void onFailure(Throwable e) {
				NotifiersMain.notifiersLog.warn("Error: " + e.getMessage());
//				e.printStackTrace();
			}

			@Override
			public void onFailure(int statusCode, String statusMessage, String errorMessage) {
				NotifiersMain.notifiersLog.warn("Twitch: " + statusCode + ": " + statusMessage + " - " + errorMessage);
			}

			@Override
			public void onSuccess(int count, List<Stream> rStreams) {
//				NotifiersMain.notifiersLog.info("response received: " + rStreams.size() + " streams live");
				for(String key : streams.keySet()) {
					String name = streams.get(key).getChannel().getDisplayName();
					String channel = streams.get(key).getChannel().getName();
					Stream current = streams.get(key);
						
					if(sanity.get(key).equals(0)) {
						if(current.isOnline()) { // old stream info is online
							boolean found = false;
							for(Stream a : rStreams) {
								if(channel.equalsIgnoreCase(a.getChannel().getName())) { // stream is still online
//									NotifiersMain.notifiersLog.info(name + " is still online.");
									found = true;
									streams.put(key, a);
									streamStats.get(channel).addViewers(a.getViewers());
									break;
								}
							}
								
							if(!found) { // stream is now offline
								sanity.put(key, 1);
								setOffline(key); // streams.get(key).setId(0); // we offline now
								
								StreamStats end = streamStats.get(channel);
								streamStats.put(channel,null);
								
								NotifiersMain.notifiersLog.info(name + " has gone offline.");
								IrcMain.sendMessage("Relay shutting down. Join our Discord if you want to chat more! https://discord.gg/0R8wxAjGrpBMK690",channel);
								IrcMain.partChannel(channel);
								DiscordMain.jda.getTextChannelById(ConfigFile.getTwitchDiscordChannelID())
									.sendMessage("*Relay to #" + channel + " on Twitch closed.*").queue();
								DiscordMain.jda.getTextChannelById(ConfigFile.getTwitchDiscordChannelID())
									.sendMessage(name + "'s stream has ended. " + end.getViewerAverage() + " average viewers, " + end.getViewerPeak() + " peak viewers. (" + end.getStreamDurationString() + ")").queue();
								HLDSMain.sendMessage("Twitch stream has gone offline. Relay is shutting down.","force");
							}
						}
						else { // old stream info is offline
							if(rStreams != null) {
								for(Stream a : rStreams) {
									if(channel.equalsIgnoreCase(a.getChannel().getName())) { // stream is has come online
										sanity.put(key, 1);
										setOnline(key);
										streams.put(key, a); // we online now
										streamStats.put(channel,new StreamStats());
										
										NotifiersMain.notifiersLog.info(name + " has come online.");
										IrcMain.joinChannel(channel);
										IrcMain.sendMessage("Now relaying messages to and from our Discord", channel);
										
										DiscordMain.jda.getTextChannelById(ConfigFile.getTwitchDiscordChannelID())
											.sendMessage("@here " + name + " is live on Twitch! \"" + a.getChannel().getStatus() + "\" (" + a.getGame() + "): " + a.getViewers() + " viewers. <" + a.getChannel().getUrl() + ">").queue();
										DiscordMain.jda.getTextChannelById(ConfigFile.getTwitchDiscordChannelID())
											.sendMessage("*Now relaying messages to and from #" + name + " on Twitch.*").queue();
										
										HLDSMain.sendMessage(name + " is now live on Twitch.",channel);
										HLDSMain.sendMessage("Now relaying messages to and from Discord/Twitch.",channel);
										break;
									}
								}
							}
						}
					}
					else {
						sanity.put(key, sanity.get(key) - 1);
					}
				}
			}
		});
	}
	
	private static synchronized void setOnline(String key) {
		streams.get(key).setId(1);
	}
	
	private static synchronized void setOffline(String key) {
		streams.get(key).setId(0);
	}
	
	public static synchronized boolean isOnline(String key) {
		if(streams.get(key).isOnline()) {
			return true;
		}
		return false;
	}
	
	public static synchronized String getGame(String key) {
		return streams.get(key).getGame();
	}
	
	public static synchronized String[] getLiveStreams() {
		String[] temp = new String[streams.size()];
		int i = 0;
		for(String key : streams.keySet()) {
			if(streams.get(key).isOnline()) {
				temp[i] = key;
				i++;
			}
		}
		
		String[] returnMe = new String[i];
		for(int j = 0; j < i; j++) {
			returnMe[j] = temp[j];
		}
		
		return returnMe;
	}
	
	public void stop() {
		keepGoing = false;
	}
}