package org.mcteam.ancientgates.commands;

import org.mcteam.ancientgates.Gate;

public class CommandSetMessage extends BaseCommand {
	
	public CommandSetMessage() {
		aliases.add("setmessage");
		aliases.add("setmsg");
		
		requiredParameters.add("id");
		requiredParameters.add("msg");
		
		requiredPermission = "ancientgates.setmessage";
		
		senderMustBePlayer = false;
		
		helpDescription = "Set teleport \"msg\" for the gate.";
	}
	
	public void perform() {	
		String msg = parameters.get(1);
            
		gate.setMessage(msg);
		sendMessage("Teleport msg for gate \""+gate.getId()+"\" is \""+msg+"\".");
		
		Gate.save();
	}
        
}