package org.mcteam.ancientgates.commands.base;

import org.bukkit.Location;
import org.mcteam.ancientgates.Gate;
import org.mcteam.ancientgates.Gates;
import org.mcteam.ancientgates.commands.BaseCommand;
import org.mcteam.ancientgates.util.GateUtil;
import org.mcteam.ancientgates.util.TeleportUtil;

public class CommandRemFrom extends BaseCommand {
	
	public CommandRemFrom() {
		aliases.add("remfrom");
		aliases.add("delfrom");
		
		requiredParameters.add("id");
		
		requiredPermission = "ancientgates.remfrom";
		
		helpDescription = "Remove a \"from\" at your location";
	}
	
	public void perform() {
		if (gate.getFroms() == null) {
			sendMessage("This gate needs a \"from\" location. Use:");
			sendMessage(new CommandSetFrom().getUsageTemplate(true, true));
			return;		
		}
		if (gate.getFroms().size() <= 1) {
			sendMessage("This gate needs multiple \"from\" locations. Use:");
			sendMessage(new CommandAddFrom().getUsageTemplate(true, true));
			return;	
		}
            
		// Find the nearest gate based on the player's location
		Location playerLocation = player.getLocation();
		String nearestFrom = GateUtil.nearestFrom(playerLocation);
		
		if (nearestFrom.isEmpty()) {
			sendMessage("No nearby \"from\" location for gate \""+gate.getId()+"\".");
			return;
		}
		
		Gates.close(gate);
		gate.delFrom(TeleportUtil.stringToLocation(nearestFrom));
		Gates.open(gate);
		
		sendMessage("Nearest \"from\" location for gate \""+gate.getId()+"\" is removed.");
		Gate.save();
	}

}

