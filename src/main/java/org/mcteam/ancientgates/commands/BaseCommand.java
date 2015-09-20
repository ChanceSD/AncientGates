package org.mcteam.ancientgates.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.mcteam.ancientgates.Conf;
import org.mcteam.ancientgates.Gate;
import org.mcteam.ancientgates.Plugin;
import org.mcteam.ancientgates.Server;
import org.mcteam.ancientgates.util.TextUtil;

public class BaseCommand {

	public List<String> aliases;
	public List<String> requiredParameters;
	public List<String> optionalParameters;

	public String requiredPermission;
	public String helpDescription;

	public CommandSender sender;
	public boolean senderMustBePlayer;
	public boolean hasServerParam;
	public boolean hasGateParam;
	public Player player;
	public Server server;
	public Gate gate;

	public List<String> parameters;

	public BaseCommand() {
		aliases = new ArrayList<>();
		requiredParameters = new ArrayList<>();
		optionalParameters = new ArrayList<>();

		requiredPermission = "ancientgates.admin";

		senderMustBePlayer = true;
		hasServerParam = false;
		hasGateParam = true;

		helpDescription = "no description";
	}

	public List<String> getAliases() {
		return aliases;
	}

	public void execute(final CommandSender sender1, final List<String> parameters1) {
		this.sender = sender1;
		this.parameters = parameters1;

		if (!validateCall()) {
			return;
		}

		if (this.senderMustBePlayer) {
			this.player = (Player) sender1;
		}

		perform();
	}

	public void perform() {
	}

	public void sendMessage(final String message) {
		sender.sendMessage(Conf.colorSystem + message);
	}

	public void sendMessage(final List<String> messages) {
		for (final String message : messages) {
			this.sendMessage(message);
		}
	}

	public boolean validateCall() {
		if (this.senderMustBePlayer && !(sender instanceof Player)) {
			sendMessage("This command can only be used by ingame players.");
			return false;
		}

		if (!hasPermission(sender)) {
			// Ignore permissions for "to" on external BungeeCord gates
			if (Conf.bungeeCordSupport && aliases.contains("to") && parameters.size() > 1)
				return true;

			sendMessage("You lack the permissions to " + this.helpDescription.toLowerCase() + ".");
			return false;
		}

		if (parameters.size() < requiredParameters.size()) {
			sendMessage("Usage: " + this.getUsageTemplate(true));
			return false;
		}

		if (this.hasGateParam) {
			final String id = parameters.get(0);
			if (!Gate.exists(id)) {
				// Ignore id for "to" on external BungeeCord gates
				if (Conf.bungeeCordSupport && TextUtil.containsSubString(aliases, "to") && parameters.size() > 1)
					return true;

				sendMessage("There exists no gate with id " + id);
				return false;
			}
			gate = Gate.get(id);
		}

		if (this.hasServerParam) {
			final String name = parameters.get(0);
			if (!Server.exists(name)) {
				sendMessage("There exists no server with name " + name);
				return false;
			}
			server = Server.get(name);
		}

		return true;

	}

	public boolean hasPermission(final CommandSender sender1) {
		return Plugin.hasPermManage(sender1, requiredPermission);
	}

	// -------------------------------------------- //
	// Help and usage description
	// -------------------------------------------- //
	public String getUsageTemplate(final boolean withColor, final boolean withDescription) {
		String ret = "";

		if (withColor) {
			ret += Conf.colorCommand;
		}

		ret += "/" + Plugin.instance.getBaseCommand() + " " + TextUtil.implode(this.getAliases(), ",") + " ";

		final List<String> parts = new ArrayList<>();

		for (final String requiredParameter : this.requiredParameters) {
			parts.add("[" + requiredParameter + "]");
		}

		for (final String optionalParameter : this.optionalParameters) {
			parts.add("*[" + optionalParameter + "]");
		}

		if (withColor) {
			ret += Conf.colorParameter;
		}

		ret += TextUtil.implode(parts, " ");

		if (withDescription) {
			ret += "  " + Conf.colorSystem + this.helpDescription;
		}
		return ret;
	}

	public String getUsageTemplate(final boolean withColor) {
		return getUsageTemplate(withColor, false);
	}

	public String getUsageTemplate() {
		return getUsageTemplate(true);
	}

}
