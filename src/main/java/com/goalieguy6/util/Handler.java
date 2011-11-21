package com.goalieguy6.util;

import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.command.ColouredConsoleSender;
import org.bukkit.entity.Player;

import com.goalieguy6.dynamicmarket.DynamicMarket;
import com.goalieguy6.dynamicmarket.Settings;

public abstract class Handler {

	protected final DynamicMarket plugin;
	
	protected CommandSender sender;
	protected Player player;
	
	protected boolean console;
	protected final ColouredConsoleSender consoleSender;
	
	public Handler(DynamicMarket instance) {
		this.plugin = instance;
		consoleSender = (ColouredConsoleSender) ColouredConsoleSender.getInstance();
	}
	
	public abstract boolean process(CommandSender cSender, String[] args);
	
	protected void init(CommandSender cSender) {
		this.sender = cSender;
		this.console = !(cSender instanceof Player);
		if (!console) player = (Player) cSender;
	}
		
	protected final boolean permission(String permission) {
		return (console) ? true : player.hasPermission(permission);
	}
	
	protected final void send(String message) {
		message = Message.parseColor(message);
		
		if (console) {
			consoleSender.sendMessage(message);
		} else {
			player.sendMessage(message);
		}
	}
	
	protected final void log(String message) {
		message = Message.parseColor(message);
		
		consoleSender.sendMessage(message);
	}
	
	protected final void sendHeader() {
		send("&c----- &6DynamicMarket V" + plugin.version + "&f(&e" + Settings.Config.Codename + "&f) &c-----");
	}
	
}
