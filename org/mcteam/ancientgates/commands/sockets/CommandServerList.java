package org.mcteam.ancientgates.commands.sockets;

import java.util.ArrayList;
import java.util.List;

import org.mcteam.ancientgates.Conf;
import org.mcteam.ancientgates.Server;
import org.mcteam.ancientgates.commands.BaseCommand;
import org.mcteam.ancientgates.util.TextUtil;

public class CommandServerList extends BaseCommand {
	
	public CommandServerList() {
		aliases.add("serverlist");
		aliases.add("serverls");
		
		requiredPermission = "ancientgates.serverlist";
		
		optionalParameters.add("page");
		
		senderMustBePlayer = false;
		hasGateParam = false;
		
		helpDescription = "Display a list of the servers";
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
		
		List<String> names = new ArrayList<String>();
		for (Server server : Server.getAll()) {
			names.add(Conf.colorValue + server.getName());
		}
		
		if (names.size() == 0) {
			sendMessage("There are no known servers yet.");
			return;
		}
		
		// Send list as readable pages
		sendMessage(TextUtil.getPage(names, page, "Server List - "+names.size()+" server(s) -", sender));
	}     
	
}