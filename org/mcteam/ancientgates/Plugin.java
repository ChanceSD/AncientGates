package org.mcteam.ancientgates;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcteam.ancientgates.commands.BaseCommand;
import org.mcteam.ancientgates.commands.CommandClose;
import org.mcteam.ancientgates.commands.CommandCreate;
import org.mcteam.ancientgates.commands.CommandDelete;
import org.mcteam.ancientgates.commands.CommandHelp;
import org.mcteam.ancientgates.commands.CommandList;
import org.mcteam.ancientgates.commands.CommandOpen;
import org.mcteam.ancientgates.commands.CommandSetFrom;
import org.mcteam.ancientgates.commands.CommandSetTo;
import org.mcteam.ancientgates.gson.Gson;
import org.mcteam.ancientgates.gson.GsonBuilder;
import org.mcteam.ancientgates.listeners.PluginBlockListener;
import org.mcteam.ancientgates.listeners.PluginPlayerListener;

public class Plugin extends JavaPlugin {
	public static Plugin instance;
	

	
	private String baseCommand;
	
	public final static Gson gson = new GsonBuilder()
	.setPrettyPrinting()
	.excludeFieldsWithModifiers(Modifier.TRANSIENT, Modifier.VOLATILE)
	.registerTypeAdapter(Location.class, new MyLocationTypeAdapter())
	.create();
	
	// Commands
	public List<BaseCommand> commands = new ArrayList<BaseCommand>();
	
	public Plugin() {
		instance = this;
	}

	@Override
	public void onDisable() {
		log("Disabled");
	}

	@Override
	public void onEnable() {
		// Add the commands
		commands.add(new CommandHelp());
		commands.add(new CommandCreate());
		commands.add(new CommandDelete());
		commands.add(new CommandSetFrom());
		commands.add(new CommandSetTo());
		commands.add(new CommandOpen());
		commands.add(new CommandClose());
		commands.add(new CommandList());
		
		// Ensure basefolder exists!
		this.getDataFolder().mkdirs();
		
		// Load from disc
		Conf.load();
		Gate.load();
		
		// Register events
		PluginManager pm = this.getServer().getPluginManager();
		getServer().getPluginManager().registerEvents(new PluginBlockListener(this), this);
		getServer().getPluginManager().registerEvents(new PluginPlayerListener(this), this);
		
		log("Enabled");
	}
	
	// -------------------------------------------- //
	// Test rights
	// -------------------------------------------- //
	
	public static boolean hasPermManage(CommandSender sender) {
		return sender.isOp();
	}
	
	// -------------------------------------------- //
	// Commands
	// -------------------------------------------- //
	
	@SuppressWarnings("unchecked")
	public String getBaseCommand() {
		if (this.baseCommand != null) 
                {
			return this.baseCommand;
		}
		
		Map<String, Map<String, Object>> Commands = this.getDescription().getCommands();
		this.baseCommand = Commands.keySet().iterator().next();
		return this.baseCommand;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) 
        {
		List<String> parameters = new ArrayList<String>(Arrays.asList(args));
		this.handleCommand(sender, parameters);
		return true;
	}
	
	public void handleCommand(CommandSender sender, List<String> parameters) {
            
           
		if (parameters.size() == 0) {
			this.commands.get(0).execute(sender, parameters);
			return;
		}
		
		String commandName = parameters.get(0).toLowerCase();
		parameters.remove(0);
		
		for (BaseCommand fcommand : this.commands) {
			if (fcommand.getAliases().contains(commandName)) {
				fcommand.execute(sender, parameters);
				return;
			}
		}
		
		sender.sendMessage(Conf.colorSystem+"Unknown gate-command \""+commandName+"\". Try "+Conf.colorCommand+"/"+getBaseCommand()+" help");
            
            
        }
	
	// -------------------------------------------- //
	// Logging
	// -------------------------------------------- //
	public static void log(String msg) {
		log(Level.INFO, msg);
	}
	
	public static void log(Level level, String msg) {
		Logger.getLogger("Minecraft").log(level, "["+instance.getDescription().getFullName()+"] "+msg);
	}

}
