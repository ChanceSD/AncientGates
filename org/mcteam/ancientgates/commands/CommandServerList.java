package org.mcteam.ancientgates.commands;

import java.util.ArrayList;
import java.util.List;

import org.mcteam.ancientgates.Conf;
import org.mcteam.ancientgates.Server;
import org.mcteam.ancientgates.util.TextUtil;

public class CommandServerList extends BaseCommand {
	
	public CommandServerList() {
		aliases.add("serverlist");
		aliases.add("serverls");
		
		requiredPermission = "ancientgates.serverlist";
		
		senderMustBePlayer = false;
		hasGateParam = false;
		
		helpDescription = "Display a list of the servers";
	}
	
	public void perform() {
		List<String> names = new ArrayList<String>();
		
		for (Server server : Server.getAll()) {
			names.add(Conf.colorAlly + server.getName());
		}
		
		if (names.size() == 0) {
			sendMessage("There are no known servers yet.");
			return;
		}
		
		sendMessage("There are currently "+names.size()+" server(s) known to this server: ");
		sendMessage(TextUtil.implode(names, Conf.colorSystem+", "));
	}     
	
}