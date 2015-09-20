package org.mcteam.ancientgates.gson.typeadapters;

import java.lang.reflect.Type;

import org.bukkit.Location;
import org.bukkit.World;
import org.mcteam.ancientgates.Plugin;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class LocationTypeAdapter implements JsonDeserializer<Location>, JsonSerializer<Location> {
	private static final String WORLD = "world";
	private static final String X = "x";
	private static final String Y = "y";
	private static final String Z = "z";
	private static final String YAW = "yaw";
	private static final String PITCH = "pitch";

	@Override
	public Location deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context) throws JsonParseException {
		final JsonObject obj = json.getAsJsonObject();

		Location location = null;
		final World world = this.getWorld(obj.get(WORLD).getAsString());

		if (world != null) {
			final double x = obj.get(X).getAsDouble();
			final double y = obj.get(Y).getAsDouble();
			final double z = obj.get(Z).getAsDouble();
			final float yaw = obj.get(YAW).getAsFloat();
			final float pitch = obj.get(PITCH).getAsFloat();
			location = new Location(world, x, y, z, yaw, pitch);
		}

		return location;
	}

	@Override
	public JsonElement serialize(final Location src, final Type typeOfSrc, final JsonSerializationContext context) {
		final JsonObject obj = new JsonObject();

		if (src == null) {
			Plugin.log("Passed location is null in MyLocationTypeAdapter.");
			return obj;
		} else if (src.getWorld() == null) {
			Plugin.log("Passed location's world is null in MyLocationTypeAdapter.");
			return obj;
		}

		obj.addProperty(WORLD, src.getWorld().getName());
		obj.addProperty(X, src.getX());
		obj.addProperty(Y, src.getY());
		obj.addProperty(Z, src.getZ());
		obj.addProperty(YAW, src.getYaw());
		obj.addProperty(PITCH, src.getPitch());

		return obj;
	}

	private World getWorld(final String name) {
		final World world = Plugin.instance.getServer().getWorld(name);
		return world;
	}

}
