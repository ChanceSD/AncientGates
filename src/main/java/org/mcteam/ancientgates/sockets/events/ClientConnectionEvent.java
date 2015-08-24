package org.mcteam.ancientgates.sockets.events;

import java.net.Socket;

import org.mcteam.ancientgates.sockets.types.ConnectionState;

public class ClientConnectionEvent {

	private final Socket socket;
	private final ConnectionState state;
	private final int ID;

	public ClientConnectionEvent(final Socket socket, final int clientID, final ConnectionState state) {
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
