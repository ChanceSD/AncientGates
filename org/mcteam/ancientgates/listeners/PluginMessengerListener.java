package org.mcteam.ancientgates.listeners;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Calendar;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.entity.minecart.HopperMinecart;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.util.Vector;

import org.mcteam.ancientgates.Conf;
import org.mcteam.ancientgates.Gate;
import org.mcteam.ancientgates.Plugin;
import org.mcteam.ancientgates.commands.base.CommandRemTo;
import org.mcteam.ancientgates.commands.base.CommandSetTo;
import org.mcteam.ancientgates.queue.BungeeQueue;
import org.mcteam.ancientgates.util.EntityUtil;
import org.mcteam.ancientgates.util.ExecuteUtil;
import org.mcteam.ancientgates.util.GateUtil;
import org.mcteam.ancientgates.util.ItemStackUtil;
import org.mcteam.ancientgates.util.TeleportUtil;
import org.mcteam.ancientgates.util.TextUtil;
import org.mcteam.ancientgates.util.types.CommandType;
import org.mcteam.ancientgates.util.types.InvBoolean;
import org.mcteam.ancientgates.util.types.PluginMessage;
import org.mcteam.ancientgates.util.types.WorldCoord;

public class PluginMessengerListener implements PluginMessageListener {

	@Override
	public void onPluginMessageReceived(String channel, Player unused, byte[] message) {
		if (!Conf.bungeeCordSupport || !channel.equals("BungeeCord")) {
			return;
		}
		
		// Get data from message
		String inChannel;
		byte[] data;
		try {
			DataInputStream in = new DataInputStream(new ByteArrayInputStream(message));
			inChannel = in.readUTF();
			short len = in.readShort();
			data = new byte[len];
			in.readFully(data);
		} catch (IOException e) {
			Plugin.log.severe("Error receiving BungeeCord message");
			e.printStackTrace();
			return;
		}
		
		// Parse BungeeCord teleport packet
		if (inChannel.equals("AGBungeeTele")) {
			// Data should be player name, and destination location
			String msg = new String(data);
			String[] parts = msg.split("#@#");
			
			String playerName = parts[0];
			String destination = parts[1];
			String fromServer = parts[2];
			String tpCmd = parts[3];
			CommandType tpCmdType = CommandType.fromName(parts[4]);
			String tpMsg = parts[5];
			
			String entityTypeName = null;
			String entityTypeData = null;

			// Handle player riding entity
			if (parts.length > 6) {
				entityTypeName = parts[6];
				entityTypeData = parts[7];
			}
			
			// Check if the player is online, if so, teleport, otherwise, queue
			Player player = Bukkit.getPlayer(playerName);
			if (player == null) {
				Plugin.bungeeCordInQueue.put(playerName.toLowerCase(), new BungeeQueue(playerName, entityTypeName, entityTypeData, fromServer, destination, tpCmd, tpCmdType, tpMsg));
			} else {
				// Get current time
		        Long now = Calendar.getInstance().getTimeInMillis();
				
				// Teleport incoming BungeeCord player
				if (!destination.equals("null")) {
					Location location = TeleportUtil.stringToLocation(destination);
					
					// Handle player riding entity
					Entity entity = null;
					if (entityTypeName != null) {
						World world = TeleportUtil.stringToWorld(destination);
						if (EntityUtil.entityType(entityTypeName).isSpawnable()) {
							// Spawn incoming BungeeCord player's entity
							entity = world.spawnEntity(location, EntityUtil.entityType(entityTypeName));
							EntityUtil.setEntityTypeData(entity, entityTypeData);
						}
					}
			
					TeleportUtil.teleportPlayer(player, location, false, InvBoolean.TRUE);
					if (entity != null) entity.setPassenger(player);
				}
				
				if (!tpCmd.equals("null")) ExecuteUtil.execCommand(player, tpCmd, tpCmdType);
				if (!tpMsg.equals("null")) player.sendMessage(tpMsg);
				
				Plugin.lastTeleportTime.put(player.getName(), now);
			}
		// Parse BungeeCord vehicle teleport packet
		} else if (inChannel.equals("AGBungeeVehicleTele")) {
			// Data should be player name, vehicle typeId, velocity and destination location
			String msg = new String(data);
			String[] parts = msg.split("#@#");
			
			String playerName = parts[0];
			String vehicleTypeName = parts[1];
			double velocity = Double.parseDouble(parts[2]);
			String destination = parts[3];
			String fromServer = parts[4];
			String tpCmd = parts[5];
			CommandType tpCmdType = CommandType.fromName(parts[6]);
			String tpMsg = parts[7];
			
			// Check if the player is online, if so, teleport, otherwise, queue
			Player player = Bukkit.getPlayer(playerName);
			if (player == null) {
				Plugin.bungeeCordInQueue.put(playerName.toLowerCase(), new BungeeQueue(playerName, fromServer, vehicleTypeName, velocity, destination, tpCmd, tpCmdType, tpMsg));
			} else {
				// Teleport incoming BungeeCord player
				if (!destination.equals("null")) {
					Location location = TeleportUtil.stringToLocation(destination);
					TeleportUtil.teleportVehicle(player, vehicleTypeName, velocity, location);
				}
				
				if (!tpCmd.equals("null")) ExecuteUtil.execCommand(player, tpCmd, tpCmdType);
				if (!tpMsg.equals("null")) player.sendMessage(tpMsg);
			}
		// Parse BungeeCord spawn packet
		} else if (inChannel.equals("AGBungeeSpawn")) {
			// Data should be entitytype id, entitytype data and destination location
			String msg = new String(data);
			String[] parts = msg.split("#@#");
				
			String entityTypeName = parts[0];
			String entityTypeData = parts[1];
			String destination = parts[2];
				
			// Spawn incoming BungeeCord entity
			Location location = TeleportUtil.stringToLocation(destination);
			World world = TeleportUtil.stringToWorld(destination);

			if (EntityUtil.entityType(entityTypeName).isSpawnable()) {
				Entity entity = world.spawnEntity(location, EntityUtil.entityType(entityTypeName)); // Entity
				EntityUtil.setEntityTypeData(entity, entityTypeData);
				entity.teleport(location);
			} else if(EntityUtil.entityType(entityTypeName) == EntityType.DROPPED_ITEM) {
				Item item = world.dropItemNaturally(location, ItemStackUtil.stringToItemStack(entityTypeData)[0]); // Dropped ItemStack
				item.teleport(location);
			}
		// Parse BungeeCord vehicle spawn packet
		} else if (inChannel.equals("AGBungeeVehicleSpawn")) {
			// Data should be vehicletype id, velocity, destination location, entitytype id and entitytype data
			String msg = new String(data);
			String[] parts = msg.split("#@#");
				
			String vehicleTypeName = parts[0];
			double velocity = Double.parseDouble(parts[1]);
			String destination = parts[2];
			
			Location location = TeleportUtil.stringToLocation(destination);
			World world = TeleportUtil.stringToWorld(destination);
			
			Entity passenger = null;
			String entityItemStack = null;

			// Parse passenger info
			if (parts.length > 4) {
				String entityTypeName = parts[3];
				String entityTypeData = parts[4];

				if (EntityUtil.entityType(entityTypeName).isSpawnable()) {
					// Spawn incoming BungeeCord entity
					passenger = world.spawnEntity(location, EntityUtil.entityType(entityTypeName));
					EntityUtil.setEntityTypeData(passenger, entityTypeData);
					passenger.teleport(location);
				}
			// Parse vehicle contents
			} else if (parts.length > 3) {
				entityItemStack = parts[3];
			}
			final Entity p = passenger;
				
			// Create new velocity
			final Vector newVelocity = location.getDirection();
			newVelocity.multiply(velocity);

			// Spawn incoming BungeeCord vehicle
			if (passenger != null) {
				final Vehicle v = (Vehicle)location.getWorld().spawnEntity(location, EntityUtil.entityType(vehicleTypeName));
				Plugin.instance.getServer().getScheduler().scheduleSyncDelayedTask(Plugin.instance, new Runnable() {
					public void run() {
						if (p != null) v.setPassenger(p);
						v.setVelocity(newVelocity);
					}
				}, 2);
			} else {
				Vehicle mc = (Vehicle)location.getWorld().spawnEntity(location, EntityUtil.entityType(vehicleTypeName));
				if (mc instanceof StorageMinecart && entityItemStack != null) {
					StorageMinecart smc = (StorageMinecart)mc;
					smc.getInventory().setContents(ItemStackUtil.stringToItemStack(entityItemStack));
				} else if (mc instanceof HopperMinecart && entityItemStack != null) {
					HopperMinecart hmc = (HopperMinecart)mc;
					hmc.getInventory().setContents(ItemStackUtil.stringToItemStack(entityItemStack));
				}
				mc.setVelocity(newVelocity);
			}
		// Parse BungeeCord command packet
		} else if (inChannel.equals("AGBungeeCom")) {
			// Data should be server, command, id and command data
			String msg = new String(data);
			String[] parts = msg.split("#@#");
			
			String command = parts[0];
			String player = parts[1];
			String gateid = parts[2];
			String comdata = parts[3];
			String server = parts[4];
			
			// Message response
			String response = null;
			
			// Parse "setto" command
			if (command.toLowerCase().equals("setto")) {
				// Check player has permission
				if(!Plugin.hasPermManage(player, "ancientgates.setto.bungee")) {
					response = "You lack the permissions to Set \"to\" to your location.";
				// Check gate exists
				} else if (!Gate.exists(gateid)) {
					response = "There exists no gate with id \""+gateid+"\" on server \""+server+"\"";
				
				// Get gate
				} else {
					Gate gate = Gate.get(gateid);
					
					// Set gate location
					if (gate.getBungeeTos() == null || gate.getBungeeTos().size() <= 1) {
						gate.addTo(null);
						gate.addBungeeTo(null, null); // Wipe previous bungeeto
						gate.addBungeeTo(server, comdata);
						response = "To location for gate \""+gateid+"\" on server \""+server+"\" is now where you stand.";
						Gate.save();

					// Display multiple tos exist response
					} else {
						response = "This gate has multiple to locations. Use:\n";
						response += new CommandRemTo().getUsageTemplate(false, true);
					}
				}
			// Parse "addto" command
			} else if (command.toLowerCase().equals("addto")) {
				// Check player has permission
				if(!Plugin.hasPermManage(player, "ancientgates.addto.bungee")) {
					response = "You lack the permissions to Add a \"to\" to your location.";
				// Check gate exists
				} else if (!Gate.exists(gateid)) {
					response = "There exists no gate with id \""+gateid+"\" on server \""+server+"\"";
				
				// Get gate
				} else {
					Gate gate = Gate.get(gateid);
					
					// Add gate location
					if (gate.getBungeeTos() != null && gate.getBungeeTos().size() >= 1) {
						gate.addTo(null);
						gate.addBungeeTo(server, comdata);
						response = "Another \"to\" location for gate \""+gateid+"\" on server \""+server+"\" is now where you stand.";
						Gate.save();

					// Display multiple tos required response
					} else {
						response = "This gate needs an initial \"to\" location. Use:\n";
						response += new CommandSetTo().getUsageTemplate(false, true);
					}
				}
			// Parse "remto" command
			} else if (command.toLowerCase().equals("remto")) {
				// Check player has permission
				if(!Plugin.hasPermManage(player, "ancientgates.remto.bungee")) {
					response = "You lack the permissions to Remove a \"to\" from your location.";
				// Check gate exists
				} else if (!Gate.exists(gateid)) {
					response = "There exists no gate with id \""+gateid+"\" on server \""+server+"\"";
				
				// Get gate
				} else {
					Gate gate = Gate.get(gateid);
					
					// Display no to exists response
					if (gate.getBungeeTos() == null) {
						response = "This gate needs a \"to\" location. Use:\n";
						response += new CommandSetTo().getUsageTemplate(false, true);
					
					// Remove gate location
					} else {
						String nearestBungeeTo = GateUtil.nearestBungeeTo(new WorldCoord(comdata));
						
						if (nearestBungeeTo.isEmpty()) {
							response = "No nearby \"to\" location for gate \""+gateid+"\" on server \""+server+"\".";
						} else {
							gate.delBungeeTo(server, nearestBungeeTo);
							response = "Nearest \"to\" location for gate \""+gateid+"\" on server \""+server+"\" is removed.";
							Gate.save();
						}
					}
				}
			}

			// Send response back if a player is online, otherwise queue
			if (response != null) {
				PluginMessage rMsg = new PluginMessage("Message", player, Conf.colorSystem+response);
				if (Plugin.instance.getServer().getOnlinePlayers().length == 0) {
					Plugin.bungeeMsgQueue.add(rMsg);
				} else {
					Plugin.instance.getServer().getOnlinePlayers()[0].sendPluginMessage(Plugin.instance, "BungeeCord", rMsg.toByteArray());
				}
			}
		// Parse BungeeCord server name packet
		} else if (inChannel.equals("GetServer")) {
			if (Conf.debug) Plugin.log("Getting BungeeCord server name");
			String server = new String(data);
			Plugin.bungeeServerName = server;
		// Parse BungeeCord server list packet
		} else if (inChannel.equals("GetServers")) {
			if (Conf.debug) Plugin.log("Getting BungeeCord server list");
			Plugin.bungeeServerList = TextUtil.split(new String(data), ", ");
		} else {
			return;
		}	
	}
	
}