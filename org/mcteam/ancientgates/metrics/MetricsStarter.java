package org.mcteam.ancientgates.metrics;

import java.io.IOException;

import org.mcteam.ancientgates.Conf;
import org.mcteam.ancientgates.Gate;
import org.mcteam.ancientgates.Plugin;
import org.mcteam.ancientgates.metrics.Metrics.Graph;

public class MetricsStarter {
	
	public Plugin plugin;

    public MetricsStarter(Plugin plugin) {
    	this.plugin = plugin;
    }
    
    public void setupMetrics() {
    	
		try {
			// Initialise metics
			Metrics metrics = new Metrics(plugin);
			
			// Plot number of gates
			Graph gatesGraph = metrics.createGraph("Number of Gates");
			gatesGraph.addPlotter(new Metrics.Plotter("Bungee Gates") {
				@Override
				public int getValue() {
					int i = 0;
					for (Gate gate : Gate.getAll()) {
						if (gate.getBungeeTo() != null) i++;
					}
					return i;
				}
			});
			gatesGraph.addPlotter(new Metrics.Plotter("Normal Gates") {
				@Override
				public int getValue() {
					int i = 0;
					for (Gate gate : Gate.getAll()) {
						if (gate.getTo() != null) i++;
					}
					return i;
				}
			});
			gatesGraph.addPlotter(new Metrics.Plotter("Total") {
				@Override
				public int getValue() {
					return Gate.getAll().size();
				}
			});
			
			// Plot gate entity access
			Graph accessGraph = metrics.createGraph("Gate Access");
			accessGraph.addPlotter(new Metrics.Plotter("Players") {
				@Override
				public int getValue() {
					int i = 0;
					for (Gate gate: Gate.getAll()) {
						if (!gate.getTeleportEntities()) i++;
					}
					return i;
				}
			});
			accessGraph.addPlotter(new Metrics.Plotter("Entities and Players") {
				@Override
				public int getValue() {
					int i = 0;
					for (Gate gate: Gate.getAll()) {
						if (gate.getTeleportEntities()) i++;
					}
					return i;
				}
			});
			
			// Plot number of servers
			Graph serverGraph = metrics.createGraph("Number of Servers");
			serverGraph.addPlotter(new Metrics.Plotter("Bungee Servers") {
				@Override
				public int getValue() {
					return (Conf.bungeeCordSupport)?1:0;
				}
			});
			serverGraph.addPlotter(new Metrics.Plotter("Normal Servers") {
				@Override
				public int getValue() {
					return (Conf.bungeeCordSupport)?0:1;
				}
			});
			serverGraph.addPlotter(new Metrics.Plotter("Total") {
				@Override
				public int getValue() {
					return 1;
				}
			});
			
			// Plot features
			Graph featureGraph = metrics.createGraph("Features");
			featureGraph.addPlotter(new Metrics.Plotter("BungeeCord Support") {
				@Override
				public int getValue() {
					return (Conf.bungeeCordSupport)?1:0;
				}
			});
			featureGraph.addPlotter(new Metrics.Plotter("Socket Comms Enabled") {
				@Override
				public int getValue() {
					return (Conf.useSocketComms)?1:0;
				}
			});
			featureGraph.addPlotter(new Metrics.Plotter("Auto-update Enabled") {
				@Override
				public int getValue() {
					return (Conf.autoUpdate)?1:0;
				}
			});
			featureGraph.addPlotter(new Metrics.Plotter("Economy Enabled") {
				@Override
				public int getValue() {
					return (Conf.useEconomy)?1:0;
				}
			});
			
			// Plot teleportation method
			Graph methodGraph = metrics.createGraph("Teleportation method");
			methodGraph.addPlotter(new Metrics.Plotter("Normal Nether") {
				@Override
				public int getValue() {
					return (Conf.useInstantNether)?0:1;
				}
			});
			methodGraph.addPlotter(new Metrics.Plotter("Instant Nether") {
				@Override
				public int getValue() {
					return (Conf.useInstantNether)?1:0;
				}
			});

			// Submit metrics
			metrics.start();
		} catch (IOException e) {
			// Failed to submit the stats :-(
		}
    }
    
}