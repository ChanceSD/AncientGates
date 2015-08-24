package org.mcteam.ancientgates.commands.base;

import org.mcteam.ancientgates.Gate;
import org.mcteam.ancientgates.commands.BaseCommand;

public class CommandSetEntities extends BaseCommand {
	
	public CommandSetEntities() {
		aliases.add("setentities");
		
		requiredParameters.add("id");
		requiredParameters.add("true/false");
		
		requiredPermission = "ancientgates.setentities";
		
		senderMustBePlayer = false;
		
		helpDescription = "Allow entities in gate";
	}
	
	public void perform() {
		Boolean flag = Boolean.valueOf(parameters.get(1));
            
		gate.setTeleportEntities(flag);
		sendMessage("Entity teleportation for gate \""+gate.getId()+"\" is now "+String.valueOf(flag)+".");
		
		Gate.save();
	}
        
}