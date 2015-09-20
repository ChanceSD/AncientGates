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
	public static Set<Block> getFloodBlocks(final Block startBlock, final Set<Block> foundBlocks, Set<Block> validBlocks, final FloodOrientation orientation, final int limit) {
		if (foundBlocks == null)
			return null;
		if (foundBlocks.size() > limit)
			return null;
		if (foundBlocks.contains(startBlock))
			return validBlocks;

		if (BlockUtil.isStandableMaterial(startBlock.getType())) {
			// Found a block
			foundBlocks.add(startBlock);

			// Only allow materials you can pass through, unless horizontal in which case accept all
			// standable materials
			if (BlockUtil.canPassThroughMaterial(startBlock.getType()) || orientation == FloodOrientation.HORIZONTAL)
				validBlocks.add(startBlock);

			// Flood away
			for (final BlockFace face : orientation.getDirections()) {
				final Block potentialBlock = startBlock.getRelative(face);
				validBlocks = getFloodBlocks(potentialBlock, foundBlocks, validBlocks, orientation, limit);
			}
		}
		return validBlocks;
	}

	// Multi-flood all orientations
	public static LinkedHashMap<FloodOrientation, Set<Block>> getAllAirFloods(final Block startBlock, final FloodOrientation[] orientations, final int limit) {
		final LinkedHashMap<FloodOrientation, Set<Block>> ret = new LinkedHashMap<>();
		for (final FloodOrientation orientation : orientations) {
			ret.put(orientation, getFloodBlocks(startBlock, new HashSet<Block>(), new HashSet<Block>(), orientation, limit));
		}
		return ret;
	}

	// Multi-flood best orientation
	public static Entry<FloodOrientation, Set<Block>> getBestAirFlood(final Block startBlock, final FloodOrientation[] orientations) {
		final LinkedHashMap<FloodOrientation, Set<Block>> floods = getAllAirFloods(startBlock, orientations, Conf.getGateMaxArea());
		Entry<FloodOrientation, Set<Block>> ret = null;
		Integer bestSize = null;
		for (final Entry<FloodOrientation, Set<Block>> entry : floods.entrySet()) {
			if (entry.getValue() == null)
				continue;
			final int size = entry.getValue().size();
			if (bestSize == null || size < bestSize) {
				ret = entry;
				bestSize = size;
			}
		}
		return ret;
	}

	// Get gate portal blocks
	public static Set<Block> getPortalBlocks(final Block block, final FloodOrientation orientation) {
		final Set<Block> blocks = getFloodBlocks(block, new HashSet<Block>(), new HashSet<Block>(), orientation, Conf.getGateMaxArea());
		return blocks;
	}

	public static Set<Block> getPortalBlocks(final Block block) {
		final Entry<FloodOrientation, Set<Block>> flood = getBestAirFlood(block, FloodOrientation.values());
		if (flood == null)
			return null;

		final FloodOrientation orientation = flood.getKey();
		final Set<Block> blocks = getPortalBlocks(block, orientation);
		return blocks;
	}

	// Get gate frame blocks
	public static Set<Block> getFrameBlocks(final Set<Block> portalBlocks, final FloodOrientation orientation) {
		final Set<Block> frame = new HashSet<>();
		for (final Block currentBlock : portalBlocks) {
			for (final BlockFace face : orientation.getDirections()) {
				final Block potentialBlock = currentBlock.getRelative(face);
				// Found a block
				if (!portalBlocks.contains(potentialBlock)) {
					frame.add(potentialBlock);
				}
			}
		}
		return frame;
	}

	// Get surrounding blocks
	public static Set<Block> getSurroundingBlocks(final Set<Block> blocks, final Set<Block> remainingBlocks, final FloodOrientation orientation) {
		final Set<Block> allBlocks = new HashSet<>();
		blocks.addAll(blocks);
		blocks.addAll(remainingBlocks);

		final Set<Block> surrounding = new HashSet<>();
		for (final Block currentBlock : blocks) {
			for (final BlockFace face : orientation.getAllDirections()) {
				final Block potentialBlock = currentBlock.getRelative(face);
				// Found a block
				if (!allBlocks.contains(potentialBlock)) {
					surrounding.add(potentialBlock);
				}
			}
		}
		return surrounding;
	}

}
