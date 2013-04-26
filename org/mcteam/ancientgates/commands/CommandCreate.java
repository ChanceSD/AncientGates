package org.mcteam.ancientgates.commands;

import org.mcteam.ancientgates.Gate;

public class CommandCreate extends BaseCommand {
	public CommandCreate() {
		aliases.add("create");
		aliases.add("new");
		
		requiredParameters.add("id");	
		
		requiredPermission = "ancientgates.create";
		
		senderMustBePlayer = false;
		hasGateParam = false;
		
		helpDescription = "Create a gate";
	}
	
	public void perform() {  
		String id = parameters.get(0);
                
                
		if (Gate.exists(id)) {
			sendMessage("The gate \"" + id + "\" already exists.");
			return;
		}
		
		Gate.create(id);
		sendMessage("Gate with id \"" + id + "\" was created. Now you should:");
		sendMessage(new CommandSetFrom().getUseageTemplate(true, true));
		
		Gate.save();	
	}
        
}

