package org.mcteam.ancientgates.commands.base;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.mcteam.ancientgates.Gate;
import org.mcteam.ancientgates.commands.BaseCommand;
import org.mcteam.ancientgates.util.TextUtil;

public class CommandRename extends BaseCommand {

	public CommandRename() {
		aliases.add("rename");

		requiredParameters.add("id");
		requiredParameters.add("newid");

		requiredPermission = "ancientgates.rename";

		senderMustBePlayer = false;

		helpDescription = "Rename a gate";
	}

	@Override
	public void perform() {
		final String id = parameters.get(0);
		final String newid = parameters.get(1);

		gate.rename(id, newid);
		sendMessage("Gate with id \"" + id + "\" was renamed to \"" + newid + "\".");

		Gate.save();
	}

	@Override
	public List<String> onTabComplete(final CommandSender sender, final List<String> parameters) {
		if (parameters.size() == 1)
			return TextUtil.getMatchingEntries(parameters.get(0), Gate.getAllIDs());

		return super.onTabComplete(sender, parameters);
	}

}
