package com.github.birdgeek.breadbot.notifiers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.github.birdgeek.breadbot.utility.ChatHandler;
import com.github.birdgeek.breadbot.utility.ConfigFile;
import com.kuoushi.hitboxjapi.Hitbox;

public class HitboxNotifiers implements Runnable {

	private Thread t;
	private static String threadName;
	private Integer pollTime;
	
	public static Map<String, JSONObject> streams      = new HashMap<String, JSONObject>();
	public static Map<String, StreamStats> streamStats = new HashMap<String, StreamStats>();
	private static Map<String, Integer> sanity         = new HashMap<String, Integer>();
	private static boolean keepGoing;
	
	public HitboxNotifiers(Integer i) {
		threadName = "Hitbox Agent";
		keepGoing = true;
		pollTime = i;
		
		List<String> channels = ConfigFile.getHitboxChannels();
		for(String channel : channels) {
			JSONObject temp = new JSONObject();
			temp.put("media_name", channel);
			temp.put("media_is_live", 0);

			streams.put(channel, temp);
			sanity.put(channel, 0);
			streamStats.put(channel, null);
		}
	}
	
	@Override
	public void run() {	

		while(keepGoing) {
			try {
				HitboxNotifiers.updateHitbox();

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
	
	public static void updateHitbox() {
//		NotifiersMain.notifiersLog.info("Update Hitbox");
		
		JSONArray arr;
		try {
			arr = Hitbox.getMedia(ConfigFile.getHitboxChannels());
			if(arr != null) {
				for(int i = 0; i < arr.length(); i++) {
					JSONObject curr = arr.getJSONObject(i);
					
					com.github.birdgeek.breadbot.utility.Channel c = ConfigFile.getChannel(curr.getString("media_name"),"hitbox");
					
					if(c == null) {
						NotifiersMain.notifiersLog.info("Could not find channel " + curr.getString("media_name") + " for Hitbox service.");
						continue;
					}
					
					
					String channel  = curr.getString("media_name");
					String name     = curr.getString("media_user_name");
						
					if(streams.get(channel).getInt("media_is_live") == 1) {
						streams.put(channel, curr);
						if(curr.getInt("media_is_live") == 0) {
							NotifiersMain.notifiersLog.info(name + " has gone offline.");
							
							StreamStats end = streamStats.get(channel);
							streamStats.put(channel, null);
							
							ChatHandler.wentOffline(c, end);
						}
						else {
							streamStats.get(channel).addViewers(curr.getInt("media_views"));
							c.updateViewers(Integer.parseInt(curr.getString("media_views")));
							c.updateGame(curr.getString("category_name"));
							c.updateCurrentStatus(curr.getString("media_status"));
						}
					}
					else {
						streams.put(channel, curr);
						if(curr.getInt("media_is_live") == 1) {
							streamStats.put(channel, new StreamStats());
							
							c.updateViewers(Integer.parseInt(curr.getString("media_views")));
							c.updateGame(curr.getString("category_name"));
							c.updateCurrentStatus(curr.getString("media_status"));
							c.updateDisplayName(curr.getString("media_user_name"));
							c.updateURL(curr.getJSONObject("channel").getString("channel_link"));
							
							ChatHandler.cameOnline(c);
							
							NotifiersMain.notifiersLog.info(name + " has come online.");
						}
					}
				}
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void stop() {
		keepGoing = false;
	}
}