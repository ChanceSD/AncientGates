package org.mcteam.ancientgates.commands;

import org.mcteam.ancientgates.Gate;

public class CommandSetCost extends BaseCommand {
	
	public CommandSetCost() {
		aliases.add("setcost");
		
		requiredParameters.add("id");
		requiredParameters.add("cost");
		
		requiredPermission = "ancientgates.setcost";
		
		senderMustBePlayer = false;
		
		helpDescription = "Set \"cost\" of the gate.";
	}
	
	public void perform() {	
		Double cost = Double.valueOf(parameters.get(1));
            
		gate.setCost(cost);
		sendMessage("Cost for gate \""+gate.getId()+"\" is now "+cost+".");
		
		Gate.save();
	}
        
}