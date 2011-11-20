package com.goalieguy6.dynamicmarket;

import com.goalieguy6.util.Handler;

import java.util.HashMap;

public class CommandHandler {
	
	private HashMap<String, Handler> handlers;
	
	public CommandHandler() {
		this.handlers = new HashMap<String, Handler>();
	}
	
	public void add(String name, Handler handler) {
		this.handlers.put(name.toLowerCase(), handler);
	}
	
	public Handler getHandler(String name) {
		return this.handlers.get(name.toLowerCase());
	}
	
}
