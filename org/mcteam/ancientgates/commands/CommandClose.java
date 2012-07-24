package org.mcteam.ancientgates.commands;

public class CommandClose extends BaseCommand {
	
	public CommandClose() {
		aliases.add("close");
		
		requiredParameters.add("id");		
		
		helpDescription = "Close that gate";
	}
	
	public void perform() {
           
		gate.close();
		sendMessage("The gate was closed.");
	
        }
}

