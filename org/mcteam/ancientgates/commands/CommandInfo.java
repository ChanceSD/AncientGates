package org.mcteam.ancientgates.commands;

import org.bukkit.Location;
import org.bukkit.Material;
import org.mcteam.ancientgates.Conf;
import org.mcteam.ancientgates.Gate;
import org.mcteam.ancientgates.util.GateUtil;
import org.mcteam.ancientgates.util.GeometryUtil;
import org.mcteam.ancientgates.util.TeleportUtil;
import org.mcteam.ancientgates.util.TextUtil;

public class CommandInfo extends BaseCommand {
	
	private static final String SERVER = "server";
	private static final String WORLD = "world";
	private static final String X = "x";
	private static final String Y = "y";
	private static final String Z = "z";
	
	public CommandInfo() {
		aliases.add("info");
		
		optionalParameters.add("id");
		
		requiredPermission = "ancientgates.info";
		
		hasGateParam = false;
		
		helpDescription = "Display info about a gate.";
	}
	
	public void perform() {		
		// Check if optional parameter exists
		String id = null;
		if (parameters.size() > 0) {
			id = parameters.get(0);
		}
		Location nearestFrom = null;
		
		// Info based on sight
		if (id == null) {
			// Find gate based on the player's line of sight
			Location playerTargetLocation = player.getTargetBlock(null, 20).getLocation();
			gate = GateUtil.nearestGate(playerTargetLocation, false);
			String from = GateUtil.nearestFrom(playerTargetLocation);
			
			if (gate == null || from.isEmpty()) {
				sendMessage("No gate in sight. Ensure you are looking at a gate, or use:");
				sendMessage(new CommandInfo().getUseageTemplate(true, true));
				return;
			}
			
			nearestFrom = TeleportUtil.stringToLocation(from);
			
		// Info based on id
		} else {
			// Find gate based on id
			if (!Gate.exists(id)) {		
				sendMessage("There exists no gate with id "+id);
				return;
			}
			
			gate = Gate.get(id);
			
		}
		
		// Display gate info
		sendMessage(TextUtil.titleize("Gate: "+ Conf.colorParameter + gate.getId() + Conf.colorSystem + ""));
		if (gate.getFroms().get(0).getBlock().getType() == Material.PORTAL) {
			sendMessage(Conf.colorSystem + "This gate is" + Conf.colorAlly + " open");
		} else {
			sendMessage(Conf.colorSystem + "This gate is" + Conf.colorAlly + " closed");
		}
		for (Location from : gate.getFroms()) {
			if (from != null) {
				if (nearestFrom != null){
					if (GeometryUtil.distanceBetweenLocations(from, nearestFrom) < 1.0) {
						sendMessage(Conf.colorSystem + "from:     " + Conf.colorParameter + "(" + from.getBlockX() + ", " + from.getBlockY() + ", " + from.getBlockZ() + ") in " + from.getWorld().getName());
					} else {
						sendMessage(Conf.colorSystem + "from:     " + Conf.colorAlly + "(" + from.getBlockX() + ", " + from.getBlockY() + ", " + from.getBlockZ() + ") in " + from.getWorld().getName());
					}
				} else {
					sendMessage(Conf.colorSystem + "from:     " + Conf.colorAlly + "(" + from.getBlockX() + ", " + from.getBlockY() + ", " + from.getBlockZ() + ") in " + from.getWorld().getName());
				}
			} else {
				sendMessage(Conf.colorSystem + "NOTE: this gate has no 'from' location");
			}
		}	
		if (gate.getTo() != null) {
			sendMessage(Conf.colorSystem + "to:  " + Conf.colorAlly + "(" + gate.getTo().getBlockX() + ", " + gate.getTo().getBlockY() + ", " + gate.getTo().getBlockZ() + ") in " + gate.getTo().getWorld().getName());
		} else if (gate.getBungeeTo() != null) {
			sendMessage(Conf.colorSystem + "to:  " + Conf.colorAlly + "(" + String.valueOf(Math.round(Double.parseDouble(gate.getBungeeTo().get(X)))) + ", " + String.valueOf(Math.round(Double.parseDouble(gate.getBungeeTo().get(Y)))) + ", " + String.valueOf(Math.round(Double.parseDouble(gate.getBungeeTo().get(Z)))) + ") in " + gate.getBungeeTo().get(WORLD) + " on " + gate.getBungeeTo().get(SERVER));	
		} else {
			sendMessage(Conf.colorSystem + "NOTE: this gate has no 'to' location");
		}
		if (gate.getTeleportEntities()) {
			sendMessage(Conf.colorSystem + "entities" + Conf.colorAlly + " allowed");
		} else {
			sendMessage(Conf.colorSystem + "entities" + Conf.colorAlly + " not allowed");
		}
		if (Conf.useEconomy) {
			sendMessage(Conf.colorSystem + "cost" + Conf.colorAlly + " " + String.valueOf(gate.getCost()));
		}	
	}

}