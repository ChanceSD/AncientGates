package org.mcteam.ancientgates.sockets;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.craftbukkit.libs.com.google.gson.Gson;

import org.mcteam.ancientgates.Conf;
import org.mcteam.ancientgates.Plugin;
import org.mcteam.ancientgates.sockets.events.*;
import org.mcteam.ancientgates.sockets.types.ConnectionState;
import org.mcteam.ancientgates.sockets.types.Packet;
import org.mcteam.ancientgates.sockets.types.Packets;
import org.mcteam.ancientgates.util.TextUtil;

public class SocketServer implements Runnable {
	
	private int maxClient;
	private String password;
	private ServerSocket listener;
	private List<ClientConnectionThread> clients;
	private Thread thread;
	private List<SocketServerEventListener> clientListeners;
	private boolean isRunning;
	private Set<Integer> ids;
	
	public SocketServer(int clientCount, int port, String password) {
		this.maxClient = clientCount;
		this.password = TextUtil.md5(password);
		try {
			this.listener = new ServerSocket(port);
			this.start();
			Plugin.log("Server started on port " + port + ".");
		} catch (IOException e) {
			Plugin.log("Error starting listener on port " + port + ".");
			e.printStackTrace();
		}
		this.clients = new ArrayList<ClientConnectionThread>();
		this.clientListeners = new ArrayList<SocketServerEventListener>();
		this.ids = new HashSet<Integer>();
	}

	public synchronized void removeClient(int id) {
		ClientConnectionThread th = this.clients.get(this.getClientIndex(id));
		this.fireClientDisconnectEvent(new ClientConnectionEvent(th.getSocket(), id, ConnectionState.Disconnected));
		th.stop();	
		
		this.clients.remove(this.getClientIndex(id));
		this.ids.remove(id);
	}

	public void run() {
		while(thread != null && this.isRunning) {
			try {
				this.addThread(this.listener.accept());
			} catch (IOException e) {
				Plugin.log("Error while accepting client.");
			}
		}
	}
	
	private int getClientIndex(int id) {
		for(int i = 0; i < this.clients.size(); i++) {
			if(this.clients.get(i).getID() == id)
				return i;
		}
		return -1;
	}
	
	private void addThread(Socket client) {
		if(this.clients.size() >= this.maxClient && this.maxClient > 0) {
			Plugin.log("Refused client: maximum reached.");
			return;
		}
		
		ClientConnectionThread th = new ClientConnectionThread(this.getNewID(), this, client);
		this.fireClientConnectEvent(new ClientConnectionEvent(client, th.getID(), ConnectionState.Connected));
		this.clients.add(th);
		
	}
	
	public void start() {  
		if(thread == null) {
			this.isRunning = true;
			thread = new Thread(this); 
			thread.start();
		}
	}
	
	public void stop() {
		if(thread != null) {
			this.isRunning = false;
			try {
				this.listener.close();
				Plugin.log("Server stopped.");
			} catch (IOException e) {
				Plugin.log("Error while closing server socket.");
			}
			
			try {
				thread.join();
			} catch (InterruptedException e) {
				if (Conf.debug) Plugin.log("Error while closing server thread.");
			}
			thread = null;
		}
	}
	
	public void sendToClient(int client, Packet data) {
		Packets p = new Packets();
		p.packets = new Packet[] { data };
		this.sendToClient(client, p);
	}
	
	public void sendToClient(int client, Packets data) {
		Gson gson = new Gson();
		String json = gson.toJson(data, Packets.class);
		
		int clientIndex = this.getClientIndex(client);
		if(clientIndex < 0)
			return;
		
		ClientConnectionThread th = this.clients.get(clientIndex);
		th.send(json);
	}
	
	private int getNewID() {
		int found = -1;
		int current = 0;
		do {
			current = (this.maxClient > 0) ? (int)(Math.random() * (this.maxClient)) : (int)(Math.random() * ((this.clients.size() + 1) * 2));
			if(this.isIDAvaible(current)) {
				found = current;
			}
		} while(found == -1);
		
		return found;
	}
	
	private boolean isIDAvaible(int id) {
		return !this.ids.contains(id);
	}

	public void handle(int client, String input) {
		Gson gson = new Gson();
		Packets packets = gson.fromJson(input, Packets.class);
		for(Packet p : packets.packets) {
			this.fireClientRecieveEvent(new ClientRecieveEvent(client, this.clients.get(this.getClientIndex(client)).getSocket(), p));
		}
	}
	
	public void close() {
		try {
			for(ClientConnectionThread th : this.clients) {
				th.close();
				th.stop();
			}
			this.clientListeners.clear();
			this.listener.close();
			this.stop();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	private void fireClientConnectEvent(ClientConnectionEvent event) {
		for(SocketServerEventListener listener : this.clientListeners) {
			listener.onClientConnect(event);
		}
	}
	
	private void fireClientDisconnectEvent(ClientConnectionEvent event) {
		for(SocketServerEventListener listener : this.clientListeners) {
			listener.onClientDisconnect(event);
		}
	}
	
	private void fireClientRecieveEvent(ClientRecieveEvent event) {
		for(SocketServerEventListener listener : this.clientListeners) {
			listener.onClientRecieve(event);
		}
	}
	
	public void addClientListener(SocketServerEventListener listener) {
		this.clientListeners.add(listener);
	}
	
	public void removeClientListener(SocketServerEventListener listener) {
		this.clientListeners.remove(listener);
	}
	
	public String getPassword() {
		return this.password;
	}
	
}