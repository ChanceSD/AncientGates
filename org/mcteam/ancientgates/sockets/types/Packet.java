package org.mcteam.ancientgates.sockets.types;

import java.util.Map;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.mcteam.ancientgates.util.EntityUtil;
import org.mcteam.ancientgates.util.ItemStackUtil;
import org.mcteam.ancientgates.util.TeleportUtil;

public class Packet {
	
	public String command;
	public String responseCommand;
	public String[] args;
	
	//----------------------------------------------//
	// Constructors
	//----------------------------------------------//
	// Command packet 
	public Packet(String command, String[] args) {
		this.command = command;
		this.args = args;
	}
	
	// Command packet (with response)
	public Packet(String command, String responseTo, String[] args) {
		this.command = command;
		this.responseCommand = responseTo;
		this.args = args;
	}
	
	// Spawn Entity packet
	public Packet(Entity entity, EntityType entityType, Map<String, String> destination) {
		this.command = "spawnentity";
		// Build the arguments, format is <entityId>,<entityWorld>,<entityTypeId>,<entityTypeData>,<location>
		this.args = new String[] {String.valueOf(entity.getEntityId()), entity.getWorld().getName(), String.valueOf(entityType.getTypeId()), EntityUtil.getEntityTypeData(entity), TeleportUtil.locationToString(destination)};
	}

	// Spawn Vehicle packet
	public Packet(Entity vehicle, double velocity, Map<String, String> destination) {
		this.command = "spawnvehicle";
		// Build the arguments, format is <vehicleId>,<vehicleWorld>,<vehicleTypeId>,<velocity>,<location>[,<entityId>,<entityTypeId>,<entityTypeData>]
		this.args = new String[] {String.valueOf(vehicle.getEntityId()), vehicle.getWorld().getName(), String.valueOf(vehicle.getType().getTypeId()), String.valueOf(velocity), TeleportUtil.locationToString(destination), null, null, null};
	}
	
	//----------------------------------------------//
	// Setters
	//----------------------------------------------//
	// Ammend arg to command packet
	public void ammendArg(String arg, int pos) {
		this.args[pos] = arg;
	}
	
	// Append passenger args to vehicle packet
	public void addPassenger(Entity passenger) {
		this.ammendArg(String.valueOf(passenger.getEntityId()), 5);
		this.ammendArg(String.valueOf(passenger.getType().getTypeId()), 6);
		this.ammendArg(EntityUtil.getEntityTypeData(passenger), 7);
	}
	
	// Append itemstack arg to vehicle packet
	public void addItemStack(ItemStack[] itemStack) {
		this.ammendArg(ItemStackUtil.itemStackToString(itemStack), 5);
	}
	
}
