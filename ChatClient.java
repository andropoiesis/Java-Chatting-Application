import java.util.Scanner;
import java.util.Date;
import java.io.PrintWriter;
import java.io.IOException;

import java.net.*;
import javax.swing.*;

import java.awt.event.*;

public class ChatClient extends JFrame implements ChatConstants
{
	private Socket socket;
	
	private Scanner fromServer;
	private PrintWriter toServer;
	
	// GUI instances
	private JTextArea chatArea;
	private JTextArea inputArea;
	private JButton enterButton;
	
	public static void main(String[] args)
	{
		boolean errorFound = false;
		String host = "localhost";
		
		int argNumber = 0;
		
		while(!errorFound && argNumber < args.length)
		{
			if(args[argNumber].equals("-server"))
			{
				argNumber++;
				
				if(args.length == argNumber || args[argNumber].charAt(0) == '-')
				{
					System.out.println("ERROR: Missing server address.");
					errorFound = true;
				}
				else
				{
					host = args[argNumber];
				}
			}
			else if(args[argNumber].equals("-help"))
			{
				System.out.println(usageString());
				errorFound = true;
			}
			
			argNumber++;
		}
		
		if(!errorFound)
		{
			ChatClient client = new ChatClient(host);
		}
	}
	
	public ChatClient(String host)
	{
		this.generateGUI();
		
		this.printToWindow("Client started on " + new Date());
		this.printToWindow("Waiting to connect to server....");
		
		this.connectToServer(host);
		
		try
		{
			try
			{
				while(!this.socket.isClosed())
				{
					String message = this.fromServer.nextLine();
					this.printToWindow(message);
				}
			}
			finally
			{
				this.closeConnection();
			}
		}
		catch(Exception exception)
		{
			exception.printStackTrace(System.err);
		}
	}
	
	private static String usageString()
	{
		String programName = ChatClient.class.getName();
		String str = "usage: ";
		
		str += "\tjava " + programName + " [-help]\n";
		str += "\t\t\t[-server hostAddress]";
		
		return str;
	}
	
	private void generateGUI()
	{
		final int WIDTH = 400;
		final int HEIGHT = 600;
		final int INPUT_WIDTH = 15;
		
		final int CHAT_ROWS = 30;
		final int CHAT_COLUMNS = 33;
		
		final int INPUT_ROWS = 3;
		final int INPUT_COLS = 26;
		
		JPanel panel = new JPanel();
		
		ActionListener inputListener = new ClientListener();
		
		this.chatArea = new JTextArea(CHAT_ROWS, CHAT_COLUMNS);
		this.chatArea.setEditable(false);
		this.chatArea.setLineWrap(true);
		JScrollPane chatScrollPane = new JScrollPane(this.chatArea);
		
		this.inputArea = new JTextArea(INPUT_ROWS, INPUT_COLS);
		this.inputArea.setLineWrap(true);
		JScrollPane inputScrollPane = new JScrollPane(this.inputArea);
		
		this.enterButton = new JButton("Enter");
		// Register listener to enter button
		this.enterButton.addActionListener(inputListener);
		
		panel.add(chatScrollPane);
		panel.add(inputScrollPane);
		panel.add(this.enterButton);
		
		this.add(panel);
		
		this.setTitle("Chat Client");
		this.setSize(WIDTH, HEIGHT);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
	}
	
	private void connectToServer(String host)
	{
		try
		{
			this.socket = new Socket(host, PORT);
			
			this.fromServer = new Scanner(this.socket.getInputStream());
			this.toServer = new PrintWriter(this.socket.getOutputStream());
			
			System.out.println("Connected to server.");
			this.printToWindow("Connected to server.");
		}
		catch(IOException exception)
		{
			exception.printStackTrace(System.err);
			this.printToWindow("ERROR: Could not connect to server.");
		}
	}
	
	private void sendText()
	{
		try
		{
			String text = this.inputArea.getText();
			System.out.println("CLIENT entered following text: " + text);
			
			this.inputArea.setText("");
			
			this.toServer.println(text);
			this.toServer.flush();
		}
		catch(Exception exception)
		{
			exception.printStackTrace(System.err);
		}
	}
	
	private void printToWindow(String str)
	{
		this.chatArea.append(str + "\n");
	}
	
	private void closeConnection() throws IOException
	{
		this.socket.close();
	}
	
	class ClientListener implements ActionListener
	{
		public void actionPerformed(ActionEvent event)
		{
			sendText();
		}
	}
}