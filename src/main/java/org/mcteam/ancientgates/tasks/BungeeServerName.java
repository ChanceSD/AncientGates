package org.mcteam.ancientgates.tasks;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.mcteam.ancientgates.Plugin;
import org.mcteam.ancientgates.util.types.PluginMessage;

import com.google.common.collect.Iterables;

public class BungeeServerName extends BukkitRunnable {

	private final Plugin plugin;

	public BungeeServerName(final Plugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public void run() {
		if (Plugin.bungeeServerName != null)
			return;
		if (plugin.getServer().getOnlinePlayers().size() == 0)
			return;

		// Send BungeeCord "GetServer" command
		final PluginMessage msg = new PluginMessage("GetServer");
		Iterables.getFirst(Bukkit.getOnlinePlayers(), null).sendPluginMessage(plugin, "BungeeCord", msg.toByteArray());

		// Re-schedule task to check bungeeServerName set
		if (Plugin.bungeeServerName == null)
			new BungeeServerName(plugin).runTaskLater(plugin, 20L);
	}

}
