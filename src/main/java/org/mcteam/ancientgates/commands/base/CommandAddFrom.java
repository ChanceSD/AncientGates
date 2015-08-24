package org.mcteam.ancientgates.commands.base;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.mcteam.ancientgates.Gate;
import org.mcteam.ancientgates.commands.BaseCommand;

public class CommandAddFrom extends BaseCommand {
	
	public CommandAddFrom() {
		aliases.add("addfrom");
		
		requiredParameters.add("id");
		
		requiredPermission = "ancientgates.addfrom";
		
		helpDescription = "Add another \"from\" to your location";
	}
	
	public void perform() {
		if (gate.getFroms() == null) {
			sendMessage("This gate needs an initial \"from\" location. Use:");
			sendMessage(new CommandSetFrom().getUsageTemplate(true, true));
			return;
		}
		if (gate.getFroms().size() < 1) {
			sendMessage("This gate needs an initial \"from\" location. Use:");
			sendMessage(new CommandSetFrom().getUsageTemplate(true, true));
			return;
		}
		
		// The player might stand in a half-block or a sign or whatever
		// Therefore we load some extra locations and blocks
		Block playerBlock = player.getLocation().getBlock();
		Block upBlock = playerBlock.getRelative(BlockFace.UP);
		
		if (playerBlock.getType() == Material.AIR) {
			gate.addFrom(playerBlock.getLocation());
		} else if (upBlock.getType() == Material.AIR) {
			gate.addFrom(upBlock.getLocation());
		} else {
			sendMessage("There is not enough room for a gate to open here");
			return;
		}
		
		sendMessage("Another \"from\" location for gate \""+gate.getId()+"\" is now where you stand.");
		sendMessage("Build a frame around that block and re-issue:");
		sendMessage(new CommandOpen().getUsageTemplate(true, true));
		
		Gate.save();
	}        
	
}

