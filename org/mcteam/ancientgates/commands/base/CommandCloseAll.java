package org.mcteam.ancientgates.commands.base;

import org.mcteam.ancientgates.Gate;
import org.mcteam.ancientgates.Gates;
import org.mcteam.ancientgates.commands.BaseCommand;

public class CommandCloseAll extends BaseCommand {
	
	public CommandCloseAll() {
		aliases.add("closeall");
		
		requiredPermission = "ancientgates.closeall";
		
		senderMustBePlayer = false;
		hasGateParam = false;
		
		helpDescription = "Close all gates";
	}
	
	public void perform() {
		for (Gate gate : Gate.getAll()) {
			Gates.close(gate);
		}
		
		sendMessage("All gates are closed.");
	}
        
}