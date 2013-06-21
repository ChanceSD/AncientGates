package org.mcteam.ancientgates.sockets.types;

public class Packets
{
	public Packet[] packets;
	
	public Packets() {
	}
	
	public Packets(Packet[] packets) {
		this.packets = packets;
	}
	
	public Packets(Packet packet) {
		this.packets = new Packet[] { packet };
	}
	
}
