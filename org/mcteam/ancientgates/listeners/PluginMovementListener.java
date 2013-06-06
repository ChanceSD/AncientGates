package org.mcteam.ancientgates.listeners;

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
import org.mcteam.ancientgates.Plugin;
import org.mcteam.ancientgates.types.WorldCoord;
import org.mcteam.ancientgates.util.BlockUtil;
import org.mcteam.ancientgates.util.GateUtil;
import org.mcteam.ancientgates.util.TeleportUtil;

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
		
		Location from = event.getFrom();
		Location to = event.getTo();
		Block blockTo = to.getBlock();
		
		if (!BlockUtil.canPlayerStandInGateBlock(blockTo, from.getBlockY() == to.getBlockY())) return;
		
		// Ok so a player walks into a portal block
		// Find the nearest gate!
		WorldCoord playerCoord = new WorldCoord(event.getPlayer().getLocation());
		Gate nearestGate = GateUtil.nearestGate(playerCoord, true);
		
		if (nearestGate != null) {
			// Check player has permission to enter the gate.
			if (Plugin.hasPermManage(event.getPlayer(), "ancientgates.use."+nearestGate.getId()) || (!Conf.enforceAccess)) {
				// Handle economy (check player has funds to use gate)
				if (!Plugin.handleEconManage(event.getPlayer(), nearestGate.getCost())) {
					return;
				}
				// Teleport the player (Instant method)
				if (nearestGate.getBungeeTo() == null)  {
					TeleportUtil.teleportPlayer(event.getPlayer(), nearestGate.getTo());
				} else {
					TeleportUtil.teleportPlayer(event.getPlayer(), nearestGate.getBungeeTo());
				}
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
		Gate nearestGate = GateUtil.nearestGate(toCoord, true);

		if (nearestGate != null) {		
			if (passenger instanceof Player) {
				Player player = (Player)passenger;
				// Check player has permission to enter the gate.
				if ((!Plugin.hasPermManage(player, "ancientgates.use."+nearestGate.getId())) && Conf.enforceAccess) {
					return;
				}
				// Handle economy (check player has funds to use gate)
				if (!Plugin.handleEconManage(player, nearestGate.getCost())) {
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
					TeleportUtil.teleportVehicle(vehicle, nearestGate.getBungeeTo(), nearestGate.getTeleportEntities());
				}
			}
		}
	}
   
}