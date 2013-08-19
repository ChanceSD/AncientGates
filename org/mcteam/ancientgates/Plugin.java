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
import org.mcteam.ancientgates.commands.base.CommandAddFrom;
import org.mcteam.ancientgates.commands.base.CommandAddTo;
import org.mcteam.ancientgates.commands.base.CommandClose;
import org.mcteam.ancientgates.commands.base.CommandCloseAll;
import org.mcteam.ancientgates.commands.base.CommandCreate;
import org.mcteam.ancientgates.commands.base.CommandDelete;
import org.mcteam.ancientgates.commands.base.CommandHelp;
import org.mcteam.ancientgates.commands.base.CommandInfo;
import org.mcteam.ancientgates.commands.base.CommandList;
import org.mcteam.ancientgates.commands.base.CommandOpen;
import org.mcteam.ancientgates.commands.base.CommandOpenAll;
import org.mcteam.ancientgates.commands.base.CommandRemExec;
import org.mcteam.ancientgates.commands.base.CommandRemFrom;
import org.mcteam.ancientgates.commands.base.CommandRemTo;
import org.mcteam.ancientgates.commands.base.CommandRename;
import org.mcteam.ancientgates.commands.base.CommandSetEntities;
import org.mcteam.ancientgates.commands.base.CommandSetExec;
import org.mcteam.ancientgates.commands.base.CommandSetFrom;
import org.mcteam.ancientgates.commands.base.CommandSetMaterial;
import org.mcteam.ancientgates.commands.base.CommandSetMessage;
import org.mcteam.ancientgates.commands.base.CommandSetTo;
import org.mcteam.ancientgates.commands.base.CommandSetVehicles;
import org.mcteam.ancientgates.commands.bungee.CommandSetBungeeType;
import org.mcteam.ancientgates.commands.economy.CommandSetCost;
import org.mcteam.ancientgates.commands.sockets.CommandAddServer;
import org.mcteam.ancientgates.commands.sockets.CommandRemServer;
import org.mcteam.ancientgates.commands.sockets.CommandServerList;
import org.mcteam.ancientgates.listeners.PluginBlockListener;
import org.mcteam.ancientgates.listeners.PluginEntityListener;
import org.mcteam.ancientgates.listeners.PluginMessengerListener;
import org.mcteam.ancientgates.listeners.PluginPlayerListener;
import org.mcteam.ancientgates.listeners.PluginMovementListener;
import org.mcteam.ancientgates.listeners.PluginSocketListener;
import org.mcteam.ancientgates.metrics.MetricsStarter;
import org.mcteam.ancientgates.queue.BungeeQueue;
import org.mcteam.ancientgates.sockets.SocketServer;

public class Plugin extends JavaPlugin {
	
	public static Plugin instance;
	public static Logger log;
	
	public static SocketServer serv = null;
	
	public static Permission perms = null;
    public static Economy econ = null;
    
	public static String bungeeServerName = null;
    
	private String baseCommand;
	
	public final static Gson gson = new GsonBuilder()
	.setPrettyPrinting()
	.excludeFieldsWithModifiers(Modifier.TRANSIENT, Modifier.VOLATILE)
	.registerTypeAdapter(Location.class, new LocationTypeAdapter())
	.create();
	
	// HashMap of incoming BungeeCord players & passengers
	public static Map<String, BungeeQueue> bungeeCordInQueue = new HashMap<String, BungeeQueue>();
	
	// HashMap of outgoing BungeeCord players & passengers
	public static Map<String, String> bungeeCordOutQueue = new HashMap<String, String>();
	
	// ArrayList of incoming BungeeCord entities & vehicles
	public static ArrayList<BungeeQueue> bungeeCordEntityInQueue = new ArrayList<BungeeQueue>();
	public static ArrayList<BungeeQueue> bungeeCordVehicleInQueue = new ArrayList<BungeeQueue>();
	
	public static HashMap<String, Long> lastMessageTime = new HashMap<String, Long>();
	
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
		//Check for updates
		if (Conf.autoUpdate) {
			new Updater(this, "ancient-gates-reloaded", this.getFile(), Updater.UpdateType.DEFAULT, false);
		}
		
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
		Gates.load();
		
		// Setup BungeeCord support
		if (Conf.useSocketComms) Server.load();
		if (Conf.bungeeCordSupport) setupBungeeCord();
		
		// Add the commands
		commands.add(new CommandHelp());
		commands.add(new CommandCreate());
		commands.add(new CommandDelete());
		commands.add(new CommandSetExec());
		commands.add(new CommandSetFrom());
		commands.add(new CommandSetTo());
		commands.add(new CommandOpen());
		commands.add(new CommandClose());
		commands.add(new CommandRemExec());
		commands.add(new CommandRename());
		commands.add(new CommandSetMessage());
		if (econ != null) {
			commands.add(new CommandSetCost());
		}
		commands.add(new CommandSetEntities());
		if (!Conf.useVanillaPortals) {
			commands.add(new CommandSetVehicles());
			commands.add(new CommandSetMaterial());
		}
		commands.add(new CommandAddFrom());
		commands.add(new CommandRemFrom());
		commands.add(new CommandAddTo());
		commands.add(new CommandRemTo());
		commands.add(new CommandInfo());
		commands.add(new CommandList());
		commands.add(new CommandOpenAll());
		commands.add(new CommandCloseAll());
		if (Conf.bungeeCordSupport) {
			commands.add(new CommandSetBungeeType());
		}
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
		if (!Conf.useVanillaPortals) {
			pm.registerEvents(new PluginMovementListener(this), this);
		}
		
		//Submit Stats
		MetricsStarter metrics = new MetricsStarter(this);
		metrics.setupMetrics();
		
		log("Enabled");
	}
	
	// -------------------------------------------- //
	// Setup optional dependencies
	// -------------------------------------------- //
	private void setupBungeeCord() {	
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
		if (perms == null) {
			return Bukkit.getServer().getOfflinePlayer(player).isOp();
		} else {
			return perms.playerHas(Bukkit.getWorlds().get(0), player, requiredPermission) |
					   Bukkit.getServer().getOfflinePlayer(player).isOp();
		}
	}
	
	// -------------------------------------------- //
	// Handle economy
	// -------------------------------------------- //
	public static boolean handleEconManage(CommandSender sender, Double requiredCost) {
		if(!(sender instanceof Player)) return true;
		
		Player player = (Player) sender;
		
		if (econ == null || !Conf.useEconomy || hasPermManage(sender, "ancientgates.econbypass") || requiredCost == 0.00) {
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
