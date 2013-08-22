package org.mcteam.ancientgates.util;

import org.bukkit.Bukkit;
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
	
}