package org.mcteam.ancientgates.commands.base;

import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.mcteam.ancientgates.Conf;
import org.mcteam.ancientgates.Gate;
import org.mcteam.ancientgates.commands.BaseCommand;
import org.mcteam.ancientgates.util.TextUtil;
import org.mcteam.ancientgates.util.types.CommandType;

public class CommandSetExec extends BaseCommand {

	public CommandSetExec() {
		aliases.add("setexec");

		requiredParameters.add("id");
		requiredParameters.add("type");
		requiredParameters.add("command");

		requiredPermission = "ancientgates.setexec";

		senderMustBePlayer = false;

		helpDescription = "Set \"exec\" cmd for gate";
	}

	@Override
	public void perform() {
		String command = "";
		final String commandType = parameters.get(1).toUpperCase();

		if (CommandType.fromName(commandType) == null) {
			sendMessage("This is not a valid command type. Valid types:");
			sendMessage(TextUtil.implode(Arrays.asList(CommandType.names), Conf.colorSystem + ", "));
			return;
		}

		parameters.remove(0);
		parameters.remove(0);
		for (final String parameter : parameters) {
			command += " " + parameter;
		}

		gate.setCommand(command.trim());
		gate.setCommandType(commandType);
		sendMessage("Command for gate \"" + gate.getId() + "\" is now /" + command.trim() + ".");

		Gate.save();
	}

	@Override
	public List<String> onTabComplete(final CommandSender sender, final List<String> parameters) {
		if (parameters.size() == 1) {
			return TextUtil.getMatchingEntries(parameters.get(0), Gate.getAllIDs());
		} else if (parameters.size() == 2) {
			return Arrays.asList(CommandType.names);
		}
		return super.onTabComplete(sender, parameters);
	}

}
