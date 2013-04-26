package org.mcteam.ancientgates.sockets.packets;

public class Packet {
	
	public String command;
	public String responseCommand;
	public String[] args;
	
	public Packet() {
	}
	
	public Packet(String _command, String _responseTo, String[] _args) {
		this.command = _command;
		this.responseCommand = _responseTo;
		this.args = _args;
	}
	
}
