package org.mcteam.ancientgates.tasks;

import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.mcteam.ancientgates.Plugin;
import org.mcteam.ancientgates.util.types.PluginMessage;

import com.google.common.collect.Iterables;

public class BungeeMessage extends BukkitRunnable {

	private final Plugin plugin;

	public BungeeMessage(final Plugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public void run() {
		if (plugin.getServer().getOnlinePlayers().size() == 0)
			return;

		final List<PluginMessage> msgQueue = Plugin.bungeeMsgQueue;
		final Iterator<PluginMessage> it = msgQueue.iterator();

		while (it.hasNext()) {
			final PluginMessage msg = it.next();
			Iterables.getFirst(Bukkit.getOnlinePlayers(), null).sendPluginMessage(plugin, "BungeeCord", msg.toByteArray());

			// Remove from queue
			it.remove();
		}
	}

}
