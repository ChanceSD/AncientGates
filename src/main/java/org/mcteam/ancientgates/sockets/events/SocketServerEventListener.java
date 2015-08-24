package org.mcteam.ancientgates.sockets.events;

import java.util.EventListener;

public interface SocketServerEventListener extends EventListener {
	
	public void onClientConnect(ClientConnectionEvent event);
	public void onClientDisconnect(ClientConnectionEvent event);
	public void onClientRecieve(ClientRecieveEvent event);
	
}
