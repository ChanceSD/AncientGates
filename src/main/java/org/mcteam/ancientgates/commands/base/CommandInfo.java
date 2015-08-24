package org.mcteam.ancientgates.commands.base;

import java.util.Map;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.mcteam.ancientgates.Conf;
import org.mcteam.ancientgates.Gate;
import org.mcteam.ancientgates.Gates;
import org.mcteam.ancientgates.Plugin;
import org.mcteam.ancientgates.commands.BaseCommand;
import org.mcteam.ancientgates.util.GateUtil;
import org.mcteam.ancientgates.util.GeometryUtil;
import org.mcteam.ancientgates.util.TeleportUtil;
import org.mcteam.ancientgates.util.TextUtil;
import org.mcteam.ancientgates.util.types.InvBoolean;
import org.mcteam.ancientgates.util.types.TeleportType;
import org.mcteam.ancientgates.util.types.WorldCoord;

public class CommandInfo extends BaseCommand {

	private static final String SERVER = "server";

	public CommandInfo() {
		aliases.add("info");

		optionalParameters.add("id");

		requiredPermission = "ancientgates.info";

		hasGateParam = false;

		helpDescription = "Display info about a gate";
	}

	@Override
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
			// NB :- getTargetBlock deprecation warnings suppressed until Bukkit API provides an
			// alternative method
			final WorldCoord playerTargetCoord = new WorldCoord(player.getTargetBlock((Set<Material>) null, 20));
			gate = GateUtil.nearestGate(playerTargetCoord, false);
			final String from = GateUtil.nearestFrom(playerTargetCoord);

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
				sendMessage("There exists no gate with id " + id);
				return;
			}

			gate = Gate.get(id);

		}

		// Display gate info
		sendMessage(TextUtil.titleize("Gate: " + Conf.colorValue + gate.getId()));
		if (Gates.isOpen(gate)) {
			sendMessage(Conf.colorSystem + "This gate is" + Conf.colorCommand + " open");
		} else {
			sendMessage(Conf.colorSystem + "This gate is" + Conf.colorParameter + " closed");
		}
		for (final Location from : gate.getFroms()) {
			if (from != null) {//
				if (nearestFrom != null) {
					if (GeometryUtil.distanceBetweenLocations(from, nearestFrom) < 1.0) {
						sendMessage("from: " + Conf.colorCommand + new WorldCoord(from).toString());
					} else {
						sendMessage("from: " + Conf.colorParameter + new WorldCoord(from).toString());
					}
				} else {
					sendMessage("from: " + Conf.colorParameter + new WorldCoord(from).toString());
				}
			} else {
				sendMessage("NOTE: this gate has no 'from' location");
			}
		}
		if (gate.getTos() != null) {
			for (final Location to : gate.getTos()) {
				if (to != null) {
					sendMessage("to:    " + Conf.colorChrome + new WorldCoord(to).toString());
				}
			}
		} else if (gate.getBungeeTos() != null) {
			for (final Map<String, String> bungeeto : gate.getBungeeTos()) {
				if (bungeeto != null) {
					if (gate.getBungeeType() == TeleportType.LOCATION) {
						sendMessage("to:    " + Conf.colorChrome + new WorldCoord(bungeeto).toString() + " on " + bungeeto.get(SERVER));
					} else {
						sendMessage("to:    " + Conf.colorChrome + bungeeto.get(SERVER));
					}
				}
			}
		} else {
			sendMessage("NOTE: this gate has no 'to' location(s)");
		}
		if (Plugin.hasPermManage(player, "ancientgates.info.exec")) {
			if (gate.getCommand() != null) {
				sendMessage("exec: " + Conf.colorValue + "/" + gate.getCommand());
			} else {
				sendMessage("NOTE: this gate has no command to execute");
			}
		}
		if (gate.getMessage() != null) {
			sendMessage("message: " + Conf.colorValue + gate.getMessage());
		} else {
			sendMessage("NOTE: this gate has no teleport message");
		}
		if (gate.getTeleportEntities()) {
			sendMessage("entities" + Conf.colorCommand + " allowed");
		} else {
			sendMessage("entities" + Conf.colorParameter + " not allowed");
		}
		if (!Conf.useVanillaPortals) {
			if (gate.getTeleportVehicles()) {
				sendMessage("vehicles" + Conf.colorCommand + " allowed");
			} else {
				sendMessage("vehicles" + Conf.colorParameter + " not allowed");
			}
			sendMessage("material" + Conf.colorValue + " " + gate.getMaterialStr());
		}
		if (gate.getTeleportInventory() == InvBoolean.TRUE) {
			sendMessage("inventory" + Conf.colorCommand + " allowed");
		} else if (gate.getTeleportInventory() == InvBoolean.CLEAR) {
			sendMessage("inventory" + Conf.colorValue + " cleared");
		} else {
			sendMessage("inventory" + Conf.colorParameter + " not allowed");
		}
		if (Conf.useEconomy) {
			if (gate.getCost() == 0.00) {
				sendMessage("cost" + Conf.colorValue + " free");
			} else {
				sendMessage("cost" + Conf.colorValue + " " + String.valueOf(gate.getCost()));
			}
		}
	}

}
