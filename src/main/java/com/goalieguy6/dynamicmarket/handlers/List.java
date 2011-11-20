package com.goalieguy6.dynamicmarket.handlers;

import com.goalieguy6.dynamicmarket.DynamicMarket;
import com.goalieguy6.util.Handler;

import org.bukkit.command.CommandSender;

public class List extends Handler {
	
	public List(DynamicMarket instance) {
		super(instance);
	}

	@Override
	public boolean process(CommandSender cSender, String[] args) {
		super.init(cSender);
		
		return false;
	}

}
