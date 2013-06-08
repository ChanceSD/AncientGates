package org.mcteam.ancientgates.listeners;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEnterEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.mcteam.ancientgates.Conf;
import org.mcteam.ancientgates.Gate;
import org.mcteam.ancientgates.Gates;
import org.mcteam.ancientgates.Plugin;
import org.mcteam.ancientgates.types.WorldCoord;
import org.mcteam.ancientgates.util.TeleportUtil;

public class PluginPlayerListener implements Listener {
	
    public Plugin plugin;
    
    private HashMap<Player, Location> playerLocationAtEvent = new HashMap<Player, Location>();
    
    public PluginPlayerListener(Plugin plugin) {
        this.plugin = plugin;
    }
    
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		if (!Conf.bungeeCordSupport) {
			return;
		}

		// Ok so a player joins the server
		// If it's a BungeeCord teleport, block the join message
		if (Plugin.bungeeCordBlockJoinQueue.remove(event.getPlayer().getName().toLowerCase())) {
			event.setJoinMessage(null);
		}
		
		// Find if they're in the BungeeCord in-bound player teleport queue
		String destination = Plugin.bungeeCordPlayerInQueue.remove(event.getPlayer().getName().toLowerCase());
		if (destination != null) {
			// Teleport incoming BungeeCord player
			Location location = TeleportUtil.stringToLocation(destination);
			TeleportUtil.teleportPlayer(event.getPlayer(), location);
			return;
		}
		
		// Find if they're in the BungeeCord in-bound passenger teleport queue
		String msg = Plugin.bungeeCordPassengerInQueue.remove(event.getPlayer().getName().toLowerCase());
		if (msg != null) {
			// Extract message parts
			String[] parts = msg.split("#@#");
			int vehicleTypeId = Integer.parseInt(parts[0]);
			double velocity = Double.parseDouble(parts[1]);
			String dest = parts[2];
			
			// Teleport incoming BungeeCord passenger
			Location location = TeleportUtil.stringToLocation(dest);
			TeleportUtil.teleportVehicle(event.getPlayer(), vehicleTypeId, velocity, location);
			return;
		}

	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		if (!Conf.bungeeCordSupport) {
			return;
		}

		// Ok so a player quits the server
		// If it's a BungeeCord teleport, block the quit message
		if (!Plugin.bungeeCordBlockQuitQueue.remove(event.getPlayer().getName().toLowerCase())) {
			return;
		}
		
		// Block BungeeCord player quit message
		event.setQuitMessage(null);
	}
    
	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerPortal(PlayerPortalEvent event) {
		if (event.isCancelled()) {
			return;
		}
		
		// Ok so a player portal event begins
		// Find the nearest gate!
		WorldCoord playerCoord = new WorldCoord(this.playerLocationAtEvent.get(event.getPlayer()));
		Gate nearestGate = Gates.gateFromPortal(playerCoord);
		
		if (nearestGate != null) {
			event.setCancelled(true);
			
			// Check teleportation method
			if (!Conf.useVanillaPortals) {
				return;
			}
			
			// Check player has permission to enter the gate.
			if ((!Plugin.hasPermManage(event.getPlayer(), "ancientgates.use."+nearestGate.getId())) && Conf.enforceAccess) {
				event.getPlayer().sendMessage("You lack the permissions to enter this gate.");
				return;
			}
			
			// Handle economy (check player has funds to use gate)
			if (!Plugin.handleEconManage(event.getPlayer(), nearestGate.getCost())) {
				event.getPlayer().sendMessage("This gate costs: "+nearestGate.getCost()+". You have insufficient funds.");
				return;
			}
			
			// Handle BungeeCord gates (BungeeCord support disabled)
			if (nearestGate.getBungeeTo() != null && (Conf.bungeeCordSupport == false)) {
				event.getPlayer().sendMessage(String.format("BungeeCord support not enabled."));
				return;
			}
			
			// Teleport the player (Nether method)
			if (nearestGate.getTo() == null && nearestGate.getBungeeTo() == null) {
				event.getPlayer().sendMessage(String.format("This gate does not point anywhere :P"));
			} else if (nearestGate.getBungeeTo() == null)  {
				TeleportUtil.teleportPlayer(event.getPlayer(), nearestGate.getTo());
			} else {
				TeleportUtil.teleportPlayer(event.getPlayer(), nearestGate.getBungeeTo(), event.getFrom().getBlockY() == event.getTo().getBlockY());
			}
		}
	}
    
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityPortalEnterEvent(EntityPortalEnterEvent event) {
    	if (event.getEntity() instanceof Player) {
    		Player player = (Player)event.getEntity();
    		
    		// Ok so a player enters a portal
    		// Immediately record their location
    	    Location playerLocation = event.getLocation();
    	    this.playerLocationAtEvent.put(player, playerLocation);
    	}
    }
    
}
