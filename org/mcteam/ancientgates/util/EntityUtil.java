package org.mcteam.ancientgates.util;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Pig;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Skeleton.SkeletonType;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Wolf;
import org.bukkit.inventory.ItemStack;

import org.mcteam.ancientgates.Plugin;

public class EntityUtil {
	
	public static Map<String, EntityType> entityTypes;
	public static Map<String, SkeletonType> skeletonTypes;
	public static Map<String, Ocelot.Type> catTypes;
	
	public static Map<String, Horse.Variant> horseVariants;
	public static Map<String, Horse.Style> horseStyles;
	public static Map<String, Horse.Color> horseColors;

	public static Map<String, Villager.Profession> villagerProfessions;
	
	public static Map<String, DyeColor> sheepColors;
	
	static {
		  entityTypes = new HashMap<String, EntityType>();
		  for (EntityType e : EntityType.values()) {
			  entityTypes.put(e.name(), e);
		  }
	}
	
	static {
		  skeletonTypes = new HashMap<String, SkeletonType>();
		  for (SkeletonType e : SkeletonType.values()) {
			  skeletonTypes.put(e.name(), e);
		  }
	}
	
	static {
		  catTypes = new HashMap<String, Ocelot.Type>();
		  for (Ocelot.Type e : Ocelot.Type.values()) {
			  catTypes.put(e.name(), e);
		  }
	}
	
	static {
		  horseVariants = new HashMap<String, Horse.Variant>();
		  for (Horse.Variant e : Horse.Variant.values()) {
			  horseVariants.put(e.name(), e);
		  }
	}
	
	static {
		  horseStyles = new HashMap<String, Horse.Style>();
		  for (Horse.Style e : Horse.Style.values()) {
			  horseStyles.put(e.name(), e);
		  }
	}
	
	static {
		  horseColors = new HashMap<String, Horse.Color>();
		  for (Horse.Color e : Horse.Color.values()) {
			  horseColors.put(e.name(), e);
		  }
	}
	
	static {
		  villagerProfessions = new HashMap<String, Villager.Profession>();
		  for (Villager.Profession e : Villager.Profession.values()) {
			  villagerProfessions.put(e.name(), e);
		  }
	}
	
	static {
		  sheepColors = new HashMap<String, DyeColor>();
		  for (DyeColor e : DyeColor.values()) {
			  sheepColors.put(e.name(), e);
		  }
	}
	
	public static String getEntityTypeData(Entity entity) {
		String data ="";

		if (entity instanceof LivingEntity) {
			if (entity instanceof Animals) {
				if (entity instanceof Sheep) {
					data += String.valueOf(((Animals)entity).getAge()) + ",";
					data += String.valueOf(((Sheep)entity).isSheared()) + ",";
					data += ((Sheep)entity).getColor().name() + ",";
					data += String.valueOf(((LivingEntity)entity).getCustomName()) + ",";
                } else if (entity instanceof Wolf) {
                	data += String.valueOf(((Animals)entity).getAge()) + ",";
        			data += String.valueOf(((LivingEntity)entity).getCustomName()) + ",";
                	data += String.valueOf(((Wolf)entity).isAngry()) + ",";
                	if (((Wolf)entity).isTamed()) {
                		data += ((Tameable)entity).getOwner().getName() + ",";
                		data += String.valueOf(((Wolf)entity).getCollarColor()) + ",";
                	}
                } else if (entity instanceof Ocelot) {
                	data += String.valueOf(((Animals)entity).getAge()) + ",";
        			data += String.valueOf(((LivingEntity)entity).getCustomName()) + ",";
                	if (((Ocelot)entity).isTamed()) {
                		data += ((Tameable)entity).getOwner().getName() + ",";
                		data += String.valueOf(((Ocelot)entity).getCatType().name()) + ",";
                	}
                } else if (entity instanceof Pig) {
                	data += String.valueOf(((Animals)entity).getAge()) + ",";
                	data += String.valueOf(((Pig)entity).hasSaddle()) + ",";
        			data += String.valueOf(((LivingEntity)entity).getCustomName()) + ",";
                } else if (entity instanceof Horse) {
                	data += String.valueOf(((Animals)entity).getAge()) + ",";
                	data += String.valueOf(((Horse)entity).getVariant().name()) + ",";
                	data += String.valueOf(((Horse)entity).getStyle().name()) + ",";
    				data += String.valueOf(((Horse)entity).getColor().name()) + ",";
    				data += String.valueOf(((Horse)entity).getDomestication()) + ",";
    				data += String.valueOf(((Horse)entity).getJumpStrength()) + ",";
        			data += String.valueOf(((LivingEntity)entity).getCustomName()) + ",";
        			if (((Horse)entity).isTamed()) {
        				data += ((Tameable)entity).getOwner().getName() + ",";
        				data += ItemStackUtil.itemStackToString(((Horse)entity).getInventory().getSaddle()) + ",";
        				data += ItemStackUtil.itemStackToString(((Horse)entity).getInventory().getArmor()) + ",";
        				if (((Horse)entity).isCarryingChest()) {
        					data += ItemStackUtil.itemStackToString(((Horse)entity).getInventory().getContents()) + ",";
        				}
        			}
                } else {
                	data += String.valueOf(((Animals)entity).getAge()) + ",";
        			data += String.valueOf(((LivingEntity)entity).getCustomName()) + ",";
                }
			} else if (entity instanceof Villager) {
				data += String.valueOf(((Villager)entity).getProfession().name()) + ",";
				data += String.valueOf(((Villager)entity).getAge()) + ",";
				data += String.valueOf(((LivingEntity)entity).getCustomName()) + ",";
			} else if (entity instanceof Creeper) {
				data += String.valueOf(((Creeper)entity).isPowered()) + ",";
				data += String.valueOf(((LivingEntity)entity).getCustomName()) + ",";
			} else if (entity instanceof Slime) {
				data += String.valueOf(((Slime)entity).getSize()) + ",";
				data += String.valueOf(((LivingEntity)entity).getCustomName()) + ",";
			} else if (entity instanceof Skeleton) {
				data += String.valueOf(((Skeleton)entity).getSkeletonType().name()) + ",";
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
					((Sheep)entity).setColor(sheepColors.get(parts[2]));
					if (!parts[3].equals("null")) ((LivingEntity)entity).setCustomName(parts[3]);
                } else if ((entity instanceof Wolf)) {
                	((Animals)entity).setAge(Integer.parseInt(parts[0]));
                	if (!parts[1].equals("null")) ((LivingEntity)entity).setCustomName(parts[1]);
                	if (Boolean.parseBoolean(parts[2])) {
                		((Wolf)entity).setAngry(Boolean.parseBoolean(parts[2]));
                	} else if (parts.length > 3) {
                		((Tameable)entity).setTamed(true);
                		((Tameable)entity).setOwner((AnimalTamer)getPlayer(parts[3]));
                		((Wolf)entity).setCollarColor(DyeColor.valueOf(parts[4]));
                	}
                } else if ((entity instanceof Ocelot)) {
            		((Animals)entity).setAge(Integer.parseInt(parts[0]));
            		if (!parts[1].equals("null")) ((LivingEntity)entity).setCustomName(parts[1]);
                	if (parts.length > 2) {
                		((Tameable)entity).setTamed(true);
                		((Tameable)entity).setOwner((AnimalTamer)getPlayer(parts[2]));
                		((Ocelot)entity).setCatType(catTypes.get(parts[3]));
                	}
                } else if ((entity instanceof Pig)) {
                	((Animals)entity).setAge(Integer.parseInt(parts[0]));
                	((Pig)entity).setSaddle(Boolean.parseBoolean(parts[1]));
                	if (!parts[2].equals("null")) ((LivingEntity)entity).setCustomName(parts[2]);
                } else if ((entity instanceof Horse)) {  	
                	((Animals)entity).setAge(Integer.parseInt(parts[0]));
                	((Horse)entity).setVariant(horseVariants.get(parts[1]));
                	((Horse)entity).setStyle(horseStyles.get(parts[2]));
                	((Horse)entity).setColor(horseColors.get(parts[3]));
                	((Horse)entity).setDomestication(Integer.parseInt(parts[4]));
                	((Horse)entity).setJumpStrength(Double.parseDouble(parts[5]));
                	if (!parts[6].equals("null")) ((LivingEntity)entity).setCustomName(parts[6]);
        			if (parts.length > 7) {
                		((Tameable)entity).setTamed(true);
                		((Tameable)entity).setOwner((AnimalTamer)getPlayer(parts[7]));
                		((Horse)entity).getInventory().setSaddle(ItemStackUtil.stringToItemStack(parts[8])[0]);
                		((Horse)entity).getInventory().setArmor(ItemStackUtil.stringToItemStack(parts[9])[0]);
                		if (parts.length > 10) {
                			((Horse)entity).setCarryingChest(true);
                			((Horse)entity).getInventory().setContents(ItemStackUtil.stringToItemStack(parts[10]));
                		}
        			}
                } else {
                	((Animals)entity).setAge(Integer.parseInt(parts[0]));
                	if (!parts[1].equals("null")) ((LivingEntity)entity).setCustomName(parts[1]);
                }
			} else if ((entity instanceof Villager)) {
				((Villager)entity).setProfession(villagerProfessions.get(parts[0]));
				((Villager)entity).setAge(Integer.parseInt(parts[1]));
				if (!parts[2].equals("null")) ((LivingEntity)entity).setCustomName(parts[2]);
			} else if ((entity instanceof Creeper)) {
				((Creeper)entity).setPowered(Boolean.parseBoolean(parts[0]));
				if (!parts[1].equals("null")) ((LivingEntity)entity).setCustomName(parts[1]);
			} else if ((entity instanceof Slime)) {
				((Slime)entity).setSize(Integer.parseInt(parts[0]));
				if (!parts[1].equals("null")) ((LivingEntity)entity).setCustomName(parts[1]);
			} else if ((entity instanceof Skeleton)) {
				((Skeleton)entity).setSkeletonType(skeletonTypes.get(parts[0]));
				if (parts[0].equals("0")) {
					((Skeleton)entity).getEquipment().setItemInHand(new ItemStack(Material.BOW));
				} else {
					((Skeleton)entity).getEquipment().setItemInHand(new ItemStack(Material.BOW));
				}
				if (!parts[1].equals("null")) ((LivingEntity)entity).setCustomName(parts[1]);	
			} else if ((entity instanceof PigZombie)) {
				((LivingEntity)entity).setHealth(Double.parseDouble(parts[0]));
				((LivingEntity)entity).getEquipment().setItemInHand(new ItemStack(Material.GOLD_SWORD));
				if (!parts[1].equals("null")) ((LivingEntity)entity).setCustomName(parts[1]);
			} else {
				((LivingEntity)entity).setHealth(Double.parseDouble(parts[0]));
				if (!parts[1].equals("null")) ((LivingEntity)entity).setCustomName(parts[1]);
			}
		}
	}
	
	public static EntityType entityType(String name) {
		return entityTypes.get(name);
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