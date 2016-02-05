package org.mcteam.ancientgates.sockets;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.mcteam.ancientgates.Conf;
import org.mcteam.ancientgates.Plugin;
import org.mcteam.ancientgates.sockets.events.SocketClientEventListener;
import org.mcteam.ancientgates.sockets.types.Packet;
import org.mcteam.ancientgates.sockets.types.Packets;
import org.mcteam.ancientgates.util.TextUtil;

import com.google.gson.Gson;

public class SocketClient implements Runnable {

	private final String serverIP;
	private final int serverPort;
	private final String serverPass;
	private Socket socket;
	private boolean listening = false;
	private SocketClientEventListener listener;
	private Thread th;
	private DataOutputStream writer;
	private DataInputStream reader;

	public SocketClient(final String inServerIP, final int inServerPort, final String inServerPass) {
		this.serverIP = inServerIP;
		this.serverPort = inServerPort;
		this.serverPass = TextUtil.md5(inServerPass);
	}

	public void setListener(final SocketClientEventListener inListener) {
		this.listener = inListener;
	}

	@Override
	public void run() {
		if (this.socket != null && this.socket.isConnected()) {
			if (this.reader == null) {
				Plugin.log("Reader is null");
				return;
			}

			final long timeoutExpired = System.currentTimeMillis() + Conf.socketCommsTimeout;
			try {
				byte[] buffer = new byte[1024];
				String out = "";
				while (this.listening) {
					final int encRecievedLen = this.reader.readInt();
					if (encRecievedLen == -1 || System.currentTimeMillis() >= timeoutExpired) {
						this.listener.onServerMessageError();
						this.stopListening();
						this.close();
						Plugin.log("Connection closed");
						break;
					}
					buffer = new byte[encRecievedLen];

					this.reader.read(buffer);
					byte[] encInput = new byte[encRecievedLen];
					System.arraycopy(buffer, 0, encInput, 0, encInput.length);
					final byte[] decInput = this.de_encrypt(this.serverPass, encInput);

					final DataInputStream data = new DataInputStream(new ByteArrayInputStream(decInput));
					final int contentLen = data.readInt();
					final byte[] contentData = new byte[contentLen];
					data.readFully(contentData);
					data.close();

					out = new String(contentData);
					if (this.listener != null) {
						if (Conf.debug)
							Plugin.log(out);
						this.listener.onServerMessageRecieve(this, this.parse(out));
					}
					encInput = new byte[1024];
				}
				if (Conf.debug)
					Plugin.log("End recieve");

			} catch (final IOException e) {
				this.listener.onServerMessageError();
				this.stopListening();
				this.close();
				if (Conf.debug) {
					Plugin.log("There was an error recieving.");
					e.printStackTrace();
				}
			}
		}
	}

	public void startListening() {
		this.listening = true;
		this.th = new Thread(this);
		this.th.start();
	}

	public void stopListening() {
		this.listening = false;
	}

	public void connect() throws Exception {
		this.socket = new Socket();
		this.socket.connect(new InetSocketAddress(this.serverIP, this.serverPort), Conf.socketCommsTimeout);
		this.reader = new DataInputStream(this.socket.getInputStream());
		this.writer = new DataOutputStream(this.socket.getOutputStream());
		this.startListening();
	}

	public void close() {
		this.stopListening();
		try {
			this.socket.close();
		} catch (final IOException e) {
			Plugin.log("Error stopping client:");
			e.printStackTrace();
		}
	}

	private Packets parse(final String input) {
		Packets p = new Packets();
		final Gson g = new Gson();
		p = g.fromJson(input, Packets.class);
		return p;
	}

	public void send(final String packets) {
		final Gson g = new Gson();
		try {
			this.send(g.fromJson(packets, Packets.class));
		} catch (final Exception e) {
			Plugin.log("There was an error pasing message to send.");
		}
	}

	public void send(final Packet p) {
		final Packets ps = new Packets();
		ps.packets = new Packet[] { p };
		this.send(ps);
	}

	public void send(final Packets p) {
		if (this.writer != null) {
			try {
				final Gson gson = new Gson();
				final String json = gson.toJson(p, Packets.class);

				final byte[] content = json.getBytes("UTF-8");

				final ByteArrayOutputStream enb = new ByteArrayOutputStream();
				final DataOutputStream enOut = new DataOutputStream(enb);
				enOut.writeInt(content.length);
				enOut.write(content);
				enOut.close();
				final byte[] encData = de_encrypt(this.serverPass, enb.toByteArray());

				this.writer.writeInt(encData.length);
				this.writer.write(encData);
				this.writer.flush();
			} catch (final IOException e) {
				Plugin.log("Error while trying to send message.");
				e.printStackTrace();
				this.close();
			}
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
