package org.mcteam.ancientgates.gson.typeadapters;

import java.lang.reflect.Type;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonDeserializationContext;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonDeserializer;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonElement;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonObject;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonParseException;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonSerializationContext;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonSerializer;
import org.mcteam.ancientgates.Plugin;

public class LocationTypeAdapter implements JsonDeserializer<Location>, JsonSerializer<Location> {
	private static final String WORLD = "world";
	private static final String X = "x";
	private static final String Y = "y";
	private static final String Z = "z";
	private static final String YAW = "yaw";
	private static final String PITCH = "pitch";
	
	@Override
	public Location deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		JsonObject obj = json.getAsJsonObject();
		
		Location location = null;
		World world = this.getWorld(obj.get(WORLD).getAsString());
		
		if (world != null) {
			double x = obj.get(X).getAsDouble();
			double y = obj.get(Y).getAsDouble();
			double z = obj.get(Z).getAsDouble();
			float yaw = obj.get(YAW).getAsFloat();
			float pitch = obj.get(PITCH).getAsFloat();
			location = new Location(world, x, y, z, yaw, pitch);
		}
		
		return location;
	}

	@Override
	public JsonElement serialize(Location src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject obj = new JsonObject();

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
	
	private World getWorld(String name) {
		World world = Plugin.instance.getServer().getWorld(name);
		return world;
    }

}
