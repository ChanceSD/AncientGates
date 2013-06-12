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

		String playerName = event.getPlayer().getName();

		// Ok so a player joins the server
		// If it's a BungeeCord teleport, display a custom join message
		String server = Plugin.bungeeCordBlockJoinQueue.remove(playerName.toLowerCase());
		if (server != null) {
			event.setJoinMessage(playerName + " came from " + server + " server");
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
		
		String playerName = event.getPlayer().getName();

		// Ok so a player quits the server
		// If it's a BungeeCord teleport, display a custom quit message
		String server = Plugin.bungeeCordBlockQuitQueue.remove(playerName.toLowerCase());
		if (server != null) {
			event.setQuitMessage(playerName + " went to " + server + " server");
		}
		
	}
    
	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerPortal(PlayerPortalEvent event) {
		if (event.isCancelled()) {
			return;
		}
		
		Player player = event.getPlayer();
		
		// Ok so a player portal event begins
		// Find the nearest gate!
		WorldCoord playerCoord = new WorldCoord(this.playerLocationAtEvent.get(player));
		Gate nearestGate = Gates.gateFromPortal(playerCoord);
		
		if (nearestGate != null) {
			event.setCancelled(true);
			
			// Check teleportation method
			if (!Conf.useVanillaPortals) {
				return;
			}

			// Check player has permission to enter the gate.
			if ((!Plugin.hasPermManage(player, "ancientgates.use."+nearestGate.getId())
					&& !Plugin.hasPermManage(player, "ancientgates.use.*")) && Conf.enforceAccess) {
				player.sendMessage("You lack the permissions to enter this gate.");
				return;
			}
			
			// Handle economy (check player has funds to use gate)
			if (!Plugin.handleEconManage(player, nearestGate.getCost())) {
				player.sendMessage("This gate costs: "+nearestGate.getCost()+". You have insufficient funds.");
				return;
			}
			
			// Handle BungeeCord gates (BungeeCord support disabled)
			if (nearestGate.getBungeeTo() != null && (Conf.bungeeCordSupport == false)) {
				player.sendMessage(String.format("BungeeCord support not enabled."));
				return;
			}
			
			// Teleport the player (Nether method)
			if (nearestGate.getTo() == null && nearestGate.getBungeeTo() == null) {
				player.sendMessage(String.format("This gate does not point anywhere :P"));
			} else if (nearestGate.getBungeeTo() == null)  {
				TeleportUtil.teleportPlayer(player, nearestGate.getTo());
				
				if (nearestGate.getMessage() != null) player.sendMessage(nearestGate.getMessage());
			} else {
				TeleportUtil.teleportPlayer(player, nearestGate.getBungeeTo(), event.getFrom().getBlockY() == event.getTo().getBlockY());
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
