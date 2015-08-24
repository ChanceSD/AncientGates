package org.mcteam.ancientgates.util.types;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import org.mcteam.ancientgates.Plugin;
import org.mcteam.ancientgates.util.EntityUtil;
import org.mcteam.ancientgates.util.ItemStackUtil;
import org.mcteam.ancientgates.util.TeleportUtil;

public class PluginMessage {
	
	private static final String SERVER = "server";
	
	private BungeeChannel channel;
	private String toServer;
	private String playerName;
	
	private String destination;
	
	private String fromServer;
	private String message;	
	
	private String entityTypeName;
	private String entityTypeData;
	
	private String vehicleTypeName;
	private String velocity;
	
	private String itemStack;
	
	private String command;
	private String commandType;
	private String[] parameters;
	
	//----------------------------------------------//
	// Constructor
	//----------------------------------------------//
	// Player teleport message
	public PluginMessage(Player player, String toServer, String fromServer, String command, CommandType commandType, String message) {
		this.channel = BungeeChannel.AGBungeeTele;
		this.toServer = toServer;
		this.destination = "null";
		this.playerName = player.getName();
		this.fromServer = fromServer;
		this.command = command;
		this.commandType = (commandType == null) ? "null" : commandType.name();
		this.message = message;
	}
	
	// Player teleport message (with destination)
	public PluginMessage(Player player, Map<String, String> destination, String fromServer, String command, CommandType commandType, String message) {
		this(player, destination.get(SERVER), fromServer, command, commandType, message);
		this.destination = TeleportUtil.locationToString(destination);
	}
	
	// Player teleport message (riding entity)
	public PluginMessage(Player player, Entity entity, Map<String, String> destination, String fromServer, String command, CommandType commandType, String message) {
		this(player, destination, fromServer, command, commandType, message);
		this.entityTypeName = entity.getType().name();
		this.entityTypeData = EntityUtil.getEntityTypeData(entity);
	}
	
	// Vehicle teleport message
	public PluginMessage(Player player, EntityType vehicleType, double velocity, Map<String, String> destination, String fromServer, String command, CommandType commandType, String message) {
		this.channel = BungeeChannel.AGBungeeVehicleTele;	
		this.toServer = destination.get(SERVER);
		this.destination = TeleportUtil.locationToString(destination);
		this.playerName = player.getName();
		this.vehicleTypeName = vehicleType.name();
		this.velocity = String.valueOf(velocity);
		this.fromServer = fromServer;
		this.command = command;
		this.commandType = (commandType == null) ? "null" : commandType.name();
		this.message = message;
	}
	
	// Entity spawn message
	public PluginMessage(EntityType entityType, Entity entity, Map<String, String> destination) {
		this.channel = BungeeChannel.AGBungeeSpawn;	
		this.toServer = destination.get(SERVER);
		this.destination = TeleportUtil.locationToString(destination);
		this.entityTypeName = entityType.name();
		if (entityType.equals(EntityType.DROPPED_ITEM)) {
			this.entityTypeData = ItemStackUtil.itemStackToString(((Item)entity).getItemStack()); // Dropped ItemStack
		} else {
			this.entityTypeData = EntityUtil.getEntityTypeData(entity); // Entity
		}
	}
	
	// Vehicle spawn message
	public PluginMessage(EntityType vehicleType, double velocity, Map<String, String> destination) {
		this.channel = BungeeChannel.AGBungeeVehicleSpawn;	
		this.toServer = destination.get(SERVER);
		this.destination = TeleportUtil.locationToString(destination);
		this.vehicleTypeName = vehicleType.name();
		this.velocity = String.valueOf(velocity);
	}
	
	// AncientGates command message
	public PluginMessage(String command, Player player, String toServer, String[] parameters) {
		this.channel = BungeeChannel.AGBungeeCom;	
		this.toServer = toServer;
		this.playerName = player.getName();
		this.command = command;
		this.parameters = parameters;
	}
	
	// BungeeCord command message
	public PluginMessage(String command) {
		this.command = command;
	}
	
	// BungeeCord command message (with parameters)
	public PluginMessage(String command, String... parameters) {
		this.command = command;
		this.parameters = parameters;
	}
	
	//----------------------------------------------//
	// Setters
	//----------------------------------------------//
	// Append entity info
	public void addEntity(Entity entity) {
		this.entityTypeName = entity.getType().name();
		this.entityTypeData = EntityUtil.getEntityTypeData(entity);
	}
	// Append item stack info
	public void addItemStack(ItemStack[] itemStack) {
		this.itemStack = ItemStackUtil.itemStackToString(itemStack);
	}
	
	//----------------------------------------------//
	// Converters
	//----------------------------------------------//
	public byte[] toByteArray() {
		// BungeeCord forward message
		if (this.channel != null) {
			// Build the message
			String msg = "";
			if (this.channel == BungeeChannel.AGBungeeTele) {
				// Format is <player>#@#<destination>#@#<fromServer>#@#<command>#@#<commandType>#@#<message>[#@#<entityTypeName>#@#<entityTypeData>]
				if (this.entityTypeName != null) {
					msg = this.playerName + "#@#" + this.destination + "#@#" + this.fromServer + "#@#" + this.command + "#@#" + this.commandType + "#@#" + this.message + "#@#" + this.entityTypeName + "#@#" + this.entityTypeData;
				} else {
					msg = this.playerName + "#@#" + this.destination + "#@#" + this.fromServer + "#@#" + this.command + "#@#" + this.commandType + "#@#" + this.message;
				}
			} else if (this.channel == BungeeChannel.AGBungeeSpawn) {
				// Format is <entityTypeName>#@#<entityTypeData>#@#<destination>
				msg = this.entityTypeName + "#@#" + this.entityTypeData + "#@#" + this.destination;
			} else if (this.channel == BungeeChannel.AGBungeeVehicleTele) {
				// Format is <player>#@#<vehicleTypeName>#@#<velocity>#@#<destination>#@#<fromServerName>#@#<command>#@#<commandType>#@#<message>
				msg = this.playerName + "#@#" + this.vehicleTypeName + "#@#" + this.velocity + "#@#" + this.destination + "#@#" + this.fromServer + "#@#" + this.command + "#@#" + this.commandType + "#@#" + this.message;
			} else if (this.channel == BungeeChannel.AGBungeeVehicleSpawn) {
				// Format is <vehicleTypeName>#@#<velocity>#@#<destination>[#@#<entityTypeName>#@#<entityTypeData>|#@#<itemStack>]
				if (this.entityTypeName != null) {
					msg = this.vehicleTypeName + "#@#" + this.velocity + "#@#" + this.destination + "#@#" + this.entityTypeName + "#@#" + this.entityTypeData;
				} else if (this.itemStack != null) {
					msg = this.vehicleTypeName + "#@#" + this.velocity + "#@#" + this.destination + "#@#" + this.itemStack;
				} else {
					msg = this.vehicleTypeName + "#@#" + this.velocity + "#@#" + this.destination;	
				}
			} else if (this.channel == BungeeChannel.AGBungeeCom) {
				// Format is <command>#@#<player>#@#<parameters>
				msg = this.command + "#@#" + this.playerName;
				for (String parameter : this.parameters) {
					msg += "#@#" + parameter;
				}
			}
		
			// Build the message data, sent over the <channel> BungeeCord channel
			ByteArrayOutputStream b = new ByteArrayOutputStream();
			DataOutputStream out = new DataOutputStream(b);
			try {
				out.writeUTF("Forward");
				out.writeUTF(this.toServer);			// Server
				out.writeUTF(this.channel.toString());	// Channel
				out.writeShort(msg.length()); 			// Data Length
				out.writeBytes(msg); 					// Data
				return b.toByteArray();
			} catch (IOException ex) {
				if (this.channel == BungeeChannel.AGBungeeCom) Bukkit.getPlayer(this.playerName).sendMessage("Error sending command externally via BungeeCord.");
				Plugin.log.severe("Error sending BungeeCord " + this.channel.toString() + " packet");
				ex.printStackTrace();
				return null;
			}
		// BungeeCord command message
		} else {
			ByteArrayOutputStream b = new ByteArrayOutputStream();
			DataOutputStream out = new DataOutputStream(b);
			try {
				out.writeUTF(this.command);
				if (this.parameters != null) {
					for (String parameter : this.parameters) out.writeUTF(parameter);
				}
				return b.toByteArray();
			} catch (IOException ex) {
				Plugin.log.severe("Error sending BungeeCord " + this.command + " packet");
				ex.printStackTrace();
				return null;
			}
		}
	}
	
}