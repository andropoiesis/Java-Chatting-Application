import java.net.*;
import javax.swing.*;

public class ChatServer extends JFrame implements ChatConstants
{	
	private JTextArea chatLog;
	
	public static void main(String[] args)
	{
		ChatServer server = new ChatServer();
	}
	
	public ChatServer()
	{
		this.generateLog();
		
		ServerSocket chatServer = null;
		
		int numChatters = 2;
		Socket[] sockets = new Socket[numChatters];
		
		try
		{
			try
			{
				chatServer = new ServerSocket(PORT);
				int chattingRoomNumber = 1;
				
				this.report("Waiting for chatters to enter the room...");
				
				while(true)
				{
					for(int i = 0; i < numChatters; i++)
					{
						sockets[i] = chatServer.accept();
						this.report("Chatter " + i + " has entered the room.");
					}
					
					for(int i = 0; i < numChatters; i++)
					{
						ChatService service = new ChatService(i, sockets, this.chatLog);
						
						Thread t = new Thread(service);
						t.start();
					}
					
					chattingRoomNumber++;
				}
			}
			finally
			{
				for(int i = 0; i < numChatters; i++)
				{
					sockets[i].close();
				}
				
				System.out.println("Closed all connections.");
			}
		}
		catch(Exception exception)
		{
			this.report(exception.getMessage());
		}
	}
	
	private void generateLog()
	{
		final int WIDTH = 700;
		final int HEIGHT = 500;
		
		this.chatLog = new JTextArea();
		this.chatLog.setEditable(false);
		
		JScrollPane logPane = new JScrollPane(this.chatLog);
		
		this.add(logPane);
		
		this.setTitle("Chat Server Log");
		this.setSize(WIDTH, HEIGHT);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
	}
	
	private void report(String message)
	{
		this.chatLog.append(message + "\n");
	}
}