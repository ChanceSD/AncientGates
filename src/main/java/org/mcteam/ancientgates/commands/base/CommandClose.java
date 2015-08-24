package org.mcteam.ancientgates.commands.base;

import org.mcteam.ancientgates.Gates;
import org.mcteam.ancientgates.commands.BaseCommand;

public class CommandClose extends BaseCommand {
	
	public CommandClose() {
		aliases.add("close");
		
		requiredParameters.add("id");
		
		requiredPermission = "ancientgates.close";
		
		senderMustBePlayer = false;
		
		helpDescription = "Close that gate";
	}
	
	public void perform() {    
		Gates.close(gate);
		sendMessage("The gate was closed.");
	}
	
}

