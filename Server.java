package Jarvis;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class Jarvis extends JFrame{
	

	private ServerSocket Server;
	private JTextField edittext;
	private JTextArea pane;
	private Socket connection;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	
	public Jarvis(){
		super("Server");
		edittext = new JTextField();
		add(edittext,BorderLayout.NORTH);
		edittext.setEditable(false);
		ableTotype(false);
		edittext.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				sendMessage(edittext.getText()+"\n");
				edittext.setText("");
			}
		});
		pane = new JTextArea();
		pane.setEditable(false);
		add(new JScrollPane(pane));
		setSize(300,400);
		setVisible(true);
	}
	
	public void startRunning() throws IOException
	{
		Server = new ServerSocket(6789,100);
		while(true){
		try{
			WaitingforConnection();
			setupSteam();
			whileChatting();
			ableTotype(false);
		}
		catch(Exception e)
		{
			showMessage("\n Connection Ended \n");
		}
		finally{
			closeall();
		}
		}
	}
	
	private void WaitingforConnection(){
		try {
			showMessage(" Waiting for someone to connect");
			connection = Server.accept();
			showMessage(" Connected to " + connection.getInetAddress().getHostName() +" \n");
				
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
  
	private void setupSteam() throws IOException{
			output = new ObjectOutputStream(connection.getOutputStream());
			output.flush();
			input = new ObjectInputStream(connection.getInputStream());
			showMessage(" Streams are now connected \n");
	}
	
	private void whileChatting() throws ClassNotFoundException, IOException{
	
//		String message = "You are now connected";
//		sendMessage(message);
		ableTotype(true);
		String message ="";
		do{
			 message = (String) input.readObject();
			showMessage(message+"\n");
		}
		while(!message.equals("Client - END"));
		
	}
	
	private void closeall()
	{
		try {
			input.close();
			output.close();
			connection.close();
		}
	 catch (IOException e) {
		e.printStackTrace();
	 	}
	}
	
	private void sendMessage(String message){
		
		try {
			output.writeObject("Server "+message);
			output.flush();
			showMessage("Server "+message);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			showMessage(" \n This message was not sent \n");
		}
	}
	
	private void showMessage(String message){
		SwingUtilities.invokeLater( 
				new Runnable()
				{
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						pane.append(message);
					}
				});
	}
	
	private void ableTotype(Boolean to){
		SwingUtilities.invokeLater( 
				new Runnable()
				{
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						edittext.setEditable(to);
					}
				});
		
	}
}
