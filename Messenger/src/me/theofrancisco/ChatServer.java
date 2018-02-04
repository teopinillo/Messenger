package me.theofrancisco;

/*
 * References
 * What is a Socket: https://docs.oracle.com/javase/tutorial/networking/sockets/definition.html
 * ServerSocket: https://docs.oracle.com/javase/7/docs/api/java/net/ServerSocket.html
 * Oracle Doc: https://docs.oracle.com/javase/tutorial/networking/sockets/index.html
 * 
 */

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.JFrame;
import javax.swing.JLabel;

public class ChatServer extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private AtomicInteger clientCount;
	private static Hashtable<Integer, ClientInfo> clientInfo;

	private int assignedPort;
	JLabel errorLbl;
	private ServerSocket serverSocket;
	private static ChatServer chatServer;

	ChatServer() {
		/*
		 * A socket is one endpoint of a two-way communication link between two
		 * programs running on the network. A socket is bound to a port number
		 * so that the TCP layer can identify the application that data is
		 * destined to be sent to.
		 */
		try {
			clientCount = new AtomicInteger(0);
			serverSocket = new ServerSocket(0); // ask the system to
														// allocate an unused
														// port
			
			assignedPort = serverSocket.getLocalPort();			
			initGUI();
			clientInfo = new Hashtable <Integer,ClientInfo> ();

			while (true) {
				Socket clientSocket = serverSocket.accept(); 	//Listens for a connection to be made to this socket and accepts it.
				errorLbl.setText("status: Connection accepted!");
				int id = clientCount.incrementAndGet();
				new ChatServer_AcceptClient(this,clientSocket,id);
			    Thread.sleep(500);
			}
		} catch (IOException | InterruptedException e) {
			errorLbl.setText(e.getMessage());
		}
	}
	
		
	private void initGUI() {
		
		//close the socket on exit
		addWindowListener( new WindowAdapter () {
			public void windowClosing (WindowEvent e){
				try {
					serverSocket.close();
				} catch (IOException e1) {					
					e1.printStackTrace();
				}
			}			
		});
		
		setLayout(new BorderLayout());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(250, 100);
		setTitle("Chat Server");
		JLabel portLbl = new JLabel("Assigned Port: " + Integer.toString(assignedPort));
		errorLbl = new JLabel("status: ok");
		add(portLbl, BorderLayout.NORTH);
		add(errorLbl, BorderLayout.CENTER);
		setVisible(true);
	}

	public static void main(String... args) {
		chatServer = new ChatServer();
	}
	
	public void showInfo(String message) {
		errorLbl.setText(message);		
	}

	public void addClient(int clientId, String loginName, Socket clientSocket) {
		clientInfo.put(clientId, new ClientInfo (clientSocket, loginName));		
	}


	public List<Socket> getClientSockets() {
		ArrayList<Socket> sockets = new ArrayList<>();
		for (ClientInfo info:clientInfo.values()){
			sockets.add(info.getSocket());
		}
		return sockets;
	}


	public void removeClient(String loginName) {
		// TODO Auto-generated method stub
		
	}
}
