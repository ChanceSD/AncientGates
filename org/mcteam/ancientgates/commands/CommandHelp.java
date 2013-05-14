package org.mcteam.ancientgates.commands;

import java.util.ArrayList;

import org.mcteam.ancientgates.Conf;
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
		pageLines.add( new CommandHelp().getUseageTemplate(true, true) );
		pageLines.add( new CommandCreate().getUseageTemplate(true, true) );
		pageLines.add( new CommandDelete().getUseageTemplate(true, true) );
		pageLines.add( new CommandSetFrom().getUseageTemplate(true, true) );
		pageLines.add( new CommandSetTo().getUseageTemplate(true, true) );
		pageLines.add( new CommandOpen().getUseageTemplate(true, true) );
		pageLines.add( new CommandClose().getUseageTemplate(true, true) );
		pageLines.add( new CommandRename().getUseageTemplate(true, true) );
		if (Conf.useEconomy) {
			pageLines.add( new CommandSetCost().getUseageTemplate(true, true) );
		}
		pageLines.add( new CommandSetEntities().getUseageTemplate(true, true) );
		if (Conf.useInstantNether) {
			pageLines.add( new CommandSetVehicles().getUseageTemplate(true, true) );
		}
		pageLines.add( new CommandAddFrom().getUseageTemplate(true, true) );
		pageLines.add( new CommandRemFrom().getUseageTemplate(true, true) );
		pageLines.add( new CommandInfo().getUseageTemplate(true, true) );
		pageLines.add( new CommandList().getUseageTemplate(true, true) );
		if (Conf.useSocketComms) {
			pageLines.add( new CommandAddServer().getUseageTemplate(true, true) );
			pageLines.add( new CommandRemServer().getUseageTemplate(true, true) );
			pageLines.add( new CommandServerList().getUseageTemplate(true, true) );
		}
		helpPages.add(pageLines);
	}
	
}

