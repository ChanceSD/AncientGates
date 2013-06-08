package org.mcteam.ancientgates.listeners;

import java.util.Calendar;
import java.util.HashMap;

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
import org.mcteam.ancientgates.types.WorldCoord;
import org.mcteam.ancientgates.util.BlockUtil;
import org.mcteam.ancientgates.util.TeleportUtil;

public class PluginMovementListener implements Listener {
	
    public Plugin plugin;
    
    protected HashMap<String, Long> lastMessageTime = new HashMap<String, Long>();
    
    public PluginMovementListener(Plugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		if (event.isCancelled()) {
			return;
		}
		
		Location from = event.getFrom();
		Location to = event.getTo();
		Block blockTo = to.getBlock();
		
		if (!BlockUtil.canPlayerStandInGateBlock(blockTo, from.getBlockY() == to.getBlockY())) return;
		
		// Ok so a player walks into a portal block
		// Find the nearest gate!
		WorldCoord playerCoord = new WorldCoord(event.getPlayer().getLocation());
		Gate nearestGate = Gates.gateFromAll(playerCoord);
		
		if (nearestGate != null) {
			
			// Get current time
	        Long now = Calendar.getInstance().getTimeInMillis();
			
			// Check player has permission to enter the gate.
			if ((!Plugin.hasPermManage(event.getPlayer(), "ancientgates.use."+nearestGate.getId())) && Conf.enforceAccess) {
				if (!this.lastMessageTime.containsKey(event.getPlayer().getName()) || this.lastMessageTime.get(event.getPlayer().getName()) < now - 10000L) {
					event.getPlayer().sendMessage("You lack the permissions to enter this gate.");
					this.lastMessageTime.put(event.getPlayer().getName(), now);
				}
				return;
			}
			
			// Handle economy (check player has funds to use gate)
			if (!Plugin.handleEconManage(event.getPlayer(), nearestGate.getCost())) {
				if (!this.lastMessageTime.containsKey(event.getPlayer().getName()) || this.lastMessageTime.get(event.getPlayer().getName()) < now - 10000L) {
					event.getPlayer().sendMessage("This gate costs: "+nearestGate.getCost()+". You have insufficient funds.");
					this.lastMessageTime.put(event.getPlayer().getName(), now);
				}
				return;
			}
			
			// Handle BungeeCord gates (BungeeCord support disabled)
			if (nearestGate.getBungeeTo() != null && (Conf.bungeeCordSupport == false)) {
				if (!this.lastMessageTime.containsKey(event.getPlayer().getName()) || this.lastMessageTime.get(event.getPlayer().getName()) < now - 10000L) {
					event.getPlayer().sendMessage(String.format("BungeeCord support not enabled."));
					this.lastMessageTime.put(event.getPlayer().getName(), now);
				}
				return;
			}
			
			// Handle gates that do not point anywhere
			if (nearestGate.getTo() == null && nearestGate.getBungeeTo() == null) {
				if (!this.lastMessageTime.containsKey(event.getPlayer().getName()) || this.lastMessageTime.get(event.getPlayer().getName()) < now - 10000L) {
					event.getPlayer().sendMessage(String.format("This gate does not point anywhere :P"));
					this.lastMessageTime.put(event.getPlayer().getName(), now);
				}
				return;
			}

			// Teleport the player (Instant method)
			if (nearestGate.getBungeeTo() == null)  {
				TeleportUtil.teleportPlayer(event.getPlayer(), nearestGate.getTo());
			} else {
				TeleportUtil.teleportPlayer(event.getPlayer(), nearestGate.getBungeeTo(), from.getBlockY() == to.getBlockY());
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
		Entity passenger = vehicle.getPassenger();
		
		// Ok so a vehicle drives into a portal block
		// Find the nearest gate!
    	WorldCoord toCoord = new WorldCoord(event.getTo());
		Gate nearestGate = Gates.gateFromAll(toCoord);

		if (nearestGate != null) {		
			if (passenger instanceof Player) {
				Player player = (Player)passenger;
				
				// Get current time
		        Long now = Calendar.getInstance().getTimeInMillis();
				
				// Check player has permission to enter the gate.
				if ((!Plugin.hasPermManage(player, "ancientgates.use."+nearestGate.getId())) && Conf.enforceAccess) {
					if (!this.lastMessageTime.containsKey(player.getName()) || this.lastMessageTime.get(player.getName()) < now - 10000L) {
						player.sendMessage("You lack the permissions to enter this gate.");
						this.lastMessageTime.put(player.getName(), now);
					}
					return;
				}
				
				// Handle economy (check player has funds to use gate)
				if (!Plugin.handleEconManage(player, nearestGate.getCost())) {
					if (!this.lastMessageTime.containsKey(player.getName()) || this.lastMessageTime.get(player.getName()) < now - 10000L) {
						player.sendMessage("This gate costs: "+nearestGate.getCost()+". You have insufficient funds.");
						this.lastMessageTime.put(player.getName(), now);
					}
					return;
				}
				
				// Handle BungeeCord gates (BungeeCord support disabled)
				if (nearestGate.getBungeeTo() != null && (Conf.bungeeCordSupport == false)) {
					if (!this.lastMessageTime.containsKey(player.getName()) || this.lastMessageTime.get(player.getName()) < now - 10000L) {
						player.sendMessage(String.format("BungeeCord support not enabled."));
						this.lastMessageTime.put(player.getName(), now);
					}
					return;
				}
				
				// Handle gates that do not point anywhere
				if (nearestGate.getTo() == null && nearestGate.getBungeeTo() == null) {
					if (!this.lastMessageTime.containsKey(player.getName()) || this.lastMessageTime.get(player.getName()) < now - 10000L) {
						player.sendMessage(String.format("This gate does not point anywhere :P"));
						this.lastMessageTime.put(player.getName(), now);
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
				if (nearestGate.getBungeeTo() == null)  {
					TeleportUtil.teleportVehicle(vehicle, nearestGate.getTo(), nearestGate.getTeleportEntities());
				} else {
					TeleportUtil.teleportVehicle(vehicle, nearestGate.getBungeeTo(), nearestGate.getTeleportEntities(), from.getBlockY() == to.getBlockY());
				}
			}
		}
	}
   
}