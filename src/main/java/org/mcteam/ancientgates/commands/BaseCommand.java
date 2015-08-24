package org.mcteam.ancientgates.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.mcteam.ancientgates.Gate;
import org.mcteam.ancientgates.Plugin;
import org.mcteam.ancientgates.Conf;
import org.mcteam.ancientgates.Server;
import org.mcteam.ancientgates.util.TextUtil;

public class BaseCommand {
	
	public List<String> aliases;
	public List<String> requiredParameters;
	public List<String> optionalParameters;
	
	public String requiredPermission;
	public String helpDescription;
	
	public CommandSender sender;
	public boolean senderMustBePlayer;
	public boolean hasServerParam;
	public boolean hasGateParam;
	public Player player;
	public Server server;
	public Gate gate;
	
	public List<String> parameters;
	
	public BaseCommand() {
		aliases = new ArrayList<String>();
		requiredParameters = new ArrayList<String>();
		optionalParameters = new ArrayList<String>();
		
		requiredPermission = "ancientgates.admin";
		
		senderMustBePlayer = true;
		hasServerParam = false;
		hasGateParam = true;
		
		helpDescription = "no description";
	}
	
	public List<String> getAliases() {
		return aliases;
	}
        
	public void execute(CommandSender sender, List<String> parameters) {
		this.sender = sender;
		this.parameters = parameters;
		
		if ( ! validateCall()) {
			return;
		}
		
		if (this.senderMustBePlayer) {
			this.player = (Player)sender;
		}
     
		perform();       
	}
	
	public void perform() {      		
	}
	
	public void sendMessage(String message) {
		sender.sendMessage(Conf.colorSystem+message);
	}
	
	public void sendMessage(List<String> messages) {
		for(String message : messages) {
			this.sendMessage(message);
		}
	}
	
	public boolean validateCall() {   
		if ( this.senderMustBePlayer && ! (sender instanceof Player)) {
			sendMessage("This command can only be used by ingame players.");
			return false;
		}
          
		if( !hasPermission(sender)) {
			// Ignore permissions for "to" on external BungeeCord gates
			if (Conf.bungeeCordSupport && aliases.contains("to") && parameters.size() > 1) return true;
			
			sendMessage("You lack the permissions to "+this.helpDescription.toLowerCase()+".");
			return false;
		}

		if (parameters.size() < requiredParameters.size()) {
			sendMessage("Usage: "+this.getUsageTemplate(true));
			return false;
		}
		
		if (this.hasGateParam) {
			String id = parameters.get(0);
			if (!Gate.exists(id)) {
				// Ignore id for "to" on external BungeeCord gates
				if (Conf.bungeeCordSupport && TextUtil.containsSubString(aliases, "to") && parameters.size() > 1) return true;
		
				sendMessage("There exists no gate with id "+id);
				return false;
			}
			gate = Gate.get(id);
		}
		
		if (this.hasServerParam) {
			String name = parameters.get(0);
			if (!Server.exists(name)) {
				sendMessage("There exists no server with name "+name);
				return false;
			}
			server = Server.get(name);
		}
                
        return true;   
           
	}
	
	public boolean hasPermission(CommandSender sender) {		
		return Plugin.hasPermManage(sender, requiredPermission);
	}
	
	// -------------------------------------------- //
	// Help and usage description
	// -------------------------------------------- //
	public String getUsageTemplate(boolean withColor, boolean withDescription) {
		String ret = "";
		
		if (withColor) {
			ret += Conf.colorCommand;
		}
		
		ret += "/" + Plugin.instance.getBaseCommand()+ " " +TextUtil.implode(this.getAliases(), ",")+" ";
		
		List<String> parts = new ArrayList<String>();
		
		for (String requiredParameter : this.requiredParameters) {
			parts.add("["+requiredParameter+"]");
		}
		
		for (String optionalParameter : this.optionalParameters) {
			parts.add("*["+optionalParameter+"]");
		}
		
		if (withColor) {
			ret += Conf.colorParameter;
		}
		
		ret += TextUtil.implode(parts, " ");
		
		if (withDescription) {
			ret += "  "+Conf.colorSystem + this.helpDescription;
		}
		return ret;
	}
	
	public String getUsageTemplate(boolean withColor) {
		return getUsageTemplate(withColor, false);
	}
	
	public String getUsageTemplate() {
		return getUsageTemplate(true);
	}
	
}
