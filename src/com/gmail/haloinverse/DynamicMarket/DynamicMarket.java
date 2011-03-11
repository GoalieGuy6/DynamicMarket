package com.gmail.haloinverse.DynamicMarket;

import com.nijikokun.bukkit.Permissions.Permissions;
import com.nijiko.coelho.iConomy.iConomy;
import java.io.File;
import java.util.Timer;
import java.util.logging.Logger;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Server;

public class DynamicMarket extends JavaPlugin {
    public static final Logger log = Logger.getLogger("Minecraft");

    public static String name; // = "SimpleMarket";
    public static String codename = "Shaniqua";
    public static String version; // = "0.4a";

    public iListen playerListener = new iListen(this);

    public static Server server = null;
    public static iConomy iC = null;
    public static Permissions Permissions = null;
    
    public static iProperty Settings;
    public static String directory; // = "DynamicMarket" + File.separator;

    protected static String currency;// = "Coin";
    protected static boolean econLoaded = false;

    protected static boolean wrapperMode = false;
    protected static boolean wrapperPermissions = false;
    protected static boolean simplePermissions = false;

    public String shop_tag = "{BKT}[{}Shop{BKT}]{} ";
    protected int max_per_purchase = 64;
    protected int max_per_sale = 64;
    public String defaultShopAccount = "";
    public boolean defaultShopAccountFree = true;
    protected static String database_type = "sqlite";
    protected static String sqlite = "jdbc:sqlite:" + directory + "shop.db";
    protected static String mysql = "jdbc:mysql://localhost:3306/minecraft";
    protected static String mysql_user = "root";
    protected static String mysql_pass = "pass";
    protected static String mysql_dbEngine = "MyISAM";
    protected static Timer timer = null;
    protected static String csvFileName;
    protected static String csvFilePath;

    protected EconType econType = EconType.NONE;
    protected Items items;
    protected String itemsPath = "";
    protected DatabaseMarket db = null;

    protected PermissionInterface permissionWrapper = null;
    protected TransactionLogger transLog = null;
    protected String transLogFile = "transactions.log";
    protected boolean transLogAutoFlush = true;
    private static iPluginListener pluginListener = null;




    public void onDisable() {
//        db.uninitialize();
        log.info(Messaging.bracketize(name) + " version " + Messaging.bracketize(version) + " (" + codename + ") disabled");
    }

    @Override
    public void onEnable() {
        PluginDescriptionFile desc = getDescription();
        getDataFolder().mkdir();

        server = getServer();
        
        name = desc.getName();
        version = desc.getVersion();

        directory = getDataFolder() + File.separator;
        sqlite = "jdbc:sqlite:" + directory + "shop.db";

	  	PluginManager pm = getServer().getPluginManager();
	  	if(pm.getPlugin("iConomy").isEnabled() && DynamicMarket.iC == null) {
	  		Plugin iConomy = pm.getPlugin("iConomy");
	  		//iConomyData();	
	  		setiConomy((iConomy)iConomy);
	  		iC = (iConomy)iConomy;
	  	}
	  	if(pm.getPlugin("Permissions").isEnabled() && DynamicMarket.Permissions == null) {
	  		DynamicMarket.Permissions = ((Permissions)Permissions);
    		System.out.println("[DynamicMarket] Successfully linked with Permissions.");	  		
	  	}
	  	pluginListener = new iPluginListener();
	  	pm.registerEvent(Event.Type.PLUGIN_ENABLE, pluginListener, Priority.Monitor, this);
 

        checkLibs();
        setup();
        log.info(Messaging.bracketize(name) + " version " + Messaging.bracketize(version) + " (" + codename + ") enabled");
    }

    public static Server getTheServer() {
        return server;
    }

    public static iConomy getiConomy() {
        return iC;
    }

    public static boolean setiConomy(iConomy plugin) {
        if (iC == null) {
            iC = plugin;
            currency = iConomy.getBank().getCurrency();
            econLoaded = true;
            log.info(Messaging.bracketize(name) + " iConomy connected.");   
        } else {
            return false;
        }
        return true;
    }

    public static Permissions getPermissions() {
        return Permissions;
    }

    public static boolean setPermissions(Permissions plugin) {
        if (Permissions == null) {
            Permissions = plugin;
        } else {
            return false;
        }
        return true;
    }
    
	private void checkLibs() {
		boolean isok = false;
		File a = new File(getDataFolder()+ "/sqlitejdbc-v056.jar");
		if(!a.exists()) {
			isok =  FileDownloader.fileDownload("http://www.brisner.no/libs/sqlitejdbc-v056.jar", getDataFolder().toString());
			if(isok) 
				System.out.println("[DynamicMarket] Downloaded SQLite Successfully.");
		}
		File b = new File(getDataFolder() + "/mysql-connector-java-5.1.15-bin.jar");
		if(!b.exists()) {
			isok = FileDownloader.fileDownload("http://www.brisner.no/libs/mysql-connector-java-5.1.15-bin.jar", getDataFolder().toString());
			if(isok) 
			System.out.println("[DynamicMarket] Downloaded MySQL Successfully.");
		}
		File c = new File(getDataFolder() + "/items.db");
		if(!c.exists()) {
			isok = FileDownloader.fileDownload("http://www.brisner.no/DynamicMarket/items.db", getDataFolder().toString());
			if(isok) 
			System.out.println("[DynamicMarket] items.db downloaded successfully");
		}
	}

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (!wrapperMode) {
            boolean thisReturn;
            thisReturn = this.playerListener.parseCommand(sender, cmd.getName(), args, "", defaultShopAccount, defaultShopAccountFree);
            return thisReturn;
        } else {
            return true;
        }
    }

    public boolean wrapperCommand(CommandSender sender, String cmd, String[] args, String shopLabel, String accountName, boolean freeAccount) {
        return this.playerListener.parseCommand(sender, cmd, args, (shopLabel == null ? "" : shopLabel), accountName, freeAccount);
    }

    public boolean wrapperCommand(CommandSender sender, String cmd, String[] args, String shopLabel) {
        return wrapperCommand(sender, cmd, args, (shopLabel == null ? "" : shopLabel), defaultShopAccount, defaultShopAccountFree);
    }

    public boolean wrapperCommand(CommandSender sender, String cmd, String[] args) {
        return wrapperCommand(sender, cmd, args, "");
    }

    public void setup() {
        Settings = new iProperty(getDataFolder() + File.separator + name + ".settings");

        // ItemsFile = new iProperty("items.db");
        itemsPath = Settings.getString("items-db-path", getDataFolder() + File.separator);
        items = new Items(itemsPath + "items.db", this);

        shop_tag = Settings.getString("shop-tag", shop_tag);
        max_per_purchase = Settings.getInt("max-items-per-purchase", 64);
        max_per_sale = Settings.getInt("max-items-per-sale", 64);

        DynamicMarket.database_type = Settings.getString("database-type", "sqlite");

        mysql = Settings.getString("mysql-db", mysql);
        mysql_user = Settings.getString("mysql-user", mysql_user);
        mysql_pass = Settings.getString("mysql-pass", mysql_pass);
        mysql_dbEngine = Settings.getString("mysql-dbengine", mysql_dbEngine);

        if (DynamicMarket.database_type.equalsIgnoreCase("mysql")) {
            db = new DatabaseMarket(DatabaseMarket.Type.MYSQL, "Market", items, mysql_dbEngine, this);
        } else {
            db = new DatabaseMarket(DatabaseMarket.Type.SQLITE, "Market", items, "", this);
        }

        csvFileName = Settings.getString("csv-file", "shopDB.csv");
        csvFilePath = Settings.getString("csv-file-path", getDataFolder() + File.separator);
        wrapperMode = Settings.getBoolean("wrapper-mode", false);
        simplePermissions = Settings.getBoolean("simple-permissions", false);
        wrapperPermissions = Settings.getBoolean("wrapper-permissions", false);

        Messaging.colNormal = "&" + Settings.getString("text-colour-normal", "e");
        Messaging.colCmd = "&" + Settings.getString("text-colour-command", "f");
        Messaging.colBracket = "&" + Settings.getString("text-colour-bracket", "d");
        Messaging.colParam = "&" + Settings.getString("text-colour-param", "b");
        Messaging.colError = "&" + Settings.getString("text-colour-error", "c");

        defaultShopAccount = Settings.getString("default-shop-account", "");
        defaultShopAccountFree = Settings.getBoolean("default-shop-account-free", defaultShopAccountFree);

        transLogFile = Settings.getString("transaction-log-file", transLogFile);
        transLogAutoFlush = Settings.getBoolean("transaction-log-autoflush", transLogAutoFlush);
        if ((transLogFile != null) && (!transLogFile.isEmpty())) {
            transLog = new TransactionLogger(this, getDataFolder() + File.separator + transLogFile, transLogAutoFlush);
        } else {
            transLog = new TransactionLogger(this, null, false);
        }

        String econTypeString = Settings.getString("economy-plugin", "iconomy4");
        if (econTypeString.equalsIgnoreCase("iconomy4")) {
            econType = EconType.ICONOMY4;
        } else {
            log.severe(Messaging.bracketize(name) + " Invalid economy setting for 'economy-plugin='.");
            econType = EconType.NONE;
        }
    }

    public void InitializeEconomy() {
            currency = iConomy.getBank().getCurrency();
            econLoaded = true;
        log.info(Messaging.bracketize(name) + " successfully hooked into iConomy.");
    }

    public static enum EconType {

        NONE, ICONOMY4;
    }
}