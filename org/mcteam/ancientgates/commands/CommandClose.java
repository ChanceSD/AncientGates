package org.mcteam.ancientgates.commands;

public class CommandClose extends BaseCommand {
	
	public CommandClose() {
		aliases.add("close");
		
		requiredParameters.add("id");
		
		requiredPermission = "ancientgates.close";
		
		senderMustBePlayer = false;
		
		helpDescription = "Close that gate";
	}
	
	public void perform() {    
		gate.close();
		sendMessage("The gate was closed.");
	}
	
}

