package com.github.birdgeek.breadbot.hlds;

import java.util.concurrent.TimeoutException;
import java.util.List;
import java.net.InetAddress;
import java.util.ArrayList;

import org.slf4j.Logger;

import com.github.koraktor.steamcondenser.exceptions.SteamCondenserException;
import com.github.koraktor.steamcondenser.steam.servers.GoldSrcServer;
import com.github.birdgeek.breadbot.utility.ConfigFile;
import com.github.birdgeek.breadbot.utility.Server;


public class HLDSMain {
	static Logger hldschatLog;
	private static List<GoldSrcServer> myServ;
	
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
		for(GoldSrcServer s : myServ) {
			sendMessage(contents,s);
		}
	}
	
	public static void sendMessage(String contents, Server serv) {
		for(GoldSrcServer g : myServ) {
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
	
	public static void sendMessage(String contents, GoldSrcServer server) {
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
		for(GoldSrcServer g : myServ) {
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
	
	public static void sendRcon(String contents, GoldSrcServer server) {
		try {
			hldschatLog.info(server.rconExec(contents));
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