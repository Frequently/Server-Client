package Client;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JFrame;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class Client extends JFrame {

	private JTextField edittext;
	private JTextArea pane;
	private ObjectInputStream input;
	private ObjectOutputStream output;
	private Socket connection;
	String serverip="";
	
	public Client(String ip)
	{
		super("Instant Messenger");
		serverip = ip;
		
		edittext = new JTextField();
		edittext.setEditable(false);
		edittext.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				sendMessage(edittext.getText()+"\n");
				edittext.setText("");
				
			}
		});
		add(edittext,BorderLayout.NORTH);
			
		pane = new JTextArea();
		pane.setEditable(false);
		add(new JScrollPane(pane),BorderLayout.CENTER);
		setSize(300,400);
		setVisible(true);
	}
	
	public void startRunning() throws IOException
	{
		try{
			connecttoServer();
			setupStreams();
			chatting();
		}
		catch(Exception e)
		{
			showMessage("\n Connection Ended \n");
		}
		finally{
			closeStreams();
		}
	}
	
	private void closeStreams() throws IOException{
		output.close();
		input.close();
		connection.close();
	}
	
	
	private void connecttoServer() throws UnknownHostException, IOException{
		showMessage(" \n Attempting to Connect \n");
		connection = new Socket(InetAddress.getByName(serverip),6789);
		showMessage("\n Connected to " + connection.getInetAddress().getHostName()+"\n");
	}
	
	private void setupStreams() throws IOException{
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input = new ObjectInputStream(connection.getInputStream());
		showMessage(" \n Streams are now connected \n");

	}
	
	private void chatting() throws ClassNotFoundException, IOException
	{
		String message = " \n You are now connected \n";
		sendMessage(message+"\n");
		ableTotype(true);
		
		do{
			message = (String) input.readObject();
			showMessage(message+"\n");
		}
		while(!message.equals("Client - END"));

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
	 
	private void sendMessage(String message){
		
		try {
			output.writeObject("Client - "+message);
			output.flush();
			showMessage("Client - "+message);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			showMessage(" \n This message was not sent");
		}
	}
}
