package org.mcteam.ancientgates.commands.base;

import java.util.ArrayList;

import org.mcteam.ancientgates.Conf;
import org.mcteam.ancientgates.commands.BaseCommand;
import org.mcteam.ancientgates.commands.bungee.CommandSetBungeeType;
import org.mcteam.ancientgates.commands.economy.CommandSetCost;
import org.mcteam.ancientgates.commands.sockets.CommandAddServer;
import org.mcteam.ancientgates.commands.sockets.CommandRemServer;
import org.mcteam.ancientgates.commands.sockets.CommandServerList;
//import org.bukkit.command.CommandSender;
import org.mcteam.ancientgates.util.TextUtil;

public class CommandHelp extends BaseCommand {
	
	public CommandHelp() {
		aliases.add("help");
		aliases.add("h");
		aliases.add("?");
		
		requiredPermission = "ancientgates.help";
		
		optionalParameters.add("page");

		senderMustBePlayer = false;
		hasGateParam = false;
		
		helpDescription = "Display a help page";
	}
	
	public void perform() {
		int page = 1;
		if (parameters.size() > 0) {
			try {
				page = Integer.parseInt(parameters.get(0));
			} catch (NumberFormatException e) {
				// wasn't an integer
			}
		}

		// Send lines as readable pages
		sendMessage(TextUtil.getPage(lines, page, "AncientGates Help", sender));
	}
	
	//----------------------------------------------//
	// Build the help page as lines
	//----------------------------------------------//
	public static ArrayList<String> lines;
	
	static {
		lines = new ArrayList<String>();
		lines.add( new CommandCreate().getUsageTemplate(true, true) );
		lines.add( new CommandSetFrom().getUsageTemplate(true, true) );
		lines.add( new CommandSetTo().getUsageTemplate(true, true) );
		if (!Conf.useVanillaPortals) lines.add( new CommandSetMaterial().getUsageTemplate(true, true) );
		lines.add( new CommandOpen().getUsageTemplate(true, true) );
		lines.add( new CommandClose().getUsageTemplate(true, true) );
		lines.add( new CommandDelete().getUsageTemplate(true, true) );
		lines.add( new CommandRename().getUsageTemplate(true, true) );
		lines.add( new CommandOpenAll().getUsageTemplate(true, true) );
		lines.add( new CommandCloseAll().getUsageTemplate(true, true) );
		lines.add( new CommandSetMessage().getUsageTemplate(true, true) );
		lines.add( new CommandSetExec().getUsageTemplate(true, true) );
		lines.add( new CommandRemExec().getUsageTemplate(true, true) );
		lines.add( new CommandSetEntities().getUsageTemplate(true, true) );
		if (!Conf.useVanillaPortals) lines.add( new CommandSetVehicles().getUsageTemplate(true, true) );
		lines.add( new CommandSetInventory().getUsageTemplate(true, true) );
		if (Conf.useEconomy) lines.add( new CommandSetCost().getUsageTemplate(true, true) );
		lines.add( new CommandTeleportFrom().getUsageTemplate(true, true) );
		lines.add( new CommandTeleportTo().getUsageTemplate(true, true) );
		lines.add( new CommandAddFrom().getUsageTemplate(true, true) );
		lines.add( new CommandRemFrom().getUsageTemplate(true, true) );
		lines.add( new CommandAddTo().getUsageTemplate(true, true) );
		lines.add( new CommandRemTo().getUsageTemplate(true, true) );
		lines.add( new CommandInfo().getUsageTemplate(true, true) );
		lines.add( new CommandList().getUsageTemplate(true, true) );
		lines.add( new CommandSetConf().getUsageTemplate(true, true) );
		lines.add( new CommandSetBungeeType().getUsageTemplate(true, true) );
		if (Conf.useSocketComms) {
			lines.add( new CommandAddServer().getUsageTemplate(true, true) );
			lines.add( new CommandRemServer().getUsageTemplate(true, true) );
			lines.add( new CommandServerList().getUsageTemplate(true, true) );
		}
	}
	
}

