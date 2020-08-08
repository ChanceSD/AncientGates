package org.mcteam.ancientgates.commands.base;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.mcteam.ancientgates.Gate;
import org.mcteam.ancientgates.commands.BaseCommand;

public class CommandSetMessage extends BaseCommand {

	public CommandSetMessage() {
		aliases.add("setmessage");
		aliases.add("setmsg");

		requiredParameters.add("id");
		requiredPermission = "ancientgates.setmessage";

		optionalParameters.add("msg");

		senderMustBePlayer = false;

		helpDescription = "Set tp \"msg\" for gate";
	}

	@Override
	public void perform() {
		String msg = "";
		parameters.remove(0);
		for (final String parameter : parameters) {
			msg += " " + parameter;
		}

		gate.setMessage(msg.trim());

		if (!msg.trim().isEmpty()) {
			sendMessage("Teleport msg for gate \"" + gate.getId() + "\" is \"" + msg.trim() + "\".");
		} else {
			sendMessage("Teleport msg for gate \"" + gate.getId() + "\" removed.");
		}

		Gate.save();
	}

	@Override
	public List<String> onTabComplete(final CommandSender sender, final List<String> parameters) {
		if (parameters.size() == 1)
			return Gate.getAllIDs();

		return super.onTabComplete(sender, parameters);
	}

}
