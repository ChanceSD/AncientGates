package org.mcteam.ancientgates.commands.base;

import org.mcteam.ancientgates.Gate;
import org.mcteam.ancientgates.commands.BaseCommand;

public class CommandSetExec extends BaseCommand {
	
	public CommandSetExec() {
		aliases.add("setexec");
		
		requiredParameters.add("id");
		requiredParameters.add("type");
		requiredParameters.add("command");
		
		requiredPermission = "ancientgates.setexec";
		
		senderMustBePlayer = false;
		
		helpDescription = "Set \"exec\" command for the gate.";
	}
	
	public void perform() {	
		String command = "";
		String commandType = parameters.get(1).toUpperCase();
		
		if (!commandType.equals("PLAYER") && !commandType.equals("CONSOLE")) {
			sendMessage("This is not a valid command type. Valid types:");
			sendMessage("PLAYER, CONSOLE");
			return;
		}

		parameters.remove(0);
		parameters.remove(0);
		for(String parameter : parameters) {
			command += " " + parameter;
		}
        
		gate.setCommand(command.trim());
		gate.setCommandType(commandType);
		sendMessage("Command for gate \""+gate.getId()+"\" is now /"+command.trim()+".");
		
		Gate.save();
	}
        
}