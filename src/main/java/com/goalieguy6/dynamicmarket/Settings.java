package com.goalieguy6.dynamicmarket;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

public class Settings {
	
	private YamlConfiguration config;
	private File file;
	
	public Settings(DynamicMarket plugin) {
		this.config = new YamlConfiguration();
		plugin.getDataFolder().mkdirs();
		
		this.file = new File(plugin.getDataFolder(), "config.yml");
		if (!this.file.exists()) {
			try {
				this.file.createNewFile();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		
	}
	
	public void load() {
		try {
			config.load(this.file);
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (InvalidConfigurationException ex) {
			ex.printStackTrace();
		}
		
		for (Config option : Config.values()) {
			if (option.getPath().isEmpty()) continue;
			
			if (!config.isSet(option.getPath())) {
				config.set(option.getPath(), option.getValue());
			} else {
				option.setValue(config.get(option.getPath()));
			}
			
			config.addDefault(option.getPath(), option.getValue());
		}
	}
	
	public void save() {
		for (Config option : Config.values()) {
			if (option.getPath().isEmpty()) continue;
			
			config.set(option.getPath(), option.getValue());
		}
		
		config.options().header("DynamicMarket Configuration");
		
		try {
			config.save(this.file);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	public static enum Config {
		Codename("", "Cygnus"),
		
		ManagerEnabled("general.manager.enabled", false),
		ManagerName("general.manager.account", ""),
		
		DatabaseType("database.type", "sqlite"),
		
		DatabaseFile("database.sqlite.file", "DynamicMarket.db"),
		
		DatabaseHost("database.mysql.host", "localhost"),
		DatabaseUser("database.mysql.user", "root"),
		DatabasePass("database.mysql.password", ""),
		DatabaseName("database.mysql.name", "minecraft"),
		DatabasePort("database.mysql.port", 3306);
		
		private String path;
		private Object value;
		
		private Config(String path, Object value) {
			this.path = path;
			this.value = value;
		}
		
		public Object getValue() {
			return this.value;
		}
		
		public void setValue(Object value) {
			this.value = value;
		}
		
		public String getPath() {
			return this.path;
		}
		
		public boolean getBoolean() {
			return (Boolean) this.value;
		}
		
		public Double getDouble() {
			return (Double) this.value;
		}
		
		public Integer getInteger() {
			return (Integer) this.value;
		}
		
		public Long getLong() {
			return (Long) this.value;
		}
		
		public String getString() {
			return String.valueOf(this.value);
		}
		
		@SuppressWarnings("unchecked")
		public List<String> getStringList() {
			return (List<String>) this.value;
		}
		
		@Override
		public String toString() {
			return this.getString();
		}
		
	}

}
