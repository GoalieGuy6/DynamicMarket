package com.gmail.haloinverse.DynamicMarket;

import com.nijikokun.registerDM.payment.Methods;

import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerListener;

import java.util.logging.Logger;

/**
 * iPluginListener
 * Allows us to hook into permissions even if it is loaded later on.
 *
 * Checks for Plugins on the event that they are enabled,
 * checks the name given with the usual name of the plugin to
 * verify the existence. If the name matches we pass the plugin along
 * to iConomy to utilize in various ways.
 *
 * @author Nijikokun
 */
public class iPluginListener extends ServerListener {
	
	private static final Logger log = Logger.getLogger("Minecraft");
	private Methods Methods = null;
	
	
	public iPluginListener() {
		this.Methods = new Methods();
	}
	@Override
	public void onPluginDisable(PluginDisableEvent event) {
		if (DynamicMarket.economy != null) {
			boolean check = false;
			
			if (this.Methods != null && this.Methods.hasMethod()) {
				check = this.Methods.checkDisabled(event.getPlugin());
			}
			
			if (check) {
				DynamicMarket.setEconomy(null);
				DynamicMarket.econLoaded = false;
				log.info("[" + DynamicMarket.name + "] Economy unlinked.");
			}
		}
	}

	@Override
	public void onPluginEnable(PluginEnableEvent event) {
		if (!this.Methods.hasMethod()) {
			if (this.Methods.setMethod(event.getPlugin())) {
				DynamicMarket.setEconomy(this.Methods.getMethod());
				log.info("[" + DynamicMarket.name + "] Linked with " + DynamicMarket.getEconomy().getName() + " Version " + DynamicMarket.getEconomy().getVersion() + " successfully.");
			}
		}
	}
}