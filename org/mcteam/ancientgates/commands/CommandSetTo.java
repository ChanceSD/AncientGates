package org.mcteam.ancientgates.commands;

import org.mcteam.ancientgates.Gate;

public class CommandSetTo extends BaseCommand {
	
	public CommandSetTo() {
		aliases.add("setto");
		
		requiredParameters.add("id");		
		
		helpDescription = "Set \"to\" to your location.";
	}
	
	public void perform() {
            
		gate.setTo(player.getLocation());
		sendMessage("To location for gate \""+gate.getId()+"\" is now where you stand.");
		
		Gate.save();
	}
        
}

