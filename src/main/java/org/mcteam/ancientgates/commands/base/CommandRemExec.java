package org.mcteam.ancientgates.commands.base;

import org.mcteam.ancientgates.Gate;
import org.mcteam.ancientgates.commands.BaseCommand;

public class CommandRemExec extends BaseCommand {
	
	public CommandRemExec() {
		aliases.add("remexec");
		
		requiredParameters.add("id");
		
		requiredPermission = "ancientgates.remexec";
		
		senderMustBePlayer = false;
		
		helpDescription = "Remove \"exec\" cmd for gate";
	}
	
	public void perform() {	
        
		gate.setCommand("");
		sendMessage("Command for gate \""+gate.getId()+"\" is removed.");
		
		Gate.save();
	}
        
}