package me.theofrancisco;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ChatClient extends JFrame implements Runnable {
	Socket socket;
	JTextArea textArea;
    javax.swing.JTextField portTf;
    JTextField userTf;
    JLabel usernameLbl;    
    
	Thread thread;
	
	DataInputStream din;
	DataOutputStream dout;
	
	String loginName;
	private int comPort=0;
	private JTextField usernameTf;
	private JButton connectBtn;
	
	ChatClient() {
		
	}
	
	private void login (String login) {		
		loginName = login;				
		
		try {
			socket = new Socket("localhost",comPort);
			din = new DataInputStream(socket.getInputStream());
			dout = new DataOutputStream (socket.getOutputStream());
			dout.writeUTF(loginName);
			dout.writeUTF(loginName+" " + " LOGIN");			
			thread = new Thread (this);
			thread.start();			
		} catch (IOException e) {	
			textArea.setText(e.getMessage());		
		}
	}
	
	@Override
	public void run(){
		while (true){
			try {
				textArea.append("\n" + din.readUTF());
			} catch (IOException e) {
				textArea.setText(e.getMessage());
			}
		}		
	}
	
	private void setup(){
	    setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(580,370);
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());
		
		 //user name gui
		 usernameLbl = new JLabel ("username: ");
		 panel.add(usernameLbl);
		 usernameTf = new JTextField ();
		 usernameTf.setColumns(20);
		 panel.add(usernameTf);
		 //server port
		 panel.add((new JLabel("Port: ")));
		 portTf = new JTextField();
		 portTf.setColumns(10);
		 panel.add(portTf);
		
		 connectBtn = new JButton ("Sign In");
		 connectBtn.addActionListener(new ActionListener()  {
			@Override
			public void actionPerformed(ActionEvent arg0) {	
				try{
				comPort = Integer.parseInt(portTf.getText());
				login(usernameTf.getText());
				}catch (java.lang.NumberFormatException e){
					textArea.setText(e.getMessage());
				}
			}			 
		 });
		
		textArea = new JTextArea (16,50);
		panel.add( new JScrollPane(textArea));
		panel.add(connectBtn);
		add(panel);		
		setVisible(true);
	}

	public static void main (String...args){
		ChatClient myApp = new ChatClient();
		myApp.setup();		
	}
}
