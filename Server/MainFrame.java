import java.awt.*;
import java.text.*;
import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.table.*;
import javax.swing.event.*;
import java.lang.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;
import java.io.PrintWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.awt.dnd.*;
import java.awt.datatransfer.*;
import javax.imageio.*;
import java.net.ServerSocket;
import java.net.Socket;


//##############################################################################

class MainFrame extends JFrame implements ActionListener
{
        Container cp;
		
		CommandHandler ch;
        
		JScrollPane scroller;
		
        JPanel inputPanel;
        JPanel buttonPanel;
       	
		JLabel messageLabel;
		static JTextArea messageField;
        
        JButton exitButton;
        JButton sendButton;
		
		static JButton tellClientToExitButton;
		static JButton tellClientToSearchButton;
		static JButton sendClientMessageButton;

      	ServerSocket serverSocket;
		Socket normalSocket;
		
		Talker talker;
	  
        String fileName;
        String toAdd;
        String strArray[];
		String msg;
		
		DataOutputStream dos;
		DataInputStream dis;
		
    
    //============================Constructor===================================
    MainFrame()
    {
        
        
		
		messageLabel = new JLabel("Message: ");
		messageField = new JTextArea("Your message here!");
		
		messageField.setLineWrap(true);
		
		scroller = new JScrollPane(messageField);
		
		//messageField.setPreferredSize(new Dimension(200, 150));
		
        inputPanel = new JPanel();
		
		sendClientMessageButton = new JButton("Send Message");
		tellClientToSearchButton = new JButton("Make Client Start Searching");
		tellClientToExitButton = new JButton("Shutdown Client");
        exitButton = new JButton("Shutdown Server");
        

        
        sendClientMessageButton.setActionCommand("SENDMESSAGE");
        sendClientMessageButton.addActionListener(this);
		sendClientMessageButton.setEnabled(false);
        tellClientToSearchButton.setActionCommand("STARTSEARCH");
        tellClientToSearchButton.addActionListener(this);
		tellClientToSearchButton.setEnabled(false);
        tellClientToExitButton.setActionCommand("SHUTDOWNCLIENT");
        tellClientToExitButton.addActionListener(this);
		tellClientToExitButton.setEnabled(false);
        exitButton.setActionCommand("EXIT");
        exitButton.addActionListener(this);
		
        
        buttonPanel = new JPanel();
        
        buttonPanel.add(sendClientMessageButton);
		buttonPanel.add(tellClientToSearchButton);
		buttonPanel.add(tellClientToExitButton);
		        
     
        buttonPanel.add(exitButton);
        
		
		GroupLayout layout = new GroupLayout(inputPanel); 
		inputPanel.setLayout(layout);
		
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		
		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
		
		hGroup.addGroup(layout.createParallelGroup()
				.addComponent(messageLabel));
		hGroup.addGroup(layout.createParallelGroup()
				.addComponent(scroller));
		
		layout.setHorizontalGroup(hGroup);
		
		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();
		
		vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(messageLabel)
				.addComponent(scroller));
		
		layout.setVerticalGroup(vGroup);
		
        cp = getContentPane();
        cp.add(buttonPanel, BorderLayout.SOUTH);
        cp.add(inputPanel, BorderLayout.CENTER);
        
        
		

        setupMainFrame(40, 40, "Logan Davis - Project 5 - Trojan Software - SERVER");
		
		
		//Begin Setting up our SreverSocket to accept the client's connection.
 		System.out.println("Attempting to Start up ServerSocket on Port 1234...");
		try
		{
			serverSocket = new ServerSocket(1234);
			while(true)
			{
				try
				{
					normalSocket = serverSocket.accept();
					talker = new Talker(normalSocket);
					ch = new CommandHandler(talker, sendClientMessageButton, tellClientToExitButton, tellClientToSearchButton, messageField);
					ch.run();
				}
				catch(IOException ioe)
				{
					System.out.println("Had issue with Thread: ");
					System.out.println("");
					System.out.println(ioe);
				}
			}//End While
		}
		catch(IOException e)
		{
			System.out.println("We had an issue creating the Server Socket... Perhaps a server is already running?");
		}
		
			
			
		
			
    }//end Constructor

  
    
//##############################################################################

//========================================================================================================================================= 
    void setupMainFrame(int xScreenPercentage, int yScreenPercentage, String title)
    {
        Toolkit tk;
        Dimension d;
        
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        tk = Toolkit.getDefaultToolkit();
        d = tk.getScreenSize();
        setSize(d.width/2, d.height/2);
        
        setLocation((Toolkit.getDefaultToolkit().getScreenSize().width  - getSize().width) / 2, (Toolkit.getDefaultToolkit().getScreenSize().height - getSize().height) / 2);
        
        setTitle(title);// Sets the title bar
        
        setVisible(true);
        
    }//end of setupMainFrame()
    
//=========================================================================================================================================
    @Override
    public void actionPerformed(ActionEvent e)
    {
		String cmd = e.getActionCommand();
        if (cmd.equals("EXIT"))
		{
			System.exit(0);
		} 
		else if (cmd.equals("SENDMESSAGE"))
		{
			try
			{	
				System.out.println("Attempting to tell client to to expect a message.");
				talker.send("SENDMESSAGE");
			}
			catch(IOException ioe)
			{
				System.out.println("Something went wrong with sending a message to the client.");
				System.out.println(ioe);
			}
		}
		else if (cmd.equals("STARTSEARCH"))
		{
			try
			{	
				System.out.println("Attempting to tell client to to expect a message.");
				talker.send("STARTSEARCH");
			}
			catch(IOException ioe)
			{
				System.out.println("Something went wrong with telling the client to start searching.");
				System.out.println(ioe);
			}
		}
		else if (cmd.equals("SHUTDOWNCLIENT"))
		{
			try
			{	
				System.out.println("Attempting to tell client to shutdown.");
				talker.send("SHUTDOWNCLIENT");
			}
			catch(IOException ioe)
			{
				System.out.println("Something went wrong with sending a message to the client.");
				System.out.println(ioe);
			}
		}
    }//end of actionPerformed(...)
//=========================================================================================================================================
class CommandHandler implements Runnable
{
	String msg;
	CommandHandler(Talker t, JButton sendClientMessageButton, JButton tellClientToExitButton, JButton tellClientToSearchButton, JTextArea messageField)
	{
		System.out.println("Command Handler Started!");
	}
	public void run()
	{
		
		while(true)
		{
			try
			{
				msg = talker.received();
				//============== JOPTIONPANE SHOWING PROTOCOL ===============
				if(msg.equals("ACCEPTMESSAGE"))// If the client tells the server it's ready to accept the message from the messageField.
				{
					try
					{
						 talker.send(messageField.getText());
					}
					catch(IOException ioe)
					{
						System.out.println("Had Trouble Sending Message Popup to Client...");
					}
				}
				//============== FILE SENDING PROTOCOL (WIP) ================
				else if(msg.equals("STARTEDCRAWLING"))// If the client has started the crawler.
				{
					SwingUtilities.invokeLater(new Runnable()
					{
						public void run()
						{
							//Inform the user of the server that the crawler is running.
							tellClientToSearchButton.setEnabled(false);
						}
							
					});
				}
				else if(msg.equals("INCOMINGFILE"))// If the client is getting ready to send the name of a file.
				{
					System.out.println("Client is preparing to send a file name...");
					try
					{
						msg = talker.received();
						System.out.println("File: " + msg);
					}
					catch(IOException ioe)
					{
						System.out.println("We had an issue trying to show the file the client was sending.");
						System.out.println(ioe);
					}
				}
				//============= CLIENT KILLING PROTOCOL ======================
				else if(msg.equals("DISCONNECTED"))// If the client tells server it's disconnected.
				{
					serverSocket.close();
					normalSocket.close();
					SwingUtilities.invokeLater(new Runnable()
					{
						public void run()
						{
							sendClientMessageButton.setEnabled(false);
							tellClientToExitButton.setEnabled(false);
							tellClientToSearchButton.setEnabled(false);
						
						}
					});
					break;
					
					
				}
				//============= CLIENT JOINING PROTOCOL =====================
				else if(msg.equals("INCOMINGCLIENT"))
				{
					System.out.println("Incoming Client Connection!");
					try
					{
						talker.send("WELCOME");
						SwingUtilities.invokeLater(new Runnable()
						{
							public void run()
							{
								sendClientMessageButton.setEnabled(true);
								tellClientToExitButton.setEnabled(true);
								tellClientToSearchButton.setEnabled(true);
						
							}
						});
					}
					catch(IOException ioe)
					{
						System.out.println("We had an issue handshaking with new client...");
						System.out.println(ioe);
					}
				}
				else if(msg.equals("DONESEARCHING"))
				{
					System.out.println("Client finished with File Searching!");
					System.out.println("Re-Enabled search button!");
					SwingUtilities.invokeLater(new Runnable()
					{
						public void run()
						{
							//Inform the user of the server that the crawler is running.
							tellClientToSearchButton.setEnabled(true);
						}
							
					});
				}
				
				
				
			}
			catch(IOException ioe)
			{
				System.out.println("We had an issue with the CommandHandler Thread!");
				System.out.println(ioe);
			}
			
		}
	}
}	

}//End of class MyFrameClass
