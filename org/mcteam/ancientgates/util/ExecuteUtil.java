package org.mcteam.ancientgates.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;

import org.mcteam.ancientgates.Plugin;
import org.mcteam.ancientgates.util.types.CommandType;

public class ExecuteUtil {
	
	// Execute command as player or console
	public static void execCommand(Player player, String command, CommandType commandType) {
		// Insert any player substitution variables
		command = command.replace("%p", player.getName());
		
		// Execute command as player
		if (commandType.equals(CommandType.PLAYER)) {
			player.performCommand(command);
			
		// Execute command as console
		} else if (commandType.equals(CommandType.CONSOLE)) {
			Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
		}
	}
	
	// Teleport player back from gate and execute command
	public static void execCommand(Player player, String command, CommandType commandType, Boolean teleport) {
		// Spin player 180 deg
		if (teleport) {
			Location position = player.getLocation();
			float yaw = position.getYaw();
			if ((yaw += 180) > 360) {
				yaw -= 360;
			}
			position.setYaw(yaw);
			position.add(position.getDirection().multiply(2));
			
			// Handle player riding an entity
			final Entity e = player.getVehicle();
			if (player.isInsideVehicle() && e instanceof LivingEntity) {
				e.eject();
				e.teleport(position);
				e.setFireTicks(0); // Cancel lava fire
			}
				
			player.teleport(position);
			player.setFireTicks(0); // Cancel lava fire
			if (e != null) e.setPassenger(player);
		}
		
		// Execute command as player or console
		execCommand(player, command, commandType);
	}
	
	// Teleport vehicle back from gate and execute command
	public static void execCommand(Vehicle vehicle, final String command, final CommandType commandType, Boolean teleport) {
		final Entity passenger = vehicle.getPassenger();
		
		// Spin player 180 deg
		if (teleport) {
			Location position = vehicle.getLocation();
			float yaw = position.getYaw();
			if ((yaw += 180) > 360) {
				yaw -= 360;
			}
			position.setYaw(yaw);
			position.add(position.getDirection().multiply(2));
			
			final Vehicle v = position.getWorld().spawn(position, vehicle.getClass());
			vehicle.eject();
			vehicle.remove();
			passenger.teleport(position);
			passenger.setFireTicks(0); // Cancel lava fire
			Plugin.instance.getServer().getScheduler().scheduleSyncDelayedTask(Plugin.instance, new Runnable() {
				public void run() {
					v.setPassenger(passenger);
				}
			}, 2);
		}
		
		// Execute command as player or console
		Plugin.instance.getServer().getScheduler().scheduleSyncDelayedTask(Plugin.instance, new Runnable() {
			public void run() {
				execCommand((Player)passenger, command, commandType);
			}
		}, 3);
	}
	
}