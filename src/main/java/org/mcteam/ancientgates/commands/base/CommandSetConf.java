package org.mcteam.ancientgates.commands.base;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.command.CommandSender;
import org.mcteam.ancientgates.Conf;
import org.mcteam.ancientgates.Plugin;
import org.mcteam.ancientgates.commands.BaseCommand;
import org.mcteam.ancientgates.tasks.BungeeServerList;
import org.mcteam.ancientgates.tasks.BungeeServerName;
import org.mcteam.ancientgates.util.TextUtil;
import org.mcteam.ancientgates.util.types.GateMaterial;
import org.mcteam.ancientgates.util.types.InvBoolean;
import org.mcteam.ancientgates.util.types.TeleportType;

import com.google.common.collect.Lists;

public class CommandSetConf extends BaseCommand {

	public CommandSetConf() {
		aliases.add("setconf");

		requiredParameters.add("option");
		requiredParameters.add("value");

		requiredPermission = "ancientgates.setconf";

		senderMustBePlayer = false;
		hasGateParam = false;

		helpDescription = "Set config option to value";
	}

	@Override
	public void perform() {
		final String item = parameters.get(0);
		String value = "";

		parameters.remove(0);
		for (final String parameter : parameters) {
			value += " " + parameter;
		}
		value = value.trim();

		final Field[] fields = Conf.class.getDeclaredFields();
		for (final Field f : fields) {
			f.setAccessible(true); // Modify private fields
			if (Modifier.isStatic(f.getModifiers()) && item.equalsIgnoreCase(f.getName())) {
				try {
					if (f.getType().getSimpleName().equals("boolean") && TextUtil.isBoolean(value)) {
						f.set(null, Boolean.parseBoolean(value));
					} else if (f.getType().getSimpleName().equals("int") && TextUtil.isInteger(value)) {
						f.set(null, Integer.valueOf(value));
					} else if (f.getType().getSimpleName().equals("InvBoolean") && InvBoolean.fromName(value.toUpperCase()) != null) {
						value = value.toUpperCase();
						f.set(null, InvBoolean.fromName(value));
					} else if (f.getType().getSimpleName().equals("GateMaterial") && GateMaterial.fromName(value.toUpperCase()) != null) {
						value = value.toUpperCase();
						f.set(null, GateMaterial.fromName(value));
					} else if (f.getType().getSimpleName().equals("TeleportType") && TeleportType.fromName(value.toUpperCase()) != null) {
						value = value.toUpperCase();
						f.set(null, TeleportType.fromName(value));
					} else if (f.getType().getSimpleName().equals("ChatColor") && TextUtil.chatColors.containsKey(value.toUpperCase())) {
						value = value.toUpperCase();
						f.set(null, TextUtil.chatColors.get(value));
					} else {
						f.set(null, value);
						value = "\"" + value + "\"";
					}
				} catch (final IllegalArgumentException e) {
					sendMessage("Config option \"" + f.getName() + "\" does not accept the value " + value + ".");
					return;
				} catch (final IllegalAccessException e) {
					continue;
				}
				sendMessage("Config option \"" + f.getName() + "\" is now " + value + ".");

				Conf.save();
				Plugin.instance.reload(sender);

				if (Conf.bungeeCordSupport) {
					if (Plugin.bungeeServerName == null)
						new BungeeServerName(Plugin.instance).runTaskLater(Plugin.instance, 20L);
					if (Plugin.bungeeServerList == null)
						new BungeeServerList(Plugin.instance).runTaskLater(Plugin.instance, 20L);
				}

				return;
			}
		}

		sendMessage("Config option \"" + item + "\" does not exist.");
	}

	@Override
	public List<String> onTabComplete(final CommandSender sender, final List<String> parameters) {
		if (parameters.size() == 1)
			return Lists.newArrayList(Conf.class.getDeclaredFields()).stream().filter(f -> Modifier.isPublic(f.getModifiers())).map(Field::getName).collect(Collectors.toList());
		return super.onTabComplete(sender, parameters);
	}

}
