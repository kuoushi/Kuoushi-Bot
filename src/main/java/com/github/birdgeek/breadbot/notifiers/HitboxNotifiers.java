package com.github.birdgeek.breadbot.notifiers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.github.birdgeek.breadbot.discord.DiscordMain;
import com.github.birdgeek.breadbot.hitbox.HitboxMain;
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
		
		String[] channels = ConfigFile.getHitboxChannel().split(",");
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
			arr = Hitbox.getMedia(ConfigFile.getHitboxChannel());
			if(arr != null) {
				for(int i = 0; i < arr.length(); i++) {
					JSONObject curr = arr.getJSONObject(i);
					String channel  = curr.getString("media_name");
					String name     = curr.getString("media_user_name");
						
					if(streams.get(channel).getInt("media_is_live") == 1) {
						streams.put(channel, curr);
						if(curr.getInt("media_is_live") == 0) {
							NotifiersMain.notifiersLog.info(name + " has gone offline.");
							HitboxMain.sendMessage("Relay shutting down. Join our Discord if you want to chat more! https://discord.gg/0R8wxAjGrpBMK690",channel);
							HitboxMain.partChannel(channel);
							
							StreamStats end = streamStats.get(channel);
							streamStats.put(channel, null);
							
							DiscordMain.jda.getTextChannelById(ConfigFile.getTwitchDiscordChannelID())
								.sendMessage("*Relay to #" + channel + " on Hitbox closed.*").queue();
							DiscordMain.jda.getTextChannelById(ConfigFile.getTwitchDiscordChannelID())
								.sendMessage(name + "'s stream has ended. " + end.getViewerAverage() + " average viewers, " + end.getViewerPeak() + " peak viewers. (" + end.getStreamDurationString() + ")").queue();
						}
						else {
							streamStats.get(channel).addViewers(curr.getInt("media_views"));
						}
					}
					else {
						streams.put(channel, curr);
						if(curr.getInt("media_is_live") == 1) {
							streamStats.put(channel, new StreamStats());
							NotifiersMain.notifiersLog.info(name + " has come online.");
							HitboxMain.joinChannel(channel);
							HitboxMain.sendMessage("Now relaying messages to and from our Discord",channel);
							DiscordMain.jda.getTextChannelById(ConfigFile.getTwitchDiscordChannelID())
								.sendMessage("@here " + name + " is live on Hitbox! \"" + curr.getString("media_status") + "\" (" + curr.getString("category_name") + "): " + curr.getString("media_views") + " viewers. " + curr.getJSONObject("channel").getString("channel_link")).queue();
							DiscordMain.jda.getTextChannelById(ConfigFile.getTwitchDiscordChannelID())
								.sendMessage("*Now relaying messages to and from #" + channel + " on Hitbox.*").queue();
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