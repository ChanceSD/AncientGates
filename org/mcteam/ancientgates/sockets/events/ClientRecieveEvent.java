package org.mcteam.ancientgates.sockets.events;

import java.net.Socket;

import org.bukkit.craftbukkit.libs.com.google.gson.Gson;
import org.mcteam.ancientgates.sockets.packets.Packet;

public class ClientRecieveEvent {
	
	private int id;
	private Socket client;
	private Packet packetData;
	
	public ClientRecieveEvent(int _id, Socket _client, Packet _packet) {
		this.id = _id;
		this.client = _client;
		this.packetData = _packet;
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
		Gson gson = new Gson();
		return gson.toJson(this.packetData, Packet.class);
	}
	
}
