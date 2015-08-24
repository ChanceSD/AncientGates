package org.mcteam.ancientgates.util.types;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import org.bukkit.block.BlockFace;

public enum FloodOrientation {
	// |
	VERTICAL1("facing west/east",  BlockFace.WEST, BlockFace.EAST, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.UP, BlockFace.DOWN),
	
	// --
	VERTICAL2("facing north/south", BlockFace.NORTH, BlockFace.SOUTH, BlockFace.WEST, BlockFace.EAST, BlockFace.UP, BlockFace.DOWN),
	
	// O
	HORIZONTAL("facing up/down", BlockFace.UP, BlockFace.DOWN, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.WEST, BlockFace.EAST),
	
	// /
	VERTICAL3("facing northwest/southeast", BlockFace.NORTH_WEST, BlockFace.SOUTH_EAST, BlockFace.NORTH_EAST, BlockFace.SOUTH_WEST, BlockFace.UP, BlockFace.DOWN),
	
	// \
	VERTICAL4("facing northeast/southwest", BlockFace.NORTH_EAST, BlockFace.SOUTH_WEST, BlockFace.NORTH_WEST, BlockFace.SOUTH_EAST, BlockFace.UP, BlockFace.DOWN);
	

	protected final Set<BlockFace> directions;
	public Set<BlockFace> getDirections() { return this.directions; }
	
	protected final Set<BlockFace> oppositeDirections;
	public Set<BlockFace> getOppositeDirections() { return this.oppositeDirections; }
	
	protected final Set<BlockFace> allDirections;
	public Set<BlockFace> getAllDirections() { return this.allDirections; }
	
	protected final String desc;
	public String getDesc() { return this.desc; }
	
	private FloodOrientation(final String desc, final BlockFace opDir1, final BlockFace opDir2, final BlockFace... directions) {
		this.directions = new LinkedHashSet<BlockFace>(Arrays.asList(directions));
		this.oppositeDirections = new LinkedHashSet<BlockFace>(Arrays.asList(opDir1, opDir2));
		this.desc = desc;
		
		this.allDirections = new LinkedHashSet<BlockFace>(Arrays.asList(directions));
		this.allDirections.addAll(this.oppositeDirections);
	}
	
}