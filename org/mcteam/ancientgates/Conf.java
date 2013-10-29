package org.mcteam.ancientgates;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.mcteam.ancientgates.util.BlockUtil;
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
	public static boolean gateDiagonalOrientations = false;
	
	// Maximum gate fill area
	private static int gateMaxArea = 70;
	
	// Default gate material
	public static String gateMaterialDefault = "PORTAL";
	
	// Gate cooldown/warmup period
	private static int gateCooldownPeriod = 1000;
	
	// BungeeCord settings
	public static boolean bungeeCordSupport = false;
	public static String bungeeTeleportDefault = "LOCATION";
	public static boolean useBungeeMessages = true;
	public static String bungeeJoinMessage = "&e%p came from %s server";
	public static String bungeeQuitMessage = "&e%p went to %s server";
	
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
	private static Boolean customBungeeMessages = null;

	public static int getGateMaxArea() {
		return gateMaxArea*10;
	}
	
	public static long getGateCooldownMillis() {
		return gateCooldownPeriod;
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
		
		// Check gateMaxArea
		if (gateMaxArea > 100) Plugin.log(Level.WARNING, "\"gateMaxArea\" high! May cause stack overflow.");
		if (gateMaxArea > 500) {
			gateMaxArea = 500;
			Plugin.log(Level.WARNING, "\"gateMaxArea\" too high! Limited to 500.");
		}
		
		// Check string values
		if (BlockUtil.asSpawnableGateMaterial(gateMaterialDefault) == null) {
			gateMaterialDefault = "PORTAL";
			Plugin.log(Level.WARNING, "\"gateMaterialDefault\" is invalid. Setting to PORTAL.");
		}
		if (!bungeeTeleportDefault.equals("LOCATION") && !bungeeTeleportDefault.equals("SERVER")) {
			bungeeTeleportDefault = "LOCATION";
			Plugin.log(Level.WARNING, "\"bungeeTeleportDefault\" is invalid. Setting to LOCATION.");
		}
		
		// Migrate old format
		if (useInstantNether != null) {
			useVanillaPortals = !useInstantNether;
			useInstantNether = null;
		}
		if (customBungeeMessages != null) {
			useBungeeMessages = customBungeeMessages;
			customBungeeMessages = null;
		}
		
		save();
		
		return true;
	}
	
}

