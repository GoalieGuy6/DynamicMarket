package com.gmail.haloinverse.DynamicMarket;

//package com.nijikokun.bukkit.SimpleShop;
// CHANGED: Commented out imports that weren't being used.
//import org.bukkit.Server;
//import org.bukkit.entity.Player;

public class Misc
{
    public static String headerify(String innerText)
    {
        int extraLength = innerText.length()
                - (stripCodes(Messaging.stripHighlights(innerText))).length();
        String newString = "--"
                + innerText
                + "------------------------------------------------------------";
        return newString.substring(0, 50 + extraLength);
        // This is approximate, due to inability to get string width of the proportional font.
    }

    public static String stripCodes(String toStrip)
    {
    	// Removes color codes from a string
        return toStrip.replaceAll("&[a-z0-9]", "").replace("&&", "&");
    }

    public static boolean isEither(String text, String against, String or)
    {
    	return ((text.equalsIgnoreCase(against)) || (text.equalsIgnoreCase(or)));
    }

    public static boolean isAny(String text, String[] against)
    {
        for (String thisAgainst : against) {
            if (text.equalsIgnoreCase(thisAgainst))
                return true;
        }
        return false;
    }
    
    public static String combineSplit(int startIndex, String[] string, String seperator)
    {
    	StringBuilder builder = new StringBuilder();

        for (int i = startIndex; i < string.length; ++i) {
        	builder.append(string[i]);
            builder.append(seperator);
        }
        
        builder.deleteCharAt(builder.length() - seperator.length());
        return builder.toString();
    }
}

/*
 * Location: C:\Program Files\eclipse\Bukkit\SimpleShop.jar Qualified Name:
 * com.nijikokun.bukkit.SimpleShop.Misc Java Class Version: 5 (49.0) JD-Core
 * Version: 0.5.3
 */