package org.mcteam.ancientgates.sockets;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import org.bukkit.craftbukkit.libs.com.google.gson.Gson;

import org.mcteam.ancientgates.Conf;
import org.mcteam.ancientgates.Plugin;
import org.mcteam.ancientgates.sockets.events.SocketClientEventListener;
import org.mcteam.ancientgates.sockets.packets.Packet;
import org.mcteam.ancientgates.sockets.packets.Packets;
import org.mcteam.ancientgates.util.TextUtil;

public class SocketClient implements Runnable {
	
	private String serverIP;
	private int serverPort;
	private String serverPass;
	private Socket socket;
	private boolean listening = false;
	private SocketClientEventListener listener;
	private Thread th;
	private DataOutputStream writer;
	private DataInputStream reader;
	
	public SocketClient(String inServerIP, int inServerPort, String inServerPass) {
		this.serverIP = inServerIP;
		this.serverPort = inServerPort;
		this.serverPass = TextUtil.md5(inServerPass);
	}
	
	public void setListener(SocketClientEventListener inListener) {
		this.listener = inListener;
	}
	
	public void run() {
		if(this.socket != null && this.socket.isConnected()) {
			if(this.reader == null) {
				Plugin.log("Reader is null");
				return;
			}
			
			try {
				byte[] buffer = new byte[1024];
				String out = "";
				while(this.listening) {
					int encRecievedLen = this.reader.readInt();
					buffer = new byte[encRecievedLen];
					int encRecieved = this.reader.read(buffer);
					if(encRecieved == -1) {
						this.stopListening();
						this.close();
						Plugin.log("Connection closed");
						break;
					}
					byte[] encInput = new byte[encRecievedLen];
					System.arraycopy(buffer, 0, encInput, 0, encInput.length);
					byte[] decInput = this.de_encrypt(this.serverPass, encInput);
					
					DataInputStream data = new DataInputStream(new ByteArrayInputStream(decInput));
					int contentLen = data.readInt();
					byte[] contentData = new byte[contentLen];
					data.readFully(contentData);
					data.close();
					
					out = new String(contentData);
					if(this.listener != null) {
						if (Conf.debug) Plugin.log(out);
						this.listener.onServerMessageRecieve(this, this.parse(out));
					}
					buffer = new byte[1024];
				}
				if (Conf.debug) Plugin.log("End recieve");
				
			} catch (IOException e) {
				Plugin.log("There was an error recieving.");
				e.printStackTrace();
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
		this.socket = new Socket(this.serverIP, this.serverPort);
		this.reader = new DataInputStream(this.socket.getInputStream());
		this.writer = new DataOutputStream(this.socket.getOutputStream());
		this.startListening();
	}
	
	public void close() {
		this.stopListening();
		try {
			this.socket.close();
		} catch (IOException e) {
			Plugin.log("Error stopping client:");
			e.printStackTrace();
		}
	}
	
	private Packets parse(String input) {
		Packets p = new Packets();
		Gson g = new Gson();
		p = g.fromJson(input, Packets.class);
		return p;
	}
	
	public void send(String packets) {
		Gson g = new Gson();
		try {
			this.send(g.fromJson(packets, Packets.class));
		} catch(Exception e) {
			Plugin.log("There was an error pasing message to send.");
		}
	}
	
	public void send(Packet p) {
		Packets ps = new Packets();
		ps.packets = new Packet[] { p };
		this.send(ps);
	}
	
	public void send(Packets p) {
		if(this.writer != null) {
			try {
				Gson gson = new Gson();
				String json = gson.toJson(p, Packets.class);
			
				byte[] content = json.getBytes("UTF-8");
			
		    	ByteArrayOutputStream enb = new ByteArrayOutputStream();
		    	DataOutputStream enOut = new DataOutputStream(enb);
		    	enOut.writeInt(content.length);
		    	enOut.write(content);
		    	enOut.close();
		    	byte[] encData = de_encrypt(this.serverPass, enb.toByteArray());
						
				this.writer.writeInt(encData.length);
				this.writer.write(encData);
				this.writer.flush();
			} catch(IOException e) {
				Plugin.log("Error while trying to send message.");
				e.printStackTrace();
				this.close();
			}
		}
	}
	
	private byte[] de_encrypt(String password, byte[] data) {
		byte[] result = new byte[data.length];
		byte[] pB = password.getBytes();
		for (int i = 0; i < data.length; i++) {
			result[i] = ((byte)(data[i] ^ pB[(i % pB.length)]));
		}
		return result;
	}
	
}