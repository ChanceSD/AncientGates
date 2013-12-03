package org.mcteam.ancientgates.util;

import java.util.HashSet;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public class BlockUtil {

	public static HashSet<Material> standableGateMaterials;
	public static HashSet<Material> standableMaterials;
	
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
		standableMaterials = new HashSet<Material>();
		try {
			standableMaterials.add(Material.AIR); // 0 Air
			standableMaterials.add(Material.SAPLING); // 6 Saplings
			standableMaterials.add(Material.WATER); // 8 Water
			standableMaterials.add(Material.STATIONARY_WATER); // 9 Stationary water
			standableMaterials.add(Material.LAVA); // 10 Lava
			standableMaterials.add(Material.STATIONARY_LAVA); // 11 Stationary lava
			standableMaterials.add(Material.POWERED_RAIL); // 27 Powered Rail
			standableMaterials.add(Material.DETECTOR_RAIL); // 28 Detector Rail
			standableMaterials.add(Material.WEB); // 30 Cobweb
			standableMaterials.add(Material.LONG_GRASS); // 31 Tall Grass
			standableMaterials.add(Material.DEAD_BUSH); // 32 Dead Bush
			standableMaterials.add(Material.PISTON_MOVING_PIECE); // 36 Piston (Moving)
			standableMaterials.add(Material.YELLOW_FLOWER); // 37 Dandelion
			standableMaterials.add(Material.RED_ROSE); // 38 Rose
			standableMaterials.add(Material.BROWN_MUSHROOM); // 39 Brown Mushroom
			standableMaterials.add(Material.RED_MUSHROOM); // 40 Red Mushroom
			standableMaterials.add(Material.STEP); // 44 Stone Slab
			standableMaterials.add(Material.TORCH); // 50 Torch
			standableMaterials.add(Material.FIRE); // 51 Fire
			standableMaterials.add(Material.REDSTONE_WIRE); // 55 Redstone Wire
			standableMaterials.add(Material.CROPS); // 59 Wheat Seeds
			standableMaterials.add(Material.SIGN_POST); // 63 Sign Post
			standableMaterials.add(Material.LADDER); // 65 Ladders
			standableMaterials.add(Material.RAILS); // 66 Rails
			standableMaterials.add(Material.WALL_SIGN); // 68 Wall Sign
			standableMaterials.add(Material.LEVER); // 69 Lever
			standableMaterials.add(Material.STONE_PLATE); // 70 Stone Pressure Plate
			standableMaterials.add(Material.WOOD_PLATE); // 72 Wooden Pressure Plate
			standableMaterials.add(Material.REDSTONE_TORCH_OFF); // 75 Redstone Torch (Off)
			standableMaterials.add(Material.REDSTONE_TORCH_ON); // 76 Redstone Torch (On)
			standableMaterials.add(Material.STONE_BUTTON); // 77 Stone Button
			standableMaterials.add(Material.SNOW); // 78 Snow
			standableMaterials.add(Material.SUGAR_CANE_BLOCK); // 83 Sugar Cane
			standableMaterials.add(Material.PORTAL); // 90 Portal
			standableMaterials.add(Material.DIODE_BLOCK_OFF); // 93 Redstone Repeater (Off)
			standableMaterials.add(Material.DIODE_BLOCK_ON); // 94 Redstone Repeater (On)
			standableMaterials.add(Material.VINE); // 106 Vines
			standableMaterials.add(Material.WATER_LILY); // 111 Lily Pad
			standableMaterials.add(Material.ENDER_PORTAL); // 119 End Portal
			standableMaterials.add(Material.WOOD_STEP); // 126 Wooden Slab
			standableMaterials.add(Material.TRIPWIRE_HOOK); // 131 Tripwire Hook
			standableMaterials.add(Material.TRIPWIRE); // 132 Tripwire
			standableMaterials.add(Material.FLOWER_POT); // 140 Flower Pot
			standableMaterials.add(Material.CARROT); // 141 Carrot
			standableMaterials.add(Material.POTATO); // 142 Potatoes	
			standableMaterials.add(Material.GOLD_PLATE); // 147 Gold Pressure Plate
			standableMaterials.add(Material.IRON_PLATE); // 148 Iron Pressure Plate
			standableMaterials.add(Material.REDSTONE_COMPARATOR_OFF); // 149 Redstone Comparator (Off)
			standableMaterials.add(Material.REDSTONE_COMPARATOR_ON); // 150 Redstone Comparator (On)	
			standableMaterials.add(Material.DAYLIGHT_DETECTOR); // 151 Daylight Sensor
			standableMaterials.add(Material.ACTIVATOR_RAIL); // 157 Activator Rail
			standableMaterials.add(Material.CARPET); // 171 Carpet
			standableMaterials.add(Material.DOUBLE_PLANT); // double_plant Double Plants
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
		return standableMaterials.contains(material);
	}

}