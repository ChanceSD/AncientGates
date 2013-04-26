package org.mcteam.ancientgates.listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.mcteam.ancientgates.Gate;
import org.mcteam.ancientgates.Plugin;
import org.mcteam.ancientgates.util.GateUtil;
import org.mcteam.ancientgates.util.TeleportUtil;

public class PluginPlayerMoveListener implements Listener {
	
    public Plugin plugin;
    
    public PluginPlayerMoveListener(Plugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		if (event.isCancelled()) {
			return;
		}
		
		Block blockTo = event.getTo().getBlock();
		Block blockToUp = blockTo.getRelative(BlockFace.UP);
		
		if (blockTo.getType() != Material.PORTAL && blockToUp.getType() != Material.PORTAL) {
			return;
		}
		
		// Ok so a player walks into a portal block
		// Find the nearest gate!
		Location playerLocation = event.getPlayer().getLocation();
		Gate nearestGate = GateUtil.nearestGate(playerLocation, true, 2.0);
		
		if (nearestGate != null) {
			// Check player has permission to enter the gate.
			if (Plugin.hasPermManage(event.getPlayer(), "ancientgates.use."+nearestGate.getId()) || Plugin.hasPermManage(event.getPlayer(), "ancientgates.use.*")) {
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
   
}