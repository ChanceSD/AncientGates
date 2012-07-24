package org.mcteam.ancientgates.util;

import org.bukkit.Location;

public class GeometryUtil {
	
	// How long between two locations?
	public static double distanceBetweenLocations(Location location1, Location location2) {
		double X = location1.getX() - location2.getX();
		double Y = location1.getY() - location2.getY();
		double Z = location1.getZ() - location2.getZ();
		return Math.sqrt(X*X+Y*Y+Z*Z);
	}
	
}
