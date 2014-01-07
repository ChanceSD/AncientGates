package org.mcteam.ancientgates.commands.base;

import java.util.ArrayList;
import java.util.List;

import org.mcteam.ancientgates.Conf;
import org.mcteam.ancientgates.Gate;
import org.mcteam.ancientgates.Gates;
import org.mcteam.ancientgates.Plugin;
import org.mcteam.ancientgates.commands.BaseCommand;
import org.mcteam.ancientgates.util.TextUtil;

public class CommandList extends BaseCommand {
	
	public CommandList() {
		aliases.add("list");
		aliases.add("ls");
		
		requiredPermission = "ancientgates.list";
		
		optionalParameters.add("page");
		
		senderMustBePlayer = false;
		hasGateParam = false;
		
		helpDescription = "Display list of the gates";
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
		
		List<String> ids = new ArrayList<String>();
		List<String> states = new ArrayList<String>();
		List<String> costs = new ArrayList<String>();
		for (Gate gate : Gate.getAll()) {
			ids.add(Conf.colorValue + gate.getId());
			states.add((Gates.isOpen(gate) ? Conf.colorCommand+"open" : Conf.colorParameter+"closed"));
			if (Conf.useEconomy && Plugin.econ != null) {
				costs.add(Conf.colorSystem + String.valueOf(gate.getCost()));
			}
		}
		
		if (ids.size() == 0) {
			sendMessage("There are no gates yet.");
			return;
		}

		// Send list as readable pages
		if (Conf.useEconomy && Plugin.econ != null) {
			sendMessage(TextUtil.getPage(TextUtil.concatenate(ids, states, costs), page, "Gate List - "+ids.size()+" gates(s) -", sender));
		} else {
			sendMessage(TextUtil.getPage(TextUtil.concatenate(ids, states), page, "Gate List - "+ids.size()+" gates(s) -", sender));	
		}
	}
	
}

