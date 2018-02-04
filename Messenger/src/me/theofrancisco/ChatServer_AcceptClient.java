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
		//private DataOutputStream dout;
		private ChatServer chatServer;
		private Vector<String> loginNames;
		private Vector<Socket> clientSockets;

		ChatServer_AcceptClient(ChatServer _chatServer, Socket _clientSocket) {
			clientSocket = _clientSocket;
			chatServer = _chatServer;
			if (chatServer==null){
				System.out.println("Error Critical: ChatSever is null!");
				return;
			}
			
			try {
				din = new DataInputStream(clientSocket.getInputStream());	
					
				String msgFromClient = din.readUTF();
				System.out.println(this.getClass().getName()+" :msgFromCLient: "+msgFromClient);
				String[] messages = msgFromClient.split("\\s");				
				String loginName = messages[0];		
				System.out.println(this.getClass().getName()+" :msgFromCLient: "+loginName);
				chatServer.showInfo ("status: username: "+loginName+" request login.");	
				
				 if (loginName!=null){
					 chatServer.addLoginName(loginName);
					 chatServer.addClientSocket (clientSocket);
					 for (Socket socket: chatServer.getClientSockets() ){
						 DataOutputStream outData = new DataOutputStream(socket.getOutputStream());
							outData.writeUTF("user: "+loginName +" has login.");
						}
						
				 }
							
				start();

			} catch (IOException e) {
				chatServer.showInfo (e.getMessage());					
			}
		}

		public void run() {
			while (true) {
				try {
					Thread.sleep(500);
					String msgFromClient = din.readUTF();
					StringTokenizer st = new StringTokenizer(msgFromClient);
					String loginName = st.nextToken();
					String msgType = st.nextToken();
					String message = st.nextToken();
					
					while (st.hasMoreTokens()) message+=st.nextToken();				

					if (msgType == "LOGIN") {
						// inform to all socket for the new login
						
						loginNames = chatServer.getLoginNames();
						clientSockets = chatServer.getClientSockets();
						
						for (int i = 0; i < loginNames.size(); i++) {
							Socket pSocket = (Socket) clientSockets.elementAt(i);
							DataOutputStream pOut = (DataOutputStream) pSocket.getOutputStream();
							pOut.writeUTF(loginNames.elementAt(i) + " has login in.");
						}					
					} else if (msgType == "LOGOUT") {
						// inform to all socket for the logout
						int i = loginNames.indexOf(loginName);
						loginNames.remove(i);
						clientSockets.remove(i);
						
						for (i = 0; i < loginNames.size(); i++) {
							Socket pSocket = (Socket) clientSockets.elementAt(i);
							DataOutputStream pOut = (DataOutputStream)pSocket.getOutputStream();
							pOut.writeUTF(loginNames.elementAt(i) + " has logged out.");
						}
					} else if (msgType == "DATA") {
						// inform to all socket for the new message
						for (int i = 0; i < loginNames.size(); i++) {
							Socket pSocket = (Socket) clientSockets.elementAt(i);
							DataOutputStream pOut = (DataOutputStream) pSocket.getOutputStream();
							pOut.writeUTF(loginNames.elementAt(i) + " : "+message);
						}
					}
				} catch (IOException | InterruptedException e) {
					chatServer.showInfo (e.getMessage());						
				}

			}
		}
	

}
