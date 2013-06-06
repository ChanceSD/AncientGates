package org.mcteam.ancientgates;

import java.io.File;
import java.io.IOException;

import org.bukkit.ChatColor;
import org.mcteam.ancientgates.util.DiscUtil;

public class Conf {
	
	public static transient File file = new File(Plugin.instance.getDataFolder(), "conf.json");
	
	// Colors
	public static ChatColor colorAlly = ChatColor.LIGHT_PURPLE;
	public static ChatColor colorSystem = ChatColor.YELLOW;
	public static ChatColor colorChrome = ChatColor.GOLD;
	public static ChatColor colorCommand = ChatColor.AQUA;
	public static ChatColor colorParameter = ChatColor.DARK_AQUA;
	
	// Flags
	public static boolean enforceAccess = false;
	public static boolean useEconomy = false;
	public static boolean useVanillaPortals = false;
	public static boolean teleportEntitiesDefault = true;
	public static boolean teleportVehiclesDefault = false;
	
	// Maximum gate fill area
	private static int gateMaxArea = 70;
	
	// Default gate material
	public static String gateMaterialDefault = "PORTAL";
	
	// BungeeCord settings
	public static boolean bungeeCordSupport = false;
	public static String bungeeServerName = "";
	
	// Socket comms settings
	public static boolean useSocketComms = false;
	public static int socketCommsPort = 18001;
	public static String socketCommsPass = "agserver1";
	
	// Enable auto-update
	public static boolean autoUpdate = true;
	
	// Enable debug msgs
	public static boolean debug = false; 
	
	// Legacy entries
	private static Boolean useInstantNether = null;

	public static int getGateMaxArea() {
		return gateMaxArea*10;
	}
	
	// -------------------------------------------- //
	// Persistance
	// -------------------------------------------- //
	public static boolean save() {
		try {
			DiscUtil.write(file, Plugin.gson.toJson(new Conf()));
		} catch (IOException e) {
			e.printStackTrace();
			Plugin.log("Failed to save the config to disk.");
			return false;
		}
		return true;
	}
	
	public static boolean load() {
		Plugin.log("Loading conf from disk");
		
		if ( ! file.exists()) {
			Plugin.log("No conf to load from disk. Creating new file.");
			save();
			return true;
		}
		
		try {
			Plugin.gson.fromJson(DiscUtil.read(file), Conf.class);
		} catch (IOException e) {
			e.printStackTrace();
			Plugin.log("Failed to load the config from disk.");
			return false;
		}
		
		// Migrate old format
		if (useInstantNether != null) {
			useVanillaPortals = !useInstantNether;
			useInstantNether = null;
		}
		
		save();
		
		return true;
	}
	
}

