package org.mcteam.ancientgates.commands.base;

import org.mcteam.ancientgates.commands.BaseCommand;
import org.mcteam.ancientgates.util.TeleportUtil;
import org.mcteam.ancientgates.util.types.InvBoolean;

public class CommandTeleportTo extends BaseCommand {
	
	public CommandTeleportTo() {
		aliases.add("tpto");
		
		requiredParameters.add("id");
		
		requiredPermission = "ancientgates.tpto";
		senderMustBePlayer = true;
		
		helpDescription = "Teleport to gate destination";
	}
	
	public void perform() {
		if (gate.getTo() == null && gate.getBungeeTo() == null) {
			player.sendMessage(String.format("This gate does not point anywhere :P"));
		} else if (gate.getTo() != null) {
			TeleportUtil.teleportPlayer(player, gate.getTo(), false, InvBoolean.TRUE);
		} else if (gate.getBungeeTo() != null) {
			TeleportUtil.teleportPlayer(player, gate.getBungeeTo(), gate.getBungeeType(), false, InvBoolean.TRUE, true, null, null, null);
		}
	}
	
}
