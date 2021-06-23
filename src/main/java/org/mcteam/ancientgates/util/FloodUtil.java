package org.mcteam.ancientgates.util;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.mcteam.ancientgates.Conf;
import org.mcteam.ancientgates.util.types.FloodOrientation;

public class FloodUtil {

	// Base air flood block algorithm
	public static Set<Block> getFloodBlocks(final Block startBlock, Set<Block> foundBlocks, final FloodOrientation orientation, final int limit) {
		if (foundBlocks == null)
			return null;
		if (foundBlocks.size() > limit)
			return null;
		if (foundBlocks.contains(startBlock))
			return foundBlocks;

		if (BlockUtil.isStandableMaterial(startBlock.getType())) {
			// Found a block
			foundBlocks.add(startBlock);

			// Only allow materials you can pass through, unless horizontal in which case accept all
			// standable materials
			// This always returns true, so removing it for now, will restore if needed
//			if (BlockUtil.canPassThroughMaterial(type) || orientation == FloodOrientation.HORIZONTAL)
//				validBlocks.add(startBlock);

			// Flood away
			for (final BlockFace face : orientation.getDirections()) {
				if (foundBlocks == null)
					return foundBlocks;

				final Block potentialBlock = startBlock.getRelative(face);
				foundBlocks = getFloodBlocks(potentialBlock, foundBlocks, orientation, limit);
			}
		}
		return foundBlocks;
	}

	// Multi-flood all orientations
	public static Map<FloodOrientation, Set<Block>> getAllAirFloods(final Block startBlock, final FloodOrientation[] orientations, final int limit) {
		final LinkedHashMap<FloodOrientation, Set<Block>> ret = new LinkedHashMap<>();
		for (final FloodOrientation orientation : orientations) {
			if (!Conf.useDiagonalPortals && orientation.isDiagonal())
				continue;
			ret.put(orientation, getFloodBlocks(startBlock, new HashSet<>(), orientation, limit));
		}
		return ret;
	}

	// Multi-flood best orientation
	public static Entry<FloodOrientation, Set<Block>> getBestAirFlood(final Block startBlock, final FloodOrientation[] orientations) {
		final Map<FloodOrientation, Set<Block>> floods = getAllAirFloods(startBlock, orientations, Conf.getGateMaxArea());
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
		return getFloodBlocks(block, new HashSet<>(), orientation, Conf.getGateMaxArea());
	}

	public static Set<Block> getPortalBlocks(final Block block) {
		final Entry<FloodOrientation, Set<Block>> flood = getBestAirFlood(block, FloodOrientation.values());
		if (flood == null)
			return null;

		final FloodOrientation orientation = flood.getKey();
		return getPortalBlocks(block, orientation);
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
		blocks.addAll(remainingBlocks);

		final Set<Block> surrounding = new HashSet<>();
		for (final Block currentBlock : blocks) {
			for (final BlockFace face : orientation.getAllDirections()) {
				final Block potentialBlock = currentBlock.getRelative(face);
				// Found a block
				surrounding.add(potentialBlock);
			}
		}
		return surrounding;
	}

}
