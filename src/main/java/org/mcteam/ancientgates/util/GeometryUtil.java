package org.mcteam.ancientgates.util;

import org.bukkit.Location;
import org.mcteam.ancientgates.util.types.WorldCoord;

public class GeometryUtil {

	// How long between two locations?
	public static double distanceBetweenLocations(final Location location1, final Location location2) {
		final double X = location1.getX() - location2.getX();
		final double Y = location1.getY() - location2.getY();
		final double Z = location1.getZ() - location2.getZ();
		return Math.sqrt(X * X + Y * Y + Z * Z);
	}

	// How long between two coords?
	public static double distanceBetweenCoords(final WorldCoord coord1, final WorldCoord coord2) {
		final double X = (double) coord1.x - (double) coord2.x;
		final double Y = (double) coord1.y - (double) coord2.y;
		final double Z = (double) coord1.z - (double) coord2.z;
		return Math.sqrt(X * X + Y * Y + Z * Z);
	}

	// Add additional height to location
	public static Location addHeightToLocation(final Location location, final double h) {
		final double Y = location.getY();
		location.setY(Y + h);
		return location;
	}

}
