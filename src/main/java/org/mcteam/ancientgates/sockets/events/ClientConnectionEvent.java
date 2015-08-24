package org.mcteam.ancientgates.sockets.events;

import java.net.Socket;

import org.mcteam.ancientgates.sockets.types.ConnectionState;

public class ClientConnectionEvent {
	
	private Socket socket;
	private ConnectionState state;
	private int ID;
	
	public ClientConnectionEvent(Socket socket, int clientID, ConnectionState state) {
		this.socket = socket;
		this.state = state;
		this.ID = clientID;
	}
	
	public Socket getSocket() {
		return this.socket;
	}
	
	public ConnectionState getConnectionState() {
		return this.state;
	}
	
	public int getClientID() {
		return this.ID;
	}
	
}
