package org.mcteam.ancientgates.commands.base;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.mcteam.ancientgates.Gate;
import org.mcteam.ancientgates.commands.BaseCommand;
import com.google.common.collect.Lists;
import org.mcteam.ancientgates.util.TextUtil;

public class CommandSetVehicles extends BaseCommand {

	public CommandSetVehicles() {
		aliases.add("setvehicles");

		requiredParameters.add("id");
		requiredParameters.add("true/false");

		requiredPermission = "ancientgates.setvehicles";

		senderMustBePlayer = false;

		helpDescription = "Allow vehicles in gate";
	}

	@Override
	public void perform() {
		final Boolean flag = Boolean.valueOf(parameters.get(1));

		gate.setTeleportVehicles(flag);
		sendMessage("Vehicle teleportation for gate \"" + gate.getId() + "\" is now " + String.valueOf(flag) + ".");

		Gate.save();
	}

	@Override
	public List<String> onTabComplete(final CommandSender sender, final List<String> parameters) {
		if (parameters.size() == 1){
			return TextUtil.getMatchingEntries(parameters.get(0), Gate.getAllIDs());
		} else if (parameters.size() == 2) {
			return Lists.newArrayList("True", "False");
		}
		return super.onTabComplete(sender, parameters);
	}

}
