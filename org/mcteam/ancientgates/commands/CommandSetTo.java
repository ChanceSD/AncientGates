package org.mcteam.ancientgates.commands;

import org.mcteam.ancientgates.Conf;
import org.mcteam.ancientgates.Gate;
import org.mcteam.ancientgates.Plugin;
import org.mcteam.ancientgates.Server;
import org.mcteam.ancientgates.sockets.SocketClient;
import org.mcteam.ancientgates.sockets.events.SocketClientEventListener;
import org.mcteam.ancientgates.sockets.types.Packet;
import org.mcteam.ancientgates.sockets.types.Packets;
import org.mcteam.ancientgates.util.TeleportUtil;
import org.mcteam.ancientgates.util.types.PluginMessage;

public class CommandSetTo extends BaseCommand {
	
	public CommandSetTo() {
		aliases.add("setto");
		
		requiredParameters.add("id");
		requiredPermission = "ancientgates.setto.local";
		
		if (Conf.bungeeCordSupport) {
			optionalParameters.add("server");
		}
		
		helpDescription = "Set \"to\" to your location.";
	}
	
	public void perform() {
		// Check if optional parameter exists
		String serverName = null;
		if (parameters.size() > 1) {
			serverName = parameters.get(1);
		}
		
		// Local 'setto' command
		if (serverName == null || !Conf.bungeeCordSupport) {
			gate.setTo(player.getLocation());
			gate.setBungeeTo(null, null);
			sendMessage("To location for gate \""+gate.getId()+"\" is now where you stand.");
			Gate.save();
			
		// External BungeeCord 'setto' command
		} else {
			// Send command packet via BungeeCord
			if (!Conf.useSocketComms || Plugin.serv == null) {
				// Build the message, format is <command>#@#<player>#@#<server>#@#<gateid>#@#<data>
				String[] args = new String[] {parameters.get(0), TeleportUtil.locationToString(player.getLocation()), serverName};
				PluginMessage msg = new PluginMessage("setto", player, serverName, args);
				
				// Send over the AGBungeeCom BungeeCord channel
				player.sendPluginMessage(Plugin.instance, "BungeeCord", msg.toByteArray());
				sendMessage("To location for gate \""+parameters.get(0)+"\" on server \""+serverName+"\" has been sent.");

			// Send command packet via client socket
			} else {
				// Check server exists
				if (!Server.exists(serverName)) {
					sendMessage("The server \""+serverName+"\" does not exist.");
					return;
				}
				
				// Get server
				Server server = Server.get(serverName);
				
				// Build the packet, format is <player>,<server>,<gateid>,<data>,<fromserver>
				String[] args = new String[] {player.getName(), Plugin.bungeeServerName, parameters.get(0), TeleportUtil.locationToString(player.getLocation()), serverName};
				Packet packet = new Packet("setto", args);
				
				// Setup socket client and listener
				SocketClient client = new SocketClient(server.getAddress(), server.getPort(), server.getPassword());
				client.setListener(new SocketClientEventListener() {
				    public void onServerMessageRecieve(SocketClient client, Packets packets) {
				    	for (Packet packet : packets.packets) {
				    		if (packet.command.toLowerCase().equals("sendmsg")) {
				    			player.sendMessage(Conf.colorSystem+packet.args[0]);
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
					sendMessage("Could not connect to server \""+serverName+"\".");
					Plugin.log("There was an error connection to the server.");
				}
			}
		}
		
	}
        
}

