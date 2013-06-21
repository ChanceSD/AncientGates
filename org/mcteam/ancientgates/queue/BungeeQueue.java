package org.mcteam.ancientgates.queue;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import org.mcteam.ancientgates.queue.types.BungeeQueueType;
import org.mcteam.ancientgates.util.TeleportUtil;

public class BungeeQueue {

	private BungeeQueueType queueType;
	
	private String playerName;
	private String server;
	private String destination;
	
	private Integer vehicleTypeId;
	private Double velocity;
	
	private Integer entityTypeId;
	private String entityTypeData;
	
	private String itemStack;
	
	private String message;
	
	//----------------------------------------------//
	// Constructors
	//----------------------------------------------//
	// Player queue
	public BungeeQueue(Player player, String server, String destination) {
		this.queueType = BungeeQueueType.PLAYER;
		this.playerName = player.getName();
		this.server = server;
		this.destination = destination;
	}
	
	// Player queue (with message)
	public BungeeQueue(Player player, String server, String destination, String message) {
		this(player, server, destination);
		this.message = message;
	}
	
	// Player passenger queue
	public BungeeQueue(Player player, String server, int vehicleTypeId, double velocity, String destination) {
		this.queueType = BungeeQueueType.PASSENGER;
		this.playerName = player.getName();
		this.server = server;
		this.vehicleTypeId = vehicleTypeId;
		this.velocity = velocity;
		this.destination = destination;
	}
	
	// Player passenger queue (with message)
	public BungeeQueue(Player player, String server, int vehicleTypeId, double velocity, String destination, String message) {
		this(player, server, vehicleTypeId, velocity, destination);
		this.message = message;
	}
	
	// Vehicle queue
	public BungeeQueue(int vehicleTypeId, double velocity, String destination) {
		this.queueType = BungeeQueueType.VEHICLE;
		this.vehicleTypeId = vehicleTypeId;
		this.velocity = velocity;
		this.destination = destination;
	}
	
	// Vehicle queue (with contents)
	public BungeeQueue(int vehicleTypeId, double velocity, String destination, String entityItemStack) {
		this(vehicleTypeId, velocity, destination);
		this.itemStack = entityItemStack;
	}
	
	// Entity queue
	public BungeeQueue(int entityTypeId, String entityTypeData, String destination) {
		this.queueType = BungeeQueueType.ENTITY;
		this.entityTypeId = entityTypeId;
		this.entityTypeData = entityTypeData;
		this.destination = destination;
	}
	
	//  Entity passenger queue
	public BungeeQueue(int vehicleTypeId, double velocity, String destination, int entityTypeId, String entityTypeData) {
		this.queueType = BungeeQueueType.PASSENGER;
		this.vehicleTypeId = vehicleTypeId;
		this.velocity = velocity;
		this.destination = destination;
		this.entityTypeId = entityTypeId;
		this.entityTypeData = entityTypeData;
	}
	
	//----------------------------------------------//
	// Getters
	//----------------------------------------------//
	public BungeeQueueType getQueueType() {
		return this.queueType;
	}
	
	public String getPlayerName() {
		return this.playerName;
	}
	
	public String getServer() {
		return this.server;
	}
	
	public Integer getVehicleTypeId() {
		return this.vehicleTypeId;
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
	
	public String getMessage() {
		return this.message;
	}
	
	//----------------------------------------------//
	// Converters
	//----------------------------------------------//
	public Location getDestination() {
		String destination = this.destination;
		if (destination == null) return null;
		return TeleportUtil.stringToLocation(destination);
	}
	
	public EntityType getEntityType() {
		Integer entityTypeId = this.entityTypeId;
		if (entityTypeId == null) return null;
		return EntityType.fromId(entityTypeId);
	}
	
	public EntityType getVehicleType() {
		Integer vehicleTypeId = this.vehicleTypeId;
		if (vehicleTypeId == null) return null;
		return EntityType.fromId(vehicleTypeId);
	}

}