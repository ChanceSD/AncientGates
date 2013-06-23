package org.mcteam.ancientgates.commands.base;

import org.mcteam.ancientgates.Gate;
import org.mcteam.ancientgates.Gates;
import org.mcteam.ancientgates.commands.BaseCommand;

public class CommandDelete extends BaseCommand {
	public CommandDelete() {
		aliases.add("delete");
		aliases.add("del");
		aliases.add("remove");
		
		requiredParameters.add("id");
		
		requiredPermission = "ancientgates.delete";
		
		senderMustBePlayer = false;
		helpDescription = "Delete a gate";
	}
	
	public void perform() { 
		if (gate.getFroms() != null) Gates.close(gate);
		
		sendMessage("Gate with id \"" + gate.getId() + "\" was deleted.");
		Gate.delete(gate.getId());
		Gate.save();
	}
	
}

