package com.gmail.haloinverse.DynamicMarket;

import java.io.File;
import java.util.Map;

import org.bukkit.util.config.Configuration;

public class Config extends Configuration {
	
	private Map<String, Object> configValues;
	
	public Config(DynamicMarket instance, File file) {
		super(file);
	}
	
	@Override
	public void load() {
		super.load();
		configValues = getAll();
	}
	
	public void addProperty(String node, Object value) {
		boolean exists = false;
		
		for (String nodes : configValues.keySet()) {
			if (nodes.startsWith(node + ".") || nodes.equals(node)) {
				exists = true;
				break;
			}
		}
		
		if (!exists) {
			setProperty(node, value);
		}
		
		save();
	}
}
