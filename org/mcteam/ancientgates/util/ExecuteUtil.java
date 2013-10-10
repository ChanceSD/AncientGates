package org.mcteam.ancientgates.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class ExecuteUtil {
	
	// Execute command as player or console
	public static void execCommand(Player player, String command, String commandType) {
		// Insert any player substitution variables
		command = command.replace("%p", player.getName());
		
		// Execute command as player
		if (commandType.equals("PLAYER")) {
			player.performCommand(command);
			
		// Execute command as console
		} else if (commandType.equals("CONSOLE")) {
			Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
		}
	}
	
	// Execute command and teleport back from gate
	public static void execCommand(Player player, String command, String commandType, Boolean teleport) {
		// Spin player 180 deg
		if (teleport == true) {
			Location position = player.getLocation();
			float yaw = position.getYaw();
			if ((yaw += 180) > 360) {
				yaw -= 360;
			}
			position.setYaw(yaw);
			player.teleport(position);
		}
		
		// Execute command as player or console
		execCommand(player, command, commandType);
	}
	
}