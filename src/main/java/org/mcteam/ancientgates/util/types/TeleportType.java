package org.mcteam.ancientgates.util.types;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum TeleportType {
	// Location based teleport
	LOCATION,

	// World based teleport
	// WORLD,

	// Server based teleport
	SERVER;

	private static final Map<String, TeleportType> nameToTpType = new HashMap<>();

	static {
		for (final TeleportType value : EnumSet.allOf(TeleportType.class)) {
			nameToTpType.put(value.name(), value);
		}
	}

	public static TeleportType fromName(final String name) {
		return nameToTpType.get(name);
	}

	public static final String[] names = new String[values().length];

	static {
		final TeleportType[] values = values();
		for (int i = 0; i < values.length; i++)
			names[i] = values[i].name();
	}

}
