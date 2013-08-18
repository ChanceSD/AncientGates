package org.mcteam.ancientgates.commands.base;

import org.mcteam.ancientgates.Gate;
import org.mcteam.ancientgates.commands.BaseCommand;

public class CommandSetExec extends BaseCommand {
	
	public CommandSetExec() {
		aliases.add("setexec");
		
		requiredParameters.add("id");
		requiredParameters.add("command");
		
		requiredPermission = "ancientgates.setexec";
		
		optionalParameters.add("type");
		
		senderMustBePlayer = false;
		
		helpDescription = "Set \"exec\" command for the gate.";
	}
	
	public void perform() {	
		String command = parameters.get(1);
		String commandType = "PLAYER";
		if (parameters.size() > 1) {
			commandType = parameters.get(2);
		}
        
		gate.setCommand(command);
		gate.setCommandType(commandType);
		sendMessage("Command for gate \""+gate.getId()+"\" is now "+command+".");
		
		Gate.save();
	}
        
}