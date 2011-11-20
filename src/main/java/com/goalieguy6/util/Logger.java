package com.goalieguy6.util;

import com.goalieguy6.dynamicmarket.DynamicMarket;

public class Logger {
	
	private static java.util.logging.Logger logger;
	private static String prefix;
	
	public static void setup(DynamicMarket plugin) {
		logger = java.util.logging.Logger.getLogger("Minecraft");
		prefix = "[" + plugin.getDescription().getName() + "] ";
	}
	
	public static void config(String message) {
		logger.config(prefix + message);
	}
	
	public static void info(String message) {
		logger.info(prefix + message);
	}
	
	public static void raw(String message) {
		logger.info(message);
	}
	
	public static void severe(String message) {
		logger.severe(prefix + message);
	}
	
	public static void warning(String message) {
		logger.warning(prefix + message);
	}

}
