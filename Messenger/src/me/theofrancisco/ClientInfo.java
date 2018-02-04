package me.theofrancisco;

import java.net.Socket;

public class ClientInfo {
	
	private Socket socket;
	private String username;
	
	public ClientInfo(Socket socket, String username) {
		super();
		this.socket = socket;
		this.username = username;
	}
	public Socket getSocket() {
		return socket;
	}
	public void setSocket(Socket socket) {
		this.socket = socket;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	
	
	
	
}
