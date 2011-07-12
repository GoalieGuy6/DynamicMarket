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
		if (!configValues.containsKey(node)) {
			this.setProperty(node, value);
		}
		
		save();
	}
}
