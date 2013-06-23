package org.mcteam.ancientgates.commands.sockets;

import org.mcteam.ancientgates.Server;
import org.mcteam.ancientgates.commands.BaseCommand;

public class CommandRemServer extends BaseCommand {
	public CommandRemServer() {
		aliases.add("remserver");
		aliases.add("delserver");
		
		requiredParameters.add("name");
		
		requiredPermission = "ancientgates.removeserver";
		
		senderMustBePlayer = false;
		hasServerParam = true;
		hasGateParam = false;
		
		helpDescription = "Remove a server";
	}
	
	public void perform() {
		sendMessage("Server \"" + server.getName() + "\" was removed.");
		Server.remove(server.getName());
		Server.save();
    }
	
}

