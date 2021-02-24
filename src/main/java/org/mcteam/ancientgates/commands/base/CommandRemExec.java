package org.mcteam.ancientgates.commands.base;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.mcteam.ancientgates.Gate;
import org.mcteam.ancientgates.commands.BaseCommand;
import org.mcteam.ancientgates.util.TextUtil;

public class CommandRemExec extends BaseCommand {

	public CommandRemExec() {
		aliases.add("remexec");

		requiredParameters.add("id");

		requiredPermission = "ancientgates.remexec";

		senderMustBePlayer = false;

		helpDescription = "Remove \"exec\" cmd for gate";
	}

	@Override
	public void perform() {

		gate.setCommand("");
		sendMessage("Command for gate \"" + gate.getId() + "\" is removed.");

		Gate.save();
	}

	@Override
	public List<String> onTabComplete(final CommandSender sender, final List<String> parameters) {
		if (parameters.size() == 1)
			return TextUtil.getMatchingEntries(parameters.get(0), Gate.getAllIDs());

		return super.onTabComplete(sender, parameters);
	}

}
