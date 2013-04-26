package org.mcteam.ancientgates.listeners;

import org.bukkit.entity.EntityType;
import org.mcteam.ancientgates.Conf;
import org.mcteam.ancientgates.Gate;
import org.mcteam.ancientgates.Plugin;
import org.mcteam.ancientgates.sockets.events.ClientConnectionEvent;
import org.mcteam.ancientgates.sockets.events.ClientRecieveEvent;
import org.mcteam.ancientgates.sockets.events.SocketServerEventListener;
import org.mcteam.ancientgates.sockets.packets.Packet;
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
						
			// Message response
			String response;
			
			// Check player has permission
			if(!Plugin.hasPermManage(player, "ancientgates.setto.bungee")) {
				response = "You lack the permissions to Set \"to\" to your location.";
			// Check gate exists
			} else if (!Gate.exists(gateid)) {
				response = "There exists no gate with id \""+gateid+"\" on server \""+Conf.bungeeServerName+"\"";
							
			// Set gate location
			} else {
				Gate gate = Gate.get(gateid);
				gate.setTo(null);
				gate.setBungeeTo(server, location);
				response = "To location for gate \""+gateid+"\" on server \""+Conf.bungeeServerName+"\" is now where you stand.";
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
				Plugin.bungeeCordEntityInQueue.add(String.valueOf(entityTypeId)+"#@#"+entityTypeData+"#@#"+destination);
				
				// Schedule synchronous task to process spawn queue
				Plugin.instance.getServer().getScheduler().scheduleSyncDelayedTask(Plugin.instance, new Runnable() {
					public void run() {
						TeleportUtil.teleportEntity();
					}
				});
				
				// Send entity removal command back to client
				Plugin.serv.sendToClient(event.getID(), packet);
			}
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