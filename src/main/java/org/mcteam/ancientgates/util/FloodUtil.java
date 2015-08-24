package org.mcteam.ancientgates.util;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import org.mcteam.ancientgates.Conf;
import org.mcteam.ancientgates.util.types.FloodOrientation;

public class FloodUtil {
	
	// Base air flood block algorithm
	public static Set<Block> getFloodBlocks(Block startBlock, Set<Block> foundBlocks, Set<Block> validBlocks, FloodOrientation orientation, int limit) {
		if (foundBlocks == null) return null;
		if (foundBlocks.size() > limit) return null;
		if (foundBlocks.contains(startBlock)) return validBlocks;
		
		if (BlockUtil.isStandableMaterial(startBlock.getType())) {
			// Found a block
			foundBlocks.add(startBlock);
			
			// Only allow materials you can pass through, unless horizontal in which case accept all standable materials
			if (BlockUtil.canPassThroughMaterial(startBlock.getType()) || orientation == FloodOrientation.HORIZONTAL) validBlocks.add(startBlock);
			
			// Flood away
			for (BlockFace face : orientation.getDirections()) {
				Block potentialBlock = startBlock.getRelative(face);
				validBlocks = getFloodBlocks(potentialBlock, foundBlocks, validBlocks, orientation, limit);
			}
		}
		return validBlocks;	
	}

	// Multi-flood all orientations
	public static LinkedHashMap<FloodOrientation, Set<Block>> getAllAirFloods(Block startBlock, FloodOrientation[] orientations, int limit) {
		LinkedHashMap<FloodOrientation, Set<Block>> ret = new LinkedHashMap<FloodOrientation, Set<Block>>();
		for (FloodOrientation orientation : orientations) {
			ret.put(orientation, getFloodBlocks(startBlock, new HashSet<Block>(), new HashSet<Block>(), orientation, limit));
		}
		return ret;	
	}
	
	 // Multi-flood best orientation
	public static Entry<FloodOrientation, Set<Block>> getBestAirFlood(Block startBlock, FloodOrientation[] orientations) {
		LinkedHashMap<FloodOrientation, Set<Block>> floods = getAllAirFloods(startBlock, orientations, Conf.getGateMaxArea());
		Entry<FloodOrientation, Set<Block>> ret = null;
		Integer bestSize = null;
		for (Entry<FloodOrientation, Set<Block>> entry : floods.entrySet()) {
			if (entry.getValue() == null) continue;
			int size = entry.getValue().size();
			if (bestSize == null || size < bestSize) {
				ret = entry;
				bestSize = size;
			}
		}
		return ret;	
	}

	// Get gate portal blocks
	public static Set<Block> getPortalBlocks(Block block, FloodOrientation orientation) {
		Set<Block> blocks = getFloodBlocks(block, new HashSet<Block>(), new HashSet<Block>(), orientation, Conf.getGateMaxArea());
		return blocks;	
	}
	public static Set<Block> getPortalBlocks(Block block) {
		Entry<FloodOrientation, Set<Block>> flood = getBestAirFlood(block, FloodOrientation.values());
		if (flood == null) return null;

		FloodOrientation orientation = flood.getKey();
		Set<Block> blocks = getPortalBlocks(block, orientation);
		return blocks;	
	}
	
	// Get gate frame blocks
	public static Set<Block> getFrameBlocks(Set<Block> portalBlocks, FloodOrientation orientation) {
		Set<Block> frame = new HashSet<Block>();
		for (Block currentBlock : portalBlocks) {
			for (BlockFace face : orientation.getDirections()) {
				Block potentialBlock = currentBlock.getRelative(face);
				// Found a block
				if (!portalBlocks.contains(potentialBlock)) {
					frame.add(potentialBlock);
				}
			}
		}
		return frame;
	}
	
	// Get surrounding blocks
	public static Set<Block> getSurroundingBlocks(Set<Block> blocks, Set<Block> remainingBlocks, FloodOrientation orientation) {
		Set<Block> allBlocks = new HashSet<Block>();
		blocks.addAll(blocks);
		blocks.addAll(remainingBlocks);
	
		Set<Block> surrounding = new HashSet<Block>();
		for (Block currentBlock : blocks) {
			for (BlockFace face : orientation.getAllDirections()) {
				Block potentialBlock = currentBlock.getRelative(face);
				// Found a block
				if (!allBlocks.contains(potentialBlock)) {
					surrounding.add(potentialBlock);
				}
			}
		}
		return surrounding;
	}

}