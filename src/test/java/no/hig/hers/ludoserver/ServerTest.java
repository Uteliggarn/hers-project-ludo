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

import no.hig.hers.ludoclient.Main;

public class ServerTest {
	private ExecutorService executorService;
	private final String text = "This is a test";
	private boolean noMatch;
	
	private class Server {
		private ServerSocket server;
		private BufferedReader input;
		private BufferedWriter output;
		
		public Server() {
			
			try {
				server = new ServerSocket();
				server.setReuseAddress(true);
				server.bind(new InetSocketAddress(12345));
				
				
			} catch (IOException io) {
				io.printStackTrace();
			}
			
			executorService = Executors.newCachedThreadPool();
			startListener();
			executorService.shutdown();
		}
		
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
	
	private class Client {
		private Socket connection;
		private BufferedReader input;
		private BufferedWriter output;
		
		public Client() {
			try {
				connection = new Socket("127.0.0.1", 12345);
				
				output = new BufferedWriter(new OutputStreamWriter(
	                    connection.getOutputStream()));
				input = new BufferedReader(new InputStreamReader(
	                    connection.getInputStream()));
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
		Server server = new Server();
		Client client = new Client();
		
		assertFalse(noMatch);
	}
}