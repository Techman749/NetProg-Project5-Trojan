import javax.mail.*;
import javax.mail.internet.*;
import java.util.*;
import java.io.*;
import javax.swing.*;
import java.io.FileOutputStream;
import java.lang.Object;
import java.net.*;

public class  Talker
{
	private DataOutputStream	dos;
	private BufferedReader      br;
	String						id;
	private Socket				socket;
	private ServerSocket		serverSocket;
	Talker(Socket normalSocket) throws IOException // For use with the Server
	{
		System.out.println("Talker Constructed for Server.");
		dos = new DataOutputStream(normalSocket.getOutputStream());
		br = new BufferedReader(new InputStreamReader(normalSocket.getInputStream()));
	}
	Talker(String domain, int port, String id) throws UnknownHostException, IOException //For use with the Client
	{
		System.out.println("Talker Constructed for Client.");
		Socket socket;
	 	socket = new Socket(domain, port);
	 	this.id = id;
	 	dos = new DataOutputStream(socket.getOutputStream());
     	br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	}
	void send(String message) throws IOException
	{
	 	 dos.writeBytes(message + '\n');
	 	 System.out.println ("Message Sent: " + message);
	}
	void expect(String expectedString) throws Exception
	{
		if (expectedString.equals(br.readLine()))
		{
			//System.out.println(received());
		}
		else
		{
			System.out.println("UNEXPECTED RESPONSE!");
			System.out.println("Expected: " + expectedString);
			System.out.println("Got " + br.readLine());
			throw new Exception("Didn't expect this to happen:");
			
		}
	}
	 
	String received() throws IOException
	{
		String message;
		message = br.readLine();
		System.out.println ("Message Received: " + message);
		return message;
	}
}