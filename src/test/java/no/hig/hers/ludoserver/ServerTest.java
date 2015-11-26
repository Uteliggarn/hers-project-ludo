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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Test;

/**
 * Testing the communication between server and client
 * @author bne9988
 *
 */
public class ServerTest {
	private ExecutorService executorService;
	private final String text = "This is a test";
	private boolean noMatch;
	
	/**
	 * The server class listens to the socket for the message
	 * @author bne9988
	 *
	 */
	private class Server {
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
			
			executorService = Executors.newCachedThreadPool();
			startListener();
			executorService.shutdown();
		}
		
		/**
		 * Continues to loop until the message is noticed
		 */
		public void startListener() {
			noMatch = true;
			executorService.execute(() -> {
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
			});
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
			
			try {
				sendText(text);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		public void sendText(String text) throws IOException {
			output.write(text);
			output.newLine();
			output.flush();
		}
	}

	@Test
	public void test() {
		while (noMatch) {
			new Server();
			new Client();
		}
		
		assertFalse(noMatch);
	}
}