package me.theofrancisco;

import java.awt.BorderLayout;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/*
 * References
 * What is a Socket: https://docs.oracle.com/javase/tutorial/networking/sockets/definition.html
 * ServerSocket: https://docs.oracle.com/javase/7/docs/api/java/net/ServerSocket.html
 * Oracle Doc: https://docs.oracle.com/javase/tutorial/networking/sockets/index.html
 * 
 */

import java.net.ServerSocket;
import java.net.Socket;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JLabel;

public class ChatServer extends JFrame {
	static Vector<Socket> clientSockets;
	static Vector<String> loginNames;
	private int assignedPort;
	JLabel errorLbl;
	
	ChatServer(){
		/*
		 * A socket is one endpoint of a two-way communication link between two programs running on the network. 
		 * A socket is bound to a port number so that the TCP layer can identify the application that data is destined to be sent to.
		 */
		try {
			ServerSocket server = new ServerSocket(0); //ask the system to allocate an unused port
			assignedPort = server.getLocalPort();
			System.out.println ("Assigned Port: "+assignedPort);
			initGUI();
			clientSockets = new Vector<>();
			loginNames = new Vector<>();
			
			while (true){
				Socket client = server.accept();
				AcceptClient acceptClient = new AcceptClient (client);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void initGUI (){
		setLayout (new BorderLayout());
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize (250,100);
		setTitle("Chat Server");
		JLabel portLbl = new JLabel ("Assigned Port: "+Integer.toString(assignedPort));
		errorLbl = new JLabel ("status: ok");
		add(portLbl,BorderLayout.NORTH);
		add(errorLbl,BorderLayout.CENTER);		
		setVisible(true);
	}
	
	public static void main (String...args){
		ChatServer server = new ChatServer();		
	}
	
	
	class AcceptClient extends Thread {
		Socket clientSocket;
		DataInputStream din;
		DataOutputStream dout;
		
		AcceptClient (Socket client){
			clientSocket = client;
			try {
				din = new DataInputStream (clientSocket.getInputStream());
				dout = new DataOutputStream (clientSocket.getOutputStream());
				
				String loginName = din.readUTF();
				loginNames.add(loginName);				
				clientSockets.add(clientSocket);				
				start();
				
			} catch (IOException e) {
				errorLbl.setText (e.getMessage());
			}			
		}
		
		public void run(){
			while(true){
				try {
					String msgFromClient = din.readUTF();
					StringTokenizer st =  new StringTokenizer( msgFromClient);
					String loginName = st.nextToken();					
					String msgType = st.nextToken();
					
					//inform to all socket for the new login
					for (int i=0; i<loginNames.size();i++){
						Socket pSocket = (Socket) clientSockets.elementAt(i);
						DataOutputStream pOut = new DataOutputStream (pSocket.getOutputStream());
						pOut.writeUTF(loginName + " has login in.");
					}
					
				} catch (IOException e) {
					errorLbl.setText(e.getMessage());
				}
				
			}
		}
	}
}

