package org.mcteam.ancientgates.commands.base;

import org.bukkit.Location;
import org.bukkit.Material;
import org.mcteam.ancientgates.Gates;
import org.mcteam.ancientgates.commands.BaseCommand;
import org.mcteam.ancientgates.util.BlockUtil;

public class CommandOpen extends BaseCommand {
	
	public CommandOpen() {
		aliases.add("open");
		
		requiredParameters.add("id");
		
		requiredPermission = "ancientgates.open";
		
		senderMustBePlayer = false;
		
		helpDescription = "Open that gate";
	}
	
	public void perform() { 
		if (gate.getFroms() == null) {
			sendMessage("You must set the from location first. To fix that:");
			sendMessage(new CommandSetFrom().getUsageTemplate(true, true));
			return;
		}
		
		if (gate.getTos() == null && gate.getBungeeTos() == null && gate.getCommand() == null) {
			sendMessage("Sure, but note that this gate does not point anywhere :P");
			sendMessage("To fix that: " + new CommandSetTo().getUsageTemplate(true, true));
		}
		
		for (Location from : gate.getFroms()) {
			if (from.getBlock().getType() != Material.AIR && !BlockUtil.isStandableGateMaterial(from.getBlock().getType())) {
				sendMessage("The gate could not open. The from location is not air.");
				return;
			}
		}
		
		if (Gates.open(gate)) {
			sendMessage("The gate was opened.");
		} else {
			sendMessage("Failed to open the gate. Have you built a frame?");
			sendMessage("More info here: " + new CommandHelp().getUsageTemplate(true, true));
		}
	}
        
}

