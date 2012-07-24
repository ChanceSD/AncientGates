package org.mcteam.ancientgates;

import java.lang.reflect.Type;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.mcteam.ancientgates.gson.JsonDeserializationContext;
import org.mcteam.ancientgates.gson.JsonDeserializer;
import org.mcteam.ancientgates.gson.JsonElement;
import org.mcteam.ancientgates.gson.JsonObject;
import org.mcteam.ancientgates.gson.JsonParseException;
import org.mcteam.ancientgates.gson.JsonSerializationContext;
import org.mcteam.ancientgates.gson.JsonSerializer;

public class MyLocationTypeAdapter implements JsonDeserializer<Location>, JsonSerializer<Location> {
	private static final String WORLD = "world";
	private static final String X = "x";
	private static final String Y = "y";
	private static final String Z = "z";
	private static final String YAW = "yaw";
	private static final String PITCH = "pitch";
	
	@Override
	public Location deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		JsonObject obj = json.getAsJsonObject();
		
		World world = this.getWorld(obj.get(WORLD).getAsString());
		double x = obj.get(X).getAsDouble();
		double y = obj.get(Y).getAsDouble();
		double z = obj.get(Z).getAsDouble();
		float yaw = obj.get(YAW).getAsFloat();
		float pitch = obj.get(PITCH).getAsFloat();
		
		return new Location(world, x, y, z, yaw, pitch);
	}

	@Override
	public JsonElement serialize(Location src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject obj = new JsonObject();

		if (src == null)
		{
			Plugin.log("Passed location is null in MyLocationTypeAdapter.");
			return obj;
		}
		else if (src.getWorld() == null)
		{
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
	
	private World getWorld(String name) 
        {
		World world = Plugin.instance.getServer().getWorld(name);
		if (world == null) 
                {
			world = Plugin.instance.getServer().createWorld((WorldCreator.name(name).environment(Environment.NORMAL)));
		}
		return world;
	}
}
