package org.mcteam.ancientgates.metrics;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.AdvancedPie;
import org.bstats.charts.SimplePie;
import org.bukkit.Material;
import org.mcteam.ancientgates.Conf;
import org.mcteam.ancientgates.Gate;
import org.mcteam.ancientgates.Plugin;
import org.mcteam.ancientgates.util.types.GateMaterial;

import com.cryptomorin.xseries.XMaterial;

public class MetricsStarter {

	public Plugin plugin;

	public MetricsStarter(final Plugin plugin) {
		this.plugin = plugin;
	}

	public void setupMetrics() {
		// Initialise metics
		final Metrics metrics = new Metrics(plugin, 5548);

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
		metrics.addCustomChart(new AdvancedPie("number_of_gates", new Callable<Map<String, Integer>>() {
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
		metrics.addCustomChart(new AdvancedPie("gate_access", new Callable<Map<String, Integer>>() {
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
		metrics.addCustomChart(new SimplePie("number_of_servers", new Callable<String>() {
			@Override
			public String call() throws Exception {
				return Conf.bungeeCordSupport ? "Bungee Servers" : "Normal Servers";
			}
		}));

		// Plot update checking
		metrics.addCustomChart(new SimplePie("update_check", new Callable<String>() {
			@Override
			public String call() throws Exception {
				return Conf.updateCheck ? "Enabled" : "Disabled";
			}
		}));

		// Plot features
		metrics.addCustomChart(new AdvancedPie("features", new Callable<Map<String, Integer>>() {
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
		metrics.addCustomChart(new SimplePie("teleportation_method", new Callable<String>() {
			@Override
			public String call() throws Exception {
				return Conf.useVanillaPortals ? "Vanilla Portal" : "Movement Hook";
			}
		}));

		// Plot gate portal materials
		metrics.addCustomChart(new AdvancedPie("gate_materials", new Callable<Map<String, Integer>>() {
			@Override
			public Map<String, Integer> call() throws Exception {
				final Map<String, Integer> valueMap = new HashMap<>();
				valueMap.put("Web", getGateCountByMaterial(GateMaterial.WEB));
				valueMap.put("Water", getGateCountByMaterial(GateMaterial.WATER));
				valueMap.put("Sugar Cane", getGateCountByMaterial(GateMaterial.SUGARCANE));
				valueMap.put("Portal", getGateCountByMaterial(GateMaterial.PORTAL));
				valueMap.put("Lava", getGateCountByMaterial(GateMaterial.LAVA));
				valueMap.put("End Portal", getGateCountByMaterial(GateMaterial.ENDPORTAL));
				valueMap.put("End Gateway", getGateCountByMaterial(GateMaterial.ENDGATEWAY));
				valueMap.put("Air", getGateCountByMaterial(GateMaterial.AIR));
				return valueMap;
			}

			private int getGateCountByMaterial(final GateMaterial material) {
				return (int) Gate.getAll().stream().filter(g -> g.getGateMaterial() == material).count();
			}
		}));

	}

}
