package org.mcteam.ancientgates.metrics;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import org.bstats.bukkit.Metrics;
import org.bukkit.Material;
import org.mcteam.ancientgates.Conf;
import org.mcteam.ancientgates.Gate;
import org.mcteam.ancientgates.Plugin;

public class MetricsStarter {

	public Plugin plugin;

	public MetricsStarter(final Plugin plugin) {
		this.plugin = plugin;
	}

	public void setupMetrics() {
		// Initialise metics
		final Metrics metrics = new Metrics(plugin);

		// It seems bstats doesn't support multi line or bar charts currently, leaving this here for when it does
//			metrics.addCustomChart(new Metrics.MultiLineChart("number_of_gates", new Callable<Map<String, Integer>>() {
//				@Override
//				public Map<String, Integer> call() throws Exception {
//					Map<String, Integer> valueMap = new HashMap<>();
//					valueMap.put("Bungee Gates", (int) Gate.getAll().stream().filter(g -> g.getBungeeTos() != null).count());
//					valueMap.put("Normal Gates", (int) Gate.getAll().stream().filter(g -> g.getTos() != null).count());
//					valueMap.put("Undefined Gates", (int) Gate.getAll().stream().filter(g -> g.getTos() == null).count());
//					valueMap.put("Total", Gate.getAll().size());
//					return valueMap;
//				}
//			}));

		// Plot number of gates
		metrics.addCustomChart(new Metrics.AdvancedPie("number_of_gates", new Callable<Map<String, Integer>>() {
			@Override
			public Map<String, Integer> call() throws Exception {
				final Map<String, Integer> valueMap = new HashMap<>();
				valueMap.put("Bungee Gates", (int) Gate.getAll().stream().filter(g -> g.getBungeeTos() != null).count());
				valueMap.put("Normal Gates", (int) Gate.getAll().stream().filter(g -> g.getTos() != null).count());
				valueMap.put("Undefined Gates", (int) Gate.getAll().stream().filter(g -> g.getTos() == null).count());
				return valueMap;
			}
		}));

		// Plot gate entity access
		metrics.addCustomChart(new Metrics.AdvancedPie("gate_access", new Callable<Map<String, Integer>>() {
			@Override
			public Map<String, Integer> call() throws Exception {
				final Map<String, Integer> valueMap = new HashMap<>();
				valueMap.put("Players", Gate.getAll().size());
				valueMap.put("Entities", (int) Gate.getAll().stream().filter(g -> g.getTeleportEntities()).count());
				valueMap.put("Vehicles", (int) Gate.getAll().stream().filter(g -> g.getTeleportVehicles()).count());
				return valueMap;
			}
		}));

		// Plot number of servers
		metrics.addCustomChart(new Metrics.SimplePie("number_of_servers", new Callable<String>() {
			@Override
			public String call() throws Exception {
				return Conf.bungeeCordSupport ? "Bungee Servers" : "Normal Servers";
			}
		}));

		// Plot features
		metrics.addCustomChart(new Metrics.AdvancedPie("features", new Callable<Map<String, Integer>>() {
			@Override
			public Map<String, Integer> call() throws Exception {
				final Map<String, Integer> valueMap = new HashMap<>();
				valueMap.put("BungeeCord Support", Conf.bungeeCordSupport ? 1 : 0);
				valueMap.put("Socket Comms Enabled", Conf.useSocketComms ? 1 : 0);
				valueMap.put("Economy Enabled", Conf.useEconomy ? 1 : 0);
				valueMap.put("Enforce Access Enabled", Conf.enforceAccess ? 1 : 0);
				return valueMap;
			}
		}));

		// Plot teleportation method
		metrics.addCustomChart(new Metrics.SimplePie("teleportation_method", new Callable<String>() {
			@Override
			public String call() throws Exception {
				return Conf.useVanillaPortals ? "Vanilla Portal" : "Movement Hook";
			}
		}));

		// Plot gate portal materials
		metrics.addCustomChart(new Metrics.AdvancedPie("gate_materials", new Callable<Map<String, Integer>>() {
			@Override
			public Map<String, Integer> call() throws Exception {
				final Map<String, Integer> valueMap = new HashMap<>();
				valueMap.put("Web", getGateCountByMaterial(Material.WEB));
				valueMap.put("Water", getGateCountByMaterial(Material.STATIONARY_WATER));
				valueMap.put("Sugar Cane", getGateCountByMaterial(Material.SUGAR_CANE_BLOCK));
				valueMap.put("Portal", getGateCountByMaterial(Material.PORTAL));
				valueMap.put("Lava", getGateCountByMaterial(Material.STATIONARY_LAVA));
				valueMap.put("End Portal", getGateCountByMaterial(Material.ENDER_PORTAL));
				valueMap.put("Air", getGateCountByMaterial(Material.PISTON_MOVING_PIECE));
				return valueMap;
			}

			private int getGateCountByMaterial(final Material material) {
				return (int) Gate.getAll().stream().filter(g -> g.getMaterial() == material).count();
			}
		}));

	}

}
