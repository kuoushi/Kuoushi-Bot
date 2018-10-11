package com.github.birdgeek.breadbot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.birdgeek.breadbot.hlds.HLDSMain;
import com.github.birdgeek.breadbot.utility.ConfigFile;

public class TestMain {

	public TestMain() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		ConfigFile config = new ConfigFile();
		
		Logger hldschatLog = LoggerFactory.getLogger("HLDS Chat");
		
		HLDSMain.setup(hldschatLog);
		
		System.out.println(HLDSMain.getServerStatus());

	}

}
