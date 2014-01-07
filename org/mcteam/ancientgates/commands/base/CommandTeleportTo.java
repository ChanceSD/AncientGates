package org.mcteam.ancientgates.commands.base;

import org.mcteam.ancientgates.commands.BaseCommand;
import org.mcteam.ancientgates.util.TeleportUtil;
import org.mcteam.ancientgates.util.types.InvBoolean;

public class CommandTeleportTo extends BaseCommand {
	
	public CommandTeleportTo() {
		aliases.add("tpto");
		
		requiredParameters.add("id");
		optionalParameters.add("no");
		
		requiredPermission = "ancientgates.tpto";
		senderMustBePlayer = true;
		
		helpDescription = "Teleport to gate destination";
	}
	
	public void perform() {
		int to = 1;
		if (parameters.size() > 1) {
			try {
				to = Integer.parseInt(parameters.get(1));
			} catch (NumberFormatException e) {
				// wasn't an integer
			}
		}
		
		if (to > 0) to -= 1;
		
		if (gate.getTos() == null && gate.getBungeeTos() == null) {
			player.sendMessage(String.format("This gate does not have a location :P"));
		} else if ((gate.getTos() != null && gate.getTos().size() <= to) || (gate.getBungeeTos() != null && gate.getBungeeTos().size() <= to)) {
			player.sendMessage(String.format("This gate does not have that many to locations :P"));
		} else if (gate.getTos() != null) {
			TeleportUtil.teleportPlayer(player, gate.getTos().get(to), false, InvBoolean.TRUE);
		} else if (gate.getBungeeTos() != null) {
			TeleportUtil.teleportPlayer(player, gate.getBungeeTos().get(to), gate.getBungeeType(), false, InvBoolean.TRUE, true, null, null, null);
		}
	}
	
}
