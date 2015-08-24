package org.mcteam.ancientgates.commands.sockets;

import org.mcteam.ancientgates.Plugin;
import org.mcteam.ancientgates.Server;
import org.mcteam.ancientgates.commands.BaseCommand;
import org.mcteam.ancientgates.sockets.SocketClient;
import org.mcteam.ancientgates.sockets.events.SocketClientEventListener;
import org.mcteam.ancientgates.sockets.types.Packet;
import org.mcteam.ancientgates.sockets.types.Packets;
import org.mcteam.ancientgates.tasks.BungeeServerList;

public class CommandAddServer extends BaseCommand {
	
	public CommandAddServer() {           
		aliases.add("addserver");
		
		requiredParameters.add("name");
		requiredParameters.add("address:port");
		requiredParameters.add("pass");
		
		requiredPermission = "ancientgates.addserver";
		
		senderMustBePlayer = false;
		hasServerParam = false;
		hasGateParam = false;
		
		helpDescription = "Add a server";
	}
	
	public void perform() {
		// Grab new BungeeCord server list
		new BungeeServerList(Plugin.instance).run();
		
		// Check bungeeServerList found
		if (Plugin.bungeeServerList == null) {
			sendMessage("Still connecting to BungeeCord. Try again.");
			return;
		}
		
		// Parse command parameters
		final String name = parameters.get(0);
		final String password = parameters.get(2);
		
		if (!parameters.get(1).contains(":")) {
			sendMessage("Incorrect address format. Use [ip]:[port].");
			return;
		}
		
		String parts[] = parameters.get(1).split(":");
		final String address = parts[0];
		final int port;
		try {
			port = Integer.parseInt(parts[1]);
		} catch(NumberFormatException e) {
			sendMessage("The port must be a number.");
			return;
		}
		
		if (Server.exists(name)) {
			sendMessage("The server \"" + name + "\" has already been added.");
			return;
		}
		
		if (!Plugin.bungeeServerList.contains(name)) {
			sendMessage("The server \"" + name + "\" does not exist in BungeeCord.");
			return;
		}
		
		// Ping the server
		Packet packet = new Packet("ping", new String[] {});
		
		// Setup socket client and listener
		SocketClient client = new SocketClient(address, port, password);
		client.setListener(new SocketClientEventListener() {
		    public void onServerMessageRecieve(SocketClient client, Packets packets) {
		    	for (Packet packet : packets.packets) {
		    		if (packet.command.toLowerCase().equals("pong")) {
		    			// Add server on valid ping response
		    			Server.add(name, address, port, password);
		    			sendMessage("The server \"" + name + "\" was added, with address \"" + address + ":" + port + "\".");
		    			// Save to disc
		    			Server.save();	
		    		}
		    	}
		    	client.close();
		    }
		    public void onServerMessageError() {
		    	sendMessage("Could not connect to server. Check port and password.");
		    }
		});
		
		// Connect and send packet
		try {
			client.connect();
			client.send(packet);
		} catch (Exception e) {
			sendMessage("Could not find server on \"" + address + ":" + port + "\". Try again.");
		}
	}
        
}