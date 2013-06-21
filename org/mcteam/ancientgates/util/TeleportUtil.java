package org.mcteam.ancientgates.util;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.entity.minecart.HopperMinecart;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.event.entity.EntityPortalEvent;
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
import org.mcteam.ancientgates.util.types.PluginMessage;

public class TeleportUtil {
	
	private static final String SERVER = "server";
	private static final String WORLD = "world";
	private static final String X = "x";
	private static final String Y = "y";
	private static final String Z = "z";
	private static final String YAW = "yaw";
	private static final String PITCH = "pitch";
	
	// Normal player teleport & BungeeCord player teleport in
	public static void teleportPlayer(Player player, Location location) {
		checkChunkLoad(location.getBlock());
		player.teleport(location);
		player.setFireTicks(0); // Cancel lava fire
	}
	
	// BungeeCord player teleport out
	public static void teleportPlayer(Player player, Map<String, String> location, Boolean fullHeight, String tpMsg) {
		if (Conf.bungeeCordSupport) {
			tpMsg = (tpMsg == null) ? "null" : tpMsg; 
		
			// Imitate teleport by spinning player 180 deg
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
			
			// Send AGBungeeTele packet first
			PluginMessage msg = new PluginMessage(player, location, Plugin.bungeeServerName, tpMsg);
			// Send message over the AGBungeeTele BungeeCord channel
			player.sendPluginMessage(Plugin.instance, "BungeeCord", msg.toByteArray());
			
			// Replace quit message is with BungeeCord teleport message
			Plugin.bungeeCordOutQueue.put(player.getName(), location.get(SERVER));
			
			// Connect player to new server
			try {
				ByteArrayOutputStream b = new ByteArrayOutputStream();
				DataOutputStream out = new DataOutputStream(b);
				out.writeUTF("Connect");
				out.writeUTF(location.get(SERVER));
				
				player.sendPluginMessage(Plugin.instance, "BungeeCord", b.toByteArray());
				b.reset();
			} catch (IOException ex) {
				Plugin.log.severe("Error sending BungeeCord connect packet");
				ex.printStackTrace();
				return;
			}

		}
	}
	
	// Entity teleport
	public static void teleportEntity(EntityPortalEvent event, Location location) {
		checkChunkLoad(location.getBlock());
		event.getEntity().teleport(location);
		event.getEntity().setFireTicks(0); // Cancel lava fire
	}
	
	// BungeeCord entity spawn out
	public static void teleportEntity(EntityPortalEvent event, Map<String, String> location) {
		if (Conf.bungeeCordSupport && event.getEntityType().isSpawnable()) {

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
				Entity entity = world.spawnEntity(destination, queue.getEntityType());
				EntityUtil.setEntityTypeData(entity, queue.getEntityTypeData());
				entity.teleport(destination);
			}
			
			// Remove from queue
			it.remove();
		}
	}

	// Normal vehicle teleport
	public static void teleportVehicle(final Vehicle vehicle, Location location, Boolean teleportEntities) {
		double velocity = vehicle.getVelocity().length();
		checkChunkLoad(location.getBlock());

		// Stop and teleport
		vehicle.setVelocity(new Vector());

		// Get new velocity
		final Vector newVelocity = location.getDirection();
		newVelocity.multiply(velocity);

		final Entity passenger = vehicle.getPassenger();
		if (passenger != null) {
			final Vehicle v = location.getWorld().spawn(location, vehicle.getClass());
			vehicle.eject();
			vehicle.remove();
			passenger.teleport(location);
			passenger.setFireTicks(0); // Cancel lava fire
			Plugin.instance.getServer().getScheduler().scheduleSyncDelayedTask(Plugin.instance, new Runnable() {
				public void run() {
					v.setPassenger(passenger);
					v.setVelocity(newVelocity);
				}
			}, 2);
		} else {
			Vehicle mc = location.getWorld().spawn(location, vehicle.getClass());
			if (mc instanceof StorageMinecart && teleportEntities) {
				StorageMinecart smc = (StorageMinecart)mc;
				smc.getInventory().setContents(((StorageMinecart)vehicle).getInventory().getContents());
			} else if (mc instanceof HopperMinecart && teleportEntities) {
				HopperMinecart hmc = (HopperMinecart)mc;
				hmc.getInventory().setContents(((HopperMinecart)vehicle).getInventory().getContents());
			}
			mc.setVelocity(newVelocity);
			vehicle.remove();
		}
	}
	
	// BungeeCord vehicle teleport out
	public static void teleportVehicle(final Vehicle vehicle, Map<String, String> location, Boolean teleportEntities, Boolean fullHeight, String tpMsg) {		
		if (Conf.bungeeCordSupport) {			
			double velocity = vehicle.getVelocity().length();
			final Entity passenger = vehicle.getPassenger();
			
			// Player vehicle teleport
			if (passenger instanceof Player) {
				Player player = (Player)passenger;
				tpMsg = (tpMsg == null) ? "null" : tpMsg; 

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
				
				// Send AGBungeeVehicleTele packet first
				PluginMessage msg = new PluginMessage(player, vehicle.getType(), velocity, location, Plugin.bungeeServerName, tpMsg);
				// Sent over the AGBungeeVehicleTele BungeeCord channel
				player.sendPluginMessage(Plugin.instance, "BungeeCord", msg.toByteArray());
				
				// Replace quit message is with BungeeCord teleport message
				Plugin.bungeeCordOutQueue.put(player.getName(), location.get(SERVER));
				
				// Connect player to new server
				try {
					ByteArrayOutputStream b = new ByteArrayOutputStream();
					DataOutputStream out = new DataOutputStream(b);
					out.writeUTF("Connect");
					out.writeUTF(location.get(SERVER));
					
					player.sendPluginMessage(Plugin.instance, "BungeeCord", b.toByteArray());
					b.reset();
				} catch (IOException ex) {
					Plugin.log.severe("Error sending BungeeCord connect packet");
					ex.printStackTrace();
					return;
				}

			// Entity vehicle teleport
			} else {
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
					} else if (vehicle instanceof StorageMinecart && teleportEntities) {
						msg.addItemStack(((StorageMinecart)vehicle).getInventory().getContents());	
					} else if (vehicle instanceof HopperMinecart && teleportEntities) {
						msg.addItemStack(((HopperMinecart)vehicle).getInventory().getContents());	
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
					} else if (vehicle instanceof StorageMinecart && teleportEntities) {
						packet.addItemStack(((StorageMinecart)vehicle).getInventory().getContents());
					} else if (vehicle instanceof HopperMinecart && teleportEntities) {
						packet.addItemStack(((HopperMinecart)vehicle).getInventory().getContents());
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
	public static void teleportVehicle(final Player player, int vehicleTypeId, double velocity, Location location) {
		checkChunkLoad(location.getBlock());

		// Crete new velocity
		final Vector newVelocity = location.getDirection();
		newVelocity.multiply(velocity);

		final Vehicle v = (Vehicle)location.getWorld().spawnEntity(location, EntityType.fromId(vehicleTypeId));
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
