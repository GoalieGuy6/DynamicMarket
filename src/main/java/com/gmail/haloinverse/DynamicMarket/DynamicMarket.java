package com.gmail.haloinverse.DynamicMarket;

import com.nijikokun.bukkit.Permissions.Permissions;
import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.registerDM.payment.Method;
import com.nijikokun.registerDM.payment.Methods;

import java.io.File;
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

    public static String name;
    public static String version;
    
    public DynamicMarketAPI DMAPI = new DynamicMarketAPI(this);
    
    public iListen playerListener = new iListen(this);

    public static Server server = null;
    public static Method economy = null;
    public static Methods economyMethods = null;
    public static PermissionHandler Permissions = null;
    
    public static Config Settings;
    public Messages messages;

    protected static boolean econLoaded = false;
    
    public static boolean debug = false;
    public static boolean needUpdate;

    protected static boolean opPermissions = false;
    protected static boolean simplePermissions = false;

    public String shop_tag = "{BKT}[{}Shop{BKT}]{} ";
    protected int max_per_purchase = 64;
    protected int max_per_sale = 64;
    protected boolean notHoldingItemBuy = false;
    protected boolean notHoldingItemSell = false;
    public String defaultShopAccount = "";
    public boolean defaultShopAccountFree = true;
    protected static String database_type = "sqlite";
    protected static String sqlite = "jdbc:sqlite:" + "plugins/DynamicMarket/shop.db";
    protected static String mysql = "jdbc:mysql://localhost:3306/minecraft";
    protected static String mysql_user = "root";
    protected static String mysql_pass = "pass";
    protected static String mysql_dbEngine = "MyISAM";
    protected static String csvFileName;
    protected static String csvFilePath;

    protected Items items;
    protected String itemsPath = "";
    protected DatabaseMarket db = null;

    protected boolean logTransactions = false;
    protected TransactionLogger transLog = null;
    protected String transLogFile = "transactions.log";
    protected boolean transLogAutoFlush = true;
    private static iPluginListener pluginListener = null;
    
    public static Config Groups;
    
    public void onDisable() {
        log.info("[" + name + "] Version " + version + " disabled.");
    }
    
    public File getLibFolder() throws Exception {
    	File libFolder = new File("lib");
    	
    	if (libFolder.exists() && !libFolder.isDirectory()) {
    		throw new Exception("lib folder could not be created!");
    	} else if (!libFolder.exists()) {
    		libFolder.mkdir();
    	}
    	
    	return libFolder;
    }
    
    @Override
    public void onEnable() {
        PluginDescriptionFile desc = getDescription();
        getDataFolder().mkdir();

        server = getServer();
        name = desc.getName();
        version = desc.getVersion();
        
    	log.info("[" + name + "] Initializing Version " + version + ".");
        
	  	PluginManager pm = getServer().getPluginManager();
	  	
	  	setupPermissions();
	  	
	  	pluginListener = new iPluginListener();
	  	pm.registerEvent(Event.Type.PLUGIN_ENABLE, pluginListener, Priority.Monitor, this);

        checkLibs();
        setup();
        
        log.info("[" + name + "] Version " + version + " enabled.");
        if (version.endsWith("SNAPSHOT")) {
        	log.info("[" + name + "] You are currently running a test build, stability is not guaranteed.");
        }
    }

    public static Server getTheServer() {
        return server;
    }
    
    public static Method getEconomy() {
        return economy;
    }

    public static boolean setEconomy(Method plugin) {
        if (economy == null) {
            economy = plugin;
            econLoaded = true;
        } else {
            return false;
        }
        return true;
    }

    public static void setupPermissions() {
    	if (Permissions != null) {
    		return;
    	}
    	
    	Plugin permissionsPlugin = getTheServer().getPluginManager().getPlugin("Permissions");
    	
        if (permissionsPlugin == null) {
        	log.info("[" + name + "] Permissions plugin not found, defaulting to OP.");
        	return;
        }
        
        DynamicMarket.Permissions = ((Permissions) permissionsPlugin).getHandler();
        log.info("[" + name + "] Linked with permissions successfully.");
    }
    
	private void checkLibs() {
		boolean isok = false;
		File libFolder;
		try {
			libFolder = getLibFolder();
		} catch (Exception ex) {
			libFolder = getDataFolder();
		} 
		File a = new File(libFolder + "/sqlitejdbc-v056.jar");
		if(!a.exists()) {
			isok =  FileDownloader.fileDownload("http://www.brisner.no/libs/sqlitejdbc-v056.jar", libFolder.toString());
			if(isok) 
				log.info("[" + name + "] Downloaded SQLite connector successfully.");
		}
		File b = new File(libFolder + "/mysql-connector-java-5.1.15-bin.jar");
		if(!b.exists()) {
			isok = FileDownloader.fileDownload("http://www.brisner.no/libs/mysql-connector-java-5.1.15-bin.jar", libFolder.toString());
			if(isok)
				log.info("[" + name + "] Downloaded MySQL connector successfully.");
		}
		File c = new File(getDataFolder() + "/items.db");
		if(!c.exists()) {
			isok = FileDownloader.fileDownload("http://www.brisner.no/DynamicMarket/items.db", getDataFolder().toString());
			if(isok)
				log.info("[" + name + "] Downloaded items.db successfully.");
		}
	}

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (this.DMAPI.hasWrappers()) {
        	return this.DMAPI.wrapCommand(sender, cmd, commandLabel, args);
        }
        return this.playerListener.parseCommand(sender, cmd.getName(), args, "", defaultShopAccount, defaultShopAccountFree);
    }
    
    public DynamicMarketAPI getAPI() {
    	return this.DMAPI;
    }

    public void setup() {
    	messages = new Messages(this);
        Settings = new Config(this, new File(getDataFolder(), "config.yml"));
    	updateSettings();
        Settings.load();
        
        Groups = new Config(this, new File(getDataFolder(), "groups.yml"));
        setupGroups();
        Groups.load();
        
        debug = false;

        items = new Items(getDataFolder().getPath() + File.separator + "items.db", this);

        shop_tag = messages.getMessage("general.tag") + " ";
        max_per_purchase = Settings.getInt("general.transactions.max-items-buy", 64);
        max_per_sale = Settings.getInt("general.transactions.max-items-sell", 64);
        
        notHoldingItemBuy = Settings.getBoolean("general.empty-hand-buy", notHoldingItemBuy);
        notHoldingItemSell = Settings.getBoolean("general.empty-hand-sell", notHoldingItemSell);

        DynamicMarket.database_type = Settings.getString("database.type", "sqlite");
        
        sqlite = "jdbc:sqlite:" + getDataFolder() + File.separator + Settings.getString("database.sqlite.file", "shop.db");
        mysql = Settings.getString("database.mysql.database", mysql);
        mysql_user = Settings.getString("database.mysql.user", mysql_user);
        mysql_pass = Settings.getString("database.mysql.password", mysql_pass);
        mysql_dbEngine = Settings.getString("database.mysql.engine", mysql_dbEngine);
        
        if (DynamicMarket.database_type.equalsIgnoreCase("mysql")) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
            } catch(ClassNotFoundException ex) {
                log.severe("[" + name + "] Could not find MySQL driver.");
                ex.printStackTrace();
            }
            db = new DatabaseMarket(DatabaseMarket.Type.MYSQL, "Market", items, mysql_dbEngine, this);
        } else {
            try {
                Class.forName("org.sqlite.JDBC");
            } catch(ClassNotFoundException ex) {
                log.severe("[" + name + "] Could not find SQLite driver.");
                ex.printStackTrace();
            }
            db = new DatabaseMarket(DatabaseMarket.Type.SQLITE, "Market", items, "", this);
        }

        csvFileName = Settings.getString("general.csv.file", "shopDB.csv");
        csvFilePath = "plugins/" + name;
        opPermissions = Settings.getBoolean("permissions.op-permissions", false);
        simplePermissions = Settings.getBoolean("permissions.ignore-permissions", false);

        defaultShopAccount = Settings.getString("general.shop-account.name", "");
        defaultShopAccountFree = Settings.getBoolean("general.shop-account.free", defaultShopAccountFree);

        logTransactions = Settings.getBoolean("general.transactions.log-transactions", false);
        transLogFile = Settings.getString("general.transactions.log-file", transLogFile);
        transLogAutoFlush = Settings.getBoolean("general.transactions.log-auto-flush", transLogAutoFlush);
        
        if (logTransactions) {
	        if ((transLogFile != null) && (!transLogFile.isEmpty())) {
	            transLog = new TransactionLogger(this, getDataFolder() + File.separator + transLogFile, transLogAutoFlush);
	        } else {
	            transLog = new TransactionLogger(this, null, false);
	        }
        }
    }
    
    private void updateSettings() {
    	Settings.load();
    	Settings.setHeader("# DynamicMarket V0.6.1 Configuration");
    	
    	Settings.addProperty("general.transactions.log-transactions", false);
    	Settings.addProperty("general.transactions.log-file", "transactions.log");
    	Settings.addProperty("general.transactions.log-auto-flush", true);
    	Settings.addProperty("general.transactions.max-items-buy", 64);
    	Settings.addProperty("general.transactions.max-items-sell", 64);
    	Settings.addProperty("general.empty-hand-buy", false);
    	Settings.addProperty("general.empty-hand-sell", false);
    	Settings.addProperty("general.csv.file", "shopexport.csv");
    	Settings.addProperty("general.shop-account.free", true);
    	Settings.addProperty("general.shop-account.name", "");
    	
    	Settings.addProperty("database.type", "sqlite");
    	Settings.addProperty("database.sqlite.file", "shop.db");
    	Settings.addProperty("database.mysql.database", "jdbc:mysql://localhost:3306/minecraft");
    	Settings.addProperty("database.mysql.user", "minecraft");
    	Settings.addProperty("database.mysql.password", "password");
    	Settings.addProperty("database.mysql.engine", "MyISAM");
    	
    	Settings.addProperty("permissions.op-permissions", false);
    	Settings.addProperty("permissions.ignore-permissions", false);
    	Settings.save();
    }
    
    private void setupGroups() {
    	Groups.load();
    	Groups.setHeader(
    	"#",
    	"# Used to setup discount groups for your DynamicMarket shop",
    	"#",
    	"# Example",
    	"#",
    	"# groups:",
    	"#     vip:",
    	"#         node: 'vip'",
    	"#         discount: 20",
    	"#",
    	"# Node: The permission node that represents this group",
    	"# i.e. 'vip' means the permission node would be:",
    	"# 'DynamicMarket.groups.vip'",
    	"#",
    	"# Discount: The percentage discount to give to members of",
    	"# this group.",
    	"#");
    	
    	Groups.addProperty("groups", null);
    	
    	Groups.save();
    }
}