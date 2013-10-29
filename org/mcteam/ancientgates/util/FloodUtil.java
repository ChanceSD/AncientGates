package org.mcteam.ancientgates.util;

import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import org.mcteam.ancientgates.Conf;
import org.mcteam.ancientgates.util.types.FloodOrientation;

public class FloodUtil {
	
	// Base air flood block algorithm
	public static Set<Block> getFloodBlocks(Block startBlock, Set<Block> foundBlocks, Set<BlockFace> expandFaces, int limit) {
		if (foundBlocks == null) return null;
		if (foundBlocks.size() > limit) return null;
		if (foundBlocks.contains(startBlock)) return foundBlocks;
		
		if (BlockUtil.isStandableMaterial(startBlock.getType())) {
			// Found a block
			foundBlocks.add(startBlock);
			
			// Flood away
			for (BlockFace face : expandFaces) {
				Block potentialBlock = startBlock.getRelative(face);
				foundBlocks = getFloodBlocks(potentialBlock, foundBlocks, expandFaces, limit);
			}
		}
		return foundBlocks;	
	}

	// Multi-flood all orientations
	public static Map<FloodOrientation, Set<Block>> getAllAirFloods(Block startBlock, Collection<FloodOrientation> orientations, int limit) {
		Map<FloodOrientation, Set<Block>> ret = new HashMap<FloodOrientation, Set<Block>>();
		for (FloodOrientation orientation : orientations) {
			if (Conf.gateDiagonalOrientations || (!orientation.equals(FloodOrientation.VERTICAL3) && !orientation.equals(FloodOrientation.VERTICAL4)))
				ret.put(orientation, getFloodBlocks(startBlock, new HashSet<Block>(), orientation.getDirections(), limit));
		}
		return ret;	
	}
	
	 // Multi-flood best orientation
	public static Entry<FloodOrientation, Set<Block>> getBestAirFlood(Block startBlock, Collection<FloodOrientation> orientations) {
		Map<FloodOrientation, Set<Block>> floods = getAllAirFloods(startBlock, orientations, Conf.getGateMaxArea());
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
		Set<Block> blocks = getFloodBlocks(block, new HashSet<Block>(), orientation.getDirections(), Conf.getGateMaxArea());
		return blocks;	
	}
	public static Set<Block> getPortalBlocks(Block block) {
		Entry<FloodOrientation, Set<Block>> flood = getBestAirFlood(block, EnumSet.allOf(FloodOrientation.class));
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