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
import org.bukkit.event.entity.EntityPortalEvent;
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
	
	// Normal teleport & BungeeCord teleport in
	public static void teleportPlayer(Player player, Location location) {
		checkChunkLoad(location.getBlock());
		player.teleport(location);
	}
	
	// BungeeCord teleport out
	public static void teleportPlayer(Player player, Map<String, String> location) {
		if (Conf.bungeeCordSupport) {
		
			// Imitate teleport by spinning player 180 deg
			Location position = player.getLocation();
			float yaw = position.getYaw();
			if ((yaw += 180) > 360) {
				yaw -= 360;
			}
			position.setYaw(yaw);
			player.teleport(position);
			
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
		event.getEntity().teleport(location);
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
			
			if (EntityType.fromId(entityTypeId).isSpawnable()) {
				Entity entity = world.spawnEntity(location, EntityType.fromId(entityTypeId));
				EntityUtil.setEntityTypeData(entity, entityTypeData);
				entity.teleport(location);
			}
			
			// Remove from queue
			it.remove();
		}
	}
	
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
