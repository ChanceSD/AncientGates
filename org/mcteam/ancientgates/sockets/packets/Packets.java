package org.mcteam.ancientgates.sockets.packets;

public class Packets
{
	public Packet[] packets;
	
	public Packets() {
	}
	
	public Packets(Packet[] _packets) {
		this.packets = _packets;
	}
	
	public Packets(Packet packet) {
		this.packets = new Packet[] { packet };
	}
	
}
