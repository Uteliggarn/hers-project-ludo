package no.hig.hers.ludoserver;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import org.junit.Test;

/**
 * Testing the communication between server and client
 * @author bne9988
 *
 */
public class ServerTest {
	private final String text = "This is a test";
	private boolean noMatch;
	
	/**
	 * The server class listens to the socket for the message. When the message is read
	 * it gets compared to see if it is the same.
	 * @author bne9988
	 *
	 */
	private class Server implements Runnable{
		private ServerSocket serverSocket;
		private Socket socket;
		private BufferedReader input;
		
		public Server() {
			
			try {
				serverSocket = new ServerSocket();
				serverSocket.setReuseAddress(true);
				serverSocket.bind(new InetSocketAddress(12345));
				
				socket = new Socket("127.0.0.1", 12345);
				
				input = new BufferedReader(new InputStreamReader(
	                    socket.getInputStream()));
				
			} catch (IOException io) {
				io.printStackTrace();
			}
		}
		
		/**
		 * Server continues to loop until the message is noticed.
		 */
		public void run() {
			noMatch = true;
			while (noMatch) {
				
				try {
					String str = read();
					if (str != null && str.equals(text)) {
						noMatch = false;
					}
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		public String read() throws IOException {
			if (input.ready())
				return input.readLine();
			return null;
		}
	}
	
	/**
	 * The client sends a message to the server
	 * @author bne9988
	 *
	 */
	private class Client {
		private Socket connection;
		private BufferedWriter output;
		
		public Client() {
			try {
				connection = new Socket("127.0.0.1", 12345);
				
				output = new BufferedWriter(new OutputStreamWriter(
	                    connection.getOutputStream()));
			} catch (IOException io) {
				io.printStackTrace();
			}
		}
		
		public void sendText(String text) throws IOException {
			output.write(text);
			output.newLine();
			output.flush();
		}
	}

	/**
	 * The server gets added to a new thread. The client tries to send a message to the server.
	 * If the server gets the message the test is ok.
	 */
	@Test
	public void test() {
		//Server server = new Server();
		(new Thread(new Server())).start();
		
		Client client = new Client();
		
		try {
			client.sendText(text);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		assertFalse(noMatch);
	}
}