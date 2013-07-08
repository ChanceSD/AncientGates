package org.mcteam.ancientgates.commands.bungee;

import org.mcteam.ancientgates.Gate;
import org.mcteam.ancientgates.commands.BaseCommand;

public class CommandSetBungeeType extends BaseCommand {
	
	public CommandSetBungeeType() {
		aliases.add("setbungeetype");
		
		requiredParameters.add("id");
		requiredParameters.add("location/server");
		
		requiredPermission = "ancientgates.setbungeetype";
		
		senderMustBePlayer = false;
		
		helpDescription = "Set BungeeCord teleport type LOCATION or SERVER.";
	}
	
	public void perform() {
		if (gate.getBungeeTo() == null) {
			sendMessage("You can only use this command on a BungeeCord gate.");
			return;
		}
		
		String bungeeType = parameters.get(1).toUpperCase();
		
		if (!bungeeType.equals("LOCATION") && !bungeeType.equals("SERVER")) {
			sendMessage("This is not a valid BungeeCord teleportation type. Valid types:");
			sendMessage("LOCATION, SERVER");
			return;
		}
            
		gate.setBungeeType(bungeeType);
		sendMessage("Bungee teleportation for gate \""+gate.getId()+"\" is now "+String.valueOf(bungeeType)+" based.");
		
		Gate.save();
	}
        
}