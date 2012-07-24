package org.mcteam.ancientgates;

import java.io.File;
import java.io.IOException;

import org.bukkit.ChatColor;
import org.mcteam.ancientgates.util.DiscUtil;


public class Conf {
	public static transient File file = new File(Plugin.instance.getDataFolder(), "conf.json");
	
	// Colors
	public static ChatColor colorMember = ChatColor.GREEN;
	public static ChatColor colorAlly = ChatColor.LIGHT_PURPLE;
	public static ChatColor colorNeutral = ChatColor.WHITE;
	public static ChatColor colorEnemy = ChatColor.RED;
	
	public static ChatColor colorSystem = ChatColor.YELLOW;
	public static ChatColor colorChrome = ChatColor.GOLD;
	public static ChatColor colorCommand = ChatColor.AQUA;
	public static ChatColor colorParameter = ChatColor.DARK_AQUA;
	
	private static double gateSearchRadius = 7.0;
	
	static {
		
	}
	
	public static double getGateSearchRadius() {
		return gateSearchRadius;
	}
	
	public static int getGateMaxArea() {
		return (int)gateSearchRadius*10;
	}
	
	// -------------------------------------------- //
	// Persistance
	// -------------------------------------------- //
	
	public static boolean save() {
		//Factions.log("Saving config to disk.");
		
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
		
		return true;
	}
}

