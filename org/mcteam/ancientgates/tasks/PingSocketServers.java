package org.mcteam.ancientgates.tasks;

import org.bukkit.scheduler.BukkitRunnable;

import org.mcteam.ancientgates.Conf;
import org.mcteam.ancientgates.Server;
import org.mcteam.ancientgates.sockets.SocketClient;
import org.mcteam.ancientgates.sockets.events.SocketClientEventListener;
import org.mcteam.ancientgates.sockets.types.ConnectionState;
import org.mcteam.ancientgates.sockets.types.Packet;
import org.mcteam.ancientgates.sockets.types.Packets;

public class PingSocketServers extends BukkitRunnable {
 
	public PingSocketServers() {
	}
 
	@Override
	public void run() {
		if (!Conf.bungeeCordSupport || !Conf.useSocketComms) return;
	    
		// Ping all socket comms servers
		for (final Server server : Server.getAll()) {
			Packet packet = new Packet("ping", new String[] {});
		
			// Setup socket client and listener
			SocketClient client = new SocketClient(server.getAddress(), server.getPort(), server.getPassword());
			client.setListener(new SocketClientEventListener() {
				public void onServerMessageRecieve(SocketClient client, Packets packets) {
					for (Packet packet : packets.packets) {
						if (packet.command.toLowerCase().equals("pong")) {
							// Set state as connected
							server.setState(ConnectionState.CONNECTED);
						}
					}
					client.close();
				}
				public void onServerMessageError() {
					// Set state as disconnected
					server.setState(ConnectionState.DISCONNECTED);
				}
			});
		
			// Connect and send packet
			try {
				client.connect();
				client.send(packet);
			} catch (Exception e) {
				// Set state as disconnected
				server.setState(ConnectionState.DISCONNECTED);
			}
		}
	}
	
}