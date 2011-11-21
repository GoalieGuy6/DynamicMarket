package com.goalieguy6.dynamicmarket;

import java.util.ArrayList;

import com.avaje.ebean.EbeanServer;
import com.goalieguy6.dynamicmarket.handlers.Default;
import com.goalieguy6.dynamicmarket.handlers.List;
import com.goalieguy6.dynamicmarket.model.ShopItem;
import com.goalieguy6.util.Logger;
import com.lennardf1989.bukkitex.DMDatabase;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class DynamicMarket extends JavaPlugin {
	
	public String name; // "DynamicMarket"
	public String version;
	
	private DMDatabase database;
	private Settings settings;
	
	private CommandHandler commands;

	public void onDisable() {
		Logger.info("Version " + this.version + " disabled.");
	}

	public void onEnable() {
		PluginDescriptionFile pdfFile = this.getDescription();
		this.name = pdfFile.getName();
		this.version = pdfFile.getVersion();
				
		Logger.setup(this);
		Logger.info("Initializing version " + this.version + ".");
		
		settings = new Settings(this);
		settings.load();
		
		setupDatabase();
		setupCommands();
		
		Logger.info("Version " + this.version + " enabled.");
	}
	
	private void setupCommands() {
		commands = new CommandHandler();
		
		Default defaultHandler = new Default(this);
		List listHandler = new List(this);
		
		commands.add("default", defaultHandler);
		
		commands.add("list", listHandler);
	}
	
	private void setupDatabase() {
		database = new DMDatabase(this) {
			protected java.util.List<Class<?>> getDatabaseClasses() {
				java.util.List<Class<?>> list = new ArrayList<Class<?>>();
				list.add(ShopItem.class);
				return list;
			}
		};
		
		String driver;
		String url = "jdbc:";
		String type = Settings.Config.DatabaseType.getString();
		
		if (type.equalsIgnoreCase("mysql")) {
			driver = "com.mysql.jdbc.Driver";
			url += "mysql://" + Settings.Config.DatabaseHost + ":" + Settings.Config.DatabasePort + "/" + Settings.Config.DatabaseName;
		} else {
			driver = "org.sqlite.JDBC";
			driver += "sqlite:plugins/" + this.name + "/" + Settings.Config.DatabaseFile;
		}
		
		database.initializeDatabase(
			driver, 
			url, 
			Settings.Config.DatabaseUser.getString(), 
			Settings.Config.DatabasePass.getString(), 
			"SERIALIZABLE"
		);
	}
	
	@Override
	public boolean onCommand(CommandSender cSender, Command command, String label, String[] args) {
		
		if (args.length == 0) {
			return commands.getHandler("default").process(cSender, args);
		}
		
		String cmd = args[0];
		
		if (cmd.equalsIgnoreCase("-l") || cmd.equalsIgnoreCase("list")) {
			return commands.getHandler("list").process(cSender, args);
		}
		
		return false;
	}
	
	@Override
	public EbeanServer getDatabase() {
		return database.getDatabase();
	}

}
