package org.mcteam.ancientgates.util.types;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum CommandType {
	// Console command
	CONSOLE,

	// Player command
	PLAYER;

	private static final Map<String, CommandType> nameToCmdType = new HashMap<>();

	static {
		for (final CommandType value : EnumSet.allOf(CommandType.class)) {
			nameToCmdType.put(value.name(), value);
		}
	}

	public static CommandType fromName(final String name) {
		return nameToCmdType.get(name);
	}

	public static final String[] names = new String[values().length];

	static {
		final CommandType[] values = values();
		for (int i = 0; i < values.length; i++)
			names[i] = values[i].name();
	}

}
