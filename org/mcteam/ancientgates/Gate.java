package org.mcteam.ancientgates;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.libs.com.google.gson.reflect.TypeToken;
import org.mcteam.ancientgates.util.DiscUtil;
import org.mcteam.ancientgates.util.FloodUtil;

public class Gate {
	
	private static transient TreeMap<String, Gate> instances = new TreeMap<String, Gate>(String.CASE_INSENSITIVE_ORDER);
	private static transient File file = new File(Plugin.instance.getDataFolder(), "gates.json");
	private static transient String SERVER = "server";
	private static transient String WORLD = "world";
	private static transient String X = "x";
	private static transient String Y = "y";
	private static transient String Z = "z";
	private static transient String YAW = "yaw";
	private static transient String PITCH = "pitch";
	
	private transient String id;
	private List<Location> froms;
	private Location from;
	private Location to;
	private Map<String, String> bungeeto;
	private Boolean entities = Conf.teleportEntitiesDefault;
	private double cost = 0.0;

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
	
	public void addFrom(Location from) {
		if (this.froms == null) {
			this.froms = new ArrayList<Location>();
		}
		if (from == null) {
			this.froms = null;
		} else {
			this.froms.add(from);		
		}
	}
	
	public void delFrom(Location from) {		
		this.froms.remove(from);
	}
	
	public List<Location> getFroms() {
		return froms;
	}

	public void setTo(Location to) {
		this.to = to;
	}

	public Location getTo() {
		return to;
	}
	
	public void setBungeeTo(String server, String to) {
		if (to==null) {
			this.bungeeto = null;
		} else {
			String[] parts = to.split(",");
			this.bungeeto = new HashMap<String, String>();
			this.bungeeto.put(SERVER, server);
			this.bungeeto.put(WORLD, parts[0]);
			this.bungeeto.put(X, parts[1]);
			this.bungeeto.put(Y, parts[2]);
			this.bungeeto.put(Z, parts[3]);
			this.bungeeto.put(YAW, parts[4]);
			this.bungeeto.put(PITCH, parts[5]);
		}
	}
	
	public Map<String, String> getBungeeTo() {
		return bungeeto;
	}
	
	public void setCost(Double cost) {
		this.cost = cost;
	}
	
	public Double getCost() {
		return cost;
	}
	
	public void setTeleportEntities(Boolean teleportEntities) {
		this.entities = teleportEntities;
	}
	
	public Boolean getTeleportEntities() {
		return entities;
	}
	
	public void rename(String id, String newid) {
		Gate gate = instances.remove(id);
		instances.put(newid, gate);
		this.id = newid;
	}
	
	//----------------------------------------------//
	// The Open And Close Methods
	//----------------------------------------------//
	public boolean open() {
		for (Location from : froms) {
			Set<Block> blocks = FloodUtil.getGateFrameBlocks(from.getBlock());
		
			if (blocks == null) {
				return false;
			}
		
			// This is not to do an effect
			// It is to stop portalblocks from destroyingthemself as they can't rely on non created blocks
			for (Block block : blocks) {
				block.setType(Material.GLOWSTONE);
			}
		
			for (Block block : blocks) {
				block.setType(Material.PORTAL);
			}
		}
		
		return true;
	}
	
	public void close() {
		for (Location from : froms) {
			Set<Block> blocks = FloodUtil.getGateFrameBlocks(from.getBlock());
		
			if (blocks != null) {
				for (Block block : blocks) {
					block.setType(Material.AIR);
				}
			}
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
		return gate;
	}
	
	public static void delete(String id) {
		// Remove the gate
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
		
		// Migrate old format
		for (Gate gate : Gate.getAll()) {
			if (gate.from != null) {
				gate.addFrom(gate.from);
				gate.from = null;
			}
		}
		
		save();
			
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
