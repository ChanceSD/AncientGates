package org.mcteam.ancientgates.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.entity.minecart.HopperMinecart;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import org.mcteam.ancientgates.Conf;
import org.mcteam.ancientgates.Plugin;
import org.mcteam.ancientgates.Server;
import org.mcteam.ancientgates.queue.BungeeQueue;
import org.mcteam.ancientgates.queue.types.BungeeQueueType;
import org.mcteam.ancientgates.sockets.SocketClient;
import org.mcteam.ancientgates.sockets.events.SocketClientEventListener;
import org.mcteam.ancientgates.sockets.types.Packet;
import org.mcteam.ancientgates.sockets.types.Packets;
import org.mcteam.ancientgates.util.types.CommandType;
import org.mcteam.ancientgates.util.types.InvBoolean;
import org.mcteam.ancientgates.util.types.PluginMessage;
import org.mcteam.ancientgates.util.types.TeleportType;

public class TeleportUtil {
	
	private static final String SERVER = "server";
	private static final String WORLD = "world";
	private static final String X = "x";
	private static final String Y = "y";
	private static final String Z = "z";
	private static final String YAW = "yaw";
	private static final String PITCH = "pitch";
	
	// Normal player teleport & BungeeCord player teleport in
	public static void teleportPlayer(final Player player, Location location, Boolean teleportEntities, InvBoolean teleportInventory) {
		checkChunkLoad(location.getBlock());
		
		// Handle player inventory
		if (!teleportInventory.equals(InvBoolean.TRUE)) {
			final ItemStack[] contents = player.getInventory().getContents();
			player.getInventory().clear();
		
			if (teleportInventory.equals(InvBoolean.FALSE)) {
				for (ItemStack itemStack : contents) {
					if (itemStack != null) player.getWorld().dropItemNaturally(player.getLocation(), itemStack);
				}
			}
		}
		
		// Handle player riding an entity
		final Entity entity = player.getVehicle();
		if (player.isInsideVehicle() && entity instanceof LivingEntity) {
			entity.eject();
			if (teleportEntities && !(entity instanceof Player)) entity.remove();
		}

		// Teleport player
		player.teleport(location);
		player.setFireTicks(0); // Cancel lava fire
		
		// Re-mount player on entity
		if (entity != null && teleportEntities && entity instanceof LivingEntity && !(entity instanceof Player)) {
			final Entity e = location.getWorld().spawn(location, entity.getClass());
			EntityUtil.setEntityTypeData(e, EntityUtil.getEntityTypeData(entity)); // Clone entity data
			Plugin.instance.getServer().getScheduler().scheduleSyncDelayedTask(Plugin.instance, new Runnable() {
				public void run() {
					e.setPassenger(player);
				}
			}, 2);
		}
	}
	
	// BungeeCord player teleport out
	public static void teleportPlayer(Player player, Map<String, String> location, TeleportType tpType, Boolean teleportEntities, InvBoolean teleportInventory, Boolean fullHeight, String tpCmd, CommandType tpCmdType, String tpMsg) {
		if (Conf.bungeeCordSupport) {
			// Check bungeeServerName found
			if (Plugin.bungeeServerName == null) {
				// Get current time
		        Long now = Calendar.getInstance().getTimeInMillis();
		        // Display error message
				if (!Plugin.lastMessageTime.containsKey(player.getName()) || Plugin.lastMessageTime.get(player.getName()) < now - 10000L) {
					Plugin.log("Error not yet connected to BungeeCord proxy.");
					player.sendMessage("Error connecting to server. Try again.");
					Plugin.lastMessageTime.put(player.getName(), now);
				}
				return;
			}
			
			// Handle player riding an entity
			final Entity e = player.getVehicle();
			if (player.isInsideVehicle() && e instanceof LivingEntity) e.eject();

			// Imitate teleport by spinning player 180 deg
			if (fullHeight) {
				Location position = player.getLocation();
				float yaw = position.getYaw();
				if ((yaw += 180) > 360) {
					yaw -= 360;
				}
				position.setYaw(yaw);
				if (e != null) e.teleport(position);
				player.teleport(position);
			}
			if (e != null) e.setFireTicks(0); // Cancel lava fire
			player.setFireTicks(0); // Cancel lava fire
			
			// Handle player inventory
			if (!teleportInventory.equals(InvBoolean.TRUE)) {
				final ItemStack[] contents = player.getInventory().getContents();
				player.getInventory().clear();
			
				if (teleportInventory.equals(InvBoolean.FALSE)) {
					for (ItemStack itemStack : contents) {
						if (itemStack != null) player.getWorld().dropItemNaturally(player.getLocation(), itemStack);
					}
				}
			}
			
			// Send AGBungeeTele packet first
			tpCmd = (tpCmd == null) ? "null" : tpCmd; 
			tpMsg = (tpMsg == null) ? "null" : tpMsg; 
			PluginMessage msg;
			// Player server teleport
			if (tpType.equals(TeleportType.SERVER)) {
				msg = new PluginMessage(player, location.get(SERVER), Plugin.bungeeServerName, tpCmd, tpCmdType, tpMsg);
			// Player location teleport
			} else if(e == null || !teleportEntities || e instanceof Player) {
				msg = new PluginMessage(player, location, Plugin.bungeeServerName, tpCmd, tpCmdType, tpMsg);
			// Player riding entity teleport
			} else {
				msg = new PluginMessage(player, e, location, Plugin.bungeeServerName, tpCmd, tpCmdType, tpMsg);
			}
			// Send message over the AGBungeeTele BungeeCord channel
			player.sendPluginMessage(Plugin.instance, "BungeeCord", msg.toByteArray());
			// Imitate teleport by removing entity
			if (e != null && teleportEntities && tpType.equals(TeleportType.LOCATION) && !(e instanceof Player)) e.remove();
		
			// Replace quit message is with BungeeCord teleport message
			Plugin.bungeeCordOutQueue.put(player.getName().toLowerCase(), location.get(SERVER));
			
			// Connect player to new server
			msg = new PluginMessage("Connect", location.get(SERVER));
			player.sendPluginMessage(Plugin.instance, "BungeeCord", msg.toByteArray());
		}
	}
	
	// Entity teleport
	public static void teleportEntity(EntityPortalEvent event, Location location) {
		checkChunkLoad(location.getBlock());
		
		// Remove entity
		final Entity entity = event.getEntity();
		entity.remove();

		// Clone entity - Spawnable
		if (entity.getType().isSpawnable()) {
			Entity e = location.getWorld().spawn(location, entity.getClass());
			EntityUtil.setEntityTypeData(e, EntityUtil.getEntityTypeData(entity));
		
		// Clone entity - Itemstack
		} else if (entity.getType() == EntityType.DROPPED_ITEM) {
			Item i = location.getWorld().dropItemNaturally(location, ((Item)entity).getItemStack());
			i.teleport(location);	
		}

	}
	
	// BungeeCord entity spawn out
	public static void teleportEntity(EntityPortalEvent event, Map<String, String> location) {
		if (Conf.bungeeCordSupport && (event.getEntityType().isSpawnable() || event.getEntityType() == EntityType.DROPPED_ITEM)) {

			// Send spawn command packet via BungeeCord
			if (!Conf.useSocketComms || Plugin.serv == null) {
				// Send AGBungeeSpawn packet
				PluginMessage msg = new PluginMessage(event.getEntityType(), event.getEntity(), location);
				// Send over the AGBungeeTele BungeeCord channel
				if (Plugin.instance.getServer().getOnlinePlayers().length > 0) {
					// Use any player to send the plugin message
					Plugin.instance.getServer().getOnlinePlayers()[0].sendPluginMessage(Plugin.instance, "BungeeCord", msg.toByteArray());
					// Imitate teleport by removing entity
					event.getEntity().remove();
				}
			
			// Send spawn command packet via client socket
			} else {
				// Get server
				Server server = Server.get(location.get(SERVER));
				// Construct spawn entity packet
				Packet packet = new Packet(event.getEntity(), event.getEntityType(), location);
				// Setup socket client and listener
				SocketClient client = new SocketClient(server.getAddress(), server.getPort(), server.getPassword());
				client.setListener(new SocketClientEventListener() {
					public void onServerMessageRecieve(SocketClient client, Packets packets) {
						for (Packet packet : packets.packets) {
							if (packet.command.toLowerCase().equals("removeentity")) {
								// Extract receiving packet arguments
								String world = String.valueOf(packet.args[0]);
								int entityId = Integer.parseInt(packet.args[1]);
								// Iterate and remove teleported entity
								List<Entity> entities = Bukkit.getServer().getWorld(world).getEntities();
						    	Iterator<Entity> it = entities.iterator();
						        while (it.hasNext()) {
						            Entity entity = it.next();
						            if (entity.getEntityId() == entityId) {
						            	entity.remove();
						            	break;
						            }
						        }
							}
						}
						client.close();
					}
				});
				// Connect and send packet
				try {
					client.connect();
					client.send(packet);
				} catch (Exception e) {
					Plugin.log.severe("Error sending spawn packet to the server.");
				}
			
			}
		}
	}
	
	// BungeeCord entity spawn in
	public static void teleportEntity() {
    	List<BungeeQueue> entityQueue = Plugin.bungeeCordEntityInQueue;
    	Iterator<BungeeQueue> it = entityQueue.iterator();
        
        while (it.hasNext()) {
        	BungeeQueue queue = it.next();

			// Spawn incoming BungeeCord entity
			Location destination = queue.getDestination();
			World world = destination.getWorld();
			checkChunkLoad(destination.getBlock());
			
			if (queue.getEntityType().isSpawnable()) {
				Entity entity = world.spawnEntity(destination, queue.getEntityType()); // Entity
				EntityUtil.setEntityTypeData(entity, queue.getEntityTypeData());
				entity.teleport(destination);
			} else if (queue.getEntityType() == EntityType.DROPPED_ITEM) {
				Item item = world.dropItemNaturally(destination, ItemStackUtil.stringToItemStack(queue.getEntityTypeData())[0]); // Dropped ItemStack
				item.teleport(destination);
			}
			
			// Remove from queue
			it.remove();
		}
	}

	// Normal vehicle teleport
	public static void teleportVehicle(final Vehicle vehicle, Location location, Boolean teleportEntities, InvBoolean teleportInventory) {
		final Location destination = GeometryUtil.addHeightToLocation(location, 0.5); // Fix vehicle spawn glitch
		double velocity = vehicle.getVelocity().length();
		checkChunkLoad(destination.getBlock());

		// Stop and teleport
		vehicle.setVelocity(new Vector());

		// Get new velocity
		final Vector newVelocity = destination.getDirection();
		newVelocity.multiply(velocity);

		final Entity passenger = vehicle.getPassenger();
		if (passenger != null) {
			// Handle player inventory
			if (passenger instanceof Player) {
				if (!teleportInventory.equals(InvBoolean.TRUE)) {
					final ItemStack[] contents = ((Player)passenger).getInventory().getContents();
					((Player)passenger).getInventory().clear();
			
					if (teleportInventory.equals(InvBoolean.FALSE)) {
						for (ItemStack itemStack : contents) {
							if (itemStack != null) ((Player)passenger).getWorld().dropItemNaturally(((Player)passenger).getLocation(), itemStack);
						}
					}
				}
			}
			final Vehicle v = destination.getWorld().spawn(destination, vehicle.getClass());
			vehicle.eject();
			vehicle.remove();
			Plugin.instance.getServer().getScheduler().scheduleSyncDelayedTask(Plugin.instance, new Runnable() {
				public void run() {
					passenger.teleport(destination);
					passenger.setFireTicks(0); // Cancel lava fire
				}
			}, 0);
			Plugin.instance.getServer().getScheduler().scheduleSyncDelayedTask(Plugin.instance, new Runnable() {
				public void run() {
					v.setPassenger(passenger);
					v.setVelocity(newVelocity);
				}
			}, 2);
		} else {
			ItemStack[] contents;
			Vehicle mc = destination.getWorld().spawn(destination, vehicle.getClass());
			if (mc instanceof StorageMinecart) {
				contents = ((StorageMinecart)vehicle).getInventory().getContents();
				// Teleport contents
				if (teleportEntities) {
					StorageMinecart smc = (StorageMinecart)mc;
					smc.getInventory().setContents(contents);
				// Drop contents
				} else {
					for (ItemStack itemStack : contents) {
						if (itemStack != null) vehicle.getWorld().dropItemNaturally(vehicle.getLocation(), itemStack);
					}
				}
			} else if (mc instanceof HopperMinecart) {
				contents = ((HopperMinecart)vehicle).getInventory().getContents();
				// Teleport contents
				if (teleportEntities) {
					HopperMinecart hmc = (HopperMinecart)mc;
					hmc.getInventory().setContents(contents);
				// Drop contents
				} else {
					for (ItemStack itemStack : contents) {
						if (itemStack != null) vehicle.getWorld().dropItemNaturally(vehicle.getLocation(), itemStack);
					}
				}
			}
			mc.setVelocity(newVelocity);
			vehicle.remove();
		}
	}
	
	// BungeeCord vehicle teleport out
	public static void teleportVehicle(final Vehicle vehicle, Map<String, String> location, TeleportType tpType, Boolean teleportEntities, InvBoolean teleportInventory, Boolean fullHeight, String tpCmd, CommandType tpCmdType, String tpMsg) {		
		if (Conf.bungeeCordSupport) {			
			double velocity = vehicle.getVelocity().length();
			final Entity passenger = vehicle.getPassenger();
			
			// Player vehicle teleport
			if (passenger instanceof Player) {
				Player player = (Player)passenger;
				
				// Check bungeeServerName found
				if (Plugin.bungeeServerName == null) {
					// Get current time
			        Long now = Calendar.getInstance().getTimeInMillis();
					// Display error message
					if (!Plugin.lastMessageTime.containsKey(player.getName()) || Plugin.lastMessageTime.get(player.getName()) < now - 10000L) {
						Plugin.log("Error not yet connected to BungeeCord proxy.");
						player.sendMessage("Error connecting to server. Try again.");
						Plugin.lastMessageTime.put(player.getName(), now);
					}
					return;
				}

				// Imitate teleport by stopping and ejecting
				vehicle.setVelocity(new Vector());
				vehicle.eject();
				vehicle.remove();
				// and spinning player 180 deg
				if (fullHeight) {
					Location position = player.getLocation();
					float yaw = position.getYaw();
					if ((yaw += 180) > 360) {
						yaw -= 360;
					}
					position.setYaw(yaw);
					player.teleport(position);
				}
				player.setFireTicks(0); // Cancel lava fire
				
				// Handle player inventory
				if (!teleportInventory.equals(InvBoolean.TRUE)) {
					final ItemStack[] contents = player.getInventory().getContents();
					player.getInventory().clear();
				
					if (teleportInventory.equals(InvBoolean.FALSE)) {
						for (ItemStack itemStack : contents) {
							if (itemStack != null) player.getWorld().dropItemNaturally(player.getLocation(), itemStack);
						}
					}
				}
				
				// Send AGBungeeTele/AGBungeeVehicleTele packet first
				tpCmd = (tpCmd == null) ? "null" : tpCmd;
				tpMsg = (tpMsg == null) ? "null" : tpMsg;
				PluginMessage msg;
				if (tpType.equals(TeleportType.SERVER)) {
					msg = new PluginMessage(player, location.get(SERVER), Plugin.bungeeServerName, tpCmd, tpCmdType, tpMsg);
				} else {
					msg = new PluginMessage(player, vehicle.getType(), velocity, location, Plugin.bungeeServerName, tpCmd, tpCmdType, tpMsg);
				}
				// Sent over the AGBungeeVehicleTele BungeeCord channel
				player.sendPluginMessage(Plugin.instance, "BungeeCord", msg.toByteArray());
				
				// Replace quit message is with BungeeCord teleport message
				Plugin.bungeeCordOutQueue.put(player.getName().toLowerCase(), location.get(SERVER));
				
				// Connect player to new server
				msg = new PluginMessage("Connect", location.get(SERVER));
				player.sendPluginMessage(Plugin.instance, "BungeeCord", msg.toByteArray());

			// Entity vehicle teleport
			} else {
				ItemStack[] contents;
				
				// Imitate teleport by stopping
				vehicle.setVelocity(new Vector());
				
				// Send vehicle spawn command packet via BungeeCord
				if (!Conf.useSocketComms || Plugin.serv == null) {
					// Send AGBungeeVehicleSpawn packet
					PluginMessage msg = new PluginMessage(vehicle.getType(), velocity, location);
					
					// Append passenger info
					if (passenger != null) {
						if (passenger.getType().isSpawnable()) {
							msg.addEntity(passenger);
						}
					// Append vehicle contents
					} else if (vehicle instanceof StorageMinecart) {
						contents = ((StorageMinecart)vehicle).getInventory().getContents();
						// Add contents
						if (teleportEntities) {
							msg.addItemStack(contents);
						// Drop contents
						} else {
							for (ItemStack itemStack : contents) {
								if (itemStack != null) vehicle.getWorld().dropItemNaturally(vehicle.getLocation(), itemStack);
							}
						}
					} else if (vehicle instanceof HopperMinecart) {
						contents = ((HopperMinecart)vehicle).getInventory().getContents();
						// Add contents
						if (teleportEntities) {
							msg.addItemStack(((HopperMinecart)vehicle).getInventory().getContents());
						// Drop contents
						} else {
							for (ItemStack itemStack : contents) {
								if (itemStack != null) vehicle.getWorld().dropItemNaturally(vehicle.getLocation(), itemStack);
							}	
						}
					}
					
					// Build the message data, sent over the AGBungeeTele BungeeCord channel
					if (Plugin.instance.getServer().getOnlinePlayers().length > 0) {
						// Use any player to send the plugin message
						Plugin.instance.getServer().getOnlinePlayers()[0].sendPluginMessage(Plugin.instance, "BungeeCord", msg.toByteArray());
						// Imitate teleport by removing entity and vehicle
						vehicle.eject();
						vehicle.remove();
						if (passenger != null) {
							passenger.remove();
						}
					}
				// Send vehicle spawn command packet via client socket
				} else {
					// Get server
					Server server = Server.get(location.get(SERVER));				
					// Construct spawn vehicle packet
					Packet packet = new Packet(vehicle, velocity, location);
					// Append passenger info
					if (passenger != null) {
						if (passenger.getType().isSpawnable()) {
							packet.addPassenger(passenger);
						}
					// Append vehicle contents
					} else if (vehicle instanceof StorageMinecart) {
						contents = ((StorageMinecart)vehicle).getInventory().getContents();
						// Add contents
						if (teleportEntities) {
							packet.addItemStack(contents);
						// Drop contents
						} else {
							for (ItemStack itemStack : contents) {
								if (itemStack != null) vehicle.getWorld().dropItemNaturally(vehicle.getLocation(), itemStack);	
							}
						}
					} else if (vehicle instanceof HopperMinecart) {
						contents = ((HopperMinecart)vehicle).getInventory().getContents();
						// Add contents
						if (teleportEntities) {
							packet.addItemStack(((HopperMinecart)vehicle).getInventory().getContents());
						// Drop contents
						} else {
							for (ItemStack itemStack : contents) {
								if (itemStack != null) vehicle.getWorld().dropItemNaturally(vehicle.getLocation(), itemStack);
							}	
						}
					}
					
					// Setup socket client and listener
					SocketClient client = new SocketClient(server.getAddress(), server.getPort(), server.getPassword());
					client.setListener(new SocketClientEventListener() {
						public void onServerMessageRecieve(SocketClient client, Packets packets) {
							for (Packet packet : packets.packets) {
								if (packet.command.toLowerCase().equals("removevehicle")) {
									// Extract receiving packet arguments
									String world = String.valueOf(packet.args[0]);
									int vehicleId = Integer.parseInt(packet.args[1]);
									// Iterate and remove teleported vehicle
									List<Entity> entities = Bukkit.getServer().getWorld(world).getEntities();
									Iterator<Entity> it = entities.iterator();
									while (it.hasNext()) {
										Entity vehicle = it.next();
										if (vehicle.getEntityId() == vehicleId) {
											vehicle.eject();
											vehicle.remove();
											break;
										}
									}
									// Iterate and remove teleported passenger
									if (packet.args.length > 2) {
										int entityId = Integer.parseInt(packet.args[2]);
										while (it.hasNext()) {
											Entity entity = it.next();
											if (entity.getEntityId() == entityId) {
												entity.remove();
												break;
											}
										}
									}
								}
							}
							client.close();
						}
					});
					// Connect and send packet
					try {
						client.connect();
						client.send(packet);
					} catch (Exception e) {
						Plugin.log.severe("Error sending vehicle spawn packet to the server.");
					}
				}
			}
			
		}
		
	}
	
	// Bungee vehicle teleport in
	public static void teleportVehicle(final Player player, String vehicleTypeName, double velocity, Location location) {
		checkChunkLoad(location.getBlock());

		// Crete new velocity
		final Vector newVelocity = location.getDirection();
		newVelocity.multiply(velocity);

		final Vehicle v = (Vehicle)location.getWorld().spawnEntity(location, EntityUtil.entityType(vehicleTypeName));
		player.teleport(location);
		Plugin.instance.getServer().getScheduler().scheduleSyncDelayedTask(Plugin.instance, new Runnable() {
			public void run() {
				v.setPassenger(player);
				v.setVelocity(newVelocity);
			}
		}, 2);
	}
	
	// BungeeCord vehicle spawn in
	public static void teleportVehicle() {
    	List<BungeeQueue> vehicleQueue = Plugin.bungeeCordVehicleInQueue;
    	Iterator<BungeeQueue> it = vehicleQueue.iterator();
        
        while (it.hasNext()) {
        	BungeeQueue queue = it.next();

			// Spawn incoming BungeeCord vehicle
			Location destination = queue.getDestination();
			World world = destination.getWorld();
			checkChunkLoad(destination.getBlock());
			
			Entity entity = null;
			String entityItemStack = null;
			
			// Parse passenger info
			if (queue.getQueueType() == BungeeQueueType.PASSENGER) {				
				if (queue.getEntityType().isSpawnable()) {
					entity = world.spawnEntity(destination, queue.getEntityType());
					EntityUtil.setEntityTypeData(entity, queue.getEntityTypeData());
					entity.teleport(destination);
				}
			// Parse vehicle contents
			} else if (queue.getItemStack() != null) {
				entityItemStack = queue.getItemStack();
			}
			final Entity passenger = entity;
			
			// Create new velocity
			final Vector newVelocity = destination.getDirection();
			newVelocity.multiply(queue.getVelocity());

			if (passenger != null) {
				final Vehicle v = (Vehicle)world.spawnEntity(destination, queue.getVehicleType());
				Plugin.instance.getServer().getScheduler().scheduleSyncDelayedTask(Plugin.instance, new Runnable() {
					public void run() {
						v.setPassenger(passenger);
						v.setVelocity(newVelocity);
					}
				}, 2);
			} else {
				Vehicle mc = (Vehicle)world.spawnEntity(destination, queue.getVehicleType());
				if (mc instanceof StorageMinecart && entityItemStack != null) {
					StorageMinecart smc = (StorageMinecart)mc;
					smc.getInventory().setContents(ItemStackUtil.stringToItemStack(entityItemStack));
				} else if (mc instanceof HopperMinecart && entityItemStack != null) {
					HopperMinecart hmc = (HopperMinecart)mc;
					hmc.getInventory().setContents(ItemStackUtil.stringToItemStack(entityItemStack));
				}
				mc.setVelocity(newVelocity);
			}
			
			// Remove from queue
			it.remove();
		}
	}

	// Pre-load chuck before teleport/spawn
	private static void checkChunkLoad(Block b) {
		World w = b.getWorld();
		Chunk c = b.getChunk();
		
		if ( ! w.isChunkLoaded(c) ) {
		    Plugin.log(Level.FINE, "Loading chunk: " + c.toString() + " on: " + w.toString());
			w.loadChunk(c);
		}
	}
	
	// Convert string to world
	public static World stringToWorld(String str) {
		ArrayList<String> parts = new ArrayList<String>(Arrays.asList(str.trim().split(",")));
		World world = Plugin.instance.getServer().getWorld(parts.get(0));
		
		return world;
	}
	
	// Convert string to location
	public static Location stringToLocation(String str) {
		ArrayList<String> parts = new ArrayList<String>(Arrays.asList(str.trim().split(",")));
		
		World world = Plugin.instance.getServer().getWorld(parts.get(0));
		double x = Double.parseDouble(parts.get(1));
		double y = Double.parseDouble(parts.get(2));
		double z = Double.parseDouble(parts.get(3));
		float yaw = Float.parseFloat(parts.get(4));
		float pitch = Float.parseFloat(parts.get(5));
		
		return new Location(world, x, y, z, yaw, pitch);
	}
	
	// Convert location to string
	public static String locationToString(Location location) {
		String world = location.getWorld().getName();
		String x = String.valueOf(location.getX());
		String y = String.valueOf(location.getY());
		String z = String.valueOf(location.getZ());
		String yaw = String.valueOf(location.getYaw());
		String pitch = String.valueOf(location.getPitch());
		
		return world + "," + x + "," + y + "," + z + "," + yaw + "," + pitch;
	}
	
	// Convert location to string (BungeeCord location)
	public static String locationToString(Map<String, String> location) {
		String world= location.get(WORLD);
		String x = location.get(X);
		String y = location.get(Y);
		String z = location.get(Z);
		String yaw = location.get(YAW);
		String pitch = location.get(PITCH);
		
		return world + "," + x + "," + y + "," + z + "," + yaw + "," + pitch;
	}
	
}
