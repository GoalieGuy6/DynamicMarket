package com.gmail.haloinverse.DynamicMarket;

import com.nijikokun.bukkit.Permissions.Permissions;
import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.registerDM.payment.Method;
import com.nijikokun.registerDM.payment.Methods;

import java.io.File;
import java.util.LinkedList;
import java.util.ListIterator;
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
    public static String version; // = "0.5";
    
    public iListen playerListener = new iListen(this);

    public static Server server = null;
    public static Method economy = null;
    public static Methods economyMethods = null;
    public static PermissionHandler Permissions = null;
    
    public static iProperty Settings;
    public static File directory = null;

    //protected static String currency;// = "Coin";
    protected static boolean econLoaded = false;
    
    public static boolean debug = true;
    public static boolean needUpdate;

    //protected static boolean wrapperMode = false;
    protected static boolean opPermissions = false;
    protected static boolean wrapperPermissions = false;
    protected static LinkedList<JavaPlugin> wrappers = new LinkedList<JavaPlugin>();    
    
    protected static boolean simplePermissions = false;

    public String shop_tag = "{BKT}[{}Shop{BKT}]{} ";
    protected int max_per_purchase = 64;
    protected int max_per_sale = 64;
    public String defaultShopAccount = "";
    public boolean defaultShopAccountFree = true;
    protected static String database_type = "sqlite";
    protected static String sqlite = "jdbc:sqlite:" + "plugins/DynamicMarket/shop.db";
    protected static String mysql = "jdbc:mysql://localhost:3306/minecraft";
    protected static String mysql_user = "root";
    protected static String mysql_pass = "pass";
    protected static String mysql_dbEngine = "MyISAM";
    protected static Timer timer = null;
    protected static String csvFileName;
    protected static String csvFilePath;

    protected Items items;
    protected String itemsPath = "";
    protected DatabaseMarket db = null;

    protected PermissionInterface permissionWrapper = null;
    protected TransactionLogger transLog = null;
    protected String transLogFile = "transactions.log";
    protected boolean transLogAutoFlush = true;
    private static iPluginListener pluginListener = null;




    public void onDisable() {
    	//db.uninitialize();
        log.info("[" + name + "] Version " + version + " (" + codename + ") disabled.");
    }

    public File getDataFolder() {
    	if ( directory == null ) {
    		String pluginDirString = "plugins" + File.separator + "DynamicMarket";
		    if ( !super.getDataFolder().toString().equals(pluginDirString) ) {
		    	log.warning("Jar is not named DynamicMarket.jar!  Beware of multiple DynamicMarket instances being loaded!");
		    	directory = new File(pluginDirString);
		    } else {
		    	directory = super.getDataFolder();
		    }
    	}
    	
        return directory;
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
        
        sqlite = "jdbc:sqlite:" + directory + File.separator + "shop.db";

	  	PluginManager pm = getServer().getPluginManager();
	  	
	  	setupPermissions();
	  	
	  	pluginListener = new iPluginListener();
	  	pm.registerEvent(Event.Type.PLUGIN_ENABLE, pluginListener, Priority.Monitor, this);

        checkLibs();
        setup();
        
        log.info("[" + name + "] Version " + version + " (" + codename + ") enabled.");
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
        ListIterator<JavaPlugin> itr = DynamicMarket.wrappers.listIterator();
        while(itr.hasNext()) {
            JavaPlugin wrap = itr.next();
            if ( wrap.onCommand(sender, cmd, commandLabel, args) ) return true;
        }
        return this.playerListener.parseCommand(sender, cmd.getName(), args, "", defaultShopAccount, defaultShopAccountFree);
    }
    
    public void hookWrapper(JavaPlugin wrap) {
    	DynamicMarket.wrappers.add(wrap);
    	log.info("[" + name + "] Wrapper mode enabled by " + wrap.getDescription().getName());    	
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

        debug = Settings.getBoolean("debug", false);

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
        
        needUpdate = false;
        if (!Settings.keyExists("version")) {
        	needUpdate = true;
        	
        	version = Settings.getString("version", version);
        }

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

        csvFileName = Settings.getString("csv-file", "shopDB.csv");
        csvFilePath = Settings.getString("csv-file-path", getDataFolder() + File.separator);
        //wrapperMode = Settings.getBoolean("wrapper-mode", false);
        opPermissions = Settings.getBoolean("op-permissions", false);
        simplePermissions = Settings.getBoolean("simple-permissions", false);
        wrapperPermissions = Settings.getBoolean("wrapper-permissions", false);

        defaultShopAccount = Settings.getString("default-shop-account", "");
        defaultShopAccountFree = Settings.getBoolean("default-shop-account-free", defaultShopAccountFree);

        transLogFile = Settings.getString("transaction-log-file", transLogFile);
        transLogAutoFlush = Settings.getBoolean("transaction-log-autoflush", transLogAutoFlush);
        
        if ((transLogFile != null) && (!transLogFile.isEmpty())) {
            transLog = new TransactionLogger(this, getDataFolder() + File.separator + transLogFile, transLogAutoFlush);
        } else {
            transLog = new TransactionLogger(this, null, false);
        }
    }
    
    private boolean runUpdate() {
    	// Updates the db to add decimal support
    	boolean update = db.updateTable();
    	
    	if (update) {
    		log.info("[" + name + "] Database updated successfully.");
    		return true;
    	} else {
    		log.warning("[" + name + "] Could not update database!");
    		return false;
    	}
    }
}