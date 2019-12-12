package org.mcteam.ancientgates.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.mcteam.ancientgates.util.types.GateMaterial;

public class BlockUtil {

	public static Set<Material> standableGateMaterials;
	public static Map<Material, Boolean> standableMaterials;

	static {
		standableGateMaterials = new HashSet<>();
		for (final GateMaterial gateMat : GateMaterial.values()) {
			standableGateMaterials.add(gateMat.getMaterial());
		}
		// TODO possibly add this one to GateMaterial?
		standableGateMaterials.add(XMaterial.END_PORTAL.parseMaterial());
	}

	static {
		standableMaterials = new HashMap<>();
		try {
			standableMaterials.put(Material.AIR, true); // 0 Air
			standableMaterials.put(XMaterial.OAK_SAPLING.parseMaterial(), true); // 6 Saplings
			standableMaterials.put(Material.WATER, true); // 8 Water
			standableMaterials.put(GateMaterial.WATER.getMaterial(), true); // 9 Stationary water
			standableMaterials.put(Material.LAVA, true); // 10 Lava
			standableMaterials.put(GateMaterial.LAVA.getMaterial(), true); // 11 Stationary lava
			standableMaterials.put(Material.POWERED_RAIL, true); // 27 Powered Rail
			standableMaterials.put(Material.DETECTOR_RAIL, true); // 28 Detector Rail
			standableMaterials.put(XMaterial.COBWEB.parseMaterial(), true); // 30 Cobweb
			standableMaterials.put(XMaterial.FERN.parseMaterial(), true); // 31 Tall Grass
			standableMaterials.put(Material.DEAD_BUSH, true); // 32 Dead Bush
			standableMaterials.put(XMaterial.MOVING_PISTON.parseMaterial(), true); // 36 Piston (Moving)
			standableMaterials.put(XMaterial.DANDELION.parseMaterial(), true); // 37 Dandelion
			standableMaterials.put(XMaterial.POPPY.parseMaterial(), true); // 38 Rose
			standableMaterials.put(Material.BROWN_MUSHROOM, true); // 39 Brown Mushroom
			standableMaterials.put(Material.RED_MUSHROOM, true); // 40 Red Mushroom
			standableMaterials.put(XMaterial.STONE_SLAB.parseMaterial(), false); // 44 Stone Slab
			standableMaterials.put(Material.TORCH, true); // 50 Torch
			standableMaterials.put(Material.FIRE, true); // 51 Fire
			standableMaterials.put(Material.REDSTONE_WIRE, true); // 55 Redstone Wire
			standableMaterials.put(XMaterial.WHEAT.parseMaterial(), true); // 59 Wheat Seeds
			standableMaterials.put(XMaterial.OAK_SIGN.parseMaterial(), true); // 63 Sign Post
			standableMaterials.put(Material.LADDER, true); // 65 Ladders
			standableMaterials.put(XMaterial.RAIL.parseMaterial(), true); // 66 Rails
			standableMaterials.put(XMaterial.OAK_WALL_SIGN.parseMaterial(), true); // 68 Wall Sign
			standableMaterials.put(Material.LEVER, true); // 69 Lever
			standableMaterials.put(XMaterial.STONE_PRESSURE_PLATE.parseMaterial(), true); // 70 Stone Pressure Plate
			standableMaterials.put(XMaterial.OAK_PRESSURE_PLATE.parseMaterial(), true); // 72 Wooden Pressure Plate
			standableMaterials.put(XMaterial.REDSTONE_TORCH.parseMaterial(), true); // 75 Redstone Torch (Off)
			// standableMaterials.put(Material.REDSTONE_TORCH_ON, true); // 76 Redstone Torch (On)
			standableMaterials.put(Material.STONE_BUTTON, true); // 77 Stone Button
			standableMaterials.put(Material.SNOW, true); // 78 Snow
			standableMaterials.put(XMaterial.SUGAR_CANE.parseMaterial(), true); // 83 Sugar Cane
			standableMaterials.put(XMaterial.NETHER_PORTAL.parseMaterial(), true); // 90 Portal
			standableMaterials.put(XMaterial.REPEATER.parseMaterial(), true); // 93 Redstone Repeater (Off)
			// standableMaterials.put(Material.DIODE_BLOCK_ON, true); // 94 Redstone Repeater (On)
			standableMaterials.put(Material.VINE, true); // 106 Vines
			standableMaterials.put(XMaterial.LILY_PAD.parseMaterial(), true); // 111 Lily Pad
			standableMaterials.put(XMaterial.END_PORTAL.parseMaterial(), true); // 119 End Portal
			standableMaterials.put(XMaterial.OAK_SLAB.parseMaterial(), false); // 126 Wooden Slab
			standableMaterials.put(Material.TRIPWIRE_HOOK, true); // 131 Tripwire Hook
			standableMaterials.put(Material.TRIPWIRE, true); // 132 Tripwire
			standableMaterials.put(Material.FLOWER_POT, true); // 140 Flower Pot
			standableMaterials.put(Material.CARROT, true); // 141 Carrot
			standableMaterials.put(Material.POTATO, true); // 142 Potatoes
			standableMaterials.put(XMaterial.LIGHT_WEIGHTED_PRESSURE_PLATE.parseMaterial(), true); // 147 Gold Pressure Plate
			standableMaterials.put(XMaterial.HEAVY_WEIGHTED_PRESSURE_PLATE.parseMaterial(), true); // 148 Iron Pressure Plate
			standableMaterials.put(XMaterial.COMPARATOR.parseMaterial(), true); // 149 Redstone Comparator (Off)
			// standableMaterials.put(Material.REDSTONE_COMPARATOR_ON, true); // 150 Redstone
			// Comparator (On)
			standableMaterials.put(Material.DAYLIGHT_DETECTOR, false); // 151 Daylight Sensor
			standableMaterials.put(Material.ACTIVATOR_RAIL, true); // 157 Activator Rail
			standableMaterials.put(XMaterial.WHITE_CARPET.parseMaterial(), true); // 171 Carpet
			standableMaterials.put(XMaterial.LARGE_FERN.parseMaterial(), true); // double_plant Double Plants
		} catch (final NoSuchFieldError e) {
		} // Support previous MC versions
	}

	public static boolean isPortalGateMaterial(final Material material) {
		return material.equals(XMaterial.NETHER_PORTAL.parseMaterial()) || material.equals(XMaterial.END_PORTAL.parseMaterial());
	}

	public static boolean isStandableGateMaterial(final Material material) {
		return standableGateMaterials.contains(material);
	}

	public static boolean canPlayerStandInGateBlock(final Block block, final Boolean fullHeight) {
		if (fullHeight)
			return isStandableGateMaterial(block.getType()) && isStandableGateMaterial(block.getRelative(BlockFace.UP).getType());
		return isStandableGateMaterial(block.getType());
	}

	public static boolean isStandableMaterial(final Material material) {
		return standableMaterials.containsKey(material);
	}

	public static boolean canPassThroughMaterial(final Material material) {
		return standableMaterials.get(material) == null ? false : standableMaterials.get(material);
	}

}
