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
		
		senderMustBePlayer = false;
		hasGateParam = false;
		
		helpDescription = "Display a list of the gates.";
	}
	
	public void perform() {
		List<String> ids = new ArrayList<String>();
		List<String> states = new ArrayList<String>();
		List<String> costs = new ArrayList<String>();
		
		for (Gate gate : Gate.getAll()) {
			ids.add(Conf.colorAlly + gate.getId());
			states.add(Conf.colorAlly + (Gates.isOpen(gate) ? "open" : "closed"));
			if (Conf.useEconomy && Plugin.econ != null) {
				costs.add(Conf.colorAlly + String.valueOf(gate.getCost()));
			}
		}
		
		if (ids.size() == 0) {
			sendMessage("There are no gates yet.");
			return;
		}
		
		sendMessage("There are currently "+ids.size()+" gates on this server: ");
		if (Conf.useEconomy && Plugin.econ != null) {
			sendMessage(TextUtil.implode(ids, states, costs, Conf.colorSystem+", "));
		} else {
			sendMessage(TextUtil.implode(ids, states, Conf.colorSystem+", "));	
		}
	}
	
}

