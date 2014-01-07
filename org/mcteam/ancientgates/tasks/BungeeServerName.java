package org.mcteam.ancientgates.tasks;

import org.bukkit.scheduler.BukkitRunnable;
import org.mcteam.ancientgates.Plugin;
import org.mcteam.ancientgates.util.types.PluginMessage;

public class BungeeServerName extends BukkitRunnable {
 
	private final Plugin plugin;
 
	public BungeeServerName(Plugin plugin) {
		this.plugin = plugin;
	}
 
	@Override
	public void run() {
		if (Plugin.bungeeServerName != null) return;
		if (plugin.getServer().getOnlinePlayers().length == 0) return;
		
		// Send BungeeCord "GetServer" command
		final PluginMessage msg = new PluginMessage("GetServer");
		plugin.getServer().getOnlinePlayers()[0].sendPluginMessage(plugin, "BungeeCord", msg.toByteArray());
		
		// Re-schedule task to check bungeeServerName set
		if (Plugin.bungeeServerName == null) new BungeeServerName(plugin).runTaskLater(plugin, 20L);
	}
	
}