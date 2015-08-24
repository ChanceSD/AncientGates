package org.mcteam.ancientgates.commands.base;

import java.util.Arrays;

import org.mcteam.ancientgates.Conf;
import org.mcteam.ancientgates.Gate;
import org.mcteam.ancientgates.commands.BaseCommand;
import org.mcteam.ancientgates.util.TextUtil;
import org.mcteam.ancientgates.util.types.CommandType;

public class CommandSetExec extends BaseCommand {
	
	public CommandSetExec() {
		aliases.add("setexec");
		
		requiredParameters.add("id");
		requiredParameters.add("type");
		requiredParameters.add("command");
		
		requiredPermission = "ancientgates.setexec";
		
		senderMustBePlayer = false;
		
		helpDescription = "Set \"exec\" cmd for gate";
	}
	
	public void perform() {	
		String command = "";
		String commandType = parameters.get(1).toUpperCase();
		
		if (CommandType.fromName(commandType) == null) {
			sendMessage("This is not a valid command type. Valid types:");
			sendMessage(TextUtil.implode(Arrays.asList(CommandType.names), Conf.colorSystem+", "));
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