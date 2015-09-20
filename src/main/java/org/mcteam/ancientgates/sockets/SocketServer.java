package org.mcteam.ancientgates.sockets;

import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.mcteam.ancientgates.Plugin;
import org.mcteam.ancientgates.sockets.events.ClientConnectionEvent;
import org.mcteam.ancientgates.sockets.events.ClientRecieveEvent;
import org.mcteam.ancientgates.sockets.events.SocketServerEventListener;
import org.mcteam.ancientgates.sockets.types.ConnectionState;
import org.mcteam.ancientgates.sockets.types.Packet;
import org.mcteam.ancientgates.sockets.types.Packets;
import org.mcteam.ancientgates.util.TextUtil;

import com.google.gson.Gson;

public class SocketServer implements Runnable {

	private final int maxClient;
	private final String password;
	private ServerSocket listener;
	private final List<ClientConnectionThread> clients;
	private Thread thread;
	private final List<SocketServerEventListener> clientListeners;
	private boolean isRunning;
	private final Set<Integer> ids;

	public SocketServer(final int clientCount, final int port, final String password) throws BindException {
		this.maxClient = clientCount;
		this.password = TextUtil.md5(password);
		try {
			this.listener = new ServerSocket(port);
			this.start();
			Plugin.log("Server started on port " + port + ".");
		} catch (final BindException e) {
			throw new BindException();
		} catch (final IOException e) {
			Plugin.log("Error starting listener on port " + port + ".");
			e.printStackTrace();
		}
		this.clients = new ArrayList<>();
		this.clientListeners = new ArrayList<>();
		this.ids = new HashSet<>();
	}

	public synchronized void removeClient(final int id) {
		final ClientConnectionThread th = this.clients.get(this.getClientIndex(id));
		this.fireClientDisconnectEvent(new ClientConnectionEvent(th.getSocket(), id, ConnectionState.DISCONNECTED));
		th.stop();

		this.clients.remove(this.getClientIndex(id));
		this.ids.remove(id);
	}

	@Override
	public void run() {
		while (thread != null && this.isRunning) {
			try {
				this.addThread(this.listener.accept());
			} catch (final IOException e) {
				Plugin.log("Error while accepting client.");
			}
		}
	}

	private int getClientIndex(final int id) {
		for (int i = 0; i < this.clients.size(); i++) {
			if (this.clients.get(i).getID() == id)
				return i;
		}
		return -1;
	}

	private void addThread(final Socket client) {
		if (this.clients.size() >= this.maxClient && this.maxClient > 0) {
			Plugin.log("Refused client: maximum reached.");
			return;
		}

		final ClientConnectionThread th = new ClientConnectionThread(this.getNewID(), this, client);
		this.fireClientConnectEvent(new ClientConnectionEvent(client, th.getID(), ConnectionState.CONNECTED));
		this.clients.add(th);

	}

	public void start() {
		if (thread == null) {
			this.isRunning = true;
			thread = new Thread(this);
			thread.start();
		}
	}

	public void stop() {
		if (thread != null) {
			this.isRunning = false;
			try {
				this.listener.close();
				Plugin.log("Server stopped.");
			} catch (final IOException e) {
				Plugin.log("Error while closing server socket.");
			}
			thread = null;
		}
	}

	public void sendToClient(final int client, final Packet data) {
		final Packets p = new Packets();
		p.packets = new Packet[] { data };
		this.sendToClient(client, p);
	}

	public void sendToClient(final int client, final Packets data) {
		final Gson gson = new Gson();
		final String json = gson.toJson(data, Packets.class);

		final int clientIndex = this.getClientIndex(client);
		if (clientIndex < 0)
			return;

		final ClientConnectionThread th = this.clients.get(clientIndex);
		th.send(json);
	}

	private int getNewID() {
		int found = -1;
		int current = 0;
		do {
			current = this.maxClient > 0 ? (int) (Math.random() * this.maxClient) : (int) (Math.random() * ((this.clients.size() + 1) * 2));
			if (this.isIDAvaible(current)) {
				found = current;
			}
		} while (found == -1);

		return found;
	}

	private boolean isIDAvaible(final int id) {
		return !this.ids.contains(id);
	}

	public void handle(final int client, final String input) {
		final Gson gson = new Gson();
		final Packets packets = gson.fromJson(input, Packets.class);
		for (final Packet p : packets.packets) {
			this.fireClientRecieveEvent(new ClientRecieveEvent(client, this.clients.get(this.getClientIndex(client)).getSocket(), p));
		}
	}

	public void close() {
		try {
			for (final ClientConnectionThread th : this.clients) {
				th.close();
				th.stop();
			}
			this.clientListeners.clear();
			this.listener.close();
			this.stop();
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	private void fireClientConnectEvent(final ClientConnectionEvent event) {
		for (final SocketServerEventListener listener1 : this.clientListeners) {
			listener1.onClientConnect(event);
		}
	}

	private void fireClientDisconnectEvent(final ClientConnectionEvent event) {
		for (final SocketServerEventListener listener1 : this.clientListeners) {
			listener1.onClientDisconnect(event);
		}
	}

	private void fireClientRecieveEvent(final ClientRecieveEvent event) {
		for (final SocketServerEventListener listener1 : this.clientListeners) {
			listener1.onClientRecieve(event);
		}
	}

	public void addClientListener(final SocketServerEventListener listener1) {
		this.clientListeners.add(listener1);
	}

	public void removeClientListener(final SocketServerEventListener listener1) {
		this.clientListeners.remove(listener1);
	}

	public String getPassword() {
		return this.password;
	}

}
