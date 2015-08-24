package org.mcteam.ancientgates.commands.bungee;

import java.util.Arrays;

import org.mcteam.ancientgates.Conf;
import org.mcteam.ancientgates.Gate;
import org.mcteam.ancientgates.commands.BaseCommand;
import org.mcteam.ancientgates.util.TextUtil;
import org.mcteam.ancientgates.util.types.TeleportType;

public class CommandSetBungeeType extends BaseCommand {
	
	public CommandSetBungeeType() {
		aliases.add("setbungeetype");
		
		requiredParameters.add("id");
		requiredParameters.add("location/server");
		
		requiredPermission = "ancientgates.setbungeetype";
		
		senderMustBePlayer = false;
		
		helpDescription = "Set teleport type";
	}
	
	public void perform() {
		if (gate.getBungeeTos() == null) {
			sendMessage("You can only use this command on a BungeeCord gate.");
			return;
		}
		
		String bungeeType = parameters.get(1).toUpperCase();
		
		if (TeleportType.fromName(bungeeType) == null) {
			sendMessage("This is not a valid BungeeCord teleportation type. Valid types:");
			sendMessage(TextUtil.implode(Arrays.asList(TeleportType.names), Conf.colorSystem+", "));
			return;
		}
            
		gate.setBungeeType(bungeeType);
		sendMessage("Bungee teleportation for gate \""+gate.getId()+"\" is now "+String.valueOf(bungeeType)+" based.");
		
		Gate.save();
	}
        
}