package org.mcteam.ancientgates;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.mcteam.ancientgates.util.BlockUtil;
import org.mcteam.ancientgates.util.types.FloodOrientation;
import org.mcteam.ancientgates.util.types.WorldCoord;

public class Gates {

	protected static Map<WorldCoord, Gate> portalBlockToGate = new HashMap<>();
	protected static Map<WorldCoord, Gate> surroundingPortalBlockToGate = new HashMap<>();
	protected static Map<WorldCoord, Gate> frameBlockToGate = new HashMap<>();
	protected static Map<WorldCoord, Gate> surroundingFrameBlockToGate = new HashMap<>();

	public Map<WorldCoord, Gate> portalBlockToGate() {
		return portalBlockToGate;
	}

	public Map<WorldCoord, Gate> surroundingPortalBlockToGate() {
		return surroundingPortalBlockToGate;
	}

	public Map<WorldCoord, Gate> frameBlockToGate() {
		return frameBlockToGate;
	}

	public Map<WorldCoord, Gate> surroundingFrameBlockToGate() {
		return surroundingFrameBlockToGate;
	}

	private Gates() {
	}

	// -------------------------------------------- //
	// Getters And Setters
	// -------------------------------------------- //
	public static Gate gateFromPortal(final WorldCoord coord) {
		return portalBlockToGate.get(coord);
	}

	public static Gate gateFromPortalSurround(final WorldCoord coord) {
		return surroundingPortalBlockToGate.get(coord);
	}

	public static Gate gateFromPortalAndSurround(final WorldCoord coord) {
		final Gate gate = gateFromPortalSurround(coord);
		if (gate != null)
			return gate;
		return gateFromPortal(coord);
	}

	public static Gate gateFromFrame(final WorldCoord coord) {
		return frameBlockToGate.get(coord);
	}

	public static Gate gateFromFrameSurround(final WorldCoord coord) {
		return surroundingFrameBlockToGate.get(coord);
	}

	public static Gate gateFromFrameAndSurround(final WorldCoord coord) {
		final Gate gate = gateFromFrameSurround(coord);
		if (gate != null)
			return gate;
		return gateFromFrame(coord);
	}

	public static Gate gateFromAll(final WorldCoord coord) {
		final Gate gate = gateFromFrame(coord);
		if (gate != null)
			return gate;
		return gateFromPortalAndSurround(coord);
	}

	public static Gate gateFromAllWithSurround(final WorldCoord coord) {
		final Gate gate = gateFromFrameAndSurround(coord);
		if (gate != null)
			return gate;
		return gateFromPortalAndSurround(coord);
	}

	// ----------------------------------------------//
	// The Open & Close Methods
	// ----------------------------------------------//
	@SuppressWarnings("deprecation")
	public static boolean open(final Gate gate) {
		// Re-populate data
		if (!gate.dataPopulate())
			return false;
		clearIndexFor(gate);
		buildIndexFor(gate);

		// This is not to do an effect
		// It is to stop portalblocks from destroying themselves as they can't rely on air blocks
		if (BlockUtil.isPortalGateMaterial(gate.getMaterial())) {
			for (final WorldCoord coord : gate.getPortalBlocks().keySet()) {
				coord.getBlock().setType(Material.GLOWSTONE);
			}
		}

		for (final WorldCoord coord : gate.getPortalBlocks().keySet()) {
			Material material = gate.getMaterial();

			// Force vertical PORTALs and horizontal ENDER_PORTALs
			final FloodOrientation orientation = gate.getPortalBlocks().get(coord);
			if (orientation == FloodOrientation.HORIZONTAL && material == Material.PORTAL) {
				material = Material.ENDER_PORTAL;
			} else if (orientation != FloodOrientation.HORIZONTAL && material == Material.ENDER_PORTAL) {
				material = Material.PORTAL;
			}

			coord.getBlock().setType(material);

			// Stop ice forming based on biome (horizontal water portals)
			if (orientation == FloodOrientation.HORIZONTAL && gate.getMaterial() == Material.STATIONARY_WATER) {
				coord.getBlock().setBiome(Biome.FOREST);
			}
			if (orientation == FloodOrientation.VERTICAL1 && material == Material.PORTAL) {
				coord.getBlock().setData((byte) 2);
			}
		}

		return true;
	}

	public static void close(final Gate gate) {
		if (!isOpen(gate))
			return;

		for (final WorldCoord coord : gate.getPortalBlocks().keySet()) {
			coord.getBlock().breakNaturally(); // Break naturally - Fix portal orientation
		}

		for (final WorldCoord coord : gate.getPortalBlocks().keySet()) {
			// Revert biome back to gate frame biome
			final FloodOrientation orientation = gate.getPortalBlocks().get(coord);
			if (orientation == FloodOrientation.HORIZONTAL && gate.getMaterial() == Material.STATIONARY_WATER) {
				coord.getBlock().setBiome(((WorldCoord) gate.getFrameBlocks().toArray()[0]).getBlock().getBiome());
			}
		}

		// Clear data
		clearIndexFor(gate);
	}

	public static boolean isOpen(final Gate gate) {
		if (gate.getFroms() == null)
			return false;
		return BlockUtil.isStandableGateMaterial(gate.getFroms().get(0).getBlock().getType());
	}

	// ----------------------------------------------//
	// Persistance and entity management
	// ----------------------------------------------//
	public static void load() {
		// Load gates
		Gate.load();

		Plugin.log("Loading gates into memory");
		for (final Gate gate : Gate.getAll()) {
			// Populate block data
			gate.dataPopulate();

			// Build lookup index
			if (isOpen(gate)) {
				buildIndexFor(gate);
			}
		}
	}

	public static void clearIndexFor(final Gate gate) {
		for (final WorldCoord coord : gate.getPortalBlocks().keySet()) {
			portalBlockToGate.remove(coord);
		}
		for (final WorldCoord coord : gate.getSurroundingPortalBlocks()) {
			surroundingPortalBlockToGate.remove(coord);
		}
		for (final WorldCoord coord : gate.getFrameBlocks()) {
			frameBlockToGate.remove(coord);
		}
		for (final WorldCoord coord : gate.getSurroundingFrameBlocks()) {
			surroundingFrameBlockToGate.remove(coord);
		}
	}

	public static void buildIndexFor(final Gate gate) {
		for (final WorldCoord coord : gate.getPortalBlocks().keySet()) {
			portalBlockToGate.put(coord, gate);
		}
		for (final WorldCoord coord : gate.getSurroundingPortalBlocks()) {
			surroundingPortalBlockToGate.put(coord, gate);
		}
		for (final WorldCoord coord : gate.getFrameBlocks()) {
			frameBlockToGate.put(coord, gate);
		}
		for (final WorldCoord coord : gate.getSurroundingFrameBlocks()) {
			surroundingFrameBlockToGate.put(coord, gate);
		}
	}

}
