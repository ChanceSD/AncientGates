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
		sendMessage(TextUtil.titleize("AncientGates Help ("+page+"/"+helpPages.size()+")"));
		page -= 1;
		if (page < 0 || page >= helpPages.size()) {
			sendMessage("This page does not exist");
			return;
		}
		sendMessage(helpPages.get(page));
	}
	
	//----------------------------------------------//
	// Build the help pages
	//----------------------------------------------//
	public static ArrayList<ArrayList<String>> helpPages;
	
	static {
		helpPages = new ArrayList<ArrayList<String>>();
		ArrayList<String> pageLines;

		pageLines = new ArrayList<String>();
		pageLines.add( new CommandCreate().getUsageTemplate(true, true) );
		pageLines.add( new CommandSetFrom().getUsageTemplate(true, true) );
		pageLines.add( new CommandSetTo().getUsageTemplate(true, true) );
		if (!Conf.useVanillaPortals) pageLines.add( new CommandSetMaterial().getUsageTemplate(true, true) );
		pageLines.add( new CommandOpen().getUsageTemplate(true, true) );
		pageLines.add( new CommandClose().getUsageTemplate(true, true) );
		pageLines.add( new CommandDelete().getUsageTemplate(true, true) );
		pageLines.add( new CommandRename().getUsageTemplate(true, true) );
		helpPages.add(pageLines);
		
		pageLines = new ArrayList<String>();
		pageLines.add( new CommandOpenAll().getUsageTemplate(true, true) );
		pageLines.add( new CommandCloseAll().getUsageTemplate(true, true) );
		pageLines.add( new CommandSetMessage().getUsageTemplate(true, true) );
		pageLines.add( new CommandSetEntities().getUsageTemplate(true, true) );
		if (!Conf.useVanillaPortals) pageLines.add( new CommandSetVehicles().getUsageTemplate(true, true) );
		if (Conf.useEconomy) pageLines.add( new CommandSetCost().getUsageTemplate(true, true) );
		pageLines.add( new CommandAddFrom().getUsageTemplate(true, true) );
		pageLines.add( new CommandRemFrom().getUsageTemplate(true, true) );
		helpPages.add(pageLines);
		
		pageLines = new ArrayList<String>();
		pageLines.add( new CommandAddTo().getUsageTemplate(true, true) );
		pageLines.add( new CommandRemTo().getUsageTemplate(true, true) );
		pageLines.add( new CommandInfo().getUsageTemplate(true, true) );
		pageLines.add( new CommandList().getUsageTemplate(true, true) );
		pageLines.add( new CommandSetBungeeType().getUsageTemplate(true, true) );
		if (Conf.useSocketComms) {
			pageLines.add( new CommandAddServer().getUsageTemplate(true, true) );
			pageLines.add( new CommandRemServer().getUsageTemplate(true, true) );
			pageLines.add( new CommandServerList().getUsageTemplate(true, true) );
		}
		helpPages.add(pageLines);
	}
	
}

