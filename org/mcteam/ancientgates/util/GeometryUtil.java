package org.mcteam.ancientgates.util;

import org.bukkit.Location;

import org.mcteam.ancientgates.util.types.WorldCoord;

public class GeometryUtil {
	
	// How long between two locations?
	public static double distanceBetweenLocations(Location location1, Location location2) {
		double X = location1.getX() - location2.getX();
		double Y = location1.getY() - location2.getY();
		double Z = location1.getZ() - location2.getZ();
		return Math.sqrt(X*X+Y*Y+Z*Z);
	}
	
	// How long between two coords?
	public static double distanceBetweenCoords(WorldCoord coord1, WorldCoord coord2) {
		double X = (double)coord1.x - (double)coord2.x;
		double Y = (double)coord1.y - (double)coord2.y;
		double Z = (double)coord1.z - (double)coord2.z;
		return Math.sqrt(X*X+Y*Y+Z*Z);
	}
	
	// Add additional height to location
	public static Location addHeightToLocation(Location location, double h) {
		double Y = location.getY();
		location.setY(Y+h);
		return location;
	}
	
}
