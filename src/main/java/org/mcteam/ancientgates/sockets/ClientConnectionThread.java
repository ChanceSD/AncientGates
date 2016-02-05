package org.mcteam.ancientgates.sockets;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import org.mcteam.ancientgates.Conf;
import org.mcteam.ancientgates.Plugin;

public class ClientConnectionThread implements Runnable {

	private final SocketServer server;
	private final Socket clientSocket;
	private DataInputStream inStream;
	private DataOutputStream outStream;
	private final int ID;
	private Thread thread;
	private boolean isRunning;

	public ClientConnectionThread(final int id, final SocketServer server, final Socket socket) {
		this.server = server;
		this.clientSocket = socket;
		this.ID = id;
		this.start();
	}

	public int getID() {
		return this.ID;
	}

	public Socket getSocket() {
		return this.clientSocket;
	}

	public void send(final String message) {
		try {
			final byte[] content = message.getBytes("UTF-8");

			final ByteArrayOutputStream enb = new ByteArrayOutputStream();
			final DataOutputStream enOut = new DataOutputStream(enb);
			enOut.writeInt(content.length);
			enOut.write(content);
			enOut.close();
			final byte[] encData = de_encrypt(this.server.getPassword(), enb.toByteArray());

			this.outStream.writeInt(encData.length);
			this.outStream.write(encData);
			this.outStream.flush();
		} catch (final IOException e) {
			Plugin.log("Error while trying to send message.");
			e.printStackTrace();
			this.server.removeClient(this.ID);
			this.stop();
		}
	}

	public void open() throws IOException {
		this.inStream = new DataInputStream(new BufferedInputStream(this.clientSocket.getInputStream()));
		this.outStream = new DataOutputStream(new BufferedOutputStream(this.clientSocket.getOutputStream()));
	}

	public void close() throws IOException {
		if (this.clientSocket != null)
			this.clientSocket.close();
		if (this.inStream != null)
			this.inStream.close();
		if (this.outStream != null)
			this.outStream.close();
	}

	public void start() {
		try {
			this.open();
		} catch (final IOException e) {
			e.printStackTrace();
		}

		if (thread == null) {
			this.isRunning = true;
			this.thread = new Thread(this);
			this.thread.start();
		}
	}

	public void stop() {
		try {
			this.close();
		} catch (final IOException e) {
			e.printStackTrace();
		}

		if (thread != null) {
			this.isRunning = false;
			thread = null;
		}
	}

	public void shutdown() {
		this.stop();
	}

	@Override
	public void run() {
		try {
			byte[] buffer = new byte[1024];
			String out = "";
			while (this.isRunning) {
				final int encRecievedLen = this.inStream.readInt();
				if (encRecievedLen == -1) {
					this.isRunning = false;
					this.server.removeClient(this.ID);
					stop();
					break;
				}
				buffer = new byte[encRecievedLen];

				this.inStream.read(buffer);
				byte[] encInput = new byte[encRecievedLen];
				System.arraycopy(buffer, 0, encInput, 0, encInput.length);
				final byte[] decInput = this.de_encrypt(this.server.getPassword(), encInput);

				final DataInputStream data = new DataInputStream(new ByteArrayInputStream(decInput));
				final int contentLen = data.readInt();
				// If > 1MB, throw exception - Probable cause, wrong encryption
				if (contentLen > 1048576)
					throw new IOException();
				// Read content data
				final byte[] contentData = new byte[contentLen];
				data.readFully(contentData);
				data.close();

				out = new String(contentData);
				if (Conf.debug)
					Plugin.log(out);
				this.server.handle(this.ID, out);
				encInput = new byte[1024];
			}
		} catch (final IOException e) {
			if (Conf.debug)
				Plugin.log("Lost connection to client " + this.ID + ".");
			this.server.removeClient(this.ID);
			stop();
		}
	}

	private byte[] de_encrypt(final String password, final byte[] data) {
		final byte[] result = new byte[data.length];
		final byte[] pB = password.getBytes();
		for (int i = 0; i < data.length; i++) {
			result[i] = (byte) (data[i] ^ pB[i % pB.length]);
		}
		return result;
	}

}
