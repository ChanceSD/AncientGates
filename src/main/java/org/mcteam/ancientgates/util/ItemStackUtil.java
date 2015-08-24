package org.mcteam.ancientgates.util;

import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
 
public class ItemStackUtil {
	
	public static String itemStackToString(ItemStack[] itemStack) {
        String serialization = itemStack.length + ";";
        for (int i = 0; i < itemStack.length; i++) {
            ItemStack is = itemStack[i];
            if (is != null) {
                String serializedItemStack = new String();
               
				String isType = is.getType().name();
                serializedItemStack += "t@" + isType;
               
                if (is.getDurability() != 0) {
                    String isDurability = String.valueOf(is.getDurability());
                    serializedItemStack += ":d@" + isDurability;
                }
               
                if (is.getAmount() != 1) {
                    String isAmount = String.valueOf(is.getAmount());
                    serializedItemStack += ":a@" + isAmount;
                }
               
                Map<Enchantment,Integer> isEnch = is.getEnchantments();
                if (isEnch.size() > 0) {
                    for (Entry<Enchantment,Integer> ench : isEnch.entrySet()) {
                        serializedItemStack += ":e@" + ench.getKey().getName() + "@" + ench.getValue();
                    }
                }
               
                serialization += i + "#" + serializedItemStack + ";";
            }
        }
        return serialization;
        
    }
    
    public static String itemStackToString(ItemStack itemStack) {
        return itemStackToString(new ItemStack[]{itemStack});
    }
   
	public static ItemStack[] stringToItemStack(String itemStackString) {
        String[] serializedBlocks = itemStackString.split(";");
        String isInfo = serializedBlocks[0];
        ItemStack[] deserializedItemStack = new ItemStack[Integer.valueOf(isInfo)];
       
        for (int i = 1; i < serializedBlocks.length; i++) {
            String[] serializedBlock = serializedBlocks[i].split("#");
            int stackPosition = Integer.valueOf(serializedBlock[0]);
           
            if (stackPosition >= deserializedItemStack.length) {
                continue;
            }
           
            ItemStack is = null;
            Boolean createdItemStack = false;
           
            String[] serializedItemStack = serializedBlock[1].split(":");
            for (String itemInfo : serializedItemStack) {
                String[] itemAttribute = itemInfo.split("@");
                if (itemAttribute[0].equals("t")) {
                    is = new ItemStack(Material.getMaterial(itemAttribute[1]));
                    createdItemStack = true;
                } else if (itemAttribute[0].equals("d") && createdItemStack) {
                    is.setDurability(Short.valueOf(itemAttribute[1]));
                } else if (itemAttribute[0].equals("a") && createdItemStack) {
                    is.setAmount(Integer.valueOf(itemAttribute[1]));
                } else if (itemAttribute[0].equals("e") && createdItemStack) {
                    is.addEnchantment(Enchantment.getByName(itemAttribute[1]), Integer.valueOf(itemAttribute[2]));
                }
            }
            deserializedItemStack[i-1] = new ItemStack(is);
        }
        return deserializedItemStack;
        
    }
    
}