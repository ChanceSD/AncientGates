package org.mcteam.ancientgates.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public class BlockUtil {

	public static Map<String, Material> spawnableGateMaterials;
	public static HashSet<Material> standableGateMaterials;
	public static HashSet<Integer> standableMaterials;

	static {
		spawnableGateMaterials = new HashMap<String, Material>();
		spawnableGateMaterials.put("ENDPORTAL", Material.ENDER_PORTAL);
		spawnableGateMaterials.put("LAVA", Material.STATIONARY_LAVA);
		spawnableGateMaterials.put("PORTAL", Material.PORTAL);
		spawnableGateMaterials.put("SUGARCANE", Material.SUGAR_CANE_BLOCK);
		spawnableGateMaterials.put("WATER", Material.STATIONARY_WATER);
		spawnableGateMaterials.put("WEB", Material.WEB);
	}
	
	static {
		standableGateMaterials = new HashSet<Material>();
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
		standableMaterials = new HashSet<Integer>();
		standableMaterials.add(0); // 0 Air
		standableMaterials.add(6); // 6 Saplings
		standableMaterials.add(8); // 8 Water
		standableMaterials.add(9); // 9 Stationary water
		standableMaterials.add(10); // 10 Lava
		standableMaterials.add(11); // 11 Stationary lava
		standableMaterials.add(27); // 27 Powered Rail
		standableMaterials.add(28); // 28 Detector Rail
		standableMaterials.add(30); // 30 Cobweb
		standableMaterials.add(31); // 31 Tall Grass
		standableMaterials.add(32); // 32 Dead Bush
		standableMaterials.add(37); // 37 Dandelion
		standableMaterials.add(38); // 38 Rose
		standableMaterials.add(39); // 39 Brown Mushroom
		standableMaterials.add(40); // 40 Red Mushroom
		standableMaterials.add(50); // 50 Torch
		standableMaterials.add(51); // 51 Fire
		standableMaterials.add(55); // 55 Redstone Wire
		standableMaterials.add(59); // 59 Wheat Seeds
		standableMaterials.add(63); // 63 Sign Post
		standableMaterials.add(65); // 65 Ladders
		standableMaterials.add(66); // 66 Rails
		standableMaterials.add(68); // 68 Wall Sign
		standableMaterials.add(69); // 69 Lever
		standableMaterials.add(70); // 70 Stone Pressure Plate
		standableMaterials.add(72); // 72 Wooden Pressure Plate
		standableMaterials.add(75); // 75 Redstone Torch ("off" state)
		standableMaterials.add(76); // 76 Redstone Torch ("on" state)
		standableMaterials.add(77); // 77 Stone Button
		standableMaterials.add(78); // 78 Snow
		standableMaterials.add(83); // 83 Sugar Cane
		standableMaterials.add(90); // 90 Portal
		standableMaterials.add(93); // 93 Redstone Repeater ("off" state)
		standableMaterials.add(94); // 94 Redstone Repeater ("on" state)
		standableMaterials.add(106); // 106 Vines
		standableMaterials.add(111); // 111 Lily Pad
		standableMaterials.add(119); // 119 End Portal
	}
	
	public static Material asSpawnableGateMaterial(String material) {
		return spawnableGateMaterials.get(material);
	}
	
	public static boolean isPortalGateMaterial(String material) {
		return spawnableGateMaterials.get(material) == Material.PORTAL || spawnableGateMaterials.get(material) == Material.ENDER_PORTAL;
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
		return standableMaterials.contains(material.getId());
	}

}