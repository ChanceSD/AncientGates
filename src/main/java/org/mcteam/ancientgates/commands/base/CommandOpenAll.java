package org.mcteam.ancientgates.commands.base;

import org.mcteam.ancientgates.Gate;
import org.mcteam.ancientgates.Gates;
import org.mcteam.ancientgates.commands.BaseCommand;

public class CommandOpenAll extends BaseCommand {
	
	public CommandOpenAll() {
		aliases.add("openall");
		
		requiredPermission = "ancientgates.openall";
		
		senderMustBePlayer = false;
		hasGateParam = false;
		
		helpDescription = "Open all gates";
	}
	
	public void perform() {
		int numFails = 0;
		
		for (Gate gate : Gate.getAll()) {
			if (gate.getFroms() == null) continue;
			
			if (!Gates.open(gate)) numFails++;
		}
		
		if (numFails == 0) {
			sendMessage("All gates have been opened.");
		} else {
			sendMessage("Failed to open " + numFails + " gate(s). Have you built all frames?");
			sendMessage("More info here: " + new CommandHelp().getUsageTemplate(true, true));
		}
	}
        
}