package org.mcteam.ancientgates.commands.base;

import java.util.Calendar;

import org.mcteam.ancientgates.Plugin;
import org.mcteam.ancientgates.commands.BaseCommand;
import org.mcteam.ancientgates.util.TeleportUtil;
import org.mcteam.ancientgates.util.types.InvBoolean;

public class CommandTeleportFrom extends BaseCommand {
	
	public CommandTeleportFrom() {
		aliases.add("tpfrom");
		aliases.add("tp");
		
		requiredParameters.add("id");
		optionalParameters.add("no");
		
		requiredPermission = "ancientgates.tpfrom";
		senderMustBePlayer = true;
		
		helpDescription = "Teleport to gate location";
	}
	
	public void perform() {
		int from = 1;
		if (parameters.size() > 1) {
			try {
				from = Integer.parseInt(parameters.get(1));
			} catch (NumberFormatException e) {
				// wasn't an integer
			}
		}
		
		if (from > 0) from -= 1;
		
		if (gate.getFroms() == null) {
			player.sendMessage(String.format("This gate does not have a location :P"));
		} else if (gate.getFroms().size() <= from) {
			player.sendMessage(String.format("This gate does not have that many from locations :P"));
		} else {
			TeleportUtil.teleportPlayer(player, gate.getFroms().get(from), false, InvBoolean.TRUE);
			
			Long now = Calendar.getInstance().getTimeInMillis() + 1000;
			Plugin.lastTeleportTime.put(player.getName(), now);
		}
	}
	
}
