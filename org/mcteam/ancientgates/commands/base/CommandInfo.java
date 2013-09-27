package org.mcteam.ancientgates.commands.base;

import java.util.Map;

import org.bukkit.Location;
import org.mcteam.ancientgates.Conf;
import org.mcteam.ancientgates.Gate;
import org.mcteam.ancientgates.Gates;
import org.mcteam.ancientgates.Plugin;
import org.mcteam.ancientgates.commands.BaseCommand;
import org.mcteam.ancientgates.util.GateUtil;
import org.mcteam.ancientgates.util.GeometryUtil;
import org.mcteam.ancientgates.util.TeleportUtil;
import org.mcteam.ancientgates.util.TextUtil;
import org.mcteam.ancientgates.util.types.WorldCoord;

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
			// NB :- getTargetBlock deprecation warnings suppressed until Bukkit API provides an alternative method
			@SuppressWarnings("deprecation")
			WorldCoord playerTargetCoord = new WorldCoord(player.getTargetBlock(null, 20));
			gate = GateUtil.nearestGate(playerTargetCoord, false);
			String from = GateUtil.nearestFrom(playerTargetCoord);
			
			if (gate == null || from.isEmpty()) {
				sendMessage("No gate in sight. Ensure you are looking at a gate, or use:");
				sendMessage(new CommandInfo().getUsageTemplate(true, true));
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
		if (Gates.isOpen(gate)) {
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
		if (gate.getTos() != null) {
			for (Location to : gate.getTos()) {
				if (to != null) {
					sendMessage(Conf.colorSystem + "to:  " + Conf.colorAlly + "(" + to.getBlockX() + ", " + to.getBlockY() + ", " + to.getBlockZ() + ") in " + to.getWorld().getName());
				}
			}
		} else if (gate.getBungeeTos() != null) {
			for (Map<String, String> bungeeto : gate.getBungeeTos()) {
				if (bungeeto != null) {
					if (gate.getBungeeType().equals("LOCATION")) {
						sendMessage(Conf.colorSystem + "to:  " + Conf.colorAlly + "(" + String.valueOf(Math.round(Double.parseDouble(bungeeto.get(X)))) + ", " + String.valueOf(Math.round(Double.parseDouble(bungeeto.get(Y)))) + ", " + String.valueOf(Math.round(Double.parseDouble(bungeeto.get(Z)))) + ") in " + bungeeto.get(WORLD) + " on " + bungeeto.get(SERVER));	
					} else {
						sendMessage(Conf.colorSystem + "to:  " + Conf.colorAlly + bungeeto.get(SERVER));	
					}
				}
			}
		} else {
			sendMessage(Conf.colorSystem + "NOTE: this gate has no 'to' location(s)");
		}
		if (Plugin.hasPermManage(player, "ancientgates.info.exec")) {
			if (gate.getCommand() != null) {
				sendMessage(Conf.colorSystem + "exec: " + Conf.colorAlly + "/" + gate.getCommand());
			} else {
				sendMessage(Conf.colorSystem + "NOTE: this gate has no command to execute");
			}
		}
		if (gate.getMessage() != null) {
			sendMessage(Conf.colorSystem + "message: " + Conf.colorAlly + gate.getMessage());
		} else {
			sendMessage(Conf.colorSystem + "NOTE: this gate has no teleport message");
		}
		if (gate.getTeleportEntities()) {
			sendMessage(Conf.colorSystem + "entities" + Conf.colorAlly + " allowed");
		} else {
			sendMessage(Conf.colorSystem + "entities" + Conf.colorAlly + " not allowed");
		}
		if (!Conf.useVanillaPortals) {
			if (gate.getTeleportVehicles()) {
				sendMessage(Conf.colorSystem + "vehicles" + Conf.colorAlly + " allowed");
			} else {
				sendMessage(Conf.colorSystem + "vehicles" + Conf.colorAlly + " not allowed");
			}
			sendMessage(Conf.colorSystem + "material" + Conf.colorAlly + " " + gate.getMaterialStr());
		}
		if (Conf.useEconomy) {
			if (gate.getCost() == 0.00) {
				sendMessage(Conf.colorSystem + "cost" + Conf.colorAlly + " free");
			} else {
				sendMessage(Conf.colorSystem + "cost" + Conf.colorAlly + " " + String.valueOf(gate.getCost()));
			}
		}	
	}

}