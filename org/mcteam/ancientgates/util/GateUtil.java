package org.mcteam.ancientgates.util;

import java.util.Set;

import org.bukkit.Location;
import org.mcteam.ancientgates.Gate;
import org.mcteam.ancientgates.Gates;
import org.mcteam.ancientgates.util.types.WorldCoord;

public class GateUtil {
	
	// Nearest Ancient Gate - Geometry radius comparison (slow)
	public static Gate nearestGate(WorldCoord coord, Boolean teleport, Double radius) {
		Gate nearestGate = null;
		double shortestDistance = -1;

		for (Gate gate : Gate.getAll()) {
			if (gate.getFroms() == null || (gate.getTo() == null && gate.getBungeeTo() == null && teleport == true)) continue;

			// Check the distance between the location and all portal blocks within the gate
			Set<WorldCoord> portalBlockCoords = gate.getPortalBlocks().keySet();
			if (portalBlockCoords == null) continue;

			for (WorldCoord blockCoord : portalBlockCoords) {
				if ( ! blockCoord.getWorld().equals(coord.getWorld())) continue; // We can only be close to gates in the same world
				
				double distance = GeometryUtil.distanceBetweenLocations(coord.getLocation(), blockCoord.getLocation());

				if (distance > radius) continue;

				if (shortestDistance == -1 || shortestDistance > distance) {
					nearestGate = gate;
					shortestDistance = distance;
				}
			}
		}

		return nearestGate;
	}
	
	// Nearest Ancient Gate - HashSet fast comparison
	public static Gate nearestGate(WorldCoord coord, Boolean teleport) {
		Gate nearestGate = Gates.gateFromAll(coord);

		if (nearestGate != null) {
			if (nearestGate.getFroms() == null || (nearestGate.getTo() == null && nearestGate.getBungeeTo() == null && teleport == true)) return null;
		}

		return nearestGate;
	}
	
	// Nearest Ancient Gate 'from' location (coordinate)
	public static String nearestFrom(WorldCoord coord) {
		String nearestFrom = "";
		double shortestDistance = -1;
		
		for (Gate gate : Gate.getAll()) {
			if (gate.getFroms() == null) {
				continue;
			}
			
			for (Location from : gate.getFroms()) {
				if ( ! from.getWorld().equals(coord.getWorld())) {
					continue; // We can only be close to gates in the same world
				}	
				
				double distance = GeometryUtil.distanceBetweenLocations(coord.getLocation(), from);

				if (distance > 10.0) {
					continue;
				}

				if (shortestDistance == -1 || shortestDistance > distance) {
					nearestFrom = TeleportUtil.locationToString(from);
					shortestDistance = distance;
				}
			}
		}
		
		return nearestFrom;
	}
	
	// Nearest Ancient Gate 'from' location (location)
	public static String nearestFrom(Location location) {
		return nearestFrom(new WorldCoord(location));
	}
	
}
