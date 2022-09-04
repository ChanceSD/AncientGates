package org.mcteam.ancientgates.commands.base;

import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.mcteam.ancientgates.Conf;
import org.mcteam.ancientgates.Gate;
import org.mcteam.ancientgates.commands.BaseCommand;
import org.mcteam.ancientgates.util.TextUtil;
import org.mcteam.ancientgates.util.types.InvBoolean;

public class CommandSetInventory extends BaseCommand {

	public CommandSetInventory() {
		aliases.add("setinv");

		requiredParameters.add("id");
		requiredParameters.add("true/false/clear");

		requiredPermission = "ancientgates.setinv";

		senderMustBePlayer = false;

		helpDescription = "Allow inventory in gate";
	}

	@Override
	public void perform() {
		final String extFlag = parameters.get(1).toUpperCase();

		if (InvBoolean.fromName(extFlag) == null) {
			sendMessage("This is not a valid option type. Options are:");
			sendMessage(TextUtil.implode(Arrays.asList(InvBoolean.names), Conf.colorSystem + ", "));
			return;
		}

		gate.setTeleportInventory(extFlag);
		sendMessage("Inventory teleportation for gate \"" + gate.getId() + "\" is now " + String.valueOf(extFlag) + ".");

		Gate.save();
	}

	@Override
	public List<String> onTabComplete(final CommandSender sender, final List<String> parameters) {
		if (parameters.size() == 1) {
			return TextUtil.getMatchingEntries(parameters.get(0), Gate.getAllIDs());
		} else if (parameters.size() == 2) {
			return Arrays.asList(InvBoolean.names);
		}
		return super.onTabComplete(sender, parameters);
	}

}
