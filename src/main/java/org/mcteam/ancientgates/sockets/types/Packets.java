package org.mcteam.ancientgates.sockets.types;

public class Packets {
	public Packet[] packets;

	public Packets() {
	}

	public Packets(final Packet[] packets) {
		this.packets = packets;
	}

	public Packets(final Packet packet) {
		this.packets = new Packet[] { packet };
	}

}
