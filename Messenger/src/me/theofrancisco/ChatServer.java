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
import java.util.Vector;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class ChatServer extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	static Vector<Socket> clientSockets;
	static Vector<String> loginNames;
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
			serverSocket = new ServerSocket(0); // ask the system to
														// allocate an unused
														// port
			
			assignedPort = serverSocket.getLocalPort();			
			initGUI();
			clientSockets = new Vector<>();
			loginNames = new Vector<>();

			while (true) {
				Socket clientSocket = serverSocket.accept(); 	//Listens for a connection to be made to this socket and accepts it.
				errorLbl.setText("status: Connection accepted!");
				//ChatServer_AcceptClient acceptClient = new ChatServer_AcceptClient(this,client);
				new ChatServer_AcceptClient(this,clientSocket);
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

	public void addLoginName(String loginName) {
		loginNames.add(loginName);		
	}

	public void addClientSocket(Socket clientSocket) {
		clientSockets.add(clientSocket);		
	}
	
	public Vector<Socket> getClientSockets(){
		return clientSockets;
	}

	public Vector<String> getLoginNames(){
		return loginNames;
	}
	public void showInfo(String message) {
		errorLbl.setText(message);		
	}
}
