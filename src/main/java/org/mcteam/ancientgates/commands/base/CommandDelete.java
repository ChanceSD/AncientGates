package org.mcteam.ancientgates.commands.base;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.mcteam.ancientgates.Gate;
import org.mcteam.ancientgates.Gates;
import org.mcteam.ancientgates.commands.BaseCommand;
import org.mcteam.ancientgates.util.TextUtil;

public class CommandDelete extends BaseCommand {
	public CommandDelete() {
		aliases.add("delete");
		aliases.add("del");
		aliases.add("remove");

		requiredParameters.add("id");

		requiredPermission = "ancientgates.delete";

		senderMustBePlayer = false;
		helpDescription = "Delete a gate";
	}

	@Override
	public void perform() {
		if (gate.getFroms() != null)
			Gates.close(gate);

		sendMessage("Gate with id \"" + gate.getId() + "\" was deleted.");
		Gate.delete(gate.getId());
		Gate.save();
	}

	@Override
	public List<String> onTabComplete(final CommandSender sender, final List<String> parameters) {
		if (parameters.size() == 1)
			return TextUtil.getMatchingEntries(parameters.get(0), Gate.getAllIDs());

		return super.onTabComplete(sender, parameters);
	}

}
