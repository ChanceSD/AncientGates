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
import org.mcteam.ancientgates.sockets.SocketClient;
import org.mcteam.ancientgates.sockets.events.SocketClientEventListener;
import org.mcteam.ancientgates.sockets.packets.Packet;
import org.mcteam.ancientgates.sockets.packets.Packets;

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
	public static void teleportPlayer(Player player, Map<String, String> location, Boolean fullHeight) {
		if (Conf.bungeeCordSupport) {
		
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
			try {
				// Build the message, format is <player>#@#<destination>
				String msg = player.getName() + "#@#" + locationToString(location);
				// Build the message data, sent over the AGBungeeTele BungeeCord channel
				ByteArrayOutputStream b = new ByteArrayOutputStream();
				DataOutputStream out = new DataOutputStream(b);
				out.writeUTF("Forward");
				out.writeUTF(location.get(SERVER));	// Server
				out.writeUTF("AGBungeeTele");		// Channel
				out.writeShort(msg.length()); 		// Data Length
				out.writeBytes(msg); 				// Data
				player.sendPluginMessage(Plugin.instance, "BungeeCord", b.toByteArray());
			} catch (IOException ex) {
				Plugin.log.severe("Error sending BungeeCord teleport packet");
				ex.printStackTrace();
				return;
			}
			
			// Ensure quit message is blocked
			Plugin.bungeeCordBlockQuitQueue.add(player.getName());
			
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
				try {
					// Build the message, format is <entityTypeId>#@#<entityTypeData>#@#<destination>
					String msg = String.valueOf(event.getEntityType().getTypeId()) + "#@#" + EntityUtil.getEntityTypeData(event.getEntity()) + "#@#" + locationToString(location);
					// Build the message data, sent over the AGBungeeTele BungeeCord channel
					ByteArrayOutputStream b = new ByteArrayOutputStream();
					DataOutputStream out = new DataOutputStream(b);
					out.writeUTF("Forward");
					out.writeUTF(location.get(SERVER));	// Server
					out.writeUTF("AGBungeeSpawn");		// Channel
					out.writeShort(msg.length()); 		// Data Length
					out.writeBytes(msg); 				// Data
					if (Plugin.instance.getServer().getOnlinePlayers().length > 0) {
						// Use any player to send the plugin message
						Plugin.instance.getServer().getOnlinePlayers()[0].sendPluginMessage(Plugin.instance, "BungeeCord", b.toByteArray());
						// Imitate teleport by removing entity
						event.getEntity().remove();
					}
				} catch (IOException ex) {
					Plugin.log.severe("Error sending BungeeCord spawn packet");
					ex.printStackTrace();
					return;
				}
			
			// Send spawn command packet via client socket
			} else {
				// Get server
				Server server = Server.get(location.get(SERVER));
				// Build the packet, format is <entityId>,<entityWorld>,<entityTypeId>,<entityTypeData>,<location>
				Packet packet = new Packet();
				packet.command = "spawnentity";
				packet.args = new String[] {String.valueOf(event.getEntity().getEntityId()), event.getEntity().getWorld().getName(), String.valueOf(event.getEntityType().getTypeId()), EntityUtil.getEntityTypeData(event.getEntity()), locationToString(location)};
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
    	List<String> entityQueue = Plugin.bungeeCordEntityInQueue;
    	Iterator<String> it = entityQueue.iterator();
        
        while (it.hasNext()) {
            String entityData = it.next();
			// Data should be entityTypeId, entityTypeData and destination location
    		String[] parts = entityData.split("#@#");
			int entityTypeId = Integer.parseInt(parts[0]);
			String entityTypeData = parts[1];
			String destination = parts[2];

			// Spawn incoming BungeeCord entity
			Location location = TeleportUtil.stringToLocation(destination);
			World world = TeleportUtil.stringToWorld(destination);
			checkChunkLoad(location.getBlock());
			
			if (EntityType.fromId(entityTypeId).isSpawnable()) {
				Entity entity = world.spawnEntity(location, EntityType.fromId(entityTypeId));
				EntityUtil.setEntityTypeData(entity, entityTypeData);
				entity.teleport(location);
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
	public static void teleportVehicle(final Vehicle vehicle, Map<String, String> location, Boolean teleportEntities, Boolean fullHeight) {		
		if (Conf.bungeeCordSupport) {			
			double velocity = vehicle.getVelocity().length();
			final Entity passenger = vehicle.getPassenger();
			
			// Player vehicle teleport
			if (passenger instanceof Player) {
				Player player = (Player)passenger;

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
				try {
					// Build the message, format is <player>#@#<vehicleTypeId>#@#<velocity>#@#<destination>
					String msg = player.getName() + "#@#" + String.valueOf(vehicle.getType().getTypeId()) + "#@#" + String.valueOf(velocity) + "#@#" + locationToString(location);
					// Build the message data, sent over the AGBungeeVehicleTele BungeeCord channel
					ByteArrayOutputStream b = new ByteArrayOutputStream();
					DataOutputStream out = new DataOutputStream(b);
					out.writeUTF("Forward");
					out.writeUTF(location.get(SERVER));		// Server
					out.writeUTF("AGBungeeVehicleTele");	// Channel
					out.writeShort(msg.length()); 			// Data Length
					out.writeBytes(msg); 					// Data
					player.sendPluginMessage(Plugin.instance, "BungeeCord", b.toByteArray());
				} catch (IOException ex) {
					Plugin.log.severe("Error sending BungeeCord vehicle teleport packet");
					ex.printStackTrace();
					return;
				}
				
				// Ensure quit message is blocked
				Plugin.bungeeCordBlockQuitQueue.add(player.getName());
				
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
					try {
						// Build the message, format is <vehicleTypeId>#@#<velocity>#@#<destination>[#@#<entityTypeId>#@#<entityTypeData>]
						String msg = String.valueOf(vehicle.getType().getTypeId()) + "#@#" + String.valueOf(velocity) + "#@#" + locationToString(location);
						// Append passenger info
						if (passenger != null) {
							if (passenger.getType().isSpawnable()) {
								msg = msg + "#@#" + String.valueOf(passenger.getType().getTypeId()) + "#@#" + EntityUtil.getEntityTypeData(passenger);
							}
						// Append vehicle contents
						} else if (vehicle instanceof StorageMinecart && teleportEntities) {
							msg = msg + "#@#" + ItemStackUtil.itemStackToString(((StorageMinecart)vehicle).getInventory().getContents());	
						} else if (vehicle instanceof HopperMinecart && teleportEntities) {
							msg = msg + "#@#" + ItemStackUtil.itemStackToString(((HopperMinecart)vehicle).getInventory().getContents());	
						}
						// Build the message data, sent over the AGBungeeTele BungeeCord channel
						ByteArrayOutputStream b = new ByteArrayOutputStream();
						DataOutputStream out = new DataOutputStream(b);
						out.writeUTF("Forward");
						out.writeUTF(location.get(SERVER));		// Server
						out.writeUTF("AGBungeeVehicleSpawn");	// Channel
						out.writeShort(msg.length()); 			// Data Length
						out.writeBytes(msg); 					// Data
						if (Plugin.instance.getServer().getOnlinePlayers().length > 0) {
							// Use any player to send the plugin message
							Plugin.instance.getServer().getOnlinePlayers()[0].sendPluginMessage(Plugin.instance, "BungeeCord", b.toByteArray());
							// Imitate teleport by removing entity and vehicle
							vehicle.eject();
							vehicle.remove();
							if (passenger != null) {
								passenger.remove();
							}
						}
					} catch (IOException ex) {
						Plugin.log.severe("Error sending BungeeCord vehicle spawn packet");
						ex.printStackTrace();
						return;
					}
				// Send vehicle spawn command packet via client socket
				} else {
					// Get server
					Server server = Server.get(location.get(SERVER));
					// Build the packet, format is <vehicleId>,<vehicleWorld>,<vehicleTypeId>,<velocity>,<location>[,<entityId>,<entityTypeId>,<entityTypeData>]
					Packet packet = new Packet();
					packet.command = "spawnvehicle";
					packet.args = new String[] {String.valueOf(vehicle.getEntityId()), vehicle.getWorld().getName(), String.valueOf(vehicle.getType().getTypeId()), String.valueOf(velocity), locationToString(location)};
					// Append passenger info
					if (passenger != null) {
						if (passenger.getType().isSpawnable()) {
							packet.args = new String[] {String.valueOf(vehicle.getEntityId()), vehicle.getWorld().getName(), String.valueOf(vehicle.getType().getTypeId()), String.valueOf(velocity), locationToString(location), String.valueOf(passenger.getEntityId()), String.valueOf(passenger.getType().getTypeId()), EntityUtil.getEntityTypeData(passenger)};
						}
					// Append vehicle contents
					} else if (vehicle instanceof StorageMinecart && teleportEntities) {
						packet.args = new String[] {String.valueOf(vehicle.getEntityId()), vehicle.getWorld().getName(), String.valueOf(vehicle.getType().getTypeId()), String.valueOf(velocity), locationToString(location), ItemStackUtil.itemStackToString(((StorageMinecart)vehicle).getInventory().getContents())};
					} else if (vehicle instanceof HopperMinecart && teleportEntities) {
						packet.args = new String[] {String.valueOf(vehicle.getEntityId()), vehicle.getWorld().getName(), String.valueOf(vehicle.getType().getTypeId()), String.valueOf(velocity), locationToString(location), ItemStackUtil.itemStackToString(((HopperMinecart)vehicle).getInventory().getContents())};
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
    	List<String> vehicleQueue = Plugin.bungeeCordPassEntInQueue;
    	Iterator<String> it = vehicleQueue.iterator();
        
        while (it.hasNext()) {
            String vehicleData = it.next();
			// Data should be entityTypeId, entityTypeData and destination location
    		String[] parts = vehicleData.split("#@#");
			int vehicleTypeId = Integer.parseInt(parts[0]);
			double velocity = Double.parseDouble(parts[1]);
			String destination = parts[2];

			// Spawn incoming BungeeCord vehicle
			Location location = TeleportUtil.stringToLocation(destination);
			World world = TeleportUtil.stringToWorld(destination);
			checkChunkLoad(location.getBlock());
			
			Entity entity = null;
			String entityItemStack = null;
			
			// Parse passenger info
			if (parts.length > 4) {
				int entityTypeId = Integer.parseInt(parts[3]);
				String entityTypeData = parts[4];
				
				if (EntityType.fromId(entityTypeId).isSpawnable()) {
					entity = world.spawnEntity(location, EntityType.fromId(entityTypeId));
					EntityUtil.setEntityTypeData(entity, entityTypeData);
					entity.teleport(location);
				}
			// Parse vehicle contents
			} else if  (parts.length > 3) {
				entityItemStack = parts[3];
			}
			final Entity passenger = entity;
			
			// Create new velocity
			final Vector newVelocity = location.getDirection();
			newVelocity.multiply(velocity);

			if (passenger != null) {
				final Vehicle v = (Vehicle)location.getWorld().spawnEntity(location, EntityType.fromId(vehicleTypeId));
				Plugin.instance.getServer().getScheduler().scheduleSyncDelayedTask(Plugin.instance, new Runnable() {
					public void run() {
						v.setPassenger(passenger);
						v.setVelocity(newVelocity);
					}
				}, 2);
			} else {
				Vehicle mc = (Vehicle)location.getWorld().spawnEntity(location, EntityType.fromId(vehicleTypeId));
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
