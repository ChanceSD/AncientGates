package org.mcteam.ancientgates.commands;

import org.mcteam.ancientgates.Gates;

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

