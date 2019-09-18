package org.mcteam.ancientgates.util.types;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.mcteam.ancientgates.util.XMaterial;

public enum GateMaterial {
	// AIR
	AIR("pseudo air blocks", XMaterial.MOVING_PISTON.parseMaterial()),

	// LAVA
	LAVA("stationary lava blocks", XMaterial.LAVA.parseMaterial()),

	// NETHER/ENDER PORTAL
	PORTAL("nether/ender portal blocks", XMaterial.NETHER_PORTAL.parseMaterial()),

	// SUGARCANE
	SUGARCANE("sugarcane blocks", XMaterial.SUGAR_CANE.parseMaterial()),

	// WATER
	WATER("stationary water blocks", XMaterial.WATER.parseMaterial()),

	// WEB
	WEB("spiders web blocks", XMaterial.COBWEB.parseMaterial());

	private static final Map<String, GateMaterial> nameToMaterial = new HashMap<>();

	static {
		for (final GateMaterial value : EnumSet.allOf(GateMaterial.class)) {
			nameToMaterial.put(value.name(), value);
		}
	}

	public static GateMaterial fromName(final String name) {
		return nameToMaterial.get(name);
	}

	public static final String[] names = new String[values().length];

	static {
		final GateMaterial[] values = values();
		for (int i = 0; i < values.length; i++) {
			names[i] = values[i].name();
		}
	}

	protected final String desc;

	public String getDesc() {
		return this.desc;
	}

	protected final Material material;

	public Material getMaterial() {
		return this.material;
	}

	private GateMaterial(final String desc, final Material material) {
		this.desc = desc;
		this.material = material;
	}

}
