package org.mcteam.ancientgates.commands;

import org.mcteam.ancientgates.Server;

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

