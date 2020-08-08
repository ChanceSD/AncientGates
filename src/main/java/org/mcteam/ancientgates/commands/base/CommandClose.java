package org.mcteam.ancientgates.commands.base;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.mcteam.ancientgates.Gate;
import org.mcteam.ancientgates.Gates;
import org.mcteam.ancientgates.commands.BaseCommand;

public class CommandClose extends BaseCommand {

	public CommandClose() {
		aliases.add("close");

		requiredParameters.add("id");

		requiredPermission = "ancientgates.close";

		senderMustBePlayer = false;

		helpDescription = "Close that gate";
	}

	@Override
	public void perform() {
		Gates.close(gate);
		sendMessage("The gate was closed.");
	}

	@Override
	public List<String> onTabComplete(final CommandSender sender, final List<String> parameters) {
		if (parameters.size() == 1)
			return Gate.getAllIDs();

		return super.onTabComplete(sender, parameters);
	}

}
