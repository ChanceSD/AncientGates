package org.mcteam.ancientgates.util;

import org.bukkit.DyeColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Wolf;
import org.mcteam.ancientgates.Plugin;

public class EntityUtil {
	
	public static String getEntityTypeData(Entity entity) {
		String data ="";

		if ((entity instanceof LivingEntity)) {
			if ((entity instanceof Animals)) {
				if ((entity instanceof Sheep)) {
					data += String.valueOf(((Animals)entity).getAge()) + ",";
					data += String.valueOf(((Sheep)entity).isSheared()) + ",";
					data += ((Sheep)entity).getColor().name() + ",";
					data += String.valueOf(((LivingEntity)entity).getCustomName()) + ",";
                } else if ((entity instanceof Wolf)) {
                	data += String.valueOf(((Wolf)entity).isAngry()) + ",";
                	data += String.valueOf(((Animals)entity).getAge()) + ",";
        			data += String.valueOf(((LivingEntity)entity).getCustomName()) + ",";
                	if (((Wolf) entity).isTamed()) {
                		data += ((Tameable)entity).getOwner().getName() + ",";
                		data += String.valueOf(((Wolf)entity).getCollarColor()) + ",";
                	}
                } else if ((entity instanceof Ocelot)) {
                	data += String.valueOf(((Animals)entity).getAge()) + ",";
        			data += String.valueOf(((LivingEntity)entity).getCustomName()) + ",";
                	if (((Ocelot) entity).isTamed()) {
                		data += ((Tameable)entity).getOwner().getName() + ",";
                		data += String.valueOf(((Ocelot)entity).getCatType().getId()) + ",";
                	}
                } else if ((entity instanceof Pig)) {
                	data += String.valueOf(((Animals)entity).getAge()) + ",";
                	data += String.valueOf(((Pig)entity).hasSaddle()) + ",";
        			data += String.valueOf(((LivingEntity)entity).getCustomName()) + ",";
                } else {
                	data += String.valueOf(((Animals)entity).getAge()) + ",";
        			data += String.valueOf(((LivingEntity)entity).getCustomName()) + ",";
                }
			} else if ((entity instanceof Villager)) {
				data += String.valueOf(((Villager)entity).getProfession().getId()) + ",";
				data += String.valueOf(((Villager)entity).getAge()) + ",";
				data += String.valueOf(((LivingEntity)entity).getCustomName()) + ",";
			} else if ((entity instanceof Creeper)) {
				data += String.valueOf(((Creeper)entity).isPowered()) + ",";
				data += String.valueOf(((LivingEntity)entity).getCustomName()) + ",";
			} else if ((entity instanceof Slime)) {
				data += String.valueOf(((Slime)entity).getSize()) + ",";
				data += String.valueOf(((LivingEntity)entity).getCustomName()) + ",";
			} else if ((entity instanceof Skeleton)) {
				data += String.valueOf(((Skeleton)entity).getSkeletonType().getId()) + ",";
				data += String.valueOf(((LivingEntity)entity).getCustomName()) + ",";
			} else {
				data += String.valueOf(((LivingEntity)entity).getHealth()) + ",";
				data += String.valueOf(((LivingEntity)entity).getCustomName()) + ",";
			}
		}

		return data;
	}
	
	public static void setEntityTypeData(Entity entity, String data) {
		if (data == "") return;
		
		String parts[] = data.split(",");
		if ((entity instanceof LivingEntity)) {
			if ((entity instanceof Animals)) {
				if ((entity instanceof Sheep)) {
					((Animals)entity).setAge(Integer.parseInt(parts[0]));
					((Sheep)entity).setSheared(Boolean.parseBoolean(parts[1]));
					((Sheep)entity).setColor(sheepColor(parts[2]));
					if (!parts[3].equals("null")) ((LivingEntity)entity).setCustomName(parts[3]);
                } else if ((entity instanceof Wolf)) {
                	if (Boolean.parseBoolean(parts[0])) {
                		((Wolf)entity).setAngry(Boolean.parseBoolean(parts[0]));
                	} else if (!parts[2].isEmpty()) {
                		((Animals)entity).setAge(Integer.parseInt(parts[1]));
                		((Tameable)entity).setOwner((AnimalTamer)getPlayer(parts[2]));
                		if (!parts[3].isEmpty()) ((Wolf)entity).setCollarColor(DyeColor.valueOf(parts[3]));
                	} else {
                		((Animals)entity).setAge(Integer.parseInt(parts[1]));
                	}
                	if (!parts[1].equals("null")) ((LivingEntity)entity).setCustomName(parts[1]);
                } else if ((entity instanceof Ocelot)) {
            		((Animals)entity).setAge(Integer.parseInt(parts[0]));
            		if (!parts[1].equals("null")) ((LivingEntity)entity).setCustomName(parts[1]);
                	if (!parts[2].isEmpty()) {
                		((Tameable)entity).setOwner((AnimalTamer)getPlayer(parts[2]));
                		((Ocelot)entity).setCatType(catType(Integer.parseInt(parts[3])));
                	}
                } else if ((entity instanceof Pig)) {
                	((Animals)entity).setAge(Integer.parseInt(parts[0]));
                	((Pig)entity).setSaddle(Boolean.parseBoolean(parts[1]));
                	if (!parts[2].equals("null")) ((LivingEntity)entity).setCustomName(parts[2]);
                } else {
                	((Animals)entity).setAge(Integer.parseInt(parts[0]));
                	if (!parts[1].equals("null")) ((LivingEntity)entity).setCustomName(parts[1]);
                }
			} else if ((entity instanceof Villager)) {
				((Villager)entity).setProfession(assignProf(Integer.parseInt(parts[0])));
				((Villager)entity).setAge(Integer.parseInt(parts[1]));
				if (!parts[2].equals("null")) ((LivingEntity)entity).setCustomName(parts[2]);
			} else if ((entity instanceof Creeper)) {
				((Creeper)entity).setPowered(Boolean.parseBoolean(parts[0]));
				if (!parts[1].equals("null")) ((LivingEntity)entity).setCustomName(parts[1]);
			} else if ((entity instanceof Slime)) {
				((Slime)entity).setSize(Integer.parseInt(parts[0]));
				if (!parts[1].equals("null")) ((LivingEntity)entity).setCustomName(parts[1]);
			} else if ((entity instanceof Skeleton)) {
				((Skeleton)entity).setSkeletonType(Skeleton.SkeletonType.getType(Integer.parseInt(parts[0])));
				if (!parts[1].equals("null")) ((LivingEntity)entity).setCustomName(parts[1]);
			} else {
				((LivingEntity)entity).setHealth(Double.parseDouble(parts[0]));
				if (!parts[1].equals("null")) ((LivingEntity)entity).setCustomName(parts[1]);
			}
		}
	}
	
	public static Ocelot.Type catType(int i) {
		Ocelot.Type type = null;
		if (i == 0)
			type = Ocelot.Type.WILD_OCELOT;
		else if (i == 1)
			type = Ocelot.Type.BLACK_CAT;
		else if (i == 2)
			type = Ocelot.Type.RED_CAT;
		else if (i == 3) {
			type = Ocelot.Type.SIAMESE_CAT;
		}
	    return type;
	}
	
	public static Villager.Profession assignProf(int i) {
		Villager.Profession prof = null;
	    if (i == 0)
	    	prof = Villager.Profession.FARMER;
	    else if (i == 1)
	    	prof = Villager.Profession.LIBRARIAN;
	    else if (i == 2)
	    	prof = Villager.Profession.PRIEST;
	    else if (i == 3)
	    	prof = Villager.Profession.BLACKSMITH;
	    else if (i == 4) {
	    	prof = Villager.Profession.BUTCHER;
	    }
	    return prof;
	}
	
	public static DyeColor sheepColor(String color) {
		  if (color.equalsIgnoreCase("white"))
		    	return DyeColor.WHITE;
		    if (color.equalsIgnoreCase("black"))
		    	return DyeColor.BLACK;
		    if (color.equalsIgnoreCase("blue"))
		    	return DyeColor.BLUE;
		    if (color.equalsIgnoreCase("brown"))
		    	return DyeColor.BROWN;
		    if (color.equalsIgnoreCase("cyan"))
		    	return DyeColor.CYAN;
		    if (color.equalsIgnoreCase("grey"))
		    	return DyeColor.GRAY;
		    if (color.equalsIgnoreCase("green"))
		    	return DyeColor.GREEN;
		    if (color.equalsIgnoreCase("light_blue"))
		    	return DyeColor.LIGHT_BLUE;
		    if (color.equalsIgnoreCase("lime"))
		    	return DyeColor.LIME;
		    if (color.equalsIgnoreCase("magenta"))
		    	return DyeColor.MAGENTA;
		    if (color.equalsIgnoreCase("orange"))
		    	return DyeColor.ORANGE;
		    if (color.equalsIgnoreCase("pink"))
		    	return DyeColor.PINK;
		    if (color.equalsIgnoreCase("purple"))
		    	return DyeColor.PURPLE;
		    if (color.equalsIgnoreCase("red"))
		    	return DyeColor.RED;
		    if (color.equalsIgnoreCase("silver"))
		    	return DyeColor.SILVER;
		    if (color.equalsIgnoreCase("yellow")) {
		    	return DyeColor.YELLOW;
		    }
		    return DyeColor.WHITE;
	  }
	
	public static OfflinePlayer getPlayer(String name) {
		OfflinePlayer player;
		if(Plugin.instance.getServer().getPlayer(name) != null) {
		    player = Plugin.instance.getServer().getPlayer(name);
		} else {
		    player = Plugin.instance.getServer().getOfflinePlayer(name);
		    if(!player.hasPlayedBefore()) {
		    	return null;
		    }
		}
		return player;
	}
	  
}