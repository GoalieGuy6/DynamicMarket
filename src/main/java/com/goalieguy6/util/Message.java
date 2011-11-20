package com.goalieguy6.util;

public class Message {
	
	public static String stripColor(String message) {
		return message.replaceAll("(&([A-Fa-f0-9]))", "").replaceAll("\u00A7([A-Fa-f0-9])", "");
	}
	
	public static String parseColor(String message) {
		return message.replaceAll("(&([A-Fa-f0-9]))", "\u00A7" + "$2".toLowerCase());
	}

}
