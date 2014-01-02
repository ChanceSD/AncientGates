package org.mcteam.ancientgates.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public class BlockUtil {

	public static HashSet<Material> standableGateMaterials;
	public static Map<Material, Boolean> standableMaterials;
	
	static {
		standableGateMaterials = new HashSet<Material>();
		standableGateMaterials.add(Material.PISTON_MOVING_PIECE);
		standableGateMaterials.add(Material.ENDER_PORTAL);
		standableGateMaterials.add(Material.LAVA);
		standableGateMaterials.add(Material.PORTAL);
		standableGateMaterials.add(Material.STATIONARY_LAVA);
		standableGateMaterials.add(Material.STATIONARY_WATER);
		standableGateMaterials.add(Material.SUGAR_CANE_BLOCK);
		standableGateMaterials.add(Material.WATER);
		standableGateMaterials.add(Material.WEB);
	}
	
	static {
		standableMaterials = new HashMap<Material, Boolean>();
		try {
			standableMaterials.put(Material.AIR, true); // 0 Air
			standableMaterials.put(Material.SAPLING, true); // 6 Saplings
			standableMaterials.put(Material.WATER, true); // 8 Water
			standableMaterials.put(Material.STATIONARY_WATER, true); // 9 Stationary water
			standableMaterials.put(Material.LAVA, true); // 10 Lava
			standableMaterials.put(Material.STATIONARY_LAVA, true); // 11 Stationary lava
			standableMaterials.put(Material.POWERED_RAIL, true); // 27 Powered Rail
			standableMaterials.put(Material.DETECTOR_RAIL, true); // 28 Detector Rail
			standableMaterials.put(Material.WEB, true); // 30 Cobweb
			standableMaterials.put(Material.LONG_GRASS, true); // 31 Tall Grass
			standableMaterials.put(Material.DEAD_BUSH, true); // 32 Dead Bush
			standableMaterials.put(Material.PISTON_MOVING_PIECE, true); // 36 Piston (Moving)
			standableMaterials.put(Material.YELLOW_FLOWER, true); // 37 Dandelion
			standableMaterials.put(Material.RED_ROSE, true); // 38 Rose
			standableMaterials.put(Material.BROWN_MUSHROOM, true); // 39 Brown Mushroom
			standableMaterials.put(Material.RED_MUSHROOM, true); // 40 Red Mushroom
			standableMaterials.put(Material.STEP, false); // 44 Stone Slab
			standableMaterials.put(Material.TORCH, true); // 50 Torch
			standableMaterials.put(Material.FIRE, true); // 51 Fire
			standableMaterials.put(Material.REDSTONE_WIRE, true); // 55 Redstone Wire
			standableMaterials.put(Material.CROPS, true); // 59 Wheat Seeds
			standableMaterials.put(Material.SIGN_POST, true); // 63 Sign Post
			standableMaterials.put(Material.LADDER, true); // 65 Ladders
			standableMaterials.put(Material.RAILS, true); // 66 Rails
			standableMaterials.put(Material.WALL_SIGN, true); // 68 Wall Sign
			standableMaterials.put(Material.LEVER, true); // 69 Lever
			standableMaterials.put(Material.STONE_PLATE, true); // 70 Stone Pressure Plate
			standableMaterials.put(Material.WOOD_PLATE, true); // 72 Wooden Pressure Plate
			standableMaterials.put(Material.REDSTONE_TORCH_OFF, true); // 75 Redstone Torch (Off)
			standableMaterials.put(Material.REDSTONE_TORCH_ON, true); // 76 Redstone Torch (On)
			standableMaterials.put(Material.STONE_BUTTON, true); // 77 Stone Button
			standableMaterials.put(Material.SNOW, true); // 78 Snow
			standableMaterials.put(Material.SUGAR_CANE_BLOCK, true); // 83 Sugar Cane
			standableMaterials.put(Material.PORTAL, true); // 90 Portal
			standableMaterials.put(Material.DIODE_BLOCK_OFF, true); // 93 Redstone Repeater (Off)
			standableMaterials.put(Material.DIODE_BLOCK_ON, true); // 94 Redstone Repeater (On)
			standableMaterials.put(Material.VINE, true); // 106 Vines
			standableMaterials.put(Material.WATER_LILY, true); // 111 Lily Pad
			standableMaterials.put(Material.ENDER_PORTAL, true); // 119 End Portal
			standableMaterials.put(Material.WOOD_STEP, false); // 126 Wooden Slab
			standableMaterials.put(Material.TRIPWIRE_HOOK, true); // 131 Tripwire Hook
			standableMaterials.put(Material.TRIPWIRE, true); // 132 Tripwire
			standableMaterials.put(Material.FLOWER_POT, true); // 140 Flower Pot
			standableMaterials.put(Material.CARROT, true); // 141 Carrot
			standableMaterials.put(Material.POTATO, true); // 142 Potatoes	
			standableMaterials.put(Material.GOLD_PLATE, true); // 147 Gold Pressure Plate
			standableMaterials.put(Material.IRON_PLATE, true); // 148 Iron Pressure Plate
			standableMaterials.put(Material.REDSTONE_COMPARATOR_OFF, true); // 149 Redstone Comparator (Off)
			standableMaterials.put(Material.REDSTONE_COMPARATOR_ON, true); // 150 Redstone Comparator (On)	
			standableMaterials.put(Material.DAYLIGHT_DETECTOR, false); // 151 Daylight Sensor
			standableMaterials.put(Material.ACTIVATOR_RAIL, true); // 157 Activator Rail
			standableMaterials.put(Material.CARPET, true); // 171 Carpet
			standableMaterials.put(Material.DOUBLE_PLANT, true); // double_plant Double Plants
		} catch (NoSuchFieldError e) {} // Support previous MC versions
	}
	
	public static boolean isPortalGateMaterial(Material material) {
		return material.equals(Material.PORTAL) || material.equals(Material.ENDER_PORTAL);
	}
	
	public static boolean isStandableGateMaterial(Material material) {
		return standableGateMaterials.contains(material);
	}
	
	public static boolean canPlayerStandInGateBlock(Block block, Boolean fullHeight) {
		if (fullHeight) {
			return isStandableGateMaterial(block.getType()) && isStandableGateMaterial(block.getRelative(BlockFace.UP).getType());
		} else {
			return isStandableGateMaterial(block.getType());
		}
	}
	
	public static boolean isStandableMaterial(Material material) {
		return standableMaterials.containsKey(material);
	}
	
	public static boolean canPassThroughMaterial(Material material) {
		return (standableMaterials.get(material) == null) ? false : standableMaterials.get(material);
	}

}