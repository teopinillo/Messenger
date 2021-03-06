package me.theofrancisco;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.util.Date;

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
    JButton sendBtn;
    
	Thread thread;
	
	DataInputStream din;
	DataOutputStream dout;
	
	String loginName;	
	private JTextField usernameTf;
	private JButton connectBtn;
	private JButton logoutBtn;
	
	@Override
	public void run(){
		while (true){
			try {
				textArea.append("\n" + din.readUTF());
			} catch (IOException e) {
				textArea.append(e.getMessage()+"\n");
			}
			try {
				Thread.sleep (1000);
			} catch (InterruptedException e) {
				textArea.append(e.getMessage()+"\n");
			}
		}		
	}
	
	private void login (String _loginname, int commPort) {		
		loginName = _loginname;				
		
		try {
			socket = new Socket("localhost",commPort);
			din = new DataInputStream(socket.getInputStream());
			dout = new DataOutputStream (socket.getOutputStream());			
			dout.writeUTF(loginName +" LOGIN " + ".");			
			thread = new Thread (this);
			thread.start();			
		} catch (IOException e) {	
			textArea.append(e.getMessage()+"\n");		
		}
	}
	
	private void setup(){
	    setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(640,440);
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
		
		 sendBtn = new JButton ("Send >");
		 JTextField tfMessage = new JTextField();
		 tfMessage.setColumns(48);
		 tfMessage.setSize(50, tfMessage.getPreferredSize().width);
		 
		 logoutBtn = new JButton ("logout");
		 connectBtn = new JButton ("Sign In");
		 
		 panel.add(connectBtn);
		 panel.add(logoutBtn);
		 
		 //sign in button action
		 connectBtn.addActionListener(new ActionListener()  {
			@Override
			public void actionPerformed(ActionEvent arg0) {	
				try{
				if (usernameTf.getText().length()<1) return;	
				  int commPort = Integer.parseInt(portTf.getText());
				  login(usernameTf.getText(), commPort);
				}catch (java.lang.NumberFormatException e){
					textArea.append(e.getMessage()+"\n");
				}
			}			 
		 });
		 
		 //send button action
		 sendBtn.addActionListener ( new ActionListener()  {
			 public void actionPerformed(ActionEvent e){
				 try {
					String outputMessage = loginName +" DATA " + tfMessage.getText();
					dout.writeUTF(outputMessage);
					System.out.println("sending message: "+outputMessage );
					System.out.println("Button send pressed");
					tfMessage.setText("");
				} catch (IOException ex) {
					textArea.append(ex.getMessage()+"\n");
				} catch (NullPointerException ex){					
					textArea.append("Please log in first\n");
				}
			 }
		 } );
		 
		 //logout button action
		 logoutBtn.addActionListener ( new ActionListener()  {
			 public void actionPerformed(ActionEvent e){
				 try {
					dout.writeUTF(loginName + " " +" LOGOUT " + textArea.getText());
				} catch (IOException ex) {
					textArea.append(ex.getMessage()+"\n");
				}
			 }
		 } );
		 		 
		
		textArea = new JTextArea (20,55);
		panel.add( new JScrollPane(textArea));
		
		//Button Part
		JPanel buttonPanel = new JPanel ();
		 FlowLayout buttonLayout = new FlowLayout();
		  buttonPanel.setLayout(buttonLayout);
		   buttonPanel.add(tfMessage);
		    buttonPanel.add(sendBtn);
		   panel.add(buttonPanel);
		
		add(panel);		
		setVisible(true);
	}

	public static void main (String...args){
		ChatClient myApp = new ChatClient();
		myApp.setup();		
	}
}
