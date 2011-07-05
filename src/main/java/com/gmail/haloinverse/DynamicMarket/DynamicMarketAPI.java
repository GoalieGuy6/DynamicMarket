package com.gmail.haloinverse.DynamicMarket;

import com.gmail.haloinverse.DynamicMarket.DynamicMarket;

import java.util.LinkedList;
import java.util.ListIterator;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class DynamicMarketAPI {

	private DynamicMarket plugin;
	
	private LinkedList<JavaPlugin> wrappers = new LinkedList<JavaPlugin>();
	
	public DynamicMarketAPI(DynamicMarket instance) {
		this.plugin = instance;
	}
	
	public void hookWrapper(JavaPlugin wrapper) {
		this.wrappers.add(wrapper);
		DynamicMarket.log.info("[" + DynamicMarket.name + "] Wrapper mode enabled by " + wrapper.getDescription().getName() + ".");
	}
	
	public boolean hasWrappers() {
		return (!wrappers.isEmpty());
	}
	
	public LinkedList<JavaPlugin> getWrappers() {
		return wrappers;
	}
	
	public boolean wrapCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!hasWrappers()) {
			return false;
		}
		ListIterator<JavaPlugin> itr = wrappers.listIterator();
		while (itr.hasNext()) {
			JavaPlugin wrapper = itr.next();
			if (wrapper.onCommand(sender, command, label, args)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean returnCommand(CommandSender sender, Command cmd, String[] args, String shopLabel, String accountName, boolean freeAccount) {
        return plugin.playerListener.parseCommand(sender, cmd.getName(), args, (shopLabel == null ? "" : shopLabel), accountName, freeAccount);
    }

    public boolean returnCommand(CommandSender sender, Command cmd, String[] args, String shopLabel) {
        return returnCommand(sender, cmd, args, (shopLabel == null ? "" : shopLabel), plugin.defaultShopAccount, plugin.defaultShopAccountFree);
    }

    public boolean returnCommand(CommandSender sender, Command cmd, String[] args) {
        return returnCommand(sender, cmd, args, "");
    }
    
    public boolean hasPermission(CommandSender sender, String permission) {
    	return plugin.playerListener.hasPermission(sender, permission);
    }
    
    public String getShopTag() {
    	return plugin.shop_tag;
    }
}
