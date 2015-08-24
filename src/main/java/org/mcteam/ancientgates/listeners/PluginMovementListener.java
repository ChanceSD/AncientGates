package org.mcteam.ancientgates.listeners;

import java.util.Calendar;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;

import org.mcteam.ancientgates.Conf;
import org.mcteam.ancientgates.Gate;
import org.mcteam.ancientgates.Gates;
import org.mcteam.ancientgates.Plugin;
import org.mcteam.ancientgates.util.BlockUtil;
import org.mcteam.ancientgates.util.ExecuteUtil;
import org.mcteam.ancientgates.util.TeleportUtil;
import org.mcteam.ancientgates.util.types.WorldCoord;

public class PluginMovementListener implements Listener {
	
    public Plugin plugin;
    
    public PluginMovementListener(Plugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		if (event.isCancelled()) {
			return;
		}
		
		final Player player = event.getPlayer();
		
		// Check player is not carrying a passenger
		if (player.getPassenger() != null) {
			return;
		}
		
		Location from = event.getFrom();
		Location to = event.getTo();
		Block blockTo = to.getBlock();
		
		if (!BlockUtil.canPlayerStandInGateBlock(blockTo, from.getBlockY() == to.getBlockY())) return;
		
		// Ok so a player walks into a portal block
		// Find the nearest gate!
		WorldCoord playerCoord = new WorldCoord(player.getLocation());
		final Gate nearestGate = Gates.gateFromAll(playerCoord);
		
		if (nearestGate != null) {
			
			// Get current time
	        Long now = Calendar.getInstance().getTimeInMillis();
	        
			// Check player has passed cooldown period
			if (Plugin.lastTeleportTime.containsKey(player.getName()) && Plugin.lastTeleportTime.get(player.getName()) > now - Conf.getGateCooldownMillis()) {
				return;
			}

			// Check player has permission to enter the gate.
			if ((!Plugin.hasPermManage(player, "ancientgates.use."+nearestGate.getId())
					&& !Plugin.hasPermManage(player, "ancientgates.use.*")) && Conf.enforceAccess) {
				if (!Plugin.lastMessageTime.containsKey(player.getName()) || Plugin.lastMessageTime.get(player.getName()) < now - 10000L) {
					player.sendMessage("You lack the permissions to enter this gate.");
					Plugin.lastMessageTime.put(player.getName(), now);
				}
				return;
			}
			
			// Handle economy (check player has funds to use gate)
			if (!Plugin.handleEconManage(player, nearestGate.getCost())) {
				if (!Plugin.lastMessageTime.containsKey(player.getName()) || Plugin.lastMessageTime.get(player.getName()) < now - 10000L) {
					player.sendMessage("This gate costs: "+nearestGate.getCost()+". You have insufficient funds.");
					Plugin.lastMessageTime.put(player.getName(), now);
				}
				return;
			}
			
			// Handle BungeeCord gates (BungeeCord support disabled)
			if (nearestGate.getBungeeTo() != null && (Conf.bungeeCordSupport == false)) {
				if (!Plugin.lastMessageTime.containsKey(player.getName()) || Plugin.lastMessageTime.get(player.getName()) < now - 10000L) {
					player.sendMessage(String.format("BungeeCord support not enabled."));
					Plugin.lastMessageTime.put(player.getName(), now);
				}
				return;
			}
			
			// Handle gates that do not point anywhere
			if (nearestGate.getTo() == null && nearestGate.getBungeeTo() == null && nearestGate.getCommand() == null) {
				if (!Plugin.lastMessageTime.containsKey(player.getName()) || Plugin.lastMessageTime.get(player.getName()) < now - 10000L) {
					player.sendMessage(String.format("This gate does not point anywhere :P"));
					Plugin.lastMessageTime.put(player.getName(), now);
				}
				return;
			}

			// Teleport the player (Instant method)
			if (nearestGate.getTo() != null) {
				TeleportUtil.teleportPlayer(player, nearestGate.getTo(), nearestGate.getTeleportEntities(), nearestGate.getTeleportInventory());
				
				if (nearestGate.getCommand() != null) ExecuteUtil.execCommand(player, nearestGate.getCommand(), nearestGate.getCommandType());
				if (nearestGate.getMessage() != null) player.sendMessage(nearestGate.getMessage());
				
				Plugin.lastTeleportTime.put(player.getName(), now);
			} else if (nearestGate.getBungeeTo() != null) {
				TeleportUtil.teleportPlayer(player, nearestGate.getBungeeTo(), nearestGate.getBungeeType(), nearestGate.getTeleportEntities(), nearestGate.getTeleportInventory(), from.getBlockY() == to.getBlockY(), nearestGate.getCommand(), nearestGate.getCommandType(), nearestGate.getMessage());
			} else {
				ExecuteUtil.execCommand(player, nearestGate.getCommand(), nearestGate.getCommandType(), true);
				Plugin.lastTeleportTime.put(player.getName(), now);
			}
		}
	}

    @EventHandler
	public void onVehicleMove(VehicleMoveEvent event) {
		Location from = event.getFrom();
		Location to = event.getTo();
		Block blockTo = to.getBlock();
		
		if (!BlockUtil.canPlayerStandInGateBlock(blockTo, from.getBlockY() == to.getBlockY())) return;
		
		Vehicle vehicle = event.getVehicle();
		final Entity passenger = vehicle.getPassenger();
		
		// Ok so a vehicle drives into a portal block
		// Find the nearest gate!
    	WorldCoord toCoord = new WorldCoord(event.getTo());
		final Gate nearestGate = Gates.gateFromAll(toCoord);

		if (nearestGate != null) {		
			Long now = 0L;
			
			if (passenger instanceof Player) {
				Player player = (Player)passenger;
				
				// Get current time
		        now = Calendar.getInstance().getTimeInMillis();
		        
				// Check player has passed cooldown period
				if (Plugin.lastTeleportTime.containsKey(player.getName()) && Plugin.lastTeleportTime.get(player.getName()) > now - Conf.getGateCooldownMillis()) {
					return;
				}
				
				// Check player has permission to enter the gate.
				if ((!Plugin.hasPermManage(player, "ancientgates.use."+nearestGate.getId())
						&& !Plugin.hasPermManage(player, "ancientgates.use.*")) && Conf.enforceAccess) {
					if (!Plugin.lastMessageTime.containsKey(player.getName()) || Plugin.lastMessageTime.get(player.getName()) < now - 10000L) {
						player.sendMessage("You lack the permissions to enter this gate.");
						Plugin.lastMessageTime.put(player.getName(), now);
					}
					return;
				}
				
				// Handle economy (check player has funds to use gate)
				if (!Plugin.handleEconManage(player, nearestGate.getCost())) {
					if (!Plugin.lastMessageTime.containsKey(player.getName()) || Plugin.lastMessageTime.get(player.getName()) < now - 10000L) {
						player.sendMessage("This gate costs: "+nearestGate.getCost()+". You have insufficient funds.");
						Plugin.lastMessageTime.put(player.getName(), now);
					}
					return;
				}
				
				// Handle BungeeCord gates (BungeeCord support disabled)
				if (nearestGate.getBungeeTo() != null && (Conf.bungeeCordSupport == false)) {
					if (!Plugin.lastMessageTime.containsKey(player.getName()) || Plugin.lastMessageTime.get(player.getName()) < now - 10000L) {
						player.sendMessage(String.format("BungeeCord support not enabled."));
						Plugin.lastMessageTime.put(player.getName(), now);
					}
					return;
				}
				
				// Handle gates that do not point anywhere
				if (nearestGate.getTo() == null && nearestGate.getBungeeTo() == null && nearestGate.getCommand() == null) {
					if (!Plugin.lastMessageTime.containsKey(player.getName()) || Plugin.lastMessageTime.get(player.getName()) < now - 10000L) {
						player.sendMessage(String.format("This gate does not point anywhere :P"));
						Plugin.lastMessageTime.put(player.getName(), now);
					}
					return;
				}
				
			} else if (passenger instanceof Entity) {
				if (!nearestGate.getTeleportEntities()) {
					return;
				}
			}
			// Teleport the vehicle (with its passenger)
			if (nearestGate.getTeleportVehicles()) {
				if (nearestGate.getTo() != null) {
					TeleportUtil.teleportVehicle(vehicle, nearestGate.getTo(), nearestGate.getTeleportEntities(), nearestGate.getTeleportInventory());
					
					if (passenger instanceof Player && nearestGate.getCommand() != null) ExecuteUtil.execCommand((Player)passenger, nearestGate.getCommand(), nearestGate.getCommandType());
					if (passenger instanceof Player && nearestGate.getMessage() != null) ((Player)passenger).sendMessage(nearestGate.getMessage());
					
					if (passenger instanceof Player) Plugin.lastMessageTime.put(((Player)passenger).getName(), now);
				} else if (nearestGate.getBungeeTo() != null) {
					TeleportUtil.teleportVehicle(vehicle, nearestGate.getBungeeTo(), nearestGate.getBungeeType(), nearestGate.getTeleportEntities(), nearestGate.getTeleportInventory(), from.getBlockY() == to.getBlockY(), nearestGate.getCommand(), nearestGate.getCommandType(), nearestGate.getMessage());
				} else if (passenger instanceof Player) {
					ExecuteUtil.execCommand(vehicle, nearestGate.getCommand(), nearestGate.getCommandType(), true);
					if (passenger instanceof Player) Plugin.lastMessageTime.put(((Player)passenger).getName(), now);
				}
			}
		}
	}
   
}