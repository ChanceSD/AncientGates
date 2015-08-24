package org.mcteam.ancientgates.util.types;

public enum BungeeChannel {
	// AncientGates player teleport
	AGBungeeTele("AGBungeeTele"),

	// AncientGates entity spawn
	AGBungeeSpawn("AGBungeeSpawn"),

	// AncientGates vehicle teleport
	AGBungeeVehicleTele("AGBungeeVehicleTele"),

	// AncientGates vehicle spawn
	AGBungeeVehicleSpawn("AGBungeeVehicleSpawn"),

	// AncientGates bungee communication
	AGBungeeCom("AGBungeeCom");

	protected final String channelName;

	@Override
	public String toString() {
		return this.channelName;
	}

	private BungeeChannel(final String channelName) {
		this.channelName = channelName;
	}

}
