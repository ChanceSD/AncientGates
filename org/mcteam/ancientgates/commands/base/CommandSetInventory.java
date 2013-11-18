package org.mcteam.ancientgates.commands.base;

import org.mcteam.ancientgates.Gate;
import org.mcteam.ancientgates.commands.BaseCommand;
import org.mcteam.ancientgates.util.types.InvBoolean;

public class CommandSetInventory extends BaseCommand {
	
	public CommandSetInventory() {
		aliases.add("setinventory");
		aliases.add("setinv");
		
		requiredParameters.add("id");
		requiredParameters.add("true/false/clear");
		
		requiredPermission = "ancientgates.setinventory";
		
		senderMustBePlayer = false;
		
		helpDescription = "Allow inventory through gate.";
	}
	
	public void perform() {
		InvBoolean extFlag = InvBoolean.fromName(parameters.get(1));
            
		gate.setTeleportInventory(extFlag);
		sendMessage("Inventory teleportation for gate \""+gate.getId()+"\" is now "+parameters.get(1).toUpperCase()+".");
		
		Gate.save();
	}
        
}