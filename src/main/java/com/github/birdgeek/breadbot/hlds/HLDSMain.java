package com.github.birdgeek.breadbot.hlds;

import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;

import com.github.koraktor.steamcondenser.exceptions.SteamCondenserException;
import com.github.koraktor.steamcondenser.steam.servers.GoldSrcServer;
import com.github.birdgeek.breadbot.notifiers.TwitchNotifiers;


public class HLDSMain {
	static Logger hldschatLog;
	private static GoldSrcServer myServ[];
//	private static String rconPass[]; 
	
	public static void setup(Logger log) {
		hldschatLog = log;
		myServ = new GoldSrcServer[1];
		try {
			myServ[0] = new GoldSrcServer("127.0.0.1:27015");
			myServ[0].initialize();
//			myServ[0].rconAuth("");
		}
		catch(SteamCondenserException e) {
			hldschatLog.info("Error connecting to HLDS: " + e.toString());
		}
		catch(TimeoutException e) {
			hldschatLog.info("Timeout connecting to HLDS: " + e.toString());
		}
	}
	
	public static void sendMessage(String contents) {
		String[] streams = TwitchNotifiers.getLiveStreams();
		for(int i = 0; i < streams.length; i++) {
			sendMessage(contents,streams[i]);
		}
	}
	
	public static void sendMessage(String contents, String channel) {
		if(channel.equals("force")) {
			sendRcon("say " + contents);
		}
		else if(channel.equals("kuoushi")) {
			if(TwitchNotifiers.isOnline(channel)) {
				if(TwitchNotifiers.getGame(channel).equals("Sven Co-Op") || TwitchNotifiers.getGame(channel).equals("Half-Life")) {
					sendRcon("say " + contents);
				}
			}
		}
	}
	
	public static void sendRcon(String contents) {
		try {
			myServ[0].rconExec(contents);
		} catch (TimeoutException e) {
			hldschatLog.info("Timeout sending message to HLDS: " + e.toString());
		} catch (SteamCondenserException e) {
			hldschatLog.info("Error sending message to HLDS: " + e.toString());
		}
	}
	
	public static void disconnect() {
		myServ[0].disconnect();
	}
	
	public static String getServerStatus() {
		return myServ[0].toString();
	}
}