package org.mcteam.ancientgates.commands.base;

import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.mcteam.ancientgates.Conf;
import org.mcteam.ancientgates.Gate;
import org.mcteam.ancientgates.Gates;
import org.mcteam.ancientgates.commands.BaseCommand;
import org.mcteam.ancientgates.util.TextUtil;
import org.mcteam.ancientgates.util.types.GateMaterial;

public class CommandSetMaterial extends BaseCommand {

	public CommandSetMaterial() {
		aliases.add("setmaterial");

		requiredParameters.add("id");
		requiredParameters.add("material");

		requiredPermission = "ancientgates.setmaterial";

		senderMustBePlayer = false;

		helpDescription = "Set portal \"material\" for gate";
	}

	@Override
	public void perform() {
		final String material = parameters.get(1).toUpperCase();

		if (GateMaterial.fromName(material) == null) {
			sendMessage("This is not a valid gate material. Valid materials:");
			sendMessage(TextUtil.implode(Arrays.asList(GateMaterial.names), Conf.colorSystem + ", "));
			return;
		}

		final boolean isOpen = Gates.isOpen(gate);

		if (isOpen)
			Gates.close(gate);
		gate.setMaterial(material);
		if (isOpen)
			Gates.open(gate);
		sendMessage("Portal material for gate \"" + gate.getId() + "\" is now " + material + ".");

		Gate.save();
	}

	@Override
	public List<String> onTabComplete(final CommandSender sender, final List<String> parameters) {
		if (parameters.size() == 1){
			return TextUtil.getMatchingEntries(parameters.get(0), Gate.getAllIDs());
		} else if (parameters.size() == 2) {
			return Arrays.asList(GateMaterial.names);
		}
		return super.onTabComplete(sender, parameters);
	}

}
