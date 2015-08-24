package org.mcteam.ancientgates.commands.economy;

import org.mcteam.ancientgates.Gate;
import org.mcteam.ancientgates.commands.BaseCommand;

public class CommandSetCost extends BaseCommand {
	
	public CommandSetCost() {
		aliases.add("setcost");
		
		requiredParameters.add("id");
		requiredParameters.add("cost");
		
		requiredPermission = "ancientgates.setcost";
		
		senderMustBePlayer = false;
		
		helpDescription = "Set \"cost\" for gate";
	}
	
	public void perform() {	
		Double cost = Double.valueOf(parameters.get(1));
            
		gate.setCost(cost);
		sendMessage("Cost for gate \""+gate.getId()+"\" is now "+cost+".");
		
		Gate.save();
	}
        
}