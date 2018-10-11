package com.github.birdgeek.breadbot.hlds;

import java.util.concurrent.TimeoutException;
import java.util.List;
import java.util.ArrayList;

import org.slf4j.Logger;

import com.github.koraktor.steamcondenser.exceptions.SteamCondenserException;
import com.github.koraktor.steamcondenser.steam.servers.GoldSrcServer;
import com.github.birdgeek.breadbot.notifiers.TwitchNotifiers;
import com.github.birdgeek.breadbot.utility.ConfigFile;
import com.github.birdgeek.breadbot.utility.Server;


public class HLDSMain {
	static Logger hldschatLog;
	private static List<GoldSrcServer> myServ;
//	private static String rconPass[]; 
	
	public static void setup(Logger log) {
		hldschatLog = log;
		myServ = new ArrayList<GoldSrcServer>();
		int i = 0;
		for(Server x : ConfigFile.getServers()) {
			try {
				myServ.add(new GoldSrcServer(x.getServerAddress()));
				myServ.get(i).initialize();
				myServ.get(i).rconAuth(x.getServerRcon());
				i++;
			}
			catch(SteamCondenserException e) {
				hldschatLog.info("Error connecting to HLDS: " + e.toString());
			}
			catch(TimeoutException e) {
				hldschatLog.info("Timeout connecting to HLDS: " + e.toString());
			}
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
			for(GoldSrcServer g : myServ) {
				g.rconExec(contents);
			}
		} catch (TimeoutException e) {
			hldschatLog.info("Timeout sending message to HLDS: " + e.toString());
		} catch (SteamCondenserException e) {
			hldschatLog.info("Error sending message to HLDS: " + e.toString());
		}
	}
	
	public static void disconnect() {
		for(GoldSrcServer g : myServ) {
			g.disconnect();
		}
	}
	
	public static String getServerStatus() {
		String build = "";
		for(GoldSrcServer g : myServ) {
			build += g.toString() + "\n";
		}
		return build;
	}
}