package org.mcteam.ancientgates.util.types;

import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.mcteam.ancientgates.util.TeleportUtil;

public class WorldCoord {

	public String worldName = "world";
	public int x = 0;
	public int y = 0;
	public int z = 0;

	private static final String WORLD = "world";
	private static final String X = "x";
	private static final String Y = "y";
	private static final String Z = "z";

	// ----------------------------------------------//
	// Constructors
	// ----------------------------------------------//
	public WorldCoord(final String worldName, final int x, final int y, final int z) {
		this.worldName = worldName;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public WorldCoord(final String worldName, final double x, final double y, final double z) {
		this(worldName, (int) x, (int) y, (int) z);
	}

	public WorldCoord(final Location location) {
		this(location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
	}

	public WorldCoord(final String location) {
		this(TeleportUtil.stringToLocation(location));
	}

	public WorldCoord(final Map<String, String> location) {
		this(location.get(WORLD), Double.parseDouble(location.get(X)), Double.parseDouble(location.get(Y)), Double.parseDouble(location.get(Z)));
	}

	public WorldCoord(final Block block) {
		this(block.getWorld().getName(), block.getX(), block.getY(), block.getZ());
	}

	public WorldCoord(final Item item) {
		this(item.getLocation());
	}

	// ----------------------------------------------//
	// Converters
	// ----------------------------------------------//
	@Override
	public String toString() {
		return "(" + x + ", " + y + ", " + z + ") in " + worldName;
	}

	public Block getBlock() {
		final World world = Bukkit.getServer().getWorld(worldName);
		if (world == null)
			return null;
		return world.getBlockAt(x, y, z);
	}

	public Location getLocation() {
		final Block block = this.getBlock();
		if (block == null)
			return null;
		return block.getLocation();
	}

	public World getWorld() {
		final Location location = this.getLocation();
		if (location == null)
			return null;
		return location.getWorld();
	}

	// ----------------------------------------------//
	// Comparison
	// ----------------------------------------------//
	@Override
	public int hashCode() {
		int hash = 3;
		hash = 19 * hash + (this.worldName != null ? this.worldName.hashCode() : 0);
		hash = 19 * hash + this.x;
		hash = 19 * hash + this.y;
		hash = 19 * hash + this.z;
		return hash;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == this)
			return true;
		if (!(obj instanceof WorldCoord))
			return false;

		final WorldCoord that = (WorldCoord) obj;
		return this.x == that.x && this.y == that.y && this.z == that.z && (this.worldName == null ? that.worldName == null : this.worldName.equals(that.worldName));
	}

}
