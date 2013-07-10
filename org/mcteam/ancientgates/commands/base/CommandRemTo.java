package org.mcteam.ancientgates.commands.base;

import org.bukkit.Location;
import org.mcteam.ancientgates.Gate;
import org.mcteam.ancientgates.commands.BaseCommand;
import org.mcteam.ancientgates.util.GateUtil;
import org.mcteam.ancientgates.util.TeleportUtil;

public class CommandRemTo extends BaseCommand {
	
	public CommandRemTo() {
		aliases.add("remto");
		aliases.add("delto");
		
		requiredParameters.add("id");
		
		requiredPermission = "ancientgates.remto";
		
		helpDescription = "Remove a \"to\" from your location.";
	}
	
	public void perform() {
		if (gate.getTos() == null) {
			sendMessage("This gate needs a \"to\" location. Use:");
			sendMessage(new CommandSetTo().getUseageTemplate(true, true));
			return;		
		}
		if (gate.getTos().size() <= 1) {
			sendMessage("This gate needs multiple \"to\" locations. Use:");
			sendMessage(new CommandAddTo().getUseageTemplate(true, true));
			return;	
		}
            
		// Find the nearest gate based on the player's location
		Location playerLocation = player.getLocation();
		String nearestTo = GateUtil.nearestTo(playerLocation);
		
		if (nearestTo.isEmpty()) {
			sendMessage("No nearby \"to\" location for gate \""+gate.getId()+"\".");
			return;
		}
		
		gate.delTo(TeleportUtil.stringToLocation(nearestTo));
		sendMessage("Nearest \"to\" location for gate \""+gate.getId()+"\" is removed.");
		Gate.save();
	}

}