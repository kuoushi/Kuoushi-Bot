package com.github.birdgeek.breadbot.notifiers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.birdgeek.breadbot.utility.ChatHandler;
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
		
		List<String> channels = ConfigFile.getTwitchChannels();
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
		String c = ConfigFile.getTwitchChannels().toString().replaceAll(", ", ",");
		params.put("channel",c.substring(1,c.length()-1));
		
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
					com.github.birdgeek.breadbot.utility.Channel c = ConfigFile.getChannel(streams.get(key).getChannel().getName(),"twitch");
					
					if(c == null) {
						NotifiersMain.notifiersLog.info("Could not find channel " + streams.get(key).getChannel().getName() + " for Twitch service.");
						continue;
					}
					
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
									c.updateViewers(a.getViewers());
									c.updateGame(a.getGame());
									c.updateCurrentStatus(a.getChannel().getStatus());
									break;
								}
							}
								
							if(!found) { // stream is now offline
								sanity.put(key, 1);
								setOffline(key); // streams.get(key).setId(0); // we offline now
								
								StreamStats end = streamStats.get(channel);
								streamStats.put(channel,null);
								
								ChatHandler.wentOffline(c,end);
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
										
										c.updateViewers(a.getViewers());
										c.updateGame(a.getGame());
										c.updateCurrentStatus(a.getChannel().getStatus());
										c.updateDisplayName(a.getChannel().getDisplayName());
										c.updateURL(a.getChannel().getUrl());
										
										ChatHandler.cameOnline(c);
										
										NotifiersMain.notifiersLog.info(name + " has come online.");
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
	
	public static synchronized List<String> getLiveStreams() {
		List<String> temp = new ArrayList<String>();

		for(String key : streams.keySet()) {
			if(streams.get(key).isOnline()) {
				temp.add(key);
			}
		}
		
		return temp;
	}
	
	public void stop() {
		keepGoing = false;
	}
}