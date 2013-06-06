package org.mcteam.ancientgates;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.mcteam.ancientgates.types.WorldCoord;
import org.mcteam.ancientgates.util.BlockUtil;

public class Gates {

	protected static Map<WorldCoord, Gate> portalBlockToGate = new HashMap<WorldCoord, Gate>();
	protected static Map<WorldCoord, Gate> surroundingPortalBlockToGate = new HashMap<WorldCoord, Gate>();
	protected static Map<WorldCoord, Gate> frameBlockToGate = new HashMap<WorldCoord, Gate>();
	protected static Map<WorldCoord, Gate> surroundingFrameBlockToGate = new HashMap<WorldCoord, Gate>();
	
	public Map<WorldCoord, Gate> portalBlockToGate() { return portalBlockToGate; }
	public Map<WorldCoord, Gate> surroundingPortalBlockToGate() { return surroundingPortalBlockToGate; }
	public Map<WorldCoord, Gate> frameBlockToGate() { return frameBlockToGate; }
	public Map<WorldCoord, Gate> surroundingFrameBlockToGate() { return surroundingFrameBlockToGate; }

	private Gates() {
	}
	
	// -------------------------------------------- //
	// Getters And Setters
	// -------------------------------------------- //
	public static Gate gateFromPortal(WorldCoord coord) { return portalBlockToGate.get(coord); }
	public static Gate gateFromPortalSurround(WorldCoord coord) { return surroundingPortalBlockToGate.get(coord); }
	public static Gate gateFromPortalAndSurround(WorldCoord coord) {
		Gate gate = gateFromPortalSurround(coord);
		if (gate != null) return gate;
		return gateFromPortal(coord);
	}
	
	public static Gate gateFromFrame(WorldCoord coord) { return frameBlockToGate.get(coord); }
	public static Gate gateFromFrameSurround(WorldCoord coord) { return surroundingFrameBlockToGate.get(coord); }
	public static Gate gateFromFrameAndSurround(WorldCoord coord) {
		Gate gate = gateFromFrameSurround(coord);
		if (gate != null) return gate;
		return gateFromFrame(coord);
	}
	
	public static Gate gateFromAll(WorldCoord coord) {
		Gate gate = gateFromFrame(coord);
		if (gate != null) return gate;
		return gateFromPortalAndSurround(coord);
	}	
	
	public static Gate gateFromAllWithSurround(WorldCoord coord) {
		Gate gate = gateFromFrameAndSurround(coord);
		if (gate != null) return gate;
		return gateFromPortalAndSurround(coord);
	}
	
	//----------------------------------------------//
	// The Open & Close Methods
	//----------------------------------------------//
	public static boolean open(Gate gate) {
		// Re-populate data
		if(!gate.dataPopulate()) return false;
		clearIndexFor(gate);
		buildIndexFor(gate);
		
		Gate.save();
		
		// This is not to do an effect
		// It is to stop portalblocks from destroyingthemself as they can't rely on non created blocks
		if (BlockUtil.isPortalGateMaterial(gate.getMaterialStr())) {
			for (WorldCoord coord : gate.getPortalBlocks()) {
				coord.getBlock().setType(Material.GLOWSTONE);
			}
		}
		
		for (WorldCoord coord : gate.getPortalBlocks()) {
			coord.getBlock().setType(gate.getMaterial());
			// Stop ice forming based on biome
			if (gate.getMaterial() == Material.STATIONARY_WATER) {
				coord.getBlock().setBiome(Biome.FOREST);
			}
		}

		return true;
	}
	
	public static void close(Gate gate) {
		// Clear data
		clearIndexFor(gate);
		
		Gate.save();
		
		for (WorldCoord coord :  gate.getPortalBlocks()) {
			coord.getBlock().setType(Material.AIR);
		}
	}
	
	public static boolean isOpen(Gate gate) {
		return BlockUtil.isStandableGateMaterial(gate.getFroms().get(0).getBlock().getType());
	}
	
	//----------------------------------------------//
	// Persistance and entity management
	//----------------------------------------------//
	public static void load() {
		// Load gates
		Gate.load();
		
		Plugin.log("Loading gates into memory");
		for (Gate gate : Gate.getAll()) {
			// Populate block data
			gate.dataPopulate();
			
			// Build lookup index
			if (isOpen(gate)) {
				buildIndexFor(gate);
			}
		}
	}
	
	public static void clearIndexFor(Gate gate) {
		for (WorldCoord coord : gate.getPortalBlocks()) {
			portalBlockToGate.remove(coord);
		}
		for (WorldCoord coord : gate.getSurroundingPortalBlocks()) {
			surroundingPortalBlockToGate.remove(coord);
		}
		for (WorldCoord coord : gate.getFrameBlocks()) {
			frameBlockToGate.remove(coord);
		}
		for (WorldCoord coord : gate.getSurroundingFrameBlocks()) {
			surroundingFrameBlockToGate.remove(coord);
		}
	}
	
	public static void buildIndexFor(Gate gate) {
		for (WorldCoord coord : gate.getPortalBlocks()) {
			portalBlockToGate.put(coord, gate);
		}
		for (WorldCoord coord : gate.getSurroundingPortalBlocks()) {
			surroundingPortalBlockToGate.put(coord, gate);
		}
		for (WorldCoord coord : gate.getFrameBlocks()) {
			frameBlockToGate.put(coord, gate);
		}
		for (WorldCoord coord : gate.getSurroundingFrameBlocks()) {
			surroundingFrameBlockToGate.put(coord, gate);
		}
	}
	
}