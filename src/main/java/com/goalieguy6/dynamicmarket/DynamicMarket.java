package com.goalieguy6.dynamicmarket;

import com.goalieguy6.dynamicmarket.handlers.Default;
import com.goalieguy6.dynamicmarket.handlers.List;
import com.goalieguy6.util.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class DynamicMarket extends JavaPlugin {
	
	public String name; // "DynamicMarket"
	public String version;
	
	private Settings settings;
	
	private CommandHandler commands;

	@Override
	public void onDisable() {
		Logger.info("Version " + this.version + " disabled.");

	}

	@Override
	public void onEnable() {
		PluginDescriptionFile pdfFile = this.getDescription();
		this.name = pdfFile.getName();
		this.version = pdfFile.getVersion();
				
		Logger.setup(this);
		Logger.info("Initializing version " + this.version + ".");
		
		settings = new Settings(this);
		settings.load();
		
		setupCommands();
		
		Logger.info("Version " + this.version + " enabled.");
	}
	
	private void setupCommands() {
		commands = new CommandHandler();
		
		Default defaultHandler = new Default(this);
		List listHandler = new List(this);
		
		commands.add("default", defaultHandler);
		
		commands.add("-l", listHandler);
		commands.add("list", listHandler);
	}
	
	@Override
	public boolean onCommand(CommandSender cSender, Command command, String label, String[] args) {
		
		if (args.length == 0) {
			return commands.getHandler("default").process(cSender, args);
		} else {
			if (commands.getHandler(args[0]) != null) {
				return commands.getHandler(args[0]).process(cSender, args);
			}
		}
		
		return false;
	}

}
