package org.mcteam.ancientgates;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.Location;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import org.bukkit.craftbukkit.libs.com.google.gson.Gson;
import org.bukkit.craftbukkit.libs.com.google.gson.GsonBuilder;
import org.mcteam.ancientgates.gson.typeadapters.LocationTypeAdapter;

import org.mcteam.ancientgates.commands.BaseCommand;
import org.mcteam.ancientgates.commands.CommandAddServer;
import org.mcteam.ancientgates.commands.CommandClose;
import org.mcteam.ancientgates.commands.CommandCreate;
import org.mcteam.ancientgates.commands.CommandDelete;
import org.mcteam.ancientgates.commands.CommandHelp;
import org.mcteam.ancientgates.commands.CommandInfo;
import org.mcteam.ancientgates.commands.CommandList;
import org.mcteam.ancientgates.commands.CommandOpen;
import org.mcteam.ancientgates.commands.CommandRemFrom;
import org.mcteam.ancientgates.commands.CommandRemServer;
import org.mcteam.ancientgates.commands.CommandRename;
import org.mcteam.ancientgates.commands.CommandServerList;
import org.mcteam.ancientgates.commands.CommandSetCost;
import org.mcteam.ancientgates.commands.CommandSetEntities;
import org.mcteam.ancientgates.commands.CommandAddFrom;
import org.mcteam.ancientgates.commands.CommandSetFrom;
import org.mcteam.ancientgates.commands.CommandSetTo;
import org.mcteam.ancientgates.listeners.PluginBlockListener;
import org.mcteam.ancientgates.listeners.PluginEntityListener;
import org.mcteam.ancientgates.listeners.PluginMessengerListener;
import org.mcteam.ancientgates.listeners.PluginPlayerListener;
import org.mcteam.ancientgates.listeners.PluginPlayerMoveListener;
import org.mcteam.ancientgates.listeners.PluginSocketListener;
import org.mcteam.ancientgates.metrics.MetricsStarter;
import org.mcteam.ancientgates.sockets.SocketServer;

public class Plugin extends JavaPlugin {
	
	public static Plugin instance;
	public static Logger log;
	
	public static SocketServer serv = null;
	
	public static Permission perms = null;
    public static Economy econ = null;
    
	private String baseCommand;
	
	public final static Gson gson = new GsonBuilder()
	.setPrettyPrinting()
	.excludeFieldsWithModifiers(Modifier.TRANSIENT, Modifier.VOLATILE)
	.registerTypeAdapter(Location.class, new LocationTypeAdapter())
	.create();
	
	// HashMap of incoming BungeeCord players
	public static Map<String, String> bungeeCordPlayerInQueue = new HashMap<String, String>();
	// HashMap of incoming BungeeCord entities
	public static ArrayList<String>  bungeeCordEntityInQueue = new ArrayList<String>();
	
	// Array of BungeeCord players to block join/quit message
	public static ArrayList<String> bungeeCordBlockJoinQueue = new ArrayList<String>();
	public static ArrayList<String> bungeeCordBlockQuitQueue = new ArrayList<String>();
	
	// Commands
	public List<BaseCommand> commands = new ArrayList<BaseCommand>();
	
	public Plugin() {
		instance = this;
	}

	@Override
	public void onDisable() {
		log(String.format("Disabled Version %s", getDescription().getName(), getDescription().getVersion()));
	}

	@Override
	public void onEnable() {
		//Enable permissions and economy
		if (getServer().getPluginManager().getPlugin("Vault") != null) {
			if (!setupPermissions()) {
				log("Vault permissions hook not found. Permissions support unavailable.");
			}	
			if(!setupEconomy()) {
				log("Vault economy hook not found. Economy support unavailable.");
			}
		} else {
			log("Vault dependency not found, defaulting to Bukkit Permissions. Economy support unavailable.");
		}
		
		// Ensure base folder exists!
		this.getDataFolder().mkdirs();
		
		// Load from disc
		Conf.load();
		Gate.load();
		
		// Setup BungeeCord support
		if (Conf.useSocketComms) Server.load();
		if (Conf.bungeeCordSupport) setupBungeeCord();
		
		// Add the commands
		commands.add(new CommandHelp());
		commands.add(new CommandCreate());
		commands.add(new CommandDelete());
		commands.add(new CommandSetFrom());
		commands.add(new CommandSetTo());
		commands.add(new CommandOpen());
		commands.add(new CommandClose());
		commands.add(new CommandRename());
		if (econ != null) {
			commands.add(new CommandSetCost());
		}
		commands.add(new CommandSetEntities());
		commands.add(new CommandAddFrom());
		commands.add(new CommandRemFrom());
		commands.add(new CommandInfo());
		commands.add(new CommandList());
		if (Conf.useSocketComms) {
			commands.add(new CommandAddServer());
			commands.add(new CommandRemServer());
			commands.add(new CommandServerList());
		}
		
		// Register events
		PluginManager pm = this.getServer().getPluginManager();
		pm.registerEvents(new PluginBlockListener(this), this);
		pm.registerEvents(new PluginEntityListener(this), this);
		pm.registerEvents(new PluginPlayerListener(this), this);
		if (Conf.useInstantNether) {
			pm.registerEvents(new PluginPlayerMoveListener(this), this);
		}
		
		//Submit Stats
		MetricsStarter metrics = new MetricsStarter(this);
		metrics.setupMetrics();
		
		log("Enabled");
	}
	
	private void setupBungeeCord() {	
		// Check BungeeCord server name specified
		if (Conf.bungeeServerName.isEmpty()) {
			log("bungeeServerName not defined. BungeeCord support disabled.");
            Conf.bungeeCordSupport = false;
            Conf.save();
            return;
		}

		// Enable required plugin channels 
		Plugin.log("Enabling bungeecord channels");
		Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
		Bukkit.getMessenger().registerIncomingPluginChannel(this, "BungeeCord", new PluginMessengerListener());
		
		if (!Conf.useSocketComms) {
			Plugin.log("Socket comms disabled. Using generic BungeeCord messaging.");
			return;
		}
		
		// Check socket comms port in range
		if (Conf.socketCommsPort > 65535) {
			Plugin.log("socketCommsPort out of range. Using generic BungeeCord messaging.");
			return;
		}
	
		// Enable server socket channel
		Plugin.log("Enabling comms channel");
		serv = new SocketServer(0, Conf.socketCommsPort, Conf.socketCommsPass);
		serv.addClientListener(new PluginSocketListener());
    }
	
	private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (permissionProvider != null) {
            perms = permissionProvider.getProvider();
        }
        return (perms != null);
    }

    private boolean setupEconomy() {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            econ = economyProvider.getProvider();
        }

        return (econ != null);
    }
	
	// -------------------------------------------- //
	// Check rights (online player)
	// -------------------------------------------- //
	public static boolean hasPermManage(CommandSender sender, String requiredPermission) {
		if(!(sender instanceof Player)) return true;
		
		Player player = (Player) sender;

		if (perms == null) {
			return sender.hasPermission(requiredPermission);
		} else {
			return perms.has(player, requiredPermission);
		}
	}
	
	// -------------------------------------------- //
	// Check rights (bungeeCord player)
	// -------------------------------------------- //
	public static boolean hasPermManage(String player, String requiredPermission) {
		return perms.playerHas(Bukkit.getWorlds().get(0), player, requiredPermission) |
			   Bukkit.getServer().getOfflinePlayer(player).isOp();
	}
	
	// -------------------------------------------- //
	// Handle economy
	// -------------------------------------------- //
	public static boolean handleEconManage(CommandSender sender, Double requiredCost) {
		if(!(sender instanceof Player)) return true;
		
		Player player = (Player) sender;
		
		if (econ == null || !Conf.useEconomy || hasPermManage(sender, "ancientgates.econbypass")) {
			return true;
		} else {
			Double balance = econ.getBalance(player.getName());
			if (requiredCost <= balance) {
				EconomyResponse r = econ.withdrawPlayer(player.getName(), requiredCost);
				if(r.transactionSuccess()) {
					sender.sendMessage(String.format("You were charged %s and now have %s.", econ.format(r.amount), econ.format(r.balance)));
					return true;
				} else {
	                sender.sendMessage(String.format("An error occured: %s.", r.errorMessage));
	                return false;
	            }
			} else {
				return false;
			}
		}
	}
	
	// -------------------------------------------- //
	// Commands
	// -------------------------------------------- //
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
