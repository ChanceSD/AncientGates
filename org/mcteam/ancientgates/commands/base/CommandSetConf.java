package org.mcteam.ancientgates.commands.base;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.bukkit.ChatColor;
import org.mcteam.ancientgates.Conf;
import org.mcteam.ancientgates.commands.BaseCommand;
import org.mcteam.ancientgates.util.TextUtil;

public class CommandSetConf extends BaseCommand {
	
	public CommandSetConf() {
		aliases.add("setconf");
		
		requiredParameters.add("option");
		requiredParameters.add("value");
		
		requiredPermission = "ancientgates.setconf";
		
		senderMustBePlayer = false;
		hasGateParam = false;
		
		helpDescription = "Set config option to value.";
	}
	
	public void perform() {
		Field[] fields = Conf.class.getDeclaredFields();
		for (Field f : fields) {
			f.setAccessible(true); // Modify private fields
		    if (Modifier.isStatic(f.getModifiers()) && parameters.get(0).equalsIgnoreCase(f.getName())) {
		        try {
		        	if (TextUtil.isBoolean(parameters.get(1))) {
		        		f.set(null, Boolean.parseBoolean(parameters.get(1)));
		        	} else if (TextUtil.isInteger(parameters.get(1))) {
		        		f.set(null, Integer.valueOf(parameters.get(1)));
		        	} else if (ChatColor.valueOf(parameters.get(1)) != null) {
		        		f.set(null, ChatColor.valueOf(parameters.get(1)));
		        	} else {
		        		f.set(null, parameters.get(1));
		        	}
				} catch (IllegalArgumentException e) {
					sendMessage("Config option \""+f.getName()+"\" does not accept the value \""+parameters.get(1)+"\".");
					return;
				} catch (IllegalAccessException e) {
					continue;
				}
		        sendMessage("Config option \""+f.getName()+"\" is now "+parameters.get(1)+".");
		        Conf.save();
		        return;
		    } 
		}

		sendMessage("Config option \""+parameters.get(0)+"\" does not exist.");
	}
        
}