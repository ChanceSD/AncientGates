package org.mcteam.ancientgates.util;

import java.util.Set;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.mcteam.ancientgates.Gate;

public class GateUtil {
	
	//Nearest Ancient Gate
	public static Gate nearestGate(Location location, Boolean teleport, Double radius) {
		Gate nearestGate = null;
		double shortestDistance = -1;
		
		for (Gate gate : Gate.getAll()) {
			if ( gate.getFroms() == null || (gate.getTo() == null && gate.getBungeeTo() == null && teleport == true)) {
				continue;
			}
			
			for (Location from : gate.getFroms()) {
				if ( ! from.getWorld().equals(location.getWorld())) {
					continue; // We can only be close to gates in the same world
				}	
			
				// Check the distance between the location and all portal blocks within the gate
				Set<Block> blocks = FloodUtil.getGateFrameBlocks(from.getBlock());
				if (blocks == null) {
					continue;
				}
			
				for (Block block : blocks) {
					double distance = GeometryUtil.distanceBetweenLocations(location, block.getLocation());
				
					if (distance > radius) {
						continue;
					}
				
					if (shortestDistance == -1 || shortestDistance > distance) {
						nearestGate = gate;
						shortestDistance = distance;
					}
				}
			}
		}
		
		return nearestGate;
	}
	
	// Nearest Ancient Gate (without radius)
	public static Gate nearestGate(Location location, Boolean teleport) {
		return nearestGate(location, teleport, 1.0);
	}
	
	//Nearest Ancient Gate from location
	public static String nearestFrom(Location location) {
		String nearestFrom = "";
		double shortestDistance = -1;
		
		for (Gate gate : Gate.getAll()) {
			if (gate.getFroms() == null) {
				continue;
			}
			
			for (Location from : gate.getFroms()) {
				if ( ! from.getWorld().equals(location.getWorld())) {
					continue; // We can only be close to gates in the same world
				}	
				
				double distance = GeometryUtil.distanceBetweenLocations(location, from);

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
	
}
