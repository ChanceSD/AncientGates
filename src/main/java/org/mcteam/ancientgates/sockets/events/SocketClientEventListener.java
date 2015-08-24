package org.mcteam.ancientgates.sockets.events;

import java.util.EventListener;

import org.mcteam.ancientgates.sockets.SocketClient;
import org.mcteam.ancientgates.sockets.types.Packets;

public interface SocketClientEventListener extends EventListener {
	
	public void onServerMessageRecieve(SocketClient client, Packets packets);
	public void onServerMessageError();
	
}