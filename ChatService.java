import java.util.*;
import java.io.PrintWriter;
import java.io.IOException;

import java.net.*;

import javax.swing.JTextArea;

public class ChatService implements Runnable
{
	private int clientOrder;
	private Socket[] clientList;
	private JTextArea log;
	
	private Scanner fromClient;
	private PrintWriter[] toClientList;
	
	public ChatService(int order, Socket[] connections, JTextArea serverLog)
	{
		this.clientOrder = order;
		this.clientList = connections;
		this.log = serverLog;
		
		this.toClientList = new PrintWriter[this.clientList.length];
		
		this.openStreams();
	}
	
	private void openStreams()
	{
		try
		{
			this.fromClient = new Scanner(this.clientList[this.clientOrder].getInputStream());
			
			for(int i = 0; i < this.toClientList.length; i++)
			{
				this.toClientList[i] = new PrintWriter(this.clientList[i].getOutputStream());
			}
		}
		catch(Exception exception)
		{
			this.printToLog(exception.getMessage());
		}
	}
	
	private void printToLog(String logInfo)
	{
		this.log.append(logInfo + "\n");
	}
	
	private void sendToClients(String message)
	{
		for(int client = 0; client < this.clientList.length; client++)
		{
			if(!this.clientList[client].isClosed())
			{
				this.toClientList[client].println(message);
				this.toClientList[client].flush();
			}
		}
	}
	
	public void run()
	{
		try
		{
			while(true)
			{
				String clientMessage = "CLIENT " + this.clientOrder + ": "
												+ this.fromClient.nextLine();
				
				this.printToLog("CLIENT " + this.clientOrder + " sent: " + clientMessage);
				
				this.sendToClients(clientMessage);
			}
		}
		catch(Exception exception)
		{
			if(exception.getClass().equals(new NoSuchElementException().getClass()))
			{
				String disconnectMsg = "CLIENT " + this.clientOrder + " HAS DISCONNECTED.";
				this.printToLog(disconnectMsg);
				this.sendToClients(disconnectMsg);
			}
			else
			{
				this.printToLog("CLIENT " + this.clientOrder + ": " + exception.getMessage());
			}
		}
	}
}