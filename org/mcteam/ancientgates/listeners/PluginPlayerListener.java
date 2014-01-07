package org.mcteam.ancientgates.listeners;

import java.util.Calendar;
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
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
import org.mcteam.ancientgates.queue.BungeeQueue;
import org.mcteam.ancientgates.queue.types.BungeeQueueType;
import org.mcteam.ancientgates.tasks.BungeeMessage;
import org.mcteam.ancientgates.tasks.BungeeServerList;
import org.mcteam.ancientgates.tasks.BungeeServerName;
import org.mcteam.ancientgates.util.EntityUtil;
import org.mcteam.ancientgates.util.ExecuteUtil;
import org.mcteam.ancientgates.util.TeleportUtil;
import org.mcteam.ancientgates.util.types.CommandType;
import org.mcteam.ancientgates.util.types.InvBoolean;
import org.mcteam.ancientgates.util.types.WorldCoord;

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

		final Player player = event.getPlayer();
		String playerName = player.getName();

		// Ok so a player joins the server
		// Find if they're in the BungeeCord in-bound teleport queue
		BungeeQueue queue = Plugin.bungeeCordInQueue.remove(playerName.toLowerCase());
		if (queue != null) {
			// Display custom join message
			String msg = null;
			if (Conf.useBungeeMessages) {
				String server = queue.getServer();
				msg = ChatColor.translateAlternateColorCodes('&', Conf.bungeeJoinMessage.replace("%p", playerName).replace("%s", server));
			}
			event.setJoinMessage(msg);
			
			// Display teleport message
			String message = queue.getMessage();
			if (!message.equals("null")) player.sendMessage(message);
			
			if (queue.getDestination() != null) {
				// Teleport incoming BungeeCord player
				BungeeQueueType queueType = queue.getQueueType();
				if (queueType == BungeeQueueType.PLAYER) {
					Location location = queue.getDestination();
					
					// Handle player riding entity
					Entity entity = null;
					if (queue.getEntityType() != null) {
						World world = location.getWorld();
						if (queue.getEntityType().isSpawnable()) {
							// Spawn incoming BungeeCord player's entity
							entity = world.spawnEntity(location, queue.getEntityType());
							EntityUtil.setEntityTypeData(entity, queue.getEntityTypeData());
						}
					}
						
					TeleportUtil.teleportPlayer(player, location, false, InvBoolean.TRUE);
					if (entity != null) entity.setPassenger(player);
					
					return;
					// Teleport incoming BungeeCord passenger
				} else if (queueType == BungeeQueueType.PASSENGER) {
					TeleportUtil.teleportVehicle(player, queue.getVehicleTypeName(), queue.getVelocity(), queue.getDestination());
					return;
				}
			}
			
			// Execute teleport command
			String command = queue.getCommand();
			CommandType commandType = queue.getCommandType();
			if (!command.equals("null")) ExecuteUtil.execCommand(player, command, commandType);
			
			// Activate cooldown period
	        Long now = Calendar.getInstance().getTimeInMillis();
	        Plugin.lastTeleportTime.put(player.getName(), now);
		}
		
		// Process BungeeCord message queue
		if (Plugin.bungeeMsgQueue.size() > 0) new BungeeMessage(plugin).runTaskLater(plugin, 20L);
		
		// Schedule task to check bungeeServerName & bungeeServerList is set
		if (Plugin.bungeeServerName == null) new BungeeServerName(plugin).runTaskLater(plugin, 20L);
		if (Plugin.bungeeServerList == null) new BungeeServerList(plugin).runTaskLater(plugin, 20L);

	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		String playerName = event.getPlayer().getName();

		// Clear player hashmaps
		Plugin.lastMessageTime.remove(playerName);
		Plugin.lastTeleportTime.remove(playerName);
		
		if (!Conf.bungeeCordSupport) {
			return;
		}

		// Ok so a player quits the server
		// If it's a BungeeCord teleport, display a custom quit message
		String server = Plugin.bungeeCordOutQueue.remove(playerName.toLowerCase());
		if (server != null) {
			String msg = null;
			if (Conf.useBungeeMessages) msg = ChatColor.translateAlternateColorCodes('&', Conf.bungeeQuitMessage.replace("%p", playerName).replace("%s", server));
			event.setQuitMessage(msg);
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
			
			// Check player is not carrying a passenger
			if (player.getPassenger() != null) {
				return;
			}
			
			// Get current time
	        Long now = Calendar.getInstance().getTimeInMillis();
			
			// Check player has passed cooldown period
			if (Plugin.lastTeleportTime.containsKey(player.getName()) && Plugin.lastTeleportTime.get(player.getName()) > now - Conf.getGateCooldownMillis()) {
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
			if (nearestGate.getTo() == null && nearestGate.getBungeeTo() == null && nearestGate.getCommand() == null) {
				player.sendMessage(String.format("This gate does not point anywhere :P"));
			} else if (nearestGate.getTo() != null)  {
				TeleportUtil.teleportPlayer(player, nearestGate.getTo(), nearestGate.getTeleportEntities(), nearestGate.getTeleportInventory());
				
				if (nearestGate.getCommand() != null) ExecuteUtil.execCommand(player, nearestGate.getCommand(), nearestGate.getCommandType());
				if (nearestGate.getMessage() != null) player.sendMessage(nearestGate.getMessage());
				
				Plugin.lastTeleportTime.put(player.getName(), now);
			} else if (nearestGate.getBungeeTo() != null) {
				TeleportUtil.teleportPlayer(player, nearestGate.getBungeeTo(), nearestGate.getBungeeType(), nearestGate.getTeleportEntities(), nearestGate.getTeleportInventory(), event.getFrom().getBlockY() == event.getTo().getBlockY(), nearestGate.getCommand(), nearestGate.getCommandType(), nearestGate.getMessage());
			} else {
				ExecuteUtil.execCommand(player, nearestGate.getCommand(), nearestGate.getCommandType(), true);
				Plugin.lastTeleportTime.put(player.getName(), now);
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
