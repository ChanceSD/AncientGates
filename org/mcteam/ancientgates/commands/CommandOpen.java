package org.mcteam.ancientgates.commands;

import org.bukkit.Location;
import org.bukkit.Material;

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
			sendMessage(new CommandSetFrom().getUseageTemplate(true, true));
			return;
		}
		
		if (gate.getTo() == null && gate.getBungeeTo() == null) {
			sendMessage("Sure, but note that this gate does not point anywhere :P");
			sendMessage("To fix that: " + new CommandSetTo().getUseageTemplate(true, true));
		}
		
		for (Location from : gate.getFroms()) {
			if (from.getBlock().getType() != Material.AIR && from.getBlock().getType() != Material.PORTAL) {
				sendMessage("The gate could not open. The from location is not air.");
				return;
			}
		}
		
		if (gate.open()) {
			sendMessage("The gate was opened.");
		} else {
			sendMessage("Failed to open the gate. Have you built a frame?");
			sendMessage("More info here: " + new CommandHelp().getUseageTemplate(true, true));
		}
	}
        
}

