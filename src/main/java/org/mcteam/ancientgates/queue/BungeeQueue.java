package org.mcteam.ancientgates.queue;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.mcteam.ancientgates.queue.types.BungeeQueueType;
import org.mcteam.ancientgates.util.EntityUtil;
import org.mcteam.ancientgates.util.TeleportUtil;
import org.mcteam.ancientgates.util.types.CommandType;

public class BungeeQueue {

	private final BungeeQueueType queueType;

	private String playerName;
	private String server;
	private String destination;

	private String vehicleTypeName;
	private Double velocity;

	private String entityTypeName;
	private String entityTypeData;

	private String itemStack;

	private String message;

	private String command;
	private CommandType commandType;

	// ----------------------------------------------//
	// Constructors
	// ----------------------------------------------//
	// Player queue (could be riding an entity)
	public BungeeQueue(final String playerName, final String entityTypeName, final String entityTypeData, final String server, final String destination) {
		this.queueType = BungeeQueueType.PLAYER;
		this.playerName = playerName;
		this.entityTypeName = entityTypeName;
		this.entityTypeData = entityTypeData;
		this.server = server;
		if (!destination.equals("null"))
			this.destination = destination;
	}

	// Player queue (with command and message)
	public BungeeQueue(final String playerName, final String entityTypeName, final String entityTypeData, final String server, final String destination, final String command, final CommandType commandType, final String message) {
		this(playerName, entityTypeName, entityTypeData, server, destination);
		this.command = command;
		this.commandType = commandType;
		this.message = message;
	}

	// Player passenger queue
	public BungeeQueue(final String playerName, final String server, final String vehicleTypeName, final double velocity, final String destination) {
		this.queueType = BungeeQueueType.PASSENGER;
		this.playerName = playerName;
		this.server = server;
		this.vehicleTypeName = vehicleTypeName;
		this.velocity = velocity;
		if (!destination.equals("null"))
			this.destination = destination;
	}

	// Player passenger queue (with command and message)
	public BungeeQueue(final String playerName, final String server, final String vehicleTypeName, final double velocity, final String destination, final String command, final CommandType commandType, final String message) {
		this(playerName, server, vehicleTypeName, velocity, destination);
		this.command = command;
		this.commandType = commandType;
		this.message = message;
	}

	// Vehicle queue
	public BungeeQueue(final String vehicleTypeName, final double velocity, final String destination) {
		this.queueType = BungeeQueueType.VEHICLE;
		this.vehicleTypeName = vehicleTypeName;
		this.velocity = velocity;
		this.destination = destination;
	}

	// Vehicle queue (with contents)
	public BungeeQueue(final String vehicleTypeName, final double velocity, final String destination, final String entityItemStack) {
		this(vehicleTypeName, velocity, destination);
		this.itemStack = entityItemStack;
	}

	// Entity queue
	public BungeeQueue(final String entityTypeName, final String entityTypeData, final String destination) {
		this.queueType = BungeeQueueType.ENTITY;
		this.entityTypeName = entityTypeName;
		this.entityTypeData = entityTypeData;
		this.destination = destination;
	}

	// Entity passenger queue
	public BungeeQueue(final String vehicleTypeName, final double velocity, final String destination, final String entityTypeName, final String entityTypeData) {
		this.queueType = BungeeQueueType.PASSENGER;
		this.vehicleTypeName = vehicleTypeName;
		this.velocity = velocity;
		this.destination = destination;
		this.entityTypeName = entityTypeName;
		this.entityTypeData = entityTypeData;
	}

	// ----------------------------------------------//
	// Getters
	// ----------------------------------------------//
	public BungeeQueueType getQueueType() {
		return this.queueType;
	}

	public String getPlayerName() {
		return this.playerName;
	}

	public String getServer() {
		return this.server;
	}

	public String getVehicleTypeName() {
		return this.vehicleTypeName;
	}

	public Double getVelocity() {
		return this.velocity;
	}

	public String getEntityTypeData() {
		return this.entityTypeData;
	}

	public String getItemStack() {
		return this.itemStack;
	}

	public String getCommand() {
		return this.command;
	}

	public CommandType getCommandType() {
		return this.commandType;
	}

	public String getMessage() {
		return this.message;
	}

	// ----------------------------------------------//
	// Converters
	// ----------------------------------------------//
	public Location getDestination() {
		if (destination != null)
			return TeleportUtil.stringToLocation(destination);
		return null;
	}

	public EntityType getEntityType() {
		if (entityTypeName != null)
			return EntityUtil.entityType(entityTypeName);
		return null;
	}

	public EntityType getVehicleType() {
		if (vehicleTypeName != null)
			return EntityUtil.entityType(vehicleTypeName);
		return null;
	}

}
