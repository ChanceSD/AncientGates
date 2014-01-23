package org.mcteam.ancientgates.util;

import java.util.HashMap;
import java.util.Map;

import io.github.dsh105.echopet.entity.inanimate.CraftInanimatePet;
import io.github.dsh105.echopet.entity.living.CraftLivingPet;

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
			data += String.valueOf(((LivingEntity)entity).getHealth()) + ",";
			data += String.valueOf(((LivingEntity)entity).getMaxHealth()) + ",";
			data += String.valueOf(((LivingEntity)entity).getCustomName()) + ",";
			if (entity instanceof Animals) {
				data += String.valueOf(((Animals)entity).getAge()) + ",";
				if (entity instanceof Sheep) {
					data += String.valueOf(((Sheep)entity).isSheared()) + ",";
					data += ((Sheep)entity).getColor().name() + ",";
                } else if (entity instanceof Wolf) {
                	data += String.valueOf(((Wolf)entity).isAngry()) + ",";
                	if (((Wolf)entity).isTamed()) {
                		data += ((Tameable)entity).getOwner().getName() + ",";
                		data += String.valueOf(((Wolf)entity).getCollarColor()) + ",";
                	}
                } else if (entity instanceof Ocelot) {
                	if (((Ocelot)entity).isTamed()) {
                		data += ((Tameable)entity).getOwner().getName() + ",";
                		data += String.valueOf(((Ocelot)entity).getCatType().name()) + ",";
                	}
                } else if (entity instanceof Pig) {
                	data += String.valueOf(((Pig)entity).hasSaddle()) + ",";
                } else if (entity instanceof Horse) {
                	data += String.valueOf(((Horse)entity).getVariant().name()) + ",";
                	data += String.valueOf(((Horse)entity).getStyle().name()) + ",";
    				data += String.valueOf(((Horse)entity).getColor().name()) + ",";
    				data += String.valueOf(((Horse)entity).getDomestication()) + ",";
    				data += String.valueOf(((Horse)entity).getMaxDomestication()) + ",";
    				data += String.valueOf(((Horse)entity).getJumpStrength()) + ",";
        			if (((Horse)entity).isTamed()) {
        				data += ((Tameable)entity).getOwner().getName() + ",";
        				data += ItemStackUtil.itemStackToString(((Horse)entity).getInventory().getSaddle()) + ",";
        				data += ItemStackUtil.itemStackToString(((Horse)entity).getInventory().getArmor()) + ",";
        				if (((Horse)entity).isCarryingChest()) {
        					data += ItemStackUtil.itemStackToString(((Horse)entity).getInventory().getContents()) + ",";
        				}
        			}
                }
			} else if (entity instanceof Villager) {
				data += String.valueOf(((Villager)entity).getProfession().name()) + ",";
				data += String.valueOf(((Villager)entity).getAge()) + ",";
			} else if (entity instanceof Creeper) {
				data += String.valueOf(((Creeper)entity).isPowered()) + ",";
			} else if (entity instanceof Slime) {
				data += String.valueOf(((Slime)entity).getSize()) + ",";
			} else if (entity instanceof Skeleton) {
				data += String.valueOf(((Skeleton)entity).getSkeletonType().name()) + ",";
			}
		}

		return data;
	}
	
	public static void setEntityTypeData(Entity entity, String data) {
		if (data == "") return;
		
		String parts[] = data.split(",");
		if ((entity instanceof LivingEntity)) {
			((LivingEntity)entity).setHealth(Double.parseDouble(parts[0]));
			((LivingEntity)entity).setMaxHealth(Double.parseDouble(parts[1]));
			if (!parts[2].equals("null")) ((LivingEntity)entity).setCustomName(parts[2]);
			if ((entity instanceof Animals)) {
				((Animals)entity).setAge(Integer.parseInt(parts[3]));
				if ((entity instanceof Sheep)) {
					((Sheep)entity).setSheared(Boolean.parseBoolean(parts[4]));
					((Sheep)entity).setColor(sheepColors.get(parts[5]));
                } else if ((entity instanceof Wolf)) {
                	if (Boolean.parseBoolean(parts[4])) {
                		((Wolf)entity).setAngry(Boolean.parseBoolean(parts[4]));
                	} else if (parts.length > 5) {
                		((Tameable)entity).setTamed(true);
                		((Tameable)entity).setOwner((AnimalTamer)getPlayer(parts[5]));
                		((Wolf)entity).setCollarColor(DyeColor.valueOf(parts[6]));
                	}
                } else if ((entity instanceof Ocelot)) {
                	if (parts.length > 4) {
                		((Tameable)entity).setTamed(true);
                		((Tameable)entity).setOwner((AnimalTamer)getPlayer(parts[4]));
                		((Ocelot)entity).setCatType(catTypes.get(parts[5]));
                	}
                } else if ((entity instanceof Pig)) {
                	((Pig)entity).setSaddle(Boolean.parseBoolean(parts[4]));
                } else if ((entity instanceof Horse)) {  	
                	((Horse)entity).setVariant(horseVariants.get(parts[4]));
                	((Horse)entity).setStyle(horseStyles.get(parts[5]));
                	((Horse)entity).setColor(horseColors.get(parts[6]));
                	((Horse)entity).setDomestication(Integer.parseInt(parts[7]));
                	((Horse)entity).setMaxDomestication(Integer.parseInt(parts[8]));
                	((Horse)entity).setJumpStrength(Double.parseDouble(parts[9]));
        			if (parts.length > 10) {
                		((Tameable)entity).setTamed(true);
                		((Tameable)entity).setOwner((AnimalTamer)getPlayer(parts[10]));
                		((Horse)entity).getInventory().setSaddle(ItemStackUtil.stringToItemStack(parts[11])[0]);
                		((Horse)entity).getInventory().setArmor(ItemStackUtil.stringToItemStack(parts[12])[0]);
                		if (parts.length > 13) {
                			((Horse)entity).setCarryingChest(true);
                			((Horse)entity).getInventory().setContents(ItemStackUtil.stringToItemStack(parts[13]));
                		}
        			}
                }
			} else if ((entity instanceof Villager)) {
				((Villager)entity).setProfession(villagerProfessions.get(parts[3]));
				((Villager)entity).setAge(Integer.parseInt(parts[4]));
			} else if ((entity instanceof Creeper)) {
				((Creeper)entity).setPowered(Boolean.parseBoolean(parts[3]));
			} else if ((entity instanceof Slime)) {
				((Slime)entity).setSize(Integer.parseInt(parts[3]));
			} else if ((entity instanceof Skeleton)) {
				((Skeleton)entity).setSkeletonType(skeletonTypes.get(parts[3]));
				if (parts[3].equals("0")) {
					((Skeleton)entity).getEquipment().setItemInHand(new ItemStack(Material.BOW));
				} else {
					((Skeleton)entity).getEquipment().setItemInHand(new ItemStack(Material.BOW));
				}
			} else if ((entity instanceof PigZombie)) {
				((LivingEntity)entity).getEquipment().setItemInHand(new ItemStack(Material.GOLD_SWORD));
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
	
	public static boolean isEchoPet(Entity entity) {
		// If EchoPet plugin present, check if entity is a pet
		try { return entity instanceof CraftLivingPet || entity instanceof CraftInanimatePet;
		} catch (NoClassDefFoundError e) { return false; }
	}
	  
}