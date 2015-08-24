package org.mcteam.ancientgates.commands.sockets;

import java.util.ArrayList;
import java.util.List;

import org.mcteam.ancientgates.Conf;
import org.mcteam.ancientgates.Plugin;
import org.mcteam.ancientgates.Server;
import org.mcteam.ancientgates.commands.BaseCommand;
import org.mcteam.ancientgates.sockets.types.ConnectionState;
import org.mcteam.ancientgates.tasks.BungeeServerList;
import org.mcteam.ancientgates.tasks.PingSocketServers;
import org.mcteam.ancientgates.util.TextUtil;

public class CommandServerList extends BaseCommand {
	
	public CommandServerList() {
		aliases.add("serverlist");
		aliases.add("serverls");
		
		requiredPermission = "ancientgates.serverlist";
		
		optionalParameters.add("page");
		
		senderMustBePlayer = true;
		hasGateParam = false;
		
		helpDescription = "Display list of the servers";
	}
	
	public void perform() {
		// Grab new BungeeCord server list
		new BungeeServerList(Plugin.instance).run();
		
		// Ping all Socket Comms servers
		new PingSocketServers().run();
		
		// Check bungeeServerList found
		if (Plugin.bungeeServerList == null) {
			sendMessage("Still connecting to BungeeCord. Try again.");
			return;
		}
		
		// Parse command parameters
		int tmpPage = 1;
		if (parameters.size() > 0) {
			try {
				tmpPage = Integer.parseInt(parameters.get(0));
			} catch (NumberFormatException e) {
				// wasn't an integer
			}
		}
		final int page = tmpPage;
		
		// Send waiting message (delay output by 1s)
		sendMessage("Checking servers...");
		Plugin.instance.getServer().getScheduler().scheduleSyncDelayedTask(Plugin.instance, new Runnable() {
			public void run() {
				// Create list of SocketComms servers
				List<String> servers = new ArrayList<String>();
				for (Server server : Server.getAll()) {
					servers.add(Conf.colorValue+server.getName()+Conf.colorChrome+" ("+Conf.colorSystem+"BC: "+((Plugin.bungeeServerList.contains(server.getName()))?Conf.colorCommand+"connected":Conf.colorParameter+"disconnected")+Conf.colorSystem+", SC: "+Conf.colorParameter+( (server.getState()==ConnectionState.CONNECTED)?Conf.colorCommand+"connected":Conf.colorParameter+"disconnected")+Conf.colorChrome+")");
				}

				if (servers.size() == 0) {
					sendMessage("There are no known servers yet.");
					return;
				}
				
				// Send list as readable pages
				sendMessage(TextUtil.getPage(servers, page, "Server List - "+servers.size()+" server(s) -", sender));
			}
		}, 20L);
	}     
	
}