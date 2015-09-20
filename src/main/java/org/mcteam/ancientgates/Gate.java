package org.mcteam.ancientgates;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.mcteam.ancientgates.util.DiscUtil;
import org.mcteam.ancientgates.util.FloodUtil;
import org.mcteam.ancientgates.util.TextUtil;
import org.mcteam.ancientgates.util.types.CommandType;
import org.mcteam.ancientgates.util.types.FloodOrientation;
import org.mcteam.ancientgates.util.types.GateMaterial;
import org.mcteam.ancientgates.util.types.InvBoolean;
import org.mcteam.ancientgates.util.types.TeleportType;
import org.mcteam.ancientgates.util.types.WorldCoord;

import com.google.gson.reflect.TypeToken;

public class Gate {

	private static transient TreeMap<String, Gate> instances = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
	private static transient File file = new File(Plugin.instance.getDataFolder(), "gates.json");
	private static transient String SERVER = "server";
	private static transient String WORLD = "world";
	private static transient String X = "x";
	private static transient String Y = "y";
	private static transient String Z = "z";
	private static transient String YAW = "yaw";
	private static transient String PITCH = "pitch";

	// Gates
	private transient String id;
	private List<Location> froms;
	private List<Location> tos;
	private List<Map<String, String>> bungeetos;
	private TeleportType bungeetype;
	private Boolean entities = Conf.teleportEntitiesDefault;
	private Boolean vehicles = Conf.teleportVehiclesDefault;
	private InvBoolean inventory = Conf.teleportInventoryDefault;
	private GateMaterial material = Conf.gateMaterialDefault;
	private String command;
	private CommandType commandtype;
	private String msg;
	private double cost = 0.0;

	// Legacy entries
	private Location to;
	private Location from;
	private Map<String, String> bungeeto;

	private transient Set<WorldCoord> frameBlockCoords;
	private transient Set<WorldCoord> surroundingFrameBlockCoords;
	private transient Map<WorldCoord, FloodOrientation> portalBlockCoords;
	private transient Set<WorldCoord> surroundingPortalBlockCoords;

	public Gate() {
	}

	// -------------------------------------------- //
	// Getters And Setters
	// -------------------------------------------- //
	public void setId(final String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void addFrom(final Location from1) {
		if (this.froms == null) {
			this.froms = new ArrayList<>();
		}
		if (from1 == null) {
			this.froms = null;
		} else {
			this.froms.add(from1);
		}
	}

	public void delFrom(final Location from1) {
		this.froms.remove(from1);
	}

	public List<Location> getFroms() {
		return froms;
	}

	public void addTo(final Location to1) {
		if (this.tos == null) {
			this.tos = new ArrayList<>();
		}
		if (to1 == null) {
			this.tos = null;
		} else {
			this.tos.add(to1);
		}
	}

	public void delTo(final Location to1) {
		this.tos.remove(to1);
		if (this.tos.size() == 0)
			this.tos = null;
	}

	public List<Location> getTos() {
		return tos;
	}

	public Location getTo() {
		if (tos == null)
			return null;
		final Random randomizer = new Random();
		return tos.get(randomizer.nextInt(tos.size()));
	}

	public void addBungeeTo(final String server, final String to1) {
		if (this.bungeetos == null) {
			this.bungeetos = new ArrayList<>();
		}
		if (to1 == null) {
			this.bungeetos = null;
			this.bungeetype = null;
		} else {
			final String[] parts = to1.split(",");
			final Map<String, String> bungeeto1 = new HashMap<>();
			bungeeto1.put(SERVER, server);
			bungeeto1.put(WORLD, parts[0]);
			bungeeto1.put(X, parts[1]);
			bungeeto1.put(Y, parts[2]);
			bungeeto1.put(Z, parts[3]);
			bungeeto1.put(YAW, parts[4]);
			bungeeto1.put(PITCH, parts[5]);
			this.bungeetos.add(bungeeto1);
			if (this.bungeetype == null)
				this.bungeetype = Conf.bungeeTeleportDefault;
		}
	}

	public void delBungeeTo(final String server, final String to1) {
		final String[] parts = to1.split(",");
		final Map<String, String> bungeeto1 = new HashMap<>();
		bungeeto1.put(SERVER, server);
		bungeeto1.put(WORLD, parts[0]);
		bungeeto1.put(X, parts[1]);
		bungeeto1.put(Y, parts[2]);
		bungeeto1.put(Z, parts[3]);
		bungeeto1.put(YAW, parts[4]);
		bungeeto1.put(PITCH, parts[5]);
		this.bungeetos.remove(bungeeto1);
		if (this.bungeetos.size() == 0)
			this.bungeetos = null;
	}

	public List<Map<String, String>> getBungeeTos() {
		return bungeetos;
	}

	public Map<String, String> getBungeeTo() {
		if (bungeetos == null)
			return null;
		final Random randomizer = new Random();
		return bungeetos.get(randomizer.nextInt(bungeetos.size()));
	}

	public void setBungeeType(final String bungeeType) {
		this.bungeetype = TeleportType.fromName(bungeeType.toUpperCase());
	}

	public TeleportType getBungeeType() {
		return bungeetype;
	}

	public void setMessage(final String msg) {
		this.msg = msg.isEmpty() ? null : msg;
	}

	public String getMessage() {
		return msg;
	}

	public void setCommand(final String command) {
		this.command = command.isEmpty() ? null : command;
		if (command.isEmpty())
			this.commandtype = null;
	}

	public String getCommand() {
		return command;
	}

	public void setCommandType(final String commandType) {
		this.commandtype = CommandType.fromName(commandType.toUpperCase());
	}

	public CommandType getCommandType() {
		return commandtype;
	}

	public void setCost(final Double cost) {
		this.cost = cost;
	}

	public Double getCost() {
		return cost;
	}

	public void setTeleportEntities(final Boolean teleportEntities) {
		this.entities = teleportEntities;
	}

	public Boolean getTeleportEntities() {
		return entities;
	}

	public void setTeleportVehicles(final Boolean teleportVehicles) {
		this.vehicles = teleportVehicles;
	}

	public Boolean getTeleportVehicles() {
		return vehicles;
	}

	public void setTeleportInventory(final String teleportInventory) {
		this.inventory = InvBoolean.fromName(teleportInventory.toUpperCase());
	}

	public InvBoolean getTeleportInventory() {
		return inventory;
	}

	public void setMaterial(final String material) {
		this.material = GateMaterial.fromName(material.toUpperCase());
	}

	public Material getMaterial() {
		return material.getMaterial();
	}

	public String getMaterialStr() {
		return material.name();
	}

	public void rename(final String id1, final String newid) {
		final Gate gate = instances.remove(id1);
		instances.put(newid, gate);
		this.id = newid;
	}

	public Set<WorldCoord> getFrameBlocks() {
		return frameBlockCoords;
	}

	public Set<WorldCoord> getSurroundingFrameBlocks() {
		return surroundingFrameBlockCoords;
	}

	public Map<WorldCoord, FloodOrientation> getPortalBlocks() {
		return portalBlockCoords;
	}

	public Set<WorldCoord> getSurroundingPortalBlocks() {
		return surroundingPortalBlockCoords;
	}

	// ----------------------------------------------//
	// Persistance and entity management
	// ----------------------------------------------//
	public static Gate get(final String id) {
		return instances.get(id);
	}

	public static boolean exists(final String id) {
		return instances.containsKey(id);
	}

	public static Gate create(final String id) {
		final Gate gate = new Gate();
		gate.id = id;
		instances.put(gate.id, gate);
		Plugin.log("created new gate " + gate.id);
		return gate;
	}

	public static void delete(final String id) {
		// Remove the gate
		instances.remove(id);
	}

	public static boolean save() {
		try {
			DiscUtil.write(file, Plugin.gson.toJson(instances));
		} catch (final IOException e) {
			Plugin.log("Failed to save the gates to disk due to I/O exception.");
			e.printStackTrace();
			return false;
		} catch (final NullPointerException e) {
			Plugin.log("Failed to save the gates to disk due to NPE.");
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public static boolean load() {
		Plugin.log("Loading gates from disk");
		if (!file.exists()) {
			Plugin.log("No gates to load from disk. Creating new file.");
			save();
			return true;
		}

		try {
			final Type type = new TypeToken<Map<String, Gate>>() {
			}.getType();
			final Map<String, Gate> instancesFromFile = Plugin.gson.fromJson(DiscUtil.read(file), type);
			instances.clear();
			instances.putAll(instancesFromFile);
		} catch (final IOException e) {
			e.printStackTrace();
			return false;
		}

		fillIds();

		// Check enum values
		for (final Gate gate : Gate.getAll()) {
			if (gate.material == null) {
				gate.material = GateMaterial.PORTAL;
				Plugin.log(Level.WARNING, "Gate \"" + gate.getId() + "\" { \"material\" } is invalid. Valid materials are: " + TextUtil.implode(Arrays.asList(GateMaterial.names), ", ") + ".");
			}
			if (gate.inventory == null) {
				gate.inventory = InvBoolean.TRUE;
				Plugin.log(Level.WARNING, "Gate \"" + gate.getId() + "\" { \"inventory\" } is invalid. Valid options are: " + TextUtil.implode(Arrays.asList(InvBoolean.names), ", ") + ".");
			}
			if (gate.bungeetos != null && gate.bungeetype == null) {
				gate.bungeetype = TeleportType.LOCATION;
				Plugin.log(Level.WARNING, "Gate \"" + gate.getId() + "\" { \"bungeetype\" } is invalid. Valid types are: " + TextUtil.implode(Arrays.asList(TeleportType.names), ", ") + ".");
			}
			if (gate.command != null && gate.commandtype == null) {
				gate.commandtype = CommandType.PLAYER;
				Plugin.log(Level.WARNING, "Gate \"" + gate.getId() + "\" { \"commandtype\" } is invalid. Valid types are: " + TextUtil.implode(Arrays.asList(CommandType.names), ", ") + ".");
			}
		}

		// Migrate old format
		for (final Gate gate : Gate.getAll()) {
			if (gate.from != null) {
				gate.addFrom(gate.from);
				gate.from = null;
			}

			if (gate.to != null) {
				gate.addTo(gate.to);
				gate.to = null;
			}

			if (gate.bungeeto != null) {
				gate.bungeetos = new ArrayList<>();
				gate.bungeetos.add(gate.bungeeto);
				gate.bungeeto = null;
			}

			if (gate.bungeetos != null && gate.bungeetype == null) {
				gate.bungeetype = Conf.bungeeTeleportDefault;
			}
		}

		// Cleanup non-existent worlds
		for (final Gate gate : Gate.getAll()) {
			if (gate.froms != null) {
				final Iterator<Location> it = gate.froms.iterator();
				while (it.hasNext()) {
					final Location from = it.next();
					if (from == null)
						it.remove();
				}
				if (gate.froms.isEmpty())
					gate.froms = null;
			}

			if (gate.tos != null) {
				final Iterator<Location> it = gate.tos.iterator();
				while (it.hasNext()) {
					final Location from = it.next();
					if (from == null)
						it.remove();
				}
				if (gate.tos.isEmpty())
					gate.tos = null;
			}
		}

		save();

		return true;
	}

	public static Collection<Gate> getAll() {
		return instances.values();
	}

	public static void fillIds() {
		for (final Entry<String, Gate> entry : instances.entrySet()) {
			entry.getValue().setId(entry.getKey());
		}
	}

	// ----------------------------------------------//
	// The Block data management
	// ----------------------------------------------//
	public boolean dataPopulate() {
		// Clear previous data
		dataClear();

		if (froms == null)
			return false;

		// Loop through all from locations
		for (final Location from1 : froms) {
			final Entry<FloodOrientation, Set<Block>> flood = FloodUtil.getBestAirFlood(from1.getBlock(), FloodOrientation.values());
			if (flood == null)
				return false;

			// Force vertical PORTALs and horizontal ENDER_PORTALs
			final FloodOrientation orientation = flood.getKey();

			// Now we add the portal blocks as world coords to the lookup maps.
			final Set<Block> portalBlocks = FloodUtil.getPortalBlocks(from1.getBlock(), orientation);
			if (portalBlocks == null)
				return false;

			for (final Block portalBlock : portalBlocks) {
				portalBlockCoords.put(new WorldCoord(portalBlock), orientation);
			}

			// Now we add the frame blocks as world coords to the lookup maps.
			final Set<Block> frameBlocks = FloodUtil.getFrameBlocks(portalBlocks, orientation);
			for (final Block frameBlock : frameBlocks) {
				frameBlockCoords.add(new WorldCoord(frameBlock));
			}

			// Now we add the surrounding blocks as world coords to the lookup maps.
			Set<Block> surroundingBlocks = FloodUtil.getSurroundingBlocks(portalBlocks, frameBlocks, orientation);
			for (final Block surroundingBlock : surroundingBlocks) {
				surroundingPortalBlockCoords.add(new WorldCoord(surroundingBlock));
			}
			surroundingBlocks = FloodUtil.getSurroundingBlocks(frameBlocks, portalBlocks, orientation);
			for (final Block surroundingBlock : surroundingBlocks) {
				surroundingFrameBlockCoords.add(new WorldCoord(surroundingBlock));
			}
		}
		return true;
	}

	public void dataClear() {
		portalBlockCoords = new HashMap<>();
		frameBlockCoords = new HashSet<>();
		surroundingPortalBlockCoords = new HashSet<>();
		surroundingFrameBlockCoords = new HashSet<>();
	}

}
