package com.github.birdgeek.breadbot.notifiers;

import org.slf4j.Logger;

import com.mb3364.twitch.api.*;

public class NotifiersMain {
	
	public static Twitch twitch;
	static Logger notifiersLog;
	private static TwitchNotifiers twitchThread;
	private static HitboxNotifiers hitboxThread;
	
	
	
	public static void setup(Logger log) {
		notifiersLog = log;
		
		NotifiersMain.twitch = new Twitch();
		
		twitch.setClientId("evme0vmdts4kzzyt1vd134mmug4o1z4");
		
		twitchThread = new TwitchNotifiers(30000);
		hitboxThread = new HitboxNotifiers(30000);
		
		twitchThread.start();
		hitboxThread.start();
		
	}
	
	public static void kill() {
		twitchThread.stop();
		hitboxThread.stop();
	}
	
}
