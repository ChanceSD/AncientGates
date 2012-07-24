package org.mcteam.ancientgates.commands;

import java.util.ArrayList;
import java.util.List;

import org.mcteam.ancientgates.Conf;
import org.mcteam.ancientgates.Gate;
import org.mcteam.ancientgates.util.TextUtil;

public class CommandList extends BaseCommand {
	
	public CommandList() {
		aliases.add("list");
		aliases.add("ls");
		
		hasGateParam = false;
		
		helpDescription = "Display a list of the gates";
	}
	
	public void perform() {
            
		List<String> ids = new ArrayList<String>();
		
		for (Gate gate : Gate.getAll()) {
			ids.add(Conf.colorAlly + gate.getId());
		}
		
		if (ids.size() == 0) {
			sendMessage("There are no gates yet.");
			return;
		}
		
		sendMessage("There are currently "+ids.size()+" gates on this server: ");
		sendMessage(TextUtil.implode(ids, Conf.colorSystem+", "));
	}
        
	
}

