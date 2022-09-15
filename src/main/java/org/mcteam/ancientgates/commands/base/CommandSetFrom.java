package org.mcteam.ancientgates.commands.base;

import java.util.List;

import com.cryptomorin.xseries.XBlock;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.mcteam.ancientgates.Conf;
import org.mcteam.ancientgates.Gate;
import org.mcteam.ancientgates.Gates;
import org.mcteam.ancientgates.Plugin;
import org.mcteam.ancientgates.commands.BaseCommand;
import org.mcteam.ancientgates.util.TextUtil;

public class CommandSetFrom extends BaseCommand {

	public CommandSetFrom() {
		aliases.add("setfrom");

		requiredParameters.add("id");

		requiredPermission = "ancientgates.setfrom";

		helpDescription = "Set \"from\" to your location";
	}

	@Override
	public void perform() {
		// Check if multiple froms exist
		if (gate.getFroms() != null) {
			if (gate.getFroms().size() > 1) {
				sendMessage("This gate has multiple from locations. Use:");
				sendMessage(new CommandRemFrom().getUsageTemplate(true, true));
				return;
			}
			Gates.close(gate);
		}

		// The player might stand in a half-block or a sign or whatever
		// Therefore we load some extra locations and blocks
		final Block playerBlock = player.getLocation().getBlock();
		final Block upBlock = playerBlock.getRelative(BlockFace.UP);

		if (XBlock.isAir(playerBlock.getType())) {
			gate.addFrom(null); // Wipe previous from
			gate.addFrom(playerBlock.getLocation());
		} else if (XBlock.isAir(upBlock.getType())) {
			gate.addFrom(null); // Wipe previous from
			gate.addFrom(upBlock.getLocation());
		} else {
			sendMessage("There is not enough room for a gate to open here");
			if (Conf.debug) {
				Plugin.log("Couldn't create gate at player location. Material types: " + playerBlock.getType() + " and " + upBlock.getType());
			}
			return;
		}

		sendMessage("From location for gate \"" + gate.getId() + "\" is now where you stand.");
		sendMessage("Build a frame around that block and:");
		sendMessage(new CommandOpen().getUsageTemplate(true, true));

		Gate.save();
	}

	@Override
	public List<String> onTabComplete(final CommandSender sender, final List<String> parameters) {
		if (parameters.size() == 1)
			return TextUtil.getMatchingEntries(parameters.get(0), Gate.getAllIDs());

		return super.onTabComplete(sender, parameters);
	}

}
