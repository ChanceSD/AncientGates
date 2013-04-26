package org.mcteam.ancientgates.sockets.events;

import java.net.Socket;

import org.mcteam.ancientgates.sockets.ConnectionState;

public class ClientConnectionEvent {
	
	private Socket socket;
	private ConnectionState state;
	private int ID;
	
	public ClientConnectionEvent(Socket _socket, int clientID, ConnectionState _state) {
		this.socket = _socket;
		this.state = _state;
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
