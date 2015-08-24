package org.mcteam.ancientgates;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.mcteam.ancientgates.sockets.types.ConnectionState;
import org.mcteam.ancientgates.util.DiscUtil;

import com.google.gson.reflect.TypeToken;

public class Server {

	private static transient TreeMap<String, Server> instances = new TreeMap<String, Server>(String.CASE_INSENSITIVE_ORDER);
	private static transient File file = new File(Plugin.instance.getDataFolder(), "servers.json");

	private transient String name;
	private String address;
	private int port;
	private String password;
	private ConnectionState state = null;

	// -------------------------------------------- //
	// Getters And Setters
	// -------------------------------------------- //

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getAddress() {
		return address;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getPort() {
		return port;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPassword() {
		return password;
	}

	public void setState(ConnectionState state) {
		this.state = state;
	}

	public ConnectionState getState() {
		return state;
	}

	// ----------------------------------------------//
	// Persistance and entity management
	// ----------------------------------------------//
	public static Server get(String name) {
		return instances.get(name);
	}

	public static boolean exists(String name) {
		return instances.containsKey(name);
	}

	public static Server add(String name, String address, int port, String password) {
		Server server = new Server();
		server.name = name;
		instances.put(server.name, server);

		server.setAddress(address);
		server.setPort(port);
		server.setPassword(password);

		Plugin.log("Added new server " + server.name);

		return server;
	}

	public static void remove(String name) {
		// Remove the server
		instances.remove(name);
	}

	public static boolean save() {
		// Clear connection states before saving
		for (Server server : Server.getAll()) {
			server.state = null;
		}

		try {
			DiscUtil.write(file, Plugin.gson.toJson(instances));
		} catch (IOException e) {
			Plugin.log("Failed to save the servers to disk due to I/O exception.");
			e.printStackTrace();
			return false;
		} catch (NullPointerException e) {
			Plugin.log("Failed to save the servers to disk due to NPE.");
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public static boolean load() {
		Plugin.log("Loading servers from disk");
		if (!file.exists()) {
			Plugin.log("No servers to load from disk. Creating new file.");

			Server server = new Server();
			server.name = (Plugin.bungeeServerName != null) ? Plugin.bungeeServerName : "server1";
			instances.put(server.name, server);
			server.address = "localhost";
			server.port = Conf.socketCommsPort;
			server.password = Conf.socketCommsPass;

			save();
			return true;
		}

		try {
			Type type = new TypeToken<Map<String, Server>>() {
			}.getType();
			Map<String, Server> instancesFromFile = Plugin.gson.fromJson(DiscUtil.read(file), type);
			instances.clear();
			instances.putAll(instancesFromFile);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		fillNames();
		save();

		return true;
	}

	public static Collection<Server> getAll() {
		return instances.values();
	}

	public static void fillNames() {
		for (Entry<String, Server> entry : instances.entrySet()) {
			entry.getValue().setName(entry.getKey());
		}
	}

}
