package org.mcteam.ancientgates.sockets.events;

import java.net.Socket;

import org.mcteam.ancientgates.sockets.types.Packet;

import com.google.gson.Gson;

public class ClientRecieveEvent {

	private final int id;
	private final Socket client;
	private final Packet packetData;

	public ClientRecieveEvent(final int id, final Socket client, final Packet packet) {
		this.id = id;
		this.client = client;
		this.packetData = packet;
	}

	public int getID() {
		return this.id;
	}

	public Socket getClientSocket() {
		return this.client;
	}

	public String getCommand() {
		return this.packetData.command;
	}

	public String getOriginalCommand() {
		return this.packetData.responseCommand;
	}

	public String[] getArguments() {
		return this.packetData.args;
	}

	public String getRawData() {
		final Gson gson = new Gson();
		return gson.toJson(this.packetData, Packet.class);
	}

}
