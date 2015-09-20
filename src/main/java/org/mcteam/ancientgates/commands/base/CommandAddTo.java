package org.mcteam.ancientgates.commands.base;

import org.mcteam.ancientgates.Conf;
import org.mcteam.ancientgates.Gate;
import org.mcteam.ancientgates.Plugin;
import org.mcteam.ancientgates.Server;
import org.mcteam.ancientgates.commands.BaseCommand;
import org.mcteam.ancientgates.sockets.SocketClient;
import org.mcteam.ancientgates.sockets.events.SocketClientEventListener;
import org.mcteam.ancientgates.sockets.types.Packet;
import org.mcteam.ancientgates.sockets.types.Packets;
import org.mcteam.ancientgates.util.TeleportUtil;
import org.mcteam.ancientgates.util.types.PluginMessage;

public class CommandAddTo extends BaseCommand {

	public CommandAddTo() {
		aliases.add("addto");

		requiredParameters.add("id");

		requiredPermission = "ancientgates.addto";

		helpDescription = "Add another \"to\" to your location";
	}

	@Override
	public void perform() {
		// Check if optional parameter exists
		String serverName = null;
		if (parameters.size() > 1) {
			serverName = parameters.get(1);
		}

		// Local 'addto' command
		if (serverName == null || !Conf.bungeeCordSupport) {
			if (gate.getTos() == null) {
				sendMessage("This gate needs an initial \"to\" location. Use:");
				sendMessage(new CommandSetTo().getUsageTemplate(true, true));
				return;
			}
			if (gate.getTos().size() < 1) {
				sendMessage("This gate needs an initial \"to\" location. Use:");
				sendMessage(new CommandSetTo().getUsageTemplate(true, true));
				return;
			}

			gate.addTo(player.getLocation());
			gate.addBungeeTo(null, null);
			sendMessage("Another \"to\" location for gate \"" + gate.getId() + "\" is now where you stand.");
			Gate.save();

			// External BungeeCord 'addto' command
		} else {
			// Check bungeeServerName found
			if (Plugin.bungeeServerName == null) {
				sendMessage("Still connecting to BungeeCord. Try again.");
				return;
			}

			// Send command packet via BungeeCord
			if (!Conf.useSocketComms || Plugin.serv == null) {
				// Build the message, format is <command>#@#<player>#@#<server>#@#<gateid>#@#<data>
				final String[] args = new String[] { parameters.get(0), TeleportUtil.locationToString(player.getLocation()), Plugin.bungeeServerName };
				final PluginMessage msg = new PluginMessage("addto", player, serverName, args);

				// Send over the AGBungeeCom BungeeCord channel
				player.sendPluginMessage(Plugin.instance, "BungeeCord", msg.toByteArray());
				sendMessage("Another \"to\" location for gate \"" + parameters.get(0) + "\" on server \"" + serverName + "\" has been sent.");

				// Send command packet via client socket
			} else {
				// Check server exists
				if (!Server.exists(serverName)) {
					sendMessage("The server \"" + serverName + "\" does not exist.");
					return;
				}

				// Get server
				final Server server1 = Server.get(serverName);

				// Build the packet, format is <player>,<server>,<gateid>,<data>,<fromserver>
				final String[] args = new String[] { player.getName(), Plugin.bungeeServerName, parameters.get(0), TeleportUtil.locationToString(player.getLocation()), serverName };
				final Packet packet = new Packet("addto", args);

				// Setup socket client and listener
				final SocketClient client = new SocketClient(server1.getAddress(), server1.getPort(), server1.getPassword());
				client.setListener(new SocketClientEventListener() {
					@Override
					public void onServerMessageRecieve(final SocketClient client1, final Packets packets) {
						for (final Packet packet1 : packets.packets) {
							if (packet1.command.toLowerCase().equals("sendmsg")) {
								sendMessage(packet1.args[0]);
							}
						}
						client1.close();
					}

					@Override
					public void onServerMessageError() {
						sendMessage("Could not connect to server \"" + server1.getName() + "\".");
						Plugin.log("There was an error connection to the server.");
					}
				});

				// Connect and send packet
				try {
					client.connect();
					client.send(packet);
				} catch (final Exception e) {
					sendMessage("Could not connect to server \"" + serverName + "\".");
					Plugin.log("There was an error connection to the server.");
				}
			}
		}

	}

}
