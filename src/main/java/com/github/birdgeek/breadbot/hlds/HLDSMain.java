package com.github.birdgeek.breadbot.hlds;

import java.util.concurrent.TimeoutException;
import java.util.List;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;

import org.slf4j.Logger;

import com.github.koraktor.steamcondenser.exceptions.SteamCondenserException;
import com.github.koraktor.steamcondenser.steam.servers.GoldSrcServer;
import com.github.koraktor.steamcondenser.steam.servers.GameServer;
import com.github.koraktor.steamcondenser.steam.servers.SourceServer;
import com.github.birdgeek.breadbot.utility.ConfigFile;
import com.github.birdgeek.breadbot.utility.Server;


public class HLDSMain {
	static Logger hldschatLog;
	private static List<GameServer> myServ;
	
	public static void setup(Logger log) {
		hldschatLog = log;
		myServ = new ArrayList<GameServer>();
		int i = 0;
		for(Server x : ConfigFile.getServers()) {
			try {
				if(x.getServerType().equalsIgnoreCase("hlds")) {
					myServ.add(new GoldSrcServer(x.getServerAddress()));
				}
				else if (x.getServerType().equalsIgnoreCase("source")) {
					myServ.add(new SourceServer(x.getServerAddress()));
				}
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
		for(GameServer s : myServ) {
			sendMessage(contents,s);
		}
	}
	
	public static void sendMessage(String contents, Server serv) {
		for(GameServer g : myServ) {
			List<InetAddress> ip = g.getIpAddresses();
			for(InetAddress i : ip) {
				hldschatLog.info("serv address: " + i.getHostAddress() + " address param: " + serv.getServerAddress());
				if(serv.getServerAddress().startsWith(i.getHostAddress())) {
					sendMessage(contents,g);
					return;
				}
			}
		}
	}
	
	public static void sendMessage(String contents, GameServer server) {
		boolean tooLong = false;
		String send = contents;
		
		if(contents.length() > 63) {
			int substr = contents.substring(0,63).lastIndexOf(" ");
			if(substr <= 20) {
				substr = 63;
			}
			send = send.substring(0,substr);
			contents = contents.substring(substr);
			tooLong = true;
		}
		
		sendRcon("say " + send,server);

		if(tooLong) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			sendMessage(contents,server);
		}
	}
	
	/*public static void sendRcon(String contents) {
		for(GoldSrcServer g : myServ) {
			sendRcon(contents,g);
		}
	}*/
	
	public static void sendRcon(String contents, Server serv) {
		sendRcon(contents,serv.getServerAddress());
	}
	
	public static void sendRcon(String contents, String address) {
		for(GameServer g : myServ) {
			List<InetAddress> ip = g.getIpAddresses();
			for(InetAddress i : ip) {
				hldschatLog.info("serv address: " + i.getHostAddress() + " address param: " + address);
				if(address.startsWith(i.getHostAddress())) {
					sendRcon(contents,g);
					return;
				}
			}
		}
		
		hldschatLog.info("Unable to locate server at address: " + address + "\nMake sure it's in the config file.");
	}
	
	public static void sendRcon(String contents, GameServer server) {
		int retries = 10;
		while(retries > 0) {
			try {
				hldschatLog.info(server.rconExec(contents));
				break;
			} catch (TimeoutException e) {
				hldschatLog.info("Timeout sending message to HLDS: " + e.toString());
				reconnect(server);
				retries--;
			} catch (SteamCondenserException e) {
				hldschatLog.info("Error sending message to HLDS: " + e.toString());
				retries--;
			}
		}
	}
	
	public static void disconnect() {
		for(GameServer g : myServ) {
			g.disconnect();
		}
	}
	
	public static void reconnect(GameServer server) {
		int retries = 10;
		while(retries > 0) {
			try {
				server.initialize();
				List<InetAddress> ip = server.getIpAddresses();
				boolean breakOut = false;
				
				for(Server x : ConfigFile.getServers()) {  // This is the absolute most inefficient way of doing this
					for(InetAddress i : ip) {
						if(x.getServerAddress().startsWith(i.getHostAddress())) {
							breakOut = true;
							server.rconAuth(x.getServerRcon());
							break;
						}
					}
					if(breakOut) {
						break;
					}
				}
				break;
			} catch (SteamCondenserException e) {
				hldschatLog.info("Timeout reconnecting to HLDS: " + e.toString());
				retries--;
			} catch (TimeoutException e) {
				hldschatLog.info("Error reconnecting to HLDS: " + e.toString());
				retries--;
			}
		}
	}
// {gameId=300647710720, dedicated=100, networkVersion=48, maxPlayers=32, serverName=Dead Games Done Together - Jaykin Bacon: Source, secure=true, serverPort=27015, gameDir=HL2JKS, operatingSystem=119, serverId=-6203588949173075969, numberOfPlayers=1, appId=70, numberOfBots=0, passwordProtected=false, gameVersion=1.1.2.2, gameDescription=Half-Life 2: Jaykin' Bacon Source, mapName=BEACH3}
// {gameId=940597837823, dedicated=100, networkVersion=17, maxPlayers=64, serverName=Kuoushi's Kool Battle Grounds 2 2.4 Server, secure=true, serverPort=27016, gameDir=bg2, operatingSystem=119, serverId=1691309188602396671, numberOfPlayers=0, serverTags=increased_maxplayers, appId=218, numberOfBots=0, passwordProtected=false, gameVersion=1.0.1.0, gameDescription=Battle Grounds 2 2.4, mapName=bg_woodland}
	
	public static String getServerStatus() {
		String build = "";
		for(GameServer g : myServ) {
			HashMap<String,Object> servInfo;
			try {
				g.updateServerInfo();
				servInfo = g.getServerInfo();
				Integer numPlayers = new Integer(servInfo.get("numberOfPlayers").toString());
				Integer numBots = new Integer(servInfo.get("numberOfBots").toString());
				int actPlayers = numPlayers - numBots;
				build += "\n" + servInfo.get("serverName") + " (" + servInfo.get("gameDescription") + ") - " + actPlayers + "/" + servInfo.get("maxPlayers") + " - " + servInfo.get("mapName");
			} catch (SteamCondenserException | TimeoutException e1) {
				e1.printStackTrace();
			}
			
//			build += "\n" + servInfo.get;// g.toString() + "\n";
		}
		return build;
	}
}