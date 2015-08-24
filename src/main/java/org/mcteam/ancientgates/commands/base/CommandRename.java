package org.mcteam.ancientgates.commands.base;

import org.mcteam.ancientgates.Gate;
import org.mcteam.ancientgates.commands.BaseCommand;

public class CommandRename extends BaseCommand {
	
	public CommandRename() {
		aliases.add("rename");
		
		requiredParameters.add("id");
		requiredParameters.add("newid");
		
		requiredPermission = "ancientgates.rename";
		
		senderMustBePlayer = false;
		
		helpDescription = "Rename a gate";
	}
	
	public void perform() {
		String id = parameters.get(0);
		String newid = parameters.get(1);

		gate.rename(id, newid);
		sendMessage("Gate with id \"" + id + "\" was renamed to \"" + newid + "\".");
		
		Gate.save();
	}
        
}

