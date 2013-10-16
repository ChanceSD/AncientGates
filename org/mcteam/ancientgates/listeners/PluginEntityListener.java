package org.mcteam.ancientgates.listeners;

import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityPortalEvent;
import org.mcteam.ancientgates.Conf;
import org.mcteam.ancientgates.Gate;
import org.mcteam.ancientgates.Plugin;
import org.mcteam.ancientgates.util.GateUtil;
import org.mcteam.ancientgates.util.TeleportUtil;
import org.mcteam.ancientgates.util.types.WorldCoord;

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
		
		if (!(event.getEntity() instanceof Player) && !(event.getEntity().getPassenger() instanceof Player)) {
			// Ok so an entity portal event begins
			// Find the nearest gate!
			WorldCoord entityCoord = new WorldCoord(event.getEntity().getLocation());
			Gate nearestGate = GateUtil.nearestGate(entityCoord, true);
		
			if (nearestGate != null) {
				event.setCancelled(true);
				
				/* Teleport non-living vehicles - Only when useVanillaPortals is enabled
				 * 
				 * Boolean Expression:
				 * Rtrn = VanilFlag' InstVeh InstLiv';
				 */
				if (!Conf.useVanillaPortals && (event.getEntity() instanceof Vehicle && !(event.getEntity() instanceof LivingEntity))) {
					return;
				}
				
				/* Teleport entities (incl. living vehicles) - If they're allowed
				 * Teleport non-living vehicles if they're allowed
				 * 
				 * Boolean Expression:
				 * Tele = EntFlag InstVeh' + EntFlag InstLiv + VehFlag InstVeh InstLiv';
				 */
				if (nearestGate.getTeleportEntities() && !(event.getEntity() instanceof Vehicle) ||
						(nearestGate.getTeleportEntities() && event.getEntity() instanceof LivingEntity) ||
						(nearestGate.getTeleportVehicles() && event.getEntity() instanceof Vehicle && !(event.getEntity() instanceof LivingEntity))) {
					if (nearestGate.getBungeeTo() == null) {
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
			WorldCoord entityCoord = new WorldCoord(event.getEntity().getLocation());
			Gate nearestGate = GateUtil.nearestGate(entityCoord, false);
		
			if (nearestGate != null) {
				event.setCancelled(true);
			}
		}
	}
    
    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
    	
		if (event.getCause() == DamageCause.DROWNING || event.getCause() == DamageCause.FIRE
				|| event.getCause() == DamageCause.FIRE_TICK || event.getCause() == DamageCause.LAVA) {
    	
			// Ok so an entity damage event begins
			// Find the nearest gate!
			WorldCoord entityCoord = new WorldCoord(event.getEntity().getLocation());
			WorldCoord entityCoordUp = new WorldCoord(event.getEntity().getLocation().getBlock().getRelative(BlockFace.UP));
			Gate nearestGate = GateUtil.nearestGate(entityCoord, false);
			Gate nearestGateUp = GateUtil.nearestGate(entityCoordUp, false);
		
			if (nearestGate != null || nearestGateUp != null) {
				event.setCancelled(true);
				event.getEntity().setFireTicks(0);
			}
		}
    }

}