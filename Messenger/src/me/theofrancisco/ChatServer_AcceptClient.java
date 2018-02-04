package me.theofrancisco;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.StringTokenizer;
import java.util.Vector;

public class ChatServer_AcceptClient extends Thread {

	private Socket clientSocket;
	private DataInputStream din;
	// private DataOutputStream dout;
	private ChatServer chatServer;
	private Vector<String> loginNames;
	private Vector<Socket> clientSockets;

	ChatServer_AcceptClient(ChatServer _chatServer, Socket _clientSocket, int clientId) {
		clientSocket = _clientSocket;
		chatServer = _chatServer;
		if (chatServer == null) {
			System.out.println("Error Critical: ChatSever is null!");
			return;
		}

		try {
			din = new DataInputStream(clientSocket.getInputStream());

			String msgFromClient = din.readUTF();
			System.out.println(this.getClass().getName() + " :msgFromCLient: " + msgFromClient);
			String[] messages = msgFromClient.split("\\s");
			String loginName = messages[0];
			System.out.println(this.getClass().getName() + " :msgFromCLient: " + loginName);
			chatServer.showInfo("status: username: " + loginName + " request login.");

			if (loginName != null) {
				chatServer.addClient(clientId, loginName, clientSocket);
				//chatServer.addLoginName(loginName);
				//chatServer.addClientSocket(clientSocket);
				for (Socket socket : chatServer.getClientSockets()) {
					DataOutputStream outData = new DataOutputStream(socket.getOutputStream());
					outData.writeUTF("user: " + loginName + " has login.");
				}

			}

			start();

		} catch (IOException e) {
			chatServer.showInfo(e.getMessage());
		}
	}

	public void run() {
		String loginName;
		String msgType;
		String message;

		while (true) {
			try {
				Thread.sleep(1000);
				String msgFromClient = din.readUTF();
				if (msgFromClient.length() > 0) {
					String messages[] = msgFromClient.split("\\s");
					loginName = messages[0];
					msgType = messages[1];
					messages[0] = "";
					messages[1] = "";
					message = "";
					for (String s : messages)
						message += s;
					System.out.println("data recived");
					System.out.println("loginName: " + loginName);
					System.out.println("msgType: " + msgType);
					System.out.println("message: " + message);
					//loginNames = chatServer.getLoginNames();
					//clientSockets = chatServer.getClientSockets();
					
					switch (msgType) {
					case "LOGIN": {
						// inform to all socket for the new login
						for (Socket socket: chatServer.getClientSockets() ) {							
							DataOutputStream pOut = new DataOutputStream ( socket.getOutputStream()) ;
							pOut.writeUTF(loginName + " has login in.");
							pOut.close();
						}
						break;
					}
					case "LOGOUT": {
						// inform to all socket for the logout
						chatServer.removeClient(loginName);						

						for (Socket socket: chatServer.getClientSockets()) {							
							DataOutputStream pOut = new DataOutputStream ( socket.getOutputStream()) ;
							pOut.writeUTF(loginName + " has logged out.");
							pOut.close();
						}
						break;
					}
					case "DATA": {
						System.out.println("\nProcessing Data");
						// inform to all socket for the new message
						for (Socket socket: chatServer.getClientSockets()) {							
							DataOutputStream pOut = new DataOutputStream ( socket.getOutputStream()) ;
							pOut.writeUTF(loginName + ": " + message);
							pOut.close();
						}
						break;
					}
					}
				}

			} catch (IOException | InterruptedException e) {
				chatServer.showInfo(e.getMessage());
			}

		}
	}

}
