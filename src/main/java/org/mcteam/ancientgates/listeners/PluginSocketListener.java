package org.mcteam.ancientgates.listeners;

import org.mcteam.ancientgates.Conf;
import org.mcteam.ancientgates.Gate;
import org.mcteam.ancientgates.Plugin;
import org.mcteam.ancientgates.commands.base.CommandRemTo;
import org.mcteam.ancientgates.commands.base.CommandSetTo;
import org.mcteam.ancientgates.queue.BungeeQueue;
import org.mcteam.ancientgates.sockets.events.ClientConnectionEvent;
import org.mcteam.ancientgates.sockets.events.ClientRecieveEvent;
import org.mcteam.ancientgates.sockets.events.SocketServerEventListener;
import org.mcteam.ancientgates.sockets.types.Packet;
import org.mcteam.ancientgates.util.EntityUtil;
import org.mcteam.ancientgates.util.GateUtil;
import org.mcteam.ancientgates.util.TeleportUtil;
import org.mcteam.ancientgates.util.types.WorldCoord;

public class PluginSocketListener implements SocketServerEventListener {

	@Override
	public void onClientRecieve(final ClientRecieveEvent event) {
		if (!Conf.bungeeCordSupport || !Conf.useSocketComms) {
			return;
		}

		// Get command from message
		final String command = event.getCommand();

		// Parse "setto" command
		if (command.toLowerCase().equals("setto")) {
			// Data should be player, server, id and command data
			final String[] parts = event.getArguments();
			final String player = parts[0];
			final String server = parts[1];
			final String gateid = parts[2];
			final String location = parts[3];
			final String fromserver = parts[4];

			// Message response
			String response;

			// Check player has permission
			if (!Plugin.hasPermManage(player, "ancientgates.setto.bungee")) {
				response = "You lack the permissions to Set \"to\" to your location.";
				// Check gate exists
			} else if (!Gate.exists(gateid)) {
				response = "There exists no gate with id \"" + gateid + "\" on server \"" + fromserver + "\"";

				// Get gate
			} else {
				final Gate gate = Gate.get(gateid);

				// Set gate location
				if (gate.getBungeeTos() == null || gate.getBungeeTos().size() <= 1) {
					gate.addTo(null);
					gate.addBungeeTo(null, null); // Wipe previous bungeeto
					gate.addBungeeTo(server, location);
					response = "To location for gate \"" + gateid + "\" on server \"" + fromserver + "\" is now where you stand.";
					Gate.save();

					// Display multiple tos exist response
				} else {
					response = "This gate has multiple to locations. Use:\n";
					response += new CommandRemTo().getUsageTemplate(false, true);
				}
			}

			// Build the packet, format is <message>
			final String[] args = { response };
			final Packet packet = new Packet("sendmsg", "setto", args);

			// Sent to querying server
			Plugin.serv.sendToClient(event.getID(), packet);
			Plugin.serv.removeClient(event.getID());

			// Parse "addto" command
		} else if (command.toLowerCase().equals("addto")) {
			// Data should be player, server, id and command data
			final String[] parts = event.getArguments();
			final String player = parts[0];
			final String server = parts[1];
			final String gateid = parts[2];
			final String location = parts[3];
			final String fromserver = parts[4];

			// Message response
			String response;

			// Check player has permission
			if (!Plugin.hasPermManage(player, "ancientgates.addto.bungee")) {
				response = "You lack the permissions to Add a \"to\" to your location.";
				// Check gate exists
			} else if (!Gate.exists(gateid)) {
				response = "There exists no gate with id \"" + gateid + "\" on server \"" + fromserver + "\"";

				// Get gate
			} else {
				final Gate gate = Gate.get(gateid);

				// Add gate location
				if (gate.getBungeeTos() != null && gate.getBungeeTos().size() >= 1) {
					gate.addTo(null);
					gate.addBungeeTo(server, location);
					response = "Another \"to\" location for gate \"" + gateid + "\" on server \"" + fromserver + "\" is now where you stand.";
					Gate.save();

					// Display multiple tos required response
				} else {
					response = "This gate needs an initial \"to\" location. Use:\n";
					response += new CommandSetTo().getUsageTemplate(false, true);
				}
			}

			// Build the packet, format is <message>
			final String[] args = { response };
			final Packet packet = new Packet("sendmsg", "addto", args);

			// Sent to querying server
			Plugin.serv.sendToClient(event.getID(), packet);
			Plugin.serv.removeClient(event.getID());

			// Parse "remto" command
		} else if (command.toLowerCase().equals("remto")) {
			// Data should be player, server, id and command data
			final String[] parts = event.getArguments();
			final String player = parts[0];
			final String server = parts[1];
			final String gateid = parts[2];
			final String location = parts[3];
			final String fromserver = parts[4];

			// Message response
			String response;

			// Check player has permission
			if (!Plugin.hasPermManage(player, "ancientgates.remto.bungee")) {
				response = "You lack the permissions to Remove a \"to\" from your location.";
				// Check gate exists
			} else if (!Gate.exists(gateid)) {
				response = "There exists no gate with id \"" + gateid + "\" on server \"" + fromserver + "\"";

				// Get gate
			} else {
				final Gate gate = Gate.get(gateid);

				// Display no to exists response
				if (gate.getBungeeTos() == null) {
					response = "This gate needs a \"to\" location. Use:\n";
					response += new CommandSetTo().getUsageTemplate(false, true);

					// Remove gate location
				} else {
					final String nearestBungeeTo = GateUtil.nearestBungeeTo(new WorldCoord(location));

					if (nearestBungeeTo.isEmpty()) {
						response = "No nearby \"to\" location for gate \"" + gateid + "\" on server \"" + fromserver + "\".";
					} else {
						gate.delBungeeTo(server, nearestBungeeTo);
						response = "Nearest \"to\" location for gate \"" + gateid + "\" on server \"" + fromserver + "\" is removed.";
						Gate.save();
					}
				}
			}

			// Build the packet, format is <message>
			final String[] args = { response };
			final Packet packet = new Packet("sendmsg", "remto", args);

			// Sent to querying server
			Plugin.serv.sendToClient(event.getID(), packet);
			Plugin.serv.removeClient(event.getID());

			// Parse "spawnentity" command
		} else if (command.toLowerCase().equals("spawnentity")) {
			// Data should be entity id, entity world, entitytype id and destination location
			final String[] parts = event.getArguments();
			final String entityId = parts[0];
			final String entityWorld = parts[1];
			final String entityTypeName = parts[2];
			final String entityTypeData = parts[3];
			final String destination = parts[4];

			// Build the packet, format is <message>
			final String[] args = { entityWorld, entityId };
			final Packet packet = new Packet("removeentity", "spawnentity", args);

			if (EntityUtil.entityType(entityTypeName).isSpawnable() || EntityUtil.isDroppedItem(EntityUtil.entityType(entityTypeName))) {
				// Add entity to spawn queue
				Plugin.bungeeCordEntityInQueue.add(new BungeeQueue(entityTypeName, entityTypeData, destination));

				// Schedule synchronous task to process spawn queue
				Plugin.instance.getServer().getScheduler().scheduleSyncDelayedTask(Plugin.instance, new Runnable() {
					@Override
					public void run() {
						TeleportUtil.teleportEntity();
					}
				});

				// Send entity removal command back to client
				Plugin.serv.sendToClient(event.getID(), packet);
				Plugin.serv.removeClient(event.getID());
			}
			// Parse "spawnvehicle" command
		} else if (command.toLowerCase().equals("spawnvehicle")) {
			// Data should be vehicle id, vehicle world, vehicletype id, velocity and destination
			// location
			// [additionally entity id, entitytype id and entitytype data]
			final String[] parts = event.getArguments();
			final String vehicleId = parts[0];
			final String vehicleWorld = parts[1];
			final String vehicleTypeName = parts[2];
			final double velocity = Double.parseDouble(parts[3]);
			final String destination = parts[4];

			// Initialise spawn queue
			BungeeQueue queue;

			// Parse passenger info
			String[] args = null;
			if (parts[6] != null) {
				final String entityId = parts[5];
				final String entityTypeName = parts[6];
				final String entityTypeData = parts[7];

				// Build spawn queue (incl. passenger info)
				queue = new BungeeQueue(vehicleTypeName, velocity, destination, entityTypeName, entityTypeData);

				// Build the packet, format is <message>
				args = new String[] { vehicleWorld, vehicleId, entityId };
				// Parse contents info
			} else if (parts[5] != null) {
				final String entityItemStack = parts[5];

				// Build spawn queue (incl. contents info)
				queue = new BungeeQueue(vehicleTypeName, velocity, destination, entityItemStack);

				// Build the packet, format is <message>
				args = new String[] { vehicleWorld, vehicleId };
			} else {
				// Build spawn queue
				queue = new BungeeQueue(vehicleTypeName, velocity, destination);

				// Build the packet, format is <message>
				args = new String[] { vehicleWorld, vehicleId };
			}
			final Packet packet = new Packet("removevehicle", "spawnvehicle", args);

			// Add entity to spawn queue
			Plugin.bungeeCordVehicleInQueue.add(queue);

			// Schedule synchronous task to process spawn queue
			Plugin.instance.getServer().getScheduler().scheduleSyncDelayedTask(Plugin.instance, new Runnable() {
				@Override
				public void run() {
					TeleportUtil.teleportVehicle();
				}
			});

			// Send entity removal command back to client
			Plugin.serv.sendToClient(event.getID(), packet);
			Plugin.serv.removeClient(event.getID());
			// Parse "ping" command
		} else if (command.toLowerCase().equals("ping")) {
			final Packet packet = new Packet("pong", "ping", new String[] {});

			// Send pong command back to client
			Plugin.serv.sendToClient(event.getID(), packet);
			Plugin.serv.removeClient(event.getID());
		}
	}

	@Override
	public void onClientConnect(final ClientConnectionEvent event) {
		if (Conf.debug)
			Plugin.log("Socket client connected " + event.getClientID() + ".");
	}

	@Override
	public void onClientDisconnect(final ClientConnectionEvent event) {
		if (Conf.debug)
			Plugin.log("Socket client disconnected " + event.getClientID() + ".");
	}

}
