package org.mcteam.ancientgates.commands.base;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.mcteam.ancientgates.Gate;
import org.mcteam.ancientgates.commands.BaseCommand;

import com.google.common.collect.Lists;

public class CommandSetEntities extends BaseCommand {

	public CommandSetEntities() {
		aliases.add("setentities");

		requiredParameters.add("id");
		requiredParameters.add("true/false");

		requiredPermission = "ancientgates.setentities";

		senderMustBePlayer = false;

		helpDescription = "Allow entities in gate";
	}

	@Override
	public void perform() {
		final Boolean flag = Boolean.valueOf(parameters.get(1));

		gate.setTeleportEntities(flag);
		sendMessage("Entity teleportation for gate \"" + gate.getId() + "\" is now " + String.valueOf(flag) + ".");

		Gate.save();
	}

	@Override
	public List<String> onTabComplete(final CommandSender sender, final List<String> parameters) {
		if (parameters.size() == 1) {
			return Gate.getAllIDs();
		} else if (parameters.size() == 2) {
			return Lists.newArrayList("True", "False");
		}
		return super.onTabComplete(sender, parameters);
	}

}
