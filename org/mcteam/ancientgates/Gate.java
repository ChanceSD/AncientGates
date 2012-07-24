package org.mcteam.ancientgates;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.mcteam.ancientgates.gson.reflect.TypeToken;
import org.mcteam.ancientgates.util.DiscUtil;
import org.mcteam.ancientgates.util.FloodUtil;


public class Gate {
	private static transient TreeMap<String, Gate> instances = new TreeMap<String, Gate>(String.CASE_INSENSITIVE_ORDER);
	private static transient File file = new File(Plugin.instance.getDataFolder(), "gates.json");
	
	private transient String id;
	private Location from;
	private Location to;
	
	public Gate() {
		
	}
	
	// -------------------------------------------- //
	// Getters And Setters
	// -------------------------------------------- //
	
	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setFrom(Location from) {
		this.from = from;
	}

	public Location getFrom() {
		return from;
	}

	public void setTo(Location to) {
		this.to = to;
	}

	public Location getTo() {
		return to;
	}
	
	//----------------------------------------------//
	// The Open And Close Methods
	//----------------------------------------------//
	
	public boolean open() {
		Set<Block> blocks = FloodUtil.getGateFrameBlocks(from.getBlock());
		
		if (blocks == null) {
			return false;
		}
		
		// This is not to do an effect
		// It is to stop portalblocks from destroyingthemself as they cant rely on non created blocks :P
		for (Block block : blocks) {
			block.setType(Material.GLOWSTONE);
		}
		
		for (Block block : blocks) {
			block.setType(Material.PORTAL);
		}
		
		return true;
	}
	
	public void close() {
		Set<Block> blocks = FloodUtil.getGateFrameBlocks(from.getBlock());
		
		for (Block block : blocks) {
			block.setType(Material.AIR);
		}
	}
	
	//----------------------------------------------//
	// Persistance and entity management
	//----------------------------------------------//
	
	public static Gate get(String id) {
		return instances.get(id);
	}
	
	public static boolean exists(String id) {
		return instances.containsKey(id);
	}
	
	public static Gate create(String id) {
		Gate gate = new Gate();
		gate.id = id;
		instances.put(gate.id, gate);
		Plugin.log("created new gate "+gate.id);
		//faction.save();
		return gate;
	}
	
	public static void delete(String id) {
		// Remove the faction
		instances.remove(id);
	}
	
	public static boolean save() {
		try {
			DiscUtil.write(file, Plugin.gson.toJson(instances));
		} catch (IOException e) {
			Plugin.log("Failed to save the gates to disk due to I/O exception.");
			e.printStackTrace();
			return false;
		} catch (NullPointerException e) {
			Plugin.log("Failed to save the gates to disk due to NPE.");
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	public static boolean load() {
		Plugin.log("Loading gates from disk");
		if ( ! file.exists()) {
			Plugin.log("No gates to load from disk. Creating new file.");
			save();
			return true;
		}
		
		try {
			Type type = new TypeToken<Map<String, Gate>>(){}.getType();
			Map<String, Gate> instancesFromFile = Plugin.gson.fromJson(DiscUtil.read(file), type);
			instances.clear();
			instances.putAll(instancesFromFile);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
		fillIds();
			
		return true;
	}
	
	public static Collection<Gate> getAll() {
		return instances.values();
	}
	
	public static void fillIds() {
		for(Entry<String, Gate> entry : instances.entrySet()) {
			entry.getValue().setId(entry.getKey());
		}
	}
}
