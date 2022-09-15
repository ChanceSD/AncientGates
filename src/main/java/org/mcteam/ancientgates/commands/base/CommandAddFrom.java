package org.mcteam.ancientgates.commands.base;

import java.util.List;

import com.cryptomorin.xseries.XBlock;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.mcteam.ancientgates.Gate;
import org.mcteam.ancientgates.commands.BaseCommand;
import org.mcteam.ancientgates.util.TextUtil;

public class CommandAddFrom extends BaseCommand {

	public CommandAddFrom() {
		aliases.add("addfrom");

		requiredParameters.add("id");

		requiredPermission = "ancientgates.addfrom";

		helpDescription = "Add another \"from\" to your location";
	}

	@Override
	public void perform() {
		if ((gate.getFroms() == null) || (gate.getFroms().size() < 1)) {
			sendMessage("This gate needs an initial \"from\" location. Use:");
			sendMessage(new CommandSetFrom().getUsageTemplate(true, true));
			return;
		}

		// The player might stand in a half-block or a sign or whatever
		// Therefore we load some extra locations and blocks
		final Block playerBlock = player.getLocation().getBlock();
		final Block upBlock = playerBlock.getRelative(BlockFace.UP);

		if (XBlock.isAir(playerBlock.getType())) {
			gate.addFrom(playerBlock.getLocation());
		} else if (XBlock.isAir(upBlock.getType())) {
			gate.addFrom(upBlock.getLocation());
		} else {
			sendMessage("There is not enough room for a gate to open here");
			return;
		}

		sendMessage("Another \"from\" location for gate \"" + gate.getId() + "\" is now where you stand.");
		sendMessage("Build a frame around that block and re-issue:");
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
