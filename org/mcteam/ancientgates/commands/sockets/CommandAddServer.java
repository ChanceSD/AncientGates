package org.mcteam.ancientgates.commands.sockets;

import org.mcteam.ancientgates.Server;
import org.mcteam.ancientgates.commands.BaseCommand;

public class CommandAddServer extends BaseCommand {
	
	public CommandAddServer() {           
		aliases.add("addserver");
		
		requiredParameters.add("name");
		requiredParameters.add("address");
		requiredParameters.add("password");
		
		requiredPermission = "ancientgates.addserver";
		
		senderMustBePlayer = false;
		hasServerParam = false;
		hasGateParam = false;
		
		helpDescription = "Add a server";
	}
	
	public void perform() {    
		String name = parameters.get(0);
		String password = parameters.get(2);
		
		if (!parameters.get(1).contains(":")) {
			sendMessage("Incorrect address format. Use [ip]:[port].");
			return;
		}
		
		String parts[] = parameters.get(1).split(":");
		String address = parts[0];
		int port;
		try {
			port = Integer.parseInt(parts[1]);
		} catch(NumberFormatException e) {
			sendMessage("The port must be a number.");
			return;
		}
		
		if (Server.exists(name)) {
			sendMessage("The server \"" + name + "\" has already been added.");
			return;
		}
		
		Server.add(name, address, port, password);
		sendMessage("The server \"" + name + "\" was added, with address \"" + address + "\".");
		
		Server.save();	
	}
        
}