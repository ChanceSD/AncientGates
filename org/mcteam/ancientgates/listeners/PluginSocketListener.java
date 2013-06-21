package org.mcteam.ancientgates.listeners;

import org.bukkit.entity.EntityType;
import org.mcteam.ancientgates.Conf;
import org.mcteam.ancientgates.Gate;
import org.mcteam.ancientgates.Plugin;
import org.mcteam.ancientgates.queue.BungeeQueue;
import org.mcteam.ancientgates.sockets.events.ClientConnectionEvent;
import org.mcteam.ancientgates.sockets.events.ClientRecieveEvent;
import org.mcteam.ancientgates.sockets.events.SocketServerEventListener;
import org.mcteam.ancientgates.sockets.types.Packet;
import org.mcteam.ancientgates.util.TeleportUtil;

public class PluginSocketListener implements SocketServerEventListener {
	
	@Override
	public void onClientRecieve(ClientRecieveEvent event) {
		if (!Conf.bungeeCordSupport || !Conf.useSocketComms) {
			return;
		}
		
		// Get command from message
		String command = event.getCommand();
					
		// Parse "setto" command
		if (command.toLowerCase().equals("setto")) {		
			// Data should be player, server, id and command data
			String[] parts = event.getArguments();	
			String player = parts[0];
			String server = parts[1];
			String gateid = parts[2];
			String location = parts[3];
			String fromserver = parts[4];
						
			// Message response
			String response;
			
			// Check player has permission
			if(!Plugin.hasPermManage(player, "ancientgates.setto.bungee")) {
				response = "You lack the permissions to Set \"to\" to your location.";
			// Check gate exists
			} else if (!Gate.exists(gateid)) {
				response = "There exists no gate with id \""+gateid+"\" on server \""+fromserver+"\"";
							
			// Set gate location
			} else {
				Gate gate = Gate.get(gateid);
				gate.setTo(null);
				gate.setBungeeTo(server, location);
				response = "To location for gate \""+gateid+"\" on server \""+fromserver+"\" is now where you stand.";
				Gate.save();
			}

			// Build the packet, format is <message>
			String[] args = {response};
			Packet packet = new Packet("sendmsg", "setto", args);
			
			// Sent to querying server
			Plugin.serv.sendToClient(event.getID(), packet);
			
		// Parse "spawnentity" command
		} else if (command.toLowerCase().equals("spawnentity")) {
			// Data should be entity id, entity world, entitytype id and destination location
			String[] parts = event.getArguments();	
			String entityId = parts[0];
			String entityWorld = parts[1];
			int entityTypeId = Integer.parseInt(parts[2]);
			String entityTypeData = parts[3];
			String destination = parts[4];
			
			// Build the packet, format is <message>
			String[] args = {entityWorld, entityId};
			Packet packet = new Packet("removeentity", "spawnentity", args);

			if (EntityType.fromId(entityTypeId).isSpawnable()) {
				// Add entity to spawn queue
				Plugin.bungeeCordEntityInQueue.add(new BungeeQueue(entityTypeId, entityTypeData, destination));
				
				// Schedule synchronous task to process spawn queue
				Plugin.instance.getServer().getScheduler().scheduleSyncDelayedTask(Plugin.instance, new Runnable() {
					public void run() {
						TeleportUtil.teleportEntity();
					}
				});
				
				// Send entity removal command back to client
				Plugin.serv.sendToClient(event.getID(), packet);
			}
		// Parse "spawnvehicle" command
		} else if (command.toLowerCase().equals("spawnvehicle")) {
			// Data should be vehicle id, vehicle world, vehicletype id, velocity and destination location
			// [additionally entity id, entitytype id and entitytype data]
			String[] parts = event.getArguments();	
			String vehicleId = parts[0];
			String vehicleWorld = parts[1];
			int vehicleTypeId = Integer.parseInt(parts[2]);
			double velocity = Double.parseDouble(parts[3]);
			String destination = parts[4];
			
			// Initialise spawn queue
			BungeeQueue queue;
			
			// Parse passenger info
			String[] args = null;
			if(parts[6] != null) {
				String entityId = parts[5];
				int entityTypeId = Integer.parseInt(parts[6]);
				String entityTypeData = parts[7];
				
				// Build spawn queue (incl. passenger info)
				queue = new BungeeQueue(vehicleTypeId, velocity, destination, entityTypeId, entityTypeData);
				
				// Build the packet, format is <message>
				args = new String[] {vehicleWorld, vehicleId, entityId};
			// Parse contents info	
			} else if(parts[5] != null) {
				String entityItemStack = parts[5];
				
				// Build spawn queue (incl. contents info)
				queue = new BungeeQueue(vehicleTypeId, velocity, destination, entityItemStack);
					
				// Build the packet, format is <message>
				args = new String[] {vehicleWorld, vehicleId};
			} else {
				// Build spawn queue
				queue = new BungeeQueue(vehicleTypeId, velocity, destination);
				
				// Build the packet, format is <message>
				args = new String[] {vehicleWorld, vehicleId};
			}
			Packet packet = new Packet("removevehicle", "spawnvehicle", args);

			// Add entity to spawn queue
			Plugin.bungeeCordVehicleInQueue.add(queue);
				
			// Schedule synchronous task to process spawn queue
			Plugin.instance.getServer().getScheduler().scheduleSyncDelayedTask(Plugin.instance, new Runnable() {
				public void run() {
					TeleportUtil.teleportVehicle();
				}
			});
				
			// Send entity removal command back to client
			Plugin.serv.sendToClient(event.getID(), packet);
		}
	}

	@Override
	public void onClientConnect(ClientConnectionEvent event) {
		if (Conf.debug) Plugin.log("Socket client connected "+event.getClientID()+".");
	}

	@Override
	public void onClientDisconnect(ClientConnectionEvent event) {
		if (Conf.debug) Plugin.log("Socket client disconnected "+event.getClientID()+".");
	}

}