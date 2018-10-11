package com.kuoushi.hitboxjapi;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Hitbox {
	
	private static String apiUrl = "http://api.hitbox.tv/";
	private static String token;

	/*public Hitbox() {

	} */
	
	public static JSONArray getMedia(String channels) throws IOException {
		JSONObject rdr = readJsonFromUrl(apiUrl + "media/live/" + channels);
		JSONArray r = null;
		
		if(rdr != null) {
			r = rdr.getJSONArray("livestream");
		}
		
		return r;
	}
	
	public static String getChatSocketURI() throws IOException {
		JSONArray arr = Hitbox.getChatServers();
		
		if(arr == null) {
			return null;
		}
		
		String IP = arr.getJSONObject(0).getString("server_ip");
		String ID = "";
		
		String temp = getStringFromUrl("http://" + IP + "/socket.io/1/");
		ID = temp.substring(0, temp.indexOf(":"));

		return "ws://" + IP + "/socket.io/1/websocket/" + ID;
	}
	
	public static JSONArray getChatServers() throws IOException {
		JSONArray arr = null;
		
		String p = getStringFromUrl(apiUrl + "chat/servers");
		if(isJSONValid(p)) {
			arr = new JSONArray(p);
		}
		return arr;
	}
		
	public static String getToken(String name, String pass){
	    try {
	        URL url = new URL(apiUrl + "auth/token");
	        URLConnection connection = url.openConnection();
	        connection.setDoInput(true);
	        connection.setDoOutput(true);
	        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
	        try (DataOutputStream os = new DataOutputStream(connection.getOutputStream ())) {
	            String content = "login=" + name + "&pass=" + pass;
	            os.writeBytes(content);
	            os.flush();
	        }
	        try (BufferedReader is = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
	            token = new JSONObject(is.readLine()).get("authToken").toString();
	        }
	    }
	    catch (Exception e){
	        e.printStackTrace();
	    }
	    return token;
	}
	
	public static JSONObject readJsonFromUrl(String url) throws IOException {
		JSONObject json = null;
		try {
			String p = getStringFromUrl(url);
			if(isJSONValid(p)) {
				json = new JSONObject(p);
			}
	    }
	    catch (JSONException e) {
	    	json = null;
	    }
		return json;
	}
	
	private static String getStringFromUrl(String url) throws MalformedURLException {
		String r = "";
		InputStream is = null;
		
		try {
			is = new URL(url).openStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
			r = readAll(rd);
		}
		catch(IOException e) {
			r = "IOException: " + e.getMessage();
		}
		finally {
			if(is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return r;
	}
	
	private static String readAll(Reader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
	    int cp;
	    while ((cp = rd.read()) != -1) {
	    	sb.append((char) cp);
	    }
	    return sb.toString();
	}
	
	private static boolean isJSONValid(String test) {
	    try {
	        new JSONObject(test);
	    }
	    catch (JSONException ex) {
	        try {
	            new JSONArray(test);
	        }
	        catch (JSONException ex1) {
	            return false;
	        }
	    }
	    return true;
	}
}