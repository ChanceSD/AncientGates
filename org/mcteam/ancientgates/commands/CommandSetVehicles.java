package org.mcteam.ancientgates.commands;

import org.mcteam.ancientgates.Gate;

public class CommandSetVehicles extends BaseCommand {
	
	public CommandSetVehicles() {
		aliases.add("setvehicles");
		
		requiredParameters.add("id");
		requiredParameters.add("true/false");
		
		requiredPermission = "ancientgates.setvehicles";
		
		senderMustBePlayer = false;
		
		helpDescription = "Allow vehicles through gate.";
	}
	
	public void perform() {
		Boolean flag = Boolean.valueOf(parameters.get(1));
            
		gate.setTeleportVehicles(flag);
		sendMessage("Vehicle teleportation for gate \""+gate.getId()+"\" is now "+String.valueOf(flag)+".");
		
		Gate.save();
	}
        
}