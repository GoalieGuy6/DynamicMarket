package com.gmail.haloinverse.DynamicMarket;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.bukkit.util.config.Configuration;

public class Messages {
	
	private DynamicMarket plugin;
	private Configuration config;

	public Messages(DynamicMarket instance) {
		this.plugin = instance;
		load();
	}
	
	public String getMessage(String message) {
		return config.getString(message, "");
	}
	
	private void load() {
		File messageFile = new File(plugin.getDataFolder(), "messages.yml");
		if (!messageFile.exists()) {
			extractFile();
		}
		
		config = new Configuration(messageFile);
		config.load();
	}

	private void extractFile() {
		File outputFile = new File(plugin.getDataFolder(), "messages.yml");
		
		InputStream input = DynamicMarket.class.getResourceAsStream("/defaults/messages.yml");    	
    	FileOutputStream output = null;
    	
    	try {
    		output = new FileOutputStream(outputFile);
    		byte[] buffer = new byte[8192];
    		int length = 0;
    		
    		while ((length = input.read(buffer)) > 0)
    			output.write(buffer, 0, length);
    		
    		DynamicMarket.log.info("[" + DynamicMarket.name + "] Message file created.");
    	} catch (Exception e) {
    		DynamicMarket.log.warning("[" + DynamicMarket.name + "] Error creating message file.");
    	} finally {
    		try {
    			if (input != null) {
    				input.close();
    			}
    		} catch (Exception e) { }
    		
    		try {
    			if (output != null) {
    				output.close();
    			}
    		} catch (Exception e) { }
    	}
	}
}
