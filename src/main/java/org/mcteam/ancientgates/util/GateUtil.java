package org.mcteam.ancientgates.util;

import java.util.Map;
import java.util.Set;

import org.bukkit.Location;
import org.mcteam.ancientgates.Gate;
import org.mcteam.ancientgates.Gates;
import org.mcteam.ancientgates.util.types.WorldCoord;

public class GateUtil {

	private static final String WORLD = "world";

	// Nearest Ancient Gate - Geometry radius comparison (slow)
	public static Gate nearestGate(final WorldCoord coord, final Boolean teleport, final Double radius) {
		Gate nearestGate = null;
		double shortestDistance = -1;

		for (final Gate gate : Gate.getAll()) {
			if (gate.getFroms() == null || gate.getTos() == null && gate.getBungeeTos() == null && teleport == true)
				continue;

			// Check the distance between the location and all portal blocks within the gate
			final Set<WorldCoord> portalBlockCoords = gate.getPortalBlocks().keySet();
			if (portalBlockCoords == null)
				continue;

			for (final WorldCoord blockCoord : portalBlockCoords) {
				if (!blockCoord.getWorld().equals(coord.getWorld()))
					continue; // We can only be close to gates in the same world

				final double distance = GeometryUtil.distanceBetweenLocations(coord.getLocation(), blockCoord.getLocation());

				if (distance > radius)
					continue;

				if (shortestDistance == -1 || shortestDistance > distance) {
					nearestGate = gate;
					shortestDistance = distance;
				}
			}
		}

		return nearestGate;
	}

	// Nearest Ancient Gate - HashSet fast comparison
	public static Gate nearestGate(final WorldCoord coord, final Boolean teleport) {
		final Gate nearestGate = Gates.gateFromAll(coord);

		if (nearestGate != null) {
			if (nearestGate.getFroms() == null || nearestGate.getTos() == null && nearestGate.getBungeeTos() == null && teleport == true)
				return null;
		}

		return nearestGate;
	}

	// Nearest Ancient Gate 'from' location (coordinate)
	public static String nearestFrom(final WorldCoord coord) {
		String nearestFrom = "";
		double shortestDistance = -1;

		for (final Gate gate : Gate.getAll()) {
			if (gate.getFroms() == null)
				continue;

			for (final Location from : gate.getFroms()) {
				if (!from.getWorld().equals(coord.getWorld()))
					continue; // We can only be close to gates in the same world

				final double distance = GeometryUtil.distanceBetweenLocations(coord.getLocation(), from);

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
	public static String nearestFrom(final Location location) {
		return nearestFrom(new WorldCoord(location));
	}

	// Nearest Ancient Gate 'to' location (coordinate)
	public static String nearestTo(final WorldCoord coord) {
		String nearestTo = "";
		double shortestDistance = -1;

		for (final Gate gate : Gate.getAll()) {
			if (gate.getTos() == null)
				continue;

			for (final Location to : gate.getTos()) {
				if (!to.getWorld().equals(coord.getWorld()))
					continue; // We can only be close to gates in the same world

				final double distance = GeometryUtil.distanceBetweenLocations(coord.getLocation(), to);

				if (distance > 10.0) {
					continue;
				}

				if (shortestDistance == -1 || shortestDistance > distance) {
					nearestTo = TeleportUtil.locationToString(to);
					shortestDistance = distance;
				}
			}
		}

		return nearestTo;
	}

	// Nearest Ancient Gate 'to' location (location)
	public static String nearestTo(final Location location) {
		return nearestTo(new WorldCoord(location));
	}

	// Nearest Ancient Gate 'bungeeto' location (coordinate)
	public static String nearestBungeeTo(final WorldCoord coord) {
		String nearestBungeeTo = "";
		double shortestDistance = -1;

		for (final Gate gate : Gate.getAll()) {
			if (gate.getBungeeTos() == null)
				continue;

			for (final Map<String, String> bungeeto : gate.getBungeeTos()) {
				if (!bungeeto.get(WORLD).equals(coord.worldName))
					continue; // We can only be close to gates in the same world

				final double distance = GeometryUtil.distanceBetweenCoords(coord, new WorldCoord(bungeeto));

				if (distance > 10.0) {
					continue;
				}

				if (shortestDistance == -1 || shortestDistance > distance) {
					nearestBungeeTo = TeleportUtil.locationToString(bungeeto);
					shortestDistance = distance;
				}
			}
		}

		return nearestBungeeTo;
	}

}
