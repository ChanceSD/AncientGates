package org.mcteam.ancientgates.listeners;

import org.bukkit.Location;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityPortalEvent;
import org.mcteam.ancientgates.Gate;
import org.mcteam.ancientgates.Plugin;
import org.mcteam.ancientgates.util.GateUtil;
import org.mcteam.ancientgates.util.TeleportUtil;

public class PluginEntityListener implements Listener {
	
    public Plugin plugin;
    
    public PluginEntityListener(Plugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
	public void onEntityPortal(EntityPortalEvent event) {
		if (event.isCancelled()) {
			return;
		}
		
		if (!(event.getEntity() instanceof Player)) {
			// Ok so an entity portal event begins
			// Find the nearest gate!
			Location entityLocation = event.getEntity().getLocation();
			Gate nearestGate = GateUtil.nearestGate(entityLocation, true, 2.0);
		
			if (nearestGate != null) {
				event.setCancelled(true);
				
				if (nearestGate.getTeleportEntities()) {
					if (nearestGate.getBungeeTo() == null)  {
						TeleportUtil.teleportEntity(event, nearestGate.getTo());
					} else {
						TeleportUtil.teleportEntity(event, nearestGate.getBungeeTo());
					}
				}
			}
		}
	}
    
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onCreatureSpawn(CreatureSpawnEvent event) {

		if (event.getEntity() instanceof PigZombie) {
			// Ok so an entity portal event begins
			// Find the nearest gate!
			Location entityLocation = event.getEntity().getLocation();
			Gate nearestGate = GateUtil.nearestGate(entityLocation, false, 2.0);
		
			if (nearestGate != null) {
				event.setCancelled(true);
			}
		}
	}

}